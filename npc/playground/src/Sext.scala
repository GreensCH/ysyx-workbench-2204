import chisel3._
import chisel3.util._

//object Util {
//  def sext
//}

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = Cat(Fill(63, data(pos - 1)), data(1, 0))(63, 0)
}