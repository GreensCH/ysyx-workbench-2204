import chisel3._
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
  private val msip      = RegInit(0.U(XLEN.W))
  mtime := mtime + 1.U
  // sideband output logic
  sb.msip   := msip(0)
  sb.mtime  := mtime
  sb.mtip   := (mtime >= mtimecmp)

  val clint_addr_0= WireDefault(0.U(32.W))
//  val clint_addr  = WireDefault(0.U(32.W))
  val clint_wdata_0 = WireDefault(0.U(64.W))
  val clint_wdata = WireDefault(0.U(64.W))
  val clint_size = WireDefault(0.U.asTypeOf(new SrcSize))
  clint_wdata := clint_wdata_0
  val clint_rdata = WireDefault(0.U(64.W))
  val clint_we    = WireDefault(false.B)
  val _is_time     = Wire(Bool())
  val _is_timecmp  = Wire(Bool())
  val _is_msip     = Wire(Bool())
  _is_time     := clint_addr_0(PAddrBits-1, 3) === ClintMTIMEAddr   (PAddrBits-1, 3)
  _is_timecmp  := clint_addr_0(PAddrBits-1, 3) === ClintMTIMECMPAddr(PAddrBits-1, 3)
  _is_msip     := clint_addr_0(PAddrBits-1, 3) === ClintMSIPAddr    (PAddrBits-1, 3)
  when(!clint_addr_0(2,0).orR()){
    _is_time     := clint_addr_0(PAddrBits-1, 3) === ClintMTIMEAddr   (PAddrBits-1, 3)
    _is_timecmp  := clint_addr_0(PAddrBits-1, 3) === ClintMTIMECMPAddr(PAddrBits-1, 3)
    _is_msip     := clint_addr_0(PAddrBits-1, 3) === ClintMSIPAddr    (PAddrBits-1, 3)
  }
  BoringUtils.addSink(clint_addr_0  , "clint_addr")
  BoringUtils.addSink(clint_wdata_0 , "clint_wdata")
  BoringUtils.addSink(clint_size    , "clint_size")
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
