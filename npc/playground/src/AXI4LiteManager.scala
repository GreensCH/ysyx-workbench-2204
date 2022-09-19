import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

class AXI4LiteManagerIn extends Bundle{
  val rd_en  = Input(Bool())
  val we_en  = Input(Bool())
  val size   = Flipped(new SrcSize)
  val addr   = Input(UInt(32.W))
  val data   = Input(UInt(64.W))
  val wmask  = Input(UInt(16.W))
}
class AXI4LiteManagerOut extends Bundle{
  val finish = Output(Bool())
  val ready  = Output(Bool())
  val data   = Output(UInt(64.W))
}

class AXI4LiteManager extends Module  {
  val io = IO(new Bundle {
    val in   = new AXI4LiteManagerIn
    val maxi = new AXI4Master
    val out  = new AXI4LiteManagerOut
  })
  /*
   Reference
   */
  private val in = io.in
  private val maxi = io.maxi
  private val out = io.out
  private val sADDR :: sARWAIT :: sREAD1  :: sAWWAIT ::sWRITE1  :: sIREAD :: sIWRITE :: Nil = Enum(7)
  private val next_state = Wire(UInt(sADDR.getWidth.W))
  private val curr_state = RegNext(init = sADDR, next = next_state)
  // Lookup Stage
  private val stage_en = Wire(Bool())
  private val stage_in = Wire(Output(chiselTypeOf(in)))
  stage_in := in
  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
  private val _in = Wire(Output(chiselTypeOf(in)))
  _in := in
  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
  dontTouch(in2)
  // AXI Read Channel Stage
  private val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
  // AXI Interface Default Connection(Read-Write)
  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleR.default (maxi.r)
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)
  /*
  Internal Control Signal
  */
  // axi
  private val r_last = maxi.r.bits.last  & maxi.r.valid
  // common
  private val is_clint = (in2.addr(31, 16) === "h0200".U) & (in2.addr(16, 15) =/= "b11".U)
  private val is_load = in2.rd_en
  private val is_save = in2.we_en
  private val size = in2.size
  /* stage */
  stage_en := curr_state === sADDR
  /*
   Internal Data Signal
  */
  // reference
  private val a_len     = 0.U(AXI4Parameters.lenBits.W)
  private val a_addr    = in2.addr(31, 0)
  private val a_size    = MuxCase(0.U(AXI4Parameters.lenBits), Array(
    in2.size.byte  -> 0.U,
    in2.size.hword -> 1.U,
    in2.size.word  -> 2.U,
    in2.size.dword -> 3.U,
  ))
  private val start_byte = in2.addr(2, 0)
  private val start_bit  =  (in2.addr(2, 0) << 3).asUInt()
  // read transaction
  r_stage_in :=  maxi.r.bits.data
  private val rdata_out_1 = maxi.r.bits.data >> start_bit
  private val clint_rdata = WireDefault(0.U(64.W))
  private val rdata_out = MuxCase(0.U, Array(
    (curr_state === sIREAD) -> clint_rdata,
    (curr_state === sREAD1) -> rdata_out_1,
  ))
  private val memory_data = MuxCase(0.U,
    Array(
      size.byte   -> rdata_out(7,  0),
      size.hword  -> rdata_out(15, 0),
      size.word   -> rdata_out(31, 0),
      size.dword  -> rdata_out(63, 0),
    )
  )
  private val memory_data_buffer = RegInit(0.U(128.W))
  // write transaction
  private val wdata = (in2.data << start_bit).asTypeOf(0.U(128.W))
  private val wmask = MuxCase(0.U(8.W), Array(
    size.byte  -> (in2.wmask  << start_byte),
    size.hword -> (in2.wmask  << start_byte),
    size.word  -> (in2.wmask  << start_byte),
    size.dword -> (in2.wmask  << start_byte),
  )).asUInt()
  /*
   States Change Rule
   val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: Nil = Enum(7)
   */
  next_state := sADDR
  switch(curr_state){
    is(sADDR){
      when(is_load){
        when(is_clint)              { next_state := sIREAD }
          .elsewhen(maxi.ar.ready)  { next_state := sREAD1 }
          .otherwise                { next_state := sARWAIT }
      }.elsewhen(is_save){
        when(is_clint)              { next_state := sIWRITE }
          .elsewhen(maxi.aw.ready)  { next_state := sWRITE1 }
          .otherwise                { next_state := sAWWAIT }
      }.otherwise                   { next_state := sADDR }
    }
    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
    is(sAWWAIT){ when(maxi.aw.ready){ next_state := sWRITE1 }.otherwise{ next_state := sAWWAIT } }
    is(sREAD1){
      when(r_last)                            { next_state := sADDR }
        .otherwise                            { next_state := sREAD1 }
    }
    is(sWRITE1){
      when(maxi.b.valid)                      { next_state := sADDR   }
        .otherwise                            { next_state := sWRITE1 }
    }
    is(sIREAD)  { next_state := sADDR }
    is(sIWRITE) { next_state := sADDR }
  }
  /*
    AXI
   */
  AXI4BundleA.clear(maxi.ar)
  when(next_state === sARWAIT | (curr_state === sADDR &next_state === sREAD1) | curr_state === sARWAIT){
    AXI4BundleA.set(inf = maxi.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = a_size, burst_len = a_len)
  }
  AXI4BundleA.clear(maxi.aw)
  when(next_state === sAWWAIT | (curr_state === sADDR & next_state === sWRITE1) | curr_state === sAWWAIT){
    AXI4BundleA.set(inf = maxi.aw, valid = true.B, id = 0.U, addr = a_addr, burst_size = a_size, burst_len = a_len)
  }
  AXI4BundleW.clear(maxi.w)
  when(curr_state === sWRITE1){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = wdata(63, 0), strb = wmask, last = true.B)
  }
  AXI4BundleB.default(maxi.b)
  dontTouch(maxi.b)
  /*
    Core Internal Bus
  */
  val clint_addr  = WireDefault(0.U(64.W))
  val clint_wdata = WireDefault(0.U(64.W))
  val clint_wmask = WireDefault("hFFFF_FFFF_FFFF_FFFF".U)
  val clint_we    = WireDefault(false.B)

  clint_we    := next_state === sIWRITE
  clint_addr  := a_addr
  clint_wdata := wdata(63, 0)

  BoringUtils.addSource(clint_addr  , "clint_addr")
  BoringUtils.addSource(clint_wdata , "clint_wdata")
  BoringUtils.addSource(clint_wmask , "clint_wmask")
  BoringUtils.addSink(clint_rdata   , "clint_rdata")
  BoringUtils.addSource(clint_we    , "clint_we")

  /*
  Output
 */
  out.ready  := next_state === sADDR | curr_state === sADDR
  dontTouch(out.ready)
  dontTouch(out.data)
  out.finish := maxi.r.bits.last | maxi.b.valid | curr_state === sIWRITE | curr_state === sIREAD//(next_state === sADDR & curr_state =/= sADDR)
  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
  out.data := Mux(curr_state === sREAD1 | curr_state === sIREAD, memory_data, memory_data_buffer)

}