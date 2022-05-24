import chisel3._
import chisel3.util._
import chisel3.experimental._


/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
      val test_i = Input(Bool())
      val inst_i = Input(UInt(64.W))
      val test_o = Output(Bool())
  })
  // val pcunit = Module(new PCUnit)
  io.test_i := DontCare
  io.test_o := DontCare
  io.inst_i := DontCare

}
