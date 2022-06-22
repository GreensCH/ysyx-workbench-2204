import chisel3._
import chisel3.util._



class PC2Out extends MyDecoupledIO{
  override val bits = new Bundle{
    val pc = (new PC2IF).pc
  }
}
class PC extends Module {
  val io = IO(new Bundle {
    val br2pc = Flipped(new BR2PC)
    val pc2if = new PC2Out
  })
  /* interface */
  val ready = io.pc2if.ready
  val jump = io.br2pc.jump
  val jump_pc = io.br2pc.npc
  /* instance */
  val pc_reg_in = Wire(UInt(64.W))
  val pc_reg = RegEnable(next = pc_reg_in, init = "h80000000".U(64.W), enable = ready)
  pc_reg_in := Mux(jump, jump_pc, pc_reg + 4.U(64.W))
  /* connection */
  io.pc2if.bits.pc := pc_reg
  io.pc2if.valid := true.B
}

class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
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
class IFUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val if2id = new IF2ID
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