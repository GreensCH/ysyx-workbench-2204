import chisel3._
import chisel3.util._


class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
}

class IF2ID extends Bundle{
  val inst  =   Output(UInt(32.W))
  val pc    =   Output(UInt(64.W))
}

class IFU extends Module {
  val io = IO(new Bundle {
    val stall   =   Input(Bool())
    val id2pc   =   Flipped(new ID2PC)
    val if2id   =   new IF2ID
  })
  /* stall interface */
  val stall = io.stall
  /* PC instance */
  val PC = Module(new PC)
  /* pre IF (PC) interface & connection */
  val pc = PC.io.pc
  val npc = PC.io.npc
  PC.io.stall := stall
  PC.io.is_jump := io.id2pc.is_jump
  PC.io.offset := io.id2pc.offset
  PC.io.jump_reg := io.id2pc.jump_reg
  PC.io.is_jumpr := io.id2pc.is_jumpr
  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  val rd_en   = true.B
  val rd_addr = pc
  val rd_data = memory_inf.rd_data
  val we_en   = false.B
  val we_addr = false.B
  val we_data = 0.U
  val we_mask = 0.U
  memory_inf.rd_en   := rd_en
  memory_inf.rd_addr := rd_addr
  memory_inf.we_en   := we_en
  memory_inf.we_addr := we_addr
  memory_inf.we_data := we_data
  memory_inf.we_mask := we_mask
  /* if2id interface */
  io.if2id.inst := Mux(stall, 0.U, rd_data)
  io.if2id.pc := pc
}