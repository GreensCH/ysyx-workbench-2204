import chisel3._
import chisel3.util._

object Util {

}

object  Sext{
  def apply (data:UInt, pos:Int) : UInt = Cat(Fill(128 - pos, data(pos - 1)), data(pos - 1, 0))//(data.getWidth - 1, 0)
}

object  Replace{//size is byte scalar. start, len is bit scalar
  def apply (src: UInt, token: UInt, rshift: UInt, lshift: UInt, lshift2: UInt, len: Int = 128) : UInt = {

    val H1 = (src >> rshift).asTypeOf(UInt(len.W))
    val H = (H1 << rshift).asTypeOf(UInt(len.W))
    val L1 = (src << lshift).asTypeOf(UInt(len.W))
    val L = (L1 >> lshift).asTypeOf(UInt(len.W))
    val M = (token << lshift2).asTypeOf(UInt(len.W))


    H | M | L
  }
}

object Useless{
  def apply (src: UInt): Unit = {
    val useless = Wire(Bool())
    useless := Cat(false.B, src, false.B).andR()
  }

}