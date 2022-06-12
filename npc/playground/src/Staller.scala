import chisel3._
import chisel3.util._
import chisel3.experimental._

object BypassMuxSel extends ChiselEnum{
  val normal, ex, mem, wb = Value
}

class Staller extends Module{
  val io = IO(new Bundle{
    val id_src1   =   Input (UInt(5.W))
    val id_src2   =   Input (UInt(5.W))
    val ex_dst    =   Input (UInt(5.W))
    val mem_dst   =   Input (UInt(5.W))
    val wb_dst    =   Input (UInt(5.W))
    val optype    =   Flipped(new Optype)
    val operator  =   Flipped(new Operator)
    val is_load   =   Input (Bool())
    val stall     =   Output(Bool())
    val bypassmux_sel1  =   Output(BypassMuxSel())
    val bypassmux_sel2  =   Output(BypassMuxSel())
    val valid1    =   Input(Bool())
    val valid2    =   Input(Bool())
    val valid3    =   Input(Bool())
  })
  /* interface */
  val id_src1     =   io.id_src1
  val id_src2     =   io.id_src2
  val ex_dst      =   io.ex_dst
  val mem_dst     =   io.mem_dst
  val wb_dst      =   io.wb_dst
  val optype      =   io.optype
  val operator    =   io.operator
  val is_load     =   io.is_load
  val valid1      =   io.valid1
  val valid2      =   io.valid2
  val valid3      =   io.valid3
  // if zero_n is false then stall shouldn't be enable
  // if zero_n is true  then stall may be enable
  val zero1_n = ex_dst  =/= 0.U
  val zero2_n = mem_dst =/= 0.U
  val zero3_n = wb_dst  =/= 0.U
  val zero_n   = zero1_n | zero2_n | zero3_n

  val eq1_1 = id_src1 === ex_dst & zero1_n
  val eq1_2 = id_src1 === mem_dst & zero2_n
  val eq1_3 = id_src1 === wb_dst & zero3_n

  val eq2_1 = id_src2 === ex_dst & zero1_n
  val eq2_2 = id_src2 === mem_dst & zero2_n
  val eq2_3 = id_src2 === wb_dst & zero3_n


  val eq1 = eq1_1 | eq1_2 | eq1_3
  val eq2 = eq2_1 | eq2_2 | eq2_3



  val stall = zero_n & (operator.jalr | optype.Stype | optype.Jtype | is_load | optype.Itype) & (!valid3)

//  val sIdle :: s1 :: s2 :: s3 :: sEnd :: Nil = Enum(5)
//  val state = RegInit(sIdle)
//  switch (state) {
//    is(sIdle) {
//      when(stall) {
//        state := s1
//      }
//    }
//    is(s1) {
//      state := s2
//    }
//    is(s2) {
//      state := s3
//    }
//    is(s3) {
//      state := sEnd
//    }
//    is(sEnd) {
//      when(!stall) {
//        state := sIdle
//      }
//    }
//  }
//  val flag = (state === s1 | state === s2 | state === s3)
  // after add stall, id-stage data is stopped, such operator.jalr is stopped until all 3 dst addr are 0

  io.bypassmux_sel1 := MuxCase(BypassMuxSel.normal,
    Array(
      (stall)  -> BypassMuxSel.normal,
      (eq1_1) -> BypassMuxSel.ex,
      (eq1_2) -> BypassMuxSel.mem,
      (eq1_3) -> BypassMuxSel.wb,
    )
  )
  io.bypassmux_sel2 := MuxCase(BypassMuxSel.normal,
    Array(
      (stall)              -> BypassMuxSel.normal,
      (optype.Itype)      -> BypassMuxSel.normal,//
      (eq2_1) -> BypassMuxSel.ex,
      (eq2_2) -> BypassMuxSel.mem,
      (eq2_3) -> BypassMuxSel.wb,
    )
  )
  io.stall := stall

}



//class Staller extends Module{
//  val io = IO(new Bundle{
//    val addr1   =   Input (UInt(5.W))
//    val addr2   =   Input (UInt(5.W))
//    val ex_dst  =   Input (UInt(5.W))
//    val mem_dst =   Input (UInt(5.W))
//    val wb_dst  =   Input (UInt(5.W))
//    val optype  =   Flipped(new Optype)
//    val operator=   Flipped(new Operator)
//    val stall   =   Output(Bool())
//    val bypass  =   Output(UInt(2.W))
//  })
//  /* interface */
//  val addr1   =   io.addr1
//  val addr2   =   io.addr2
//  val ex_dst  =   io.ex_dst
//  val mem_dst =   io.mem_dst
//  val wb_dst  =   io.wb_dst
//  val optype  =   io.optype
//  val operator=   io.operator
//
//  val eq1 = (addr1 =/= 0.U | addr2 =/= 0.U ) &
//            ((addr1 === ex_dst & ex_dst =/= 0.U) | (addr1 === mem_dst & mem_dst =/= 0.U) | (addr1 === wb_dst & wb_dst =/= 0.U))&
//            (optype.Itype | optype.Rtype | optype.Btype | optype.Jtype  | optype.Stype)
//  val eq2 = (addr1 =/= 0.U | addr2 =/= 0.U ) &
//            ((addr2 === ex_dst & ex_dst =/= 0.U) | (addr2 === mem_dst & mem_dst =/= 0.U) | (addr2 === wb_dst & wb_dst =/= 0.U)) &
//            (optype.Itype | optype.Rtype | optype.Btype | optype.Jtype  | optype.Stype)
//
//  //val jalr_cond = (0.U =/= ex_dst | 0.U =/= mem_dst | 0.U =/= wb_dst) & operator.jalr//分支跳转添加空泡
//  /* output */
//  io.stall := eq1 | eq2
//}
