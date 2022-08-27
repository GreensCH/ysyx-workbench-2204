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

class Interconnect3x3 extends Module with ClintConfig {
  val io = IO(new Bundle{
    val s00 = Flipped(new AXI4Master)
    val s01 = Flipped(new AXI4Master)
    val s02 = Flipped(new AXI4Master)
    val m00 = new AXI4Master
    val m01 = new AXI4Master
    val m02 = new AXI4Master
  })
  /*
   IO Interface
   */
  //  io.s00 <> DontCare
  val s_inst    = io.s00//AXI4Master.default()
  val s_memu    = io.s01
  val s_device  = io.s02
  val memory    = io.m00
  val perif     = io.m01//peripheral
  val clint     = io.m02
  dontTouch(io.s00)
  dontTouch(io.s01)
  dontTouch(io.s02)
  dontTouch(io.m00)
  dontTouch(io.m01)
  //  io.m02 <> DontCare
  /**** ID allocation ****/
  val zero_id   = 0.U(AXI4Parameters.idBits.W)
  val inst_id =   1.U(AXI4Parameters.idBits.W)
  val memu_id =   2.U(AXI4Parameters.idBits.W)
  val perif_id =  3.U(AXI4Parameters.idBits.W)
  val clint_id =  4.U(AXI4Parameters.idBits.W)
  /**** Default Connection ****/
  s_memu <> memory
  s_inst <> AXI4Master.default()
  /**** Arbiter ****/
  /*  addr read channel */
  s_memu.ar.ready := memory.ar.ready
  s_inst.ar.ready := memory.ar.ready & (!s_memu.ar.valid)
  when(s_memu.ar.valid){
    memory.ar.bits <> s_memu.ar.bits
    memory.ar.valid := true.B
    memory.ar.bits.id := memu_id
  }.elsewhen(s_inst.ar.valid){
    memory.ar.bits <> s_inst.ar.bits
    memory.ar.valid := true.B
    memory.ar.bits.id := inst_id
  }
  /*  read channel */
  memory.r.ready := (s_memu.r.ready & s_inst.r.ready)
  when(memory.r.bits.id === memu_id){
    s_memu.r.valid := memory.r.valid
    s_memu.r.bits <> memory.r.bits
  }.otherwise{
    AXI4BundleR.clear(s_memu.r)
  }
  when(memory.r.bits.id === inst_id){
    s_inst.r.valid := memory.r.valid
    s_inst.r.bits <> memory.r.bits
  }.otherwise{
    AXI4BundleR.clear(s_inst.r)
  }
  /*  write channel */
  s_memu.b <> memory.b
  s_memu.w <> memory.w
  s_memu.aw <> memory.aw

  //
  //  s_device <> perif
  //  clint <> DontCare

