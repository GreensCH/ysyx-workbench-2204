import chisel3._
import chisel3.util._

object Util {

}

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = data//Cat(Fill(64, data(pos - 1)), data(pos - 1, 0))(63, 0)
}