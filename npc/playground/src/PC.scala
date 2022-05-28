import chisel3._


class PC extends Module {
  val io = IO(new Bundle {
    val is_jump = Input (Bool())
    val offset  = Input(UInt(64.W))
    val pc      = Output(UInt(64.W))
    val npc      = Output(UInt(64.W))//NPC
  })
  printf("NPC@PCU\n")
  /* interface */
  val is_jump = io.is_jump
  val offset = io.offset

  val npc = Wire(UInt(64.W))
  val npc_mux_out = Mux(is_jump, offset, 4.U(64.W))
  val pc_reg = RegNext(next = npc, init = "h80000000".U(64.W))
  npc := pc_reg + npc_mux_out
  io.pc := pc_reg
  io.npc:= npc

}
