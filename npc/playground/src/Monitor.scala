import chisel3._
import chisel3.util._

class Monitor extends Module{
  val io = IO(new Bundle{
    val pc = Input(UInt(64.W))
    val inst = Input(UInt(32.W))
  })
  printf(p"\33[1;31mNPC\tpc=0x${Hexadecimal(io.pc)}, inst=0x${Hexadecimal(io.inst)}\33[0m\n")
}
