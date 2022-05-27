import chisel3._
//import chisel3.util._
//import chisel3.experimental._


/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
    val inst = Output(UInt(32.W))
    val pc = Output(UInt(64.W))
  })
  val pcu = Module(new PCU)
  val ifu = Module(new IFU)
  val idu = Module(new IDU)
  val regfile = Module(new RegFile)
  val memory = Module(new MemoryInf)
  val exu = Module(new EXU)
  val memu = Module(new MEMU)
  val wbu = Module(new WBU)
}
