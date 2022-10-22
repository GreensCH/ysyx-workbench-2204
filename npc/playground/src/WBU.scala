import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

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
  rdyPrev := rdyNext//RegNext(rdyNext, true.B)//rdyNext
  // Right
  vldNext := RegEnable(init = false.B, next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new MEMUOut).bits))
  val reg = RegEnable(init = 0.U.asTypeOf(data), next = data, enable = rdyNext)
  dataNext := reg
}

class WBU extends Module {
  val io = IO(new Bundle {
    val prev = Flipped(new MEMUOut)
    val regfile = Flipped(new RegfileWB)
  })
  private val idb = io.prev.bits.id2wb
  private val exb = io.prev.bits.ex2wb
  private val memb = io.prev.bits.mem2wb
  private val rfb  = io.regfile
  io.prev.ready := true.B
  private val prev_valid = io.prev.valid
  /* interface */
  private val we_en = idb.regfile_we_en
  private val we_addr = idb.regfile_we_addr
  private val wb_sel = idb.wb_sel
  private val result_data = exb.result_data
  private val memory_data = memb.memory_data
  /* wb2regfile interface */
  rfb.en  := we_en & prev_valid
  rfb.addr:= we_addr

  private val load_size = io.prev.bits.id2wb.load_size
  private val raw_read_data = MuxCase(0.U,
    Array(
      load_size.byte   -> memory_data(7,  0),
      load_size.hword  -> memory_data(15, 0),
      load_size.word   -> memory_data(31, 0),
      load_size.dword  -> memory_data,
    )
  )
  private val sext_memory_data_signed = MuxCase(memory_data.asSInt,
    Array(
      load_size.byte   -> memory_data(7, 0).asSInt,
      load_size.hword  -> memory_data(15, 0).asSInt,
      load_size.word   -> memory_data(31, 0).asSInt,
      load_size.dword  -> memory_data(63, 0).asSInt,
    )
  )
  private val sext_memory_data = sext_memory_data_signed.asUInt
  private val load_data = Mux(io.prev.bits.id2wb.load_sext, sext_memory_data, raw_read_data)
  rfb.data := Mux(wb_sel, load_data, result_data)

  /* ebreak */
  if(!SparkConfig.ysyxSoC){
    val ebreak = Module(new Ebreak)
    ebreak.io.valid := idb.ebreak//operator.ebreak
  }
  /* feedback */
  BoringUtils.addSource(io.prev.bits.id2wb.intr_exce_ret, "wb_intr_exce_ret")
  BoringUtils.addSource(io.prev.bits.id2wb.fencei, "wb_fencei")
  /*
   Test Interface
   */
  /* test */
  val test_pc = idb.test_pc
  val test_inst = idb.test_inst
  if(SparkConfig.Printf) {
    when(!reset.asBool()){
      printf(p"${test_pc} ${test_inst}\n")
    }
  }
  /* DPIC pc out */
  if(/*!SparkConfig.ysyxSoC*/true){
    val test = Module(new TestPC)
    test.io.pc := test_pc
    test.io.npc := test_inst === "h7b".U
    test.io.is_device := idb.test_device
  }
}//

object WBU {
  def apply(prev: MEMUOut,
            regfile: RegfileWB, fwu: WB2FW): WBU ={
    val MEM2WBReg = Module(new WBReg)
    MEM2WBReg.io.prev <> prev

    val wbu = Module(new WBU)
    wbu.io.prev <> MEM2WBReg.io.next
    regfile <> wbu.io.regfile

    fwu.dst_addr := MEM2WBReg.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data := wbu.io.regfile.data

    wbu
  }
}