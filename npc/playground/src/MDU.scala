import chisel3._
import chisel3.util._
/**
 * ebreak
 */
class MDUIO extends Bundle{
  val mdu_op  =   Input(new ID2MDU)
  val src1    =   Input(UInt(64.W))
  val src2    =   Input(UInt(64.W))
  val result  =   Output(UInt(64.W))
  val ready   =   Output(Bool())
}

class ysyx_040978_mdu extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock   =   Input(Clock())
    val reset   =   Input(Reset())
    val mul_signed  = Input(UInt(1.W))
    val mul_32      = Input(Bool())
    val is_mu       = Input(Bool())
    val div_signed  = Input(UInt(2.W))
    val is_div      = Input(Bool())
    val is_du       = Input(Bool())
    val src1    =   Input(UInt(64.W))
    val src2    =   Input(UInt(64.W))
    val result  =   Output(UInt(64.W))
    val ready   =   Output(Bool())
  })
  addResource("/ysyx_040978_mdu.v")
}

class ysyx_040978_muler extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock         = Input(Clock())
    val reset         = Input(Reset())
    val in_valid      = Input(Bool())
    val mul_signed    = Input(UInt(2.W))
    val multiplicand  = Input(UInt(64.W))
    val multiplier    = Input(UInt(64.W))

    val out_valid     = Output(UInt(1.W))
    val result_hi     = Output(UInt(64.W))
    val result_lo     = Output(UInt(64.W))
  })
  addResource("/ysyx_040978_muler.v")
}

class ysyx_040978_diver extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock         = Input(Clock())
    val reset         = Input(Reset())
    val in_valid      = Input(Bool())
    val div_signed    = Input(UInt(1.W))
    val dividend      = Input(UInt(64.W))
    val divisor       = Input(UInt(64.W))

    val out_valid     = Output(UInt(1.W))
    val quotient      = Output(UInt(64.W))
    val remainder     = Output(UInt(64.W))
  })
  addResource("/ysyx_040978_diver.v")
}

class ysyx_040978_ref_mdu extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock   =   Input(Clock())
    val reset   =   Input(Reset())
    val flush   =   Input(Bool())
    val mul     =   Input(Bool())
    val mulh    =   Input(Bool())
    val mulhu   =   Input(Bool())
    val mulhsu  =   Input(Bool())
    val div     =   Input(Bool())
    val divu    =   Input(Bool())
    val rem     =   Input(Bool())
    val remu    =   Input(Bool())
    val src1    =   Input(UInt(64.W))
    val src2    =   Input(UInt(64.W))
    val result  =   Output(UInt(64.W))
    val ready   =   Output(Bool())
  })

  addResource("/ysyx_040978_ref_mdu.v")

}


class MDU extends Module {
  val io = IO(new MDUIO)
  if (SparkConfig.RealMDU) {
    val mdu = Module(new ysyx_040978_mdu)
    mdu.io.clock <> clock
    mdu.io.reset <> reset
    mdu.io.is_mu := io.mdu_op.is_mu
    mdu.io.mul_signed := io.mdu_op.mul_signed
    mdu.io.mul_32 := io.mdu_op.mul_32
    mdu.io.is_du := io.mdu_op.is_du
    mdu.io.is_div := io.mdu_op.is_div
    mdu.io.div_signed := io.mdu_op.div_signed
    mdu.io.src1 := io.src1
    mdu.io.src2 := io.src2
    io.result := mdu.io.result
    io.ready := mdu.io.ready
  }else if(SparkConfig.ChiselMDU){
    val mdu = Module(new ChiselMDU)
    mdu.io <> io
  }else{
    val mdu = Module(new ysyx_040978_ref_mdu)
    mdu.io.clock <> clock
    mdu.io.reset <> reset
//    mdu.io.mul     := io.mul
//    mdu.io.mulh    := io.mulh
//    mdu.io.mulhu   := io.mulhu
//    mdu.io.mulhsu  := io.mulhsu
//    mdu.io.div     := io.div
//    mdu.io.divu    := io.divu
//    mdu.io.rem     := io.rem
//    mdu.io.remu    := io.remu
    mdu.io.src1    := io.src1
    mdu.io.src2    := io.src2
    io.result := mdu.io.result
    io.ready  := mdu.io.ready
  }

}

