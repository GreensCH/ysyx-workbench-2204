import chisel3._

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

class CsrHit extends Bundle {
  val is_mepc     = Output(Bool())
  val is_mtvec    = Output(Bool())
  val is_mstatus  = Output(Bool())
  val is_mie      = Output(Bool())
  val is_mcause   = Output(Bool())
  val is_mip      = Output(Bool())
  val is_mtime    = Output(Bool())
  val is_mcycle   = Output(Bool())
  val is_mhartid  = Output(Bool())
}

class ID2MDU extends Bundle {
  val mul_signed = Output(UInt(1.W))
  val is_mu = Output(Bool())
  val mul_32 = Output(Bool())
  val div_signed = Output(UInt(2.W))
  val is_div = Output(Bool())
  val is_du  = Output(Bool())
}

class ID2EX extends Bundle{
  val alu_src1  = Output(UInt(64.W))
  val alu_src2  = Output(UInt(64.W))
  val salu_src1 = Output(SInt(64.W))
  val salu_src2 = Output(SInt(64.W))
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
  val csr_we      = Output(Bool())
//  val csr_idx     = Output(UInt(12.W))
  val csr_hit     = new CsrHit
  val mdu_op = Output(new ID2MDU)
  val zimm        = Output(UInt(5.W))// also rs1 index
  val rd_idx      = Output(UInt(5.W))
  val intr        = Output(Bool())
  val exec        = Output(Bool())
  val mret        = Output(Bool())
  val exce_code   = Output(UInt(4.W))
  val pc          = Output(UInt(64.W))
  val is_iem      = Output(Bool())
}

class ID2MEM extends Bundle{
  val fencei        = Output(Bool())
  val size          = new SrcSize
  val sext_flag     = Output(Bool())
  val memory_rd_en  = Output(Bool())
  val memory_we_en  = Output(Bool())
}

class ID2WB extends Bundle{
  val test_pc       = Output(UInt(64.W))
  val test_device    = Output(Bool())
  val test_inst     = Output(UInt(32.W))
  val intr_exce_ret = Output(Bool())
  val fencei        = Output(Bool())
  val ebreak        = Output(Bool())
  val wb_sel        = Output(Bool())
  val regfile_we_en = Output(Bool())
  val regfile_we_addr = Output(UInt(5.W))
}

class EX2MEM extends Bundle{
  val addr    = Output(UInt(39.W))
  val we_data = Output(UInt(64.W))
  val we_mask = Output(UInt(8 .W))
}

class EX2WB extends Bundle{
  val result_data = Output(UInt(64.W))
}

class MEM2WB extends Bundle{
  val memory_data = Output(UInt(64.W))
}