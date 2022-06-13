//import chisel3._
//import chisel3.util._
//
//class ByPassMux extends Module{
//  val io = IO(new Bundle {
//    val sel1  =   Input(BypassMuxSel())
//    val sel2  =   Input(BypassMuxSel())
//    val id_data1  = Input(UInt(64.W))
//    val id_data2  = Input(UInt(64.W))
//    val ex_data   = Input(UInt(64.W))
//    val mem_data  = Input(UInt(64.W))
//    val wb_data   = Input(UInt(64.W))
//    val src_data1 = Output(UInt(64.W))
//    val src_data2 = Output(UInt(64.W))
//  })
//  val sel1  = io.sel1
//  val sel2  = io.sel2
//  val id_data1        = io.id_data1
//  val id_data2        = io.id_data2
//  val ex_data         = io.ex_data
//  val mem_data        = io.mem_data
//  val wb_data         = io.wb_data
//
//  io.src_data1 := MuxCase(id_data1,
//    Array(
//      (sel1 === BypassMuxSel.normal) -> id_data1,
//      (sel1 === BypassMuxSel.ex) -> ex_data,
//      (sel1 === BypassMuxSel.mem) -> mem_data,
//      (sel1 === BypassMuxSel.wb) -> wb_data
//    )
//  )
//
//  io.src_data2 := MuxCase(id_data2,
//    Array(
//      (sel2 === BypassMuxSel.normal) -> id_data2,
//      (sel2 === BypassMuxSel.ex) -> ex_data,
//      (sel2 === BypassMuxSel.mem) -> mem_data,
//      (sel2 === BypassMuxSel.wb) -> wb_data
//    )
//  )
//
//}
