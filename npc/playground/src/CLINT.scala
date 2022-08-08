import chisel3._
import chisel3.util._

trait ClintConfig extends CoreParameter {
  protected val ClintBaseAddr  = "h0200_0000".U(PAddrBits.W)
  protected val ClintBoundAddr = "h0200_BFFF".U(PAddrBits.W)

  protected val ClintMSIPAddr     = "h0200_0000".U(PAddrBits.W)
  protected val ClintMTIMECMPAddr = "h0200_4000".U(PAddrBits.W)
  protected val ClintMTIMEAddr    = "h0200_BFF8".U(PAddrBits.W)
}
// Clint  Sideband Signals
class ClintSB extends Bundle with CoreParameter {
  val msip  = Output(Bool())
  val mtip  = Output(Bool())
  val mtime = Output(UInt(DataBits.W))//Used as a shadow for TIME csr in a RISC-V core.
}

// axi4 lite only
class CLINT extends  Module with ClintConfig {
  val io = IO(new Bundle{
    val mmio = Flipped(new AXI4Master)
    val sideband = new ClintSB
  })
  val mmio = io.mmio
  val sb = io.sideband

  private val mtime     = RegInit(0.U(XLEN.W))
  private val mtimecmp  = RegInit(0.U(XLEN.W))
  private val msip      = RegInit(0.U(32.W))
  mtime := mtime + 1.U
  // sideband output logic
  sb.msip   := msip(0)
  sb.mtime  := mtime
  sb.mtip   := (mtime >= mtimecmp)
  // register access
  val axi_addr = MuxCase(0.U(PAddrBits.W), Array(
    mmio.ar.valid -> mmio.ar.bits.addr,
    mmio.aw.valid -> mmio.aw.bits.addr
  ))
  val axi_id = MuxCase(0.U(mmio.aw.bits.id.getWidth.W), Array(
    mmio.ar.valid -> mmio.ar.bits.id,
    mmio.aw.valid -> mmio.aw.bits.id
  ))
  val is_time     = axi_addr(PAddrBits-1, 4) === ClintMTIMEAddr(PAddrBits-1, 4)
  val is_timecmp  = axi_addr(PAddrBits-1, 4) === ClintMTIMECMPAddr(PAddrBits-1, 4)
  val is_msip     = axi_addr(PAddrBits-1, 4) === ClintMSIPAddr(PAddrBits-1, 4)
  private val go_on = Wire(Bool())
  private val is_time_1   = RegEnable(init = false.B,  next = is_time       , enable = go_on  )
  private val is_msip_1   = RegEnable(init = false.B,  next = is_msip       , enable = go_on  )
  private val is_timecmp_1= RegEnable(init = false.B,  next = is_timecmp    , enable = go_on  )
  private val is_write_1  = RegEnable(init = false.B,  next = mmio.aw.valid , enable = go_on  )
  private val is_read_1   = RegEnable(init = false.B,  next = mmio.ar.valid , enable = go_on  )
  private val axi_id_1    = RegEnable(init = 0.U(AXI4Parameters.idBits.W),  next = axi_id        , enable = go_on  )
  private val axi_offset_1= RegEnable(init = 0.U(4.W), next = axi_addr(3, 0), enable = go_on  )
  go_on := (is_read_1 & mmio.r.ready) | (is_write_1) | (!is_read_1 & !is_write_1)
  // slave read channel
  mmio.ar.ready := (!is_read_1)

  private val reg_out_64 = Wire(UInt(AXI4Parameters.dataBits.W))
  reg_out_64 := MuxCase(0.U(XLEN.W), Array(
   is_msip_1    -> Cat(0.U(32.W), msip),
   is_time_1    -> mtime,
   is_timecmp_1 -> mtimecmp,
  ))
  private val axi_r_data = (reg_out_64 >> axi_offset_1).asUInt()
  AXI4BundleR.clear(mmio.r)
  when(is_read_1){
    AXI4BundleR.set(mmio.r, id = axi_id_1, data = axi_r_data, last = true.B, resp = AXI4Parameters.RESP_OKAY)
  }
  // slave write channel
  mmio.aw.ready := (!is_write_1)
  val axi_w_data = (mmio.w.bits.data << axi_offset_1).asUInt()
  val axi_w_mask = MuxCase(0.U(XLEN.W),Array(
    (mmio.w.bits.strb === "b0000_0001".U) -> "h0000_000F".U,
    (mmio.w.bits.strb === "b0000_0011".U) -> "h0000_00FF".U,
    (mmio.w.bits.strb === "b0000_1111".U) -> "h0000_FFFF".U,
    (mmio.w.bits.strb === "b1111_1111".U) -> "hFFFF_FFFF".U,
  ))
  val axi_w_mask_64 = (axi_w_mask << axi_offset_1).asUInt()
  val reg_in = (axi_w_mask_64 & axi_w_data) | reg_out_64

