import chisel3._
import chisel3.util._

class ID2FW extends Bundle{
  val src1_addr   =   Input (UInt(5.W))
  val src2_addr   =   Input (UInt(5.W))
  val src1_data   =   Input (UInt(64.W))
  val src2_data   =   Input (UInt(64.W))
}

class EX2FW extends Bundle{
  val dst_addr = Input (UInt(5.W))
  val dst_data = Input (UInt(64.W))
}

class MEM2FW extends Bundle{
  val dst_addr = Input (UInt(5.W))
  val dst_data = Input (UInt(64.W))
}

class WB2FW extends Bundle{
  val dst_addr = Input (UInt(5.W))
  val dst_data = Input (UInt(64.W))
}

class FW2RegEX extends Bundle{
  val bubble = Output(Bool())
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
}

class FW2PC extends Bundle{
  val stall = Output(Bool())
}

class FWU {
  val io = new Bundle() {
    val id2fw = Flipped(new ID2FW)
    val ex2fw = Flipped(new EX2FW)
    val mem2fw = Flipped(new MEM2FW)
    val wb2fw = Flipped(new WB2FW)
    val fw2regex = new FW2RegEX
    val fw2pc = new FW2PC
  }
  io.id2fw.src1_addr := DontCare
  io.id2fw.src2_addr := DontCare
  io.ex2fw := DontCare
  io.mem2fw := DontCare
  io.wb2fw := DontCare
  io.fw2regex.src1 := io.id2fw.src1_data
  io.fw2regex.src2 := io.id2fw.src2_data
  io.fw2regex.bubble := false.B

  /*
  * function interface
  * target PCU
  * direct out
  */
  io.fw2pc.stall := false.B
}
