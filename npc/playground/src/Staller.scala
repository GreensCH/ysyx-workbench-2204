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

  val eq1 = (addr1 === ex_dst | addr1 === mem_dst | addr1 === wb_dst) & (optype.Itype | optype.Rtype)
  val eq2 = (addr2 === ex_dst | addr2 === mem_dst | addr2 === wb_dst) & (optype.Itype | optype.Rtype)
  /* output */
  io.stall := Mux(addr1 === 0.U & addr2 ===0.U, false.B, eq1 | eq2)
}
