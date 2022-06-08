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

  wire [63:0] test1 = $signed(src1) / $signed(src2);
  wire [63:0] test2 = $unsigned(src1) / $unsigned(src2);
  wire [9: 0] op = {2'd3,mul,mulh,mulhu,mulhsu,div,divu,rem,remu};

  assign result = ({64{mul    }} & ($signed(src1) * $signed(src2)))
                | ({64{mulh   }} & mulh_buf[127: 64])
                | ({64{mulhu  }} & mulhu_buf[127: 64])
                | ({64{mulhsu }} & mulhsu_buf[127: 64])
                | ({64{div    }} & ($signed(src1) / $signed(src2)))
                | ({64{divu   }} & ($unsigned(src1) / $unsigned(src2)))
                | ({64{rem    }} & ($signed(src1) % $signed(src2)))
                | ({64{remu   }} & ($unsigned(src1) % $unsigned(src2)));
  always@(*)$display("op%b\n",op);
  always@(*)$display("result%b\n",result);
  always@(*)$display("test1%b\n",test1);
  always@(*)$display("test2%b\n",test1);

  
  // wire [63:0] test = ($signed(src1) / $signed(src2);
endmodule
