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
//////////////////////////////////////
class IDRegIO extends Bundle{
  val if2id = new IF2ID
}
class IDReg extends Module{
  val io = IO(new Bundle() {
    val bubble = Input(Bool())
    val in = Flipped(new IDRegIO)
    val out = new IDRegIO
  })
  // pipeline control
  val bubble = io.bubble
  // data transfer
  val nop = Wire(new IF2ID)
  nop.inst := "h00000013".U(32.W)
  nop.pc := 0.U(64.W)
  val if2id = Mux(bubble, nop, io.in.if2id)
  val reg_if2id = RegNext(next = if2id)

  io.out.if2id  :=  reg_if2id
}
//////////////////////////////////////
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