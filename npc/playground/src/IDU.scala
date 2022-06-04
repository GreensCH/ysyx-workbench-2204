import chisel3._
import chisel3.util._


class ID2PC extends Bundle{
  val offset   = Output(UInt(64.W))
  val is_jump  = Output(Bool())
  val is_jumpr = Output(Bool())
  val jump_reg = Output(UInt(64.W))
}

class ID2EX extends Bundle{
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val src3 = Output(UInt(64.W))
  val operator    =   new Operator
  val optype      =   new Optype
  val srcsize     =   new SrcSize
  val is_load     =   Output(Bool())
  val is_save     =   Output(Bool())
}

class ID2MEM extends Bundle{
  val size      = new SrcSize
  val sext_flag    = Output(Bool())
  val memory_rd_en = Output(Bool())
  val memory_we_en = Output(Bool())
  val operator    =   new Operator
}

class ID2WB extends Bundle{
  val wb_sel        = Output(Bool())
  val regfile_we_en = Output(Bool())
  val regfile_we_addr = Output(UInt(5.W))
}

class IDU extends Module {
  val io = IO(new Bundle {
    val if2id = Flipped(new IF2ID)
    val regfile2id = Flipped(new RegFileID)
    val id2pc = new ID2PC
    val id2ex = new ID2EX
    val id2mem = new ID2MEM
    val id2wb = new ID2WB
  })
  //  printf("IDU\t\n")
  val inst = io.if2id.inst
  printf(p"${Binary(inst)}\n")
  val pc = io.if2id.pc
  /* controller instance */
  val ctrl = Module(new Controller)
  val operator = ctrl.io.operator
  val optype = ctrl.io.optype
  val srcsize = ctrl.io.srcsize
  val is_load = ctrl.io.is_load
  val is_save = ctrl.io.is_save
  ctrl.io.inst := inst
  /* regfile interface */
  io.regfile2id.en := true.B
  io.regfile2id.addr1 := inst(19, 15)
  io.regfile2id.addr2 := inst(24, 20)
  val reg_src1 = io.regfile2id.data1
  val reg_src2 = io.regfile2id.data2
  /* id2mem interface */
  io.id2mem.operator := operator
  io.id2mem.sext_flag := operator.lb | operator.lh  | operator.lw | operator.ld
  io.id2mem.size := srcsize
  io.id2mem.memory_we_en := is_save
  io.id2mem.memory_rd_en := is_load
  /* id2wb interface */
  io.id2wb.wb_sel := is_load
  io.id2wb.regfile_we_en := optype.Utype | optype.Itype | optype.Rtype | optype.Jtype
  io.id2wb.regfile_we_addr := inst(11, 7)
  /* id2ex interface */
  io.id2ex.operator := operator
  io.id2ex.optype   := optype
  io.id2ex.srcsize  := srcsize
  io.id2ex.is_load  := is_load
  io.id2ex.is_save  := is_save
  io.id2ex.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype |
        optype.Itype |
        optype.Btype |
        optype.Stype) -> reg_src1,
      optype.Utype -> Sext(data = Cat(inst(31, 12), Fill(12, 0.U)), pos = 32)
    )
  )
  io.id2ex.src2 := MuxCase(default = 0.U(64.W),
    Array(
      (optype.Rtype | optype.Stype | optype.Btype) -> reg_src2,
      (optype.Itype) -> Sext(data = Cat(inst(31, 20)), pos = 12)//Sext(data = inst(31, 20), pos = 12),
    )
  )
  //jalr or save addr
  io.id2ex.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, Sext(data = Cat(inst(31, 25), inst(11, 7)), pos = 12))
  //  io.id2ex.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, Sext(data = Cat(inst(31, 20)), pos = 12))
  //Sext(data = Cat(inst(31, 25), inst(11, 7)), pos = 12)

  /* npc generator */
  //io.id2pc.offset
  val beq_jump = operator.beq & (reg_src1 === reg_src2)
  val bne_jump = operator.bne & (reg_src1 =/= reg_src2)
  val blt_jump = operator.blt & (reg_src1.asSInt() < reg_src2.asSInt())
  val bge_jump = operator.bge & (reg_src1.asSInt() >= reg_src2.asSInt())
  val bltu_jump = operator.bltu & (reg_src1 < reg_src2)
  val bgeu_jump = operator.bgeu & (reg_src1 >= reg_src2)
  val b_jump = beq_jump | bne_jump | blt_jump | bge_jump | bltu_jump | bgeu_jump
  io.id2pc.is_jump  := b_jump | operator.jal
  io.id2pc.is_jumpr := operator.jalr
  io.id2pc.offset := MuxCase(0.U(64.W),
    Array(
      operator.jal -> Sext(data = Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)), pos = 21),
      b_jump -> Sext(data = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
  io.id2pc.jump_reg := Cat((io.id2ex.src1 + io.id2ex.src2)(63, 1), 0.U(1.W))(63, 0)
}



