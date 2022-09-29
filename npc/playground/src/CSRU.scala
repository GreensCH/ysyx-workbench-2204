import chisel3._
import chisel3.util._

trait CSRs {
  protected val time_addr     = "hC01".U(12.W)
  protected val mvendorid_addr= "hF11".U(12.W)
  protected val mtvec_addr    = "h305".U(12.W)
  protected val mepc_addr     = "h341".U(12.W)
  protected val mcause_addr   = "h342".U(12.W)
  protected val mie_addr      = "h304".U(12.W)
  protected val mip_addr      = "h344".U(12.W)
  protected val mtval_addr    = "h343".U(12.W)
  protected val mstatus_addr  = "h300".U(12.W)
  protected val mtime_addr    = "h305".U(12.W)
  protected val mcycle_addr   = "hB00".U(12.W)
}

class CSRCtrlInf extends Bundle with CoreParameter {
  // interrupt and exec control
  val out = new Bundle{
    val mepc        = Output(UInt(XLEN.W))
    val mtvec       = Output(UInt(XLEN.W))
    // mie
    val mie         = Output(Bool()) // machine-mode global interrupt enable bit
    val mtie        = Output(Bool()) // machine-mode time interrupt enable bit
    val msie        = Output(Bool()) // machine-mode software interrupt enable bit
    val meie        = Output(Bool()) // machine-mode external interrupt enable bit
    // mip
    val mtip        = Output(Bool()) // machine-mode time interrupt pending bit
    val msip        = Output(Bool()) // machine-mode software interrupt pending bit
    val meip        = Output(Bool()) // machine-mode external interrupt pending bit
  }
}

class CSRInf extends Bundle with CoreParameter{
  val csr_we      = Input(Bool())
  val csr_raddr   = Input(UInt(12.W))
  val rs1_data    = Input(UInt(XLEN.W))
  val zimm        = Input(UInt(XLEN.W))
  val operator    = Input(new CSRType)
  val result      = Output(UInt(XLEN.W))
  val rd_idx      = Input(UInt(5.W))
  val rs1_idx     = Input(UInt(5.W))

  val intr        = Input(Bool())
  val exec        = Input(Bool())
  val mret        = Input(Bool())
  val exce_code   = Input(UInt(4.W))
  val pc          = Input(UInt(64.W))
}

class SideBand extends Bundle{
  val clint   = Flipped(new ClintSB)// mtime //Holds the current value of the mtime register. Used as a shadow for TIME csr in a RISC-V core.
  val meip    = Input(Bool())// external interrupt
}

class CSRU extends Module with CoreParameter with CSRs{
  val io = IO(new Bundle{
    val exu         = new CSRInf        // csr inst
    val ctrl        = new CSRCtrlInf    // ctrl and exception
    val sb          = new SideBand})    // external and core interrupts
  private val exu = io.exu
  private val idb_in  = io.ctrl.out
  private val sb  = io.sb
  private val csr_addr = io.exu.csr_raddr
  private val operator = io.exu.operator
  private val rs1_data = io.exu.rs1_data
  private val zimm     = io.exu.zimm.asTypeOf(UInt(64.W))
  private val csrrw =  operator.csrrw
  private val csrrs =  operator.csrrs
  private val csrrc =  operator.csrrc
  private val csrrwi = operator.csrrwi
  private val csrrsi = operator.csrrsi
  private val csrrci = operator.csrrci

  // Control and Csr inst write read signals
  private val csr_we = exu.csr_we
  private val csr_rdata = WireDefault(0.U(64.W))
  private val csr_wdata = WireDefault(0.U(64.W))
  csr_wdata := MuxCase(0.U, Array(
    csrrw  -> rs1_data,
    csrrs  -> (csr_rdata | rs1_data),
    csrrc  -> (csr_rdata & (~rs1_data).asUInt()),//(csr_rdata & (~rs1_data).asUInt()),  正经应该是取反，不知道为什么rtos是不取反直接与
    csrrwi -> zimm,
    csrrsi -> Cat(csr_rdata(63, 5), csr_rdata(4,0) | zimm(4,0) ),// because zimm is 5-bits and you will never change csr's 5+ bits by this inst
    csrrci -> Cat(csr_rdata(63, 5), csr_rdata(4,0) & (~zimm(4,0)).asUInt()), //csr_rdata(4,0) & (~zimm(4,0)).asUInt()),
    // because zimm is 5-bits and you will never change csr's 5+ bits by this inst
  ))
  exu.result := csr_rdata
  /*
   mepc
   */
  private val mepc = RegInit(0.U(64.W))
  private val a_is_mepc = csr_addr === mepc_addr
  when(a_is_mepc)           { csr_rdata := mepc }//read

