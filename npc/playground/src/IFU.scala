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
class  IFUIn extends Bundle{
  val pc2if   =   Flipped(new PC2IF)
}
class  IFUOut extends Bundle{
  val if2id   =   new IF2ID
}
class IFU extends Module {
  val io = IO(new Bundle {
    val in = new IFUIn
    val out = new IFUOut
  })
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  val rd_en   = true.B
  val rd_addr = io.in.pc2if.pc
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
  io.out.if2id.inst := rd_data
  io.out.if2id.pc := io.in.pc2if.pc
}

object IFU{
  def apply(fw2id: FW2ID, if2id: IF2ID, regfile2id: RegFileID,
            id2fw: ID2FW, id2br: ID2BR, id2ex: ID2EX, id2mem: ID2EX, id2wb: ID2WB): IDU = {
    val idu = Module(new IDU)
    idu.io.fw2id := Flipped(fw2id)
    idu.io.in.if2id := Flipped(if2id)
    idu.io.regfile2id <> Flipped(regfile2id)
    id2fw  := idu.io.out.id2fw
    id2br  := idu.io.out.id2br
    id2ex  := idu.io.out.id2ex
    id2mem := idu.io.out.id2mem
    id2wb  := idu.io.out.id2wb
    idu
  }
}