import chisel3._
import chisel3.util._


class dpic_pc extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val rst = Input(Reset())
    val pc = Input(UInt(64.W))
    val npc = Input(UInt(64.W))
  })
  addResource("/dpic_pc.v")
}


class TestPC extends Module{
  val io = IO(new Bundle {
    val pc = Input(UInt(64.W))
    val npc = Input(UInt(64.W))
  } )

  val dpic_pc = Module(new dpic_pc)
  dpic_pc.io.pc := io.pc
  dpic_pc.io.npc := io.npc
  dpic_pc.io.clk := clock
  dpic_pc.io.rst := reset
}
