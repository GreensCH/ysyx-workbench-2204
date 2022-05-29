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
  val io = IO(new MemoryIO{
    val clk      =   Input (Clock())
    val rst    =   Input (Reset())
  })
  addResource("/dpic_memory.v")
}

class MemoryInf extends Module{
  val io = IO(new MemoryIO)

  val m = Module(new dpic_memory)
  m.io.clk := clock
  m.io.rst := reset

  m.io.rd_en := io.rd_en
  m.io.rd_addr := io.rd_addr
  io.rd_data := m.io.rd_data
  //printf(p"NPC\trd_addr=0x${Hexadecimal(io.rd_addr)}, rd_data=0x${Hexadecimal(io.rd_data)}, rd_en=${Binary(m.io.rd_en)}\n")
  m.io.we_en := io.we_en
  m.io.we_addr := io.we_addr
  m.io.we_data := io.we_data
  m.io.we_mask := io.we_mask
  //printf(p"NPC\twe_addr=0x${Hexadecimal(io.we_addr)}, we_data=0x${Hexadecimal(io.we_data)}, we_mask=${Binary(m.io.we_en)}\n")

}