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
  val RegfileIDInf = Wire(new RegfileID)
  val RegfileWBInf = Wire(new RegfileWB)
  val ifu = IFU(next = IFUOut, bru = BRPCInf)
  val idu = IDU(prev = IFUOut, next = IDUOut, fwu = IDFWInf, regfile = RegfileIDInf)
  val exu = EXU(prev = IDUOut, next = EXUOut)
  val memu = MEMU(prev = EXUOut, next = MEMUOut)
  val wb = WBU(prev = MEMUOut, regfile = RegfileWBInf)
  val regfile = Module(new RegFile)
  regfile.io.wbu <> RegfileWBInf
  regfile.io.idu <> RegfileIDInf

}

  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


