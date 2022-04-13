`timescale 1ps/1ps
//4-bits shifter
module shifter #
(
    parameter N = 8
)
(
   input        In,
   input        Clk,
   input        L_R,//0算术移位,1逻辑意味

   output reg [N-1:0]     Q
);

    always@(posedge Clk)begin
        //L_R=1左移(both a_l),L_R=0右移
        Q = L_R ?{Q[N-2:0],In}:{In,Q[N-1:1]};
    end

endmodule
