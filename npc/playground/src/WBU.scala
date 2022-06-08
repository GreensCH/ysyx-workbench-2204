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

}
