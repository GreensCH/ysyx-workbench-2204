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
    val inst_i = Input(UInt(64.W))
    val test_o = Output(UInt(64.W))
  })

  val test_reg = Reg(0.U(2.W))
//  test_reg := test_reg + 1
  val pc_unit = Module(new PCUnit)
  pc_unit.io.offset := io.inst_i
  pc_unit.io.npc_op := test_reg//PcOpcode.init

  io.test_o <> pc_unit.io.pc
}
