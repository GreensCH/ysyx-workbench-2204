import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils



class ICacheIn extends MyDecoupledIO {
  override val bits = new Bundle {
    val addr = Output(UInt(CacheCfg.paddr_bits.W))
  }
}
class ICacheOut extends MyDecoupledIO {
  override val bits = new Bundle{
    val data = (new IFUOut).bits
  }
}
class CacheBase[IN <: ICacheIn, OUT <: ICacheOut] (_in: IN, _out: OUT) extends Module {
  val io = IO(new Bundle {
    val cache_reset = Input(Bool())
    val prev = Flipped(_in)
    val maxi = new AXI4Master
    val next = _out
  })
  /*
   IO Interface
   */
  protected val prev = io.prev
  protected val memory = io.maxi
  protected val next = io.next
  /*
   Cache Manual Argument
   */
  protected val index_border_up   = 9//CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits - 1
  protected val index_border_down = 4//CacheCfg.cache_offset_bits
  protected val tag_border_up     = 38//CacheCfg.paddr_bits - 1
  protected val tag_border_down   = 10//CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits
  /*
   States
   */
  protected val sLOOKUP  = 0.U(3.W)
  protected val sREAD    = 1.U(3.W)
  protected val sALLOC   = 2.U(3.W)// allocation
  protected val sWRITE   = 3.U(3.W)
  protected val sEND     = 4.U(3.W)
  protected val next_state = Wire(UInt(sLOOKUP.getWidth.W))
  protected val curr_state = RegNext(init = sLOOKUP, next = next_state)
  /*
   AXI Interface Default Connection(Read-Only)
   */
  AXI4BundleA.clear(memory.ar)
  AXI4BundleR.default(memory.r)
  memory.aw <> 0.U.asTypeOf(new AXI4BundleA) // useless port use default signal
  memory.w <> 0.U.asTypeOf(new AXI4BundleW)
  memory.b <> 0.U.asTypeOf(new AXI4BundleB)
  /*
   SRAM & SRAM Signal
   */
  protected val data_cen_0 = Wire(Bool())
  protected val data_cen_1 = Wire(Bool())
  protected val tag_cen_0 = Wire(Bool())
  protected val tag_cen_1 = Wire(Bool())
  protected val data_array_0 = SRAM()
  protected val data_array_1 = SRAM()
  protected val tag_array_0 = SRAM()
  protected val tag_array_1 = SRAM()
  protected val valid_array_0 = RegInit(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W))))
  protected val valid_array_1 = RegInit(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W))))
  protected val valid_array_0_out = Wire(Bool())
  protected val valid_array_1_out = Wire(Bool())
  protected val data_rdata_out_0 = Wire(UInt(CacheCfg.ram_width.W))
  protected val data_rdata_out_1 = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_rdata_out_0 = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_rdata_out_1 = Wire(UInt(CacheCfg.ram_width.W))
  protected val lru_list = Reg(chiselTypeOf(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))))
  /*
  Stage
 */
  /* Lookup Stage */
  protected val lkup_stage_en = Wire(Bool())
  protected val lkup_stage_in = Wire(Output(chiselTypeOf(io.prev)))
  protected val lkup_stage_out = RegEnable(init = 0.U.asTypeOf(lkup_stage_in),next = lkup_stage_in, enable = lkup_stage_en)
  /* AXI Read Channel Stage */
  protected val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
  protected val r_stage_out = RegNext(init = 0.U(AXI4Parameters.dataBits.W), next = r_stage_in)
  /*
   Main Data Reference
   */
  protected val prev_index  = prev.bits.addr(index_border_up, index_border_down)//(8, 4)
  protected val prev_tag    = prev.bits.addr(tag_border_up, tag_border_down)
  protected val stage_index = lkup_stage_out.bits.addr(index_border_up, index_border_down)
  protected val stage_tag   = lkup_stage_out.bits.addr(tag_border_up, tag_border_down)
  /*
   Main Internal Control Signal
   */
  protected val r_okay = (AXI4Parameters.RESP_OKAY === memory.r.bits.resp) & memory.r.valid
  protected val r_last = memory.r.bits.last  & memory.r.valid
  protected val r_data = memory.r.bits.data
  protected val tag0_hit = (tag_rdata_out_0 === stage_tag) & (tag_rdata_out_0 =/= 0.U)
  protected val tag1_hit = (tag_rdata_out_1 === stage_tag) & (tag_rdata_out_1 =/= 0.U)
  protected val miss1 = (!(tag0_hit | tag1_hit))
  protected val miss2 = (tag0_hit & !valid_array_0_out)
  protected val miss3 = (tag1_hit & !valid_array_1_out)
  protected val miss = miss1
  dontTouch(miss1)
  dontTouch(miss2)
  dontTouch(miss3)
  protected val valid_array_reset = WireDefault(false.B)
  BoringUtils.addSink(valid_array_reset, "fencei")
  /*
   Main Internal Data Signal
   */
  protected val a_addr = Wire(UInt(AXI4Parameters.addrBits.W))
  r_stage_in := Mux(curr_state === sREAD & !r_last, memory.r.bits.data, r_stage_out)
  protected val bus_rdata_out = Cat(memory.r.bits.data, r_stage_out)//cat(64, 64) -> total out 128 bits
  protected val cache_line_data_out = MuxCase(0.U(CacheCfg.cache_line_bits.W), Array(
    tag0_hit -> data_rdata_out_0,
    tag1_hit -> data_rdata_out_1
  ))
  /*
   AXI ARead AWrite
   */
  when(curr_state === sLOOKUP & next_state === sREAD){
    AXI4BundleA.set(inf = memory.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = 1.U)
  }
  .otherwise{
    AXI4BundleA.clear(memory.ar)
  }
}

