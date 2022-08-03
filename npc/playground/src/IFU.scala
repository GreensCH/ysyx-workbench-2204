import chisel3._
import chisel3.util._


class IF2Memory extends Bundle{
  val rd_addr  =  Output (UInt(64.W))
  val rd_data  =  Input (UInt(64.W))
}

class IF2ID extends Bundle{
  val inst  =   Output(UInt(32.W))
  val pc = Output(UInt(64.W))
}

class IFU extends Module {
  val io = IO(new Bundle {
    val inst    =   Input(UInt(32.W))
    val id2pc   =   Flipped(new ID2PC)
    val if2id   =   new IF2ID
  })
//  printf("IFU\t\n")
  /* PC instance */
  val PC = Module(new PC)
  /* pre IF (PC) interface*/
  val pc = PC.io.pc
  val npc = PC.io.npc
  PC.io.is_jump := io.id2pc.is_jump
  PC.io.offset := io.id2pc.offset
  PC.io.jump_reg := io.id2pc.jump_reg
  PC.io.is_jumpr := io.id2pc.is_jumpr

  /* if2id interface */
  io.if2id.inst := io.inst
  io.if2id.pc := pc
//  printf(p"IFU\taddr${Hexadecimal(memory_inf.rd_addr)}, data${Hexadecimal(memory_inf.rd_data)}, io.data${Hexadecimal(io.if2id.inst)}\n")

}
