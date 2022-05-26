import chisel3._

class IFU extends Module {
  val io = IO(new Bundle {
    val pc    =   Input (UInt(64.W))
    val inst  =   Output(UInt(32.W))
  })

  val inst_mem = Module(new MemoryInf)

  inst_mem.io.rd_addr := io.pc
  io.inst := inst_mem.io.rd_data

  inst_mem.io.we_addr := DontCare
  inst_mem.io.we_data := DontCare
  inst_mem.io.we_mask := DontCare
}
