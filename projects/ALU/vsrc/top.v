`timescale 1ps/1ps
//*****WARNING******
//****发现重要BUG-1****
//NVBROAD输出信号不能与$display冲突
//并且通过间接信号进行assign也没用也没用
//否则导致$display没有数据
//****发现重要BUG-2****
//for内部不能进行赋值，否则无结果
//你需要另外开一个initial进行赋值

module top(
   input      [3:0]       a   ,
   input      [3:0]       b   ,
   input      [2:0]       sel  ,
   
   output                  carry,
   output                  zero,
   output                  overflow,
   output     [3:0]        s,
   output     [15:0]       led_o,
   output     [15:0]       hex_o  //输出指示数码管
);

   wire [3:0] a_comp,b_comp;
   source2comp i_a (a,a_comp);
   comp2source i_b (b,b_comp);
   


   alu #(4) i_alu
   (
   .b       (b) ,
   .a       (a) ,
   .sel     (sel) ,
   .carry   (carry),//进位
   .zero    (zero),//结果是否为0
   .overflow(overflow),//溢出
   .s       (s) //输出指示数码管
   );
   //board debug interface
   bcd8seg ia( .b(a), .h(hex_o[15:8]) );//output a to hex0
   bcd8seg ib( .b(b), .h(hex_o[7:0]) );//output b to hex0
   assign led_o[3:0] = s;
   assign led_o[15:13]={carry,zero,overflow};


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