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
      val word = Input(Bool())
      val alu_op = Input(UInt(4.W))
      val src1 = Input(UInt(64.W))
      val src2 = Input(UInt(64.W))
    }
    val out = new Bundle() {
      val res = Output(UInt(64.W))
      val zero = Output(Bool())
    }
  })
  val in = io.in
  val out = io.out
  val alu_op = ALUOptype(in.alu_op)

  val src1 = Mux(in.word, in.src1(31, 0), in.src1)
  val src2 = Mux(in.word, in.src2(31, 0), in.src2)
  /***    ADDER    ***/
  val adder_in1 = src1
  val adder_in2 = Mux(alu_op === ALUOptype.SUB, (src2 ^ (-1).asUInt()) + 1, src2)
  val adder_out = adder_in1 + adder_in2
  /**  MAIN ALU  **/
  val res = MuxCase(adder_out,
    Array(
      (alu_op === ALUOptype.ADD) -> adder_out,
      (alu_op === ALUOptype.SUB) -> adder_out,
      (alu_op === ALUOptype.XOR) -> (src1 ^ src2),
      (alu_op === ALUOptype.OR)  -> (src1 | src2),
      (alu_op === ALUOptype.AND) -> (src1 & src2),
      (alu_op === ALUOptype.SLT) -> (src1.asSInt() < src2.asSInt()),
      (alu_op === ALUOptype.SLTU)-> (src1 < src2),
      (alu_op === ALUOptype.SLL)-> (src1 << src2(5, 0)).asUInt(),
      (alu_op === ALUOptype.SRL)-> (src1 >> src2(5, 0)).asUInt(),
      (alu_op === ALUOptype.SRA)-> (src1.asSInt() >> src2(5, 0)).asUInt()
    )
  )

  out.res := Mux(in.word,Util.sext(res, pos = 32), res)
  out.zero := out.res.toBool()
}

class EXUInput extends Bundle {
  val pc = Input(UInt(64.W))
  val src1 = Input(UInt(64.W))
  val src2 = Input(UInt(64.W))
  val src3 = Input(UInt(64.W))
  val csig = Flipped(new CtrlOutput)
}
class EXUOutput extends Bundle{
  val data = Output(UInt(64.W))
  val memory_we_data = Output(UInt(64.W))
}
class EXU extends Module{
  val io = IO(new Bundle{
    val in = new EXUInput
    val out = new EXUOutput
  })
  val in = io.in
  val out = io.out
  val inst_type = in.csig.inst_type
  val fun3 = in.csig.alu_op(2, 0)
  ////////////
  val alu = Module(new ALU).io
  alu.in.word := inst_type.CalWR | inst_type.CalWI
  alu.in.alu_op := in.csig.alu_op
  alu.in.src1 := in.src1
  alu.in.src2 := in.src2
  val alu_res = alu.out.res
  alu.out.zero <> DontCare
///////////
//  val load_addr = MuxCase(0.U(64.W),
//    Array(
//      (in.csig.fun3 === "b000".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b001".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b010".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b011".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b100".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b101".U) -> 0.U(64.W),
//      (in.csig.fun3 === "b110".U) -> 0.U(64.W),
//    )
//  )


///////////
  //WB
  out.data := MuxCase(0.U(64.W),
    Array(
      (inst_type.AuipcU) -> (in.pc + in.src1),
      (inst_type.LuiU) -> in.src1,
      (inst_type.JalJ | inst_type.JalrI) -> (in.pc + 4.U(64.W)),
      (inst_type.LoadI) -> (in.src1 + in.src2),
      (inst_type.SaveS) -> (in.src1 + in.src3),
      (inst_type.CalI | inst_type.CalWI | inst_type.CalR | inst_type.CalWR) -> alu.out.res
    )
  )
  out.memory_we_data :=
////////////
}