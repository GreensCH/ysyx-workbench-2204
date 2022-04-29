`timescale 1ps/1ps
//4-bits shifter
module shifter #
(
    parameter N = 4
)
(
   input        In,
   input        Clk,
   input        A_L,//0算术移位,1逻辑意味
   input        L_R,//0左移,1右移
   output       Q4
);

endmodule
