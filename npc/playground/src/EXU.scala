import chisel3._
import chisel3.util._
import chisel3.experimental._


class EXUInput extends Bundle {
  val pc = Input(UInt(64.W))
  val src1 = Input(UInt(64.W))
  val src2 = Input(UInt(64.W))
  val src3 = Input(UInt(64.W))
  val csig = Flipped(new CtrlOutput)
}

class EXUOutput extends Bundle{
  val res_data = Output(UInt(64.W))
  val regfile_we_data = Output(UInt(5.W))
  val memory_addr = Output(UInt(64.W))
}

class EXU {
  val io = IO(new Bundle{
    val in = new EXUInput
    val out = new EXUOutput
  })
  val in = io.in
  val out = io.out

  val res = Wire(UInt(64.W))
  out.regfile_we_data


}