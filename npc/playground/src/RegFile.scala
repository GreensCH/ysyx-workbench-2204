import chisel3._
import chisel3.util._

class RegFileRD extends Bundle {
  val en     =   Input (Bool())
  val addr1  =   Input (UInt(5.W))
  val data1  =   Output (UInt(64.W))
  val addr2  =   Input (UInt(5.W))
  val data2  =   Output (UInt(64.W))
}

class RegFileWE extends Bundle {
  val en     =   Input (Bool())
  val addr   =   Input (UInt(5.W))
  val data   =   Input (UInt(64.W))
}



class RegFile extends Module{
  val io = IO(new Bundle{
    val we = new RegFileWE
    val rd = new RegFileRD
  })

  val gpr = RegInit(VecInit(0.U(64.W), 0.U(64.W)))
  io.rd.data1 := gpr(io.rd.addr1 & Fill(64, io.rd.en))
  io.rd.data2 := gpr(io.rd.addr2 & Fill(64, io.rd.en))
  gpr(io.we.addr & Fill(64, io.we.en)) := (io.we.data & Fill(64, io.we.en))
  gpr(0) := 0.U(64.W)
}