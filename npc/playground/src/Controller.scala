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

class CtrlInput extends Bundle{
  val inst        =   Input (UInt(32.W))
}

class CtrlOutput() extends Bundle{
  val operator    =   Output(new Operator)
  val optype      =   Output(new Optype)
  val srcsize     =   Output(new SrcSize)
  val is_load     =   Output(Bool())
  val is_save     =   Output(Bool())
}

class Controller extends Module{
  val io = IO(new Bundle{
    val in = new CtrlInput
    val out = new CtrlOutput
  })
  val opcode = io.in.inst(6, 0)
  val fun3 = io.in.inst(14, 12)
  val fun7 = io.in.inst(25, 31)
  val operator = io.out.operator
  val optype = io.out.optype
  val srcsize = io.out.srcsize
  val is_load = io.out.is_load
  val is_save = io.out.is_save

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
  operator.jalr  := (opcode === "b1100111".U) | (fun3 === "b000".U)
  io.out.is_load := (opcode === "b0000011".U)
  operator.lb  := (fun3 === "b000".U) & is_load
  operator.lh  := (fun3 === "b001".U) & is_load
  operator.lw  := (fun3 === "b010".U) & is_load
  operator.lbu := (fun3 === "b100".U) & is_load
  operator.lhu := (fun3 === "b101".U) & is_load
  operator.ld  := (fun3 === "b011".U) & is_load
  operator.lwu := (fun3 === "b110".U) & is_load
  io.out.is_save := (opcode === "b0100011".U)//S
  operator.sb  := (fun3 === "b000".U) & is_save
  operator.sh  := (fun3 === "b001".U) & is_save
  operator.sw  := (fun3 === "b010".U) & is_save
  operator.sd  := (fun3 === "b011".U) & is_save
  operator.beq  := optype.Btype & (fun3 === "000".U)
  operator.bne  := optype.Btype & (fun3 === "001".U)
  operator.blt  := optype.Btype & (fun3 === "100".U)
  operator.bge  := optype.Btype & (fun3 === "101".U)
  operator.bltu := optype.Btype & (fun3 === "110".U)
  operator.bgeu := optype.Btype & (fun3 === "111".U)
  private val is_cal  = (opcode === "b0110011".U) | (opcode === "b0111011".U) | (opcode === "b0010011".U) | (opcode === "b0011011".U)
  private val is_mcal = is_cal & (fun7 === "b0000001".U)
  private val is_sub_sra  = is_cal & (fun7 === "b0100000".U)
  operator.add    := (fun3 === "b000".U) & is_cal
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

  srcsize.byte  := operator.lb | operator.lbu | operator.sb
  srcsize.hword := operator.lh | operator.lhu | operator.sh
  srcsize.word  :=  operator.lw | operator.lwu | operator.sw |
    (is_mcal & opcode === "b0111011".U) | (is_cal & (opcode ==="b0011011".U))
  srcsize.dword := ~(srcsize.byte | srcsize.hword | srcsize.word)

}



//  val auipc = (opcode === "0010111".U)
//  val lui = (opcode === "0110111".U)
//
//  val jal = Jtype
//  val jalr = (opcode === "1100111".U) && (fun3 === "000".U)
//
//  val beq = Btype & (fun3 === "000".U)
//  val bne = Btype & (fun3 === "001".U)
//  val blt = Btype & (fun3 === "100".U)
//  val bge = Btype & (fun3 === "101".U)
//  val bltu = Btype & (fun3 === "110".U)
//  val bgeu = Btype & (fun3 === "111".U)
//
//  val lb = (opcode === "0000011".U) & (fun3 === "000".U)
//  val lb = (opcode === "0000011".U) & (fun3 === "001".U)
//  val lb = (opcode === "0000011".U) & (fun3 === "010".U)
//  val lb = (opcode === "0000011".U) & (fun3 === "010".U)




//  val optype = MuxCase(default = Optype.N,
//    Seq(
//      (opcode ===  "0000011".U) -> Optype.I,
//      (opcode ===  "0010011".U) -> Optype.I,
//      (opcode ===  "0011011".U) -> Optype.I,
//      (opcode ===  "1100111".U) -> Optype.I,
//      (opcode ===  "0110011".U) -> Optype.R,
//      (opcode ===  "0111011".U) -> Optype.R,
//      (opcode ===  "0010111".U) -> Optype.U,
//      (opcode ===  "0110111".U) -> Optype.U,
//      (opcode ===  "1101111".U) -> Optype.J,
//      (opcode ===  "1100011".U) -> Optype.B,
//      (opcode ===  "0100011".U) -> Optype.S,
//    )
//  )
//val Btype = (opcode === "1100011".U)
//val Jtype = (opcode === "1101111".U)
//val Stype = (opcode === "0100011".U)
//val Rtype = (opcode === "0110011".U) || (opcode === "0110011".U)
//val Utype = (opcode === "0010111".U) || (opcode === "0110111".U)
//val Itype = (opcode === "0000011".U) || (opcode === "0010011".U) || (opcode === "0011011".U) || (opcode === "1100111".U)
//val Ntype = ~((Btype) || (Jtype) || (Stype) || (Rtype) || (Utype) || (Itype))
//
//val auipc = (opcode ===  "0010111".U)
//val lui   = (opcode ===  "0110111".U)
//val jal   = (opcode ===  "1101111".U)
//val jalr  = (opcode ===  "1100111".U) && (fun3 === "000".U)
//val beq   = (optype === Optype.B) & (fun3 === "000")
//val bne   = (optype === Optype.B) & (fun3 === "001")
//val blt   = (optype === Optype.B) & (fun3 === "100")
//val bge   = (optype === Optype.B) & (fun3 === "000")
//
//io.inst_opcode_o := opcode
//io.inst_format_o := optype
//io.regfile_wen_o := true.B
//io.m2r_ctrl_o := optype === Opcode.S


//val optype = Wire(new Bundle {
//  val AuipcU= (opcode === "b0010111".U)//U
//  val LuiU  = (opcode === "b0110111".U)
//  val BrhB  = (opcode === "b1100011".U)//B
//  val CalR  = (opcode === "b0110011".U)//R
//  val CalWR = (opcode === "b0111011".U)
//  val SaveS = (opcode === "b0100011".U)//S
//  val JalJ =  (opcode === "b1101111".U)//J
//  val JalrI = (opcode === "b1100111".U)//I
//  val LoadI = (opcode === "b0000011".U)
//  val CalI  = (opcode === "b0010011".U)
//  val CalWI = (opcode === "b0011011".U)
//  val Btype = BrhB
//  val Jtype = JalJ
//  val Stype = SaveS
//  val Rtype = CalR || CalWR
//  val Utype = AuipcU || LuiU
//  val Itype = CalWI || CalI || LoadI || JalrI
//  val Ntype = ~((Btype) || (Jtype) || (Stype) || (Rtype) || (Utype) || (Itype))