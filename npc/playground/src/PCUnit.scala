import chisel3._
import chisel3.util._
import chisel3.experimental._


class PCUnit {
  val io = IO(new Bundle {
    val npc_i   = Input(UInt(64.W))
    val npcop_i = Input(UInt( 4.W))
    val pc_o    = Input(UInt(64.W))
  })  
  
   val pc = RegInit(0x80000000.U(64.W))
   pc <> io.npc_i

}
