import chisel3._
import chisel3.util._

class RegFileID extends Bundle {
  val en     =   Input (Bool())
  val addr1  =   Input (UInt(5.W))
  val data1  =   Output (UInt(64.W))
  val addr2  =   Input (UInt(5.W))
  val data2  =   Output (UInt(64.W))
}

class RegFile extends Module{
  val io = IO(new Bundle{
    val wbu = Flipped(new WB2Regfile) // Instruction Decode Unit interface
    val idu = new RegFileID // Write Back Unit interface
  })

  val gpr = RegInit(VecInit(Seq.fill(32)(0.U(64.W))))
  io.idu.data1 := gpr(io.idu.addr1 & Fill(5, io.idu.en))
  io.idu.data2 := gpr(io.idu.addr2 & Fill(5, io.idu.en))
//  gpr(io.wbu.addr & Fill(5, io.wbu.en)) := io.wbu.data
  gpr(io.wbu.addr) := Mux(io.wbu.en, io.wbu.data, gpr(io.wbu.addr))
  gpr(0) := 0.U(64.W)

  when(io.wbu.en){
    printf("RegFile\t\n")
    printf(p"io.wbu.addr ${io.wbu.addr& Fill(5, io.wbu.en)} ")
    printf(p"gpr(addr) ${gpr(io.wbu.addr& Fill(5, io.wbu.en))} ")
    printf(p"io.wbu.data ${Hexadecimal(io.wbu.data)} \n")
  }


  /* DiffTest */
  val test_regfile = Module(new TestRegFile)
  test_regfile.io.gpr := gpr
}