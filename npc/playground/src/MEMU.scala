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
  vldNext := RegEnable(next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new EXUOut).bits))
  val reg = RegEnable(next = data, enable = rdyNext)
  dataNext := reg
}
//////////////////////////////////////
class MEMU extends Module {
  val io = IO(new Bundle{
    val prev = Flipped(new EXUOut)
    val next = new MEMUOut
//    val maxi  = new AXI4
//    val mmio  = new AXI4
  })
  if(SparkConfig.DCache){


  }else{
//    io.maxi <> 0.U.asTypeOf(new AXI4)
//    io.mmio <> 0.U.asTypeOf(new AXI4)
    MEMU.dpic_load_save(io.prev, io.next)
  }
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
            fwu: MEM2FW): MEMU ={
    val EX2MEMReg = Module(new MEMReg)
    EX2MEMReg.io.prev <> prev

    val memu = Module(new MEMU)
    memu.io.prev <> EX2MEMReg.io.next
    next <> memu.io.next

    fwu.dst_addr := EX2MEMReg.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data := Mux(EX2MEMReg.io.next.bits.id2mem.memory_rd_en, memu.io.next.bits.mem2wb.memory_data, EX2MEMReg.io.next.bits.ex2wb.result_data)
    memu
  }
  def axi_load_save(prev: EXUOut, next: MEMUOut): Unit = {

  }
  def dcache_load_save(prev: EXUOut, next: MEMUOut): Unit = {
    /*
      DCache Connection
     */
//    val icache = Module(new DCache(2.U(AXI4Parameters.idBits.W)))
//    /*  Connection Between outer.prev and inter.icache */
//    icache.io.prev.bits.data.pc2if.pc := prev.bits.pc2if.pc
//    icache.io.prev.bits.addr := prev.bits.pc2if.pc
//    icache.io.prev.valid := prev.valid
//    /*  Connection Between outer.next and inter.icache */
//    next.bits.if2id := icache.io.next.bits.data.if2id
//    icache.io.next.ready := next.ready
//    /*  Connection Between outer.maxi and inter.icache */
//    icache.io.master <> io.maxi
//    /* Output Handshake Signals */
//    prev.ready := next.ready & icache.io.prev.ready
//    next.valid := prev.valid & icache.io.next.valid
  }
  def dpic_load_save( prev: EXUOut, next: MEMUOut): Unit = {
    prev.ready := next.ready
    next.valid := prev.valid
    next.bits.ex2wb := prev.bits.ex2wb
    next.bits.id2wb := prev.bits.id2wb
    val idu = prev.bits.id2mem
    val exu = prev.bits.ex2mem
    val wbu = next.bits.mem2wb
    /* MEMU interface */
    val byte  = idu.size.byte
    val hword = idu.size.hword
    val word  = idu.size.word
    val dword = idu.size.dword
    val sext_flag = idu.sext_flag
    /* memory bus instance */
    val memory_inf = Module(new MemoryInf).io
    /* memory interface */
    val rd_en   = idu.memory_rd_en
    val rd_addr = exu.rd_addr
    val rd_data = memory_inf.rd_data
    val we_en   = idu.memory_we_en
    val we_addr = exu.we_addr
    val we_data = exu.we_data
    val we_mask = exu.we_mask
    memory_inf.rd_en   := rd_en
    memory_inf.rd_addr := rd_addr
    memory_inf.we_en   := we_en
    memory_inf.we_addr := we_addr
    memory_inf.we_data := we_data
    memory_inf.we_mask := we_mask

    val raw_memory_data = MuxCase(memory_inf.rd_data,
      Array(
        byte   -> memory_inf.rd_data(7,  0),
        hword  -> memory_inf.rd_data(15, 0),
        word   -> memory_inf.rd_data(31, 0),
        dword  -> memory_inf.rd_data,
      )
    )
    val sext_memory_data = MuxCase(memory_inf.rd_data,
      Array(
        byte   -> Sext(data = memory_inf.rd_data(7,  0), pos = 8),
        hword  -> Sext(data = memory_inf.rd_data(15, 0), pos = 16),
        word   -> Sext(data = memory_inf.rd_data(31, 0), pos = 32),
        dword  -> memory_inf.rd_data
      )
    )
    /* mem2wb interface */
    wbu.memory_data := Mux(sext_flag, sext_memory_data, raw_memory_data)
  }
}