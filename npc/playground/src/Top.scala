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
  val pc_unit = Module(new PCUnit)
  val if_unit = Module(new IFUnit)

  pc_unit.io.npc_op := PcOpcode.next
  pc_unit.io.offset := DontCare
  if_unit.io.pc := pc_unit.io.pc

  io.inst := pc_unit.io.pc
  io.pc   := if_unit.io.inst

}
