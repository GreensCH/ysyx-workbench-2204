import chisel3._
import chisel3.util._
import chisel3.experimental._


class PCUnit {
  val io = IO(new Bundle {
    val npc_i   = Input(64.W)
    val npcop_i = Input(4.W)
    val pc_o    = Input(64.W)
  })  
  
   val pc = Reg(next = npc_i, init = 0x80000000.U(64.W))

}
