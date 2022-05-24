import chisel3._
import chisel3.util._


class MemInf extends Bundle {
  val raddr   =   Input (UInt(64.W))
  val rdata   =   Output(UInt(64.W))
  val waddr   =   Input (UInt(64.W))
  val wdata   =   Input (UInt(64.W))
  val wmask   =   Input (UInt(8.W))
}

class DPICMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val in1 = Input(UInt(64.W))
    val in2 = Input(UInt(64.W))
    val out = Output(UInt(64.W))
  })
  addResource("/dpic_memory.v")
}


class Memory extends Module{
  val io = IO(new MemInf)
  val m = new DPICMem
  /* connect */
  m.io <> io
}

class MEMUnit extends Module {
  val io = IO(new MemInf)
  val mem = new Memory


}