import chisel3._

class CSRType extends Bundle{
  val is_csr = Output(Bool())
  val csrrw = Output(Bool())
  val csrrs = Output(Bool())
  val csrrc = Output(Bool())
  val csrrwi= Output(Bool())
  val csrrsi= Output(Bool())
  val csrrci= Output(Bool())
}

class Operator extends Bundle {
  val auipc = Output(Bool())//U
  val lui   = Output(Bool())//U
  val jal   = Output(Bool())//J
  val jalr  = Output(Bool())//I
  val lb    = Output(Bool())//Load
  val lh    = Output(Bool())
  val lw    = Output(Bool())
  val lbu   = Output(Bool())
  val lhu   = Output(Bool())
  val ld    = Output(Bool())
  val lwu   = Output(Bool())
  val sb    = Output(Bool())//Save
  val sh    = Output(Bool())
  val sw    = Output(Bool())
  val sd    = Output(Bool())
  val beq   = Output(Bool())//B
  val bne   = Output(Bool())//B
  val blt   = Output(Bool())//B
  val bge   = Output(Bool())//B
  val bltu  = Output(Bool())//B
  val bgeu  = Output(Bool())//B
  val add   = Output(Bool())
  val sub   = Output(Bool())
  val sll   = Output(Bool())
  val slt   = Output(Bool())
  val sltu  = Output(Bool())
  val xor   = Output(Bool())
  val srl   = Output(Bool())
  val sra   = Output(Bool())
  val or    = Output(Bool())
  val and   = Output(Bool())
  val mul   = Output(Bool())
  val mulh  = Output(Bool())
  val mulhu = Output(Bool())
  val mulhsu= Output(Bool())
  val div   = Output(Bool())
  val divu  = Output(Bool())
  val rem   = Output(Bool())
  val remu  = Output(Bool())
  val ebreak= Output(Bool())
  val ecall = Output(Bool())
  val mret  = Output(Bool())
  val fencei= Output(Bool())
  val csr   = new CSRType
}

class Optype extends Bundle{
  val Btype = Output(Bool())
  val Jtype = Output(Bool())
  val Stype = Output(Bool())
  val Rtype = Output(Bool())
  val Utype = Output(Bool())
  val Itype = Output(Bool())
  val Ntype = Output(Bool())
}

class SrcSize extends Bundle {
  val byte  = Output(Bool())
  val hword = Output(Bool())
  val word  = Output(Bool())
  val dword = Output(Bool())
}

class Controller extends Module{
  val io = IO(new Bundle{
    val inst        =   Input (UInt(32.W))
    val operator    =   Output(new Operator)
    val optype      =   Output(new Optype)
    val srcsize     =   Output(new SrcSize)
    val is_load     =   Output(Bool())
    val is_save     =   Output(Bool())
  })
  private val inst = io.inst
  private val opcode = io.inst(6, 0)
  private val fun3 = io.inst(14, 12)
  private val fun7 = io.inst(31, 25)
  private val fun6 = io.inst(31, 26)
  private val operator = io.operator
  private val optype = io.optype
  private val srcsize = io.srcsize
  private val is_load = io.is_load
  private val is_save = io.is_save

  private val fun3_000 = fun3 === "b000".U
  private val fun3_001 = fun3 === "b001".U
  private val fun3_010 = fun3 === "b010".U
  private val fun3_011 = fun3 === "b011".U
  private val fun3_100 = fun3 === "b100".U
  private val fun3_101 = fun3 === "b101".U
  private val fun3_110 = fun3 === "b110".U
  private val fun3_111 = fun3 === "b111".U
  private val fun7_0000000 = fun7 === "b0000000".U
  private val fun7_0000001 = fun7 === "b0000001".U
  private val fun7_0100000 = fun7 === "b0100000".U
  private val fun6_000000 = fun6 === "b000000".U
  private val fun6_010000 = fun6 === "b010000".U

