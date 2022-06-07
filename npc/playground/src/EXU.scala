import chisel3._
import chisel3.util._
import chisel3.experimental._



class EX2MEM extends Bundle{
  val rd_addr = Output(UInt(64.W))
  val we_data = Output(UInt(64.W))
  val we_addr = Output(UInt(64.W))
  val we_mask = Output(UInt(8 .W))
}

class EX2WB extends Bundle{
  val result_data = Output(UInt(64.W))
}

class EXU extends Module{
  val io = IO(new Bundle{
    val id2ex = Flipped(new ID2EX)
    val ex2mem = new EX2MEM
    val ex2wb = new EX2WB
  })

  val src1 = io.id2ex.src1
  val src2 = io.id2ex.src2
  val src3 = io.id2ex.src3
  val operator = io.id2ex.operator
  val byte = io.id2ex.srcsize.byte
  val hword = io.id2ex.srcsize.hword
  val word = io.id2ex.srcsize.word
  val dword = io.id2ex.srcsize.dword

  val alu_src1 = Mux(word, src1(31, 0), src1)
  val alu_src2 = Mux(word, src2(31, 0), src2)
  val salu_src1   = Mux(word, src1(31, 0).asSInt(), src1.asSInt())
  val salu_src2   = Mux(word, src2(31, 0).asSInt(), src2.asSInt())
  //val adder_in1 = alu_src1
  //val adder_in2 = Mux(operator.sub, (alu_src2 ^ "hffff_ffff".U) + 1.U(64.W), alu_src2)
  //val adder_out = adder_in1 + adder_in2

  /* result generator */
  val result = MuxCase(0.U(64.W),
    Array(
      (operator.auipc ) -> (src1 + src3),//src3 = pc
      (operator.lui   ) -> src1,
      (operator.jal | operator.jalr) -> (4.U + src3),//src1 = 0, src3 = pc
      (operator.sb    ) -> src2,
      (operator.sh    ) -> src2,
      (operator.sw    ) -> src2,
      (operator.sd    ) -> src2,
      (operator.sub   ) -> (alu_src1 - alu_src2),
      (operator.add   ) -> (alu_src1 + alu_src2),
      (operator.xor   ) -> (alu_src1 ^ alu_src2),
      (operator.or    ) -> (alu_src1 | alu_src2),
      (operator.and   ) -> (alu_src1 & alu_src2),
      (operator.slt   ) -> (salu_src1 < salu_src2),
      (operator.sltu  ) -> (alu_src1 < alu_src2),
      (operator.sll   ) -> (alu_src1  << alu_src2(5, 0)).asUInt(),
      (operator.srl   ) -> (alu_src1  >> alu_src2(5, 0)).asUInt(),
      (operator.sra   ) -> ((salu_src1 >> alu_src2(5, 0)).asSInt()).asUInt(),
      (operator.mul   ) -> (salu_src1  * salu_src2).asUInt(),
      (operator.mulh  ) -> ((salu_src1 * salu_src2) >> 64).asUInt(),
      (operator.mulhu ) -> ((salu_src1 * salu_src2) >> 64).asUInt(),
      (operator.mulhsu) -> ((salu_src1 * salu_src2) >> 64).asUInt(),
      (operator.div   ) -> (src1 / src2).asUInt(),
      (operator.divu  ) -> (src1 / src2).asUInt(),
      (operator.rem   ) -> (salu_src1 % salu_src2).asUInt(),
      (operator.remu  ) -> (salu_src1 % salu_src2).asUInt()
    )
  )
  val result_out = MuxCase(result,
    Array(
      byte  -> Sext(data = result, pos = 8),
      hword -> Sext(data = result, pos = 16),
      word  -> Sext(data = result, pos = 32),
      dword -> Sext(data = result, pos = 64),
    )
  )
  /* ex2mem interface */
  io.ex2mem.rd_addr := src1 + src2
  io.ex2mem.we_data := result_out
  io.ex2mem.we_addr := src1 + src3
  io.ex2mem.we_mask := MuxCase("b0000_00000".U,
    Array(
      byte  -> "b0000_0001".U,
      hword -> "b0000_0011".U,
      word  -> "b0000_1111".U,
      dword -> "b1111_1111".U,
    )
  )
  /* ex2wb interface */
  io.ex2wb.result_data := result_out
  /* ebreak */
  val ebreak = Module(new Ebreak)
  ebreak.io.valid := operator.ebreak
}


//object ALUOptype extends ChiselEnum {
//  val ADD = Value("b0000".U)
//  val SUB = Value("b1000".U)
//  val SLL = Value("b0001".U)
//  val SLT = Value("b0010".U)
//  val SLTU= Value("b0011".U)
//  val XOR = Value("b0100".U)
//  val SRL = Value("b0101".U)
//  val SRA = Value("b1101".U)
//  val OR  = Value("b0110".U)
//  val AND = Value("b0111".U)
//}
//
//class ALU extends Module {
//  val io = IO(new Bundle() {
//    val in = new Bundle() {
//      val word = Input(Bool())
//      val alu_op = Input(UInt(4.W))
//      val src1 = Input(UInt(64.W))
//      val src2 = Input(UInt(64.W))
//    }
//    val out = new Bundle() {
//      val res = Output(UInt(64.W))
//      val zero = Output(Bool())
//    }
//  })
//  val in = io.in
//  val out = io.out
//  val alu_op = ALUOptype(in.alu_op)
//
//  val src1 = Mux(in.word, in.src1(31, 0), in.src1)
//  val src2 = Mux(in.word, in.src2(31, 0), in.src2)
//  /***    ADDER    ***/
//  val adder_in1 = src1
//  val adder_in2 = Mux(alu_op === ALUOptype.SUB, (src2 ^ (-1).asUInt()) + 1, src2)
//  val adder_out = adder_in1 + adder_in2
//  /**  MAIN ALU  **/
//  val res = MuxCase(adder_out,
//    Array(
//      (alu_op === ALUOptype.ADD) -> adder_out,
//      (alu_op === ALUOptype.SUB) -> adder_out,
//      (alu_op === ALUOptype.XOR) -> (src1 ^ src2),
//      (alu_op === ALUOptype.OR)  -> (src1 | src2),
//      (alu_op === ALUOptype.AND) -> (src1 & src2),
//      (alu_op === ALUOptype.SLT) -> (src1.asSInt() < src2.asSInt()),
//      (alu_op === ALUOptype.SLTU)-> (src1 < src2),
//      (alu_op === ALUOptype.SLL)-> (src1 << src2(5, 0)).asUInt(),
//      (alu_op === ALUOptype.SRL)-> (src1 >> src2(5, 0)).asUInt(),
//      (alu_op === ALUOptype.SRA)-> (src1.asSInt() >> src2(5, 0)).asUInt()
//    )
//  )
//
//  out.res := Mux(in.word,Util.sext(res, pos = 32), res)
//  out.zero := out.res.toBool()
//}