import chisel3._
import chisel3.util._

class CLINT extends  Module with CoreParameter{
  val io = IO(new Bundle{
    val addr = Input(UInt(PAddrBits.W))
    val data = Input(UInt(DataBits.W))
    val intr = Output(Bool())
  })

}
