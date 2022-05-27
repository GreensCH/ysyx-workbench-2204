import chisel3._
import chisel3.util._

object Util {
  def sext(data:UInt, pos:UInt) : UInt = Cat(Fill(64 - pos, data(pos - 1.U)), data(pos - 1.U, 0))(63, 0)
}