class ICache extends CacheBase[ICacheIn, ICacheOut](_in = new ICacheIn, _out = new ICacheOut){
  /*
   Internal Control Signal
  */
//  private val allocation = (curr_state === sREAD) & r_last
  private val ar_waiting = (curr_state === sLOOKUP) & miss & (!memory.ar.ready)
  dontTouch(ar_waiting)
  lkup_stage_en := next_state === sLOOKUP
  data_cen_0 := !next.ready  // If next isn't ready, then lock the sram output
  data_cen_1 := !next.ready
  tag_cen_0  := !next.ready
  tag_cen_1  := !next.ready
  /*
   States Change Rule
   */
  next_state := sLOOKUP //switch default
  switch(curr_state){
    is(sLOOKUP){
      when(!prev.valid){ next_state := sLOOKUP }
      .elsewhen(!memory.ar.ready){ next_state := sLOOKUP }// cannot transfer
      .elsewhen(miss & lkup_stage_out.valid)  { next_state := sREAD   }
      .otherwise {next_state := sLOOKUP}
    }
    is(sREAD){
      when(r_last) { next_state := sALLOC }
      .otherwise   { next_state := sREAD  }
    }
    is(sALLOC){
      when(next.ready) { next_state := sLOOKUP }
      .otherwise  { next_state := sEND }//can delete this way, and directly be sLOOKUP
    }
    is(sEND){
      when(next.ready) { next_state := sLOOKUP }
      .otherwise  { next_state := sEND }//can delete this way, and directly be sLOOKUP
    }
  }
  /*
   Internal Data Signal
   */
  a_addr := Cat(lkup_stage_out.bits.addr(38, 4), 0.U(4.W))// axi read addr
  lkup_stage_in.bits.addr := prev.bits.addr
  lkup_stage_in.valid := prev.valid
  lkup_stage_in.ready := DontCare
  /*
   SRAM LRU
   */
  when((curr_state === sREAD) & r_last/*allocation*/){
    when(lru_list(stage_index) === 0.U){// last is 0
      lru_list(stage_index) := 1.U//now the last is 1
      SRAM.write(data_array_0, data_rdata_out_0)
      SRAM.write(tag_array_0 , tag_rdata_out_0)
      SRAM.write(data_array_1, stage_index, bus_rdata_out, data_rdata_out_1)
      SRAM.write(tag_array_1 , stage_index, stage_tag, tag_rdata_out_1)
      valid_array_1(stage_index) := 1.U(1.W)
    }
    .otherwise{
      lru_list(stage_index) := 0.U//now the last is 0
      SRAM.write(data_array_0, stage_index, bus_rdata_out, data_rdata_out_0)
      SRAM.write(tag_array_0 , stage_index, stage_tag, tag_rdata_out_0 )
      SRAM.write(data_array_1, data_rdata_out_1)
      SRAM.write(tag_array_1 , tag_rdata_out_1)
      valid_array_0(stage_index) := 1.U(1.W)
    }
  }.otherwise{
    SRAM.read(data_array_0, data_cen_0, prev_index, data_rdata_out_0)//read=index
    SRAM.read(data_array_1, data_cen_1, prev_index, data_rdata_out_1)
    SRAM.read(tag_array_0 , tag_cen_0 , prev_index, tag_rdata_out_0 )
    SRAM.read(tag_array_1 , tag_cen_1 , prev_index, tag_rdata_out_1 )
  }
  // valid array
  valid_array_0_out := valid_array_0(prev_index)
  valid_array_1_out := valid_array_1(prev_index)
  when(valid_array_reset){
    valid_array_0 := VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))
    valid_array_1 := VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W)))
    valid_array_0_out := 0.U(1.W)
    valid_array_1_out := 0.U(1.W)
  }
  /*
   Output Control Signal
   */
  prev.ready := (next_state === sLOOKUP & (!ar_waiting)) & next.ready
  next.valid := lkup_stage_out.valid & next_state === sLOOKUP
  /*
   Output Data
   */
  private val bus_out = Wire((new ICacheOut).bits)
  bus_out.data.if2id.pc := lkup_stage_out.bits.addr
  bus_out.data.if2id.inst := MuxLookup(key = lkup_stage_out.bits.addr(3, 2), default = 0.U(32.W), mapping = Array(
    "b00".U(2.W) -> r_stage_out(31, 0),
    "b01".U(2.W) -> r_stage_out(63, 32),
    "b10".U(2.W) -> memory.r.bits.data(31, 0),
    "b11".U(2.W) -> memory.r.bits.data(63, 32),
  ))
  private val cache_out = Wire((new ICacheOut).bits)
  cache_out.data.if2id.pc := lkup_stage_out.bits.addr
  cache_out.data.if2id.inst := MuxLookup(key = lkup_stage_out.bits.addr(3, 2), default = 0.U(32.W), mapping = Array(
    "b00".U(2.W) -> cache_line_data_out(31,0),
    "b01".U(2.W) -> cache_line_data_out(63,32),
    "b10".U(2.W) -> cache_line_data_out(95,64),
    "b11".U(2.W) -> cache_line_data_out(127,96)
  ))
  next.bits.data := MuxCase(0.U.asTypeOf((new ICacheOut).bits.data), Array(
    (curr_state === sALLOC & next.ready) -> bus_out.data,
    (curr_state === sEND) -> cache_out.data,
    (curr_state === sLOOKUP) -> cache_out.data,
  ))
  //icache reset