  mmio.w.ready := true.B
  when(is_write_1){
    when(is_time)         { mtime     := reg_in }
    .elsewhen(is_timecmp) { mtimecmp  := reg_in }
    .elsewhen(is_msip)    { msip      := reg_in(31, 0) }
  }

  val is_write_2 = RegNext(init = false.B, next = is_write_1)
  val axi_id_2 = RegNext(init = 0.U(AXI4Parameters.idBits.W),  next = axi_id_1 )
  mmio.b.valid := is_write_2
  mmio.b.bits.id := axi_id_2
  mmio.b.bits.resp := AXI4Parameters.RESP_OKAY
  when(mmio.ar.valid | mmio.aw.valid){
    printf(p"| c(${mtime}) |")
  }

}

object CLINT extends  ClintConfig {
  def isMtimecmp(addr: UInt): Bool = {
    addr === ClintBaseAddr
  }
  def isMtime(addr: UInt): Bool = {
    addr === ClintMTIMEAddr
  }
  def isClint(addr: UInt): Bool = {
    (addr(31, 16) === "h0200".U) & (addr(15, 14) =/= "b11".U)
  }
}

//class AXI4Manager extends Module  {
//  val io = IO(new Bundle {
//    val in   = new AXI4ManagerIn
//    val maxi = new AXI4Master
//    val out  = new AXI4ManagerOut
//  })
//  /*
//   Reference
//   */
//  private val in = io.in
//  private val maxi = io.maxi
//  private val out = io.out
//  private val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: Nil = Enum(7)
//  private val next_state = Wire(UInt(sADDR.getWidth.W))
//  private val curr_state = RegNext(init = sADDR, next = next_state)
//  /* Lookup Stage */
//  private val stage_en = Wire(Bool())
//  private val stage_in = Wire(Output(chiselTypeOf(in)))
//  stage_in := in
//  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
//  private val _in = Wire(Output(chiselTypeOf(in)))
//  _in := in
//  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
//  dontTouch(in2)
//  /* AXI Read Channel Stage */
//  private val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
//  private val r_stage_out = RegNext(init = 0.U(AXI4Parameters.dataBits.W), next = r_stage_in)
//  /* AXI Interface Default Connection(Read-Write) */
//  AXI4BundleA.clear   (maxi.ar)
//  AXI4BundleR.default (maxi.r)
//  AXI4BundleA.clear   (maxi.aw)
//  AXI4BundleW.clear   (maxi.w)
//  AXI4BundleB.default (maxi.b)
//  /*
//  Internal Control Signal
//  */
//  /* axi */
//  private val r_last = maxi.r.bits.last  & maxi.r.valid
//  /* common */
//  private val is_load = in2.rd_en
//  private val is_save = in2.we_en
//  private val size = in2.size
//  private val overborder = MuxCase(false.B, Array(
//    size.byte  -> false.B,
//    size.hword -> (in2.addr(0)    =/= 0.U),
//    size.word  -> (in2.addr(1, 0) =/= 0.U),
//    size.dword -> (in2.addr(2, 0) =/= 0.U),
//    size.qword -> true.B
//  ))
//  /* stage */
//  stage_en := curr_state === sADDR
//  /*
//   Internal Data Signal
//  */
//  /* reference */
//  private val a_addr = Mux(overborder, Cat(in2.addr(31, 4), 0.U(4.W)), Cat(in2.addr(31, 3), 0.U(3.W)))
//  private val start_byte = Mux(overborder, in2.addr(3, 0), in2.addr(2, 0))
//  private val start_bit =  Mux(overborder, in2.addr(3, 0) << 3, in2.addr(2, 0) << 3).asUInt()
//  /* read transaction */
//  r_stage_in := MuxCase(0.U, Array(
//    (curr_state === sREAD1 & !r_last) -> maxi.r.bits.data,
//    (curr_state === sREAD2) -> r_stage_out
//  )) //    r_stage_in := Mux(curr_state === sREAD_1 & !r_last, maxi.r.bits.data, r_stage_out)
//  private val rdata_out_128 = Cat(maxi.r.bits.data, r_stage_out)
//  private val rdata_out_1 = maxi.r.bits.data >> start_bit
//  private val rdata_out_2 =  rdata_out_128 >> start_bit
//  private val rdata_out = MuxCase(0.U, Array(
//    (curr_state === sREAD1) -> rdata_out_1,
//    (curr_state === sREAD2) -> rdata_out_2
//  ))
//  private val memory_data = MuxCase(0.U,
//    Array(
//      size.byte   -> rdata_out(7,  0),
//      size.hword  -> rdata_out(15, 0),
//      size.word   -> rdata_out(31, 0),
//      size.dword  -> rdata_out,
//      size.qword  -> rdata_out_128,
//    )
//  )
//  private val memory_data_buffer = RegInit(0.U(128.W))
//  /* write transaction */
//  private val wdata = (in2.data << start_bit).asTypeOf(0.U(128.W))
//  private val wmask = MuxCase(0.U(8.W), Array(
//    size.byte  -> (in2.wmask  << start_byte),
//    size.hword -> (in2.wmask  << start_byte),
//    size.word  -> (in2.wmask  << start_byte),
//    size.dword -> (in2.wmask  << start_byte),
//    size.qword -> ("hffff".U),
//  )).asUInt()
//  /*
//   States Change Rule
//   */
//  next_state := sADDR
//  switch(curr_state){
//    is(sADDR){
//      when(is_load){
//        when(maxi.ar.ready) { next_state := sREAD1 } .otherwise  { next_state := sARWAIT }
//      }.elsewhen(is_save){
//        when(maxi.aw.ready) { next_state := sWRITE1 } .otherwise { next_state := sAWWAIT }
//      }.otherwise           { next_state := sADDR }
//    }
//    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
//    is(sAWWAIT){ when(maxi.ar.ready){ next_state := sWRITE1 }.otherwise{ next_state := sAWWAIT } }
//    is(sREAD1){
//      when(r_last)                            { next_state := sADDR }
//        .elsewhen(overborder & maxi.r.ready)  { next_state := sREAD2 }
//        .otherwise                            { next_state := sREAD1 }
//    }
//    is(sWRITE1){
//      when(overborder & maxi.w.ready )        { next_state := sWRITE2  }
//        .elsewhen(maxi.b.valid)               { next_state := sADDR    }
//        .otherwise                            { next_state := sWRITE1 }
//    }
//    is(sREAD2){
//      when(r_last)                            { next_state := sADDR }
//        .otherwise                            { next_state := sREAD2 }
//    }
//    is(sWRITE2){
//      when(maxi.b.valid)                      { next_state := sADDR }
//        .otherwise                            { next_state := sWRITE2 }
//    }
//  }
//  /*
//    AXI
//   */
//  private val burst_len = Mux(overborder, 1.U, 0.U)
//  //    private val w_stay = RegInit(0.U.asTypeOf((new AXI4BundleW).bits))
//  AXI4BundleA.clear(maxi.ar)
//  when(next_state === sREAD1){
//    AXI4BundleA.set(inf = maxi.ar, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
//  }
//  AXI4BundleA.clear(maxi.aw)
//  when(next_state === sWRITE1){
//    AXI4BundleA.set(inf = maxi.aw, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
//  }
//  AXI4BundleW.clear(maxi.w)
//  when(curr_state === sWRITE1){
//    AXI4BundleW.set(inf = maxi.w, data = wdata(63, 0), strb = wmask, last = !overborder)
//  }
//  when(curr_state === sWRITE2){
//    AXI4BundleW.set(inf = maxi.w, data = wdata(127, 64), strb = wmask, last = true.B)
//  }
//  AXI4BundleB.default(maxi.b)
//  dontTouch(maxi.b)
//  /*
//  Output
// */
//  out.ready  := next_state === sADDR | curr_state === sADDR
//  dontTouch(out.ready)
//  dontTouch(out.data)
//  out.finish := maxi.r.bits.last | maxi.b.valid//(next_state === sADDR & curr_state =/= sADDR)
//  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
//  out.data := Mux(curr_state === sREAD1 | curr_state === sREAD2, memory_data, memory_data_buffer)
//
//}