//class Diver extends Module {
//  val io = IO(new Bundle{
//    val in_valid      = Input(Bool())
//    val div_signed    = Input(UInt(1.W))
//    val dividend      = Input(UInt(64.W))
//    val divisor       = Input(UInt(64.W))
//
//    val out_valid     = Output(UInt(1.W))
//    val quotient      = Output(UInt(64.W))
//    val remainder     = Output(UInt(64.W))
//  })
//  private val in_valid      = io.in_valid
//  private val div_signed    = io.div_signed
//  private val dividend      = io.dividend
//  private val divisor       = io.divisor
//  private val out_valid     = io.out_valid
//  private val quotient      = io.quotient
//  private val remainder     = io.remainder
//
//  private val cnt_en = Wire(Bool())
//  private val (cnt, finish) = Counter(cnt_en, 64)
//  private val busy = RegInit(init = false.B)
//  when(in_valid){
//    busy := true.B
//  }.elsewhen(finish){
//    busy := false.B
//  }
//  private val ready = RegInit(init = true.B)
//  when(finish){
//    ready := true.B
//  }.elsewhen(in_valid){
//    ready := false.B
//  }
//  cnt_en := in_valid | busy
//
//  // 符号位缓存
//  private val dsor_neg      = RegInit(true.B)
//  private val dend_neg      = RegInit(true.B)
//  private val div_signed_1  = RegInit(false.B)
//  when(in_valid & ready){
//    when(div_signed.asBool()){
//      dsor_neg      := divisor(63)
//      dend_neg      := dividend(63)
//      div_signed_1  := 1.U(1.W)
//    }.otherwise{
//      dsor_neg      := 0.U(1.W)
//      dend_neg      := 0.U(1.W)
//      div_signed_1  := 0.U(1.W)
//    }
//  }
//  // 输入除数被除数绝对值计算
//  private val dividend_0 = Mux((dividend(63) && div_signed.asBool()), (0.U - dividend), dividend)
//  private val divisor_0 = Mux((divisor(63) && div_signed.asBool()), (0.U - divisor), divisor)
//  // 除数缓存
//  private val divisor_1      = RegInit(0.U(65.W))
//  when(in_valid & ready){
//    divisor_1 := Cat(0.U(1.W), divisor_0)
//  }.elsewhen(finish){
//    divisor_1 := 0.U
//  }
//  private val divisor_2      = Mux(in_valid & ready, )
//}


class ChiselMDU extends Module{
  val io = IO(new MDUIO)

  private val mdu_op = io.mdu_op
  private val src1 = io.src1
  private val src2 = io.src2
  private val muler = Module(new ysyx_040978_muler)
  private val diver = Module(new ysyx_040978_diver)
  // diver
  diver.io.clock <> clock
  diver.io.reset <> reset
  private val diver_res = Mux(mdu_op.is_div, diver.io.quotient, diver.io.remainder)
  private val diver_valid = diver.io.out_valid
  diver.io.in_valid := mdu_op.is_du & (! diver_valid)
  diver.io.div_signed := mdu_op.div_signed
  diver.io.dividend := src1
  diver.io.divisor := src2
  // muler
  muler.io.clock <> clock
  muler.io.reset <> reset
  private val muler_res = Mux(mdu_op.mul_32, muler.io.result_lo, muler.io.result_hi)
  private val muler_valid = muler.io.out_valid
  muler.io.in_valid := mdu_op.is_mu & (! muler_valid)
  muler.io.mul_signed := mdu_op.mul_signed
  muler.io.multiplicand := src1
  muler.io.multiplier := src2
  // output
  io.result := Mux(mdu_op.is_du, diver_res, muler_res)
  io.ready := muler_valid | diver_valid | !(mdu_op.is_du | mdu_op.is_mu)

}

//class ChiselMDU extends Module{
//  val io = IO(new MDUIO)
//
//  private val mdu_op = io.mdu_op
//  private val src1 = io.src1
//  private val src2 = io.src2
//  private val muler = Module(new ysyx_040978_muler)
//  private val diver = Module(new ysyx_040978_diver)
//  // diver
//  diver.io.clock <> clock
//  diver.io.reset <> reset
//  private val diver_res = Mux(mdu_op.is_div, diver.io.quotient, diver.io.remainder)
//  private val diver_valid = diver.io.out_valid
//  diver.io.in_valid := mdu_op.is_du & (! diver_valid)
//  diver.io.div_signed := mdu_op.div_signed
//  diver.io.dividend := src1
//  diver.io.divisor := src2
//  // muler
//  muler.io.clock <> clock
//  muler.io.reset <> reset
//  private val muler_res = Mux(mdu_op.mul_32, muler.io.result_lo, muler.io.result_hi)
//  private val muler_valid = muler.io.out_valid
//  muler.io.in_valid := mdu_op.is_mu & (! muler_valid)
//  muler.io.mul_signed := mdu_op.mul_signed
//  muler.io.multiplicand := src1
//  muler.io.multiplier := src2
//  // output
//  io.result := Mux(mdu_op.is_du, diver_res, muler_res)
//  io.ready := muler_valid | diver_valid | !(mdu_op.is_du | mdu_op.is_mu)
//
//}