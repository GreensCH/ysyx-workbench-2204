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

  val test = 1.U.asTypeOf(new BR2PC)//Wire(new BR2PC)
  val test2 = Wire(new IF2ID)
  test2.ready := io.ready
  val ifu = IFU(in = test,next = test2)
  printf(s"${test2}")
  printf(s"${io.inst}")
}

  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst


