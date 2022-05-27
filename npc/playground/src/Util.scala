import chisel3._
import chisel3.util._

object Util {
  def sext(data:UInt, pos:Int) : UInt = Cat(Fill(64 - 3, data(3 - 1)), data(3 - 1, 0))(63, 0)
}
