import chisel3._
import chisel3.util._

class PC extends Module {
  val io = IO(new Bundle {
    val is_jump = Input (Bool())
    val offset  = Input(UInt(64.W))
    val pc      = Output(UInt(64.W))
  })
//  printf("PCU\t\n")
  /* interface */
  val is_jump = io.is_jump
  val offset = io.offset
  /* instance */
  val pc_reg_in = Wire(UInt(64.W))
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg_out = RegNext(next = pc_reg_in, init = "h80000000".U(64.W))

  pc_reg_in := npc_mux_out + pc_reg_out
  io.pc := pc_reg_in
  //  printf(p"NPC@pc_reg:${Hexadecimal(pc_reg)}\n")
}
