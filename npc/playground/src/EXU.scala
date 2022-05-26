import chisel3._
import chisel3.util._
import chisel3.experimental._

object ALUOptype extends ChiselEnum {
  val ADD = Value("b0000".U)
  val SUB = Value("b1000".U)
  val SLL = Value("b0001".U)
  val SLT = Value("b0010".U)
  val SLTU= Value("b0011".U)
  val XOR = Value("b0100".U)
  val SRL = Value("b0101".U)
  val SRA = Value("b1101".U)
  val OR  = Value("b0110".U)
  val AND = Value("b0111".U)
}

class ALU extends Module {
  val io = IO(new Bundle() {
    val in = new Bundle() {
      val aluop = Input(UInt(4.W))
      val src1 = Input(UInt(64.W))
      val src2 = Input(UInt(64.W))
    }
    val out = new Bundle() {
      val res = Output(UInt(64.W))
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

  val alu = Module(new ALU).io
  alu.in.aluop := in.csig.alu_op


}