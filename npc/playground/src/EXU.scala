import chisel3._
import chisel3.util._


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
  vldNext := RegEnable(init = false.B, next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new IDUOut).bits))
  val reg = RegEnable(init = 0.U.asTypeOf(data), next = data, enable = rdyNext)
  dataNext := reg
}

class EXU extends Module{
  val io = IO(new Bundle{
    val prev = Flipped(new IDUOut)
    val next = new EXUOut
    val csr2ctrl = new CSRCtrlInf
    val sb = new SideBand
  })
  io.next.bits.id2wb := io.prev.bits.id2wb
  io.next.bits.id2mem := io.prev.bits.id2mem

  private val idb = io.prev.bits.id2ex
  private val memb = io.next.bits.ex2mem
  private val wbb = io.next.bits.ex2wb

  private val src1 = idb.src1
  private val src2 = idb.src2
  private val src3 = idb.src3
  private val operator = idb.operator
  private val div_inf = idb.div_inf
  private val byte = idb.srcsize.byte
  private val hword = idb.srcsize.hword
  private val word = idb.srcsize.word
  private val dword = idb.srcsize.dword

  private val alu_src1  = Wire(UInt(64.W))
  alu_src1 := Mux(word, src1(31, 0), src1)
  private val alu_src2  = Wire(UInt(64.W))
  alu_src2 := Mux(word, src2(31, 0), src2)
  private val salu_src1 = Wire(SInt(64.W))
  salu_src1 := Mux(word, src1(31, 0).asSInt(), src1.asSInt())
  private val salu_src2 = Wire(SInt(64.W))
  salu_src2 := Mux(word, src2(31, 0).asSInt(), src2.asSInt())
  //val adder_in1 = alu_src1
  //val adder_in2 = Mux(operator.sub, (alu_src2 ^ "hffff_ffff".U) + 1.U(64.W), alu_src2)
  //val adder_out = adder_in1 + adder_in2
  private val shift_src2 = Mux(word, src2(4, 0), src2(5, 0))
  /* Multiplier  */
  private val mdu = Module(new MDU)
  mdu.io.src1 := alu_src1
  mdu.io.src2 := alu_src2
  private val mdu_result = Mux(div_inf, "hFFFF_FFFF_FFFF_FFFF".U,mdu.io.result)
  mdu.io.mul      :=    operator.mul
  mdu.io.mulh     :=    operator.mulh
  mdu.io.mulhu    :=    operator.mulhu
  mdu.io.mulhsu   :=    operator.mulhsu
  mdu.io.div      :=    !div_inf & operator.div
  mdu.io.divu     :=    !div_inf & operator.divu
  mdu.io.rem      :=    operator.rem
  mdu.io.remu     :=    operator.remu
  /* result generator */
  //  private val result = Wire(UInt(64.W))
  private val result_sll = Wire(UInt(64.W))
  result_sll := (alu_src1  << shift_src2)
  private val result_srl = Wire(UInt(64.W))
  result_srl := alu_src1  >> shift_src2
  private val result_sra = Wire(SInt(64.W))
  result_sra := (salu_src1 >> shift_src2).asSInt
  private val add_src1_src3 = src1 + src3
  val result = MuxCase(mdu_result,
    Array(
      (operator.auipc ) -> add_src1_src3,//src3 = pc
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
      (operator.sll   ) -> result_sll,
      (operator.srl   ) -> result_srl,
      (operator.sra   ) -> result_sra.asUInt,
    )
  )
  private val result_out_signed = Wire(SInt(64.W))
  result_out_signed := MuxCase(0.S(64.W),
    Array(
      byte  -> result(7, 0).asSInt,
      hword -> result(15, 0).asSInt,
      word  -> result(31, 0).asSInt,
      dword -> result(63, 0).asSInt,
    )
  )
  private val result_out = Mux(dword, result, result_out_signed.asUInt)//result_out_signed.asUInt
  /* ex2mem interface */
  memb.addr := MuxCase(0.U(39.W), Array(
    idb.is_save -> add_src1_src3(38,0),
    idb.is_load -> (src1(38,0) + src2(38,0)),
  ))
  memb.we_data := result_out
  memb.we_mask := MuxCase("b0000_00000".U, Array(
    byte  -> "b0000_0001".U,
    hword -> "b0000_0011".U,
    word  -> "b0000_1111".U,
    dword -> "b1111_1111".U))
  /* ex2wb interface */
  wbb.result_data := result_out
  /* csr */
  private val csru = Module(new CSRU)
  csru.io.sb   <> io.sb
  csru.io.ctrl <> io.csr2ctrl
  csru.io.exu.csr_hit := idb.csr_hit
  csru.io.exu.csr_we := idb.csr_we
  csru.io.exu.operator := operator.csr
  csru.io.exu.rs1_data := src1
  csru.io.exu.rd_idx := idb.rd_idx
  csru.io.exu.rs1_idx := idb.zimm
  csru.io.exu.zimm := idb.zimm
  csru.io.exu.intr := idb.intr
  csru.io.exu.exec := idb.exec
  csru.io.exu.mret := idb.mret
  csru.io.exu.exce_code := idb.exce_code
  csru.io.exu.pc := idb.pc
  when(operator.csr.is_csr){ wbb.result_data := csru.io.exu.result }
  when(idb.is_iem){
    io.next.bits := 0.U.asTypeOf((new EXUOut).bits)
    io.next.bits.id2wb.intr_exce_ret := io.prev.bits.id2wb.intr_exce_ret
  }

  io.prev.ready := io.next.ready & mdu.io.ready
  io.next.valid := io.prev.valid & mdu.io.ready
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
            fwu: EX2FW,
            sb: SideBand, csr2ctrl: CSRCtrlInf
           ): EXU ={
    val ID2EXReg = Module(new EXReg)
    ID2EXReg.io.prev <> prev

    val exu = Module(new EXU)
    exu.io.sb <> sb
    exu.io.csr2ctrl <> csr2ctrl
    exu.io.prev <> ID2EXReg.io.next
    next <> exu.io.next

    fwu.is_load := ID2EXReg.io.next.bits.id2mem.memory_rd_en
    fwu.dst_addr := ID2EXReg.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data := exu.io.next.bits.ex2wb.result_data

    exu
  }
}