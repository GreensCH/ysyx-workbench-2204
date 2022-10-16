import chisel3._
import chisel3.util._
import chisel3.util.experimental.BoringUtils

class AXI4ManagerIn extends Bundle{
  val rd_en  = Input(Bool())
  val we_en  = Input(Bool())
  val size   = Flipped(new SrcSize{ val qword  = Output(Bool()) })
  val addr   = Input(UInt(32.W))
  val data   = Input(UInt(128.W))
  val wmask  = Input(UInt(16.W))
}
class AXI4ManagerStage extends Bundle{
  val rd_en  = Bool()
  val we_en  = Bool()
  val size   = new Bundle{
    val byte  = Bool()
    val hword = Bool()
    val word  = Bool()
    val dword = Bool()
    val qword = Bool()
  }
  val addr   = UInt(32.W)
  val data   = UInt(128.W)
  val wmask  = UInt(16.W)
}
class AXI4ManagerOut extends Bundle{
  val finish = Output(Bool())
  val ready  = Output(Bool())
  val data   = Output(UInt(128.W))
}

class IAXIManagerIN extends Bundle{
  val rd_en  = Input(Bool())
  val dev    = Input(Bool())
  val addr   = Input(UInt(32.W))
}
class IAXIManagerStage extends Bundle{
  val rd_en  = Bool()
  val dev    = Bool()
  val addr   = UInt(32.W)
}
class IAXIManagerOUT extends Bundle{
  val finish = Output(Bool())
  val ready  = Output(Bool())
  val data   = Output(UInt(128.W))
}
//////////////////////////////////////////////////////
//
// (no used)normal write and read axi manager
//  根据第三期soc，memory访问支持burst，外设只支持lite
//  且在burst传输时，r事物不一定连续，因此需要考虑不连续的情况
//  但是因此外设没有burst需求因此可以不实现这个功能，
//  只针对性的写了一个axi4manger128负责这种类型的访memory事物
//
//////////////////////////////////////////////////////
//class AXI4Manager extends Module  {
//  val io = IO(new Bundle {
//    val in   = new AXI4ManagerIn
//    val maxi = new AXI4Master
//    val out  = new AXI4ManagerOut
//  })


//////////////////////////////////////////////////////
//
// read only axi manager
// axi4lite supported
//
//////////////////////////////////////////////////////
class IAXIManager extends Module  {
  val io = IO(new Bundle {
    val in   = new IAXIManagerIN
    val maxi = new AXI4Master
    val out  = new IAXIManagerOUT
  })
  /*
   Reference
   */
  private val in = io.in
  private val maxi = io.maxi
  private val out = io.out
  private val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: Nil = Enum(4)
  private val next_state = Wire(UInt(sADDR.getWidth.W))
  private val curr_state = RegNext(init = sADDR, next = next_state)
  // Lookup Stage
  private val stage_en = Wire(Bool())
  private val stage_in = Wire(new IAXIManagerStage)
  stage_in := in
  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
  private val _in = Wire(new IAXIManagerStage)
  _in := in
  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
  // AXI Interface Default Connection(Read-Write)
  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleR.default (maxi.r)
  maxi.r.ready := true.B
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)
  /*
  Internal Control Signal
  */
  // axi
  private val r_last = maxi.r.bits.last  & maxi.r.valid
  // common
  private val is_load = in2.rd_en
  private val is_dev = in2.dev
  // stage
  stage_en := curr_state === sADDR
  /*
   Internal Data Signal
  */
  // reference
  private val a_len     =  Mux(is_dev, 0.U(AXI4Parameters.lenBits.W), 1.U(AXI4Parameters.lenBits.W))
  private val a_addr    =  Mux(is_dev, Cat(in2.addr(31, 2), 0.U(2.W)), Cat(in2.addr(31, 4), 0.U(4.W)))// changed
  private val a_size    =  Mux(is_dev, 2.U, 3.U)// changed
  private val start_bit =  Mux(is_dev, in2.addr(2, 0) << 3, in2.addr(2, 0) << 3).asUInt()
  // read transaction
  private val rdata64  = RegEnable(init = 0.U(64.W), next = maxi.r.bits.data, enable = maxi.r.valid & (!maxi.r.bits.last))
  private val memory_data = Mux(is_dev, maxi.r.bits.data >> start_bit, Cat(maxi.r.bits.data, rdata64))
  /*
   States Change Rule
   */
  next_state := sADDR
  switch(curr_state){
    is(sADDR){
      when(is_load){
        when(maxi.ar.ready) { next_state := sREAD1 } .otherwise  { next_state := sARWAIT }
      }.otherwise           { next_state := sADDR }
    }
    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
    is(sREAD1){
      when(r_last)                          { next_state := sADDR }
        .elsewhen(!is_dev)     { next_state := sREAD2 }
        .otherwise                            { next_state := sREAD1 }
    }
    is(sREAD2){
      when(r_last)                            { next_state := sADDR }
        .otherwise                            { next_state := sREAD2 }
    }
  }
  /*
    AXI
   */
  when(next_state === sREAD1 | next_state === sARWAIT){
    AXI4BundleA.set(inf = maxi.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = a_size, burst_len = a_len)
  }
  /*
  Output
 */
  out.ready  := next_state === sADDR | curr_state === sADDR
  out.finish := maxi.r.bits.last | maxi.b.valid
  private val memory_data_buffer = RegInit(0.U(128.W))//lock output data
  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
  out.data := Mux(curr_state === sREAD1 | curr_state === sREAD2, memory_data, memory_data_buffer)
}

