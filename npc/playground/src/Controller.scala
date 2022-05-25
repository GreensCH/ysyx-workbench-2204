import chisel3._
import chisel3.util._
import chisel3.experimental._

object Opcode extends ChiselEnum {
  val R, I, S, B, U, J = Value
}

class Controller extends Module{
  val io = IO(new Bundle {
    val inst_i        =   Input (UInt(32.W))
    val inst_fmt_o    =   Output(UInt(6 .W))
    val inst_opcode_o =   Output(UInt(6 .W))
    val exop_o    =   Output(UInt(64.W))
    val m2r_ctrl_o =   Output(Bool())
    val regfile_wen_o = Output(Bool())
  })
  val optype = io.inst_i(6, 0)
  val inst_fmt_o = MuxCase(default = R,
    Seq((
     optype ===  
    ))
  )

}