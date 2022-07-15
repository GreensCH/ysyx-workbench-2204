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
  def read(ram: SRAM, addr: UInt, rdata: UInt): Unit = {
    ram.io.cen := false.B
    ram.io.wen := true.B
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
  override val valid = Input(Bool())
  override val ready = Output(Bool())
}

class CacheBaseOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val data = new Bundle{}
  }
}

class CacheBase[IN <: CacheBaseIn, OUT <: CacheBaseOut] (val id: UInt, _in: IN ,val _out: OUT) extends Module {
  val io = IO(new Bundle {
    val prev = _in
    val master = new AXI4
    val next = _out
  })
  /*
   IO Interface
   */
  protected val prev = io.prev
  protected val memory = io.master
  protected val next = io.next
  protected val prev_addr = prev.bits.addr
  protected val prev_data = prev.bits.data
  /*
   Cache Manual Argument
   */
  val index_border_up = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits - 1
  val index_border_down = CacheCfg.cache_offset_bits
  val tag_border_up = 31
  val tag_border_down = CacheCfg.cache_offset_bits + CacheCfg.cache_line_index_bits
  /*
   States
   */
  protected val sLOOKUP = 0.U(2.W)
  protected val sREAD   = 1.U(2.W)
  protected val sRBACK = 2.U(2.W)
  protected val next_state = Wire(UInt(sLOOKUP.getWidth.W))
  protected val curr_state = RegNext(init = sLOOKUP, next = next_state)
  /*
   AXI Interface Default Connection(Read-Only)
   */
  AXI4BundleA.dontCare(memory.ar)
  AXI4BundleR.default(memory.r)
  memory.aw <> 0.U.asTypeOf(new AXI4BundleA) // useless port use default signal
  memory.w <> 0.U.asTypeOf(new AXI4BundleW)
  memory.b <> 0.U.asTypeOf(new AXI4BundleB)
  val trans_id = id.asTypeOf(UInt(AXI4Parameters.idBits.W))
  /*
   SRAM & SRAM Signal
   */
  val data_array_0 = SRAM()
  val data_array_1 = SRAM()
  val tag_array_0 = SRAM()
  val tag_array_1 = SRAM()
  val data_rdata_out_0 = Wire(UInt(CacheCfg.ram_width.W))
  val data_rdata_out_1 = Wire(UInt(CacheCfg.ram_width.W))
  val tag_rdata_out_0 = Wire(UInt(CacheCfg.ram_width.W))
  val tag_rdata_out_1 = Wire(UInt(CacheCfg.ram_width.W))
  val lru_list = RegInit(VecInit(Seq.fill(CacheCfg.ram_depth)(0.U(1.W))))
  /*
  Stage
 */
  /* Lookup Stage */
  val lkup_stage_type = new Bundle {
    val valid = Bool()
    val data = (new CacheBaseIn).bits
  }
  protected val lkup_stage_en = Wire(Bool())
  protected val lkup_stage_in = WireDefault(0.U.asTypeOf(lkup_stage_type))
  protected val lkup_stage_out = RegEnable(init = 0.U.asTypeOf(lkup_stage_type), next = lkup_stage_in, enable = lkup_stage_en)
  /* AXI Read Channel Stage */
  protected class r_stage_type extends Bundle { val data = Input((new AXI4BundleR).bits.data) }
  protected val r_stage_in = Wire(new r_stage_type)
  r_stage_in.data := memory.r.bits.data
  protected val r_stage_out = RegNext(init = 0.U.asTypeOf(new r_stage_type), next = r_stage_in)
  /*
   Main Data Reference
   */
  protected val prev_index = prev.bits.addr(index_border_up, index_border_down)
  protected val prev_tag = prev.bits.addr(tag_border_up, tag_border_down)
  protected val stage_index = lkup_stage_out.data.addr(index_border_up, index_border_down)
  protected val stage_tag   = lkup_stage_out.data.addr(tag_border_up, tag_border_down)
  /*
   Main Internal Control Signal
   */
  val ar_addr = Wire(UInt(AXI4Parameters.addrBits.W))
  protected val r_id = trans_id === memory.r.bits.id
  protected val r_okay = r_id & (AXI4Parameters.RESP_OKAY === memory.r.bits.resp) & memory.r.valid
  protected val r_last = r_id & memory.r.bits.last  & memory.r.valid
  protected val r_data = memory.r.bits.data
  protected val tag0_hit = tag_rdata_out_0 === stage_tag
  protected val tag1_hit = tag_rdata_out_1 === stage_tag
  protected val miss = !(tag0_hit | tag1_hit)
  /*
   Main Internal Data Signal
   */
  protected val bus_rdata_out = Cat(memory.r.bits.data, r_stage_out.data)//128 bits
  protected val cache_line_data_out = MuxCase(0.U(CacheCfg.cache_line_bits.W), Array(
    tag0_hit -> data_rdata_out_0,
    tag1_hit -> data_rdata_out_1
  ))
  /*
   AXI ARead AWrite
   */
  when(next_state === sREAD){
    memory.ar.valid := true.B
    memory.ar.bits.id := trans_id
    memory.ar.bits.addr := ar_addr
    memory.ar.bits.size := 3.U
    memory.ar.bits.len  := 1.U
    memory.ar.bits.burst := AXI4Parameters.BURST_INCR
  }
  .otherwise{
    memory.ar.valid := false.B
    memory.ar.bits.id := 0.U
    memory.ar.bits.addr := 0.U
    memory.ar.bits.size := 0.U
    memory.ar.bits.len  := 0.U
    memory.ar.bits.burst := AXI4Parameters.BURST_INCR
  }
}

