import chisel3._
import chisel3.util._
import chisel3.experimental._

class IFUnit extends Module {
  val io = IO(new Bundle {
    val pc    =   Input (UInt(64.W))
    val inst  =   Output(UInt(32.W))
  })
  val inst_mem = Module(new InstMem)


  
}
