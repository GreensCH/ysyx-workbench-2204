import chisel3._
import chisel3.util._

class PC extends Module {
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val is_jump = Input (Bool())
    val offset  = Input(UInt(64.W))
    val pc      = Output(UInt(64.W))
    val npc      = Output(UInt(64.W))//NPC
  })
  printf("NPC@PCU\n")
  /* interface */
  val is_jump = io.is_jump
  val offset = io.offset
  val enable = io.enable

  val npc = Wire(UInt(64.W))
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg = RegNext(next = npc, init = "h80000000".U(64.W))
  printf(p"NPC-PCU@pc_reg:${Hexadecimal(pc_reg)}, ")
  npc := pc_reg + 4.U(64.W)
  io.pc := pc_reg
  io.npc:= npc
  printf(p"npc:${Hexadecimal(npc)}, is_jump:${io.is_jump}, mux_out:${Hexadecimal(npc_mux_out)}, off:${Hexadecimal(io.offset)}\n")

}
