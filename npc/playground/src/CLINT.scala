import chisel3._
import chisel3.util._

class CLINT extends extends Module{
  val io = IO(new Bundle{
    val addr = Input(CacheCfg.)
    val intr = Output(Bool())
  })

}
