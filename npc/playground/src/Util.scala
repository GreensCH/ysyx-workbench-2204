import chisel3._
import chisel3.util._

object Util {

}
//def SETX(a:UInt, b:Int):UInt = Cat(Fill(64-b, a(b-1)) ,a(b-1,0))(63,0);
object  Sext{
  def apply (data:UInt, pos:Int) : UInt = {
     printf(p"NPC@data:$data, pos:$pos\n")
    // return 0.U(64.W)
    val result = Cat(Fill(128, data(pos - 1)), data(pos - 1, 0))
    "hffff_ffff".U(64.W)
//    Cat(0.U, result(result.getWidth - 8, 0))
  }
}