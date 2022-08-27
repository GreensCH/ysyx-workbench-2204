import chisel3._
import chisel3.util._



object SparkConfig{
  val StartAddr = "h80000000".U(64.W)
  val ICache: Boolean = true
//  val IFU: Int = 0//0 -dpic 1-axi 2-dcache
  val MEMU: Int = 2//0 -dpic 1-axi 2-dcache
  val ChiselRam: Boolean = true
  val CacheHitCount: Boolean = true
  val RealMDU: Boolean = false
  val Debug: Boolean = true
  val Printf: Boolean = false
}
