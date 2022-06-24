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
  vldNext := RegNext(vldNext, true.B)
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
  })
  val idb = io.prev.bits.id2mem
  val exb = io.prev.bits.ex2mem
  val wbb = io.next.bits.mem2wb
  io.next.bits.ex2wb := io.prev.bits.ex2wb
  io.next.bits.id2wb := io.prev.bits.id2wb
  io.prev.ready := io.next.ready
  io.next.valid := io.prev.valid
  /* MEMU interface */
  val byte  = idb.size.byte
  val hword = idb.size.hword
  val word  = idb.size.word
  val dword = idb.size.dword
  val sext_flag = idb.sext_flag
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  /* memory interface */
  val rd_en   = idb.memory_rd_en
  val rd_addr = exb.rd_addr
  val rd_data = memory_inf.rd_data
  val we_en   = idb.memory_we_en
  val we_addr = exb.we_addr
  val we_data = exb.we_data
  val we_mask = exb.we_mask
  memory_inf.rd_en   := rd_en
  memory_inf.rd_addr := rd_addr
  memory_inf.we_en   := we_en
  memory_inf.we_addr := we_addr
  memory_inf.we_data := we_data
  memory_inf.we_mask := we_mask
//  printf(p"MEMU\tenabel${memory_inf.rd_en}addr${Hexadecimal(memory_inf.rd_addr)}data${Hexadecimal(memory_inf.rd_data)}\n")

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
            dword  -> memory_inf.rd_data//Sext(data = memory_inf.rd_data, pos = 64),
        )
    )
  /* mem2wb interface */
  wbb.memory_data := Mux(sext_flag, sext_memory_data, raw_memory_data)

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
    val reg = Module(new MEMReg)
    reg.io.prev <> prev

    val memu = Module(new MEMU)
    memu.io.prev <> reg.io.next
    next <> memu.io.next
    
    fwu.dst_addr := reg.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data := Mux(reg.io.next.bits.id2mem.memory_rd_en, memu.io.next.bits.mem2wb.memory_data, reg.io.next.bits.ex2wb.result_data)
    memu
  }
}