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
    val ready = Input(Bool())
  })

//  val ifuIn = Wire(new BR2PC)
//  val ifuOut = Wire(new IFUOut)
//  ifuIn.jump := false.B
//  ifuIn.npc := 3.U
//  ifuOut.ready := io.ready
  val BRPCInf = Wire(new BR2PC)
  val IFOut = Wire(new IFUOut)
  val IDOut = Wire(new IDUOut)
  val EXOut = Wire(new EXUOut)
  val IDFWInf = Wire(new IDFW)
  val IDRegfileInf = Wire(new IDRegfile)
  val ifu = IFU(next = IFOut, bru = BRPCInf)
  val idu = IDU(prev = IFOut, next = IDOut, fwu = IDFWInf, regfile = IDRegfileInf)

}

  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


