import chisel3._
import chisel3.util._


class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
}

class MyDecoupledIO extends Bundle{
  val ready = Input (Bool())
  val valid = Output(Bool())
  val bits  = new Bundle{}
}
class IF2ID extends Bundle {
    val inst  =   Output(UInt(32.W))
    val pc    =   Output(UInt(64.W))
}
class IFU extends Module {
  val io = IO(new Bundle {
    val pc2if   =   Flipped(new PC2IF).bits
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
class IFUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val inst  =   Output(UInt(32.W))
    val pc    =   Output(UInt(64.W))
  }
}
object IFU {
  def apply(in: BR2PC, next: IFUOut): IFU ={
    val pc = Module(new PC)
    val ifu = Module(new IFU)

    val pc2if = pc.io.pc2if.bits// pc out
    val pcVld = pc.io.pc2if.valid// pc(reg) decouple
    val pcRdy = pc.io.pc2if.ready
    /** PC(Reg) Connection */
    pc.io.br2pc := in // pc in
    pcRdy := next.ready// decouple connection
    next.valid := pcVld
    /** IFU(Logic) Connection */
    ifu.io.pc2if := pc2if// ifu in
    next.bits := ifu.io.if2id
    /** Return */
    ifu
  }
}