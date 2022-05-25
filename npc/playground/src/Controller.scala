import chisel3._

class Controller extends Module{
  val io = IO(new Bundle {
    val inst_i    =   Input (UInt(32.W))
    val instfmt_o =   Output(UInt(6 .W))
    val exop_o    =   Output(UInt(64.W))
    val m2r_ctrl_o =   Output(Bool())
    val regfile_wen_o = Output(Bool())
  })

}