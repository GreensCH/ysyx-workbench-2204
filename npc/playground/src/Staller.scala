import chisel3._
import chisel3.util._

class Staller extends Module{
  val io = IO(new Bundle{
    val addr1    =   Input (UInt(5.W))
    val addr2    =   Input (UInt(5.W))
    val ex_dst  =   Input (UInt(5.W))
    val mem_dst =   Input (UInt(5.W))
    val wb_dst  =   Input (UInt(5.W))
    val optype  =   Flipped(new Optype)
    val operator=   Flipped(new Operator)
    val stall   =   Output(Bool())
  })
  /* interface */
  val addr1   =   io.addr1
  val addr2   =   io.addr2
  val ex_dst  =   io.ex_dst
  val mem_dst =   io.mem_dst
  val wb_dst  =   io.wb_dst
  val optype  =   io.optype
  val operator=   io.operator

  val eq1 = (addr1 =/= 0.U | addr2 =/= 0.U ) & ((addr1 === ex_dst & ex_dst =/= 0.U) | (addr1 === mem_dst & mem_dst =/= 0.U) | (addr1 === wb_dst & wb_dst =/= 0.U)) & (optype.Itype | optype.Rtype | optype.Btype | optype.Jtype | optype.Stype)
  val eq2 = (addr1 =/= 0.U | addr2 =/= 0.U ) & ((addr2 === ex_dst & ex_dst =/= 0.U) | (addr2 === mem_dst & mem_dst =/= 0.U) | (addr2 === wb_dst & wb_dst =/= 0.U)) & (optype.Itype | optype.Rtype | optype.Btype | optype.Jtype | optype.Stype)

  val jalr_cond = (0.U =/= ex_dst | 0.U =/= mem_dst | 0.U =/= wb_dst) & operator.jalr//分支跳转添加空泡
  /* output */
  io.stall := eq1 | eq2 | jalr_cond
}
