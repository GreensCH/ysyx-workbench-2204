import chisel3._


class MEMU extends Module {
  val io = IO(new Bundle() {
    val in = new Bundle() {
      val data = Input(UInt(64.W))
      val we_en = Input(Bool())
      val we_mask = Input(UInt(8.W))
      val we_data = Input(UInt(64.W))
    }
    val out = new Bundle() {
      val data = Output(UInt(64.W))
    }
  })
  //put
  val rd_addr = io.in.data
  val ex_data = io.in.data
  // Memory
  val mem_if = Module(new MemoryInf).io

  mem_if.rd_en := ~(io.in.we_en)
  mem_if.rd_addr := rd_addr
  mem_if.we_en := io.in.we_en
  mem_if.we_addr := io.in.data
  mem_if.we_data := io.in.we_data
  mem_if.we_mask := io.in.we_mask

  io.out.data := Mux(true.B, ex_data, mem_if.rd_data)
}