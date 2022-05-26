import chisel3._
import chisel3.util._


class RegFileIO extends Bundle {
  val rd_en     =   Input (Bool())
  val rd_addr1  =   Input (UInt(5.W))
  val rd_data1  =   Output (UInt(64.W))
  val rd_addr2  =   Input (UInt(5.W))
  val rd_data2  =   Output (UInt(64.W))
  val we_en     =   Input (Bool())
  val we_addr   =   Input (UInt(5.W))
  val we_data   =   Input (UInt(64.W))
}


class RegFile extends Module{
  val io = IO(new RegFileIO())
  val gpr = RegInit(VecInit(0.U(64.W), 0.U(64.W)))
  io.rd_data1 := gpr(io.rd_addr1 & Fill(64, io.rd_en))
  io.rd_data2 := gpr(io.rd_addr2 & Fill(64, io.rd_en))
  gpr(io.we_addr & Fill(64, io.we_en)) := (io.we_data & Fill(64, io.we_en))
  gpr(0) := 0.U(64.W)
}