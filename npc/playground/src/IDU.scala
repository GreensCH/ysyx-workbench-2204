import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils


class IDReg extends Module{
  val io = IO(new Bundle {
    val prev = Flipped(new IFUOut)
    val next = new IFUOut
  })
  // nop inst
  val nop = Wire(new IF2ID)
  nop.inst := "h00000013".U(32.W)
  nop.pc := 0.U(64.W)
  // Reference
  val rdyPrev = io.prev.ready
  val vldPrev = io.prev.valid
  val dataPrev= io.prev.bits.if2id
  val rdyNext = io.next.ready
  val vldNext = io.next.valid
  val dataNext= io.next.bits.if2id
  // Left
  rdyPrev := rdyNext//RegNext(rdyNext, true.B)//rdyNext
  // Right
  vldNext := RegEnable(next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, nop)
  val reg = RegEnable(next = data, enable = rdyNext)
  dataNext := reg
}
//////////////////////////////////////
class IDU extends Module {
  val io = IO(new Bundle {
    val regfile = Flipped(new RegfileID)
    val fwu = new IDFW
    val bru = new IDBR
    val csr = Flipped(new CSRCtrlInf)
    val prev = Flipped(new IFUOut)
    val next = new IDUOut
  })
  val ifb = io.prev.bits.if2id
  val rfb  = io.regfile
  val fwb  = io.fwu
  val brb  = io.bru
  val exb  = io.next.bits.id2ex
  val memb = io.next.bits.id2mem
  val wbb  = io.next.bits.id2wb
  val csrb_in = io.next.bits.id2ex
  val csrb_out = io.csr.out
  val inst = ifb.inst
  val pc = ifb.pc
  /* controller instance */
  val ctrl = Module(new Controller)
  val operator = ctrl.io.operator
  val optype = ctrl.io.optype
  val srcsize = ctrl.io.srcsize
  val is_load = ctrl.io.is_load
  val is_save = ctrl.io.is_save
  ctrl.io.inst := inst
  /* regfile interface */
  rfb.en := true.B
  rfb.addr1 := inst(19, 15)
  rfb.addr2 := inst(24, 20)
  val reg_src1 = rfb.data1
  val reg_src2 = rfb.data2
  /* forwarding interface */
  val src1_data = fwb.fw_src1_data
  val src2_data = fwb.fw_src2_data
  fwb.optype := optype
  fwb.operator := operator
  fwb.src1_data := reg_src1
  fwb.src2_data := reg_src2
  fwb.src1_addr := inst(19, 15)
  fwb.src2_addr := inst(24, 20)
  /* id2mem interface */
  memb.fencei := operator.fencei
  memb.sext_flag := operator.lb | operator.lh  | operator.lw | operator.ld
  memb.size := srcsize
  memb.memory_we_en := is_save
  memb.memory_rd_en := is_load
  /* id2wb interface */
  wbb.ebreak := operator.ebreak
  wbb.fencei := operator.fencei
  wbb.wb_sel := is_load
  wbb.regfile_we_en := optype.Utype | optype.Itype | optype.Rtype | optype.Jtype | operator.csr.is_csr
  wbb.regfile_we_addr := Mux(optype.Btype | optype.Stype, 0.U, inst(11, 7))
  if(SparkConfig.Debug){
    wbb.test_pc     := pc
    wbb.test_inst   := inst
    wbb.test_clint  :=  (is_save & (exb.src1 + exb.src3)>="h0200_0000".U & (exb.src1 + exb.src3)<="h0200_BFFF".U) |
                        (is_load & (exb.src1 + exb.src2)>="h0200_0000".U & (exb.src1 + exb.src2)<="h0200_BFFF".U) |
                        operator.csr.is_csr | csrb_in.exec | csrb_in.intr
  }else{
    wbb.test_pc     := DontCare
    wbb.test_inst   := DontCare
    wbb.test_clint  := DontCare
  }

