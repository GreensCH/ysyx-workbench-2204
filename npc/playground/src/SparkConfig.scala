import chisel3._



object SparkConfig{
  val ysyxSoC: Boolean = false
  var StartAddr = "h80000000".U(64.W)


  val ICache: Boolean = true
  val MEMU: Int = 2//0 -dpic 1-axi 2-dcache
  val CacheHitCount: Boolean = false
  val RealMDU: Boolean = false
  val ChiselMDU: Boolean = true
  val Debug: Boolean = true
  val Printf: Boolean = false
}
