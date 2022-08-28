//import chisel3._
//import chisel3.util._
//import chisel3.util.experimental.BoringUtils
//
//class CacheIn extends MyDecoupledIO {
//  override val bits = new Bundle {
//    val addr = Output(UInt(CacheCfg.paddr_bits.W))
//  }
//}
//class CacheOut extends MyDecoupledIO {
//  override val bits = new Bundle{
//    val data = (new IFUOut).bits
//  }
//}
//
//class ICacheBase[IN <: CacheIn, OUT <: CacheOut] (_in: IN, _out: OUT) extends Module {
//  val io = IO(new Bundle {
//    val cache_reset = Input(Bool())
//    val prev = Flipped(_in)
//    val maxi = new AXI4Master
//    val next = _out
//  })
//  /*
//   IO Interface
//   */
//  protected val prev = io.prev
//  protected val memory = io.maxi
//  protected val next = io.next
//  /*
//    Argument
//   */
//  protected val index_border_up   = 9//CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits// 4 + 5
//  protected val index_border_down = 4//CacheCfg.cache_offset_bits//4
//  protected val tag_border_up     = 38//CacheCfg.paddr_bits - 1
//  protected val tag_border_down   = 10//index_border_up + 1//9 + 1
//  /*
//   States
//   */
//  protected val sLOOKUP     = 1.U(3.W)
//  protected val sREAD       = 2.U(3.W)
//  protected val sRWAIT      = 3.U(3.W)
//  protected val sEND        = 4.U(3.W)
//
//  protected val next_state = Wire(UInt(sLOOKUP.getWidth.W))
//  protected val curr_state = RegNext(init = sLOOKUP, next = next_state)
//  /*
//   AXI Manager and Interface
//   */
//  /* memory */
//  AXI4Master.default(memory)
//  val maxi4_manager = Module(new AXI4ManagerLite)
//  maxi4_manager.io.maxi <> memory
//  val maxi_rd_en    = maxi4_manager.io.in.rd_en
//  maxi4_manager.io.in.we_en := false.B
//  maxi4_manager.io.in.data := 0.U(AXI4Parameters.dataBits.W)
//  val maxi_addr     = maxi4_manager.io.in.addr
//  val maxi_rd_data  = maxi4_manager.io.out.data
//  val maxi_finish   = maxi4_manager.io.out.finish
//  val maxi_ready    = maxi4_manager.io.out.ready
//  maxi4_manager.io.in.wmask := 0.U
//  maxi4_manager.io.in.size := 0.U.asTypeOf(chiselTypeOf(maxi4_manager.io.in.size))
//  maxi4_manager.io.in.size.qword := true.B
//  /*
//   Array Signal
//   */
//  protected val data_cen_0 = false.B//Wire(Bool())
//  protected val data_cen_1 = false.B//Wire(Bool())
//  protected val data_array_0 = SRAM()
//  protected val data_array_1 = SRAM()
//  protected val data_array_out_0 = Wire(UInt(CacheCfg.ram_width.W))
//  protected val data_array_out_1 = Wire(UInt(CacheCfg.ram_width.W))
//
//  protected val tag_cen_0 = false.B//Wire(Bool())
//  protected val tag_cen_1 = false.B//Wire(Bool())
//  protected val tag_sram_0 = SRAM()
//  protected val tag_sram_1 = SRAM()
//  protected val tag_sram_out_0   = Wire(UInt(CacheCfg.ram_width.W))
//  protected val tag_sram_out_1   = Wire(UInt(CacheCfg.ram_width.W))
//  protected val tag_array_out_0  = tag_sram_out_0(CacheCfg.cache_tag_bits - 1, 0)
//  protected val tag_array_out_1  = tag_sram_out_1(CacheCfg.cache_tag_bits - 1, 0)
//
//  protected val lru_list = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))
//
//  /*
//  Data
//  */
//  /* stage-1 */
//  protected val stage1_in = Wire(Output(chiselTypeOf(io.prev)))
//  stage1_in.bits := prev.bits
//  stage1_in.ready := DontCare
//  stage1_in.valid := prev.valid
//  dontTouch(stage1_in)
//  protected val stage1_en = Wire(Bool())
//  protected val stage1_out = RegEnable(init = SparkConfig.StartAddr.asTypeOf(stage1_in),next = stage1_in, enable = stage1_en)
//  /* main data reference */
//  protected val prev_index     = prev.bits.addr(index_border_up, index_border_down)
//  protected val prev_tag       = prev.bits.addr(tag_border_up, tag_border_down)
//  protected val stage1_index   = stage1_out.bits.addr(index_border_up, index_border_down)
//  protected val stage1_tag     = stage1_out.bits.addr(tag_border_up, tag_border_down)
//  /*
//   Base Internal Signal
//   */
//  /* control */
//  protected val next_way        = !lru_list(stage1_index)// if lru = 0 then next is 1, if lru = 1 then next is 0
//  protected val tag0_hit        = (tag_array_out_0 === stage1_tag) & (tag_array_out_0 =/= 0.U)
//  protected val tag1_hit        = (tag_array_out_1 === stage1_tag) & (tag_array_out_1 =/= 0.U)
//  protected val miss            = !(tag0_hit | tag1_hit)
//  protected val go_on           = next_state === sLOOKUP & next.ready
//  dontTouch(next_way)
//  protected val valid_array_reset = WireDefault(false.B)
//  BoringUtils.addSink(valid_array_reset, "fencei")
//  /* control */
//  stage1_en := go_on
//  /* data */
//  protected val cache_line_data_out = MuxCase(0.U(CacheCfg.cache_line_bits.W), Array(
//    tag0_hit -> data_array_out_0,
//    tag1_hit -> data_array_out_1
//  ))
//  /* Array Load Save */
//  protected val array_write = Wire(Bool())//false.B.asTypeOf()
//  protected val array_we_index = Wire(UInt(prev_index.getWidth.W))
//  protected val array_rd_index = Wire(UInt(prev_index.getWidth.W))
//  protected val data_array_in  = Wire(UInt(CacheCfg.ram_width.W))
//  protected val tag_array_in   = Wire(UInt(CacheCfg.ram_width.W))
////  protected val tag_sram_in    = Cat(dirty_array_in, valid_array_in , tag_array_in(CacheCfg.ram_width-2, 0))
//  /*
//   AXI ARead AWrite
//   */
//  maxi_rd_en := false.B
//  switch(curr_state) {
//    is(sLOOKUP) {
//      when(!prev.valid | reset.asBool()) {
//        maxi_rd_en := false.B
//      }.elsewhen(miss) {
//        maxi_rd_en := true.B
//      }
//    }
//    is(sRWAIT) {
//        maxi_rd_en := true.B
//    }
//  }
//
//
//  maxi_addr := Cat(stage1_out.bits.addr(38, 4), 0.U(4.W))
//  /*
//   SRAM
//   */
//  SRAM.read(data_array_0, data_cen_0, array_rd_index, data_array_out_0)
//  SRAM.read(data_array_1, data_cen_1, array_rd_index, data_array_out_1)
//  SRAM.read(tag_sram_0,   tag_cen_0,  array_rd_index, tag_sram_out_0)
//  SRAM.read(tag_sram_1,   tag_cen_1,  array_rd_index, tag_sram_out_1)
//  when(array_write){
//    when(curr_state === sREAD){//writeback
//      when(next_way){
//        lru_list(array_we_index) := 1.U//last is 1
//        SRAM.write(data_array_1, addr = array_we_index, data_array_in, data_array_out_1)
//        SRAM.write(tag_sram_1  , addr = array_we_index, tag_array_in , tag_sram_out_1)
//      }.otherwise{
//        lru_list(array_we_index) := 0.U//last is 0
//        SRAM.write(data_array_0, addr = array_we_index, data_array_in, data_array_out_0)
//        SRAM.write(tag_sram_0  , addr = array_we_index, tag_array_in  , tag_sram_out_0)
//      }
//    }
//  }
//
//}
//
//
//class ICache extends ICacheBase[CacheIn, CacheOut](_in = new CacheIn, _out = new CacheOut){
//  /*
//   States Change Rule
//  */
//  next_state := curr_state
//  switch(curr_state){
//    is(sLOOKUP){
//      when(!prev.valid){
//        next_state := sLOOKUP
//      }.elsewhen(miss){
//          when(maxi_ready) {
//              next_state := sREAD
//          }.otherwise {
//              next_state := sRWAIT
//          }
//      }
//    }
//    is(sRWAIT){
//      when(maxi_ready) {
//        next_state := sREAD
//      }
//    }
//    is(sREAD){
//      when(maxi_finish){
//          when(next.ready) {
//            next_state := sEND
//        }.otherwise{
//            next_state := sEND
//        }
//      }
//    }
//    is(sEND){
//      when(next.ready)  {
//        next_state := sLOOKUP
//      }
//    }
//  }
//  /* data read */
//  /* data read */
//  private val _is_lookup = curr_state === sLOOKUP
//  private val start_byte    = stage1_out.bits.addr(3, 0)
//  private val start_bit     =  (start_byte << 3).asUInt()
//  private val read_data_128 = MuxCase(maxi_rd_data, Array(
//    _is_lookup -> cache_line_data_out,// cache line data out is 16-bytes aligned
//  ))
//  private val read_data = (read_data_128 >> start_bit)(31, 0)
//  /*
//   Array Data & Control
//  */
//  array_write := curr_state === sREAD & maxi_finish
//  array_rd_index := MuxCase(stage1_index, Array(
//    (next_state === sLOOKUP) -> prev_index,
//    (next_state === sEND)    -> prev_index,
//  ))
//  array_we_index := MuxCase(stage1_index, Array(
//    (curr_state === sREAD) -> stage1_index,
//  ))
//  data_array_in := maxi_rd_data
//  tag_array_in  := stage1_tag
//  /*
//   Output
//  */
//  prev.ready := go_on //_is_lookup & next.ready
//  next.bits.data.if2id.pc   := Mux(go_on, stage1_out.bits.addr, 0.U)
//  next.valid                := Mux(go_on, stage1_out.valid, false.B)
//  next.bits.data.if2id.inst := read_data
//  //icache reset
//  when(io.cache_reset){
//    curr_state := sLOOKUP
//    next_state := sLOOKUP
//    stage1_out := 0.U.asTypeOf(stage1_in)
//  }
//  /*
//    Debug
//   */
////  if(SparkConfig.Debug){
////    val test_valid_array_0 = Wire(UInt(CacheCfg.ram_depth.W))
////    test_valid_array_0 := valid_array_0.asUInt()
////    val test_valid_array_1 = Wire(UInt(CacheCfg.ram_depth.W))
////    test_valid_array_1 := valid_array_1.asUInt()
////    dontTouch(test_valid_array_0)
////    dontTouch(test_valid_array_1)
////  }
//  /*
//   Hit Collection
//  */
//  if(SparkConfig.CacheHitCount){
//    val way0_hit_cnt = RegInit(0.U(128.W))
//    val way1_hit_cnt = RegInit(0.U(128.W))
//    val miss_cnt = RegInit(0.U(128.W))
//    val pc_old = Reg(UInt(64.W))
//    pc_old := prev.bits.addr
//    when(curr_state === sLOOKUP & (pc_old =/= prev.bits.addr)){
//      when(tag0_hit ){
//        way0_hit_cnt := way0_hit_cnt + 1.U
//      }.elsewhen(tag1_hit){
//        way1_hit_cnt := way1_hit_cnt + 1.U
//      }.otherwise{
//        miss_cnt := miss_cnt + 1.U
//      }
//    }
//    val ebreak = WireDefault(false.B)
//    BoringUtils.addSink(ebreak, "icache_count_print")
//    when(ebreak){
//      printf("--------------------ICache Hit Table-------------------------\n")
//      printf(p" way 0 hit number      :    ${way0_hit_cnt}\n")
//      printf(p" way 1 hit number      :    ${way1_hit_cnt}\n")
//      printf(p" total hit number      :    ${way0_hit_cnt + way1_hit_cnt}\n")
//      printf(p" miss number           :    ${miss_cnt}\n")
//      printf(p" total access          :    ${way0_hit_cnt + way1_hit_cnt + miss_cnt}\n")
//      printf(p" Total cache hit rate  : ${(100.U * (way0_hit_cnt + way1_hit_cnt))/(miss_cnt + way0_hit_cnt + way1_hit_cnt)}%\n")
//      printf("------------------------------------------------------------\n")
//    }
//  }
//
//
//}