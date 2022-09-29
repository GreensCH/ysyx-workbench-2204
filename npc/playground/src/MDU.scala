import chisel3._
import chisel3.util._
/**
 * ebreak
 */
class MDUIO extends Bundle{
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
}

class ysyx_040978_mdu extends BlackBox with HasBlackBoxResource {
  val io = IO(new Bundle{
    val clock   =   Input(Clock())
    val reset   =   Input(Reset())
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

  addResource("/ysyx_040978_mdu.v")

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


class MDU extends Module{
  val io = IO(new MDUIO)

  if(SparkConfig.RealMDU){
    val mdu = Module(new ysyx_040978_mdu)
    mdu.io.clock <> clock
    mdu.io.reset <> reset
    mdu.io.mul     := io.mul
    mdu.io.mulh    := io.mulh
    mdu.io.mulhu   := io.mulhu
    mdu.io.mulhsu  := io.mulhsu
    mdu.io.div     := io.div
    mdu.io.divu    := io.divu
    mdu.io.rem     := io.rem
    mdu.io.remu    := io.remu
    mdu.io.src1    := io.src1
    mdu.io.src2    := io.src2
    io.result := mdu.io.result
    io.ready  := mdu.io.ready
  }else{
    val mdu = Module(new ysyx_040978_ref_mdu)
    mdu.io.clock <> clock
    mdu.io.reset <> reset
    mdu.io.mul     := io.mul
    mdu.io.mulh    := io.mulh
    mdu.io.mulhu   := io.mulhu
    mdu.io.mulhsu  := io.mulhsu
    mdu.io.div     := io.div
    mdu.io.divu    := io.divu
    mdu.io.rem     := io.rem
    mdu.io.remu    := io.remu
    mdu.io.src1    := io.src1
    mdu.io.src2    := io.src2
    io.result := mdu.io.result
    io.ready  := mdu.io.ready
  }

}