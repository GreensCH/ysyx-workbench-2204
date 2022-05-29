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
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg = RegInit(init = "h7FFFFFFC".U(64.W))

  io.pc := pc_reg

  pc_reg := pc_reg + 4.U(64.W)

  //  printf(p"NPC@pc_reg:${Hexadecimal(pc_reg)}\n")
}
