import chisel3._

class IFUnit extends Module {
  val io = IO(new Bundle {
    val pc    =   Input (UInt(64.W))
    val inst  =   Output(UInt(32.W))
  })
  val inst_mem = Module(new Memory)
  inst_mem.io.raddr := io.pc
  io.inst := inst_mem.io.rdata
}
