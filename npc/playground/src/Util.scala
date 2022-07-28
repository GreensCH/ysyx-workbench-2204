import chisel3._
import chisel3.util._

object Util {

}

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = Cat(Fill(128 - pos, data(pos - 1)), data(pos - 1, 0))//(data.getWidth - 1, 0)
}

object  Replace{//size is byte scalar. start, len is bit scalar
  def apply (src: UInt, token: UInt, rshift: UInt, lshift: UInt, len: Int = 128) : UInt = {

    val L = ((src >> rshift) << rshift).asTypeOf(UInt(len.W))
    val R = ((src << lshift) >> lshift).asTypeOf(UInt(len.W))
    val M = (token << rshift).asTypeOf(UInt(len.W))

    L | M  | R
  }
}