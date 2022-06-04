import chisel3._

class WB2Regfile extends Bundle {
  val en     =   Output (Bool())
  val addr   =   Output (UInt(5.W))
  val data   =   Output (UInt(64.W))
}

class WBU extends Module {
  val io = IO(new Bundle {
    val id2wb = Flipped(new ID2WB)
    val ex2wb = Flipped(new EX2WB)
    val mem2wb = Flipped(new MEM2WB)
    val wb2regfile = new WB2Regfile
  })

  /* interface */
  val we_en = io.id2wb.regfile_we_en
  val we_addr = io.id2wb.regfile_we_addr
  val wb_sel = io.id2wb.wb_sel
  val memory_data = io.mem2wb.memory_data
  val result_data = io.ex2wb.result_data
  /* wb2regfile interface */
  io.wb2regfile.en  := we_en
  io.wb2regfile.addr:= we_addr
  io.wb2regfile.data:= Mux(wb_sel, memory_data, result_data)
//  when(wb_sel){
    printf("WBU\t\n")
    printf(p"we_en ${Hexadecimal(we_en)} ")
    printf(p"we_addr ${Hexadecimal(we_addr)} ")
    printf(p"result_data ${Hexadecimal(result_data)} ")
    printf(p"io.mem2wb.memory_data ${Hexadecimal(io.mem2wb.memory_data )}")
    printf(p"io.wb2regfile.data ${Hexadecimal(io.wb2regfile.data)} \n")
//  }
}
