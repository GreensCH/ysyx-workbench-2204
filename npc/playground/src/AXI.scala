import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

trait CoreParameter {
  protected val XLEN = 64
  protected val VAddrBits = 39 // VAddrBits is Virtual Memory addr bits
  protected val PAddrBits = 32 // PAddrBits is Phyical Memory addr bits
  protected val DataBits = XLEN
}

object AXI4Parameters extends CoreParameter {
  // These are all fixed by the AXI4 standard:
  val lenBits   = 8
  val sizeBits  = 3
  val burstBits = 2
  val respBits  = 2

  // These are not fixed:
  val idBits    = 4
  val addrBits  = PAddrBits
  val dataBits  = DataBits
  val strbBits = dataBits/8
  val userBits  = 1

  def BURST_FIXED = 0.U(burstBits.W)
  def BURST_INCR  = 1.U(burstBits.W)
  def BURST_WRAP  = 2.U(burstBits.W)

  def RESP_OKAY   = 0.U(respBits.W)
  def RESP_EXOKAY = 1.U(respBits.W)
  def RESP_SLVERR = 2.U(respBits.W)
  def RESP_DECERR = 3.U(respBits.W)
}

class AXI4BundleA extends MyDecoupledIO{
  override val bits = new Bundle {
    val addr  = Output(UInt(AXI4Parameters.addrBits.W))
    val burst = Output(UInt(AXI4Parameters.burstBits.W))
    val id    = Output(UInt(AXI4Parameters.idBits.W))
    val len   = Output(UInt(AXI4Parameters.lenBits.W))
    val size  = Output(UInt(AXI4Parameters.sizeBits.W))
  }
}
class AXI4BundleR extends MyDecoupledIO{
  override val bits = new Bundle {
    val data = Output(UInt(AXI4Parameters.dataBits.W))
    val id   = Output(UInt(AXI4Parameters.idBits.W))
    val last = Output(Bool())
    val resp = Output(UInt(AXI4Parameters.respBits.W))
  }
}
class AXI4BundleW extends MyDecoupledIO{
  override val bits = new Bundle {
    val data = Output(UInt(AXI4Parameters.dataBits.W))
    val strb = Output(UInt(8.W))
    val last = Output(Bool())
  }
}
class AXI4BundleB extends MyDecoupledIO{
  override val bits = new Bundle {
    val id  = Output(UInt(AXI4Parameters.idBits.W))
    val resp = Output(UInt(AXI4Parameters.respBits.W))
  }
}

class AXI4Master extends Bundle{
  val ar = new AXI4BundleA
  val r = Flipped(new AXI4BundleR)
  val aw = new AXI4BundleA
  val w = new AXI4BundleW
  val b = Flipped(new AXI4BundleB)
}


//val s_inst    = io.s00//AXI4Master.default()
//val s_memu    = io.s01
//val s_device  = io.s02
//val memory    = io.m00
//val perif     = io.m01//peripheral
//val clint     = io.m02

class Interconnect3x1 extends Module with ClintConfig {
  val io = IO(new Bundle{
    val maxi = new AXI4Master

    val ifu = Flipped(new AXI4Master)
    val memu = Flipped(new AXI4Master)
    val devu = Flipped(new AXI4Master)
  })
  dontTouch(io.maxi)
  dontTouch(io.ifu)
  dontTouch(io.memu)
  dontTouch(io.devu)
  /**** ID allocation ****/
  private val zero_id   =   0.U(AXI4Parameters.idBits.W)
  private val ifu_id    =   1.U(AXI4Parameters.idBits.W)
  private val memu_id   =   2.U(AXI4Parameters.idBits.W)
  private val devu_id   =   3.U(AXI4Parameters.idBits.W)

  val maxi = io.maxi
  val ifu  = io.ifu
  val memu = io.memu
  val devu = io.devu
  //default connection addition
  ifu  <> AXI4Master.default()
  memu <> AXI4Master.default()
  devu <> AXI4Master.default()
  maxi <> AXI4Slave.default()

