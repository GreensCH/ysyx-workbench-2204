import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils



class PCUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val pc2if = new PC2IF
  }
}

class PC extends Module {
  val io = IO(new Bundle {
    val jump0 = Input(Bool())
    val jump = Input(Bool())
    val npc  = Input(UInt(64.W))
    val next = new PCUOut
  })
    /* interface */
    val dataNext  = io.next.bits.pc2if
    val jump      = io.jump
    val jump_pc   = io.npc
    /* instance */
    val pc_reg_in = Wire(UInt(64.W))
    val pc_reg = RegEnable(next = pc_reg_in, init = SparkConfig.StartAddr, enable = io.next.ready | io.jump)
    val inc_pc_out = pc_reg + 4.U(64.W)


    pc_reg_in := MuxCase(inc_pc_out, Array(
      jump              -> jump_pc,
      (!io.next.ready)  -> pc_reg ,
    ))
    io.next.valid := MuxCase(true.B, Array(
      reset.asBool()        -> false.B,
      io.jump0              -> false.B,
    ))
    /* connection */
    dataNext.pc := pc_reg
}


class IFU extends Module {
  val io = IO(new Bundle {
    val cache_reset = Input(Bool())
    val prev  = Flipped(new PCUOut)
    val maxi  = new AXI4Master
    val next  = new IFUOut
  })
  val prev = io.prev
  val maxi = io.maxi
  val next = io.next
  if(SparkConfig.ICache){
    /*
      ICache Connection
     */
    val icache = Module(new ICacheUnit)
    /*  Connection Between outer.prev and inter.icache */
    icache.io.prev.bits.addr := prev.bits.pc2if.pc
    icache.io.prev.valid := prev.valid
    /*  Connection Between outer.next and inter.icache */
    next.bits.if2id := icache.io.next.bits.data.if2id
    icache.io.next.ready := next.ready
    /*  Connection Between outer.maxi and inter.icache */
    icache.io.cache_reset := io.cache_reset
    icache.io.maxi <> io.maxi
    icache.io.maxi.ar.ready := io.maxi.ar.ready
    icache.io.maxi.aw.ready := io.maxi.aw.ready
    /* Output Handshake Signals */
    io.prev.ready := io.next.ready & icache.io.prev.ready
    io.next.valid := io.prev.valid & icache.io.next.valid
  } else{
    io.cache_reset <> DontCare
    val valid_array_rst = WireDefault(false.B)
    BoringUtils.addSink(valid_array_rst, "fencei")

    /* interface */
    io.prev.ready := io.next.ready
    io.next.valid := io.prev.valid
    AXI4Master.default(maxi)
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
  def apply(bru: BR2IF, next: IFUOut, maxi: AXI4Master): IFU ={
    val pc = Module(new PC)
    pc.io.npc := bru.npc
    pc.io.jump := bru.jump
    pc.io.jump0 := bru.jump0

    val ifu = Module(new IFU)
    ifu.io.prev <> pc.io.next
    ifu.io.cache_reset := false.B
    when(bru.jump0){
      ifu.io.cache_reset := true.B
    }

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