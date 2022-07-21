import chisel3._
import chisel3.util._

trait CoreParameter {
  val XLEN = 64
  val HasMExtension = true
  val HasDiv = true
  val AddrBits = 64 // AddrBits is used in some cases
  val VAddrBits = 39 // VAddrBits is Virtual Memory addr bits
  val PAddrBits = 32 // PAddrBits is Phyical Memory addr bits
  val AddrBytes = AddrBits / 8 // unused
  val DataBits = XLEN
}

object AXI4Parameters extends CoreParameter {
  // These are all fixed by the AXI4 standard:
  val lenBits   = 8
  val sizeBits  = 3
  val burstBits = 2
  val respBits  = 2

  // These are not fixed:
  val idBits    = 1
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
    val addr = Output(UInt(AXI4Parameters.addrBits.W))
    val burst = Output(UInt(AXI4Parameters.burstBits.W))
    val id = Output(UInt(AXI4Parameters.idBits.W))
    val len = Output(UInt(AXI4Parameters.lenBits.W))
    val size = Output(UInt(AXI4Parameters.sizeBits.W))
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
    val id = Output(UInt(AXI4Parameters.idBits.W))
    val resp = Output(UInt(AXI4Parameters.respBits.W))
  }
}

class AXI4 extends Bundle{
  val ar = new AXI4BundleA
  val r = Flipped(new AXI4BundleR)
  val aw = new AXI4BundleA
  val w = new AXI4BundleW
  val b = Flipped(new AXI4BundleB)
}



class Interconnect extends Module{
  val io = IO(new Bundle{
    val s00 = Flipped(new AXI4)
    val s01 = Flipped(new AXI4)
    val s02 = Flipped(new AXI4)
    val m00 = new AXI4
    val m01 = new AXI4
  })
  /*
   IO Interface
   */
  val s_second   = io.s00
  val s_first    = io.s01
  val s_device   = io.s02
  val memory     = io.m00
  val device     = io.m01
  dontTouch(io.s00)
  dontTouch(io.s01)
  dontTouch(io.s02)
  dontTouch(io.m00)
  dontTouch(io.m01)
  /**** ID allocation ****/
  val zero_id   = 0.U(AXI4Parameters.idBits)
  val icache_id = 1.U(AXI4Parameters.idBits)
  val dcache_id = 2.U(AXI4Parameters.idBits)
  /**** Default Connection ****/
  AXI4Slave.default(s_first)
  AXI4Slave.default(s_second)
  AXI4Master.default(memory)
 /**** Lock ****/
  // AR
  when(s_first.ar.valid){
    memory.ar <> s_first.ar
    memory.ar.bits.id := 1.U
  }.elsewhen(s_second.ar.valid){
    memory.ar <> s_second.ar
    memory.ar.bits.id := 2.U
  }
  // R
  when(memory.r.bits.id === 1.U){
    memory.r <> s_first.r
    s_first.r.bits.id := 0.U
  }.elsewhen(memory.r.bits.id === 2.U){
    memory.r <> s_second.r
    s_second.r.bits.id := 0.U
  }
  // AW
  when(s_first.aw.valid){
    memory.aw <> s_first.aw
    memory.aw.bits.id := 1.U
  }.elsewhen(s_second.aw.valid){
    memory.aw <> s_second.aw
    memory.aw.bits.id := 2.U
  }
  // W
  when(s_first.w.valid){
    memory.w <> s_first.w
  }.elsewhen(s_second.w.valid){
    memory.w <> s_second.w
  }
  // B
  when(memory.b.bits.id === 1.U){
    memory.b <> s_first.b
    s_first.b.bits.id := 0.U
  }.elsewhen(memory.b.bits.id === 2.U){
    memory.b <> s_second.b
    s_second.b.bits.id := 0.U
  }
  /**** Other connection(Route) ****/
  s_device <> device
}

object Interconnect{
  def apply(s00: AXI4, s01: AXI4, s02: AXI4, m00: AXI4, m01: AXI4):  Interconnect = {
    val interconnect = Module(new Interconnect)
     interconnect.io.s00 <> s00
     interconnect.io.s01 <> s01
     interconnect.io.s02 <> s02
     interconnect.io.m00 <> m00
     interconnect.io.m01 <> m01
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
  def set(inf: AXI4BundleA, id: UInt, addr: UInt, burst_size: UInt, burst_len: UInt): Unit ={
    inf.valid := true.B
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
}

object AXI4BundleW{
  def apply(): AXI4BundleW = {
    val wire = WireDefault(0.U.asTypeOf(new AXI4BundleW))
    wire
  }
  def default(inf: AXI4BundleW): Unit ={
    inf.ready := true.B
  }
  def set(inf: AXI4BundleW, data: UInt, strb: UInt, last: Bool): Unit ={
    inf.valid := true.B
    inf.bits.data := data
    inf.bits.strb := strb
    inf.bits.last  := last
  }
  def set(inf: AXI4BundleW, valid: Bool, data: UInt, strb: UInt, last: Bool): Unit ={
    inf.valid := valid
    inf.bits.data := data
    inf.bits.strb := strb
    inf.bits.last  := last
  }
  def clear(inf: AXI4BundleW): Unit ={
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
}

object AXI4Master{
  def default(maxi: AXI4): Unit = {
    AXI4BundleA.clear(maxi.ar)
    AXI4BundleA.clear(maxi.aw)
    AXI4BundleR.default(maxi.r)
    AXI4BundleW.clear(maxi.w)
    AXI4BundleB.default(maxi.b)
  }
}

object AXI4Slave{
  def default(maxi: AXI4): Unit = {
    AXI4BundleA.default(maxi.ar)
    AXI4BundleA.default(maxi.aw)
    AXI4BundleR.clear(maxi.r)
    AXI4BundleW.default(maxi.w)
    AXI4BundleB.clear(maxi.b)
  }
}


//class AXIMaster extends Module{
//  val io = IO(new AXI4)
//}
//
//class AXISlave extends Module{
//  val io = IO(Flipped(new AXI4))
//  AXI4Parameters.CACHE_BUFFERABLE
//}