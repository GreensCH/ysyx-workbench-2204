import chisel3._
import chisel3.util._

/**
 * Spark CPU: A Single Cycle Processor,
 * CPU powered by RV64IM instruction set
 *
 */
class SparkCore extends Module {
  val io = IO(new Bundle {
    val maxi4  = new AXI4Master
    val sideband   = new  SideBand
    val sram0 = Flipped(new SRAMIO)
    val sram1 = Flipped(new SRAMIO)
    val sram2 = Flipped(new SRAMIO)
    val sram3 = Flipped(new SRAMIO)
    val sram4 = Flipped(new SRAMIO)
    val sram5 = Flipped(new SRAMIO)
    val sram6 = Flipped(new SRAMIO)
    val sram7 = Flipped(new SRAMIO)
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
  private val icon = Interconnect(maxi = io.maxi4, ifu = IFAxi, memu = LSUAxi, devu = MMIOAxi)
  private val sram_init_reg = RegInit(true.B)
  private val (sram_init_cnt, sram_init_end) = Counter(sram_init_reg, 64)

  private val ifu = IFU(next = IFUOut, bru = BRIFBdl, maxi = IFAxi, sys_ready = !sram_init_reg)
  private val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWBdl, bru = IDBRBdl, regfile = RegfileIDInf, flush = BRIFBdl.jump, csr = CSRCTRLBdl)
  private val exu = EXU(prev = IDUOut, next = EXUOut, fwu = EXFWBdl, sb = io.sideband, csr2ctrl = CSRCTRLBdl)
  private val memu = MEMU(prev = EXUOut, next = MEMUOut, fwu = MEMFWBdl, maxi = LSUAxi, mmio = MMIOAxi)
  private val wbu = WBU(prev = MEMUOut, regfile = RegfileWBInf, fwu = WBFWBdl)
  private val fwu = FWU(idu = IDFWBdl, exu = EXFWBdl, memu = MEMFWBdl, wbu = WBFWBdl)
  private val bru = BRU(ifu = BRIFBdl, idu = IDBRBdl)

  private val regfile = Module(new RegFile)
  regfile.io.wbu <> RegfileWBInf
  regfile.io.idu <> RegfileIDInf


  ifu.io.sram0  <> io.sram0
  ifu.io.sram1  <> io.sram1
  ifu.io.sram2  <> io.sram2
  ifu.io.sram3  <> io.sram3
  memu.io.sram4 <> io.sram4
  memu.io.sram5 <> io.sram5
  memu.io.sram6 <> io.sram6
  memu.io.sram7 <> io.sram7
  when(sram_init_reg){
    SRAM.write(io.sram0, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = ifu.io.sram0.rdata)
    SRAM.write(io.sram1, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = ifu.io.sram1.rdata)
    SRAM.write(io.sram2, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = ifu.io.sram2.rdata)
    SRAM.write(io.sram3, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = ifu.io.sram3.rdata)
    SRAM.write(io.sram4, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = memu.io.sram4.rdata)
    SRAM.write(io.sram5, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = memu.io.sram5.rdata)
    SRAM.write(io.sram6, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = memu.io.sram6.rdata)
    SRAM.write(io.sram7, sram_init_cnt, "h0000_0000_0000_0000_0000_0000_0000_0000".U(128.W), rdata = memu.io.sram7.rdata)
  }//"hFFFF_FFFF_FFFF_FFFF_FFFF_FFFF_FFFF_FFFF"
  when(sram_init_cnt === 63.U){
    sram_init_reg := false.B
  }
  dontTouch(io.maxi4)

}


class Top extends Module {
  val io = IO(new Bundle {
    val master  = new TopAxiMaster
    val slave = Flipped(new TopAxiMaster)
    val interrupt = Input(Bool())
  })
  // useless port
  TopAxiMaster.useless_slave(io.slave)
  // core connect
  private val core = Module(new SparkCore)
  io.master.awid := core.io.maxi4.aw.bits.id
  io.master.awlen := core.io.maxi4.aw.bits.len
  io.master.awaddr := core.io.maxi4.aw.bits.addr
  io.master.awsize := core.io.maxi4.aw.bits.size
  io.master.awburst := core.io.maxi4.aw.bits.burst
  io.master.awvalid := core.io.maxi4.aw.valid
  core.io.maxi4.aw.ready := io.master.awready

//  core.io.maxi4.ar.ready := true.B
//  core.io.maxi4.aw.ready := true.B
//  core.io.maxi4.w.ready := true.B

  io.master.arid     := core.io.maxi4.ar.bits.id
  io.master.arlen    := core.io.maxi4.ar.bits.len
  io.master.araddr   := core.io.maxi4.ar.bits.addr
  io.master.arsize   := core.io.maxi4.ar.bits.size
  io.master.arburst  := core.io.maxi4.ar.bits.burst
  io.master.arvalid  := core.io.maxi4.ar.valid
  core.io.maxi4.ar.ready := io.master.arready

  core.io.maxi4.r.bits.id   := io.master.rid
  core.io.maxi4.r.bits.data := io.master.rdata
  core.io.maxi4.r.bits.resp := io.master.rresp
  core.io.maxi4.r.bits.last := io.master.rlast
  io.master.rready       := core.io.maxi4.r.ready
  core.io.maxi4.r.valid     := io.master.rvalid

  io.master.wdata := core.io.maxi4.w.bits.data
  io.master.wlast := core.io.maxi4.w.bits.last
  io.master.wstrb := core.io.maxi4.w.bits.strb
  io.master.wvalid := core.io.maxi4.w.valid
  core.io.maxi4.w.ready := io.master.wready

  core.io.maxi4.b.bits.id := io.master.bid
  core.io.maxi4.b.bits.resp := io.master.bresp
  core.io.maxi4.b.valid := io.master.bvalid
  io.master.bready := core.io.maxi4.b.ready

  core.io.sram0 <> SRAM().io
  core.io.sram1 <> SRAM().io
  core.io.sram2 <> SRAM().io
  core.io.sram3 <> SRAM().io
  core.io.sram4 <> SRAM().io
  core.io.sram5 <> SRAM().io
  core.io.sram6 <> SRAM().io
  core.io.sram7 <> SRAM().io
  // clint instance
  private val clint = Module(new CLINT)
  core.io.sideband.clint <> clint.io.sideband
  core.io.sideband.meip := io.interrupt

}





