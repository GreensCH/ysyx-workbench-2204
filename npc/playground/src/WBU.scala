import chisel3._
import chisel3.util._
import chisel3.experimental._


class WBU extends Module {
  val io = IO(new Bundle {
    val in = new Bundle {
      val offset = Input (UInt(64.W))
      val npcop = Input (PCUOptype())
    }
    val out = new Bundle {
      val pc = Output(UInt(64.W))
    }
  })

  val pc_reg = RegInit("h80000000".U(64.W))

  val npc_mux_out = MuxCase("h80000000".U(64.W),
    Array((io.in.npcop === PCUOptype.next) -> 4.U(64.W),
      (io.in.npcop === PCUOptype.jump) -> io.in.offset))

  pc_reg := pc_reg + npc_mux_out
  io.out.pc := pc_reg

}
