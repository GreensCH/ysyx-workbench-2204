import chisel3._
import chisel3.util._




class MEMUnit extends Module {
  val io = IO(new MemInf)
  val mem = Module(new Memory)

}