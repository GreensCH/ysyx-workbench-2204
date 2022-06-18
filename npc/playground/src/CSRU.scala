import chisel3._

class CSRU extends Module{
  val io = IO(new Bundle() {
    val inst = Input(UInt(64.W))
  })
  io.inst <> DontCare
}
