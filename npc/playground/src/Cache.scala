import chisel3._
import chisel3.util._

//object ICache {
//
//}
//
//
class ICache extends Module{
  val io = IO(new Bundle{
      val prev  = Flipped(new PCUOut)
      val master = new AXI4
      val next  = new IFUOut
  })
  // module interface
  private val prev = io.prev
  private val memory = io.master
  private val next = io.next
  private val pc = prev.bits.pc2if.pc
  // Miss register
  val miss_data_reg = RegInit(0.U.asTypeOf((new IFUOut).bits))
  val miss_valid_reg = RegInit(false.B)
  // AXI interface
  val axi_ar_out = memory.ar
  val axi_r_in = memory.r
  memory.aw <> 0.U.asTypeOf(new AXI4BundleA) // useless port use default signal
  memory.w <> 0.U.asTypeOf(new AXI4BundleW)
  memory.b <> 0.U.asTypeOf(new AXI4BundleB)
  val trans_id = 1.U(AXI4Parameters.idBits)
  // Main Signal
  val cache_ready = WireDefault(init = false.B)
  val cache_valid = WireDefault(init = false.B)
  val resp_okay = (trans_id === axi_r_in.bits.id) & (AXI4Parameters.RESP_OKAY === axi_r_in.bits.resp) & (axi_r_in.valid)
  val last = (axi_r_in.bits.last & resp_okay)
  val read_data = axi_r_in.bits.data
  val miss = WireDefault(init = true.B)
  // FSM States
  protected val sIDLE :: sLOOKUP :: sMISSUE :: sMCATCH :: sMWRITE :: Nil = Enum(5) //sIDLEUInt<3>(0) sLOOKUPUInt<3>(1)
  protected val next_state = WireDefault(sMISSUE)
  protected val curr_state = RegEnable(init = sMISSUE, next = next_state, enable = next.ready)
  // States change
  switch(curr_state){
    is (sIDLE){
      next_state := sMISSUE
    }
    is(sLOOKUP){
      assert(false.B) // DEBUG!
      when(miss) {
        next_state := sMISSUE
      } .otherwise{
        next_state := sMISSUE
      }
    }
    is (sMISSUE) {
      when(resp_okay) {
        next_state := sMCATCH
      }.otherwise{
        next_state := sMISSUE
      }
    }
    is (sMCATCH){
      when(last) {
        next_state := sMWRITE
      } .otherwise{
        next_state := sMCATCH
      }
    }
    is (sMWRITE){
      when(next.ready){
        next_state := sMISSUE
      } .otherwise{
        next_state := sMWRITE
      }
    }
  }
  /* Output */
  // Cache-Pipeline Control Signal(note: miss_reg_valid is prev-valid ctrl sig)
  when(curr_state === sIDLE){
    cache_valid := false.B
    cache_ready := true.B
  }
  .elsewhen(curr_state === sLOOKUP){
    cache_valid := true.B
    cache_ready := true.B
  }.elsewhen(curr_state === sMISSUE) {
    cache_valid:= false.B
    cache_ready := false.B
  }.elsewhen(curr_state === sMCATCH){
    cache_valid := false.B
      when(last) { cache_ready := true.B }
      .otherwise{  cache_ready := false.B }
  }.elsewhen(curr_state === sMWRITE){
    cache_valid := true.B// this may be same as prev.valid, but could cause unpredicted problem
    cache_ready := false.B
  }
 // AXI Control Signal
  axi_r_in.ready := true.B
  when(next_state === sMISSUE){
    axi_ar_out.valid := true.B
    axi_ar_out.bits.id := trans_id
    axi_ar_out.bits.addr := Cat(pc(pc.getWidth - 1, 4), 0.U(4.W))// [PARA]
    axi_ar_out.bits.size := 3.U // soc datasheet [PARA]
    axi_ar_out.bits.len  := 1.U // cache line / (axi_size * 8) [CAL]
    axi_ar_out.bits.burst := AXI4Parameters.BURST_INCR
  }.otherwise{
    axi_ar_out.valid := false.B
    axi_ar_out.bits.id := 0.U
    axi_ar_out.bits.addr := 0.U
    axi_ar_out.bits.size := 0.U
    axi_ar_out.bits.len  := 0.U
    axi_ar_out.bits.burst := AXI4Parameters.BURST_INCR
  }
// Miss Register
  when(next_state === sMISSUE){
    miss_valid_reg := prev.valid
    miss_data_reg.if2id.pc := pc
  }
// Data
  val pc_index = pc(3, 2)
  val cache_line_in = WireDefault(0.U(128.W)) // soc datasheet [PARA]
  val shift_reg_in = Wire(UInt(64.W)) // soc datasheet [PARA]
  val shift_reg_en = Wire(Bool())
  val shift_reg_out = RegEnable(next = shift_reg_in, enable = shift_reg_en)//ShiftRegister(in = shift_reg_in, n = 1, en = shift_reg_en) // n = cache line / (axi_size * 8) [CAL]
  //printf(s"this is a shift test : default out  = ${shift_reg_out}, index0  = ${shift_reg_out(0)}, index1  = ${shift_reg_out(1)}, index2  = ${shift_reg_out(2)}\n")
  shift_reg_en := true.B
  shift_reg_in := read_data
  val inst_out = MuxLookup(key = pc_index, default = 0.U(32.W), mapping = Array(
    "b00".U(2.W) -> shift_reg_out(31, 0),
    "b01".U(2.W) -> shift_reg_out(63, 32),
    "b10".U(2.W) -> read_data(31, 0),
    "b11".U(2.W) -> read_data(63, 32),
  ))
  when(last){
    cache_line_in := Cat(shift_reg_out, read_data)
    miss_data_reg.if2id.inst := inst_out
  }
  next.valid := cache_valid & miss_valid_reg
  prev.ready := cache_ready
// Data Output
  next.bits.if2id := miss_data_reg.if2id

// cache function part
  // miss := ?
}

//val outList = MuxCase(
//  default = List(0.U(64.W), 0.U(32.W), true.B, true.B),
//  mapping = List(
//    (curState === sMissIssue) -> List(0.U(64.W), 0.U(32.W), true.B, true.B),
//    (curState === sMissCatch) -> List(0.U(64.W), 0.U(32.W), true.B, true.B),
//    (curState === sMissEnd)   -> List(0.U(64.W), 0.U(32.W), true.B, true.B)
//  )
//)
//ifOut.bits.if2id.pc := outList(0)
//ifOut.bits.if2id.inst := outList(1)