import chisel3._
import chisel3.util._
import chisel3.experimental._

object PcOpcode extends ChiselEnum {
  val next = Value(1.U)
  val jump = Value(2.U)
}

class PCUnit extends Module {
  val io = IO(new Bundle {
    val offset  =   Input (UInt(64.W))
    val npc_op  =   Input (PcOpcode())
    val pc      =   Output(UInt(64.W))
  })

  /* mux */
  val npc_mux_out = MuxCase("h80000000".U(64.W),
    Seq((io.npc_op === PcOpcode.next) -> 4.U(64.W),
        (io.npc_op === PcOpcode.jump) -> io.offset))
  /* pc reg */
  val pc_reg = RegInit("h80000000".U(64.W))
  /* pc adder */
  pc_reg := pc_reg + npc_mux_out
  /* output */
  io.pc := pc_reg

}
