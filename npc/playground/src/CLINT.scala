import chisel3._
import chisel3.util._

trait ClintConfig extends CoreParameter {
  protected val ClintBaseAddr  = "h0200 0000".U(PAddrBits.W)
  protected val ClintBoundAddr = "h0200 BFFF".U(PAddrBits.W)
  protected val ClintMSIPAddr  = "h0200 0000".U(PAddrBits.W)
  protected val ClintMTIME     = "h0200 4000".U(PAddrBits.W)
  protected val ClintMTIMECMP  = "h0020 BFF8".U(PAddrBits.W)
}
// Clint  Sideband Signals
class ClintSB extends Bundle with CoreParameter {
  val msip  = Output(Bool())
  val mtip  = Output(Bool())
  val mtime = Output(UInt(DataBits.W))//Used as a shadow for TIME csr in a RISC-V core.
}


class CLINT extends  Module with CoreParameter {
  val io = IO(new Bundle{
    val mmio = Flipped(new AXI4Master)
    val sideband = Output(Bool())
  })
  val mmio = io.mmio
  val sb = io.sideband

}

object CLINT extends  ClintConfig {

  def isMtimecmp(addr: UInt): Bool = {
    addr === ClintBaseAddr
  }

  def isMtime(addr: UInt): Bool = {
    addr === ClintMTIME
  }

}