class ICacheIn extends CacheBaseIn {
  override val bits = new Bundle{
    val data = (new PCUOut).bits
    val addr = Output(UInt(CacheCfg.paddr_bits.W))
  }
}
class ICacheOut extends CacheBaseOut {
  override val bits = new Bundle{
      val data = (new IFUOut).bits
    }
}
class ICache(id: UInt) extends CacheBase[ICacheIn, ICacheOut](id = id, _in = new ICacheIn, _out = new ICacheOut){
  /*
   Internal Control Signal
  */
  private val r_write_back = curr_state === sREAD && r_last
  /*
   States Change Rule
   */
  next_state := sLOOKUP //switch default
  switch(curr_state){
    is(sLOOKUP){
      when(!prev.valid){ next_state := sLOOKUP }
      .elsewhen(!memory.ar.ready){ next_state := sLOOKUP }
      .elsewhen(miss & lkup_stage_out.valid)  { next_state := sREAD   }
      .otherwise {next_state := sLOOKUP}
    }
    is(sREAD){
      when(r_last) { next_state := sRBACK }
      .otherwise   { next_state := sREAD  }
    }
    is(sRBACK){
      when(!miss) { next_state := sLOOKUP}
      .otherwise  { next_state := sRBACK }//can delete this way, and directly be sLOOKUP
    }
  }
  /*
    Main Internal Data Signal
   */
  lkup_stage_en := prev.ready
  lkup_stage_in.data.addr := prev.bits.data.pc2if.pc
  lkup_stage_in.data.data := DontCare
  lkup_stage_in.valid := prev.valid
  /*
   SRAM LRU
   */
  when(r_write_back){
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
    SRAM.read(data_array_0, prev_index, data_rdata_out_0)//read=index
    SRAM.read(data_array_1, prev_index, data_rdata_out_1)
    SRAM.read(tag_array_0 , prev_index, tag_rdata_out_0 )
    SRAM.read(tag_array_1 , prev_index, tag_rdata_out_1 )
  }
  /*
   Output Control Signal
   */
  prev.ready := next_state === sLOOKUP
  next.valid := lkup_stage_out.valid
  /*
   Output Data
   */
  private val is_bus_out = r_write_back
  private val bus_out = Wire((new ICacheOut).bits)
  bus_out.data.if2id.pc := lkup_stage_out.data.addr
  bus_out.data.if2id.inst := r_stage_out.data
  bus_out.data.if2id.inst := MuxLookup(key = lkup_stage_out.data.addr(3, 2), default = 0.U(32.W), mapping = Array(
    "b00".U(2.W) -> r_stage_out.data(31, 0),
    "b01".U(2.W) -> r_stage_out.data(63, 32),
    "b10".U(2.W) -> memory.r.bits.data(31, 0),
    "b11".U(2.W) -> memory.r.bits.data(63, 32),
  ))
  private val cache_out = Wire((new ICacheOut).bits)
  cache_out.data.if2id.pc := lkup_stage_out.data.addr
  cache_out.data.if2id.inst := MuxLookup(key = lkup_stage_out.data.addr(3, 2), default = 0.U(32.W), mapping = Array(
    "b00".U(2.W) -> cache_line_data_out(31,0),
    "b01".U(2.W) -> cache_line_data_out(63,32),
    "b10".U(2.W) -> cache_line_data_out(95,64),
    "b11".U(2.W) -> cache_line_data_out(127,96)
  ))
  next.bits.data := Mux(is_bus_out, bus_out.data, cache_out.data)
}

//class DCache (id: UInt)  extends CacheBase(id){
//  // states
//  override protected val sLOOKUP = 0.U(2.W)
//  protected val sWRITE = 1.U(2.W)
//  override protected val sREAD   = 2.U(2.W)
//
//
//}
///*
// Main Internal Data Signal
// */
///* Stage Data In */
//lkup_stage_in.data := prev.bits
//lkup_stage_in.valid := prev.valid
//r_stage_in := MuxCase(0.U, Array(
//  (curr_state === sLOOKUP) -> 0.U,
//  (r_last) -> 0.U,
//  (curr_state === sREAD) -> r_data
//))
///* Array or AXI Data Out (128 bit) */
//protected val lkup_data = MuxCase(0.U.asTypeOf(next), Array(
//
//))
//protected val read_data = Wire(UInt(CacheCfg.ram_width.W))