//  when(io.cache_reset){
//    curr_state := sLOOKUP
//    next_state := sLOOKUP
//    lkup_stage_in := 0.U.asTypeOf(lkup_stage_in)
//  }
  /*
    Debug
   */
  if(SparkConfig.Debug){
    val test_valid_array_0 = Wire(UInt(CacheCfg.ram_depth.W))
    test_valid_array_0 := valid_array_0.asUInt()
    val test_valid_array_1 = Wire(UInt(CacheCfg.ram_depth.W))
    test_valid_array_1 := valid_array_1.asUInt()
    dontTouch(test_valid_array_0)
    dontTouch(test_valid_array_1)
  }
  /*
   Hit Collection
  */
  if(SparkConfig.CacheHitCount){
    val way0_hit_cnt = RegInit(0.U(128.W))
    val way1_hit_cnt = RegInit(0.U(128.W))
    val miss_cnt = RegInit(0.U(128.W))
    val pc_old = Reg(UInt(64.W))
    pc_old := prev.bits.addr
    when(curr_state === sLOOKUP & (pc_old =/= prev.bits.addr)){
      when(tag0_hit ){
        way0_hit_cnt := way0_hit_cnt + 1.U
      }.elsewhen(tag1_hit){
        way1_hit_cnt := way1_hit_cnt + 1.U
      }.otherwise{
        miss_cnt := miss_cnt + 1.U
      }
    }
    val ebreak = WireDefault(false.B)
    BoringUtils.addSink(ebreak, "icache_count_print")
    when(ebreak){
      printf("--------------------ICache Hit Table-------------------------\n")
      printf(p" way 0 hit number      :    ${way0_hit_cnt}\n")
      printf(p" way 1 hit number      :    ${way1_hit_cnt}\n")
      printf(p" total hit number      :    ${way0_hit_cnt + way1_hit_cnt}\n")
      printf(p" miss number           :    ${miss_cnt}\n")
      printf(p" total access          :    ${way0_hit_cnt + way1_hit_cnt + miss_cnt}\n")
      printf(p" Total cache hit rate  : ${(100.U * (way0_hit_cnt + way1_hit_cnt))/(miss_cnt + way0_hit_cnt + way1_hit_cnt)}%\n")
      printf("------------------------------------------------------------\n")
    }
  }


}


