import chisel3._
import chisel3.util._

//object ICache {
//
//}
//
//
class ICache extends Module{
  val io = IO(new Bundle{
      val in     = new PCUOut
      val master = new AXI4
      val out    = new IFUOut
  })
  val prev = io.in
  val memory = io.master
  val next = io.out

  private val sIdle :: sLookup :: sMissIssue :: sMissCatch :: sMissEnd :: Nil = Enum(4)
  val curState = RegInit(init = sMissIssue)
  val nState = Wire(UInt(sIdle.getWidth.W))


  switch(curState){
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
      next.valid := prev.valid
      prev.ready := true.B
    }
  }



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