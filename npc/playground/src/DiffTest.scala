import chisel3._
import chisel3.util._


class dpic_regfile extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val rst = Input(Reset())
    val gpr = Input(Vec(32, UInt(64.W)))
  })
  addResource("/dpic_regfile.v")
}


class TestRegFile extends Module{
  val io = IO(new Bundle {
    val gpr = Input(Vec(32, UInt(64.W)))
  } )

  val dpic_regfile = Module(new dpic_regfile)
  dpic_regfile.io.gpr := io.gpr
  dpic_regfile.io.clk := clock
  dpic_regfile.io.rst := reset
}


class dpic_pc extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val clk = Input(Clock())
    val rst = Input(Reset())
    val pc = Input(UInt(64.W))
  })
  addResource("/dpic_pc.v")
}


class TestPC extends Module{
  val io = IO(new Bundle {
    val pc = Input(UInt(64.W))
  } )

  val dpic_regfile = Module(new dpic_pc)
  dpic_regfile.io.pc := io.pc
  dpic_regfile.io.clk := clock
  dpic_regfile.io.rst := reset
}
