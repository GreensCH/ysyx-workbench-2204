import chisel3._
import chisel3.util._

class Monitor extends Module{
  val io = IO(new Bundle{
    val pc = Input(UInt(64.W))
    val inst = Input(UInt(32.W))
  })
  printf(p"@NPC: pc = ${io.pc}, inst = ${io.inst}")
}
