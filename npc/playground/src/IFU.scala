import chisel3._



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

  /* memory bus instance */
  val memory_inf = Module(new MemoryInf).io
  /* memory interface */
  memory_inf.rd_en   := true.B
  memory_inf.rd_addr := npc
  io.if2id.inst := memory_inf.rd_data
  memory_inf.we_en   := false.B
  memory_inf.we_addr := 0.U(64.W)
  memory_inf.we_data := 0.U(64.W)
  memory_inf.we_mask := "b00000000".U
//  printf(p"IFU\taddr${Hexadecimal(memory_inf.rd_addr)}, data${Hexadecimal(memory_inf.rd_data)}, io.data${Hexadecimal(io.if2id.inst)}\n")
  /* if2id interface */
  io.if2id.pc := pc
}
