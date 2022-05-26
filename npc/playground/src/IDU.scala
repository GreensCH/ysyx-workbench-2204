import chisel3._
import chisel3.util._
import chisel3.experimental._

class IDUInput extends Bundle{
  val inst = Input(UInt(32.W))
  val regfile_wdata = Input(UInt(64.W))
}

class IDUOutput extends Bundle{
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val src3 = Output(UInt(64.W))
  val csig = new CtrlOutput
}


class IDU {
  val io = IO(new Bundle {
     val in = new IDUInput
     val out = new IDUOutput
  })

  val inst = io.in.inst
  val ctrl = Module(new Controller).io
  val gpr = Module(new RegFile).io

  io.out.csig := ctrl.out

  gpr.rd_en := ctrl.out.regfile_rd
  gpr.rd_addr1 := io.in.inst(19, 15)
  gpr.rd_addr2 := io.in.inst(24, 20)
  gpr.we_en := ctrl.out.regfile_we
  gpr.we_addr := io.in.inst(11, 7)
  gpr.we_data := io.in.regfile_wdata

  val inst_fmt = ctrl.out.inst_type
  io.out.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( inst_fmt.Rtype |
        inst_fmt.Itype |
        inst_fmt.Btype |
        inst_fmt.Stype) -> gpr.rd_data1,
        inst_fmt.Utype -> Util.sext(Cat(inst(31, 12), Fill(12, 0.U)), pos = 32),
        inst_fmt.Jtype -> Util.sext(Cat(inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U), pos = 21),
    )
  )

  io.out.src2 := MuxCase(default = 0.U(64.W),
    Array(
      ( inst_fmt.Rtype  |
        inst_fmt.Stype  |
        inst_fmt.Btype) -> gpr.rd_data2,
        inst_fmt.Itype -> Util.sext(inst(31, 20), pos = 12),
        inst_fmt.Jtype -> 0.U(64.W),
        inst_fmt.Utype -> 0.U(64.W),
    )
  )

  io.out.src3 := MuxCase(default = 0.U(64.W),
    Array(
        inst_fmt.Stype -> Util.sext(Cat(inst(31, 25), inst(11, 7)), pos = 12),
        inst_fmt.Btype -> Util.sext(Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
}


//  io.src3_o := MuxCase(default = 0.U(64.W),
//    Array(
//      ctrl.inst_type_o.Rtype -> 0.U(64.W),
//      ctrl.inst_type_o.Stype -> 0.U(64.W),
//      ctrl.inst_type_o.Itype -> 0.U(64.W),
//      ctrl.inst_type_o.Btype -> 0.U(64.W),
//      ctrl.inst_type_o.Jtype -> 0.U(64.W),
//      ctrl.inst_type_o.Utype -> 0.U(64.W),
//    )
//  )


