import chisel3._
import chisel3.util._
import chisel3.experimental._

//object Optype extends ChiselEnum {
//  val R, I, S, B, U, J, N = Value
//}

class Optype extends Bundle {
  val AuipcU= Output(Bool())//U
  val LuiU  = Output(Bool())
  val JalJ  = Output(Bool())//J
  val JalrI = Output(Bool())//I
  val BrhB  = Output(Bool())//B
  val LoadI = Output(Bool())
  val SaveS = Output(Bool())//S
  val CalI  = Output(Bool())
  val CalR  = Output(Bool())//R
  val CalWR = Output(Bool())
  val CalWI = Output(Bool())

  val Btype = Output(Bool())
  val Jtype = Output(Bool())
  val Stype = Output(Bool())
  val Rtype = Output(Bool())
  val Utype = Output(Bool())
  val Itype = Output(Bool())
  val Ntype = Output(Bool())
}

class CtrlInput extends Bundle{
  val inst        =   Input (UInt(32.W))
}

class CtrlOutput() extends Bundle{
  val inst_type   =   Output(new Optype)
  val mem2reg_sel =   Output(Bool())
  val regfile_we  =   Output(Bool())
  val regfile_rd  =   Output(Bool())
  val exsrc_sel   =   Output(Bool())
  val alu_op      =   Output(UInt(4.W))
}

class Controller extends Module{
  val io = IO(new Bundle{
    val in = new CtrlInput
    val out = new CtrlOutput
  })
  val opcode = io.in.inst(6, 0)
  val fun3 = io.in.inst(14, 12)
  val fun7 = io.in.inst(25, 31)


  io.out.alu_op := Cat(fun7(5), fun3) |
                        Fill(4, Cat(fun7(6), ~fun7(4, 0).toBool))

  val inst_type = io.out.inst_type
  inst_type.AuipcU:= (opcode === "b0010111".U)//U
  inst_type.LuiU  := (opcode === "b0110111".U)
  inst_type.BrhB  := (opcode === "b1100011".U)//B
  inst_type.CalR  := (opcode === "b0110011".U)//R
  inst_type.CalWR := (opcode === "b0111011".U)
  inst_type.SaveS := (opcode === "b0100011".U)//S
  inst_type.JalJ  := (opcode === "b1101111".U)//J
  inst_type.JalrI := (opcode === "b1100111".U)//I
  inst_type.LoadI := (opcode === "b0000011".U)
  inst_type.CalI  := (opcode === "b0010011".U)
  inst_type.CalWI := (opcode === "b0011011".U)

  inst_type.Btype := inst_type.BrhB
  inst_type.Jtype := inst_type.JalJ
  inst_type.Stype := inst_type.SaveS
  inst_type.Rtype := inst_type.CalR   | inst_type.CalWR
  inst_type.Utype := inst_type.AuipcU | inst_type.LuiU
  inst_type.Itype := inst_type.CalWI  | inst_type.CalI | inst_type.LoadI | inst_type.JalrI
  inst_type.Ntype := ~((inst_type.Btype) | (inst_type.Jtype) | (inst_type.Stype) | (inst_type.Rtype) | (inst_type.Utype) || (inst_type.Itype))

  /* ctrl connection */
  // S
  io.out.mem2reg_sel := inst_type.SaveS
  // U , I , R , J
  io.out.regfile_we := inst_type.Utype | inst_type.Itype | inst_type.Rtype | inst_type.Jtype
  //
  io.out.regfile_rd := inst_type.Itype | inst_type.Rtype | inst_type.Stype | inst_type.Btype
  //
  io.out.inst_type := inst_type
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