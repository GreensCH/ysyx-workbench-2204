import chisel3._
import chisel3.util._


class MEMReg extends Module{
  val io = IO(new Bundle() {
    val prev = Flipped(new EXUOut)
    val next = new EXUOut
  })
  val rdyPrev  = io.prev.ready
  val vldPrev  = io.prev.valid
  val dataPrev = io.prev.bits
  val rdyNext  = io.next.ready
  val vldNext  = io.next.valid
  val dataNext = io.next.bits
  // Left
  rdyPrev := rdyNext//RegNext(rdyNext, true.B)//rdyNext
  // Right
  vldNext := RegEnable(next = vldPrev, enable = rdyNext)
  // comp
  val data = Mux(vldPrev, dataPrev, 0.U.asTypeOf((new EXUOut).bits))
  val reg = RegEnable(next = data, enable = rdyNext)
  dataNext := reg
}
//////////////////////////////////////
class MEMU extends Module {
  val io = IO(new Bundle{
    val prev = Flipped(new EXUOut)
    val next = new MEMUOut
    val maxi  = new AXI4Master
    val mmio  = new AXI4Master
  })
  private val maxi = io.maxi
  private val mmio = io.mmio
  private val prev = io.prev
  private val next = io.next

  if(SparkConfig.MEMU == 0){
    maxi <> DontCare
    mmio <> DontCare
    MEMU.dpic_load_save(io.prev, io.next)
    next.bits.mem2wb.test_is_device := DontCare
  }else if(SparkConfig.MEMU == 1){
    MEMU.axi_load_save(io.prev, io.next, io.maxi, io.mmio)
  }else if(SparkConfig.MEMU == 2){
    val dcache = Module(new DCacheUnit)
    /*  Connection Between outer.prev and inter.icache */
    dcache.io.prev.bits.data := prev.bits
    dcache.io.prev.valid := prev.valid
    dcache.io.prev.bits.addr  := prev.bits.ex2mem.addr(CacheCfg.paddr_bits-1, 0)
    dcache.io.prev.bits.wdata := prev.bits.ex2mem.we_data
    dcache.io.prev.bits.wmask := prev.bits.ex2mem.we_mask
    dcache.io.prev.bits.size  := prev.bits.id2mem.size
    dcache.io.prev.bits.flush  :=  false.B
    /*  Connection Between outer.next and inter.icache */
    next.bits := dcache.io.next.bits.data
    dcache.io.next.ready := next.ready
    /*  Connection Between outer.maxi and inter.icache */
    dcache.io.master <> maxi
    /* Output Handshake Signals */
    next.valid := dcache.io.next.valid
    prev.ready := dcache.io.prev.ready
    // TODO
    next.bits.mem2wb.test_is_device := DontCare
    mmio <> DontCare
  }

}

class MEMUOut extends MyDecoupledIO{
  override val bits = new Bundle{
    val id2wb  = new ID2WB
    val ex2wb  = new EX2WB
    val mem2wb = new MEM2WB
  }
}

