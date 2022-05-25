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
  val ctrl = Module(new Controller)
  ctrl.io.m2r_ctrl_o := DontCare
  ctrl.io.exop_o := DontCare
  ctrl.io.regfile_wen_o := DontCare
  ctrl.io.instfmt_o := DontCare
  ctrl.io.inst_i := DontCare


  pcu.io.npcop_i := PcOpcode.next
  pcu.io.offset_i := DontCare
  ifu.io.pc_i := pcu.io.pc_o
  io.inst := ifu.io.inst_o
  io.pc   := pcu.io.pc_o

}
