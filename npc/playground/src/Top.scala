import chisel3._
import chisel3.util._

/**
 * Spark CPU: A Single Cycle Processor,
 * CPU powered by RV64IM instruction set
 *
 */
class SparkCore extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val mem_axi4  = new AXI4Master
    val mmio_axi4 = new AXI4Master
    val clint_axi4 = new AXI4Master
    val sideband   = new  SideBand
  })

  private val BRIFBdl = Wire(new BR2IF)
  private val IDBRBdl = Wire(new IDBR)
  private val IFUOut  = Wire(new IFUOut)
  private val IDUOut  = Wire(new IDUOut)
  private val EXUOut  = Wire(new EXUOut)
  private val MEMUOut = Wire(new MEMUOut)
  private val IDFWBdl = Wire(new IDFW)
  private val EXFWBdl = Wire(new EX2FW)
  private val MEMFWBdl = Wire(new MEM2FW)
  private val WBFWBdl  = Wire(new WB2FW)
  private val CSRCTRLBdl = Wire(new CSRCtrlInf)
  /* GPR connect wire */
  private val RegfileIDInf = Wire(new RegfileID)
  private val RegfileWBInf = Wire(new RegfileWB)
  /* AXI connect wire */
  private val IFAxi = Wire(new AXI4Master)
  private val LSUAxi = Wire(new AXI4Master)
  private val MMIOAxi = Wire(new AXI4Master)
  private val interconnect = Interconnect(s00 = IFAxi, s01 = LSUAxi, s02 = MMIOAxi, m00 = io.mem_axi4, m01 = io.mmio_axi4, m02 = io.clint_axi4)

  private val ifu = IFU(next = IFUOut, bru = BRIFBdl, maxi = IFAxi)
  private val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWBdl, bru = IDBRBdl, regfile = RegfileIDInf, flush = BRIFBdl.jump, csr = CSRCTRLBdl)
  private val exu = EXU(prev = IDUOut, next = EXUOut, fwu = EXFWBdl, sb = io.sideband, csr2ctrl = CSRCTRLBdl)
  private val memu = MEMU(prev = EXUOut, next = MEMUOut, fwu = MEMFWBdl, maxi = LSUAxi, mmio = MMIOAxi)
  private val wbu = WBU(prev = MEMUOut, regfile = RegfileWBInf, fwu = WBFWBdl)
  private val fwu = FWU(idu = IDFWBdl, exu = EXFWBdl, memu = MEMFWBdl, wbu = WBFWBdl)
  private val bru = BRU(ifu = BRIFBdl, idu = IDBRBdl)

  private val regfile = Module(new RegFile)
  regfile.io.wbu <> RegfileWBInf
  regfile.io.idu <> RegfileIDInf

  dontTouch(io.mem_axi4)
  dontTouch(io.mmio_axi4)

}


class Top extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val mem_axi4  = new AXI4Master
    val mmio_axi4 = new AXI4Master
    val plic_intr = Input(Bool())
  })

  private val core = Module(new SparkCore)
  core.io.inst <> io.inst
  core.io.mem_axi4 <> io.mem_axi4
  core.io.mmio_axi4 <> io.mmio_axi4

  private val clint = Module(new CLINT)
  core.io.sideband.clint <> clint.io.sideband
  core.io.clint_axi4 <> clint.io.mmio
  core.io.sideband.meip := io.plic_intr
}


/* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


