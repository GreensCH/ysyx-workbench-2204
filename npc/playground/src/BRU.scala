import chisel3._
import chisel3.util._

class BR2RegID extends Bundle{
  val bubble = Output(Bool())
}

class BR2PC extends Bundle{
  val jump = Output(Bool())
  val npc  = Output(UInt(64.W))
}

class BRU extends Module{
  val io = new Bundle{
    val id2br = Flipped(new ID2BR)
    val br2regid = new BR2RegID
    val br2pc = new BR2PC
  }
  val brh  = io.id2br.brh
  val jal  = io.id2br.jal
  val jalr = io.id2br.jalr
  val pc   = io.id2br.pc
  val src1 = io.id2br.src1
  val src2 = io.id2br.src2
  val imm  = io.id2br.imm

//  val jump = Wire(Bool())
//  jump := brh | jal | jalr

//  io.br2regid.bubble := false.B
//
//  io.br2pc.jump := false.B

//  io.br2pc.npc := MuxCase(default = 0.U,
//    Array(
//      (brh | jal) -> (pc + imm),
//      (jalr) -> Cat((src1 + src2)(63, 1), 0.U(1.W))(63, 0)
//    )
//  )

}
