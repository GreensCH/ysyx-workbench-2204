import chisel3._
import chisel3.util._


class MemInf extends Bundle {
  val raddr   =   Input (UInt(64.W))
  val rdata   =   Output(UInt(64.W))
  val waddr   =   Input (UInt(64.W))
  val wdata   =   Input (UInt(64.W))
  val wmask   =   Input (UInt(8.W))
}

//class dpic_memory extends Module{
//  val io = IO(new MemInf)
//  io.rdata := "h80000000".U
//}

class dpic_memory extends BlackBox with HasBlackBoxResource {
  val io = IO(new MemInf)
  addResource("/dpic_memory.v")
}


class Memory extends Module{
  val io = IO(new MemInf)
  val m = Module(new dpic_memory)
  m.io <> io
}

class MEMUnit extends Module {
  val io = IO(new MemInf)
  val mem = Module(new Memory)

}