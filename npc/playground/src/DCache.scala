import chisel3._
import chisel3.util._

class DCacheBaseIn extends MyDecoupledIO{
  override val bits = new Bundle{
    val data  = new Bundle{}
    val flush = Input(Bool())
    val wdata = Input(UInt(64.W))
    val wmask = Input(UInt(8.W))
    val size  = Input(new SrcSize)
    val addr  = Input(UInt(CacheCfg.paddr_bits.W))
  }
}
class DCacheBaseOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val data = new Bundle{}
  }
}

class DCacheBase[IN <: DCacheBaseIn, OUT <: DCacheBaseOut] (_in: IN, _out: OUT) extends Module {
  val io = IO(new Bundle {
    val prev = Flipped(_in)
    val master = new AXI4Master
    val next = _out
  })
  /*
   IO Interface
   */
  protected val prev = io.prev
  protected val memory = io.master
  protected val next = io.next
  /*
    Argument
   */
  protected val index_border_up   = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits - 1
  protected val index_border_down = CacheCfg.cache_offset_bits
  protected val tag_border_up   = CacheCfg.paddr_bits - 1
  protected val tag_border_down = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits
  /*
   States
   */
  protected val sLOOKUP     = 0.U(3.W)
  protected val sSAVE       = 1.U(3.W)
  protected val sREAD       = 2.U(3.W)
  protected val sRWAIT      = 3.U(3.W)
  protected val sWRITEBACK  = 4.U(3.W)
  protected val sWWAIT      = 5.U(3.W)
  protected val sEND        = 6.U(3.W)
  protected val sFLUSH      = 7.U(3.W)
  //protected val sLOOKUP :: sSAVE :: sREAD :: sRWAIT :: sWRITEBACK :: sWWAIT :: sEND :: sFLUSH :: Nil = Enum(8)
  protected val next_state = Wire(UInt(sLOOKUP.getWidth.W))
  protected val curr_state = RegNext(init = sLOOKUP, next = next_state)
  /*
   AXI Manager and Interface
   */
  AXI4Master.default(memory)
  val axi4_manager = Module(new AXI4Manager)
  axi4_manager.io.maxi <> memory
  val axi_rd_en = axi4_manager.io.in.rd_en
  val axi_we_en = axi4_manager.io.in.we_en
  val axi_we_data = axi4_manager.io.in.data
  val axi_addr = axi4_manager.io.in.addr
  val axi_rd_data = axi4_manager.io.out.data
  val axi_finish = axi4_manager.io.out.finish
  val axi_ready  = axi4_manager.io.out.ready
  axi4_manager.io.in.wmask := "hffff".U
  axi4_manager.io.in.size := 0.U.asTypeOf(chiselTypeOf(axi4_manager.io.in.size))
  axi4_manager.io.in.size.qword := true.B
  /*
   Array Signal
   */
  protected val data_cen_0 = false.B//Wire(Bool())
  protected val data_cen_1 = false.B//Wire(Bool())
  protected val data_array_0 = SRAM()
  protected val data_array_1 = SRAM()
  protected val data_array_out_0 = Wire(UInt(CacheCfg.ram_width.W))
  protected val data_array_out_1 = Wire(UInt(CacheCfg.ram_width.W))

  protected val tag_cen_0 = false.B//Wire(Bool())
  protected val tag_cen_1 = false.B//Wire(Bool())
  protected val tag_sram_0 = SRAM()
  protected val tag_sram_1 = SRAM()
  protected val tag_sram_out_0   = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_sram_out_1   = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_array_out_0  = tag_sram_out_0(CacheCfg.cache_tag_bits - 1, 0)
  protected val tag_array_out_1  = tag_sram_out_1(CacheCfg.cache_tag_bits - 1, 0)

