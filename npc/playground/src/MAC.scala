import chisel3._
import chisel3.util._
/**
 * ebreak
 */
class MacIO extends Bundle{
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
}

class mac extends BlackBox with HasBlackBoxResource {
  val io = IO(new MacIO)
  addResource("/mac.v")
}


class MAC extends Module{
  val io = IO(new MacIO)

  val mac = Module(new mac)

  mac.io <> io
}