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
//////////////////////////////////////
class EXRegIO extends Bundle{
  val id2ex = new ID2EX
  val id2mem = new ID2MEM
  val id2wb = new ID2WB
}
class EXReg extends Module{
  val io = IO(new Bundle() {
    val stall = Input(Bool())
    val in = Flipped(new EXRegIO)
    val out = new EXRegIO
  })
  val stall = io.stall

  val id2ex = io.in.id2ex
  val id2mem = io.in.id2mem//Mux(stall, 0.U.asTypeOf(new ID2MEM), io.in.id2mem)
//  val id2wb = Mux(stall, 0.U.asTypeOf(new ID2WB), io.in.id2wb)
  val id2wb = Wire(new ID2WB)
  id2wb.test_pc := io.in.id2wb.test_pc
  id2wb.test_inst := io.in.id2wb.test_inst
  id2wb.wb_sel := io.in.id2wb.wb_sel//Mux(stall, 0.U, io.in.id2wb.wb_sel)
  id2wb.regfile_we_addr := io.in.id2wb.regfile_we_addr//Mux(stall, 0.U, io.in.id2wb.regfile_we_addr)
  id2wb.regfile_we_en := io.in.id2wb.regfile_we_en//Mux(stall, 0.U, io.in.id2wb.regfile_we_en)

  val reg_2ex   =   RegNext(next = id2ex)
  val reg_2mem  =   RegNext(next = id2mem)
  val reg_2wb   =   RegNext(next = id2wb)

  io.out.id2ex  :=  reg_2ex
  io.out.id2mem :=  reg_2mem
  io.out.id2wb  :=  reg_2wb
}
//////////////////////////////////////
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
  val shift_src2 = Mux(word, src2(4, 0), src2(5, 0))
  /* Multiplier  */
  val mac = Module(new MAC)
  mac.io.src1 := src1
  mac.io.src2 := src2
  val mac_result = mac.io.result
  mac.io.mul      :=    operator.mul
  mac.io.mulh     :=    operator.mulh
  mac.io.mulhu    :=    operator.mulhu
  mac.io.mulhsu   :=    operator.mulhsu
  mac.io.div      :=    operator.div
  mac.io.divu     :=    operator.divu
  mac.io.rem      :=    operator.rem
  mac.io.remu     :=    operator.remu
  /* result generator */
  val result = Wire(UInt(64.W))
  result := MuxCase(mac_result,
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
      (operator.sll   ) -> (alu_src1  << shift_src2).asUInt(),
      (operator.srl   ) -> (alu_src1  >> shift_src2).asUInt(),
      (operator.sra   ) -> ((salu_src1 >> shift_src2).asSInt()).asUInt()
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

