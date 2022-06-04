import chisel3._


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
  val inst = io.inst
  val opcode = io.inst(6, 0)
  val fun3 = io.inst(14, 12)
  val fun7 = io.inst(31, 25)
  val fun6 = io.inst(31, 26)
  val operator = io.operator
  val optype = io.optype
  val srcsize = io.srcsize
  val is_load = io.is_load
  val is_save = io.is_save

  val fun3_000 = fun3 === "b000".U
  val fun3_001 = fun3 === "b001".U
  val fun3_010 = fun3 === "b010".U
  val fun3_011 = fun3 === "b011".U
  val fun3_100 = fun3 === "b100".U
  val fun3_101 = fun3 === "b101".U
  val fun3_110 = fun3 === "b110".U
  val fun3_111 = fun3 === "b111".U
  val fun7_0000000 = fun7 === "b0000000".U
  val fun7_0000001 = fun7 === "b0000001".U
  val fun7_0100000 = fun7 === "b0100000".U
  val fun6_000000 = fun6 === "b000000".U
  val fun6_010000 = fun6 === "b010000".U

  optype.Btype := (opcode === "b1100011".U)//B
  optype.Jtype := (opcode === "b1101111".U)//J
  optype.Stype := (opcode === "b0100011".U)//S
  optype.Rtype := (opcode === "b0110011".U) | (opcode === "b0111011".U)
  optype.Utype := (opcode === "b0010111".U) | (opcode === "b0110111".U)
  optype.Itype := (opcode === "b0000011".U) | (opcode === "b0010011".U) | (opcode === "b0011011".U) | (opcode === "b1100111".U)
  optype.Ntype := ~(optype.Btype | optype.Jtype | optype.Stype | optype.Rtype | optype.Utype | optype.Itype)

  operator.auipc := (opcode === "b0010111".U)
  operator.lui   := (opcode === "b0110111".U)
  operator.jal   := (opcode === "b1101111".U)
  operator.jalr  := (opcode === "b1100111".U) & fun3_000
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
  operator.ebreak := (inst === "b0000000_00001_00000_000_00000_1110011".U)

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
  operator.mulhu  := fun3_010 & fun7_0000001 & (calr32 | calr64)
  operator.mulhsu := fun3_011 & fun7_0000001 & (calr32 | calr64)
  operator.div    := fun3_100 & fun7_0000001 & (calr32 | calr64)
  operator.divu   := fun3_101 & fun7_0000001 & (calr32 | calr64)
  operator.rem    := fun3_110 & fun7_0000001 & (calr32 | calr64)
  operator.remu   := fun3_111 & fun7_0000001 & (calr32 | calr64)

  srcsize.byte  := operator.lb | operator.lbu | operator.sb
  srcsize.hword := operator.lh | operator.lhu | operator.sh
  srcsize.word  :=  operator.lw | operator.lwu | operator.sw | cali64 | calr64
  srcsize.dword := ~(srcsize.byte | srcsize.hword | srcsize.word)

}

