object Elaborate extends App {
  if(SparkConfig.ysyxSoC){
    (new chisel3.stage.ChiselStage).execute(args, Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new Top2())))
  }else{
    (new chisel3.stage.ChiselStage).execute(args, Seq(chisel3.stage.ChiselGeneratorAnnotation(() => new Top())))
  }
}
