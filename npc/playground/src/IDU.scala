import chisel3._
import chisel3.util._


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
  io.prev.ready := io.next.ready
  io.next.valid := io.prev.valid
  brb.ready := io.next.ready
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
  memb.sext_flag := operator.lb | operator.lh  | operator.lw | operator.ld
  memb.size := srcsize
  memb.memory_we_en := is_save
  memb.memory_rd_en := is_load
  /* id2wb interface */
  wbb.wb_sel := is_load
  wbb.regfile_we_en := optype.Utype | optype.Itype | optype.Rtype | optype.Jtype
  wbb.regfile_we_addr := Mux(optype.Btype | optype.Stype, 0.U, inst(11, 7))
  wbb.test_pc := pc
  wbb.test_inst := inst
  /* id2ex interface */
  exb.operator := operator
  exb.optype   := optype
  exb.srcsize  := srcsize
  exb.is_load  := is_load
  exb.is_save  := is_save
  exb.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype |
        optype.Itype |
        optype.Btype |
        optype.Stype) -> src1_data,
      optype.Utype -> Sext(data = Cat(inst(31, 12), Fill(12, 0.U)), pos = 32)
    )
  )
  exb.src2 := MuxCase(default = 0.U(64.W),
    Array(
      (optype.Rtype | optype.Stype | optype.Btype) -> src2_data,
      (optype.Itype) -> Sext(data = Cat(inst(31, 20)), pos = 12)//Sext(data = inst(31, 20), pos = 12),
    )
  )
  //jalr or save addr
  exb.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, Sext(data = Cat(inst(31, 25), inst(11, 7)), pos = 12))
  /* branch unit interface */
  //io.id2pc.offset
  val beq_jump = operator.beq & (src1_data === src2_data)
  val bne_jump = operator.bne & (src1_data =/= src2_data)
  val blt_jump = operator.blt & (src1_data.asSInt() < src2_data.asSInt())
  val bge_jump = operator.bge & (src1_data.asSInt() >= src2_data.asSInt())
  val bltu_jump = operator.bltu & (src1_data < src2_data)
  val bgeu_jump = operator.bgeu & (src1_data >= src2_data)
  val branch = beq_jump | bne_jump | blt_jump | bge_jump | bltu_jump | bgeu_jump
  brb.brh  := branch
  brb.jal  := operator.jal
  brb.jalr := operator.jalr
  brb.pc   := ifb.pc
  brb.src1 := exb.src1
  brb.src2 := exb.src2
  brb.imm  := MuxCase(0.U(64.W),
    Array(
      operator.jal -> Sext(data = Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)), pos = 21),
      branch -> Sext(data = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
  /*
   Test Interface
   */
  if(SparkConfig.Printf) { printf(p"curr_inst: ${Binary(inst)}\n") }
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
           ): IDU ={
    val IF2IDReg = Module(new IDReg)
    when(flush) { IF2IDReg.reset := true.B }
    IF2IDReg.io.prev <> prev

    val idu = Module(new IDU)
    idu.io.fwu <> fwu
    idu.io.bru <> bru
    idu.io.regfile <> regfile
    idu.io.prev <> IF2IDReg.io.next
    next <> idu.io.next

    idu.io.next.ready := next.ready & fwu.fw_ready
    next.valid := idu.io.next.valid & fwu.fw_ready

    /* test */
    if(!SparkConfig.Debug){
      fwu.test_pc := DontCare
      fwu.test_pc := next.bits.id2wb.test_pc
    }else{
      fwu.test_pc := next.bits.id2wb.test_pc
    }

    idu
  }
}

//io.id2pc.is_jump  := b_jump | operator.jal
//io.id2pc.is_jumpr := operator.jalr
//io.id2pc.offset := MuxCase(0.U(64.W),
//  Array(
//    operator.jal -> Sext(data = Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)), pos = 21),
//    b_jump -> Sext(data = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
//  )
//)
//io.id2pc.jump_reg := Cat((io.id2ex.src1 + io.id2ex.src2)(63, 1), 0.U(1.W))(63, 0)


//class ID2EXReg extends Module{
//  val io = IO(new Bundle{
//    val stall =   Input(Bool())
//    val in    =   Flipped(new ID2EX)
//    val out   =   new ID2EX
//  })
//  val src1     = RegEnable(next = io.in.src1, init = 0.U(64.W), enable = !io.stall)
//  val src2     = RegEnable(next = io.in.src2, init = 0.U(64.W), enable = !io.stall)
//  val src3     = RegEnable(next = io.in.src3, init = 0.U(64.W), enable = !io.stall)
//  val operator = RegEnable(next = io.in.operator.asUInt(), init = 0.U, enable = !io.stall)
//  val optype   = RegEnable(next = io.in.optype.asUInt(), init = 0.U, enable = !io.stall)
//  val srcsize  = RegEnable(next = io.in.srcsize, enable = !io.stall)
//  val is_load  = RegEnable(next = io.in.is_load, init = 0.U, enable = !io.stall)
//  val is_save  = RegEnable(next = io.in.is_save, init = 0.U, enable = !io.stall)
//  io.out.src1     :=    src1
//  io.out.src2     :=    src2
//  io.out.src3     :=    src3
//  //  io.out.operator :=    operator.asTypeOf(Flipped(new Operator))
//  io.out.optype   :=    optype
//  io.out.srcsize  :=    srcsize
//  io.out.is_load  :=    is_load
//  io.out.is_save  :=    is_save
//}
