import chisel3._
import chisel3.util._

class ID2Regfile extends Bundle {
  val rd_en  =   Output (Bool())
  val addr1  =   Output (UInt(5.W))
  val data1  =   Input (UInt(64.W))
  val addr2  =   Output (UInt(5.W))
  val data2  =   Input (UInt(64.W))
  val we_addr=   Output (UInt(5.W))
  val we_en  =   Output (Bool())
}

class WB2Regfile extends Bundle {
  val data   =   Output (UInt(64.W))
}

class RegFile extends Module{
  val io = IO(new Bundle{
    val idu = Flipped(new ID2Regfile) // Instruction Decode Unit interface
    val wbu = Flipped(new WB2Regfile) // Write Back Unit interface
  })

  val gpr = RegInit(VecInit(Seq.fill(32)(0.U(64.W))))
  io.idu.data1 := gpr(io.idu.addr1 & Fill(64, io.idu.rd_en))
  io.idu.data2 := gpr(io.idu.addr2 & Fill(64, io.idu.rd_en))
  gpr(io.idu.we_addr) := (io.wbu.data & Fill(64, io.idu.we_en))
  gpr(0) := 0.U(64.W)



    printf(p"io.idu.we_en ${Hexadecimal(io.idu.we_en)} ")
    printf(p"io.idu.we_addr ${Hexadecimal(io.idu.we_addr)} ")
    printf(p"io.wbu.data ${Hexadecimal(io.wbu.data)} \n")

//  printf(p"gpr1:${gpr(1)}\n")
  /* DiffTest */
  val test_regfile = Module(new TestRegFile)
  test_regfile.io.gpr := gpr
}