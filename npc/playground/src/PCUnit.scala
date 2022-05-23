import chisel3._
import chisel3.util._
import chisel3.experimental._


class PCUnit extends Module {
  val io = IO(new Bundle {
    val npc_i   = Input(UInt(32.W))
    val npcop_i = Input(UInt( 4.W))
    val pc_o    = Input(UInt(32.W))
  })  
  
   val pc = RegInit(0x80000000.U(32.W))
   pc <> io.npc_i

}
