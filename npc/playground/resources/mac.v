/*
* multiplier–accumulator (MAC unit)
*
*/
module mac (
  input             mul     ,
  input             mulh    ,
  input             mulhu   ,
  input             mulhsu  ,
  input             div     ,
  input             divu    ,
  input             rem     ,
  input             remu    ,
  input   [63: 0]   src1    ,
  input   [63: 0]   src2    ,
  output  [63: 0]   result  
);

  wire [127: 0] mulh_buf, mulhu_buf, mulhsu_buf;
  assign mulh_buf = ($signed(src1) * $signed(src2));
  assign mulhu_buf = ($unsigned(src1) * $unsigned(src2));
  assign mulhsu_buf = ($signed(src1) * $unsigned(src2));

  assign result = ({64{mul    }} & ($signed(src1) * $signed(src2)))
                | ({64{mulh   }} & mulh_buf[127: 64])
                | ({64{mulhu  }} & mulhu_buf[127: 64])
                | ({64{mulhsu }} & mulhsu_buf[127: 64])
                | ({64{div    }} & ($signed(src1) / $signed(src2)))
                | ({64{divu   }} & ($unsigned(src1) / $unsigned(src2)))
                | ({64{rem    }} & ($signed(src1) % $signed(src2)))
                | ({64{remu   }} & ($unsigned(src1) % $unsigned(src2)));
  
endmodule