  /**** Other connection(Route) ****/
  // 0200_0000 ~ 0200_C000
  private val s_device_addr = MuxCase(0.U(PAddrBits.W), Array(
    s_device.ar.valid -> s_device.ar.bits.addr,
    s_device.aw.valid -> s_device.aw.bits.addr
  ))
  private val is_clint = (s_device_addr(PAddrBits-1, 16) === "h0200".U) & (s_device_addr(16, 15) =/= "b11".U)
  /* read addr channel */
  s_device.ar.ready := perif.ar.ready & clint.ar.ready // Attention! use "or" is because of both clint and perif is to mmio
  AXI4BundleA.set(inf = perif.ar,valid = (!is_clint) & s_device.ar.valid,id = perif_id,
    addr = s_device.ar.bits.addr,burst_size = s_device.ar.bits.size,burst_len = s_device.ar.bits.len)
  AXI4BundleA.set(inf = clint.ar,valid =  is_clint & s_device.ar.valid,id = clint_id,
    addr = s_device.ar.bits.addr,burst_size = s_device.ar.bits.size,burst_len = s_device.ar.bits.len)
  /* read channel */
  perif.r.ready := s_device.r.ready
  clint.r.ready := s_device.r.ready
  AXI4BundleR.set(inf = s_device.r,valid = perif.r.valid,id = zero_id, data = perif.r.bits.data, last = perif.r.bits.last, resp = perif.r.bits.resp)
  when(clint.r.valid){
    AXI4BundleR.set(inf = s_device.r, valid = true.B,id = zero_id, data = clint.r.bits.data, last = clint.r.bits.last, resp = clint.r.bits.resp)
  }
  /* write addr channel */
  s_device.aw.ready := perif.aw.ready & clint.aw.ready
  AXI4BundleA.set(inf = perif.aw,valid = (!is_clint) & s_device.aw.valid,id = perif_id,
    addr = s_device.aw.bits.addr,burst_size = s_device.aw.bits.size,burst_len = s_device.aw.bits.len)
  AXI4BundleA.set(inf = clint.aw,valid =  is_clint & s_device.aw.valid,id = clint_id,
    addr = s_device.aw.bits.addr,burst_size = s_device.aw.bits.size,burst_len = s_device.aw.bits.len)
  /* write data channel */
  private val is_clint_1 = RegInit(false.B)
  when(s_device.aw.valid){ is_clint_1 := is_clint }
    .elsewhen(s_device.b.valid){ is_clint_1 := false.B }
  s_device.w.ready := (perif.w.ready & (!is_clint_1)) | (clint.w.ready & is_clint_1)
  AXI4BundleW.set(inf = perif.w, valid = (!is_clint_1) & s_device.w.valid, data = s_device.w.bits.data, strb = s_device.w.bits.strb, last = s_device.w.bits.last)
  AXI4BundleW.set(inf = clint.w, valid =  is_clint_1 & s_device.w.valid, data = s_device.w.bits.data, strb = s_device.w.bits.strb, last = s_device.w.bits.last)
  /* response channel */
  perif.b.ready := s_device.b.ready
  clint.b.ready := s_device.b.ready
  AXI4BundleB.set(inf = s_device.b, valid = true.B, id = zero_id, resp = AXI4Parameters.RESP_OKAY)
  s_device.b.valid := clint.b.valid | perif.b.valid

}

//val s_inst    = io.s00//AXI4Master.default()
//val s_memu    = io.s01
//val s_device  = io.s02
//val memory    = io.m00
//val perif     = io.m01//peripheral
//val clint     = io.m02
class Interconnect extends Module with ClintConfig {
  val io = IO(new Bundle{
    val maxi = new AXI4Master
    val clint = new AXI4Master

    val ifu = Flipped(new AXI4Master)
    val memu = Flipped(new AXI4Master)
    val devu = Flipped(new AXI4Master)
  })
  dontTouch(io.maxi)
  dontTouch(io.clint)
  dontTouch(io.ifu)
  dontTouch(io.memu)
  dontTouch(io.devu)
  /**** ID allocation ****/
  private val zero_id   =   0.U(AXI4Parameters.idBits.W)
  private val inst_id   =   1.U(AXI4Parameters.idBits.W)
  private val memu_id   =   2.U(AXI4Parameters.idBits.W)
  private val devu_id   =   3.U(AXI4Parameters.idBits.W)
  private val clint_id  =   4.U(AXI4Parameters.idBits.W)

  val con3x3 = Module(new Interconnect3x3)
  con3x3.io.s00 <> io.ifu
  con3x3.io.s01 <> io.memu
  con3x3.io.s02 <> io.devu
  con3x3.io.m02 <> io.clint
  val meio = con3x3.io.m00
  val mmio = con3x3.io.m01
  val maxi = io.maxi
  //default connection addition
  mmio <> AXI4Master.default()
  meio <> AXI4Master.default()
  maxi <> AXI4Slave.default()

  private val dev_reading = RegInit(false.B)
  private val mem_reading = RegInit(false.B)
  when(maxi.ar.valid & maxi.ar.bits.id === devu_id){ dev_reading := true.B }
    .elsewhen(maxi.r.bits.id === devu_id & maxi.r.bits.last & maxi.r.ready){ dev_reading := false.B }

  when(maxi.ar.valid & maxi.ar.bits.id =/= devu_id ){ mem_reading := true.B }
    .elsewhen(maxi.b.bits.id =/= devu_id & maxi.r.bits.last & maxi.r.ready){ mem_reading := false.B }

