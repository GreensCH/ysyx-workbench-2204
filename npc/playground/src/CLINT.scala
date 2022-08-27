import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

trait ClintConfig extends CoreParameter {
  protected val ClintBaseAddr  = "h0200_0000".U(PAddrBits.W)
  protected val ClintBoundAddr = "h0200_BFFF".U(PAddrBits.W)

  protected val ClintMSIPAddr     = "h0200_0000".U(PAddrBits.W)
  protected val ClintMTIMECMPAddr = "h0200_4000".U(PAddrBits.W)
  protected val ClintMTIMEAddr    = "h0200_BFF8".U(PAddrBits.W)
}
// Clint  Sideband Signals
class ClintSB extends Bundle with CoreParameter {
  val msip  = Output(Bool())
  val mtip  = Output(Bool())
  val mtime = Output(UInt(DataBits.W))//Used as a shadow for TIME csr in a RISC-V core.
}

// axi4 lite only
class CLINT extends  Module with ClintConfig {
  val io = IO(new Bundle{
    val sideband = new ClintSB
  })

  val sb = io.sideband

  private val mtime     = RegInit(0.U(XLEN.W))
  private val mtimecmp  = RegInit(0.U(XLEN.W))
  private val msip      = RegInit(0.U(32.W))
  mtime := mtime + 1.U
  // sideband output logic
  sb.msip   := msip(0)
  sb.mtime  := mtime
  sb.mtip   := (mtime >= mtimecmp)
  // register access
  // slave write channel
//  val w_data = MuxCase(0.U(XLEN.W),Array(
//    (mmio.w.bits.strb === "b0000_0001".U) -> "h0000_000F".U,
//    (mmio.w.bits.strb === "b0000_0011".U) -> "h0000_00FF".U,
//    (mmio.w.bits.strb === "b0000_1111".U) -> "h0000_FFFF".U,
//    (mmio.w.bits.strb === "b1111_1111".U) -> "hFFFF_FFFF".U,
//  ))
//  val axi_w_mask_64 = (axi_w_mask << axi_offset_1).asUInt()
//  val reg_in = (axi_w_mask_64 & axi_w_data)

  val clint_addr  = WireDefault(0.U(64.W))
  val clint_wdata = WireDefault(0.U(64.W))
  val clint_wmask = WireDefault("hFFFF_FFFF_FFFF_FFFF".U)
  val clint_rdata = WireDefault(0.U(64.W))
  val clint_we    = WireDefault(false.B)
  val _is_time     = clint_addr(PAddrBits-1, 3) === ClintMTIMEAddr   (PAddrBits-1, 3)
  val _is_timecmp  = clint_addr(PAddrBits-1, 3) === ClintMTIMECMPAddr(PAddrBits-1, 3)
  val _is_msip     = clint_addr(PAddrBits-1, 3) === ClintMSIPAddr    (PAddrBits-1, 3)
  BoringUtils.addSink(clint_addr    , "clint_addr")
  BoringUtils.addSink(clint_wdata   , "clint_wdata")
  BoringUtils.addSink(clint_wmask   , "clint_wmask")
  BoringUtils.addSource(clint_rdata , "clint_rdata")
  BoringUtils.addSink(clint_we      , "clint_we")
  when(_is_time){
    when(clint_we){
      mtime := clint_wdata
    }.otherwise{
      clint_rdata := mtime
    }
  }
  when(_is_timecmp){
    when(clint_we){
      mtimecmp := clint_wdata
    }.otherwise{
      clint_rdata := mtimecmp
    }
  }
  when(_is_msip){
    when(clint_we){
      msip := clint_wdata
    }.otherwise{
      clint_rdata := msip
    }
  }

}
