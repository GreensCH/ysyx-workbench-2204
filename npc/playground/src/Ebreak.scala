 import chisel3._
 import chisel3.util._
 /**
   * ebreak
   */
 class Ebreak extends HasBlackBoxResource {
   val io = IO(new Bundle {
     val valid = Input(Bool())
   } )
   addResource("/ebreak.v")
 }
