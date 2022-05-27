import chisel3._
import chisel3.util._
import chisel3.experimental._

class IDUInput extends Bundle{
  val inst = Input(UInt(32.W))
}

class ID2EX extends Bundle{
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val src3 = Output(UInt(64.W))
  val csig = new CtrlOutput
}



class IDU {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val regfile_rd = Flipped(new RegFileRD)
    val id2ex = new ID2EX()
    val id2mem = new Bundle() {
      val rd_en = Output(Bool())
      val we_en = Output(Bool())
    }
    val id2wb = new Bundle() {
      val we_en = Output(Bool())
    }
  })

  val inst = io.inst
  val ctrl = Module(new Controller)
  val csig = ctrl.io.out
  val optype = ctrl.io.out.optype
  val operator = ctrl.io.out.operator
  val is_load = ctrl.io.out.is_load
  val is_save = ctrl.io.out.is_save
  io.id2ex.csig := csig
  io.regfile_rd.en := true.B
  io.regfile_rd.addr1 := inst(19, 15)
  io.regfile_rd.addr2 := inst(24, 20)
  val reg_src1 = io.regfile_rd.data1
  val reg_src2 = io.regfile_rd.data2


  io.id2mem.we_en := is_save
  io.id2mem.rd_en := is_load

  io.id2wb.we_en := is_load


  io.id2ex.src1 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype |
        optype.Itype |
        optype.Btype |
        optype.Stype) -> reg_src1,
      optype.Utype -> Util.sext(Cat(inst(31, 12), Fill(12, 0.U)), pos = 32),
      optype.Jtype -> Util.sext(Cat(inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U), pos = 21),
    )
  )

  io.id2ex.src2 := MuxCase(default = 0.U(64.W),
    Array(
      ( optype.Rtype  |
        optype.Stype  |
        optype.Btype) -> reg_src2,
      optype.Itype -> Util.sext(inst(31, 20), pos = 12),
      optype.Jtype -> 0.U(64.W),
      optype.Utype -> 0.U(64.W),
    )
  )
  //Jump Inst
  io.id2ex.src3 := MuxCase(default = 0.U(64.W),
    Array(
      optype.Stype -> Util.sext(Cat(inst(31, 25), inst(11, 7)), pos = 12),
      optype.Btype -> Util.sext(Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U), pos = 13)
    )
  )
}



