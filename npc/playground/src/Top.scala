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
    val inst = Output(UInt(64.W))
    val pc = Output(UInt(64.W))
  })
  val pcu = Module(new PCUnit)
  val ifu = Module(new IFUnit)

  pcu.io.npc_op := PcOpcode.next


  ifu.io.offset := DontCare
  ifu.io.pc := pc_unit.io.pc

  io.inst := pcu.io.pc
  io.pc   := pcu.io.inst

}
