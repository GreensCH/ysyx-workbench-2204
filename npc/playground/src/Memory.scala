import chisel3._
import chisel3.util._

//class MemoryRD extends Bundle{
//  val rd_en    =   Input (Bool())
//  val rd_addr  =   Input (UInt(64.W))
//  val rd_data  =   Output(UInt(64.W))
//}
//
//class MemoryWE extends Bundle{
//  val we_en    =   Input (Bool())
//  val we_addr  =   Input (UInt(64.W))
//  val we_data  =   Input (UInt(64.W))
//  val we_mask  =   Input (UInt(8.W))
//}

class MemoryIO extends Bundle{
  val rd_en    =   Input (Bool())
  val rd_addr  =   Input (UInt(64.W))
  val rd_data  =   Output(UInt(64.W))
  val we_en    =   Input (Bool())
  val we_addr  =   Input (UInt(64.W))
  val we_data  =   Input (UInt(64.W))
  val we_mask  =   Input (UInt(8.W))
}

class dpic_memory extends BlackBox with HasBlackBoxResource {
  val io = IO(new MemoryIO{
    val clk      =   Input (Clock())
    val rst    =   Input (Reset())
  })
  addResource("/dpic_memory.v")
}

class MemoryInf extends Module{
  val io = IO(new MemoryIO)

  val m = Module(new dpic_memory)
  m.io.clk := clock
  m.io.rst := reset

  m.io.rd_en := io.rd_en
  m.io.rd_addr := io.rd_addr
  io.rd_data := m.io.rd_data
  //printf(p"NPC\trd_addr=0x${Hexadecimal(io.rd_addr)}, rd_data=0x${Hexadecimal(io.rd_data)}, rd_en=${Binary(m.io.rd_en)}\n")
  m.io.we_en := io.we_en
  m.io.we_addr := io.we_addr
  m.io.we_data := io.we_data
  m.io.we_mask := io.we_mask
  //printf(p"NPC\twe_addr=0x${Hexadecimal(io.we_addr)}, we_data=0x${Hexadecimal(io.we_data)}, we_mask=${Binary(m.io.we_en)}\n")

}

class Memory extends Module{
  val io = IO(new Bundle{
    val saxi = Flipped(new AXI4)
  })
  // axi interface
  val master = io.saxi
  val axi_ar_in = master.ar
  val axi_aw_in = master.aw
  val axi_w_in = master.w
  val axi_r_out = AXI4BundleR()
  val axi_b_out = AXI4BundleB()
  master.r <> axi_r_out
  master.b <> axi_b_out
  // control signal
  val rd_en    =   Wire(Bool())
  val rd_addr  =   Wire(UInt(64.W))
  val rd_data  =   Wire(UInt(64.W))
  val we_en    =   Wire(Bool())
  val we_addr  =   Wire(UInt(64.W))
  val we_data  =   Wire(UInt(64.W))
  val we_mask  =   Wire(UInt(8.W))
  // dpic
  val m = Module(new dpic_memory)
  m.io.clk := clock
  m.io.rst := reset
  m.io.rd_en := rd_en
  m.io.rd_addr := rd_addr
  rd_data := m.io.rd_data
  m.io.we_en := we_en
  m.io.we_addr := we_addr
  m.io.we_data := we_data
  m.io.we_mask := we_mask
  // FSM States
  protected val sIDLE :: sBUSY :: sEND :: Nil = Enum(3)
  protected val next_state = Wire(UInt(sIDLE.getWidth.W))
  protected val curr_state = RegNext(init = sIDLE, next = next_state)
  next_state := sIDLE
  protected val is_write = RegInit(false.B)
  protected val is_read = Wire(Bool())
  is_read := !is_write.asBool()
  protected val config = RegInit(0.U.asTypeOf((new AXI4BundleA).bits)) // save the config
  protected val inc_addr = Wire(UInt(AXI4Parameters.dataBits.W))
  // States change
  switch(curr_state){
    is(sIDLE){
      when(axi_ar_in.valid) {
        next_state := sBUSY
        is_write := false.B
        config := axi_ar_in.bits
        inc_addr := axi_ar_in.bits.addr
      }.elsewhen(axi_aw_in.valid){
        next_state := sBUSY
        is_write := true.B
        config := axi_aw_in.bits
        inc_addr := axi_aw_in.bits.addr
      }.otherwise{
        next_state := sIDLE
        is_write := false.B
        config := 0.U.asTypeOf((new AXI4BundleA).bits)
        inc_addr := 0.U
      }
    }
    is(sBUSY) {
      when(config.len === 1.U) {
        next_state := sEND
        config.len := 0.U
        inc_addr := inc_addr
      } .otherwise{
        next_state := sBUSY
        config.len := config.len - 1.U
        inc_addr := inc_addr + config.size
      }
    }
    is(sEND){
      when(axi_r_out.ready & is_read) {
        next_state := sEND
        config := 0.U.asTypeOf((new AXI4BundleA).bits)
      }
      when(axi_b_out.ready & is_write) {
        next_state := sEND
        config := 0.U.asTypeOf((new AXI4BundleA).bits)
      }
    }
  }
  // Output Logic
  val AxREADY = Wire(Bool())
  val RID = Wire(UInt(AXI4Parameters.idBits.W))
  val RDATA = Wire(UInt(AXI4Parameters.dataBits.W))
  val RLAST = Wire(Bool())
  val RRESP = Wire(UInt(AXI4Parameters.respBits.W))
  RRESP := AXI4Parameters.RESP_OKAY
  switch(curr_state){
    is(sIDLE){
      RID := 0.U
      RDATA := 0.U
      RLAST := false.B
    }
    is(sBUSY){
      RID := config.id
      RDATA := rd_data
      RLAST := false.B
    }
    is(sEND){
      RID := config.id
      RDATA := rd_data
      RLAST := true.B
    }
  }
  // connection between dpic(memory dram/flash)
  rd_en := true.B
  rd_addr := inc_addr
  we_en := false.B
  we_addr := 0.U
  we_data := 0.U
  we_mask := 0.U
}

// "ready := true.B" can exist in below section to reduce 1 cycle