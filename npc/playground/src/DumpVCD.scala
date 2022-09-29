import chisel3._
import chisel3.util._

class ysyx_040978_dumpvcd extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clk = Input(Clock())
    val rst = Input(Reset())
    val en = Input(Bool())
  })


  addResource("/ysyx_040978_dumpvcd.v")

}

class DumpWave extends Module{
  val io = IO(new Bundle{ val en = Input(Bool()) })
  private val DumpVCD = Module(new ysyx_040978_dumpvcd)
  DumpVCD.io.en := io.en
  DumpVCD.io.rst <> reset
  DumpVCD.io.clk <> clock

}