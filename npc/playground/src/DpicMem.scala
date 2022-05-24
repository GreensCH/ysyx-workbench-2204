import chisel3._
import chisel3.util._
import chisel3.experimental._

class DpicRAM extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle {
    val wr_en     =   Input (Bool())
    val wr_addr   =   Input (UInt(64.W))
    val rd_en     =   Input (Bool())
    val rd_addr   =   Input (UInt(64.W))
    val inst      =   Output(UInt(64.W))
  })
  addResource("/dpic_memory.v")
}


//class DpicRAM {
//
//}
