import chisel3._


class WBU extends Module {
  val io = IO(new Bundle {
    val id2wb = Flipped(new ID2WB)
    val ex2wb = Flipped(new EX2WB)
    val mem2wb = Flipped(new MEM2WB)
    val wb2regfile = new WB2RegFile
  })
//  printf("WBU\t\n")
  /* interface */
  val wb_sel = io.id2wb.wb_sel
  val result_data = io.ex2wb.result_data
  val memory_data = io.mem2wb.memory_data
  /* wb2regfile interface */
  io.wb2regfile.data:= Mux(wb_sel, memory_data, result_data)
  when(wb_sel){
    printf(p"result_data ${Hexadecimal(result_data)} ")
    printf(p"io.mem2wb.memory_data ${Hexadecimal(io.mem2wb.memory_data )}")
    printf(p"io.wb2regfile.data ${Hexadecimal(io.wb2regfile.data)} \n")
  }
}
