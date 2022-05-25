import chisel3._

class IFUnit extends Module {
  val io = IO(new Bundle {
    val pc_i    =   Input (UInt(64.W))
    val inst_o  =   Output(UInt(32.W))
  })
  val inst_mem = Module(new Memory)

  inst_mem.io.raddr := io.pc_i
  io.inst_o := inst_mem.io.rdata
  inst_mem.io.waddr := DontCare
  inst_mem.io.wdata := DontCare
  inst_mem.io.wmask := DontCare
}
