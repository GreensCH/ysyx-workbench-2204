import chisel3._
import chisel3.util._

class IDFW extends Bundle{
  val fw_src1_data   = Input(UInt(64.W))
  val fw_src2_data   = Input(UInt(64.W))
  val fw_ready    =  Input(Bool())
  val optype      =   new Optype
  val operator    =   new Operator
  val src1_addr   =   Output (UInt(5.W))
  val src2_addr   =   Output (UInt(5.W))
  val src1_data   =   Output (UInt(64.W))
  val src2_data   =   Output (UInt(64.W))
  val test_pc  = Output(UInt(32.W))
}

class EX2FW extends Bundle{
  val is_load = Output(Bool())
  val dst_addr = Output (UInt(5.W))
  val dst_data = Output (UInt(64.W))
  val test_pc  = Output(UInt(32.W))
}

class MEM2FW extends Bundle{
  val dst_addr_1 = Output(UInt(5.W))
  val dst_data_1 = Output(UInt(64.W))
  val test_pc_1  = Output(UInt(32.W))
  val dst_addr_2 = Output(UInt(5.W))
  val dst_data_2 = Output(UInt(64.W))
  val test_pc_2  = Output(UInt(32.W))
}

class WB2FW extends Bundle{
  val dst_addr = Output (UInt(5.W))
  val dst_data = Output (UInt(64.W))
  val test_pc  = Output(UInt(32.W))
}

//class FW2RegEX extends Bundle{
//  val bubble = Output(Bool())
//}
//class FW2PC extends Bundle{
//  val stall = Output(Bool())
//}
//class FW2RegID extends Bundle{
//  val stall = Output(Bool())
//}

class FWU extends Module{
  val io = IO(new Bundle() {
    val idu  = Flipped(new IDFW)
    val exu  = Flipped(new EX2FW)
    val memu = Flipped(new MEM2FW)
    val wbu  = Flipped(new WB2FW)
  })
  val idb = io.idu
  val exb = io.exu
  val memb = io.memu
  val wbb = io.wbu

  val ex_is_load = exb.is_load
  val optype   = idb.optype
  val operator = idb.operator
  val id_data1 = idb.src1_data
  val id_addr1 = idb.src1_addr
  val id_data2 = idb.src2_data
  val id_addr2 = idb.src2_addr
  val ex_data  = exb.dst_data
  val ex_addr  = exb.dst_addr
  val mem_data = memb.dst_data_2
  val mem_addr = memb.dst_addr_2
  val wb_data  = wbb.dst_data
  val wb_addr  = wbb.dst_addr

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

  val pre_is_load = (eq1_1 | eq2_1) & (ex_is_load)
  dontTouch(pre_is_load)

  idb.fw_src1_data := MuxCase(id_data1,
    Array(
      (eq1_1) -> ex_data,
      (eq1_2) -> mem_data,
      (eq1_3) -> wb_data
    )
  )

  idb.fw_src2_data := MuxCase(id_data2,
    Array(
      (optype.Itype) -> id_data2,
      (eq2_1) -> ex_data,
      (eq2_2) -> mem_data,
      (eq2_3) -> wb_data
    )
  )

  io.idu.fw_ready := !pre_is_load
//  io.fw2regex.bubble := pre_is_load
//  io.fw2regid.stall := pre_is_load
//  io.fw2pc.stall := pre_is_load

  if(!SparkConfig.Debug){
    idb.test_pc := DontCare
    exb.test_pc := DontCare
    memb.test_pc_1 := DontCare
    memb.test_pc_2 := DontCare
    wbb.test_pc := DontCare
  }else{
    idb.test_pc := DontCare
    exb.test_pc := DontCare
    memb.test_pc_1 := DontCare
    memb.test_pc_2 := DontCare
    wbb.test_pc := DontCare
  }
}

object FWU{
  def apply(idu: IDFW, exu: EX2FW, memu: MEM2FW, wbu: WB2FW): Unit ={
    val fwu = Module(new FWU)
    fwu.io.idu <> idu
    fwu.io.exu <> exu
    fwu.io.memu <> memu
    fwu.io.wbu <> wbu
  }
}
