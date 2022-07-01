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
  val cacheBits = 4
  val protBits  = 3
  val qosBits   = 4
  val respBits  = 2

  // These are not fixed:
  val idBits    = 1
  val addrBits  = PAddrBits
  val dataBits  = DataBits
  val userBits  = 1

  def CACHE_RALLOCATE  = 8.U(cacheBits.W)
  def CACHE_WALLOCATE  = 4.U(cacheBits.W)
  def CACHE_MODIFIABLE = 2.U(cacheBits.W)
  def CACHE_BUFFERABLE = 1.U(cacheBits.W)

  def PROT_PRIVILEDGED = 1.U(protBits.W)
  def PROT_INSECURE    = 2.U(protBits.W)
  def PROT_INSTRUCTION = 4.U(protBits.W)

  def BURST_FIXED = 0.U(burstBits.W)
  def BURST_INCR  = 1.U(burstBits.W)
  def BURST_WRAP  = 2.U(burstBits.W)

  def RESP_OKAY   = 0.U(respBits.W)
  def RESP_EXOKAY = 1.U(respBits.W)
  def RESP_SLVERR = 2.U(respBits.W)
  def RESP_DECERR = 3.U(respBits.W)
}

class AXI4BundleA extends MyDecoupledIO{
  val addr  = Output(UInt(AXI4Parameters.addrBits.W))
  val burst = Output(UInt(AXI4Parameters.burstBits.W))
  val cache = Output(UInt(AXI4Parameters.cacheBits.W))
  val id    = Output(UInt(AXI4Parameters.idBits.W))
  val len   = Output(UInt(AXI4Parameters.lenBits.W))
  val port  = Output(UInt(AXI4Parameters.protBits.W))
  val qos   = Output(UInt(AXI4Parameters.qosBits.W))// 0=no QoS, bigger = higher priority
  val lock  = Output(Bool())
  val size  = Output(UInt(AXI4Parameters.sizeBits.W))
}
class AXI4BundleR extends MyDecoupledIO{
  val data  = Output(UInt(AXI4Parameters.dataBits.W))
  val id    = Output(UInt(AXI4Parameters.idBits.W))
  val last  = Output(Bool())
  val resp  = Output(UInt(AXI4Parameters.respBits.W))
}
class AXI4BundleW extends MyDecoupledIO{
  val data  = Output(UInt(AXI4Parameters.dataBits.W))
  val strb  = Output(UInt((AXI4Parameters.dataBits/8).W))
  val last  = Output(Bool())
}
class AXI4BundleB extends MyDecoupledIO{
  val id    = Output(UInt(AXI4Parameters.idBits.W))
  val resp  = Output(UInt(AXI4Parameters.respBits.W))
}

class AXI4 extends Bundle{
  val ar = new AXI4BundleA
  val r = Flipped(new AXI4BundleR)
  val aw = new AXI4BundleA
  val w = new AXI4BundleR
  val b = Flipped(new AXI4BundleB)
}

object AXI4BundleA{
  def apply: AXI4BundleA = {
    val wire = Wire(new AXI4BundleA)
    wire.qos := 0.U
    wire.lock := 0.U
    wire
  }
}
object AXI4BundleR{
  def apply: AXI4BundleR = {
    val wire = Wire(new AXI4BundleR)
    wire
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