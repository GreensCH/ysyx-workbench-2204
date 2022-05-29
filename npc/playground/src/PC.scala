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
  val pc_reg_in = Wire(UInt(64.W))
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg_out = RegNext(next = pc_reg_in, init = "h7FFFFFFC".U(64.W))

  pc_reg_in := Mux(is_jumpr, jump_reg, npc_mux_out + pc_reg_out)

  //npc_mux_out + pc_reg_out
  io.pc := pc_reg_out
  io.npc := pc_reg_in
  //  printf(p"NPC@pc_reg:${Hexadecimal(pc_reg)}\n")
}
