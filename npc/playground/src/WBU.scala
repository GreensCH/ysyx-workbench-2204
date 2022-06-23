import chisel3._
import chisel3.util._


class WBReg extends Module{
  val io = IO(new Bundle() {
    val prev = Flipped(new MEMUOut)
    val next = new MEMUOut
  })
  val rdyPrev  = io.prev.ready
  val vldPrev  = io.prev.valid
  val dataPrev = io.prev.bits
  val rdyNext  = io.next.ready
  val vldNext  = io.next.valid
  val dataNext = io.next.bits
  // Left
  rdyPrev := rdyNext
  // Right
  vldNext := vldPrev
  // comp
  val data = Mux(vldPrev, 0.U.asTypeOf((new IDUOut).bits), dataPrev)
  val reg = RegEnable(next = data, enable = rdyNext)
  dataNext := reg
}
class WBU extends Module {
  val io = IO(new Bundle {
    val prev = Flipped(new MEMUOut)
    val regfile = Flipped(new RegfileWB)
  })
  val idb = io.prev.bits.id2wb
  val exb = io.prev.bits.ex2wb
  val memb = io.prev.bits.mem2wb
  val rfb  = io.regfile
  io.prev.ready := true.B
  io.prev.valid <> DontCare
  /* interface */
  val we_en = idb.regfile_we_en
  val we_addr = idb.regfile_we_addr
  val wb_sel = idb.wb_sel
  val result_data = exb.result_data
  val memory_data = memb.memory_data
  /* wb2regfile interface */
  rfb.en  := we_en
  rfb.addr:= we_addr
  rfb.data:= Mux(wb_sel, memory_data, result_data)
  /* test */
  val test_pc = idb.test_pc
  val test_inst = idb.test_inst
  when(!reset.asBool()){
    printf(p"${test_pc} ${test_inst}\n")
  }
  /* DPIC pc out */
  val test = Module(new TestPC)
  test.io.pc := test_pc
  test.io.npc := DontCare
}

object WBU {
  def apply( regfile: RegfileWB,
             prev: MEMUOut): WBU ={
    val reg = Module(new WBReg)
    reg.io.prev <> prev

    val wbu = Module(new WBU)
    wbu.io.prev <> reg.io.next
    regfile <> wbu.io.regfile

    wbu
  }
}