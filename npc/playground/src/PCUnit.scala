import chisel3._
import chisel3.util._
import chisel3.experimental._

object PcOpcode extends ChiselEnum {
  val next = Value(1.U)
  val jump = Value(2.U)
}

class PCUnit extends Module {
  val io = IO(new Bundle {
    val offset_i  =   Input (UInt(64.W))
    val npcop_i  =   Input (PcOpcode())
    val pc_o      =   Output(UInt(64.W))
  })

  /* mux */
  val npc_mux_out = MuxCase("h80000000".U(64.W),
    Seq((io.npcop_i === PcOpcode.next) -> 4.U(64.W),
        (io.npcop_i === PcOpcode.jump) -> io.offset_i))
  /* pc reg */
  val pc_reg = RegInit("h80000000".U(64.W))
  /* pc adder */
  pc_reg := pc_reg + npc_mux_out
  /* output */
  io.pc_o := pc_reg

}
