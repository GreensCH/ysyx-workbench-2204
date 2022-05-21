import chisel3._

/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
      val i_test = Input(Bool())
      val o_test = Input(Bool())
  })
  io.i_test <> io.o_test;
}
