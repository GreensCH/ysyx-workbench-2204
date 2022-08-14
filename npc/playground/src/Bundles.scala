import chisel3._
import chisel3.util._

class MyDecoupledIO extends Bundle{
  val ready = Input (Bool())
  val valid = Output(Bool())
  val bits  = new Bundle{}
}

class PC2IF extends Bundle {
  val pc = Output(UInt(64.W))
}

class IF2ID extends Bundle {
  val inst  =   Output(UInt(32.W))
  val pc    =   Output(UInt(64.W))
}


class ID2EX extends Bundle{
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val src3 = Output(UInt(64.W))
  val operator    =   new Operator
  val optype      =   new Optype
  val srcsize     =   new SrcSize
  val is_load     =   Output(Bool())
  val is_save     =   Output(Bool())
  val div_inf     =   Output(Bool())//use for divisor is zero
  // csru in
  val csr_idx     =   Output(UInt(12.W))
  val zimm        =   Output(UInt(5.W))// also rs1 index
  val rd_idx      =   Output(UInt(5.W))
}
class ID2MEM extends Bundle{
  val size      = new SrcSize
  val sext_flag    = Output(Bool())
  val memory_rd_en = Output(Bool())
  val memory_we_en = Output(Bool())
}
class ID2WB extends Bundle{
  val test_pc       = Output(UInt(64.W))
  val test_inst     = Output(UInt(32.W))
  val intr_exce_ret = Output(Bool())
  val ebreak        = Output(Bool())
  val wb_sel        = Output(Bool())
  val regfile_we_en = Output(Bool())
  val regfile_we_addr = Output(UInt(5.W))
}
class EX2MEM extends Bundle{
  val addr    = Output(UInt(64.W))
  val we_data = Output(UInt(64.W))
  val we_mask = Output(UInt(8 .W))
}
class EX2WB extends Bundle{
  val result_data = Output(UInt(64.W))
}

class MEM2WB extends Bundle{
  val memory_data = Output(UInt(64.W))
  val test_is_device = Output(Bool())// debug
}