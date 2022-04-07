`timescale 1ps/1ps
module top(
  input clk,
  input rst,
  output [15:0] led
);
    
    light light_i1(clk,rst,led);
endmodule