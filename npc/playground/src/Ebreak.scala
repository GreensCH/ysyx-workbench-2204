 import chisel3._
 import chisel3.util._
 /**
   * ebreak
   */
 class verilog_ebreak extends BlackBox with HasBlackBoxResource {
   val io = IO(new Bundle {
     val valid = Input(Bool())
   } )
   addResource("/ebreak.v")
 }


 class Ebreak extends Module{
   val io = IO(new Bundle {
     val valid = Input(Bool())
   } )
   val ebreak = Module(new verilog_ebreak)
   ebreak.io.valid := io.valid
 }