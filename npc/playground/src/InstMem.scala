import chisel3._
import chisel3.util._
import chisel3.experimental._

class BbxInstMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val wr_en     =   Input (Bool())
    val wr_addr   =   Input (UInt(64.W))
    val rd_en     =   Input (Bool())
    val rd_addr   =   Input (UInt(64.W))
    val inst      =   Output(UInt(64.W))
  })
}


class InstMem extends Module {
  val io = IO(new Bundle {
    val wr_en     =   Input (Bool())
    val wr_addr   =   Input (UInt(64.W))
    val rd_en     =   Input (Bool())
    val rd_addr   =   Input (UInt(64.W))
    val inst      =   Output(UInt(64.W))
  })
  io.wr_en    :=    DontCare
  io.wr_addr  :=    DontCare

  val inst_mem = Module(new BbxInstMem)
  io <> inst_mem.io
}