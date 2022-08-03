import chisel3._
import chisel3.util._

class MEM2WB extends Bundle{
  val memory_data = Output(UInt(64.W))
}

class MEMU extends Module {
  val io = IO(new Bundle{
    val id2mem = Flipped(new ID2MEM)
    val ex2mem = Flipped(new EX2MEM)
    val mem2wb = new MEM2WB
  })
//  printf("MEMU\t\n")
  /* MEMU interface */
  val byte  = io.id2mem.size.byte
  val hword = io.id2mem.size.hword
  val word  = io.id2mem.size.word
  val dword = io.id2mem.size.dword
  val sext_flag = io.id2mem.sext_flag
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  /* memory interface */
  val rd_en   = io.id2mem.memory_rd_en
  val rd_addr = io.ex2mem.rd_addr
  val rd_data = memory_inf.rd_data
  val we_en   = io.id2mem.memory_we_en
  val we_addr = io.ex2mem.we_addr
  val we_data = io.ex2mem.we_data
  val we_mask = io.ex2mem.we_mask
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
  io.mem2wb.memory_data := Mux(sext_flag, sext_memory_data, raw_memory_data)

}