 import chisel3._
 import chisel3.util._
 /**
   * ebreak
   */
 class ebreak extends BlackBox with HasBlackBoxResource {
   val io = IO(new Bundle {
     val valid = Input(Bool())
   } )
   addResource("/ebreak.v")
 }


 class Ebreak extends Module{
   val io = IO(new Bundle {
     val valid = Input(Bool())
   } )
   val valid = io.valid
   val ebreak = Module(new ebreak)
   ebreak.io.valid := io.valid
 }