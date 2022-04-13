`timescale 1ps/1ps

module top(
   input      [7:0]       sw_i   ,

   output     [7:0]       hex_o  //输出指示数码管
);

  assign hex_o=sw_i;


endmodule
