import chisel3._
import chisel3.util._

class ByPass extends Module{
  val io = IO(new Bundle{
    val is_load  = Input(Bool())
    val optype   = Flipped(new Optype)
    val id_data1 = Input(UInt(64.W))
    val id_addr1 = Input(UInt(5.W))
    val id_data2 = Input(UInt(64.W))
    val id_addr2 = Input(UInt(5.W))
    val ex_data  = Input(UInt(64.W))
    val ex_addr  = Input(UInt(5.W))
    val mem_data = Input(UInt(64.W))
    val mem_addr = Input(UInt(5.W))
    val wb_data  = Input(UInt(64.W))
    val wb_addr  = Input(UInt(5.W))

    val src1     = Output(UInt(64.W))
    val src1_addr= Input(UInt(5.W))
    val src2     = Output(UInt(64.W))
    val src2_addr= Input(UInt(5.W))
    val go       = Output(Bool())
  })
  val is_load    = io.is_load
  val optype     = io.optype
  val id_data1   = io.id_data1
  val id_addr1   = io.id_addr1
  val id_data2   = io.id_data2
  val id_addr2   = io.id_addr2
  val ex_data    = io.ex_data
  val ex_addr    = io.ex_addr
  val mem_data   = io.mem_data
  val mem_addr   = io.mem_addr
  val wb_data    = io.wb_data
  val wb_addr    = io.wb_addr

  val src1       = io.src1
  val src1_addr  = io.src1_addr
  val src2       = io.src2
  val src2_addr  = io.src2_addr
  val go         = io.go

  val nzero_1 = ex_addr  =/= 0.U
  val nzero_2 = mem_addr =/= 0.U
  val nzero_3 = wb_addr  =/= 0.U

  val eq1_1 = src1_addr === ex_addr & nzero_1
  val eq1_2 = src1_addr === mem_addr & nzero_2
  val eq1_3 = src1_addr === wb_addr & nzero_3

  val eq2_1 = src2_addr === ex_addr & nzero_1
  val eq2_2 = src2_addr === mem_addr & nzero_2
  val eq2_3 = src2_addr === wb_addr & nzero_3

  val eq1 = eq1_1 | eq1_2 | eq1_3
  val eq2 = eq2_1 | eq2_2 | eq2_3

  // 旁路条件 许可指令类型 只允许一边旁路
  val pass1 = (eq1_1 | (eq1_2 & !is_load) | eq1_3) & (optype.Rtype | optype.Itype)
  val pass2 = (eq2_1 | (eq2_2 & !is_load) | eq2_3) & (optype.Rtype | optype.Itype)
  src1 := MuxCase(id_data1,
    Array(
      (pass2) -> id_data1,
      eq1_
    )
  )

}
