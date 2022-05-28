 import chisel3._
 import chisel3.util._
// /**
//   * ebreak
//   */
 class Ebreak extends HasBlackBoxInline {
   val io = IO(new Bundle {
     var valid = Input(UInt(1.W))
   } )

    val valid = io.valid
//     setInline("Ebreak.v",
//     s"""
//     | import \"DPI-C\" function void ebreak();
//     | module Ebreak (valid);
//     | input valid;
//     | always@(*)
//     | begin
//     |   if(valid)
//     |     ebreak();
//     | end
//     | endmodule
//     """.stripMargin)
 }