  /**** Arbiter ****/
  private val dev_reading = RegInit(false.B)
  private val mem_reading = RegInit(false.B)
  private val ifu_reading = RegInit(false.B)
  when      (maxi.ar.bits.id === devu_id & maxi.ar.valid)                     { dev_reading := true.B }
    .elsewhen (maxi.r.bits.id  === devu_id & maxi.r.bits.last & maxi.r.ready)   { dev_reading := false.B }
  when      (maxi.ar.bits.id === memu_id & maxi.ar.valid)                     { mem_reading := true.B }
    .elsewhen (maxi.b.bits.id  === memu_id & maxi.r.bits.last & maxi.r.ready)   { mem_reading := false.B }
  when      (maxi.ar.bits.id === ifu_id  & maxi.ar.valid)                     { ifu_reading := true.B }
    .elsewhen (maxi.b.bits.id  === ifu_id  & maxi.r.bits.last & maxi.r.ready)   { ifu_reading := false.B }


  devu.ar.ready := maxi.ar.ready
  memu.ar.ready := maxi.ar.ready & (!devu.ar.valid)
  ifu.ar.ready  := maxi.ar.ready & (!memu.ar.valid) & (!devu.ar.valid)
  when(devu.ar.valid){
    maxi.ar.bits <> devu.ar.bits
    maxi.ar.valid := true.B
    maxi.ar.bits.id := devu_id
  }.elsewhen(memu.ar.valid){
    maxi.ar.bits <> memu.ar.bits
    maxi.ar.valid := true.B
    maxi.ar.bits.id := memu_id
  }.elsewhen(ifu.ar.valid){
    maxi.ar.bits <> ifu.ar.bits
    maxi.ar.valid := true.B
    maxi.ar.bits.id := ifu_id
  }

  maxi.r.ready := devu.r.ready & memu.r.ready & ifu.r.ready
  when(maxi.r.bits.id === devu_id){
    devu.r.valid  := maxi.r.valid
    devu.r.bits   <> maxi.r.bits
  }.elsewhen(maxi.r.bits.id === memu_id){
    memu.r.valid  := maxi.r.valid
    memu.r.bits   <> maxi.r.bits
  }.elsewhen(maxi.r.bits.id === ifu_id){
    ifu.r.valid  := maxi.r.valid
    ifu.r.bits   <> maxi.r.bits
  }.otherwise{
    devu.r.bits   := maxi.r.bits
    memu.r.bits   := maxi.r.bits
    ifu.r.bits    := maxi.r.bits
  }

  private val dev_writing = RegInit(false.B)
  private val mem_writing = RegInit(false.B)
  when      (maxi.aw.valid & maxi.aw.bits.id === devu_id) { dev_writing := true.B   }
    .elsewhen (maxi.b.valid  & maxi.b.bits.id  === devu_id) { dev_writing := false.B  }
  when      (maxi.aw.valid & maxi.aw.bits.id === memu_id) { mem_writing := true.B   }
    .elsewhen (maxi.b.valid  & maxi.b.bits.id  === memu_id) { mem_writing := false.B  }

  devu.aw.ready := maxi.aw.ready & (!mem_writing)
  memu.aw.ready := maxi.aw.ready & (!devu.aw.valid) & (!dev_writing)
  when(devu.aw.valid){
    maxi.aw.bits <> devu.aw.bits
    maxi.aw.valid := true.B
    maxi.aw.bits.id := devu_id
  }.elsewhen(memu.aw.valid){
    maxi.aw.bits <> memu.aw.bits
    maxi.aw.valid := true.B
    maxi.aw.bits.id := memu_id
  }
  // write channel
  devu.w.ready := maxi.w.ready & (!mem_writing)
  memu.w.ready := maxi.w.ready & (!dev_writing)

  when(dev_writing){
    maxi.w.bits <> devu.w.bits
    maxi.w.valid := true.B
  }.elsewhen(mem_writing){
    maxi.w.bits <> memu.w.bits
    maxi.w.valid := true.B
  }

  /* response channel */
  maxi.b.ready := devu.b.ready & memu.b.ready
  when(maxi.b.bits.id === devu_id){
    devu.b.valid  := maxi.b.valid
    devu.b.bits   <> maxi.b.bits
  }.elsewhen(maxi.b.valid){
    memu.b.valid  := maxi.b.valid
    memu.b.bits   <> maxi.b.bits
  }.otherwise{
    devu.b.bits   <> maxi.b.bits
    memu.b.bits   <> maxi.b.bits
  }

}


object Interconnect{

  def apply(maxi: AXI4Master, ifu: AXI4Master, memu: AXI4Master, devu: AXI4Master):  Interconnect3x1 = {
    val interconnect = Module(new Interconnect3x1)
    interconnect.io.maxi  <> maxi
    interconnect.io.ifu   <> ifu
    interconnect.io.memu  <> memu
    interconnect.io.devu  <> devu

    interconnect
  }
}
/*
 Default used by consumer
 Clear used by producer
 */
