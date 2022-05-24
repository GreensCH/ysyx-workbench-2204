import chisel3._
import chisel3.util._


class MemInf extends Bundle {
  val bbb   =   Input (UInt(64.W))
  val asdf   =   Input (UInt(64.W))
  val aaa   =   Output(UInt(64.W))
  val asdfasd   =   Input (UInt(64.W))
  val asdfs   =   Input (UInt(8.W))
}

class DPICMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new MemInf)
  addResource("/dpic_memory.v")
}


class Memory extends Module{
  val io = IO(new MemInf)
  val m = Module(new DPICMem)
  /* connect */
  m.io <> io
}

class MEMUnit extends Module {
  val io = IO(new MemInf)
  val mem = Module(new Memory)

}