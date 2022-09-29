import chisel3._

trait TopAWMaster extends Bundle{
  val awready  =    Input(Bool())
  val awvalid  =    Output(Bool())
  val awaddr   =    Output(UInt(AXI4Parameters.addrBits.W))
  val awid     =    Output(UInt(AXI4Parameters.idBits.W))
  val awlen    =    Output(UInt(AXI4Parameters.lenBits.W))
  val awsize   =    Output(UInt(AXI4Parameters.sizeBits.W))
  val awburst  =    Output(UInt(AXI4Parameters.burstBits.W))
}

trait TopARMaster extends Bundle{
  val arready  =    Input(Bool())
  val arvalid  =    Output(Bool())
  val araddr   =    Output(UInt(AXI4Parameters.addrBits.W))
  val arid     =    Output(UInt(AXI4Parameters.idBits.W))
  val arlen    =    Output(UInt(AXI4Parameters.lenBits.W))
  val arsize   =    Output(UInt(AXI4Parameters.sizeBits.W))
  val arburst  =    Output(UInt(AXI4Parameters.burstBits.W))
}

trait TopRMaster extends Bundle{
  val rready  =   Output(Bool())
  val rvalid  =   Input(Bool())
  val rresp   =   Input(UInt(AXI4Parameters.respBits.W))
  val rdata   =   Input(UInt(AXI4Parameters.dataBits.W))
  val rlast   =   Input(Bool())
  val rid     =   Input(UInt(AXI4Parameters.idBits.W))
}

trait TopWMaster extends Bundle{
  val wready    = Input(Bool())
  val wvalid    = Output(Bool())
  val wdata     = Output(UInt(AXI4Parameters.dataBits.W))
  val wstrb     = Output(UInt(AXI4Parameters.strbBits.W))
  val wlast     = Output(Bool())
}

trait TopBMaster extends Bundle{
  val bready    = Output(Bool())
  val bvalid    = Input(Bool())
  val bresp     = Input(UInt(AXI4Parameters.respBits.W))
  val bid       = Input(UInt(AXI4Parameters.idBits.W))
}


class TopAxiMaster extends Bundle with TopARMaster with TopRMaster with TopAWMaster with TopWMaster with TopBMaster

object TopAxiMaster{
  def useless_slave(port: TopAxiMaster): Unit ={
    // val _uused_ok_ar = Cat(false.B,
    //   port.arvalid,
    //   port.araddr ,
    //   port.arid   ,
    //   port.arlen  ,
    //   port.arsize ,
    //   port.arburst,
    //   false.B).andR()
    port.arready := false.B

    // val _uused_ok_aw = Cat(false.B,
    //   port.awvalid,
    //   port.awaddr ,
    //   port.awid   ,
    //   port.awlen  ,
    //   port.awsize ,
    //   port.awburst,
    //   false.B).andR()
    port.awready := false.B

    // val _uused_ok_w = Cat(false.B,
    //   port.wvalid,
    //   port.wdata ,
    //   port.wstrb ,
    //   port.wlast ,
    //   false.B).andR()
    port.wready := false.B

    // val _uused_ok_r = Cat(false.B,
    //   port.rready,
    //   false.B).andR()
    port.rvalid  := 0.U
    port.rresp   := 0.U
    port.rdata   := 0.U
    port.rlast   := 0.U
    port.rid     := 0.U

    // val _uused_ok_b = Cat(false.B,
    //   port.bready,
    //   false.B).andR()
    port.bvalid  := 0.U
    port.bresp   := 0.U
    port.bid     := 0.U

    // dontTouch(_uused_ok_ar)
    // dontTouch(_uused_ok_aw)
    // dontTouch(_uused_ok_r )
    // dontTouch(_uused_ok_w)
    // dontTouch(_uused_ok_b)
  }
  def connect(left: TopAxiMaster,right: AXI4Master): Unit ={
    left.awid := right.aw.bits.id
    left.awlen := right.aw.bits.len
    left.awaddr := right.aw.bits.addr
    left.awsize := right.aw.bits.size
    left.awburst := right.aw.bits.burst
    left.awvalid := right.aw.valid
    right.aw.ready := left.awready

    left.arid     := right.ar.bits.id
    left.arlen    := right.ar.bits.len
    left.araddr   := right.ar.bits.addr
    left.arsize   := right.ar.bits.size
    left.arburst  := right.ar.bits.burst
    left.arvalid  := right.ar.valid
    right.ar.ready := left.arready

    right.r.bits.id   := left.rid
    right.r.bits.data := left.rdata
    right.r.bits.resp := left.rresp
    right.r.bits.last := left.rlast
    left.rready       := right.r.ready
    right.r.valid     := left.rvalid

    left.wdata := right.w.bits.data
    left.wlast := right.w.bits.last
    left.wstrb := right.w.bits.strb
    left.wvalid := right.w.valid
    right.w.ready := left.wready

    right.b.bits.id := left.bid
    right.b.bits.resp := left.bresp
    right.b.valid := left.bvalid
    left.bready := right.b.ready
  }
}