object AXI4BundleA{
  def apply(): AXI4BundleA = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleA))
    wire
  }
  def default(inf: AXI4BundleA): Unit = {
    inf.ready := true.B
  }
  def clear(inf: AXI4BundleA): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.addr := 0.U
    inf.bits.size := 0.U
    inf.bits.len  := 0.U
    inf.bits.burst := AXI4Parameters.BURST_INCR
  }
  def set(inf: AXI4BundleA, valid: Bool, id: UInt, addr: UInt, burst_size: UInt, burst_len: UInt): Unit ={
    inf.valid := valid
    inf.bits.id := id
    inf.bits.addr := addr
    inf.bits.size := burst_size
    inf.bits.len  := burst_len
    inf.bits.burst := AXI4Parameters.BURST_INCR
  }
}

object AXI4BundleR{
  def apply(): AXI4BundleR = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleR))
    wire
  }
  def default(inf: AXI4BundleR): Unit = {
    inf.ready := true.B
  }
  def clear(inf: AXI4BundleR): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.resp := 0.U
    inf.bits.data := 0.U
    inf.bits.last := false.B
  }
  def set(inf: AXI4BundleR,valid: Bool, id: UInt, data: UInt, last: UInt, resp: UInt): Unit = {
    inf.valid := valid
    inf.bits.id := id
    inf.bits.data := data
    inf.bits.last := last
    inf.bits.resp := AXI4Parameters.RESP_OKAY
  }
}

object AXI4BundleW{
  def apply(): AXI4BundleW = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleW))
    wire
  }
  def default(inf: AXI4BundleW): Unit ={
    inf.ready := true.B
  }
  def set(inf: AXI4BundleW, valid: Bool, data: UInt, strb: UInt, last: Bool): Unit ={
    inf.valid := valid
    inf.bits.data := data
    inf.bits.strb := strb
    inf.bits.last  := last
  }
  def clear(inf: AXI4BundleW): Unit = {
    inf.valid := false.B
    inf.bits.data := 0.U
    inf.bits.strb := 0.U
    inf.bits.last := false.B
  }
}

object AXI4BundleB{
  def apply(): AXI4BundleB = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleB))
    wire
  }
  def default(inf: AXI4BundleB): Unit = {
    inf.ready := true.B
  }
  def clear(inf: AXI4BundleB): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.resp := 0.U
  }
  def set(inf: AXI4BundleB, valid: Bool, id: UInt, resp: UInt): Unit = {
    inf.valid := valid
    inf.bits.id := id
    inf.bits.resp := resp
  }
}

object AXI4Master{
  def default(): AXI4Master = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4Master))
    AXI4BundleA.clear  (wire.ar)
    AXI4BundleA.clear  (wire.aw)
    AXI4BundleR.default(wire.r)
    AXI4BundleW.clear  (wire.w)
    AXI4BundleB.default(wire.b)
    wire
  }
  def default(maxi: AXI4Master): Unit = {
    AXI4BundleA.clear  (maxi.ar)
    AXI4BundleA.clear  (maxi.aw)
    AXI4BundleR.default(maxi.r)
    AXI4BundleW.clear  (maxi.w)
    AXI4BundleB.default(maxi.b)
  }
}

object AXI4Slave{
  def default(): AXI4Master = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4Master))
    AXI4BundleA.default(wire.ar)
    AXI4BundleA.default(wire.aw)
    AXI4BundleR.clear  (wire.r)
    AXI4BundleW.default(wire.w)
    AXI4BundleB.clear  (wire.b)
    wire
  }
  def default(maxi: AXI4Master): Unit = {
    AXI4BundleA.default(maxi.ar)
    AXI4BundleA.default(maxi.aw)
    AXI4BundleR.clear(maxi.r)
    AXI4BundleW.default(maxi.w)
    AXI4BundleB.clear(maxi.b)
  }
}

class AXI4ManagerIn extends Bundle{
  val rd_en  = Input(Bool())
  val we_en  = Input(Bool())
  val size   = Flipped(new SrcSize{ val qword  = Output(Bool()) })
  val addr   = Input(UInt(32.W))
  val data   = Input(UInt(128.W))
  val wmask  = Input(UInt(16.W))
}
class AXI4ManagerOut extends Bundle{
  val finish = Output(Bool())
  val ready  = Output(Bool())
  val data   = Output(UInt(128.W))
}