object MEMU {
  def apply(prev: EXUOut, next: MEMUOut,
            maxi: AXI4Master, mmio: AXI4Master,
            fwu: MEM2FW): MEMU ={
    val EX2MEMReg = Module(new MEMReg)
    EX2MEMReg.io.prev <> prev

    val memu = Module(new MEMU)
    memu.io.prev <> EX2MEMReg.io.next
    memu.io.maxi <> maxi
    memu.io.mmio <> mmio
    next <> memu.io.next

    // TODO
    fwu.dst_data_1 := DontCare
    fwu.dst_addr_1 := DontCare

    fwu.dst_addr_2 := memu.io.next.bits.id2wb.regfile_we_addr
    fwu.dst_data_2 := Mux(memu.io.next.bits.id2wb.wb_sel, memu.io.next.bits.mem2wb.memory_data, memu.io.next.bits.ex2wb.result_data)// wb_sel = is_load
    /* test */
    if(!SparkConfig.Debug){
      fwu.test_pc_1 := DontCare
      fwu.test_pc_2 := DontCare
    }else{
      fwu.test_pc_1 := memu.prev.bits.id2wb.test_pc
      fwu.test_pc_2 := memu.next.bits.id2wb.test_pc
    }

    memu
  }
  def bare_connect(prev: EXUOut, next: MEMUOut): Unit = {
    next.bits.id2wb <> prev.bits.id2wb
    next.bits.ex2wb <> prev.bits.ex2wb
    next.bits.mem2wb <> DontCare
    next.valid := prev.valid
    prev.ready := true.B
  }
  def bare_axi_lsu(prev: EXUOut, next: MEMUOut, maxi: AXI4Master): Unit = {
    val axi4_manager = Module(new AXI4Manager)
    axi4_manager.io.maxi <> maxi

    axi4_manager.io.in.rd_en := prev.bits.id2mem.memory_rd_en
    axi4_manager.io.in.we_en := prev.bits.id2mem.memory_we_en
    axi4_manager.io.in.data  := prev.bits.ex2mem.we_data
    axi4_manager.io.in.addr  := prev.bits.ex2mem.addr
    axi4_manager.io.in.size.qword := false.B
    axi4_manager.io.in.size.byte  := prev.bits.id2mem.size.byte
    axi4_manager.io.in.size.hword := prev.bits.id2mem.size.hword
    axi4_manager.io.in.size.word  := prev.bits.id2mem.size.word
    axi4_manager.io.in.size.dword := prev.bits.id2mem.size.dword
    axi4_manager.io.in.wmask := prev.bits.ex2mem.we_mask

    val busy = RegInit(false.B)
    when(axi4_manager.io.out.finish){
      busy := false.B
    }.otherwise{
      busy := true.B
    }

    val stage = RegInit(0.U.asTypeOf(chiselTypeOf(prev.bits)))
    when(!busy){
      stage := prev.bits
    }
    val raw_memory_data = axi4_manager.io.out.data
    val sext_memory_data = MuxCase(raw_memory_data,
      Array(
        stage.id2mem.size.byte   -> Sext(data = raw_memory_data, pos = 8),
        stage.id2mem.size.hword  -> Sext(data = raw_memory_data, pos = 16),
        stage.id2mem.size.word   -> Sext(data = raw_memory_data, pos = 32),
        stage.id2mem.size.dword  -> raw_memory_data
      )
    )
    val read_data = Mux(stage.id2mem.sext_flag, sext_memory_data, raw_memory_data)

    next.bits.id2wb := prev.bits.id2wb
    next.bits.ex2wb := prev.bits.ex2wb
    next.bits.mem2wb.memory_data := 0.U(64.W)
    next.valid := prev.valid
    when(axi4_manager.io.out.finish){
      next.bits.id2wb := stage.id2wb
      next.bits.ex2wb := stage.ex2wb
      next.bits.mem2wb.memory_data := read_data
      next.valid := true.B
    }.elsewhen(busy){
      next.bits.id2wb  := 0.U.asTypeOf(new ID2WB )
      next.bits.ex2wb  := 0.U.asTypeOf(new EX2WB )
      next.bits.mem2wb := 0.U.asTypeOf(new MEM2WB)
      next.valid := false.B
    }
    prev.ready := !(busy) | axi4_manager.io.out.finish
  }
  def axi_load_save(prev: EXUOut, next: MEMUOut, maxi: AXI4Master, mmio: AXI4Master): Unit = {
    val valid = prev.valid & (prev.bits.id2mem.memory_rd_en | prev.bits.id2mem.memory_we_en)
    val is_device = (prev.bits.ex2mem.addr(31) === 0.U(1.W)) & valid// addr < 0x8000_0000
    dontTouch(is_device)
    AXI4Master.default(maxi)
    AXI4Master.default(mmio)
    val axi4_manager = Module(new AXI4Manager)
    axi4_manager.io.maxi <> maxi
    when(is_device){
      axi4_manager.io.maxi <> mmio
    }
    axi4_manager.io.in.rd_en := prev.bits.id2mem.memory_rd_en
    axi4_manager.io.in.we_en := prev.bits.id2mem.memory_we_en
    axi4_manager.io.in.data  := prev.bits.ex2mem.we_data
    axi4_manager.io.in.addr  := prev.bits.ex2mem.addr
    axi4_manager.io.in.size.qword := false.B
    axi4_manager.io.in.size.byte  := prev.bits.id2mem.size.byte
    axi4_manager.io.in.size.hword := prev.bits.id2mem.size.hword
    axi4_manager.io.in.size.word  := prev.bits.id2mem.size.word
    axi4_manager.io.in.size.dword := prev.bits.id2mem.size.dword
    axi4_manager.io.in.wmask := prev.bits.ex2mem.we_mask

    val busy = RegInit(false.B)
    when(axi4_manager.io.out.finish){
      busy := false.B
    } .elsewhen(valid){
      busy := true.B
    }

    val stage = RegInit(0.U.asTypeOf(chiselTypeOf(prev.bits)))
    when(valid & (!busy)){
      stage := prev.bits
    }
    val raw_memory_data = axi4_manager.io.out.data
    val sext_memory_data = MuxCase(raw_memory_data,
      Array(
        stage.id2mem.size.byte   -> Sext(data = raw_memory_data, pos = 8),
        stage.id2mem.size.hword  -> Sext(data = raw_memory_data, pos = 16),
        stage.id2mem.size.word   -> Sext(data = raw_memory_data, pos = 32),
        stage.id2mem.size.dword  -> raw_memory_data
      )
    )
    val read_data = Mux(stage.id2mem.sext_flag, sext_memory_data, raw_memory_data)

    next.bits.id2wb := prev.bits.id2wb
    next.bits.ex2wb := prev.bits.ex2wb
    next.bits.mem2wb.memory_data := 0.U(64.W)
    next.valid := prev.valid
    when(axi4_manager.io.out.finish){
      next.bits.id2wb := stage.id2wb
      next.bits.ex2wb := stage.ex2wb
      next.bits.mem2wb.memory_data := read_data
      next.valid := true.B
    }.elsewhen(busy | valid){
      next.bits.id2wb  := 0.U.asTypeOf(new ID2WB )
      next.bits.ex2wb  := 0.U.asTypeOf(new EX2WB )
      next.bits.mem2wb := 0.U.asTypeOf(new MEM2WB)
      next.valid := false.B
    }
    prev.ready := !(busy | valid) | axi4_manager.io.out.finish
    /*
     Diff Test
     */
    if(!SparkConfig.Debug){
      next.bits.mem2wb.test_is_device := DontCare
    }
    else{
      val is_device_reg = RegInit(false.B)
      next.bits.mem2wb.test_is_device := Mux(is_device, true.B, is_device_reg)
      when(is_device) { is_device_reg := true.B }
      .elsewhen(axi4_manager.io.out.finish){ is_device_reg := false.B }
    }
  }

//  def dcache_load_save(prev: EXUOut, next: MEMUOut, maxi: AXI4Master): Unit = {
//    /*
//      DCache Connection
//     */
//    val icache = Module(new DCache(2.U(AXI4Parameters.idBits.W)))
//    /*  Connection Between outer.prev and inter.icache */
//    icache.io.prev.bits.data.pc2if.pc := prev.bits.pc2if.pc
//    icache.io.prev.bits.addr := prev.bits.pc2if.pc
//    icache.io.prev.valid := prev.valid
//    /*  Connection Between outer.next and inter.icache */
//    next.bits.if2id := icache.io.next.bits.data.if2id
//    icache.io.next.ready := next.ready
//    /*  Connection Between outer.maxi and inter.icache */
//    icache.io.master <> io.maxi
//    /* Output Handshake Signals */
//    prev.ready := next.ready & icache.io.prev.ready
//    next.valid := prev.valid & icache.io.next.valid
//  }
  def dpic_load_save(prev: EXUOut, next: MEMUOut): Unit = {
    prev.ready := next.ready
    next.valid := prev.valid
    next.bits.ex2wb := prev.bits.ex2wb
    next.bits.id2wb := prev.bits.id2wb
    val idu = prev.bits.id2mem
    val exu = prev.bits.ex2mem
    val wbu = next.bits.mem2wb
    /* MEMU interface */
    val byte  = idu.size.byte
    val hword = idu.size.hword
    val word  = idu.size.word
    val dword = idu.size.dword
    val sext_flag = idu.sext_flag
    /* memory bus instance */
    val memory_inf = Module(new MemoryInf).io
    /* memory interface */
    val rd_en   = idu.memory_rd_en
    val rd_addr = exu.addr
    val rd_data = memory_inf.rd_data
    val we_en   = idu.memory_we_en
    val we_addr = exu.addr
    val we_data = exu.we_data
    val we_mask = exu.we_mask
    memory_inf.rd_en   := rd_en
    memory_inf.rd_addr := rd_addr
    memory_inf.we_en   := we_en
    memory_inf.we_addr := we_addr
    memory_inf.we_data := we_data
    memory_inf.we_mask := we_mask

    val raw_memory_data = MuxCase(memory_inf.rd_data,
      Array(
        byte   -> memory_inf.rd_data(7,  0),
        hword  -> memory_inf.rd_data(15, 0),
        word   -> memory_inf.rd_data(31, 0),
        dword  -> memory_inf.rd_data,
      )
    )
    val sext_memory_data = MuxCase(memory_inf.rd_data,
      Array(
        byte   -> Sext(data = memory_inf.rd_data(7,  0), pos = 8),
        hword  -> Sext(data = memory_inf.rd_data(15, 0), pos = 16),
        word   -> Sext(data = memory_inf.rd_data(31, 0), pos = 32),
        dword  -> memory_inf.rd_data
      )
    )
    /* mem2wb interface */
    wbu.memory_data := Mux(sext_flag, sext_memory_data, raw_memory_data)
  }
  def _axi_load_save(prev: EXUOut, next: MEMUOut, maxi: AXI4Master): Unit = {
    /*
    Stage
    */
    val sIDLE     = 0.U(3.W)
    val sREAD_1   = 1.U(3.W)
    val sREAD_2   = 2.U(3.W)
    val sWRITE_1  = 3.U(3.W)
    val sWRITE_2  = 4.U(3.W)
    val sEND      = 5.U(3.W)
    val next_state = Wire(UInt(sIDLE.getWidth.W))
    val curr_state = RegNext(init = sIDLE, next = next_state)
    /* Lookup Stage */
    val lkup_stage_en = Wire(Bool())
    val lkup_stage_in = Wire(Output(chiselTypeOf(prev)))
    val lkup_stage_out = RegEnable(init = 0.U.asTypeOf(lkup_stage_in),next = lkup_stage_in, enable = lkup_stage_en)
    /* AXI Read Channel Stage */
    val r_stage_in = Wire(UInt(AXI4Parameters.dataBits.W))
    val r_stage_out = RegNext(init = 0.U(AXI4Parameters.dataBits.W), next = r_stage_in)
    /*
    AXI Interface Default Connection(Read-Write)
   */
    AXI4BundleA.clear(maxi.ar)
    AXI4BundleR.default(maxi.r)
    AXI4BundleA.clear(maxi.aw)
    AXI4BundleW.clear(maxi.w)
    AXI4BundleB.default(maxi.b)
    /*
    Internal Control Signal
    */
    /* axi */
    val r_last = maxi.r.bits.last  & maxi.r.valid
    /* common */
    val prev_is_load = prev.bits.id2mem.memory_rd_en
    val prev_is_save = prev.bits.id2mem.memory_we_en
    val size = lkup_stage_out.bits.id2mem.size
    val overborder = MuxCase(false.B, Array(
      size.byte  -> false.B,
      size.hword -> (lkup_stage_out.bits.ex2mem.addr(0)    =/= 0.U),
      size.word  -> (lkup_stage_out.bits.ex2mem.addr(1, 0) =/= 0.U),
      size.dword -> (lkup_stage_out.bits.ex2mem.addr(2, 0) =/= 0.U),
    ))
    //val trans_end = r_last | (curr_state === sWRITE_1 & !overborder) | (curr_state === sWRITE_2)
    val a_waiting = (curr_state === sIDLE) & ((prev_is_load & (maxi.ar.ready === false.B)) | (prev_is_save & (maxi.aw.ready === false.B)))
    /* stage */
    lkup_stage_en := (curr_state === sIDLE | curr_state === sREAD_1)
    /*
     Internal Data Signal
    */
    /* reference */
    val a_addr = Mux(overborder, Cat(prev.bits.ex2mem.addr(38, 4), 0.U(4.W)), Cat(prev.bits.ex2mem.addr(38, 3), 0.U(3.W)))
    val start_byte = Mux(overborder, lkup_stage_out.bits.ex2mem.addr(3, 0), lkup_stage_out.bits.ex2mem.addr(2, 0))
    val start_bit = Mux(overborder, lkup_stage_out.bits.ex2mem.addr(3, 0) << 3, lkup_stage_out.bits.ex2mem.addr(2, 0) << 3).asUInt()
    /* read transaction */
    r_stage_in := MuxCase(0.U, Array(
      (curr_state === sREAD_1 & !r_last) -> maxi.r.bits.data,
      (curr_state === sREAD_2) -> r_stage_out
    )) //    r_stage_in := Mux(curr_state === sREAD_1 & !r_last, maxi.r.bits.data, r_stage_out)
    val rdata_out_1 = maxi.r.bits.data >> start_bit
    val rdata_out_2 = Cat(maxi.r.bits.data, r_stage_out) >> start_bit
    val rdata_out = MuxCase(0.U, Array(
      (curr_state === sREAD_1) -> rdata_out_1,
      (curr_state === sREAD_2) -> rdata_out_2
    ))
    val raw_memory_data = MuxCase(0.U,
      Array(
        size.byte   -> rdata_out(7,  0),
        size.hword  -> rdata_out(15, 0),
        size.word   -> rdata_out(31, 0),
        size.dword  -> rdata_out,
      )
    )
    val sext_memory_data = MuxCase(rdata_out,
      Array(
        size.byte   -> Sext(data = rdata_out(7,  0), pos = 8),
        size.hword  -> Sext(data = rdata_out(15, 0), pos = 16),
        size.word   -> Sext(data = rdata_out(31, 0), pos = 32),
        size.dword  -> rdata_out
      )
    )
    /* write transaction */
    val wdata = (lkup_stage_out.bits.ex2mem.we_data << start_bit).asTypeOf(0.U(128.W))
    val wmask = MuxCase(0.U(8.W), Array(
      size.byte  -> (lkup_stage_out.bits.ex2mem.we_mask  << start_byte),
      size.hword -> (lkup_stage_out.bits.ex2mem.we_mask  << start_byte),
      size.word  -> (lkup_stage_out.bits.ex2mem.we_mask  << start_byte),
      size.dword -> (lkup_stage_out.bits.ex2mem.we_mask  << start_byte),
    )).asUInt()
    /* stage */
    lkup_stage_in.bits := prev.bits
    lkup_stage_in.valid := prev.valid
    lkup_stage_in.ready := DontCare
    /*
     States Change Rule
     */
    next_state := sIDLE
    switch(curr_state){
      is(sIDLE){
        when(!prev.valid) { next_state := sEND }
          .elsewhen(!maxi.ar.ready){ next_state := sIDLE }// cannot transfer
          .elsewhen(prev_is_load)  {
            when(maxi.ar.ready) { next_state := sREAD_1 } .otherwise { next_state := sIDLE }
          }
          .elsewhen(prev_is_save)  {
            when(maxi.aw.ready) { next_state := sWRITE_1 } .otherwise { next_state := sIDLE }
          }
          .otherwise { next_state := sEND }
      }
      is(sREAD_1){
        when(r_last & next.ready){ next_state := sIDLE }
          .elsewhen(overborder & maxi.r.ready)  { next_state := sREAD_2 }
          .otherwise               { next_state := sREAD_1 }
      }
      is(sWRITE_1){
        when(overborder & maxi.w.ready )        { next_state := sWRITE_2  }
          .elsewhen(next.ready & maxi.b.valid)    { next_state := sIDLE    }
          .otherwise              { next_state := sWRITE_1 }
      }
      is(sREAD_2){
        when(r_last & next.ready) { next_state := sIDLE }
          .otherwise       { next_state := sREAD_2 }
      }
      is(sWRITE_2){
        when(next.ready & maxi.b.valid) { next_state := sIDLE }
          .otherwise       { next_state := sWRITE_2 }
      }
      is(sEND){
        when(next.ready) { next_state := sIDLE }
          .otherwise       { next_state := sEND }
      }
    }
    /*
     Output Control Signal
    */
    prev.ready := (next_state === sIDLE & (!a_waiting)) & next.ready
    next.valid := Mux((next_state === sIDLE & (!a_waiting)) & next.ready, true.B, false.B)//lkup_stage_out.valid
    /*
     AXI
    */
    val burst_len = Mux(overborder, 1.U, 0.U)
    //    val w_stay = RegInit(0.U.asTypeOf((new AXI4BundleW).bits))
    AXI4BundleA.clear(maxi.ar)
    when(curr_state === sIDLE & next_state === sREAD_1){
      AXI4BundleA.set(inf = maxi.ar, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
    }
    AXI4BundleA.clear(maxi.aw)
    when(curr_state === sIDLE & next_state === sWRITE_1){
      AXI4BundleA.set(inf = maxi.aw, id = 0.U, addr = a_addr, burst_size = 3.U, burst_len = burst_len)
    }
    AXI4BundleW.clear(maxi.w)
    when(curr_state === sWRITE_1){
      AXI4BundleW.set(inf = maxi.w, data = wdata(63, 0), strb = wmask, last = !overborder)
      //      w_stay.data := wdata(63, 0)
      //      w_stay.strb := "hff".U
      //      w_stay.last := !overborder
    }.elsewhen(curr_state === sWRITE_2){
      AXI4BundleW.set(inf = maxi.w, data = wdata(127, 64), strb = "hff".U, last = true.B)
      //      w_stay.data := wdata(127, 64)
      //      w_stay.strb := "hff".U
      //      w_stay.last := true.B
    }
    //      .otherwise{
    //      AXI4BundleW.set(inf = maxi.w, valid = false.B, data = w_stay.data, strb = w_stay.strb, last = w_stay.last)
    //    }
    AXI4BundleB.default(maxi.b)
    dontTouch(maxi.b)
    /*
     Output
     */
    next.bits.id2wb := Mux(next_state === sIDLE, lkup_stage_out.bits.id2wb, 0.U.asTypeOf(new ID2WB))
    next.bits.ex2wb := Mux(next_state === sIDLE, lkup_stage_out.bits.ex2wb, 0.U.asTypeOf(new EX2WB))
    val memory_data = Mux(lkup_stage_out.bits.id2mem.sext_flag, sext_memory_data, raw_memory_data)
    next.bits.mem2wb.memory_data := Mux(
      (curr_state === sREAD_1 | curr_state === sREAD_2), memory_data, 0.U.asTypeOf(chiselTypeOf(memory_data)))
  }
  //val size = lkup_stage_out.bits.id2mem.size// = Cat(lkup_stage_out.bits.id2mem.size.dword, lkup_stage_out.bits.id2mem.size.word,
  //     lkup_stage_out.bits.id2mem.size.hword, lkup_stage_out.bits.id2mem.size.byte)

}