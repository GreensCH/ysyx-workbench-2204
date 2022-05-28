import chisel3._
import chisel3.util._

//object Util {
//  def sext
//}

object  sext{
  def apply (data:UInt, pos:Int) : UInt = Cat(Fill(64 - pos, data(pos - 1)), data(pos - 1, 0))(63, 0)
}