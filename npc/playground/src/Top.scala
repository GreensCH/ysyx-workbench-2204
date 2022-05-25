import chisel3._
//import chisel3.util._
//import chisel3.experimental._


/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
    val inst = Output(UInt(32.W))
    val pc = Output(UInt(64.W))
  })
  val pcu = Module(new PCUnit)
  val ifu = Module(new IFUnit)
//  val ctrl = Module(new Controller)
//  ctrl.io := DontCare
  pcu.io.npcop_i := PcOpcode.next
  pcu.io.offset_i := DontCare
  ifu.io.pc_i := pcu.io.pc_o
  io.inst := ifu.io.inst_o
  io.pc   := pcu.io.pc_o

}