  when(a_is_mepc & csr_we )  { mepc := csr_wdata }//write
  .elsewhen(exu.intr | exu.exec) { mepc := exu.pc }
  idb_in.mepc := mepc
  /*
   mtvec
   */
  private val mtvec = RegInit(0.U(64.W))
  private val a_is_mtvec = csr_addr === mtvec_addr
  when(a_is_mtvec)          { csr_rdata := mtvec }//read
  when(a_is_mtvec & csr_we) { mtvec := Cat(csr_wdata(XLEN-1, 2), 0.U(2.W)) }//mode = direct
  idb_in.mtvec := mtvec
  /*
   mstatus
   */
  private val mstatus_in = Wire(UInt(64.W))
  private val mstatus_in_mie  = Wire(UInt(1.W))
  private val mstatus_in_mpie = Wire(UInt(1.W))
  private val mstatus_in_mpp  = Wire(UInt(2.W))
  private val mstatus = RegNext(init = "ha00000000".U(64.W), next = mstatus_in)//mie = 1 ha00000000
  mstatus_in_mie  := mstatus(3)
  mstatus_in_mpie := mstatus(7)
  mstatus_in_mpp  := mstatus(12, 11)//"b11".U //mstatus(12, 11)
  mstatus_in := Cat(mstatus(63, 13), mstatus_in_mpp, mstatus(10, 8), mstatus_in_mpie, mstatus(6, 4), mstatus_in_mie, mstatus(2, 0))
  chisel3.assert(mstatus_in.getWidth == 64, "mstatus_in should be 64")
  private val a_is_mstatus = csr_addr === mstatus_addr
  when(a_is_mstatus)          { csr_rdata := mstatus }
  when(a_is_mstatus & csr_we & !(exu.intr | exu.exec)) {
    mstatus_in := Cat(mstatus(63, 32), csr_wdata(31, 0))
  }.elsewhen(a_is_mstatus & csr_we & (exu.intr | exu.exec))  {
    mstatus_in := Cat(mstatus(63, 32), csr_wdata(31, 0))
    mstatus_in_mpie:= mstatus(3)
    mstatus_in_mie:=0.U(1.W)
  }.elsewhen(exu.intr | exu.exec ) { // mie -> mpie, mstatus(7):= mstatus(3)
    mstatus_in_mpie := mstatus(3);
    mstatus_in_mie := 0.U(1.W)
  }
  .elsewhen(exu.mret){ // mpie -> mie, mstatus(3) := mstatus(7)
    mstatus_in_mie := mstatus(7)
  }
  idb_in.mie := mstatus(3)
  /*
   mie(rw)
   */
  private val mie = RegInit("h00000000".U(64.W))
  private val a_is_mie = csr_addr === mie_addr
  when(a_is_mie)          { csr_rdata := mie }
  when(a_is_mie & csr_we) { mie := csr_wdata }
  idb_in.msie := mie(3)
  idb_in.mtie := mie(7)
  idb_in.meie := mie(11)
  /*
   mcause(read only)
   */
  private val mcause = RegInit(0.U(64.W))
  private val a_is_mcause = csr_addr === mcause_addr
  when(a_is_mcause)  { csr_rdata := mcause }
  when(exu.intr)     { mcause := Cat(1.U(1.W), 0.U(59.W) ,exu.exce_code) }
  .elsewhen(exu.exec){ mcause := Cat(0.U(1.W), 0.U(59.W) ,exu.exce_code) }
  /*
   mip(read only)
   */
  private val mip_in = Wire(UInt(64.W))
  private val mip_in_msip = Wire(UInt(1.W))
  private val mip_in_mtip = Wire(UInt(1.W))
  private val mip_in_meip = Wire(UInt(1.W))
  private val mip = RegNext(init = 0.U(64.W), next = mip_in)
  mip_in := 0.U//Cat(mip(63, 12), mip_in_meip, mip(10, 8), mip_in_mtip, mip(6, 4), mip_in_msip, mip(2, 0))
  private val a_is_mip = csr_addr === mip_addr
  when(a_is_mip) { csr_rdata := mip }
  mip_in_msip := sb.clint.msip
  mip_in_mtip := sb.clint.mtip  // accept external and core interrupt
  mip_in_meip := sb.meip
  idb_in.msip := sb.clint.msip//mip(7) // send to ctrl
  idb_in.mtip := sb.clint.mtip //mip(3)
  idb_in.meip := sb.meip//mip(11)
  /*
  time (mtime shadow, read only)
   */
  private val mtime = sb.clint.mtime
  when(csr_addr === time_addr){ csr_rdata := mtime }
  /*
  mcycle (read only)
   */
  private val mcycle = RegInit(0.U(64.W))
  mcycle := mcycle + 1.U
  when(csr_addr === mcycle_addr){ csr_rdata := mcycle }
  /*
   mhartid
   */
  when(csr_addr === "hF14".U(64.W)){ csr_rdata := 0.U }
  /*
   atom constraint
  */

}
