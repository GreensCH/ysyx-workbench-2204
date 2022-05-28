import chisel3._
import chisel3.util._

object Util {

}
//def SETX(a:UInt, b:Int):UInt = Cat(Fill(64-b, a(b-1)) ,a(b-1,0))(63,0);

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = {
//     printf(p"NPC@data:${Hexadecimal(data)}, pos:$pos\n")
    Cat(Fill(128 - pos, data(pos - 1)), data(pos - 1, 0))//(data.getWidth - 1, 0)
//    "hffff_ffff".U(64.W)
  }
}