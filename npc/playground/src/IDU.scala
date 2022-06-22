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
class IDReg extends Module{
  val io = IO(new Bundle() {
    val bubble = Input(Bool())
    val stall  = Input(Bool())
    val in = Flipped(new IFUOut)
    val out = new IFUOut
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
class IDUOut extends Bundle{
  val id2ex = new ID2EX
  val id2mem = new ID2MEM
  val id2wb = new ID2WB
}
class IDU extends Module {
  val io = IO(new Bundle {
    val in = Flipped(new IFUOut)
    val out = new IDUOut
    val fw2id = Flipped(new FW2ID)
    val id2fw = new ID2FW
    val id2br = new ID2BR
    val regfile2id = Flipped(new RegFileID)
  })
  // inst
  val fw2id      = io.fw2id
  val if2id      = io.in.if2id
  val regfile2id = io.regfile2id
  val id2fw  = io.id2fw
  val id2br  = io.id2br
  val id2ex  = io.out.id2ex
  val id2mem = io.out.id2mem
  val id2wb  = io.out.id2wb
  // printf("IDU\t\n")
  val inst = if2id.inst
  printf(p"${Binary(inst)}\n")
  val pc = if2id.pc
  /* controller instance */
  val ctrl = Module(new Controller)
  val operator = ctrl.io.operator
  val optype = ctrl.io.optype
  val srcsize = ctrl.io.srcsize
  val is_load = ctrl.io.is_load
  val is_save = ctrl.io.is_save
  ctrl.io.inst := inst
  /* regfile interface */
  regfile2id.en := true.B
  regfile2id.addr1 := inst(19, 15)
  regfile2id.addr2 := inst(24, 20)
  val reg_src1 = regfile2id.data1
  val reg_src2 = regfile2id.data2
  /* forwarding interface */
  val src1_data = fw2id.src1_data
  val src2_data = fw2id.src2_data
  id2fw.optype := optype
  id2fw.operator := operator
  id2fw.src1_data := reg_src1
  id2fw.src2_data := reg_src2
  id2fw.src1_addr := inst(19, 15)
  id2fw.src2_addr := inst(24, 20)
  /* id2mem interface */
  id2mem.sext_flag := operator.lb | operator.lh  | operator.lw | operator.ld
  id2mem.size := srcsize
  id2mem.memory_we_en := is_save
  id2mem.memory_rd_en := is_load
  /* id2wb interface */
  id2wb.wb_sel := is_load
  id2wb.regfile_we_en := optype.Utype | optype.Itype | optype.Rtype | optype.Jtype
  id2wb.regfile_we_addr := Mux(optype.Btype | optype.Stype, 0.U, inst(11, 7))
  id2wb.test_pc := pc
  id2wb.test_inst := inst
  /* id2ex interface */
  id2ex.operator := operator
  id2ex.optype   := optype
  id2ex.srcsize  := srcsize
  id2ex.is_load  := is_load
  id2ex.is_save  := is_save
  id2ex.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype |
        optype.Itype |
        optype.Btype |
        optype.Stype) -> src1_data,
      optype.Utype -> Sext(data = Cat(inst(31, 12), Fill(12, 0.U)), pos = 32)
    )
  )
  id2ex.src2 := MuxCase(default = 0.U(64.W),
    Array(
      (optype.Rtype | optype.Stype | optype.Btype) -> src2_data,
      (optype.Itype) -> Sext(data = Cat(inst(31, 20)), pos = 12)//Sext(data = inst(31, 20), pos = 12),
    )
  )
  //jalr or save addr
  id2ex.src3 := Mux(operator.jalr | optype.Jtype | optype.Utype, pc, Sext(data = Cat(inst(31, 25), inst(11, 7)), pos = 12))
  /* branch unit interface */
  //io.id2pc.offset
  val beq_jump = operator.beq & (src1_data === src2_data)
  val bne_jump = operator.bne & (src1_data =/= src2_data)
  val blt_jump = operator.blt & (src1_data.asSInt() < src2_data.asSInt())
  val bge_jump = operator.bge & (src1_data.asSInt() >= src2_data.asSInt())
  val bltu_jump = operator.bltu & (src1_data < src2_data)
  val bgeu_jump = operator.bgeu & (src1_data >= src2_data)
  val branch = beq_jump | bne_jump | blt_jump | bge_jump | bltu_jump | bgeu_jump
  id2br.brh  := branch
  id2br.jal  := operator.jal
  id2br.jalr := operator.jalr
  id2br.pc   := if2id.pc
  id2br.src1 := id2ex.src1
  id2br.src2 := id2ex.src2
  id2br.imm  := MuxCase(0.U(64.W),
    Array(
      operator.jal -> Sext(data = Cat(inst(31), inst(19, 12), inst(20), inst(30, 25), inst(24, 21), 0.U(1.W)), pos = 21),
      branch -> Sext(data = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
}
object IDU{
  def apply(in: IFUOut, fw2id: FW2ID,
            out: IDUOut,
            regfile2id: RegFileID): IDU = {
    val idu = Module(new IDU)
    idu.io.fw2id := Flipped(fw2id)
    idu.io.in := in
    out := idu.io.out
    idu.io.regfile2id <> Flipped(regfile2id)
    idu
  }
}
//val fw2id = Flipped(new FW2ID)
//val if2id = Flipped(new IF2ID)
//val regfile2id = Flipped(new RegFileID)
//val id2fw = new ID2FW
//val id2br = new ID2BR
//val id2ex = new ID2EX
//val id2mem = new ID2MEM
//val id2wb = new ID2WB