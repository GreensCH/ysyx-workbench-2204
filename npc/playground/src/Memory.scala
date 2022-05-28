import chisel3._
import chisel3.util._

//class MemoryRD extends Bundle{
//  val rd_en    =   Input (Bool())
//  val rd_addr  =   Input (UInt(64.W))
//  val rd_data  =   Output(UInt(64.W))
//}
//
//class MemoryWE extends Bundle{
//  val we_en    =   Input (Bool())
//  val we_addr  =   Input (UInt(64.W))
//  val we_data  =   Input (UInt(64.W))
//  val we_mask  =   Input (UInt(8.W))
//}

class MemoryIO extends Bundle{
  val rd_en    =   Input (Bool())
  val rd_addr  =   Input (UInt(64.W))
  val rd_data  =   Output(UInt(64.W))
  val we_en    =   Input (Bool())
  val we_addr  =   Input (UInt(64.W))
  val we_data  =   Input (UInt(64.W))
  val we_mask  =   Input (UInt(8.W))
}

class dpic_memory extends BlackBox with HasBlackBoxResource {
  val io = IO(new MemoryIO)
  addResource("/dpic_memory.v")
}

class MemoryInf extends Module{
  val io = IO(new MemoryIO)
  val m = Module(new dpic_memory)
  m.io <> io
  printf(p"@NPC: we_addr=0x{io.we_addr}, we_addr=0x{io.we_data}, we_addr=0x{io.we_mask},")
}