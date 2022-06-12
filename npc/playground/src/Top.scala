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

  val stall = true.B//io.stall
  val reg_ex = Module(new EXReg)
  val reg_mem = Module(new MEMReg)
  val reg_wb = Module(new WBReg)

  val staller = Module(new Staller)
  staller.io.id_src1 := idu.io.regfile2id.addr1
  staller.io.id_src2 := idu.io.regfile2id.addr2
  staller.io.optype := idu.io.id2ex.optype
  staller.io.operator := idu.io.id2ex.operator
  staller.io.is_load := idu.io.id2ex.is_load
  staller.io.ex_dst := reg_ex.io.out.id2wb.regfile_we_addr
  staller.io.mem_dst:= reg_mem.io.out.id2wb.regfile_we_addr
  staller.io.wb_dst := reg_wb.io.out.id2wb.regfile_we_addr
  staller.io.valid1 := reg_ex.io.valid_out
  staller.io.valid2 := reg_mem.io.valid_out
  staller.io.valid3 := reg_wb.io.valid_out

  val bypassmux = Module(new ByPassMux)
  bypassmux.io.sel1 := staller.io.bypassmux_sel1
  bypassmux.io.sel2 := staller.io.bypassmux_sel2
  bypassmux.io.id_data1 := idu.io.id2ex.src1
  bypassmux.io.id_data2 := idu.io.id2ex.src2
  bypassmux.io.ex_data := exu.io.ex2mem.we_data
  bypassmux.io.mem_data := memu.io.ex2mem.we_data
  bypassmux.io.wb_data := wbu.io.wb2regfile.data
  val new_id2ex = Wire(new ID2EX)
  new_id2ex := idu.io.id2ex
  new_id2ex.src1 := bypassmux.io.src_data1
  new_id2ex.src2 := bypassmux.io.src_data2

  ifu.io.stall := staller.io.stall // PC
  reg_ex.io.stall := staller.io.stall // bubble generate
  reg_ex.io.valid_in := ~(staller.io.stall)
  reg_mem.io.stall := false.B
  reg_wb.io.stall := false.B
  /* cpu interconnection */
  /* IF(PC) from ID also branch transfer path*/
  ifu.io.id2pc := idu.io.id2pc          // Branch change pa path
  /* ID from IF */
  idu.io.if2id := ifu.io.if2id          // IDU in
  reg_ex.io.in.id2ex := new_id2ex    // Bypass Mux out to Reg
  reg_ex.io.in.id2mem := idu.io.id2mem  // PreReg to Reg
  reg_ex.io.in.id2wb := idu.io.id2wb    // PreReg to Reg
  reg_ex.io.valid_in := staller.io.stall // Stall Csig to Reg
  /* EX from ID */
  exu.io.id2ex := reg_ex.io.out.id2ex           // EXU in PreReg(exu.io.id2ex := idu.io.id2ex)
  reg_mem.io.in.ex2mem := exu.io.ex2mem         // EXU out to Reg
  reg_mem.io.in.ex2wb  := exu.io.ex2wb          // EXU out to Reg
  reg_mem.io.in.id2mem := reg_ex.io.out.id2mem  // PreReg to Reg
  reg_mem.io.in.id2wb := reg_ex.io.out.id2wb    // PreReg to Reg
  reg_mem.io.valid_in := reg_ex.io.valid_out   // PreReg Csig to Reg
  /* MEM from ID EX */
  memu.io.id2mem := reg_mem.io.out.id2mem     // MEMU in PreReg(memu.io.id2mem := idu.io.id2mem)
  memu.io.ex2mem := reg_mem.io.out.ex2mem     // MEMU in PreReg(memu.io.ex2mem := exu.io.ex2mem)
  reg_wb.io.in.mem2wb := memu.io.mem2wb       // MEMU out to Reg
  reg_wb.io.in.id2wb  := reg_mem.io.out.id2wb // PreReg to Reg
  reg_wb.io.in.ex2wb  := reg_mem.io.out.ex2wb // PreReg to Reg
  reg_wb.io.valid_in := reg_mem.io.valid_out   // PreReg Csig to Reg
  /* WB from ID EX MEM */
  wbu.io.id2wb := reg_wb.io.out.id2wb// WBU in PreReg（wbu.io.id2wb := idu.io.id2wb）
  wbu.io.ex2wb := reg_wb.io.out.ex2wb// WBU in PreReg（wbu.io.ex2wb := exu.io.ex2wb）
  wbu.io.mem2wb := reg_wb.io.out.mem2wb// WBU in PreUnit（wbu.io.mem2wb:= memu.io.mem2wb）

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
