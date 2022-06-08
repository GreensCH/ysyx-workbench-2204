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

  wire inf  = (src2 == 64'h0);
  wire over = (src2 == 64'hffffffff);
  wire normal = (~inf) & (~over);
  wire [63:0] div_result  = ({64{inf    }} & 64'hffffffff)
                          | ({64{over   }} & src1)
                          | ({64{normal }} & $signed(src1) / $signed(src2));
  wire [63:0] divu_result = ({64{inf    }} & 64'hffffffff)
                          | ({64{normal }} & $unsigned(src1) / $unsigned(src2));

  wire [63:0] rem_result  = ({64{inf    }} & src1)
                          | ({64{over   }} & 64'h0)
                          | ({64{normal }} & $signed(src1) % $signed(src2));
  wire [63:0] remu_result = ({64{inf    }} & src1)
                          | ({64{normal }} & $unsigned(src1) % $unsigned(src2));

  
  
  
  //assign $signed(src1) / $signed(src2);
  // wire [63:0] divu_result = $unsigned(src1) / $unsigned(src2);
  // wire [63:0] rem_result  = $signed(src1) % $signed(src2);
  // wire [63:0] remu_result = $unsigned(src1) % $unsigned(src2);

  // wire [9: 0] op = {2'd3,mul,mulh,mulhu,mulhsu,div,divu,rem,remu};

  assign result = ({64{mul    }} & mulh_buf[63:0])
                | ({64{mulh   }} & mulh_buf[127: 64])
                | ({64{mulhu  }} & mulhu_buf[127: 64])
                | ({64{mulhsu }} & mulhsu_buf[127: 64])
                | ({64{div    }} & div_result)
                | ({64{divu   }} & divu_result)
                | ({64{rem    }} & rem_result)
                | ({64{remu   }} & remu_result);


  // always@(*)$display("op%b\n",op);
  // always@(*)$display("result%b\n",result);
  // always@(*)$display("test1%b\n",test1);
  // always@(*)$display("test2%b\n",test1);

  
  // wire [63:0] test = ($signed(src1) / $signed(src2);
endmodule