  protected val dirty_array_out_index = Wire(UInt(CacheCfg.cache_line_index_bits.W))
  protected val dirty_array_0  = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))
  protected val dirty_array_1  = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))
  protected val dirty_array_data_out_0  = dirty_array_0(dirty_array_out_index)
  protected val dirty_array_data_out_1  = dirty_array_1(dirty_array_out_index)

  protected val valid_array_out_0 = tag_sram_out_0(CacheCfg.cache_tag_bits)
  protected val valid_array_out_1 = tag_sram_out_0(CacheCfg.cache_tag_bits)

  protected val lru_list = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))

  protected val flush_cnt_en  = curr_state === sFLUSH & axi_finish
  protected val flush_cnt_rst = curr_state === sLOOKUP
  protected val flush_cnt = new Counter(CacheCfg.cache_way * CacheCfg.ram_depth)
  protected val flush_cnt_val = flush_cnt.value
  when(flush_cnt_rst) { flush_cnt.reset() }
  protected val flush_cnt_end = WireInit(false.B)
  when (flush_cnt_en) { flush_cnt_end := flush_cnt.inc() }
  protected val flush_way_num = flush_cnt_val(6) // manual
  protected val flush_line_num = flush_cnt_val(5, 0)
  /*
  Data
  */
  /* stage-1 */
  protected val stage1_in = Wire(Output(chiselTypeOf(io.prev)))
  stage1_in.bits := prev.bits
  stage1_in.ready := DontCare
  stage1_in.valid := prev.valid
  protected val stage1_en = Wire(Bool())
  protected val stage1_out = RegEnable(init = 0.U.asTypeOf(stage1_in),next = stage1_in, enable = stage1_en)
  /* main data reference */
  protected val prev_index     = prev.bits.addr(index_border_up, index_border_down)
  protected val prev_tag       = prev.bits.addr(tag_border_up, tag_border_down)
  protected val stage1_index   = stage1_out.bits.addr(index_border_up, index_border_down)
  protected val stage1_tag     = stage1_out.bits.addr(tag_border_up, tag_border_down)
  protected val flush_out_addr = flush_cnt_val
  protected val flush_out_data = Mux(flush_cnt_val(6), data_array_out_1, data_array_out_0)
  /*
   Base Internal Signal
   */
  /* reference */

  protected val prev_load   = Wire(Bool())
  protected val prev_save   = Wire(Bool())
  protected val prev_flush  = Wire(Bool())
  protected val stage1_load = Wire(Bool())
  protected val stage1_save = Wire(Bool())
  /* control */
  dontTouch(tag_array_out_0)
  dontTouch(tag_array_out_1)
  protected val tag0_hit = (tag_array_out_0 === stage1_tag) & (tag_array_out_0 =/= 0.U)
  protected val tag1_hit = (tag_array_out_1 === stage1_tag) & (tag_array_out_1 =/= 0.U)
  protected val writeback_data = Mux(tag1_hit, data_array_out_1, data_array_out_0)
  protected val flushing = curr_state === sFLUSH
  protected val miss     = !(tag0_hit | tag1_hit)
  protected val next_way = lru_list(stage1_index) === 0.U // 0=0->1 next is 1, 1!=0->0 next is 0
  protected val need_writeback = Mux(next_way, dirty_array_data_out_0, dirty_array_data_out_1).asBool()
  protected val go_on = next_state === sLOOKUP//(curr_state === sLOOKUP) //|
  //(curr_state === sREAD & axi_finish & next.ready) |
  //(curr_state === sEND & next.ready)  | (curr_state === sSAVE)
  /* control */
  stage1_en := go_on
  /* data */
  protected val cache_line_data_out = MuxCase(0.U(CacheCfg.cache_line_bits.W), Array(
    tag0_hit -> data_array_out_0,
    tag1_hit -> data_array_out_1
  ))
  /* Array Load Save */
  protected val array_write = Wire(Bool())//false.B.asTypeOf()
  protected val array_we_index = Wire(UInt(prev_index.getWidth.W))
  protected val array_rd_index = prev_index
  protected val data_array_in  = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_array_in   = Wire(UInt(CacheCfg.cache_tag_bits.W))
  protected val valid_array_in = Wire(UInt(1.W))
  protected val dirty_array_in = Wire(UInt(1.W))//= stage1_save
  protected val save_data = Wire(UInt(128.W))
  array_write:= false.B
  dirty_array_out_index := stage1_index
  array_we_index := -1.S.asUInt()
  valid_array_in := 1.U
  dirty_array_in := stage1_save
  tag_array_in   := stage1_tag
  data_array_in  := stage1_tag
  when(curr_state === sLOOKUP){
    when(prev_flush){
      array_write := true.B
      array_we_index := flush_cnt_val
      valid_array_in := 0.U
      dirty_array_in := 0.U
      tag_array_in := 0.U
      data_array_in := 0.U
    }.elsewhen(/* !miss &*/stage1_save){
      array_write := true.B
      array_we_index := stage1_index
      valid_array_in := 1.U
      dirty_array_in := 1.U
      tag_array_in := stage1_tag
      data_array_in := save_data
    }
  }
    .elsewhen(curr_state === sREAD){
      when(true.B/*axi_finish*/){
        array_write := true.B
        array_we_index := stage1_index
        valid_array_in := 1.U
        dirty_array_in := stage1_save.asUInt()
        tag_array_in := stage1_tag
        when(stage1_save){
          data_array_in := save_data
        }.otherwise{
          data_array_in := axi_rd_data
        }
      }
    }
    .elsewhen(curr_state === sFLUSH){
      array_write := true.B
      array_we_index := flush_cnt_val
      valid_array_in := 0.U
      dirty_array_in := 0.U
      tag_array_in := 0.U
      data_array_in := 0.U
    }
  dontTouch(array_write)
  //  array_write:= (curr_state === sLOOKUP & miss === false.B & stage1_save)
  /*
   AXI ARead AWrite
   */
  axi_rd_en := false.B
  axi_we_en := false.B
  when(curr_state === sFLUSH){ axi_we_en := true.B  }
    .elsewhen(curr_state === sLOOKUP){
      when(prev.bits.flush) { axi_we_en := true.B }
        .elsewhen(miss & (stage1_load | stage1_save)){
          when(need_writeback){ axi_we_en := true.B }
            .otherwise{ axi_rd_en := true.B }
        }
    }
    .elsewhen(curr_state === sRWAIT){ axi_rd_en := true.B }
    .elsewhen(curr_state === sWWAIT){ axi_we_en := true.B }

  axi_addr := MuxCase(stage1_out.bits.addr, Array(
    (curr_state === sLOOKUP) -> stage1_out.bits.addr,
    //    (curr_state === sRWAIT)  -> stage_2_out.bits.addr,
    //    (curr_state === sWWAIT)  -> stage_2_out.bits.addr,
    (curr_state === sFLUSH)  -> flush_out_addr,
  ))
  axi_we_data := MuxCase(stage1_out.bits.wdata, Array(
    (curr_state === sLOOKUP) -> stage1_out.bits.wdata,
    //    (curr_state === sWWAIT)  -> stage_2_out.bits.wdata,
    (curr_state === sFLUSH)  -> flush_out_data,
  ))
  /*
   SRAM
   */

  //  private   val tag_sram_in = Cat(valid_array_in, tag_array_in)

  SRAM.read(data_array_0, data_cen_0, array_rd_index, data_array_out_0)
  SRAM.read(data_array_1, data_cen_1, array_rd_index, data_array_out_1)
  SRAM.read(tag_sram_0,   tag_cen_0,  array_rd_index, tag_sram_out_0)
  SRAM.read(tag_sram_1,   tag_cen_1,  array_rd_index, tag_sram_out_1)
  when(array_write){
    when(next_way){
      SRAM.write(data_array_1, array_we_index, data_array_in, data_array_out_1)
      SRAM.write(tag_sram_1  , array_we_index, tag_array_in , tag_sram_out_1)
      dirty_array_1(array_we_index) := dirty_array_in
    }.otherwise{
      SRAM.write(data_array_0, array_we_index, data_array_in, data_array_out_0)
      SRAM.write(tag_sram_0  , array_we_index, tag_array_in , tag_sram_out_0)
      dirty_array_0(array_we_index) := dirty_array_in
    }
  }

}