  /* id2ex interface */
  exb.operator  := operator
  exb.optype    := optype
  exb.srcsize   := srcsize
  exb.is_load   := is_load
  exb.is_save   := is_save
  exb.csr_we    := MuxCase(false.B, Array(
    (operator.csr.csrrw  | operator.csr.csrrwi) -> true.B,
    (operator.csr.csrrs  | operator.csr.csrrc)  -> (exb.zimm =/= 0.U(5.W)),
    (operator.csr.csrrsi | operator.csr.csrrci) -> (exb.zimm =/= 0.U(5.W)),
  ))
  exb.csr_idx   := inst(31, 20)
  exb.zimm      := inst(19, 15)
  exb.rd_idx    := wbb.regfile_we_addr
  private val exb_src1_Utype = Wire(SInt(64.W))
  exb_src1_Utype := Cat(inst(31, 12), Fill(12, 0.U)).asSInt
  exb.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype |
        optype.Itype |
        optype.Btype |
        optype.Stype |
        operator.csr.is_csr
        ) -> src1_data,
      optype.Utype -> exb_src1_Utype.asUInt
    )
  )
  private val exb_src2_Itype = Wire(SInt(64.W))
  exb_src2_Itype := Cat(inst(31, 20)).asSInt
  exb.src2 := MuxCase(default = 0.U(64.W),
    Array(
      (optype.Rtype | optype.Stype | optype.Btype) -> src2_data,
      (optype.Itype) -> exb_src2_Itype.asUInt,
    )
  )
  //jalr or save addr
  exb.div_inf := exb.src2 === 0.U(64.W) & (operator.div | operator.divu)
  private val exb_src3_false = Wire(SInt(64.W))
  exb_src3_false := Cat(inst(31, 25), inst(11, 7)).asSInt
  exb.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, exb_src3_false.asUInt)
  /* branch unit interface */
  val beq_jump = operator.beq & (src1_data === src2_data)
  val bne_jump = operator.bne & (src1_data =/= src2_data)
  val blt_jump = operator.blt & (src1_data.asSInt() < src2_data.asSInt())
  val bge_jump = operator.bge & (src1_data.asSInt() >= src2_data.asSInt())
  val bltu_jump = operator.bltu & (src1_data < src2_data)
  val bgeu_jump = operator.bgeu & (src1_data >= src2_data)
  val branch = beq_jump | bne_jump | blt_jump | bge_jump | bltu_jump | bgeu_jump
  brb.brh  := branch         & io.next.ready //very important ！
  brb.jal  := operator.jal   & io.next.ready //very important ！
  brb.jalr := operator.jalr  & io.next.ready //very important ！
  brb.pc   := ifb.pc
  brb.src1 := exb.src1
  brb.src2 := exb.src2
  private val brb_imm_jal = Wire(SInt(64.W))
  brb_imm_jal := Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)).asSInt
  private val brb_imm_branch = Wire(SInt(64.W))
  brb_imm_branch := Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U).asSInt
  brb.imm  := MuxCase(0.U(64.W),
    Array(
      operator.jal -> brb_imm_jal.asUInt,
      branch -> brb_imm_branch.asUInt,
    )
  )
  /*
    fence
  */
  val fenceiing = RegInit(false.B)
  val wb_fencei = WireDefault(false.B)
  BoringUtils.addSink(wb_fencei, "wb_fencei")
  when  (wb_fencei & io.next.ready){
    fenceiing := false.B
  }.elsewhen(operator.fencei & io.next.ready){
    fenceiing := true.B
  }
  // icache control
  BoringUtils.addSource(fenceiing, "fencei")
  /*
    Interrupt and Exception
  */
  private val exce_flushing = RegInit(false.B)// This effect will delay 1 cycle
  /* default */
  csrb_in.intr := false.B
  csrb_in.exec := false.B
  csrb_in.pc := pc
  csrb_in.mret := false.B
  csrb_in.exce_code := 0.U
  when(io.prev.valid & io.next.ready){
    when(exce_flushing){
      csrb_in.exce_code := 0.U
    }.elsewhen(ctrl.io.operator.jal | ctrl.io.operator.jalr | branch){
      csrb_in.intr := false.B
      csrb_in.exec := false.B
      csrb_in.mret := false.B
    }.elsewhen(ctrl.io.operator.mret){
      csrb_in.mret := true.B
    }.elsewhen(ctrl.io.operator.ecall){
      csrb_in.exec := true.B
      csrb_in.exce_code := 11.U
    }.elsewhen(csrb_out.mie & csrb_out.msie & csrb_out.msip){
      csrb_in.intr := true.B
      csrb_in.exce_code := 3.U
    }.elsewhen(csrb_out.mie & csrb_out.mtie & csrb_out.mtip){
      csrb_in.intr := true.B
      csrb_in.exce_code := 7.U
    }.elsewhen(csrb_out.mie & csrb_out.meie & csrb_out.meip){
      csrb_in.intr := true.B
      csrb_in.exce_code := 11.U
    }
  }

  /* int exe jump */
  private val intr_exce_ret = ctrl.io.operator.mret | csrb_in.exec | csrb_in.intr
  csrb_in.is_iem := intr_exce_ret
  when(csrb_in.exec | csrb_in.intr){
    brb.src1  := csrb_out.mtvec
    brb.src2  := 0.U
    brb.jalr  := true.B
    brb.jal   := false.B
    brb.brh   := false.B
  }.elsewhen(ctrl.io.operator.mret){
    brb.src1  := csrb_out.mepc
    brb.src2  := 0.U
    brb.jalr  := true.B
    brb.jal   := false.B
    brb.brh   := false.B
  }

  /* int exe pipeline control */
  //lock intr_exce_ret and flushing
  wbb.intr_exce_ret := intr_exce_ret //transfer to wb
  private val wb_intr_exce_ret = Wire(Bool())
  wb_intr_exce_ret := false.B
  BoringUtils.addSink(wb_intr_exce_ret, "wb_intr_exce_ret")
  when  (wb_intr_exce_ret & io.next.ready){ exce_flushing := false.B }
  .elsewhen(intr_exce_ret & io.next.ready){ exce_flushing := true.B }
  // pipeline control
  when(!io.next.ready & !exce_flushing){
    io.prev.ready := io.next.ready
    io.next.valid := io.prev.valid
  }.elsewhen(exce_flushing){
    io.prev.ready := false.B
    io.next.valid := false.B
  }.elsewhen(intr_exce_ret){// mepc/mtvec --brb--> pcu
    io.prev.ready := io.next.ready
    io.next.valid := true.B
  }.elsewhen(wb_intr_exce_ret){//end
    io.prev.ready := true.B
    io.next.valid := false.B
  }.otherwise{
    io.prev.ready := io.next.ready
    io.next.valid := io.prev.valid
  }
  /*
   Test Interface
   */
  if(SparkConfig.Printf) { printf(p"curr_inst: ${Binary(inst)}\n") }
  if(!SparkConfig.Debug){
    fwb.test_pc := DontCare
  }else{
    fwb.test_pc := wbb.test_pc
  }
}

class IDUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val id2ex = new ID2EX
    val id2mem = new ID2MEM
    val id2wb = new ID2WB
  }
}

object IDU {
  def apply(prev: IFUOut, next: IDUOut, flush : Bool,
            fwu: IDFW, bru: IDBR, regfile: RegfileID,
            csr: CSRCtrlInf
           ): IDU ={
    val IF2IDReg = Module(new IDReg)
    when(flush) { IF2IDReg.reset := true.B }
    IF2IDReg.io.prev <> prev

    val idu = Module(new IDU)
    idu.io.fwu <> fwu
    idu.io.bru <> bru
    idu.io.regfile <> regfile
    idu.io.prev <> IF2IDReg.io.next
    idu.io.csr <> csr
    next <> idu.io.next

    idu.io.next.ready := next.ready & fwu.fw_ready
    next.valid := idu.io.next.valid & fwu.fw_ready

    idu
  }
}

