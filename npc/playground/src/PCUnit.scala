// import chisel3._
// import chisel3.util._
// import chisel3.experimental._

// object PcOpcode extends ChiselEnum {
//   val rst = Value(0.U)
//   val sta = Value(1.U)
//   val dyn = Value(2.U)
// }


// class PCUnit extends Module {
//   val io = IO(new Bundle {
//     val npc_i   = Input (UInt(64.W))
//     val npcop_i = Input (PcOpcode())
//     val pc_o    = Output(UInt(64.W))
//   })  
//     io.o_test <> io.i_test;
//     val mux_out = Mux1H(Seq(
//     io.npcop_i(0) -> 2.U,
//     io.npcop_i(1) -> 4.U,
//     io.npcop_i(2) -> 8.U,
//     io.npcop_i(4) -> 11.U
// ))
//     val pc = RegInit("h80000000".U(64.W))
//     pc := mux_out
// //    pc := io.i_test//0x1.U(64.W)
// //    io.i_test := pc(0)
// //    pc := io.npc_i

// }