class DCacheIn extends DCacheBaseIn {
  override val bits = new Bundle{
    val data = (new EXUOut).bits
    val flush = Output(Bool())
    val wdata = Output(UInt(64.W))
    val wmask = Output(UInt(8.W))
    val size  = Output(new SrcSize)
    val addr  = Output(UInt(CacheCfg.paddr_bits.W))
  }
}
class DCacheOut extends DCacheBaseOut {
  override val bits = new Bundle{
    val data = (new MEMUOut).bits
  }
}
class DCacheUnit extends DCacheBase[DCacheIn, DCacheOut](_in = new DCacheIn, _out = new DCacheOut){
  /*
   Main Control Signal Reference
  */
  prev_load   := prev.bits.data.id2mem.memory_rd_en
  prev_save   := prev.bits.data.id2mem.memory_we_en
  prev_flush  := prev.bits.flush
  stage1_load := stage1_out.bits.data.id2mem.memory_rd_en
  stage1_save := stage1_out.bits.data.id2mem.memory_we_en
  /*
   States Change Rule
  */
  dontTouch(need_writeback)
  next_state := curr_state
  switch(curr_state){
    is(sLOOKUP){
      when(prev_flush)        { next_state := sFLUSH  }
        .elsewhen(!miss)        {//hit
          when(stage1_load)    { next_state := sLOOKUP }
            .elsewhen(stage1_save){ next_state := sSAVE   }
        }.elsewhen(stage1_load | stage1_save){//miss situation
        when(need_writeback){
          when(axi_ready) { next_state := sWRITEBACK } .otherwise { next_state := sWWAIT }
        }.otherwise          {
          when(axi_ready) { next_state := sREAD } .otherwise { next_state := sRWAIT }
        }
      }
    }
    is(sSAVE){ next_state := sLOOKUP }
    is(sRWAIT){ when(axi_ready) { next_state := sREAD } }
    is(sWWAIT){ when(axi_ready) { next_state := sWRITEBACK } }
    is(sREAD){
      when(axi_finish){
        when(next.ready) { next_state := sLOOKUP }
          .otherwise       { next_state := sEND    }
      }
    }
    is(sWRITEBACK){
      when(axi_finish){ next_state := sREAD }
    }
    is(sEND){ when(next.ready)  { next_state := sLOOKUP } }
    is(sFLUSH){ when(flush_cnt_end){ next_state := sLOOKUP } }
  }
  /* data read */
  private val _is_lookup = curr_state === sLOOKUP
  private val read_data_128    = Mux(_is_lookup, cache_line_data_out, axi_rd_data)
  private val read_data_size   = stage1_out.bits.size
  private val read_data_sext   = stage1_out.bits.data.id2mem.sext_flag
  private val start_byte = stage1_out.bits.addr(3, 0)
  private val start_bit =  (start_byte << 3).asUInt()
  private val read_data_64 = (read_data_128 >> start_bit)(63, 0)
  private val raw_read_data = MuxCase(0.U,
    Array(
      read_data_size.byte   -> read_data_64(7,  0),
      read_data_size.hword  -> read_data_64(15, 0),
      read_data_size.word   -> read_data_64(31, 0),
      read_data_size.dword  -> read_data_64,
    )
  )
  private val sext_memory_data = MuxCase(raw_read_data,
    Array(
      read_data_size.byte   -> Sext(data = raw_read_data, pos = 8),
      read_data_size.hword  -> Sext(data = raw_read_data, pos = 16),
      read_data_size.word   -> Sext(data = raw_read_data, pos = 32),
      read_data_size.dword  -> raw_read_data
    )
  )
  private val read_data = Mux(read_data_sext, sext_memory_data, raw_read_data)
  /* save data */
  private val _is_save = curr_state === sSAVE | curr_state === sLOOKUP
  private val _save_data_src   = Mux(_is_save, cache_line_data_out, axi_rd_data)// is_save -> normal save, otherwise is writeback-save
  private val _save_data_token = Mux(_is_save, prev.bits.wdata, stage1_out.bits.data.ex2mem.we_data)
  private val _save_data_size  = Mux(_is_save, prev.bits.size,  stage1_out.bits.size)
  private val _save_data_size_2 = Cat(_save_data_size.dword, _save_data_size.word, _save_data_size.hword, _save_data_size.byte)
  private val _save_start_byte_left = Mux(_is_save, prev.bits.addr(3, 0), stage1_out.bits.addr(3, 0))
  private val _save_start_bit_left  = (_save_start_byte_left << 3).asUInt()
  private val _save_start_bit_right = (_save_data_size_2 << 3).asUInt() + 1.U
  save_data := Replace(_save_data_src, _save_data_token, _save_start_bit_left, _save_start_bit_right)
  dontTouch(save_data)
  /* tag data */
  private val save_tag   = stage1_tag
  /*
   Array Data & Control
  */
  /* data array in */
  data_array_in := MuxCase(axi_rd_data, Array(
    flushing    -> 0.U(128.W),
    (stage1_save) -> save_data,
    (curr_state === sREAD) -> axi_rd_data,
  ))
  tag_array_in := MuxCase(stage1_tag, Array(
    flushing    -> 0.U(128.W),
    (stage1_save) -> stage1_tag,
  ))
  /*
   Output
  */
  prev.ready := go_on//_is_lookup & next.ready

  next.bits.data.id2wb := Mux(go_on, stage1_out.bits.data.id2wb, 0.U.asTypeOf(chiselTypeOf(stage1_out.bits.data.id2wb)))//stage1_out.bits.data.id2wb
  next.bits.data.ex2wb := Mux(go_on, stage1_out.bits.data.ex2wb, 0.U.asTypeOf(chiselTypeOf(stage1_out.bits.data.ex2wb)))//stage1_out.bits.data.ex2wb
  next.valid           := Mux(go_on, stage1_out.valid, false.B)
  next.bits.data.mem2wb.memory_data := read_data
  next.bits.data.mem2wb.test_is_device := DontCare
}