  /* optype */
  optype.Btype := (opcode === "b1100011".U)//B
  optype.Jtype := (opcode === "b1101111".U)//J
  optype.Stype := (opcode === "b0100011".U)//S
  optype.Rtype := (opcode === "b0110011".U) | (opcode === "b0111011".U)
  optype.Utype := (opcode === "b0010111".U) | (opcode === "b0110111".U)
  optype.Itype := (opcode === "b0000011".U) | (opcode === "b0010011".U) | (opcode === "b0011011".U) | (opcode === "b1100111".U)
  optype.Ntype := ~(optype.Btype | optype.Jtype | optype.Stype | optype.Rtype | optype.Utype | optype.Itype)
  /* pc operation inst */
  operator.auipc := (opcode === "b0010111".U)
  operator.lui   := (opcode === "b0110111".U)
  operator.jal   := (opcode === "b1101111".U)
  operator.jalr  := (opcode === "b1100111".U) & fun3_000
  /* memory access inst */
  is_load := (opcode === "b0000011".U)
  operator.lb  := fun3_000 & is_load
  operator.lh  := fun3_001 & is_load
  operator.lw  := fun3_010 & is_load
  operator.lbu := fun3_100 & is_load
  operator.lhu := fun3_101 & is_load
  operator.ld  := fun3_011 & is_load
  operator.lwu := fun3_110 & is_load
  is_save := (opcode === "b0100011".U)//S
  operator.sb  := fun3_000 & is_save
  operator.sh  := fun3_001 & is_save
  operator.sw  := fun3_010 & is_save
  operator.sd  := fun3_011 & is_save
  operator.beq := fun3_000 & (optype.Btype)
  operator.bne := fun3_001 & (optype.Btype)
  operator.blt := fun3_100 & (optype.Btype)
  operator.bge := fun3_101 & (optype.Btype)
  operator.bltu:= fun3_110 & (optype.Btype)
  operator.bgeu:= fun3_111 & (optype.Btype)
  /* cal inst */
  private val cali32 = opcode === "b0010011".U
  private val calr32 = opcode === "b0110011".U
  private val cali64 = opcode === "b0011011".U
  private val calr64 = opcode === "b0111011".U
  operator.add    := fun3_000 & (cali32 | (calr32 & fun7_0000000)  | cali64 | (calr64 & fun7_0000000))//caln
  operator.sub    := fun3_000 & ((calr32 & fun7_0100000) | (calr64 & fun7_0100000))//cals
  operator.sll    := fun3_001 & ((cali32 & fun6_000000) | (calr32 & fun7_0000000)  | (cali64 & fun7_0000000) | (calr64 & fun7_0000000))
  operator.slt    := fun3_010 & (cali32 | (calr32 & fun7_0000000))//cal32n
  operator.sltu   := fun3_011 & (cali32 | (calr32 & fun7_0000000))//cal32n
  operator.xor    := fun3_100 & (cali32 | (calr32 & fun7_0000000))//cal32n
  operator.srl    := fun3_101 &  ((cali32 & fun6_000000) | (calr32 & fun7_0000000)  | (cali64 & fun7_0000000) | (calr64 & fun7_0000000))
  operator.sra    := fun3_101 & ((cali32 & fun6_010000) | (calr32 & fun7_0100000)  | (cali64 & fun7_0100000) | (calr64 & fun7_0100000))
  operator.or     := fun3_110 & (cali32 | (calr32 & fun7_0000000))//cal32n
  operator.and    := fun3_111 & (cali32 | (calr32 & fun7_0000000))//cal32n
  operator.mul    := fun3_000 & fun7_0000001 & (calr32 | calr64)
  operator.mulh   := fun3_001 & fun7_0000001 & (calr32 | calr64)
  operator.mulhsu := fun3_010 & fun7_0000001 & (calr32 | calr64)
  operator.mulhu  := fun3_011 & fun7_0000001 & (calr32 | calr64)
  operator.div    := fun3_100 & fun7_0000001 & (calr32 | calr64)
  operator.divu   := fun3_101 & fun7_0000001 & (calr32 | calr64)
  operator.rem    := fun3_110 & fun7_0000001 & (calr32 | calr64)
  operator.remu   := fun3_111 & fun7_0000001 & (calr32 | calr64)
  /* csr inst */
  private val fun7_1110011 = opcode === "b1110011".U
  operator.csr.csrrw  := fun3_001 & fun7_1110011
  operator.csr.csrrs  := fun3_010 & fun7_1110011
  operator.csr.csrrc  := fun3_011 & fun7_1110011
  operator.csr.csrrwi := fun3_101 & fun7_1110011
  operator.csr.csrrsi := fun3_110 & fun7_1110011
  operator.csr.csrrci := fun3_111 & fun7_1110011
  operator.csr.is_csr := fun7_1110011 & (fun3_001 | fun3_010 | fun3_011 | fun3_101 | fun3_110 | fun3_111)
  /* environment call and breakpoints */
  private val zero_19_7 =  inst(19, 7) === 0.U(13.W)
  operator.ecall  := (inst(31, 20) === 0.U(12.W)) & zero_19_7 & fun7_1110011//(inst === "b0000000_00000_00000_000_00000_1110011".U)
  operator.ebreak := (inst(31, 20) === 1.U(12.W)) & zero_19_7 & fun7_1110011//(inst === "b0000000_00001_00000_000_00000_1110011".U)
  /*  trap-return inst */
  operator.mret   := (inst(31, 20) === "b0011000_00010".U) & zero_19_7 & fun7_1110011
  /* fence */
  operator.fencei := inst === "b000000000000_00000_001_00000_0001111".U

  srcsize.byte  := operator.lb | operator.lbu | operator.sb
  srcsize.hword := operator.lh | operator.lhu | operator.sh
  srcsize.word  :=  operator.lw | operator.lwu | operator.sw | cali64 | calr64
  srcsize.dword := ~(srcsize.byte | srcsize.hword | srcsize.word)

}

