import chisel3._
import chisel3.util._

class BR2IF extends Bundle {
  val jump = Output(Bool())
  val npc  = Output(UInt(64.W))
}

class IDBR extends Bundle{
  val ready = Output(Bool())
  val brh  = Output(Bool())
  val jal  = Output(Bool())
  val jalr = Output(Bool())
  val pc   = Output(UInt(64.W))
  val src1 = Output(UInt(64.W))
  val src2 = Output(UInt(64.W))
  val imm  = Output(UInt(64.W))
}

class BRU extends Module{
  val io = IO(new Bundle() {
    val idu = Flipped(new IDBR)
    val ifu = new BR2IF
  })
  val idb = io.idu
  val ifb = io.ifu

  val brh  = idb.brh
  val jal  = idb.jal
  val jalr = idb.jalr
  val pc   = idb.pc
  val src1 = idb.src1
  val src2 = idb.src2
  val imm  = idb.imm

  val jump = brh | jal | jalr

  ifb.jump := jump//Mux(io.idu.ready, jump, false.B)

  val npc_reg_in = Wire(UInt(64.W))
  val npc_reg = RegEnable(init = 0.U(64.W), next = npc_reg_in, enable = jump)

  npc_reg_in := ifb.npc

  ifb.npc := MuxCase(default = npc_reg,
    Array(
      (!io.idu.ready) -> npc_reg,
      (brh | jal) -> (pc + imm),
      (jalr) -> Cat((src1 + src2)(63, 1), 0.U(1.W))(63, 0)
    )
  )

}

object BRU {
  def apply(ifu: BR2IF, idu: IDBR): BRU = {
    val bru = Module(new BRU)
    bru.io.ifu <> ifu
    bru.io.idu <> idu
    bru
  }
}
