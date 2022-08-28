import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

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
    val maxi = new AXI4Master
    val mmio = new AXI4Master
    val next = _out
  })
  /*
   IO Interface
   */
  protected val prev = io.prev
  protected val memory = io.maxi
  protected val device = io.mmio
  protected val next = io.next
  /*
    Argument
   */
  protected val index_border_up   = 9//CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits// 4 + 5
  protected val index_border_down = 4//CacheCfg.cache_offset_bits//4
  protected val tag_border_up     = 38//CacheCfg.paddr_bits - 1
  protected val tag_border_down   = 10//index_border_up + 1//9 + 1
  /*
   States
   */
  protected val sLOOKUP     = 0.U(4.W)
  protected val sSAVE       = 1.U(4.W)
  protected val sREAD       = 2.U(4.W)
  protected val sRWAIT      = 3.U(4.W)
  protected val sWRITEBACK  = 4.U(4.W)
  protected val sWWAIT      = 5.U(4.W)
  protected val sEND        = 6.U(4.W)
  protected val sFLUSH      = 7.U(4.W)
  protected val sDEV        = 8.U(4.W)
  protected val sDWAIT      = 9.U(4.W)
  //protected val sLOOKUP :: sSAVE :: sREAD :: sRWAIT :: sWRITEBACK :: sWWAIT :: sEND :: sFLUSH :: Nil = Enum(8)
  protected val next_state = Wire(UInt(sLOOKUP.getWidth.W))
  protected val curr_state = RegNext(init = sLOOKUP, next = next_state)
  /*
   AXI Manager and Interface
   */
  /* memory */
  AXI4Master.default(memory)
  val maxi4_manager = Module(new AXI4ManagerLite)
  maxi4_manager.io.maxi <> memory
  val maxi_rd_en    = maxi4_manager.io.in.rd_en
  val maxi_we_en    = maxi4_manager.io.in.we_en
  val maxi_wdata    = maxi4_manager.io.in.data
  val maxi_addr     = maxi4_manager.io.in.addr
  val maxi_rd_data  = maxi4_manager.io.out.data
  val maxi_finish   = maxi4_manager.io.out.finish
  val maxi_ready    = maxi4_manager.io.out.ready
  maxi4_manager.io.in.wmask := "hffff".U
  maxi4_manager.io.in.size := 0.U.asTypeOf(chiselTypeOf(maxi4_manager.io.in.size))
  maxi4_manager.io.in.size.qword := true.B
  /* device */
  AXI4Master.default(device)
  val mmio_manager = Module(new AXI4Manager)
  mmio_manager.io.maxi <> device
  val mmio_rd_en    = mmio_manager.io.in.rd_en
  val mmio_we_en    = mmio_manager.io.in.we_en
  val mmio_wdata    = mmio_manager.io.in.data
  val mmio_addr     = mmio_manager.io.in.addr
  val mmio_wmask    = mmio_manager.io.in.wmask
  val mmio_size     = mmio_manager.io.in.size
  val mmio_rd_data  = mmio_manager.io.out.data
  val mmio_finish   = mmio_manager.io.out.finish
  val mmio_ready    = mmio_manager.io.out.ready
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

  protected val dirty_array_out_0  = tag_sram_out_0(127)
  protected val dirty_array_out_1  = tag_sram_out_1(127)

  protected val valid_array_out_0 = tag_sram_out_0(126)
  protected val valid_array_out_1 = tag_sram_out_1(126)

  protected val lru_list = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))

  protected val flush_cnt_en  = curr_state === sFLUSH & maxi_finish
  protected val flush_cnt_rst = curr_state === sLOOKUP
  protected val flush_cnt = new Counter(CacheCfg.cache_way * CacheCfg.ram_depth)
  protected val flush_cnt_val = flush_cnt.value
  when(flush_cnt_rst) { flush_cnt.reset() }
  protected val flush_cnt_end = WireInit(false.B)
  when (flush_cnt_en) { flush_cnt_end := flush_cnt.inc() }
  protected val flush_way_num  = flush_cnt_val(6) // manual
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
  protected val stage1_clint = Wire(Bool())
  /* control */
  protected val next_way        = !lru_list(stage1_index)// if lru = 0 then next is 1, if lru = 1 then next is 0
  protected val tag0_hit        = (tag_array_out_0 === stage1_tag) & (tag_array_out_0 =/= 0.U)
  protected val tag1_hit        = (tag_array_out_1 === stage1_tag) & (tag_array_out_1 =/= 0.U)
  protected val hit_reg         = RegEnable(next = tag1_hit,enable = curr_state === sLOOKUP)
  protected val writeback_data  = Mux(next_way, data_array_out_1, data_array_out_0)
  protected val addr_array_0    = Cat(tag_array_out_0, stage1_index, stage1_out.bits.addr(3, 0))(31, 0)
  protected val addr_array_1    = Cat(tag_array_out_1, stage1_index, stage1_out.bits.addr(3, 0))(31, 0)
  protected val writeback_addr  = Mux(next_way, addr_array_1, addr_array_0)
  protected val flushing        = (flush_cnt_val =/= 0.U)
  protected val miss            = !(tag0_hit | tag1_hit)
  protected val addr_underflow  = stage1_out.bits.addr(31) === 0.U(1.W)// addr < 0x8000_000
  //printf(p"underflow ${addr_underflow}, addr(1) ${stage1_out.bits.addr(31)}, addr ${stage1_out.bits.addr}\n")
  protected val need_writeback = Mux(next_way, dirty_array_out_1, dirty_array_out_0).asBool()
  protected val go_on = next_state === sLOOKUP
  dontTouch(next_way)
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
  protected val array_rd_index = Wire(UInt(prev_index.getWidth.W))
  protected val data_array_in  = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_array_in   = Wire(UInt(CacheCfg.ram_width.W))
  protected val valid_array_in = Wire(UInt(1.W))
  protected val dirty_array_in = Wire(UInt(1.W))//= stage1_save
  protected val tag_sram_in = Cat(dirty_array_in, valid_array_in , tag_array_in(CacheCfg.ram_width-2, 0))
  protected val save_data = Wire(UInt(128.W))
  /*
   AXI ARead AWrite
   */
  maxi_rd_en := false.B
  maxi_we_en := false.B
  when(curr_state === sFLUSH){ maxi_we_en := true.B  }
  .elsewhen(curr_state === sLOOKUP){
      when(prev.bits.flush) { maxi_we_en := true.B }
      .elsewhen(addr_underflow) {
          maxi_we_en := false.B
          maxi_rd_en := false.B
      }.elsewhen(stage1_load | stage1_save){
          when(need_writeback & miss){ maxi_we_en := true.B }
          .elsewhen(miss){ maxi_rd_en := true.B }
      }
    }
    .elsewhen(curr_state === sRWAIT){ maxi_rd_en := true.B }
    .elsewhen(curr_state === sWWAIT){ maxi_we_en := true.B }
  maxi_addr := Cat(MuxCase(stage1_out.bits.addr, Array(
    (curr_state === sLOOKUP & (!need_writeback)) -> stage1_out.bits.addr,
    (curr_state === sLOOKUP & (need_writeback)) -> writeback_addr,
    (curr_state === sFLUSH)  -> flush_out_addr,
  ))(38, 4), 0.U(4.W))
  maxi_wdata := MuxCase(stage1_out.bits.wdata, Array(
    (curr_state === sLOOKUP & (!need_writeback)) -> stage1_out.bits.wdata,
    (curr_state === sLOOKUP & (need_writeback)) -> writeback_data,
    (curr_state === sFLUSH)  -> flush_out_data,
  ))
  mmio_rd_en := false.B
  mmio_we_en := false.B
  when(curr_state === sLOOKUP & addr_underflow){
    when(stage1_save)       { mmio_we_en := true.B }
    .elsewhen(stage1_load)  { mmio_rd_en := true.B }
  }
  .elsewhen(curr_state === sDWAIT){
      when(stage1_save)       { mmio_we_en := true.B }
      .elsewhen(stage1_load)  { mmio_rd_en := true.B }
  }
  mmio_addr := stage1_out.bits.addr
  mmio_wdata := stage1_out.bits.wdata
  mmio_wmask := stage1_out.bits.wmask
  mmio_size.qword := false.B
  mmio_size.byte  := stage1_out.bits.size.byte
  mmio_size.hword := stage1_out.bits.size.hword
  mmio_size.word  := stage1_out.bits.size.word
  mmio_size.dword := stage1_out.bits.size.dword
  /*
   SRAM
   */
  SRAM.read(data_array_0, data_cen_0, array_rd_index, data_array_out_0)
  SRAM.read(data_array_1, data_cen_1, array_rd_index, data_array_out_1)
  SRAM.read(tag_sram_0,   tag_cen_0,  array_rd_index, tag_sram_out_0)
  SRAM.read(tag_sram_1,   tag_cen_1,  array_rd_index, tag_sram_out_1)
  when(array_write){
    when(curr_state === sREAD){//writeback
      when(next_way){
        lru_list(array_we_index) := 1.U//last is 1
        SRAM.write(data_array_1, addr = array_we_index, data_array_in, data_array_out_1)
        SRAM.write(tag_sram_1  , addr = array_we_index, tag_sram_in , tag_sram_out_1)
      }.otherwise{
        lru_list(array_we_index) := 0.U//last is 0
        SRAM.write(data_array_0, addr = array_we_index, data_array_in, data_array_out_0)
        SRAM.write(tag_sram_0  , addr = array_we_index, tag_sram_in  , tag_sram_out_0)
      }
    }
    .elsewhen(curr_state === sFLUSH | prev_flush){//flush
        when(flush_way_num){
          lru_list(flush_line_num) := 1.U//last is 1
          SRAM.write(data_array_1, flush_line_num, 0.U, data_array_out_1)
          SRAM.write(tag_sram_1  , flush_line_num, 0.U, tag_sram_out_1)
        }.otherwise{
          lru_list(flush_line_num) := 0.U//last is 0
          SRAM.write(data_array_0, flush_line_num, 0.U, data_array_out_0)
          SRAM.write(tag_sram_0  , flush_line_num, 0.U, tag_sram_out_0)
        }
      }
    .otherwise{//normal miss
        when(hit_reg === 0.U){
          lru_list(array_we_index) := 0.U//last is 0
          SRAM.write(data_array_0, array_we_index, data_array_in, data_array_out_0)
          SRAM.write(tag_sram_0  , array_we_index, tag_sram_in  , tag_sram_out_0)
        }.otherwise{
          lru_list(array_we_index) := 1.U//last is 1
          SRAM.write(data_array_1, array_we_index, data_array_in, data_array_out_1)
          SRAM.write(tag_sram_1  , array_we_index, tag_sram_in  , tag_sram_out_1)
        }
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
  stage1_clint:= stage1_out.bits.data.ex2mem.addr(31, 16) === "h0200".U & (stage1_out.bits.data.id2mem.memory_we_en | stage1_out.bits.data.id2mem.memory_we_en)
  /*
   States Change Rule
  */
  dontTouch(need_writeback)
  next_state := curr_state
  switch(curr_state){
    is(sLOOKUP){
      when(prev_flush)        { next_state := sFLUSH  }
        .elsewhen(stage1_load | stage1_save){
          when(addr_underflow) {
            when(mmio_ready) { next_state := sDEV } .otherwise { next_state := sDWAIT }
          }.elsewhen(need_writeback & miss){
            when(maxi_ready) { next_state := sWRITEBACK } .otherwise { next_state := sWWAIT }
          }.elsewhen(miss){
            when(maxi_ready) { next_state := sREAD } .otherwise { next_state := sRWAIT }
          }.elsewhen(stage1_load){ next_state := sLOOKUP
          }.elsewhen(stage1_save){ next_state := sSAVE }
        }
    }
    is(sSAVE){ next_state := sEND }
    is(sRWAIT){ when(maxi_ready) { next_state := sREAD } }
    is(sWWAIT){ when(maxi_ready) { next_state := sWRITEBACK } }
    is(sDWAIT){ when(maxi_ready) { next_state := sDWAIT } }
    is(sREAD){
      when(maxi_finish){
        when(next.ready) { next_state := sEND    }
          .otherwise       { next_state := sEND    }
      }
    }
    is(sDEV){
      when(mmio_finish){
        when(next.ready) { next_state := sEND    }
          .otherwise       { next_state := sEND    }
      }
    }
    is(sWRITEBACK){
      when(maxi_finish){ next_state := sRWAIT }
    }
    is(sEND){ when(next.ready)  { next_state := sLOOKUP } }
    is(sFLUSH){ when(flush_cnt_end){ next_state := sLOOKUP } }
  }
  /* data read */
  private val _is_lookup = curr_state === sLOOKUP
  private val start_byte    = stage1_out.bits.addr(3, 0)
  private val start_bit     =  (start_byte << 3).asUInt()
  private val read_data_128 = MuxCase(maxi_rd_data, Array(
    _is_lookup -> cache_line_data_out,// cache line data out is 16-bytes aligned
    addr_underflow -> (mmio_rd_data << start_bit).asUInt(),
  ))
  private val read_data_size   = stage1_out.bits.size
  private val read_data_sext   = stage1_out.bits.data.id2mem.sext_flag

  private val read_data_64  = (read_data_128 >> start_bit)(63, 0)
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
  private val _is_save             = curr_state === sSAVE | curr_state === sLOOKUP
  private val save_data_src        = Mux(_is_save, cache_line_data_out, maxi_rd_data)// is_save -> normal save, otherwise is writeback-save
  private val save_data_token      = MuxCase(stage1_out.bits.data.ex2mem.we_data,
    Array(
      read_data_size.byte   -> stage1_out.bits.data.ex2mem.we_data(7,  0),
      read_data_size.hword  -> stage1_out.bits.data.ex2mem.we_data(15, 0),
      read_data_size.word   -> stage1_out.bits.data.ex2mem.we_data(31, 0),
      read_data_size.dword  -> stage1_out.bits.data.ex2mem.we_data(63, 0),
    )
  )
  private val save_data_size       = stage1_out.bits.size
  private val save_data_size_2     = Cat(0.U(1.W), save_data_size.dword, save_data_size.word, save_data_size.hword, save_data_size.byte)
  private val save_start_byte_rshift = stage1_out.bits.addr(3, 0)
  private val save_start_bit_rshift  = ((save_start_byte_rshift + save_data_size_2)<< 3).asUInt()
  private val save_start_bit_lshift = 128.U - (save_start_byte_rshift << 3).asUInt()
  private val save_start_bit_lshift2 = (save_start_byte_rshift << 3).asUInt()
  private val save_data_inserted = Replace(src = save_data_src,token = save_data_token,rshift = save_start_bit_rshift,lshift = save_start_bit_lshift, lshift2 = save_start_bit_lshift2)
  save_data := Mux(stage1_save, save_data_inserted, maxi_rd_data)
  dontTouch(save_data)
  /*
   Array Data & Control
  */
  array_write := (curr_state === sSAVE) | (curr_state === sREAD & maxi_finish) | (curr_state === sFLUSH)
  array_rd_index := MuxCase(stage1_index, Array(
    (curr_state === sSAVE)   -> stage1_index,
    (next_state === sLOOKUP) -> prev_index,
    (next_state === sEND)    -> prev_index,
  ))
  array_we_index := MuxCase(stage1_index, Array(
    (curr_state === sFLUSH | prev_flush) -> flush_cnt_val,
    (curr_state === sSAVE) -> stage1_index,
    (curr_state === sREAD) -> stage1_index,
  ))
  data_array_in := MuxCase(save_data, Array(
    (curr_state === sFLUSH | prev_flush) -> 0.U(128.W),
    (curr_state === sSAVE) -> save_data,
    (curr_state === sREAD) -> save_data,
  ))
  tag_array_in := MuxCase(stage1_tag, Array(
    (curr_state === sFLUSH | prev_flush) -> 0.U(128.W),
    (curr_state === sSAVE) -> stage1_tag,
    (curr_state === sREAD) -> stage1_tag,
  ))
  valid_array_in := MuxCase(0.U(1.W), Array(
    (curr_state === sFLUSH | prev_flush) -> 0.U(1.W),
    (curr_state === sSAVE) -> 1.U(1.W),
    (curr_state === sREAD) -> 1.U(1.W),
  ))
  dirty_array_in := MuxCase(0.U(1.W), Array(
    (curr_state === sFLUSH | prev_flush) -> 0.U(1.W),
    (curr_state === sSAVE) -> 1.U(1.W),
    (curr_state === sREAD & stage1_load) -> 1.U(1.W),
    (curr_state === sREAD & (!stage1_load)) -> 0.U(1.W),
  ))
  /*
   Output
  */
  prev.ready := go_on//_is_lookup & next.ready

  next.bits.data.id2wb := Mux(go_on, stage1_out.bits.data.id2wb, 0.U.asTypeOf(chiselTypeOf(stage1_out.bits.data.id2wb)))//stage1_out.bits.data.id2wb
  next.bits.data.ex2wb := Mux(go_on, stage1_out.bits.data.ex2wb, 0.U.asTypeOf(chiselTypeOf(stage1_out.bits.data.ex2wb)))//stage1_out.bits.data.ex2wb
  next.valid           := Mux(go_on, stage1_out.valid, false.B)
  next.bits.data.mem2wb.memory_data := read_data
  //icache reset
  /*
   Hit Collection
  */
  if(SparkConfig.CacheHitCount){
    val load_cnt = RegInit(0.U(128.W))
    val save_cnt = RegInit(0.U(128.W))
    val way0_load_hit_cnt = RegInit(0.U(128.W))
    val way0_save_hit_cnt = RegInit(0.U(128.W))
    val way1_load_hit_cnt = RegInit(0.U(128.W))
    val way1_save_hit_cnt = RegInit(0.U(128.W))
    when(curr_state === sLOOKUP & (!addr_underflow)){
      when(stage1_load){
        load_cnt := load_cnt + 1.U
      }
      when(stage1_load & tag0_hit){
        way0_load_hit_cnt := way0_load_hit_cnt + 1.U
      }
      when(stage1_load & tag1_hit){
        way1_load_hit_cnt := way1_load_hit_cnt + 1.U
      }
    }
    when(curr_state === sLOOKUP & (!addr_underflow)){
      when(stage1_save){
        save_cnt := save_cnt + 1.U
      }
      when(stage1_save & tag0_hit & next_state === sSAVE){
        way0_save_hit_cnt := way0_save_hit_cnt + 1.U
      }
      when(stage1_save& tag1_hit & next_state === sSAVE){
        way1_save_hit_cnt := way1_save_hit_cnt + 1.U
      }
    }
    val ebreak = WireDefault(false.B)
    BoringUtils.addSource(ebreak, "icache_count_print")
    when(next.bits.data.id2wb.ebreak){
      printf("--------------------DCache Hit Table-------------------------\n")
      printf(p" way 0 hit number    :    ${(way0_load_hit_cnt + way0_save_hit_cnt)}\n")
      printf(p" way 1 hit number    :    ${(way1_load_hit_cnt + way1_save_hit_cnt)}\n")
      printf(p" total hit number    :    ${(way0_load_hit_cnt + way0_save_hit_cnt + way1_load_hit_cnt + way1_save_hit_cnt)}\n")
      printf(p" Total cache hit rate: ${(100.U * (way0_load_hit_cnt + way0_save_hit_cnt + way1_load_hit_cnt + way1_save_hit_cnt))/(load_cnt + save_cnt)}%\n")
      printf("------------------------------------------------------------\n")
      printf(p" way0 cache hit rate : ${(100.U * (way0_load_hit_cnt + way0_save_hit_cnt))/(load_cnt + save_cnt)}%\n")
      printf(p" way0 load proportion: ${(100.U * (way0_load_hit_cnt))/(way0_load_hit_cnt + way1_load_hit_cnt)}%\n")
      printf(p" way0 save proportion: ${(100.U * (way0_save_hit_cnt))/(way0_save_hit_cnt + way1_save_hit_cnt)}%\n")
      printf("------------------------------------------------------------\n")
      printf(p" way1 cache hit rate : ${(100.U * (way1_load_hit_cnt + way1_save_hit_cnt))/(load_cnt + save_cnt)}%\n")
      printf(p" way1 load proportion: ${(100.U * (way1_load_hit_cnt))/(way0_load_hit_cnt + way1_load_hit_cnt)}%\n")
      printf(p" way1 save proportion: ${(100.U * (way1_save_hit_cnt))/(way0_save_hit_cnt + way1_save_hit_cnt)}%\n")
      printf("------------------------------------------------------------\n")
      ebreak := true.B
    }
    if(!SparkConfig.Debug){
      next.bits.data.mem2wb.test_is_device := DontCare
    }
    else{
      next.bits.data.mem2wb.test_is_device := Mux(go_on,stage1_out.valid & addr_underflow & (stage1_load | stage1_save), false.B)
    }

  }
}