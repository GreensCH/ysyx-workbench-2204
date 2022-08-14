import chisel3._
import chisel3.util._
/**
 * ebreak
 */
class MDUIO extends Bundle{
  val clock   =   Input(Clock())
  val reset   =   Input(Reset())
  val mul     =   Input(Bool())
  val mulh    =   Input(Bool())
  val mulhu   =   Input(Bool())
  val mulhsu  =   Input(Bool())
  val div     =   Input(Bool())
  val divu    =   Input(Bool())
  val rem     =   Input(Bool())
  val remu    =   Input(Bool())
  val src1    =   Input(UInt(64.W))
  val src2    =   Input(UInt(64.W))
  val result  =   Output(UInt(64.W))
  val ready   =   Output(Bool())
}

class mdu extends BlackBox with HasBlackBoxResource {
  val io = IO(new MDUIO)
  addResource("/mdu.v")
}


class MDU extends Module{
  val io = IO(new MDUIO)

  private val mdu = Module(new mdu)

  mdu.io <> io
  mdu.io.clock <> clock
  mdu.io.reset <> reset
}