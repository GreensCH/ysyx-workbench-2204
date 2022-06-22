import chisel3._
import chisel3.util._

class ID2BR extends Bundle{
  val brh  = Output(Bool())
  val jal  = Output(Bool())
  val jalr = Output(Bool())
  val pc   = Output(UInt(64.W))
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val imm  = Output(UInt(64.W))
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
}
class ID2WB extends Bundle{
  val test_pc       = Output(UInt(64.W))
  val test_inst     = Output(UInt(32.W))
  val wb_sel        = Output(Bool())
  val regfile_we_en = Output(Bool())
  val regfile_we_addr = Output(UInt(5.W))
}
//////////////////////////////////////
class IDRegIO extends Bundle{
  val if2id = new IF2ID
}
class IDReg extends Module{
  val io = IO(new Bundle() {
    val bubble = Input(Bool())
    val stall  = Input(Bool())
    val in = Flipped(new IDRegIO)
    val out = new IDRegIO
  })
  // pipeline control
  val bubble = io.bubble
  val stall = io.stall
  // data transfer
  val nop = Wire(new IF2ID)
  nop.inst := "h00000013".U(32.W)
  nop.pc := 0.U(64.W)
  val if2id = Mux(bubble, nop, io.in.if2id)
  val reg_if2id = RegEnable(next = if2id, enable = !stall)

  io.out.if2id  :=  reg_if2id
}
//////////////////////////////////////
class IDU extends Module {
  val io = IO(new Bundle {
    val fw2id = Flipped(new FW2ID)
    val if2id = Flipped(new IF2ID)
    val regfile2id = Flipped(new RegFileID)
    val id2fw = new ID2FW
    val id2br = new ID2BR
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
  /* forwarding interface */
  val src1_data = io.fw2id.src1_data
  val src2_data = io.fw2id.src2_data
  io.id2fw.optype := optype
  io.id2fw.operator := operator
  io.id2fw.src1_data := reg_src1
  io.id2fw.src2_data := reg_src2
  io.id2fw.src1_addr := inst(19, 15)
  io.id2fw.src2_addr := inst(24, 20)
  /* id2mem interface */
  io.id2mem.sext_flag := operator.lb | operator.lh  | operator.lw | operator.ld
  io.id2mem.size := srcsize
  io.id2mem.memory_we_en := is_save
  io.id2mem.memory_rd_en := is_load
  /* id2wb interface */
  io.id2wb.wb_sel := is_load
  io.id2wb.regfile_we_en := optype.Utype | optype.Itype | optype.Rtype | optype.Jtype
  io.id2wb.regfile_we_addr := Mux(optype.Btype | optype.Stype, 0.U, inst(11, 7))
  io.id2wb.test_pc := pc
  io.id2wb.test_inst := inst
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
        optype.Stype) -> src1_data,
      optype.Utype -> Sext(data = Cat(inst(31, 12), Fill(12, 0.U)), pos = 32)
    )
  )
  io.id2ex.src2 := MuxCase(default = 0.U(64.W),
    Array(
      (optype.Rtype | optype.Stype | optype.Btype) -> src2_data,
      (optype.Itype) -> Sext(data = Cat(inst(31, 20)), pos = 12)//Sext(data = inst(31, 20), pos = 12),
    )
  )
  //jalr or save addr
  io.id2ex.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, Sext(data = Cat(inst(31, 25), inst(11, 7)), pos = 12))
  /* branch unit interface */
  //io.id2pc.offset
  val beq_jump = operator.beq & (src1_data === src2_data)
  val bne_jump = operator.bne & (src1_data =/= src2_data)
  val blt_jump = operator.blt & (src1_data.asSInt() < src2_data.asSInt())
  val bge_jump = operator.bge & (src1_data.asSInt() >= src2_data.asSInt())
  val bltu_jump = operator.bltu & (src1_data < src2_data)
  val bgeu_jump = operator.bgeu & (src1_data >= src2_data)
  val branch = beq_jump | bne_jump | blt_jump | bge_jump | bltu_jump | bgeu_jump
  io.id2br.brh := branch
  io.id2br.jal := operator.jal
  io.id2br.jalr := operator.jalr
  io.id2br.pc  := io.if2id.pc
  io.id2br.src1 := io.id2ex.src1
  io.id2br.src2 := io.id2ex.src2
  io.id2br.imm  := MuxCase(0.U(64.W),
    Array(
      operator.jal -> Sext(data = Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)), pos = 21),
      branch -> Sext(data = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
}
class IDUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val fw2id = Flipped(new FW2ID)
    val if2id = Flipped(new IF2ID).bits
    val regfile2id = Flipped(new RegFileID)
    val id2fw = new ID2FW
    val id2br = new ID2BR
    val id2ex = new ID2EX
    val id2mem = new ID2MEM
    val id2wb = new ID2WB
  }
}
//object IDU {
//  def apply(prev: IF2ID, next: ID2EX): IFU ={
//    val pc = Module(new PC)
//    val ifu = Module(new IFU)
//
//    val pc2if = pc.io.pc2if.bits// pc out
//    val pcVld = pc.io.pc2if.valid// pc(reg) decouple
//    val pcRdy = pc.io.pc2if.ready
//    /** PC(Reg) Connection */
//    pc.io.br2pc := in // pc in
//    pcRdy := next.ready// decouple connection
//    next.valid := pcVld
//    /** IFU(Logic) Connection */
//    ifu.io.pc2if := pc2if// ifu in
//    next.bits := ifu.io.if2id
//    /** Return */
//    ifu
//  }
//}

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
