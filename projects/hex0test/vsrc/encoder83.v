`timescale 1ps/1ps

module encoder83(
   input      [7:0]       I   ,
   input                  EI  ,
   
   output     [2:0]       Y   ,
   output                 GS    
);
assign Y[2] = EI & (I[7] | I[6] | I[5] | I[4]);
assign Y[1] = EI & (I[7] | I[6] | ~I[5]&~I[4]&I[3] | ~I[5]&~I[4]&I[2]);
assign Y[0] = EI & (I[7] | ~I[6]&I[5] | ~I[6]&~I[4]&I[3] | ~I[6]&~I[4]&~I[2]&I[1]);

//assign EO = EI&~I[7]&~I[6]&~I[5]&~I[4]&~I[3]&~I[2]&~I[1]&~I[0];

assign GS = EI&(I[7] | I[6] | I[5] | I[4] | I[3] | I[2] | I[1] | I[0]);
//assign GS = EI&(| I);
         
endmodule

   // always@(*)begin
   //    if(EN)
   //       casex (I)
   //          8'b0000_0000: Y = 3'b000;
   //          8'b1xxx_xxxx: Y = 3'b111; 
   //          8'b01xx_xxxx: Y = 3'b110; 
   //          8'b001x_xxxx: Y = 3'b101; 
   //          8'b0001_xxxx: Y = 3'b100; 
   //          8'b0000_1xxx: Y = 3'b011; 
   //          8'b0000_01xx: Y = 3'b010; 
   //          8'b0000_001x: Y = 3'b001; 
   //          8'b0000_0001: Y = 3'b000;
   //          //8'bxxxx_xxxx: Y = 3'b000;
   //          default: Y = 3'b000;
   //       endcase
   // end