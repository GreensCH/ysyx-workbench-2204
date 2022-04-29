`timescale 1ps/1ps

module top(
   input      [7:0]       sw_i   ,
   input                  en_i  ,
   
   output                 led_o,  //输出指示led
   output     [7:0]       hex_o  //输出指示数码管
);

   wire     [2:0]       Y   ;

  encoder83 i0(
     .I(sw_i),//input
     .EI(en_i),// 使能
     .Y(Y),// output
     .GS(led_o)// 扩展端 or 输出指示
);
   assign hex_o[0]=1'b1;
   wire [3:0] b;
   bcd7seg i1(
      .b({1'b0,Y}),
      .h(hex_o[7:1])
   );


endmodule
