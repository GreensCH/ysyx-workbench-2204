import chisel3._
import chisel3.util._


class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
}

class IF2ID extends Bundle{
  val inst  =   Output(UInt(32.W))
  val pc    =   Output(UInt(64.W))
}
class IFU extends Module {
  val io = IO(new Bundle {
    val pc2if   =   Flipped(new PC2IF)
    val if2id   =   new IF2ID
  })
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  val rd_en   = true.B
  val rd_addr = io.pc2if.pc
  val rd_data = memory_inf.rd_data
  val we_en   = false.B
  val we_addr = false.B
  val we_data = 0.U
  val we_mask = 0.U
  memory_inf.rd_en   := rd_en
  memory_inf.rd_addr := rd_addr
  memory_inf.we_en   := we_en
  memory_inf.we_addr := we_addr
  memory_inf.we_data := we_data
  memory_inf.we_mask := we_mask
  /* if2id interface */
  io.if2id.inst := rd_data
  io.if2id.pc := io.pc2if.pc
}

class IFU2 extends Module{
  val io = IO(new Bundle() {
    val in = Flipped(new PC2IF)
    val out = new PC2IF
  })
  io.out := io.in
}

object IFU2 {
  def apply(in: PC2IF, out: PC2IF): IFU2 ={
    val ifu2 = Module(new IFU2)
    ifu2.io.in := in
    out := ifu2.io.out
    ifu2
  }
}