import chisel3._
import chisel3.util._

class RegFileID extends Bundle {
  val rd_en     =   Input (Bool())
  val addr1  =   Input (UInt(5.W))
  val data1  =   Output (UInt(64.W))
  val addr2  =   Input (UInt(5.W))
  val data2  =   Output (UInt(64.W))
  val we_addr=   Input (UInt(64.W))
  val we_en  =   Input (Bool())
}

class RegFileWB extends Bundle {
  val data   =   Input (UInt(64.W))
}

class RegFile extends Module{
  val io = IO(new Bundle{
    val idu = new RegFileID // Instruction Decode Unit interface
    val wbu = new RegFileWB // Write Back Unit interface
  })

  val gpr = RegInit(VecInit(Seq.fill(32)(0.U(64.W))))
  io.idu.data1 := gpr(io.idu.addr1 & Fill(64, io.idu.rd_en))
  io.idu.data2 := gpr(io.idu.addr2 & Fill(64, io.idu.rd_en))
  gpr(io.idu.we_addr & Fill(64, io.idu.we_en)) := (io.wbu.data & Fill(64, io.idu.we_en))
  gpr(0) := 0.U(64.W)


  when(io.idu.we_en){
    printf(p"io.wbu.addr ${Hexadecimal(io.idu.we_addr)} ")
    printf(p"io.wbu.data ${Hexadecimal(io.wbu.data)} \n")
  } .otherwise{
    printf(p"io.wbu.addr ${Hexadecimal(io.idu.we_addr)} \n")
  }
//  printf(p"gpr1:${gpr(1)}\n")
  /* DiffTest */
  val test_regfile = Module(new TestRegFile)
  test_regfile.io.gpr := gpr
}