import chisel3._
import chisel3.util._



class PCUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val pc2if = new PC2IF
  }
}
class PC extends Module {
  val io = IO(new Bundle {
    val br2pc = Flipped(new BR2PC)
    val next = new PCUOut
  })
  /* interface */
  val rdyNext  = io.next.ready
  val vldNext  = io.next.valid
  val dataNext = io.next.bits.pc2if
  val jump = io.br2pc.jump
  val jump_pc = io.br2pc.npc
  /* instance */
  val pc_reg_in = Wire(UInt(64.W))
  val pc_reg = RegEnable(next = pc_reg_in, init = "h80000000".U(64.W), enable = rdyNext)
  pc_reg_in := Mux(jump, jump_pc, pc_reg + 4.U(64.W))
  /* connection */
  dataNext.pc := pc_reg
  vldNext := true.B
}

class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
}

class IFU extends Module {
  val io = IO(new Bundle {
    val prev  = Flipped(new PCUOut)
    val next  = new IFUOut
  })
  val pcb = io.prev.bits.pc2if
  val ifb = io.next.bits.if2id
  io.prev.ready := io.next.ready
  io.next.valid := io.prev.valid
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  val rd_en   = true.B
  val rd_addr = pcb.pc
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
  ifb.inst := rd_data
  ifb.pc   := pcb.pc
}
class IFUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val if2id = new IF2ID
  }
}
object IFU {
  def apply(bru: BR2PC, next: IFUOut): IFU ={
    val pc = Module(new PC)
    pc.io.br2pc := bru

    val ifu = Module(new IFU)
    ifu.io.prev <> pc.io.next
    next <> ifu.io.next

    ifu
  }
}