import chisel3._
import chisel3.util._

class PC extends Module {
  val io = IO(new Bundle {
    val is_jump  = Input (Bool())
    val offset   = Input(UInt(64.W))
    val is_jumpr = Input (Bool())
    val jump_reg = Input (UInt(64.W))
    val pc       = Output(UInt(64.W))
    val npc      = Output(UInt(64.W))
  })
//  printf("PCU\t\n")
  /* interface */
  val is_jump = io.is_jump
  val is_jumpr = io.is_jumpr
  val jump_reg = io.jump_reg
  val offset = io.offset
  /* instance */
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg_in = Wire(UInt(64.W))
  val pc_reg = RegNext(next = pc_reg_in, init = "h80000000".U(64.W))
  pc_reg_in := Mux(is_jumpr, jump_reg, pc_reg + npc_mux_out)

  io.pc := pc_reg
  io.npc := pc_reg_in
  //  printf(p"NPC@pc_reg:${Hexadecimal(pc_reg)}\n")
  val test_pc = Module(new TestPC)
  test_pc.io.pc := pc_reg
  test_pc.io.npc := pc_reg_in
}
