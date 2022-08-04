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
  })

  val BRIFBdl = Wire(new BR2IF)
  val IDBRBdl = Wire(new IDBR)
  val IFUOut = Wire(new IFUOut)
  val IDUOut = Wire(new IDUOut)
  val EXUOut = Wire(new EXUOut)
  val MEMUOut = Wire(new MEMUOut)
  val IDFWBdl = Wire(new IDFW)
  val EXFWBdl = Wire(new EX2FW)
  val MEMFWBdl = Wire(new MEM2FW)
  val WBFWBdl = Wire(new WB2FW)
  /* GPR connect wire */
  val RegfileIDInf = Wire(new RegfileID)
  val RegfileWBInf = Wire(new RegfileWB)
  /* AXI connect wire */
  val IFAxi = Wire(new AXI4Master)
  val LSUAxi = Wire(new AXI4Master)
  val MMIOAxi = Wire(new AXI4Master)
  val interconnect = Interconnect(s00 = IFAxi, s01 = LSUAxi, s02 = MMIOAxi, m00 = io.mem_axi4, m01 = io.mmio_axi4)

  val ifu = IFU(next = IFUOut, bru = BRIFBdl, maxi = IFAxi)
  val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWBdl, bru = IDBRBdl, regfile = RegfileIDInf, flush = BRIFBdl.jump)
  val exu = EXU(prev = IDUOut, next = EXUOut, fwu = EXFWBdl)
  val memu = MEMU(prev = EXUOut, next = MEMUOut, fwu = MEMFWBdl, maxi = LSUAxi, mmio = MMIOAxi)
  val wb = WBU(prev = MEMUOut, regfile = RegfileWBInf, fwu = WBFWBdl)
  val fwu = FWU(idu = IDFWBdl, exu = EXFWBdl, memu = MEMFWBdl, wbu = WBFWBdl)
  val bru = BRU(ifu = BRIFBdl, idu = IDBRBdl)

  val regfile = Module(new RegFile)
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
  })

  private val core = Module(new SparkCore)
  core.io.inst <> io.inst
  core.io.mem_axi4 <> io.mem_axi4
  core.io.mmio_axi4 <> io.mmio_axi4



}


  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


