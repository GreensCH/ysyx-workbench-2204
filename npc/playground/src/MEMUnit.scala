import chisel3._
import chisel3.util._




class MEMUnit extends Module {
  val io = IO(new MemeryIO)
  val memif = Module(new MemoryIf)

}