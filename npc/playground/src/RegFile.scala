import chisel3._
import chisel3.util._

class RegFileID extends Bundle {
  val en     =   Input (Bool())
  val addr1  =   Input (UInt(5.W))
  val data1  =   Output (UInt(64.W))
  val addr2  =   Input (UInt(5.W))
  val data2  =   Output (UInt(64.W))
}

class RegFileWB extends Bundle {
  val en     =   Input (Bool())
  val addr   =   Input (UInt(5.W))
  val data   =   Input (UInt(64.W))
}

class RegFile extends Module{
  val io = IO(new Bundle{
    val wbu = new RegFileWB // Instruction Decode Unit interface
    val idu = new RegFileID // Write Back Unit interface
  })

  val gpr = RegInit(VecInit(0.U(64.W), 0.U(64.W)))
  io.idu.data1 := gpr(io.idu.addr1 & Fill(64, io.idu.en))
  io.idu.data2 := gpr(io.idu.addr2 & Fill(64, io.idu.en))
  gpr(io.wbu.addr & Fill(64, io.wbu.en)) := (io.wbu.data & Fill(64, io.wbu.en))
  gpr(0) := 0.U(64.W)

}