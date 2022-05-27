import chisel3._
import chisel3.util._

object Util {
  def sext(data:UInt, pos:Int) : UInt = 0.U(64.W)//Cat(Fill(64 - pos, data(pos - 1)), data(pos - 1, 0))(63, 0)
}
