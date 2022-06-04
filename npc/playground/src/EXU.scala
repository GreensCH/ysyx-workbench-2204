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
//  printf("EXU\t\n")
  val src1 = io.id2ex.src1
  val src2 = io.id2ex.src2
  val src3 = io.id2ex.src3
  val operator = io.id2ex.operator
  val byte = io.id2ex.srcsize.byte
  val hword = io.id2ex.srcsize.hword
  val word = io.id2ex.srcsize.word
  val dword = io.id2ex.srcsize.dword
  /*    adder    */
  val alu_src1 = Mux(word, src1(31, 0), src1)
  val alu_src2 = Mux(word, src2(31, 0), src2)
  //val adder_in1 = alu_src1
  //val adder_in2 = Mux(operator.sub, (alu_src2 ^ "hffff_ffff".U) + 1.U(64.W), alu_src2)
  //val adder_out = adder_in1 + adder_in2
  /* result generator */
  val res = MuxCase(0.U,
    Array(
      (operator.auipc ) -> (src1 + src2),//src2 = pc
      (operator.lui   ) -> src1,
      (operator.jal | operator.jalr) -> (4.U + src2),//src1 = 0, src = pc
      (operator.sb    ) -> src2,
      (operator.sh    ) -> src2,
      (operator.sw    ) -> src2,
      (operator.sd    ) -> src2,
      (operator.sub   ) -> (alu_src1 - alu_src2),
      (operator.add   ) -> (alu_src1 + alu_src2),
      (operator.xor   ) -> (alu_src1 ^ alu_src2),
      (operator.or    ) -> (alu_src1 | alu_src2),
      (operator.and   ) -> (alu_src1 & alu_src2),
      (operator.slt   ) -> (alu_src1.asSInt() < alu_src2.asSInt()),
      (operator.sltu  ) -> (alu_src1 < alu_src2),
      (operator.sll   ) -> (alu_src1 << alu_src2(5, 0)).asUInt(),
      (operator.srl   ) -> (alu_src1 >> alu_src2(5, 0)).asUInt(),
      (operator.sra   ) -> (alu_src1.asSInt() >> alu_src2(5, 0)).asUInt(),
      (operator.mul   ) -> 0.U,
      (operator.mulh  ) -> 0.U,
      (operator.mulhu ) -> 0.U,
      (operator.mulhsu) -> 0.U,
      (operator.div   ) -> 0.U,
      (operator.divu  ) -> 0.U,
      (operator.rem   ) -> 0.U,
      (operator.remu  ) -> 0.U,
    )
  )
  val result_out = MuxCase(res,
    Array(
      byte  -> Sext(data = res, pos = 8),
      hword -> Sext(data = res, pos = 16),
      word  -> Sext(data = res, pos = 32),
      dword -> Sext(data = res, pos = 64),
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
