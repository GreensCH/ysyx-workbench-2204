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
  val pcOut = io.in
  val memory = io.master
  val ifOut = io.out

  private val sIdle :: sLookup :: sMissIssue :: sMissCatch :: sMissEnd :: Nil = Enum(4)
  val curState = RegInit(init = sMissIssue)
  val nState = Wire(UInt(sIdle.getWidth.W))

  val outList = MuxCase(
    default = List(0.U(64.W), 0.U(32.W), true.B, true.B),
    mapping = List(
      (curState === sMissIssue) -> List(0.U(64.W), 0.U(32.W), true.B, true.B),
      (curState === sMissCatch) -> List(0.U(64.W), 0.U(32.W), true.B, true.B),
      (curState === sMissEnd)   -> List(0.U(64.W), 0.U(32.W), true.B, true.B)
    )
  )

  ifOut.bits.if2id.pc := outList(0)
  ifOut.bits.if2id.inst := outList(1)

}
