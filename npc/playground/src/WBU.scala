import chisel3._


class WBU extends Module {
  val io = IO(new Bundle {
    val id2wb = Flipped(new ID2WB)
    val ex2wb = Flipped(new EX2WB)
    val mem2wb = Flipped(new MEM2WB)
    val wb2regfile = Flipped(new RegFileWB)
  })
//  printf("WBU\t\n")
  /* interface */
  val we_en = io.id2wb.regfile_we_en
  val we_addr = io.id2wb.regfile_we_addr
  val memory_result = io.id2wb.memory_result
  val memory_data = io.mem2wb.memory_data
  val result_data = io.ex2wb.result_data
  /* wb2regfile interface */
  io.wb2regfile.en  := we_en
  io.wb2regfile.addr:= we_addr
  io.wb2regfile.data:= Mux(memory_result, memory_data, result_data)
}
