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
//  dontTouch(io.maxi)
//  dontTouch(io.ifu)
//  dontTouch(io.memu)
//  dontTouch(io.devu)
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
  AXI4BundleA.default (ifu.ar)
  AXI4BundleA.default (ifu.aw)
  AXI4BundleR.clear   (ifu.r)
  AXI4BundleW.default (ifu.w)
  AXI4BundleB.clear   (ifu.b)

  AXI4BundleA.default (memu.ar)
  AXI4BundleA.default (memu.aw)
  AXI4BundleR.clear   (memu.r)
  AXI4BundleW.default (memu.w)
  AXI4BundleB.clear   (memu.b)

  AXI4BundleA.default (devu.ar)
  AXI4BundleA.default (devu.aw)
  AXI4BundleR.clear   (devu.r)
  AXI4BundleW.default (devu.w)
  AXI4BundleB.clear   (devu.b)

  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleR.default (maxi.r)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)

  /**** Arbiter ****/
  private val axi_reading = RegInit(zero_id)

  when(maxi.ar.valid & axi_reading===zero_id & maxi.ar.ready){//reading is according to maxi.ar.valid , which implict an order
    axi_reading := maxi.ar.bits.id
  }.elsewhen(maxi.r.valid & maxi.r.bits.last){
    axi_reading := zero_id
  }

  devu.ar.ready := maxi.ar.ready & (axi_reading === zero_id)
  memu.ar.ready := maxi.ar.ready & (!devu.ar.valid)  & (axi_reading === zero_id)
  ifu.ar.ready  := maxi.ar.ready & (!memu.ar.valid) & (!devu.ar.valid) & (axi_reading === zero_id)
  when(axi_reading =/= zero_id){
    AXI4BundleA.clear(maxi.ar)
  }.elsewhen(devu.ar.valid){
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
    // val _unused_ok_ar = Cat(false.B,
    //   inf.valid,
    //   inf.bits.addr ,
    //   inf.bits.id   ,
    //   inf.bits.len  ,
    //   inf.bits.size ,
    //   inf.bits.burst,
    //   false.B).andR()
    // dontTouch(_unused_ok_ar)
  }
  def clear(inf: AXI4BundleA): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.addr := 0.U
    inf.bits.size := 0.U
    inf.bits.len  := 0.U
    inf.bits.burst := AXI4Parameters.BURST_INCR
    // val _unused_ok_ar = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_ar)
  }
  def set(inf: AXI4BundleA, valid: Bool, id: UInt, addr: UInt, burst_size: UInt, burst_len: UInt): Unit ={
    inf.valid := valid
    inf.bits.id := id
    inf.bits.addr := addr
    inf.bits.size := burst_size
    inf.bits.len  := burst_len
    inf.bits.burst := AXI4Parameters.BURST_INCR
    // val _unused_ok_ar = Cat(false.B,
    //   inf.ready,
    // false.B).andR()
    // dontTouch(_unused_ok_ar)
  }
}

object AXI4BundleR{
  def apply(): AXI4BundleR = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleR))
    wire
  }
  def default(inf: AXI4BundleR): Unit = {
    inf.ready := true.B
    // val _unused_ok_r = Cat(false.B,
    //   inf.valid,
    //   inf.bits.data ,
    //   inf.bits.id   ,
    //   inf.bits.last ,
    //   inf.bits.resp ,
    //   false.B).andR()
    // dontTouch(_unused_ok_r)
  }
  def clear(inf: AXI4BundleR): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.resp := 0.U
    inf.bits.data := 0.U
    inf.bits.last := false.B
    // val _unused_ok_r = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_r)
  }
  def set(inf: AXI4BundleR,valid: Bool, id: UInt, data: UInt, last: UInt, resp: UInt): Unit = {
    inf.valid := valid
    inf.bits.id := id
    inf.bits.data := data
    inf.bits.last := last
    inf.bits.resp := AXI4Parameters.RESP_OKAY
    // val _unused_ok_r = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_r)
  }
}

object AXI4BundleW{
  def apply(): AXI4BundleW = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleW))
    wire
  }
  def default(inf: AXI4BundleW): Unit ={
    inf.ready := true.B
    // val _unused_ok_w = Cat(false.B,
    //   inf.valid,
    //   inf.bits.data ,
    //   inf.bits.strb ,
    //   inf.bits.last ,
    //   false.B).andR()
    // dontTouch(_unused_ok_w)
  }
  def clear(inf: AXI4BundleW): Unit = {
    inf.valid := false.B
    inf.bits.data := 0.U(AXI4Parameters.dataBits.W)
    inf.bits.strb := 0.U(AXI4Parameters.strbBits.W)
    inf.bits.last := false.B
    // val _unused_ok_w = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_w)
  }
  def set(inf: AXI4BundleW, valid: Bool, data: UInt, strb: UInt, last: Bool): Unit ={
    inf.valid := valid
    inf.bits.data := data
    inf.bits.strb := strb
    inf.bits.last  := last
    // val _unused_ok_w = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_w)
  }
}

object AXI4BundleB{
  def apply(): AXI4BundleB = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleB))
    wire
  }
  def default(inf: AXI4BundleB): Unit = {
    inf.ready := true.B
    // val _unused_ok_b = Cat(false.B,
    //   inf.valid,
    //   inf.bits.resp ,
    //   inf.bits.id   ,
    //   false.B).andR()
    // dontTouch(_unused_ok_b)
  }
  def clear(inf: AXI4BundleB): Unit = {
    inf.valid := false.B
    inf.bits.id := 0.U
    inf.bits.resp := 0.U
    // val _unused_ok_b = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_b)
  }
  def set(inf: AXI4BundleB, valid: Bool, id: UInt, resp: UInt): Unit = {
    inf.valid := valid
    inf.bits.id := id
    inf.bits.resp := resp
    // val _unused_ok_b = Cat(false.B,
    //   inf.ready,
    //   false.B).andR()
    // dontTouch(_unused_ok_b)
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



