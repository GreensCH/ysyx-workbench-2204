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
  private val prev = io.prev
  private val memory = io.master
  private val next = io.next
  private val rdyNext = next.ready

  protected val sIdle :: sLookup :: sMissIssue :: sMissCatch :: sMissEnd :: Nil = Enum(5)
  protected val next_state = Wire(UInt(sIdle.getWidth.W))
  protected val curr_state = RegEnable(init = sMissEnd, next = next_state, enable = rdyNext)

  switch(curr_state){
    next_state := sMissIssue
    is (sIdle){
      when(prev.valid) { next_state := sMissIssue }
    }
    is (sMissIssue) {
      next_state := 0.U
    }
    is (sMissCatch){
      next_state := 0.U
    }
    is (sMissEnd){
      next_state := 0.U
    }
  }
  // Logic output
  switch(curr_state){
    is (sIdle){
      next.valid := false.B
      prev.ready := true.B
    }
    is (sMissIssue) {
      next.valid := false.B
      prev.ready := false.B
    }
    is (sMissCatch){
      next.valid := false.B
      prev.ready := false.B
    }
    is (sMissEnd){
      next.valid := true.B
      prev.ready := true.B
    }
  }
  // AXI
  val axi_ar_out = AXI4BundleA
  val axi_aw_out = AXI4BundleA
  val axi_r_in = memory.r
  // AXI output
  axi_r_in.ready := true.B
  // DontCare
  prev <> DontCare
  memory <> DontCare
  next <> DontCare
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