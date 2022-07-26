import chisel3._
import chisel3.util._

object CacheCfg {
  val byte  = 8
  val hword = 16
  val word  = 32
  val dword = 64
  val paddr_bits = 39 //fixed ,and related to cpu core config

  val ram_depth_bits = 6
  val ram_depth = scala.math.pow(2, ram_depth_bits).toInt//2^6=64
  val ram_width = 128 //
  val ram_mask_scale = 1 // 1-bit 8-byte

  val cache_way = 2 //fixed
  val cache_line_bits = ram_width
  val cache_line_number = (ram_depth/cache_way).toInt // 32
  val cache_line_index_bits =  (Math.log(cache_line_number)/Math.log(2)).toInt //l2g(32) = 5
  val cache_offset_bits = (Math.log(ram_width/byte)/Math.log(2)).toInt // l2g(128/8) = l2g(16) = 4
  val cache_tag_bits = paddr_bits - cache_offset_bits - cache_line_index_bits // 39 - 5 - 4 = 30
  // This design doesn't use cache line valid
  //val cache_tag_valid_set = cache_tag_bits//begin with 0

}


class SRAMIO extends Bundle{
  val addr = Input(UInt(CacheCfg.ram_depth_bits.W))//addr
  val cen = Input(Bool())//sram enable low is active
  val wen = Input(Bool())//sram write enable low is active
  val wmask = Input(UInt(CacheCfg.ram_width.W)) // low is active
  val wdata = Input(UInt(CacheCfg.ram_width.W))
  val rdata = Output(UInt(CacheCfg.ram_width.W))
}
class SRAM extends Module{
  val io = IO(new SRAMIO)
  if(SparkConfig.ChiselRam){
    // arg_1(128) = ram depth, vec_arg1 = total_data(128), vec_arg2 = pre_data_size(1)
    val data_in = Wire(Vec(CacheCfg.ram_width/CacheCfg.ram_mask_scale, UInt(CacheCfg.ram_mask_scale.W)))
    val data_out = Wire(Vec(CacheCfg.ram_width/CacheCfg.ram_mask_scale, UInt(CacheCfg.ram_mask_scale.W)))
    val wmask = Wire(Vec(CacheCfg.ram_width/CacheCfg.ram_mask_scale, Bool()))
    val ram = SyncReadMem(CacheCfg.ram_depth, Vec(CacheCfg.ram_width/CacheCfg.ram_mask_scale, UInt(CacheCfg.ram_mask_scale.W)))
    wmask := (~io.wmask).asTypeOf(wmask)
    data_in := io.wdata.asTypeOf(data_in)
    io.rdata := data_out.asTypeOf(io.rdata)

    data_out := DontCare
    when(!io.cen){
      when(!io.wen){
        ram.write(io.addr, data_in, wmask)
      }.otherwise{
        data_out := ram.read(io.addr)
      }
    }
  }

}

object SRAM{
  def apply(): SRAM = {
    val ram = Module(new SRAM)
    ram.io.cen := true.B// low is valid, when true mean sram doesn't work, or not
    ram.io.wen := true.B
    ram.io.addr := 0.U(CacheCfg.ram_depth_bits.W)
    ram.io.wmask := 0.U(CacheCfg.ram_width.W)
    ram.io.wdata := 0.U(CacheCfg.ram_width.W)
    ram
  }
  def write(ram: SRAM, rdata: UInt): Unit = {
    ram.io.cen := true.B
    ram.io.wen := true.B
    ram.io.addr := DontCare
    ram.io.wdata := DontCare
    ram.io.wmask := DontCare
    rdata := ram.io.rdata
  }
  def write(ram: SRAM, addr: UInt, wdata: UInt, mask: UInt, rdata: UInt): Unit = {
    ram.io.cen := false.B
    ram.io.wen := false.B
    ram.io.addr := addr
    ram.io.wdata := wdata
    ram.io.wmask := mask
    rdata := ram.io.rdata
  }
  def write(ram: SRAM, addr: UInt, wdata: UInt, rdata: UInt): Unit = {
    ram.io.cen := false.B
    ram.io.wen := false.B
    ram.io.addr := addr
    ram.io.wdata := wdata
    ram.io.wmask := 0.U(CacheCfg.ram_width.W)
    rdata := ram.io.rdata
  }
  def read(ram: SRAM, cen: Bool, addr: UInt, rdata: UInt): Unit = {
    ram.io.cen := cen
    ram.io.wen := true.B//true is close, false is open
    ram.io.addr := addr
    ram.io.wdata := DontCare
    ram.io.wmask := DontCare
    rdata := ram.io.rdata
  }
}

