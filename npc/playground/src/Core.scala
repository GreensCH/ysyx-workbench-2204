import chisel3._
import chisel3.util._



object SparkConfig{
  val ICache: Boolean = true
  val DCache: Boolean = true
  val ChiselRam: Boolean = true
  val CacheHitCount: Boolean = true
  val debug: Boolean = true
  val printf: Boolean = false
}
