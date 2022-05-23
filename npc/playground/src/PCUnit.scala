import chisel3._
import chisel3.util._
import chisel3.experimental._


class PCUnit extends Module {
  val io = IO(new Bundle {
    val i_test = Input(Bool())
    val o_test = Output(Bool())
    // val npc_i   = Input(UInt(64.W))
    // val npcop_i = Input(UInt( 4.W))
    // val pc_o    = Input(UInt(64.W))
  })  
    io.o_test <> io.i_test;
   val pc = RegInit("h80000000".U(64.W))
   pc := io.i_test//0x1.U(64.W)
//    pc := io.npc_i

}