class AXI4Manager extends Module  {
  val io = IO(new Bundle {
    val in   = new AXI4ManagerIn
    val maxi = new AXI4Master
    val out  = new AXI4ManagerOut
  })
  /*
   Reference
   */
  private val in = io.in
  private val maxi = io.maxi
  private val out = io.out
  private val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: sIREAD :: sIWRITE :: Nil = Enum(9)
  private val next_state = Wire(UInt(sADDR.getWidth.W))
  private val curr_state = RegNext(init = sADDR, next = next_state)
  /* Lookup Stage */
  private val stage_en = Wire(Bool())
  private val stage_in = Wire(Output(chiselTypeOf(in)))
  stage_in := in
  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
  private val _in = Wire(Output(chiselTypeOf(in)))
  _in := in
  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
  dontTouch(in2)
  /* AXI Read Channel Stage */
  private val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
  private val r_stage_out = RegNext(init = 0.U(AXI4Parameters.dataBits.W), next = r_stage_in)
  /* AXI Interface Default Connection(Read-Write) */
  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleR.default (maxi.r)
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)
  /*
  Internal Control Signal
  */
  /* axi */
  private val r_last = maxi.r.bits.last  & maxi.r.valid
  /* common */
  private val is_clint = (in2.addr(31, 16) === "h0200".U) & (in2.addr(16, 15) =/= "b11".U)
  private val is_load = in2.rd_en
  private val is_save = in2.we_en
  private val size = in2.size
  private val overborder = MuxCase(false.B, Array(
    size.byte  -> false.B,
    size.hword -> (in2.addr(0)    =/= 0.U),
    size.word  -> (in2.addr(1, 0) =/= 0.U),
    size.dword -> (in2.addr(2, 0) =/= 0.U),
    size.qword -> true.B
  ))
  /* stage */
  stage_en := curr_state === sADDR
  /*
   Internal Data Signal
  */
  /* reference */
  private val a_addr = Mux(overborder, Cat(in2.addr(31, 4), 0.U(4.W)), Cat(in2.addr(31, 3), 0.U(3.W)))
  private val start_byte = Mux(overborder, in2.addr(3, 0), in2.addr(2, 0))
  private val start_bit =  Mux(overborder, in2.addr(3, 0) << 3, in2.addr(2, 0) << 3).asUInt()
  /* read transaction */
  r_stage_in := MuxCase(0.U, Array(
    (curr_state === sREAD1 & !r_last) -> maxi.r.bits.data,
    (curr_state === sREAD2) -> r_stage_out
  )) //    r_stage_in := Mux(curr_state === sREAD_1 & !r_last, maxi.r.bits.data, r_stage_out)
  private val rdata_out_128 = Cat(maxi.r.bits.data, r_stage_out)
  private val rdata_out_1 = maxi.r.bits.data >> start_bit
  private val rdata_out_2 =  rdata_out_128 >> start_bit
  private val clint_rdata = WireDefault(0.U(64.W))
  private val rdata_out = MuxCase(0.U, Array(
    (curr_state === sIREAD) -> clint_rdata,
    (curr_state === sREAD1) -> rdata_out_1,
    (curr_state === sREAD2) -> rdata_out_2
  ))
  private val memory_data = MuxCase(0.U,
    Array(
      size.byte   -> rdata_out(7,  0),
      size.hword  -> rdata_out(15, 0),
      size.word   -> rdata_out(31, 0),
      size.dword  -> rdata_out,
      size.qword  -> rdata_out_128,
    )
  )
  private val memory_data_buffer = RegInit(0.U(128.W))
  /* write transaction */
  private val wdata = (in2.data << start_bit).asTypeOf(0.U(128.W))
  private val wmask = MuxCase(0.U(8.W), Array(
    size.byte  -> (in2.wmask  << start_byte),
    size.hword -> (in2.wmask  << start_byte),
    size.word  -> (in2.wmask  << start_byte),
    size.dword -> (in2.wmask  << start_byte),
    size.qword -> ("hffff".U),
  )).asUInt()
  /*
   States Change Rule
   val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: Nil = Enum(7)
   */
  next_state := sADDR
  switch(curr_state){
    is(sADDR){
      when(is_load){
        when(is_clint)            { next_state := sIREAD }
          .elsewhen(maxi.ar.ready)  { next_state := sREAD1 }
          .otherwise                { next_state := sARWAIT }
      }.elsewhen(is_save){
        when(is_clint)            { next_state := sIWRITE }
          .elsewhen(maxi.aw.ready)  { next_state := sWRITE1 }
          .otherwise                { next_state := sAWWAIT }
      }.otherwise                 { next_state := sADDR }
    }
    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
    is(sAWWAIT){ when(maxi.aw.ready){ next_state := sWRITE1 }.otherwise{ next_state := sAWWAIT } }
    is(sREAD1){
      when(r_last)                          { next_state := sADDR }
        .elsewhen(overborder & maxi.r.ready)  { next_state := sREAD2 }
        .otherwise                            { next_state := sREAD1 }
    }
    is(sWRITE1){
      when(overborder & maxi.w.ready )        { next_state := sWRITE2  }
        .elsewhen(maxi.b.valid)               { next_state := sADDR    }
        .otherwise                            { next_state := sWRITE1 }
    }
    is(sREAD2){
      when(r_last)                            { next_state := sADDR }
        .otherwise                            { next_state := sREAD2 }
    }
    is(sWRITE2){
      when(maxi.b.valid)                      { next_state := sADDR }
        .otherwise                            { next_state := sWRITE2 }
    }
    is(sIREAD)  { next_state := sADDR }
    is(sIWRITE) { next_state := sADDR }
  }
  /*
    AXI
   */
  private val burst_len = Mux(overborder, 1.U, 0.U)
  //    private val w_stay = RegInit(0.U.asTypeOf((new AXI4BundleW).bits))
  AXI4BundleA.clear(maxi.ar)
  when(next_state === sARWAIT | (curr_state === sADDR &next_state === sREAD1) | curr_state === sARWAIT){
    AXI4BundleA.set(inf = maxi.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
  }
  AXI4BundleA.clear(maxi.aw)
  when(next_state === sAWWAIT | (curr_state === sADDR & next_state === sWRITE1) | curr_state === sAWWAIT){
    AXI4BundleA.set(inf = maxi.aw, valid = true.B, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
  }
  AXI4BundleW.clear(maxi.w)
  when(curr_state === sWRITE1){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = wdata(63, 0), strb = wmask, last = !overborder)
  }
  when(curr_state === sWRITE2){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = wdata(127, 64), strb = wmask, last = true.B)
  }
  AXI4BundleB.default(maxi.b)
  dontTouch(maxi.b)
  /*
    Core Internal Bus
  */
  val clint_addr  = WireDefault(0.U(64.W))
  val clint_wdata = WireDefault(0.U(64.W))
  val clint_wmask = WireDefault("hFFFF_FFFF_FFFF_FFFF".U)
  val clint_we    = WireDefault(false.B)

  clint_we    := next_state === sIWRITE
  clint_addr  := a_addr
  clint_wdata := wdata(63, 0)

  BoringUtils.addSource(clint_addr  , "clint_addr")
  BoringUtils.addSource(clint_wdata , "clint_wdata")
  BoringUtils.addSource(clint_wmask , "clint_wmask")
  BoringUtils.addSink(clint_rdata   , "clint_rdata")
  BoringUtils.addSource(clint_we    , "clint_we")
  /*
  Output
 */
  out.ready  := next_state === sADDR | curr_state === sADDR
  dontTouch(out.ready)
  dontTouch(out.data)
  out.finish := maxi.r.bits.last | maxi.b.valid | curr_state === sIWRITE | curr_state === sIREAD//(next_state === sADDR & curr_state =/= sADDR)
  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
  out.data := Mux(curr_state === sREAD1 | curr_state === sREAD2 | curr_state === sIREAD, memory_data, memory_data_buffer)

}


