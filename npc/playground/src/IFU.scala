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
    val sys_ready = Input(Bool())
    val next = new PCUOut
  })
  /* interface */
  val dataNext  = io.next.bits.pc2if
  val jump      = io.jump
  val jump_pc   = io.npc
  /* instance */
  val pc_reg_in = Wire(UInt(64.W))
  val pc_reg = RegEnable(init = SparkConfig.StartAddr, next = pc_reg_in, enable = (io.next.ready | io.jump) & io.sys_ready)
  val inc_pc_out = pc_reg + 4.U(64.W)
  val start = RegEnable(init = false.B, next = true.B, enable = io.sys_ready)

  pc_reg_in := MuxCase(inc_pc_out, Array(
    jump              -> jump_pc,
    (!io.next.ready)  -> pc_reg ,
  ))
  io.next.valid := MuxCase(start, Array(
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
    val sram0 = Flipped(new SRAMIO)
    val sram1 = Flipped(new SRAMIO)
    val sram2 = Flipped(new SRAMIO)
    val sram3 = Flipped(new SRAMIO)
  })
  private val prev = io.prev
  private val maxi = io.maxi
  private val next = io.next

  val pc_hi = prev.bits.pc2if.pc(63, 38).andR()
  val pc2addr = Wire(UInt(CacheCfg.paddr_bits.W))
  pc2addr := Cat(pc_hi, prev.bits.pc2if.pc(37, 0))
  /*
    ICache Connection
    */
  private val icache = Module(new ICacheUnit)
  /*  Connection Between outer.prev and inter.icache */
  icache.io.prev.bits.addr := pc2addr
  icache.io.prev.valid := prev.valid
  /*  Connection Between outer.next and inter.icache */
  next.bits.if2id := icache.io.next.bits.data.if2id
  icache.io.next.ready := next.ready
  /*  Connection Between outer.maxi and inter.icache */
  icache.io.cache_reset := io.cache_reset
  icache.io.maxi <> io.maxi
  icache.io.maxi.ar.ready := io.maxi.ar.ready
  icache.io.maxi.aw.ready := io.maxi.aw.ready
  /*  Connection SRAM */
  icache.io.sram0 <> io.sram0
  icache.io.sram1 <> io.sram1
  icache.io.sram2 <> io.sram2
  icache.io.sram3 <> io.sram3
  /* Output Handshake Signals */
  io.prev.ready := io.next.ready & icache.io.prev.ready
  io.next.valid := io.prev.valid & icache.io.next.valid

}

class IFUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val if2id = new IF2ID
  }
}

object IFU {
  def apply(bru: BR2IF, next: IFUOut, maxi: AXI4Master, sys_ready: Bool): IFU ={
    val pc = Module(new PC)
    pc.io.npc := bru.npc
    pc.io.jump := bru.jump
    pc.io.jump0 := bru.jump0
    pc.io.sys_ready := sys_ready

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
