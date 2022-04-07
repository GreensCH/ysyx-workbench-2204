`timescale 1ps/1ps
module encoder83(
   input      [7:0]       I   ,
   input                  EI  ,
   
   output wire [2:0]      Y   ,
   output wire            GS  ,
   output wire            EO    
);
assign Y[2] = EI & (I[7] | I[6] | I[5] | I[4]);
assign Y[1] = EI & (I[7] | I[6] | ~I[5]&~I[4]&I[3] | ~I[5]&~I[4]&I[2]);
assign Y[0] = EI & (I[7] | ~I[6]&I[5] | ~I[6]&~I[4]&I[3] | ~I[6]&~I[4]&~I[2]&I[1]);

assign EO = EI&~I[7]&~I[6]&~I[5]&~I[4]&~I[3]&~I[2]&~I[1]&~I[0];

assign GS = EI&(I[7] | I[6] | I[5] | I[4] | I[3] | I[2] | I[1] | I[0]);
//assign GS = EI&(| I);
         
endmodule