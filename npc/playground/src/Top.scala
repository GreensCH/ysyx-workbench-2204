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
  })

//  val ifuIn = Wire(new BR2PC)
//  val ifuOut = Wire(new IFUOut)
//  ifuIn.jump := false.B
//  ifuIn.npc := 3.U
//  ifuOut.ready := io.ready
  val BRPCInf = 0.U.asTypeOf(new BR2PC)//Wire(new BR2PC)
  val IFUOut = Wire(new IFUOut)
  val IDUOut = Wire(new IDUOut)
  val EXUOut = Wire(new EXUOut)
  val MEMUOut = Wire(new MEMUOut)
  val IDFWInf = Wire(new IDFW)
  val EXFWInf = Wire(new EX2FW)
  val MEMFWInf = Wire(new MEM2FW)
  val WBFWInf = Wire(new WB2FW)

  val RegfileIDInf = Wire(new RegfileID)
  val RegfileWBInf = Wire(new RegfileWB)
  val ifu = IFU(next = IFUOut, bru = BRPCInf)
  val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWInf, regfile = RegfileIDInf)
  val exu = EXU(prev = IDUOut, next = EXUOut, fwu = EXFWInf)
  val memu = MEMU(prev = EXUOut, next = MEMUOut, fwu = MEMFWInf)
  val wb = WBU(prev = MEMUOut, regfile = RegfileWBInf, fwu = WBFWInf)
  val regfile = Module(new RegFile)
  regfile.io.wbu <> RegfileWBInf
  regfile.io.idu <> RegfileIDInf
  val fwu = Module(new FWU)
  fwu.io.idu <> IDFWInf
  fwu.io.exu <> EXFWInf
  fwu.io.memu <> MEMFWInf
  fwu.io.wbu <> WBFWInf

}

  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