class AXI4ManagerLite extends Module {
  val io = IO(new Bundle {
    val in   = new AXI4ManagerIn
    val maxi = new AXI4Master
    val out  = new AXI4ManagerOut
  })
  /*
   Reference
   */
  private val in = io.in
  in.wmask := DontCare
  private val maxi = io.maxi
  private val out = io.out
  private val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: Nil = Enum(7)
  private val next_state = Wire(UInt(sADDR.getWidth.W))
  private val curr_state = RegNext(init = sADDR, next = next_state)
  /* Lookup Stage */
  private val stage_en = Wire(Bool())
  private val stage_in = Wire(Output(chiselTypeOf(in)))
  stage_in := in
  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
  private val _in = Wire(Output(chiselTypeOf(in)))
  _in := in
  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
  /* AXI Read Channel Stage */
  private val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
  private val r_stage_out = RegNext(init = 0.U(AXI4Parameters.dataBits.W), next = r_stage_in)
  /* AXI Interface Default Connection(Read-Write) */
  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleR.default (maxi.r)
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)
  /*
  Internal Control Signal
  */
  /* axi */
  private val r_last = maxi.r.bits.last  & maxi.r.valid
  /* common */
  private val is_load = in2.rd_en
  private val is_save = in2.we_en
  private val size = in2.size
  /* stage */
  stage_en := curr_state === sADDR
  /*
   Internal Data Signal
  */
  /* reference */
  private val a_addr = Cat(in2.addr(31, 4), 0.U(4.W))
  private val start_byte = in2.addr(3, 0)
  private val start_bit =  (in2.addr(3, 0) << 3).asUInt()
  /* read transaction */
  r_stage_in := MuxCase(0.U, Array(
    (curr_state === sREAD1 & !r_last) -> maxi.r.bits.data,
    (curr_state === sREAD2) -> r_stage_out
  )) //    r_stage_in := Mux(curr_state === sREAD_1 & !r_last, maxi.r.bits.data, r_stage_out)
  private val rdata_out_128 = Cat(maxi.r.bits.data, r_stage_out)
  private val rdata_out_1 = maxi.r.bits.data >> start_bit
  private val rdata_out_2 =  rdata_out_128 >> start_bit
  private val rdata_out = MuxCase(0.U, Array(
    (curr_state === sREAD1) -> rdata_out_1,
    (curr_state === sREAD2) -> rdata_out_2
  ))
  private val memory_data = MuxCase(0.U,
    Array(
      size.byte   -> rdata_out(7,  0),
      size.hword  -> rdata_out(15, 0),
      size.word   -> rdata_out(31, 0),
      size.dword  -> rdata_out,
      size.qword  -> rdata_out_128,
    )
  )
  /* write transaction */
  private val wdata = (in2.data << start_bit).asTypeOf(0.U(128.W))
  private val memory_data_buffer = RegInit(0.U(128.W))
  private val wmask = "hffff".U
  /*
   States Change Rule
   */
  next_state := sADDR
  switch(curr_state){
    is(sADDR){
      when(is_load){
        when(maxi.ar.ready) { next_state := sREAD1 } .otherwise  { next_state := sARWAIT }
      }.elsewhen(is_save){
        when(maxi.aw.ready) { next_state := sWRITE1 } .otherwise { next_state := sAWWAIT }
      }.otherwise           { next_state := sADDR }
    }
    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
    is(sAWWAIT){ when(maxi.aw.ready){ next_state := sWRITE1 }.otherwise{ next_state := sAWWAIT } }
    is(sREAD1){
      when(maxi.r.ready)                    { next_state := sREAD2 }
        .otherwise                            { next_state := sREAD1 }
    }
    is(sWRITE1){
      when(maxi.w.ready )                   { next_state := sWRITE2  }
        .otherwise                            { next_state := sWRITE1 }
    }
    is(sREAD2){
      when(r_last)                            { next_state := sADDR }
        .otherwise                            { next_state := sREAD2 }
    }
    is(sWRITE2){
      when(maxi.b.valid)                      { next_state := sADDR }
        .otherwise                            { next_state := sWRITE2 }
    }
  }
  /*
    AXI
   */
  private val burst_len = 1.U
  AXI4BundleA.clear(maxi.ar)
  when(next_state === sARWAIT | (curr_state === sADDR & next_state === sREAD1) | curr_state === sARWAIT){
    AXI4BundleA.set(inf = maxi.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
  }
  AXI4BundleA.clear(maxi.aw)
  when(next_state === sAWWAIT | (curr_state === sADDR & next_state === sWRITE1) | curr_state === sAWWAIT){
    AXI4BundleA.set(inf = maxi.aw, valid = true.B, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
  }
  AXI4BundleW.clear(maxi.w)
  when(curr_state === sWRITE1){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = wdata(63, 0), strb = wmask, last = false.B)
  }
  when(curr_state === sWRITE2){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = wdata(127, 64), strb = wmask, last = true.B)
  }
  AXI4BundleB.default(maxi.b)
  /*
  Output
 */
  out.ready  := next_state === sADDR | curr_state === sADDR
  out.finish := maxi.r.bits.last | maxi.b.valid
  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
  out.data := Mux(curr_state === sREAD1 | curr_state === sREAD2, memory_data, memory_data_buffer)

}