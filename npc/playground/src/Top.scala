import chisel3._


/**
  * Spark CPU: A Single Cycle Processor,
  * CPU powered by RV64IM instruction set 
  * 
  */

class Top extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val stall = Input(Bool())
//    val pc = Output(UInt(64.W))
  })
  val regfile = Module(new RegFile)

  val ifu = Module(new IFU)
  val idu = Module(new IDU)
  val exu = Module(new EXU)
  val memu = Module(new MEMU)
  val wbu = Module(new WBU)

  val reg_if2id = Module(new IF2IDReg)
  val reg_id2ex = Module(new ID2EXReg)
  val reg_id2mem = Module(new ID2MEMReg)
  val reg_id2wb = Module(new ID2WBReg)
  val reg_exe2mem = Module(new EX2MEMReg)
  val reg_exe2wb = Module(new EX2WBReg)
//  val reg_mem2wb = Module(new MEM2WBReg)

  val stall = io.stall
  /* cpu interconnection */
  /* IF(PC) from ID also branch transfer path*/
  ifu.io.stall := stall // stall
  ifu.io.id2pc := idu.io.id2pc

  /* ID from IF */
  reg_if2id.io.stall := stall // register stall
  reg_if2id.io.in := ifu.io.if2id // ID2Reg
  idu.io.if2id := reg_if2id.io.out // Reg2IF

  /* EX from ID */
  reg_id2ex.io.stall := stall // register stall
  reg_id2ex.io.in := idu.io.id2ex // ID2Reg
  exu.io.id2ex := reg_id2ex.io.out // Reg2EX

  /* MEM from ID EX */
  reg_id2mem.io.stall := stall // register stall
  reg_id2mem.io.in := idu.io.id2mem // ID2Reg
  memu.io.id2mem := reg_id2mem.io.out // Reg2MEM
  reg_exe2mem.io.stall := stall // register stall
  reg_exe2mem.io.in := exu.io.ex2mem // EX2Reg
  memu.io.ex2mem := reg_exe2mem.io.in // Reg2MEM

  /* WB from ID EX MEM */
  reg_id2wb.io.stall := stall // register stall
  reg_id2wb.io.in := idu.io.id2wb // ID2Reg
  wbu.io.id2wb := reg_id2wb.io.out // Reg2MEM
  reg_exe2wb.io.stall := stall // register stall
  reg_exe2wb.io.in := exu.io.ex2wb // EX2Reg
  wbu.io.ex2wb := reg_exe2wb.io.out // Reg2MEM
  wbu.io.mem2wb:= memu.io.mem2wb

  /* Regfile Connection */
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
