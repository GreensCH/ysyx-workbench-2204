`timescale 1ps/1ps

module top(
    input   [1:0] X0,//input
    input   [1:0] X1,
    input   [1:0] X2,
    input   [1:0] X3,
    input   [1:0] Y,//selcet
    output  [1:0] F//output
);

  mux412 i0(
     X0,//input
     X1,
     X2,
     X3,
     Y,//selcet
     F//output
);

endmodule
