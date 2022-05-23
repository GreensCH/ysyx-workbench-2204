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
      val o_test2= Output(UInt(64.W))
  })
  // io.o_test <> io.i_test;
  val pcunit = Module(new PCUnit)
  pcunit.io.i_test <> io.i_test
  pcunit.io.o_test <> io.o_test
  val pc = RegInit("h80000000".U(64.W))
  pc := 1.U
  io.o_test2 <> pc
}
