import chisel3._
import chisel3.util._

/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */



class Top extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val mem_axi4  = new AXI4
    val mmio_axi4 = new AXI4
  })

  val BRIFInf = Wire(new BR2IF)
  val IDBRInf = Wire(new IDBR)
  val IFUOut = Wire(new IFUOut)
  val IDUOut = Wire(new IDUOut)
  val EXUOut = Wire(new EXUOut)
  val MEMUOut = Wire(new MEMUOut)
  val IDFWInf = Wire(new IDFW)
  val EXFWInf = Wire(new EX2FW)
  val MEMFWInf = Wire(new MEM2FW)
  val WBFWInf = Wire(new WB2FW)
  /* GPR connect wire */
  val RegfileIDInf = Wire(new RegfileID)
  val RegfileWBInf = Wire(new RegfileWB)
  /* AXI connect wire */
  val IFAXI = Wire(new AXI4)

  val ifu = IFU(next = IFUOut, bru = BRIFInf, maxi = IFAXI)
  val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWInf, bru = IDBRInf, regfile = RegfileIDInf, flush = BRIFInf.jump)
  val exu = EXU(prev = IDUOut, next = EXUOut, fwu = EXFWInf)
  val memu = MEMU(prev = EXUOut, next = MEMUOut, fwu = MEMFWInf)
  val wb = WBU(prev = MEMUOut, regfile = RegfileWBInf, fwu = WBFWInf)
  val fwu = FWU(idu = IDFWInf, exu = EXFWInf, memu = MEMFWInf, wbu = WBFWInf)
  val bru = BRU(ifu = BRIFInf, idu = IDBRInf)

  val regfile = Module(new RegFile)
  regfile.io.wbu <> RegfileWBInf
  regfile.io.idu <> RegfileIDInf

  IFAXI <> io.mem_axi4
  io.mmio_axi4 <> DontCare
  //val mmio_wire = Wire(new AXI4)
}


  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


