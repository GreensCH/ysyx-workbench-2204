import chisel3._
import chisel3.util._


class MEMReg extends Module{
  val io = IO(new Bundle() {
    val prev = Flipped(new EXUOut)
    val next = new EXUOut
  })
  val rdyPrev  = io.prev.ready
  val vldPrev  = io.prev.valid
  val dataPrev = io.prev.bits
  val rdyNext  = io.next.ready
  val vldNext  = io.next.valid
  val dataNext = io.next.bits
  // Left
  rdyPrev := rdyNext//RegNext(rdyNext, true.B)//rdyNext
  // Right
  vldNext := RegEnable(init = false.B, next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new EXUOut).bits))
  val reg = RegEnable(init = 0.U.asTypeOf(data), next = data, enable = rdyNext)
  dataNext := reg
}
//////////////////////////////////////
class MEMU extends Module {
  val io = IO(new Bundle{
    val prev = Flipped(new EXUOut)
    val next = new MEMUOut
    val maxi  = new AXI4Master
    val mmio  = new AXI4Master
    val sram4 = Flipped(new SRAMIO)
    val sram5 = Flipped(new SRAMIO)
    val sram6 = Flipped(new SRAMIO)
    val sram7 = Flipped(new SRAMIO)
  })
  private val maxi = io.maxi
  private val mmio = io.mmio
  private val prev = io.prev
  private val next = io.next


  private val dcache = Module(new DCacheUnit)
  /*  Connection Between outer.prev and inter.icache */
  dcache.io.prev.bits.data := prev.bits
  dcache.io.prev.valid := prev.valid
  dcache.io.prev.bits.addr  := prev.bits.ex2mem.addr
  dcache.io.prev.bits.wdata := prev.bits.ex2mem.we_data
  dcache.io.prev.bits.wmask := prev.bits.ex2mem.we_mask
  dcache.io.prev.bits.size  := prev.bits.id2mem.size
  dcache.io.prev.bits.flush := prev.bits.id2mem.fencei
  /*  Connection Between outer.next and inter.icache */
  next.bits := dcache.io.next.bits.data
  dcache.io.next.ready := next.ready
  /*  Connection Between outer.maxi and inter.icache */
  dcache.io.maxi <> maxi
  dcache.io.mmio <> mmio
  /*  Connection SRAM */
  dcache.io.sram4 <> io.sram4
  dcache.io.sram5 <> io.sram5
  dcache.io.sram6 <> io.sram6
  dcache.io.sram7 <> io.sram7
  /* Output Handshake Signals */
  next.valid := dcache.io.next.valid
  prev.ready := dcache.io.prev.ready


}



class MEMUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val id2wb  = new ID2WB
    val ex2wb  = new EX2WB
    val mem2wb = new MEM2WB
  }
}

object MEMU {
  def apply(prev: EXUOut, next: MEMUOut,
            maxi: AXI4Master, mmio: AXI4Master,
            fwu: MEM2FW): MEMU = {
    val EX2MEMReg = Module(new MEMReg)
    EX2MEMReg.io.prev <> prev

    val memu = Module(new MEMU)
    memu.io.prev <> EX2MEMReg.io.next
    memu.io.maxi <> maxi
    memu.io.mmio <> mmio
    next <> memu.io.next

    // TODO
    fwu.dst_addr_1 := memu.io.prev.bits.id2wb.regfile_we_addr
    //    fwu.dst_data_1 := Mux(memu.io.prev.bits.id2wb.wb_sel, -1.S.asUInt(), memu.io.prev.bits.ex2wb.result_data)//error?
    fwu.dst_data_1 := memu.io.prev.bits.ex2wb.result_data //error?
    fwu.is_load_1 := memu.io.prev.bits.id2wb.wb_sel

    fwu.dst_addr_2 := memu.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data_2 := Mux(memu.io.next.bits.id2wb.wb_sel, memu.io.next.bits.mem2wb.memory_data, memu.io.next.bits.ex2wb.result_data) // wb_sel = is_load

    memu
  }

}
