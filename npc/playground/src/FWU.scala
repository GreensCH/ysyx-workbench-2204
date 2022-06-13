import chisel3._
import chisel3.util._

class FW2ID extends Bundle{
  val src1_data = Output(UInt(64.W))
  val src2_data = Output(UInt(64.W))
}

class ID2FW extends Bundle{
  val optype      =   new Optype
  val operator    =   new Operator
  val src1_addr   =   Output (UInt(5.W))
  val src2_addr   =   Output (UInt(5.W))
  val src1_data   =   Output (UInt(64.W))
  val src2_data   =   Output (UInt(64.W))
}

class EX2FW extends Bundle{
  val dst_addr = Output (UInt(5.W))
  val dst_data = Output (UInt(64.W))
}

class MEM2FW extends Bundle{
  val dst_addr = Output (UInt(5.W))
  val dst_data = Output (UInt(64.W))
}

class WB2FW extends Bundle{
  val dst_addr = Output (UInt(5.W))
  val dst_data = Output (UInt(64.W))
}

class FW2RegEX extends Bundle{
  val bubble = Output(Bool())
//  val src1 = Output(UInt(64.W))
//  val src2 = Output(UInt(64.W))
}

class FW2PC extends Bundle{
  val stall = Output(Bool())
}

class FWU extends Module{
  val io = IO(new Bundle() {
    val id2fw = Flipped(new ID2FW)
    val ex2fw = Flipped(new EX2FW)
    val mem2fw = Flipped(new MEM2FW)
    val wb2fw = Flipped(new WB2FW)
    val fw2id = new FW2ID
    val fw2regex = new FW2RegEX
    val fw2pc = new FW2PC
  })
  val optype   = io.id2fw.optype
  val operator = io.id2fw.operator
  val id_data1 = io.id2fw.src1_data
  val id_addr1 = io.id2fw.src1_addr
  val id_data2 = io.id2fw.src2_data
  val id_addr2 = io.id2fw.src2_addr
  val ex_data  = io.ex2fw.dst_data
  val ex_addr  = io.ex2fw.dst_addr
  val mem_data = io.mem2fw.dst_data
  val mem_addr = io.mem2fw.dst_addr
  val wb_data  = io.wb2fw.dst_data
  val wb_addr  = io.wb2fw.dst_addr

  val zero1_n = ex_addr  =/= 0.U
  val zero2_n = mem_addr =/= 0.U
  val zero3_n = wb_addr  =/= 0.U
  val zero_n   = zero1_n | zero2_n | zero3_n

  val eq1_1 = id_addr1 === ex_addr  & zero1_n
  val eq1_2 = id_addr1 === mem_addr & zero2_n
  val eq1_3 = id_addr1 === wb_addr  & zero3_n
  val eq2_1 = id_addr2 === ex_addr  & zero1_n
  val eq2_2 = id_addr2 === mem_addr & zero2_n
  val eq2_3 = id_addr2 === wb_addr  & zero3_n

  io.fw2id.src1_data := MuxCase(id_data1,
    Array(
      (eq1_1) -> ex_data,
      (eq1_2) -> mem_data,
      (eq1_3) -> wb_data
    )
  )

  io.fw2id.src2_data := MuxCase(id_data2,
    Array(
      (optype.Itype) -> id_data2,
      (eq1_1) -> ex_data,
      (eq1_2) -> mem_data,
      (eq1_3) -> wb_data
    )
  )

  io.fw2regex.bubble := false.B

  /*
  * function interface
  * target PCU
  * direct out
  */
  io.fw2pc.stall := false.B
}
