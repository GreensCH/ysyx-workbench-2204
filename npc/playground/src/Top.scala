import chisel3._

/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
      val i_test = Input(Bool())
      val o_test = Output(Bool())
  })
  io.o_test <> io.i_test;
  val pcunit = Module(new PCUnit)
}
