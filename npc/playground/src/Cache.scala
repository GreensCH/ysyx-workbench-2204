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
    ram.io.cen := true.B
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
  protected val prev_index = prev.bits.addr(index_border_up, index_border_down)
  protected val prev_tag = prev.bits.addr(tag_border_up, tag_border_down)
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

class DCacheBase[IN <: CacheBaseIn, OUT <: CacheBaseOut] (_in: IN, _out: OUT) extends Module {
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
  memory <> AXI4Master.default()
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
  protected val prev_index = prev.bits.addr(index_border_up, index_border_down)
  protected val prev_tag = prev.bits.addr(tag_border_up, tag_border_down)
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
class DCacheIn extends CacheBaseIn {
  override val bits = new Bundle{val data = (new EXUOut).bits
    val addr = Output(UInt(CacheCfg.paddr_bits.W))
  }
}
class DCacheOut extends CacheBaseOut {
  override val bits = new Bundle{
      val data = (new MEMUOut).bits
    }
}
class DCache extends DCacheBase[DCacheIn, DCacheOut](_in = new DCacheIn, _out = new DCacheOut){
  /*
   States addition and overriding
  */
  override val sLOOKUP = 0.U(3.W)// save inst or load inst
  override val sREAD   = 1.U(3.W) // load/save inst, cache miss, r transaction is launching
  override val sALLOC  = 2.U(3.W) // load/save inst, cache miss, r response has received in sLREAD, this stage  allocate the rdata to sram
  val sSAVE   = 3.U(3.W) // save inst, cache hit, this stage write sram
  override val sWRITE = 4.U(3.W) // cache line is dirty, w transaction is launching for writeback
  /*
   Main Control Signal Reference
  */
  private val prev_load  = prev.bits.data.id2mem.memory_rd_en
  private val prev_save = prev.bits.data.id2mem.memory_we_en
  private val lkup_stage_load = lkup_stage_out.bits.data.id2mem.memory_rd_en
  private val lkup_stage_save = lkup_stage_out.bits.data.id2mem.memory_we_en
  /*
   Internal Control Signal
  */
  private val allocation = (curr_state === sREAD) & r_last
  private val ar_waiting = (curr_state === sLOOKUP) & miss & (memory.ar.ready === false.B)
  private val need_writeback = Wire(Bool())
  private val w_waiting = (curr_state === sLOOKUP) & need_writeback & (memory.w.ready === false.B)
  lkup_stage_en := prev.ready
  data_cen_0 := !next.ready  // If next isn't ready, then lock the sram output
  data_cen_1 := !next.ready
  tag_cen_0  := !next.ready
  tag_cen_1  := !next.ready
  miss := true.B// Delete !
  /*
   States Change Rule
   */
  next_state := sLOOKUP
  switch(curr_state){
    when(!prev.valid){ next_state := sLOOKUP }
    .elsewhen(!memory.ar.ready) { next_state := sLOOKUP }// cannot transfer
    .elsewhen(miss & lkup_stage_out.valid & lkup_stage_load) { next_state := sREAD  }
    .elsewhen(miss & lkup_stage_out.valid & lkup_stage_save) { next_state := sWRITE }
    .otherwise { next_state := sLOOKUP }
  }
  is(sREAD){
    when(r_last) { next_state := sALLOC }
    .otherwise   { next_state := sREAD  }
  }
  /*
   Internal Control Signal
   */

  /*
   Internal Data Signal
   */
  a_addr := Cat(lkup_stage_out.bits.addr(38, 4), 0.U(4.W))

  /*
   SRAM LRU
   */

  /*
   Output Control Signal
   */

  /*
   Output Data
   */

}
