import chisel3._
import chisel3.util._
import chisel3.experimental._

object ALUOptype extends ChiselEnum {
  val add = Value(1.U)
  val sub = Value(2.U)
  val sub = Value(2.U)
  val sub = Value(2.U)
  val sub = Value(2.U)
}

class ALU extends Module {
  val io = IO(new Bundle() {
    val in = new Bundle() {
      val aluop = Input()
      val src1 = Input(UInt(64.W))
      val src2 = Input(UInt(64.W))
    }
    val out = new Bundle() {
      val res = Outpu(UInt(64.W))
      val zero = Output(Bool())
    }
  })


}

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

class EXU Module {
  val io = IO(new Bundle{
    val in = new EXUInput
    val out = new EXUOutput
  })
  val in = io.in
  val out = io.out

  val res = Wire(UInt(64.W))
  out.regfile_we_data


}