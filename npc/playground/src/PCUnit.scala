import chisel3._
import chisel3.util._
import chisel3.experimental._

object PcOpcode extends ChiselEnum {
  val init = Value(0.U)
  val next = Value(1.U)
  val jump = Value(2.U)
}

class PCUnit extends Module {
  val io = IO(new Bundle {
    val npc_op  = Input (PcOpcode())
    val offset  = Input (UInt(64.W))
    val pc      = Output(UInt(64.W))
  })

    val npc_mux_out = MuxCase("h80000000".U(64.W),
      Seq((io.npc_op === PcOpcode.init) -> "h80000000".U(64.W),
          (io.npc_op === PcOpcode.next) -> 4.U(64.W),
          (io.npc_op === PcOpcode.jump) -> io.offset))

    val pc = RegInit("h80000000".U(64.W))
    pc := npc_mux_out
    io.pc <> pc

}
