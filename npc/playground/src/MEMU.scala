import chisel3._
import chisel3.util._

class MEM2WB extends Bundle{
  val memory_data = Output(UInt(64.W))
}
class MEM2WBReg extends Module{
  val io = IO(new Bundle {
    val stall = Input(Bool())
    val in = Flipped(new MEM2WB)
    val out = new MEM2WB
  })
  val reg = RegEnable(next = io.in, enable = !io.stall)
  io.out := reg
}
//////////////////////////////////////
class MEMRegIO extends Bundle{
  val ex2mem = new EX2MEM
  val id2mem = new ID2MEM
  val id2wb = new ID2WB
  val ex2wb = new EX2WB
}
class MEMReg extends Module{
  val io = IO(new Bundle() {
    val in = Flipped(new MEMRegIO)
    val out = new MEMRegIO
  })
  // data transfer
  val reg = RegNext(next = io.in)
  io.out := reg
}
//////////////////////////////////////
class MEMUIn extends Bundle{
  val id2mem = Flipped(new ID2MEM)
  val ex2mem = Flipped(new EX2MEM)
}
class MEMUOut extends Bundle{
  val mem2wb = new MEM2WB
}
class MEMU extends Module {
  val io = IO(new Bundle{
    val in  = new MEMUIn
    val out = new MEMUOut

  })
  /* Refer main bundles */
  val id2mem = io.in.id2mem
  val ex2mem = io.in.ex2mem
  val mem2wb = io.out.mem2wb
  /* MEMU interface */
  val byte  = id2mem.size.byte
  val hword = id2mem.size.hword
  val word  = id2mem.size.word
  val dword = id2mem.size.dword
  val sext_flag = id2mem.sext_flag
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  /* memory interface */
  val rd_en   = id2mem.memory_rd_en
  val rd_addr = ex2mem.rd_addr
  val rd_data = memory_inf.rd_data
  val we_en   = id2mem.memory_we_en
  val we_addr = ex2mem.we_addr
  val we_data = ex2mem.we_data
  val we_mask = ex2mem.we_mask
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
  mem2wb.memory_data := Mux(sext_flag, sext_memory_data, raw_memory_data)

}