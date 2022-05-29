import chisel3._
import chisel3.util._


class dpic_difftest extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val rst = Input(Reset())
    val gpr = Input(Vec(32, UInt(64.W)))
  })
  addResource("/dpic_difftest.v")
}


class DiffTest extends Module{
  val io = IO(new Bundle {
    val gpr = Input(Vec(32, UInt(64.W)))
  } )

  val dpic_difftest = Module(new dpic_difftest)
  dpic_difftest.io.gpr := io.gpr
  dpic_difftest.io.clk := clock
  dpic_difftest.io.rst := reset
}