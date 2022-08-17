/*
* multipt and div unit
*
*/
module ref_mdu (
  input             clock   ,
  input             reset   ,
  input             flush   ,
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
  output  [63: 0]   result  ,
  output            ready
);
  
  wire [127: 0] mulh_buf, mulhu_buf, mulhsu_buf;
  assign mulh_buf = ($signed(src1) * $signed(src2));
  assign mulhu_buf = ($unsigned(src1) * $unsigned(src2));
  assign mulhsu_buf = ($signed(src1) * $signed({1'b0,src2}));

  wire inf  = (src2 == 64'h0);
  wire over = (src2 == -1);
  wire normal = (~inf) & (~over);
  wire [63: 0] div_buf = $signed(src1) / $signed(src2);
  wire [63: 0] rem_buf = $signed(src1) % $signed(src2);
  wire [63:0] div_result  = ({64{inf    }} & (-1))
                          | ({64{over   }} & src1)
                          | ({64{normal }} & div_buf);
  wire [63:0] divu_result = ({64{inf    }} & (-1))
                          | ({64{normal | over}} & ($unsigned(src1) / $unsigned(src2)));

  wire [63:0] rem_result  = ({64{inf    }} & src1)
                          | ({64{over   }} & 64'h0)
                          | ({64{normal }} & rem_buf);
  wire [63:0] remu_result = ({64{inf    }} & src1)
                          | ({64{normal | over}} & ($unsigned(src1) % $unsigned(src2)));

  assign result = ({64{mul    }} & mulh_buf[63:0])
                | ({64{mulh   }} & mulh_buf[127: 64])
                | ({64{mulhu  }} & mulhu_buf[127: 64])
                | ({64{mulhsu }} & mulhsu_buf[127: 64])
                | ({64{div    }} & div_result)
                | ({64{divu   }} & divu_result)
                | ({64{rem    }} & rem_result)
                | ({64{remu   }} & remu_result);
  assign ready = 1'b1;
endmodule
