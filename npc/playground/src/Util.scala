import chisel3._
import chisel3.util._

object Util {

}

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = Cat(Fill(128 - pos, data(pos - 1)), data(pos - 1, 0))//(data.getWidth - 1, 0)
}

object  Replace{//size is byte scalar. start, len is bit scalar
  def apply (src: UInt, token: UInt, left: UInt, right: UInt, len: Int = 128) : UInt = {

    val _L = ((src >> right) << right).asTypeOf(UInt(len.W))
    val _R = ((src << left) >> left).asTypeOf(UInt(len.W))
    val _M = (token << left).asTypeOf(UInt(len.W))

    _L | _M  | _R
  }
}