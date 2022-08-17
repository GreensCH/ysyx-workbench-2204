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
  val in = new Bundle{
    val intr        = Input(Bool())
    val exec        = Input(Bool())
    val mret        = Input(Bool())
    val exce_code   = Input(UInt(4.W))
    val pc          = Input(UInt(XLEN.W))
  }
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
  val csr_raddr   = Input(UInt(12.W))
  val rs1_data    = Input(UInt(XLEN.W))
  val zimm        = Input(UInt(XLEN.W))
  val operator    = Input(new CSRType)
  val result      = Output(UInt(XLEN.W))
  val rd_idx      = Input(UInt(5.W))
  val rs1_idx     = Input(UInt(5.W))
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
  private val idb_out = io.ctrl.in
  private val idb_in  = io.ctrl.out
  private val sb  = io.sb
  private val csr_addr = io.exu.csr_raddr
  private val operator = io.exu.operator
  private val rs1_data = io.exu.rs1_data
  private val rs1_idx  = io.exu.zimm(4, 0)
  private val rd_idx   = io.exu.rd_idx
  private val zimm     = io.exu.zimm.asTypeOf(UInt(64.W))
  private val csrrw =  operator.csrrw
  private val csrrs =  operator.csrrs
  private val csrrc =  operator.csrrc
  private val csrrwi = operator.csrrwi
  private val csrrsi = operator.csrrsi
  private val csrrci = operator.csrrci

  // Control and Csr inst write read signals
  private val is_csr = operator.is_csr
  private val csr_rdata = WireDefault(0.U(64.W))
  private val csr_wdata = WireDefault(0.U(64.W))
  csr_wdata := MuxCase(0.U, Array(
    csrrw  -> rs1_data,
    csrrs  -> (csr_rdata | rs1_data),
    csrrc  -> (csr_rdata & rs1_data),
    csrrwi -> zimm,
    csrrsi -> (zimm | rs1_data),
    csrrci -> (zimm & rs1_data),
  ))
  exu.result := csr_rdata
  /*
   mepc
   */
  private val mepc = RegInit(0.U(64.W))
  private val a_is_mepc = csr_addr === mepc_addr
  when(a_is_mepc)           { csr_rdata := mepc }//read
  when(a_is_mepc & is_csr)  { mepc := csr_wdata }//write
  .elsewhen(idb_out.intr | idb_out.exec) { mepc := idb_out.pc }
  idb_in.mepc := mepc
  /*
   mtvec
   */
  private val mtvec = RegInit(0.U(64.W))
  private val a_is_mtvec = csr_addr === mtvec_addr
  when(a_is_mtvec)          { csr_rdata := mtvec }//read
  when(a_is_mtvec & is_csr) { mtvec := Cat(csr_wdata(XLEN-1, 2), 0.U(2.W)) }//mode = direct
  idb_in.mtvec := mtvec
  /*
   mstatus
   */
  private val mstatus_in = Wire(UInt(64.W))
  private val mstatus_in_mie  = Wire(UInt(1.W))
  private val mstatus_in_mpie = Wire(UInt(1.W))
  private val mstatus_in_mpp  = Wire(UInt(2.W))
  private val mstatus = RegNext(init = "ha00000008".U(64.W), next = mstatus_in)//mie = 1
  mstatus_in_mie  := mstatus(3)
  mstatus_in_mpie := mstatus(7)
  mstatus_in_mpp  := mstatus(12, 11)//"b11".U //mstatus(12, 11)
  mstatus_in := Cat(mstatus(63, 13), mstatus_in_mpp, mstatus(10, 8), mstatus_in_mpie, mstatus(6, 4), mstatus_in_mie, mstatus(2, 0))
  chisel3.assert(mstatus_in.getWidth == 64, "mstatus_in should be 64")
  private val a_is_mstatus = csr_addr === mstatus_addr
  when(a_is_mstatus)          { csr_rdata := mstatus }
  when(a_is_mstatus & is_csr) { mstatus_in := csr_wdata }
  .elsewhen(idb_out.intr | idb_out.exec ) { mstatus_in_mpie:= mstatus(3); mstatus_in_mie:=0.U(1.W)}// mie -> mpie, mstatus(7):= mstatus(3)
  .elsewhen(idb_out.mret){ mstatus_in_mie := mstatus(7) }// mpie -> mie, mstatus(3) := mstatus(7)
  idb_in.mie := mstatus(3)
  /*
   mie(rw)
   */
  private val mie = RegInit("h00000088".U(64.W))
  private val a_is_mie = csr_addr === mie_addr
  when(a_is_mie)          { csr_rdata := mie }
  when(a_is_mie & is_csr) { mie := csr_wdata }
  idb_in.msie := mie(3)
  idb_in.mtie := mie(7)
  idb_in.meie := mie(11)
  /*
   mcause(read only)
   */
  private val mcause = RegInit(0.U(64.W))
  private val a_is_mcause = csr_addr === mcause_addr
  when(a_is_mcause){ csr_rdata := mcause }
  when(idb_out.intr)     { mcause := Cat(1.U(1.W), 0.U(59.W) ,idb_out.exce_code) }
  .elsewhen(idb_out.exec){ mcause := Cat(0.U(1.W), 0.U(59.W) ,idb_out.exce_code) }
  /*
   mip(read only)
   */
  private val mip_in = Wire(UInt(64.W))
  private val mip_in_msip = Wire(UInt(1.W))
  private val mip_in_mtip = Wire(UInt(1.W))
  private val mip_in_meip = Wire(UInt(1.W))
  private val mip = RegNext(init = 0.U(64.W), next = mip_in)
  mip_in := Cat(mip(63, 12), mip_in_meip, mip(10, 8), mip_in_mtip, mip(6, 4), mip_in_msip, mip(2, 0))
  private val a_is_mip = csr_addr === mip_addr
  when(a_is_mip) { csr_rdata := mip }
  mip_in_msip := sb.clint.msip
  mip_in_mtip := sb.clint.mtip  // accept external and core interrupt
  mip_in_meip := sb.meip
  idb_in.mtip := mip(7) // send to ctrl
  idb_in.msip := mip(3)
  idb_in.meip := mip(11)
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
   atom constraint
  */
//  when(rs1_idx === 0.U) { is_csr := false.B }
//  when(rd_idx === 0.U) { csr_rdata := 0.U }
//  idb_in.mtie := mtime>30000.U
}
