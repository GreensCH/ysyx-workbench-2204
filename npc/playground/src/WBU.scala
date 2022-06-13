import chisel3._
import chisel3.util._

class WB2Regfile extends Bundle {
  val en     =   Output (Bool())
  val addr   =   Output (UInt(5.W))
  val data   =   Output (UInt(64.W))
}
//////////////////////////////////////
class WBRegIO extends Bundle{
  val ex2wb = new EX2WB
  val mem2wb = new MEM2WB
  val id2wb = new ID2WB
}
class WBReg extends Module{
  val io = IO(new Bundle() {
    val stall = Input(Bool())
    val in = Flipped(new WBRegIO)
    val out = new WBRegIO
  })
  val reg = RegEnable(next = io.in, enable = !io.stall)
  io.out := reg
}
//////////////////////////////////////
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
  /* test */
  val test_pc = io.id2wb.test_pc
  val test_inst = io.id2wb.test_inst
  when(!reset.asBool()){
    printf(p"${test_pc} ${test_inst}\n")
  }
  /* DPIC pc out */
  val test = Module(new TestPC)
  test.io.pc := test_pc
  test.io.npc := DontCare
}
