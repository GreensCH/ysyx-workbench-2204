`timescale 1ps/1ps


module top(
   input                   clk         ,
   input                   clrn        ,
   input                   ps2_clk     ,
   input                   ps2_data    ,
   // input                   nextdata_n  ,
   // output     [7:0]        data        ,
   // output                  ready       ,
   // output                  overflow    ,
   output     [15:0]       led_o       ,
   output     [47:0]       hex_o  
);

   //design module instance 
   wire [7:0] data;
   wire ready,nextdata_n,overflow;
   ps2_keyboard i_kbd_control(
      .clk(clk),
      .clrn(clrn),
      .ps2_clk(ps2_clk),
      .ps2_data(ps2_data),
      .data(data),
      .ready(ready),
      .nextdata_n(nextdata_n),
      .overflow(overflow)
   );

   //board function&debug interface instruction
   assign led_o[1:0]={overflow,ready};
   show_ps2kbd i_show_ps2kbd( clk,clrn,data,ready,hex_o,nextdata_n   );
   // test instruction
   // always@(*)begin
   //    if(clk)
   //    $display("ps2data:%b,ps2clk:%b,data:%b,ready:%b,overflow:%b,nextdata_n:%b"
   //    ,ps2_data,ps2_clk,data,ready,overflow,nextdata_n);
   // end
   // bcd8seg ia( .b(a), .h(hex_o[15:8]) );//output a to hex0
   // bcd8seg ib( .b(b), .h(hex_o[7:0]) );//output b to hex0
   // assign led_o[3:0] = s;
   // assign led_o[15:13]={carry,zero,overflow};


endmodule






   // initial for(integer i=0;i<8;i++) $display("top:%d %d %d ",a,b,s);

   // alu_tb#(4) i_tb
   // (
   // .b_o       (b) ,
   // .a_o       (a) ,
   // .sel_o     (sel) ,
   // .carry   (carry),//进位
   // .zero    (zero),//结果是否为0
   // .overflow(overflow),//溢出
   // .s       (s) //输出指示数码管
   // );