  mmio.ar.ready := maxi.ar.ready & (!mem_reading)
  meio.ar.ready := maxi.ar.ready & (!mmio.ar.valid) & (!dev_reading)
  when(mmio.ar.valid){
    maxi.ar.bits <> mmio.ar.bits
    maxi.ar.valid := true.B
  }.elsewhen(meio.ar.valid){
    maxi.ar.bits <> meio.ar.bits
    maxi.ar.valid := true.B
  }

  maxi.r.ready := meio.r.ready & mmio.r.ready
  when(maxi.r.bits.id === devu_id){
    mmio.r.valid  := maxi.r.valid
    mmio.r.bits   <> maxi.r.bits
  }.elsewhen(maxi.r.valid){
    meio.r.valid  := maxi.r.valid
    meio.r.bits   <> maxi.r.bits
  }.otherwise{
    mmio.r.bits   := maxi.r.bits
    meio.r.bits   := maxi.r.bits
  }

  private val dev_writing = RegInit(false.B)
  private val mem_writing = RegInit(false.B)
  mmio.aw.ready := maxi.aw.ready & (!mem_writing)
  meio.aw.ready := maxi.aw.ready & (!mmio.aw.valid) & (!dev_writing)
  when(mmio.aw.valid){
    maxi.aw.bits <> mmio.aw.bits
    maxi.aw.valid := true.B
  }.elsewhen(meio.aw.valid){
    maxi.aw.bits <> meio.aw.bits
    maxi.aw.valid := true.B
  }

  // write channel
  when(maxi.aw.valid & maxi.aw.bits.id === devu_id){ dev_writing := true.B }
  .elsewhen(maxi.b.bits.id === devu_id & maxi.b.valid){ dev_writing := false.B }

  when(maxi.aw.valid & maxi.aw.bits.id =/= devu_id ){ mem_writing := true.B }
  .elsewhen(maxi.b.bits.id =/= devu_id & maxi.b.valid){ mem_writing := false.B }

  mmio.w.ready := maxi.w.ready & (!mem_writing)
  meio.w.ready := maxi.w.ready & (!dev_writing)// more!!!

  when(dev_writing){
    maxi.w.bits <> mmio.w.bits
    maxi.w.valid := true.B
  }.elsewhen(mem_writing){
    maxi.w.bits <> meio.w.bits
    maxi.w.valid := true.B
  }

  /* response channel */
  maxi.b.ready := meio.b.ready & mmio.b.ready
  when(maxi.b.bits.id === devu_id){
    mmio.b.valid  := maxi.b.valid
    mmio.b.bits   <> maxi.b.bits
  }.elsewhen(maxi.b.valid){
    meio.b.valid  := maxi.b.valid
    meio.b.bits   <> maxi.b.bits
  }.otherwise{
    mmio.b.bits   <> maxi.b.bits
    meio.b.bits   <> maxi.b.bits
  }
}


object Interconnect3x3{
  def apply(s00: AXI4Master, s01: AXI4Master, s02: AXI4Master, m00: AXI4Master, m01: AXI4Master, m02: AXI4Master):  Interconnect3x3 = {
    val interconnect = Module(new Interconnect3x3)
    interconnect.io.s00 <> s00
    interconnect.io.s01 <> s01
    interconnect.io.s02 <> s02
    interconnect.io.m00 <> m00
    interconnect.io.m01 <> m01
    interconnect.io.m02 <> m02
    interconnect
  }
}

object Interconnect{

  def apply(maxi: AXI4Master, clint: AXI4Master, ifu: AXI4Master, memu: AXI4Master, devu: AXI4Master):  Interconnect = {
    val interconnect = Module(new Interconnect)
    interconnect.io.maxi  <> maxi
    interconnect.io.clint <> clint
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
  val clint_we    = WireDefault(false.B)

  clint_we    := next_state === sIWRITE
  clint_addr  := a_addr
  clint_wdata := wdata(63, 0)

  BoringUtils.addSource(clint_addr  , "clint_addr")
  BoringUtils.addSource(clint_wdata , "clint_wdata")
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