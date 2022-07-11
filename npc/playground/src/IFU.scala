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
  if(SparkConfig.ICache){
    /* interface */
    val dataNext = io.next.bits.pc2if
    val jump = io.br2pc.jump
    val jump_pc = io.br2pc.npc
    /* jump fifo */
    // if jump then lock the pc
    // when
    val jump_latch = RegEnable(next = jump_pc, init = 0.U(64.W), enable = jump | io.next.ready)
    val jump_status_latch = RegEnable(next = jump, init = false.B, enable = jump | io.next.ready)
    val jump_pc_out = Mux(jump, jump_pc, jump_latch)
    /* instance */
    val pc_reg_in = Wire(UInt(64.W))
    val pc_reg = RegEnable(next = pc_reg_in, init = "h80000000".U(64.W), enable = io.next.ready)
    pc_reg_in := Mux(jump | jump_status_latch, jump_pc_out, pc_reg + 4.U(64.W))
    /* connection */
    dataNext.pc := pc_reg
    io.next.valid := true.B
  }else{
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
}


class IFU extends Module {
  val io = IO(new Bundle {
    val prev  = Flipped(new PCUOut)
    val next  = new IFUOut
    val maxi  = new AXI4
  })
  dontTouch(io.prev.ready)
  dontTouch(io.prev.valid)
  dontTouch(io.next.ready)
  dontTouch(io.next.valid)
  if(SparkConfig.ICache){
    /* inst cache instance */
    val icache = Module(new ICache)
    icache.io.prev.bits <> io.prev.bits
    icache.io.next.bits <> io.next.bits
    icache.io.master <> io.maxi
    icache.io.prev.valid := io.prev.valid
    icache.io.next.ready := io.next.ready
    /* handshake signal */
    io.prev.ready := io.next.ready & icache.io.prev.ready
    io.next.valid := io.prev.valid & icache.io.next.valid
  }
  else{
    /* interface */
    io.prev.ready := io.next.ready
    io.next.valid := io.prev.valid
    io.maxi <> 0.U.asTypeOf(new AXI4)
    /* memory bus instance */
    val memory_inf = Module(new MemoryInf).io
    memory_inf.rd_en   := true.B
    memory_inf.rd_addr := io.prev.bits.pc2if.pc
    memory_inf.we_en   := false.B
    memory_inf.we_addr := 0.U
    memory_inf.we_data := 0.U
    memory_inf.we_mask := 0.U
    /* if2id interface */
    io.next.bits.if2id.inst := memory_inf.rd_data
    io.next.bits.if2id.pc   := io.prev.bits.pc2if.pc
  }
}

class IFUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val if2id = new IF2ID
  }
}
object IFU {
  def apply(bru: BR2IF, next: IFUOut, maxi: AXI4): IFU ={
    val pc = Module(new PC)
    pc.io.br2pc.npc := bru.npc
    pc.io.br2pc.jump := bru.jump

    val ifu = Module(new IFU)
    ifu.io.prev <> pc.io.next
    ifu.io.prev.valid := bru.br_valid & pc.io.next.valid
    next <> ifu.io.next
    maxi <> ifu.io.maxi

    ifu
  }
}

//class IFU extends Module {
//  val io = IO(new Bundle {
//    val prev  = Flipped(new PCUOut)
//    val next  = new IFUOut
//    val maxi  = new AXI4
//  })
//  val pcb = io.prev.bits.pc2if
//  val ifb = io.next.bits.if2id
//  io.prev.ready := io.next.ready
//  io.next.valid := io.prev.valid
//  /* memory bus instance */
//  val memory_inf = Module(new MemoryInf).io
//  val rd_en   = true.B
//  val rd_addr = pcb.pc
//  val rd_data = memory_inf.rd_data
//  val we_en   = false.B
//  val we_addr = false.B
//  val we_data = 0.U
//  val we_mask = 0.U
//  memory_inf.rd_en   := rd_en
//  memory_inf.rd_addr := rd_addr
//  memory_inf.we_en   := we_en
//  memory_inf.we_addr := we_addr
//  memory_inf.we_data := we_data
//  memory_inf.we_mask := we_mask
//  /* if2id interface */
//  ifb.inst := rd_data
//  ifb.pc   := pcb.pc
//}

//class IFU extends Module {
//  val io = IO(new Bundle {
//    val prev  = Flipped(new PCUOut)
//    val next  = new IFUOut
//  })
//  val pcb = io.prev.bits.pc2if
//  val ifb = io.next.bits.if2id
//  io.prev.ready := io.next.ready
//  io.next.valid := io.prev.valid
//  /* memory bus instance */
//  val memory_inf = Module(new MemoryInf).io
//  val rd_en   = true.B
//  val rd_addr = pcb.pc
//  val rd_data = memory_inf.rd_data
//  val we_en   = false.B
//  val we_addr = false.B
//  val we_data = 0.U
//  val we_mask = 0.U
//  memory_inf.rd_en   := rd_en
//  memory_inf.rd_addr := rd_addr
//  memory_inf.we_en   := we_en
//  memory_inf.we_addr := we_addr
//  memory_inf.we_data := we_data
//  memory_inf.we_mask := we_mask
//  /* if2id interface */
//  ifb.inst := rd_data
//  ifb.pc   := pcb.pc
//}
//class IFUOut extends MyDecoupledIO{
//  override val bits = new Bundle{
//    val if2id = new IF2ID
//  }
//}
//object IFU {
//  def apply(bru: BR2IF, next: IFUOut): IFU ={
//    val pc = Module(new PC)
//    pc.io.br2pc.npc := bru.npc
//    pc.io.br2pc.jump := bru.jump
//
//    val ifu = Module(new IFU)
//    ifu.io.prev <> pc.io.next
//    next <> ifu.io.next
//
//    next.valid := ifu.io.next.valid & bru.br_valid
//
//    ifu
//  }
//}


///* interface */
//val rdyNext  = io.next.ready
//val vldNext  = io.next.valid
//val dataNext = io.next.bits.pc2if
//val jump = io.br2pc.jump
//val jump_pc = io.br2pc.npc
///* instance */
//val pc_reg_in = Wire(UInt(64.W))
//val pc_reg = RegEnable(next = pc_reg_in, init = "h80000000".U(64.W), enable = rdyNext)
//pc_reg_in := Mux(jump, jump_pc, pc_reg + 4.U(64.W))
///* connection */
//dataNext.pc := pc_reg
//vldNext := true.B