class CacheBaseIn extends MyDecoupledIO{
  override val bits = new Bundle{
    val data = new Bundle{}
    val addr = Input(UInt(CacheCfg.paddr_bits.W))
  }
}
class CacheBaseOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val data = new Bundle{}
  }
}
class CacheBase[IN <: CacheBaseIn, OUT <: CacheBaseOut] (_in: IN, _out: OUT) extends Module {
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
   Cache Manual Argument
   */
  protected val index_border_up   = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits - 1
  protected val index_border_down = CacheCfg.cache_offset_bits
  protected val tag_border_up   = CacheCfg.paddr_bits - 1
  protected val tag_border_down = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits
  /*
   States
   */
  protected val sLOOKUP  = 0.U(2.W)
  protected val sREAD    = 1.U(2.W)
  protected val sALLOC   = 2.U(2.W)// allocation
  protected val sWRITE   = 3.U(2.W)
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
  protected val prev_index  = prev.bits.addr(index_border_up, index_border_down)
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
  protected val miss = !(tag0_hit | tag1_hit)
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
    AXI4BundleA.set(inf = memory.ar, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = 1.U)
  }
  .otherwise{
    AXI4BundleA.clear(memory.ar)
  }
}
class ICacheIn extends CacheBaseIn {
  override val bits = new Bundle {
    val data = (new PCUOut).bits
    val addr = Output(UInt(CacheCfg.paddr_bits.W))
  }
}
class ICacheOut extends CacheBaseOut {
  override val bits = new Bundle{
      val data = (new IFUOut).bits
    }
}
class ICache extends CacheBase[ICacheIn, ICacheOut](_in = new ICacheIn, _out = new ICacheOut){
  /*
   Internal Control Signal
  */
  private val allocation = (curr_state === sREAD) & r_last
  private val ar_waiting = (curr_state === sLOOKUP) & miss & (!memory.ar.ready)
  dontTouch(ar_waiting)
  lkup_stage_en := prev.ready
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
      .otherwise  { next_state := sALLOC }//can delete this way, and directly be sLOOKUP
    }
  }
  /*
   Internal Data Signal
   */
  a_addr := Cat(lkup_stage_out.bits.addr(38, 4), 0.U(4.W))// axi read addr
  lkup_stage_in.bits.addr := prev.bits.data.pc2if.pc
  lkup_stage_in.bits.data := DontCare
  lkup_stage_in.valid := prev.valid
  lkup_stage_in.ready := DontCare
  /*
   SRAM LRU
   */
  when(allocation){
    when(lru_list(stage_index) === 0.U){// last is 0
      lru_list(stage_index) := 1.U//now the last is 1
      SRAM.write(data_array_0, data_rdata_out_0)
      SRAM.write(tag_array_0 , tag_rdata_out_0)
      SRAM.write(data_array_1, stage_index, bus_rdata_out, data_rdata_out_1)
      SRAM.write(tag_array_1 , stage_index, stage_tag, tag_rdata_out_1)
    }
    .otherwise{
      lru_list(stage_index) := 0.U//now the last is 0
      SRAM.write(data_array_0, stage_index, bus_rdata_out, data_rdata_out_0)
      SRAM.write(tag_array_0 , stage_index, stage_tag, tag_rdata_out_0 )
      SRAM.write(data_array_1, data_rdata_out_1)
      SRAM.write(tag_array_1 , tag_rdata_out_1)
    }
  }.otherwise{
    SRAM.read(data_array_0, data_cen_0, prev_index, data_rdata_out_0)//read=index
    SRAM.read(data_array_1, data_cen_1, prev_index, data_rdata_out_1)
    SRAM.read(tag_array_0 , tag_cen_0 , prev_index, tag_rdata_out_0 )
    SRAM.read(tag_array_1 , tag_cen_1 , prev_index, tag_rdata_out_1 )
  }
  /*
   Output Control Signal
   */
  prev.ready := (next_state === sLOOKUP & (!ar_waiting)) & next.ready
  next.valid := lkup_stage_out.valid
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
    (curr_state === sALLOC) -> bus_out.data,
    (curr_state === sLOOKUP) -> cache_out.data,
  ))
}


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
  protected val prev_index    = prev.bits.addr(index_border_up, index_border_down)
  protected val prev_tag      = prev.bits.addr(tag_border_up, tag_border_down)
  protected val stage1_index = stage1_out.bits.addr(index_border_up, index_border_down)
  protected val stage1_tag   = stage1_out.bits.addr(tag_border_up, tag_border_down)
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
  protected val go_on = (curr_state === sLOOKUP) //|
                        //(curr_state === sREAD & axi_finish & next.ready) |
                        //(curr_state === sEND & next.ready)  | (curr_state === sSAVE)
  /* control */
  stage1_en := go_on
  /* data */
  protected val cache_line_data_out = MuxCase(0.U(CacheCfg.cache_line_bits.W), Array(
    tag0_hit -> data_array_out_0,
    tag1_hit -> data_array_out_1
  ))
  /*
   AXI ARead AWrite
   */
  axi_rd_en := false.B
  axi_we_en := false.B
  when(curr_state === sFLUSH){ axi_we_en := true.B  }
  .elsewhen(curr_state === sLOOKUP){
    when(prev.bits.flush) { axi_we_en := true.B }
    .elsewhen(miss & (prev_load | prev_save)){
       when(need_writeback){ axi_we_en := true.B }
      .otherwise{ axi_rd_en := true.B }
    }
  }
  .elsewhen(curr_state === sRWAIT){ axi_rd_en := true.B }
  .elsewhen(curr_state === sWWAIT){ axi_we_en := true.B }

  axi_addr := MuxCase(stage1_out.bits.addr, Array(
    (curr_state === sLOOKUP) -> prev.bits.addr,
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
  protected val dirty_array_in = Wire(Bool())
  protected val data_array_in  = Wire(UInt(CacheCfg.ram_width.W))
  protected val tag_array_in   = Wire(UInt(CacheCfg.cache_tag_bits.W))
  protected val valid_array_in = Wire(UInt(1.W))
//  private   val tag_sram_in = Cat(valid_array_in, tag_array_in)
  protected val array_write = Wire(Bool())
  protected val array_index = Wire(UInt(prev_index.getWidth.W))
  SRAM.read(data_array_0, data_cen_0, array_index, data_array_out_0)
  SRAM.read(data_array_1, data_cen_1, array_index, data_array_out_1)
  SRAM.read(tag_sram_0,  tag_cen_0, array_index, tag_sram_out_0)
  SRAM.read(tag_sram_1,  tag_cen_1, array_index, tag_sram_out_1)
  when(array_write){
    when(next_way){
      SRAM.write(data_array_1, array_index, data_array_in, data_array_out_1)
      SRAM.write(tag_sram_1  , array_index, tag_array_in , tag_sram_out_1)
      dirty_array_1(array_index) := dirty_array_in
    }.otherwise{
      SRAM.write(data_array_0, array_index, data_array_in, data_array_out_0)
      SRAM.write(tag_sram_0  , array_index, tag_array_in , tag_sram_out_0)
      dirty_array_0(array_index) := dirty_array_in
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
         when(prev_load)    { next_state := sLOOKUP }
        .elsewhen(prev_save){ next_state := sSAVE   }
      }.elsewhen(prev_load | prev_save){//miss situation
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
  private val read_data_size   = Mux(_is_lookup, prev.bits.size, stage1_out.bits.size)
  private val read_data_sext   = Mux(_is_lookup, prev.bits.data.id2mem.sext_flag, stage1_out.bits.data.id2mem.sext_flag)
  private val start_byte = Mux(_is_lookup, prev.bits.addr(3, 0), stage1_out.bits.addr(3, 0))
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
  private val _is_save = curr_state === sSAVE
  private val _save_data_src   = Mux(_is_save, cache_line_data_out, axi_rd_data)// is_save -> normal save, otherwise is writeback-save
  private val _save_data_token = stage1_out.bits.data
  private val _save_data_size  = stage1_out.bits.size
  private val _save_data_size_2 = Cat(_save_data_size.dword, _save_data_size.word, _save_data_size.hword, _save_data_size.byte)
  private val _save_start_byte_left = Mux(_is_save, stage1_out.bits.addr(3, 0), stage1_out.bits.addr(3, 0))
  private val _save_start_bit_left  = (_save_start_byte_left << 3).asUInt()
  private val _save_start_bit_right = (_save_data_size_2 << 3).asUInt() + 1.U
  private val save_data = Replace(_save_data_src, _save_data_token.ex2mem.we_data, _save_start_bit_left, _save_start_bit_right)
  /* tag data */
  private val save_tag   = stage1_tag
  /*
   Array Data & Control
  */
  /* array read write */
  array_write := (curr_state === sSAVE) | (curr_state === sREAD) | (flushing)
  /* array index */
  array_index := MuxCase(0.U, Array(
    (prev.bits.flush | flushing) -> flush_cnt_val,
    (curr_state === sLOOKUP) -> prev_index,
    (go_on) -> prev_index,
    (curr_state === sSAVE)   -> stage1_index,
    (curr_state === sREAD)   -> stage1_index,
  ))
  /* data array in */
  data_array_in := MuxCase(0.U, Array(
    flushing    -> 0.U(128.W),
    (stage1_save) -> save_data,
    (curr_state === sREAD) -> axi_rd_data,
  ))
  tag_array_in := MuxCase(0.U, Array(
    flushing    -> 0.U(128.W),
    (stage1_save) -> stage1_tag,
  ))
  /* valid array in */
  valid_array_in := !flushing
  /* dirty array */
  dirty_array_out_index := stage1_index
  dirty_array_in := stage1_save & (!flushing)
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
