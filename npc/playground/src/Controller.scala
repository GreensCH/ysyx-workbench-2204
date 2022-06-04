import chisel3._
import chisel3.util._
import chisel3.experimental._


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
  val operator = io.operator
  val optype = io.optype
  val srcsize = io.srcsize
  val is_load = io.is_load
  val is_save = io.is_save

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
  operator.jalr  := (opcode === "b1100111".U) & (fun3 === "b000".U)
  is_load := (opcode === "b0000011".U)
  operator.lb  := (fun3 === "b000".U) & is_load
  operator.lh  := (fun3 === "b001".U) & is_load
  operator.lw  := (fun3 === "b010".U) & is_load
  operator.lbu := (fun3 === "b100".U) & is_load
  operator.lhu := (fun3 === "b101".U) & is_load
  operator.ld  := (fun3 === "b011".U) & is_load
  operator.lwu := (fun3 === "b110".U) & is_load
  is_save := (opcode === "b0100011".U)//S
  operator.sb  := (fun3 === "b000".U) & is_save
  operator.sh  := (fun3 === "b001".U) & is_save
  operator.sw  := (fun3 === "b010".U) & is_save
  operator.sd  := (fun3 === "b011".U) & is_save
  operator.beq := (fun3 === "b000".U) & (optype.Btype)
  operator.bne := (fun3 === "b001".U) & (optype.Btype)
  operator.blt := (fun3 === "b100".U) & (optype.Btype)
  operator.bge := (fun3 === "b101".U) & (optype.Btype)
  operator.bltu:= (fun3 === "b110".U) & (optype.Btype)
  operator.bgeu:= (fun3 === "b111".U) & (optype.Btype)
  private val is_cal  = (opcode === "b0110011".U) | (opcode === "b0111011".U) | (opcode === "b0010011".U) | (opcode === "b0011011".U)
  private val is_mcal = is_cal & (fun7 === "b0000001".U)
  private val is_sub_sra  = is_cal & (fun7 === "b0100000".U)
  operator.add    := (fun3 === "b000".U) & MuxCase(false.B,
    Array(
      (optype.Rtype) -> (opcode === "b0010011".U | opcode === "b0011011".U) ,
      (optype.Itype) -> ((fun7 === "b0000000".U)&(opcode === "b0110011".U | opcode === "b0111011".U))
    )
  )
  operator.sub    := (fun3 === "b000".U) & is_sub_sra
  operator.sll    := (fun3 === "b001".U) & is_cal
  operator.slt    := (fun3 === "b010".U) & is_cal
  operator.sltu   := (fun3 === "b011".U) & is_cal
  operator.xor    := (fun3 === "b100".U) & is_cal
  operator.srl    := (fun3 === "b101".U) & is_cal
  operator.sra    := (fun3 === "b101".U) & is_sub_sra
  operator.or     := (fun3 === "b110".U) & is_cal
  operator.and    := (fun3 === "b111".U) & is_cal
  operator.mul    := (fun3 === "b000".U) & is_mcal
  operator.mulh   := (fun3 === "b001".U) & is_mcal
  operator.mulhu  := (fun3 === "b010".U) & is_mcal
  operator.mulhsu := (fun3 === "b011".U) & is_mcal
  operator.div    := (fun3 === "b100".U) & is_mcal
  operator.divu   := (fun3 === "b101".U) & is_mcal
  operator.rem    := (fun3 === "b110".U) & is_mcal
  operator.remu   := (fun3 === "b111".U) & is_mcal
  operator.ebreak := (inst === "b0000000_00001_00000_000_00000_1110011".U)

  srcsize.byte  := operator.lb | operator.lbu | operator.sb
  srcsize.hword := operator.lh | operator.lhu | operator.sh
  srcsize.word  :=  operator.lw | operator.lwu | operator.sw |
    (is_cal & (opcode === "b0111011".U)) | (is_cal & (opcode ==="b0011011".U))
  srcsize.dword := ~(srcsize.byte | srcsize.hword | srcsize.word)

}

