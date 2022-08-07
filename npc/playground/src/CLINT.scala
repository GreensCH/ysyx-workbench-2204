import chisel3._
import chisel3.util._

trait ClintConfig extends CoreParameter {
  protected val ClintBaseAddr  = "h0200 0000".U(PAddrBits.W)
  protected val ClintBoundAddr = "h0200 BFFF".U(PAddrBits.W)
  protected val ClintMSIPAddr  = "h0200 0000".U(PAddrBits.W)
  protected val ClintMTIME     = "h0200 4000".U(PAddrBits.W)
  protected val ClintMTIMECMP  = "h0020 BFF8".U(PAddrBits.W)
}
// Clint  Sideband Signals
class ClintSB extends Bundle with CoreParameter {
  val msip  = Output(Bool())
  val mtip  = Output(Bool())
  val mtime = Output(UInt(DataBits.W))//Used as a shadow for TIME csr in a RISC-V core.
}


class CLINT extends  Module with CoreParameter {
  val io = IO(new Bundle{
    val mmio = Flipped(new AXI4Master)
    val sideband = new ClintSB
  })
  val mmio = io.mmio
  val sb = io.sideband
  mmio <> DontCare
  sb <> DontCare

}

object CLINT extends  ClintConfig {

  def isMtimecmp(addr: UInt): Bool = {
    addr === ClintBaseAddr
  }

  def isMtime(addr: UInt): Bool = {
    addr === ClintMTIME
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