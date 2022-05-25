import chisel3._
import chisel3.util.HasBlackBoxResource

class MemeryIO extends Bundle {
  val raddr   =   Input (UInt(64.W))
  val rdata   =   Output(UInt(64.W))
  val waddr   =   Input (UInt(64.W))
  val wdata   =   Input (UInt(64.W))
  val wmask   =   Input (UInt(8.W))
}

class dpic_memory extends BlackBox with HasBlackBoxResource {
  val io = IO(new MemeryIO)
  addResource("/dpic_memory.v")
}

class MemoryIf extends Module{
  val io = IO(new MemeryIO)
  val m = Module(new dpic_memory)
  m.io <> io
}