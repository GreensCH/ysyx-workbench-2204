import chisel3._
import chisel3.util._

object Util {

}
//def SETX(a:UInt, b:Int):UInt = Cat(Fill(64-b, a(b-1)) ,a(b-1,0))(63,0);
object  Sext{
  def apply (data:UInt, pos:Int) : Int = {
    // printf(s"NPC@data:$data, pos:$pos\n")
    // return 0.U(64.W)
    val result = Cat(Fill(128, data(pos - 1)), data(pos - 1, 0))
    printf(s"NPC@${data.getWidth} \n")
    "hf0ff_ffff".U(64.W).asUInt()
//    result(result.getWidth - 1, 0)
  }
}