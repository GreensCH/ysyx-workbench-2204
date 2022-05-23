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

//    pc := io.i_test//0x1.U(64.W)
//    io.i_test := pc(0)
//    pc := io.npc_i

}
