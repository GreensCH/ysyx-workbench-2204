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
  val is_time     = axi_addr(PAddrBits-1, 3) === ClintMTIMEAddr   (PAddrBits-1, 3)
  val is_timecmp  = axi_addr(PAddrBits-1, 3) === ClintMTIMECMPAddr(PAddrBits-1, 3)
  val is_msip     = axi_addr(PAddrBits-1, 3) === ClintMSIPAddr    (PAddrBits-1, 3)
  private val go_on = Wire(Bool())
  private val is_time_1   = RegEnable(init = false.B,  next = is_time       , enable = go_on  )
  private val is_msip_1   = RegEnable(init = false.B,  next = is_msip       , enable = go_on  )
  private val is_timecmp_1= RegEnable(init = false.B,  next = is_timecmp    , enable = go_on  )
  private val is_write_1  = RegEnable(init = false.B,  next = mmio.aw.valid , enable = go_on  )
  private val is_read_1   = RegEnable(init = false.B,  next = mmio.ar.valid , enable = go_on  )
  private val axi_id_1    = RegEnable(init = 0.U(AXI4Parameters.idBits.W),  next = axi_id        , enable = go_on  )
  private val axi_offset_1= RegEnable(init = 0.U(3.W), next = axi_addr(2, 0), enable = go_on  )
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