//////////////////////////////////////////////////////
//
// 128-bits write and read axi manager
//
//////////////////////////////////////////////////////
class DAXIManager extends Module {
  val io = IO(new Bundle {
    val in   = new AXI4ManagerIn
    val maxi = new AXI4Master
    val out  = new AXI4ManagerOut
  })
  /*
   Reference
   */
  private val in = io.in

  private val maxi = io.maxi
  private val out = io.out
  private val sADDR :: sARWAIT :: sREAD1 :: sREAD2 :: sAWWAIT ::sWRITE1 :: sWRITE2 :: Nil = Enum(7)
  private val next_state = Wire(UInt(sADDR.getWidth.W))
  private val curr_state = RegNext(init = sADDR, next = next_state)
  // Lookup Stage
  private val stage_en = Wire(Bool())
  private val stage_in = Wire(new AXI4ManagerStage)
  stage_in := in
  private val stage_out2 = RegEnable(init = 0.U.asTypeOf(stage_in),next = stage_in, enable = stage_en)
  private val _in = Wire(new AXI4ManagerStage)
  _in := in
  private val in2 = Mux(curr_state === sADDR, _in, stage_out2)
  // AXI Interface Default Connection(Read-Write)
  AXI4BundleA.clear   (maxi.ar)
  AXI4BundleR.default (maxi.r)
  maxi.r.ready := true.B
  AXI4BundleA.clear   (maxi.aw)
  AXI4BundleW.clear   (maxi.w)
  AXI4BundleB.default (maxi.b)
  /*
  Internal Control Signal
  */
  // axi
  private val r_last = maxi.r.bits.last  & maxi.r.valid
  // common
  private val is_load = in2.rd_en
  private val is_save = in2.we_en
  // stage
  stage_en := curr_state === sADDR
  /*
   Internal Data Signal
  */
  // reference
  private val a_len     = 1.U
  private val a_addr    = Cat(in2.addr(31, 4), 0.U(4.W))
  private val a_size    = 3.U
  // read transaction
  private val rdata64  = RegEnable(init = 0.U(64.W), next = maxi.r.bits.data, enable = maxi.r.valid & (!maxi.r.bits.last))
  private val memory_data = Cat(maxi.r.bits.data, rdata64)
  /*
   States Change Rule
   */
  next_state := sADDR
  switch(curr_state){
    is(sADDR){
      when(is_load){
        when(maxi.ar.ready) { next_state := sREAD1 } .otherwise  { next_state := sARWAIT }
      }.elsewhen(is_save){
        when(maxi.aw.ready) { next_state := sWRITE1 } .otherwise { next_state := sAWWAIT }
      }.otherwise           { next_state := sADDR }
    }
    is(sARWAIT){ when(maxi.ar.ready){ next_state := sREAD1  }.otherwise{ next_state := sARWAIT } }
    is(sAWWAIT){ when(maxi.aw.ready){ next_state := sWRITE1 }.otherwise{ next_state := sAWWAIT } }
    is(sREAD1){
      when(maxi.r.valid)                    { next_state := sREAD2 }
      .elsewhen(maxi.r.bits.resp === 0.U & maxi.r.valid){ next_state := sREAD2 }
      .otherwise                            { next_state := sREAD1 }
    }
    is(sWRITE1){
      when(maxi.w.ready )                     { next_state := sWRITE2  }
        .otherwise                            { next_state := sWRITE1 }
    }
    is(sREAD2){
      when(r_last)                            { next_state := sADDR }
        .otherwise                            { next_state := sREAD2 }
    }
    is(sWRITE2){
      when(maxi.b.valid)                      { next_state := sADDR }
      .elsewhen(maxi.b.bits.resp === 0.U & maxi.b.valid){ next_state := sADDR }
        .otherwise                            { next_state := sWRITE2 }
    }
  }
  /*
    AXI
   */
  AXI4BundleA.clear(maxi.ar)
  when(next_state === sARWAIT | (curr_state === sADDR & next_state === sREAD1) | curr_state === sARWAIT){
    AXI4BundleA.set(inf = maxi.ar, valid = true.B, id = 0.U, addr = a_addr, burst_size = a_size, burst_len = a_len)
  }
  AXI4BundleA.clear(maxi.aw)
  when(next_state === sAWWAIT | (curr_state === sADDR & next_state === sWRITE1) | curr_state === sAWWAIT){
    AXI4BundleA.set(inf = maxi.aw, valid = true.B, id = 0.U, addr = a_addr, burst_size = a_size, burst_len = a_len)
  }
  AXI4BundleW.clear(maxi.w)
  when(curr_state === sWRITE1 | curr_state === sARWAIT){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = in2.data(63, 0), strb = in2.wmask(7, 0), last = false.B)
  }
  when(curr_state === sWRITE2){
    AXI4BundleW.set(inf = maxi.w, valid = true.B, data = in2.data(127, 64), strb = in2.wmask(15, 8), last = true.B)
  }
  AXI4BundleB.default(maxi.b)
  /*
  Output
 */
  out.ready  := next_state === sADDR | curr_state === sADDR
  out.finish := maxi.r.bits.last | maxi.b.valid
  private val memory_data_buffer = RegInit(0.U(128.W))//lock output data
  memory_data_buffer := Mux(out.finish, memory_data, memory_data_buffer)
  out.data := Mux(curr_state === sREAD1 | curr_state === sREAD2, memory_data, memory_data_buffer)

}