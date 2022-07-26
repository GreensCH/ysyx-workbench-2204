import chisel3._
import chisel3.util._



object SparkConfig{
  val ICache: Boolean = false
  val MEMU: Int = 2//0 -dpic 1-axi 2-dcache
  val ChiselRam: Boolean = true
  val CacheHitCount: Boolean = true
  val Debug: Boolean = true
  val Printf: Boolean = false
}
