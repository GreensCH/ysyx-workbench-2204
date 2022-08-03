import chisel3._


/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
//    val pc = Output(UInt(64.W))
  })
  val regfile = Module(new RegFile)

  val ifu = Module(new IFU)
  val idu = Module(new IDU)
  val exu = Module(new EXU)
  val memu = Module(new MEMU)
  val wbu = Module(new WBU)

  /* cpu interconnection */
  // stage connection
  ifu.io.id2pc := idu.io.id2pc
  ifu.io.inst := io.inst

  idu.io.if2id := ifu.io.if2id

  exu.io.id2ex := idu.io.id2ex

  memu.io.id2mem := idu.io.id2mem
  memu.io.ex2mem := exu.io.ex2mem

  wbu.io.id2wb := idu.io.id2wb
  wbu.io.ex2wb := exu.io.ex2wb
  wbu.io.mem2wb:= memu.io.mem2wb

  // regfile connection
  regfile.io.idu.en := idu.io.regfile2id.en
  regfile.io.idu.addr1 := idu.io.regfile2id.addr1
  regfile.io.idu.addr2 := idu.io.regfile2id.addr2
  idu.io.regfile2id.data1 := regfile.io.idu.data1
  idu.io.regfile2id.data2 := regfile.io.idu.data2
  regfile.io.wbu.data := wbu.io.wb2regfile.data
  regfile.io.wbu.addr := wbu.io.wb2regfile.addr
  regfile.io.wbu.en := wbu.io.wb2regfile.en
  /* monitor and top interface */
//  io.inst := ifu.io.if2id.inst
//  io.pc := ifu.io.if2id.pc
//  val monitor = Module(new Monitor)
//  monitor.io.pc := ifu.io.if2id.pc
//  monitor.io.inst :=ifu.io.if2id.inst

}
