import chisel3._
import chisel3.util._
import chisel3.experimental._


class EXReg extends Module{
  val io = IO(new Bundle() {
    val prev = Flipped(new IDUOut)
    val next = new IDUOut
  })
  val rdyPrev  = io.prev.ready
  val vldPrev  = io.prev.valid
  val dataPrev = io.prev.bits
  val rdyNext  = io.next.ready
  val vldNext  = io.next.valid
  val dataNext = io.next.bits
  // Left
  rdyPrev := rdyNext//RegNext(rdyNext, true.B)//rdyNext
  // Right
  vldNext := RegEnable(next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new IDUOut).bits))
  val reg = RegEnable(next = data, enable = rdyNext)
  dataNext := reg
}
class EXU extends Module{
  val io = IO(new Bundle{
    val prev = Flipped(new IDUOut)
    val next = new EXUOut
  })
  io.next.bits.id2wb := io.prev.bits.id2wb
  io.next.bits.id2mem := io.prev.bits.id2mem
  io.prev.ready := io.next.ready
  io.next.valid := io.prev.valid
  val idb = io.prev.bits.id2ex
  val memb = io.next.bits.ex2mem
  val wbb = io.next.bits.ex2wb

  val src1 = idb.src1
  val src2 = idb.src2
  val src3 = idb.src3
  val operator = idb.operator
  val byte = idb.srcsize.byte
  val hword = idb.srcsize.hword
  val word = idb.srcsize.word
  val dword = idb.srcsize.dword

  val alu_src1  = Mux(word, src1(31, 0), src1)
  val alu_src2  = Mux(word, src2(31, 0), src2)
  val salu_src1 = Mux(word, src1(31, 0).asSInt(), src1.asSInt())
  val salu_src2 = Mux(word, src2(31, 0).asSInt(), src2.asSInt())
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
  memb.addr := MuxCase(0.U(64.W), Array(
    idb.is_save -> (src1 + src3),
    idb.is_load -> (src1 + src2)
  ))
  memb.we_data := result_out
  memb.we_mask := MuxCase("b0000_00000".U,
    Array(
      byte  -> "b0000_0001".U,
      hword -> "b0000_0011".U,
      word  -> "b0000_1111".U,
      dword -> "b1111_1111".U,
    )
  )
  /* ex2wb interface */
  wbb.result_data := result_out
  /* ebreak */
  val ebreak = Module(new Ebreak)
  ebreak.io.valid := operator.ebreak
}

class EXUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val id2mem = new ID2MEM
    val id2wb = new ID2WB
    val ex2mem = new EX2MEM
    val ex2wb = new EX2WB
  }
}

object EXU {
  def apply(prev: IDUOut, next: EXUOut,
            fwu: EX2FW): EXU ={
    val ID2EXReg = Module(new EXReg)
    ID2EXReg.io.prev <> prev

    val exu = Module(new EXU)
    exu.io.prev <> ID2EXReg.io.next
    next <> exu.io.next

    fwu.is_load := ID2EXReg.io.next.bits.id2mem.memory_rd_en
    fwu.dst_addr := ID2EXReg.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data := exu.io.next.bits.ex2wb.result_data

    /* test */
    if(!SparkConfig.Debug){
      fwu.test_pc := DontCare
    }else{
      fwu.test_pc := exu.io.prev.bits.id2wb.test_pc
    }

    exu
  }
}