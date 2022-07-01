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

//  protected val sIdle :: sLookup :: sMissIssue :: sMissCatch :: sMissEnd :: Nil = Enum(4)
//  protected val curState = RegInit(init = sMissIssue)
//  protected val nState = Wire(UInt(sIdle.getWidth.W))
//
//
//  switch(curState){
//    is (sIdle){
//      next.valid := false.B
//      prev.ready := true.B
//    }
//    is (sMissIssue) {
//      next.valid := false.B
//      prev.ready := false.B
//    }
//    is (sMissCatch){
//      next.valid := false.B
//      prev.ready := false.B
//    }
//    is (sMissEnd){
//      next.valid := true.B
//      prev.ready := true.B
//    }
//  }

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