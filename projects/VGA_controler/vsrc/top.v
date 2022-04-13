`timescale 1ps/1ps


module top(
   input                   clk            ,//50Mhz clk
   input                   rst          ,

   output                  hsync          ,    //行同步和列同步信号
   output                  vsync          ,
   output                  valid          ,    //消隐信号
   output      [7:0]       vga_r          ,    //红绿蓝颜色信号
   output      [7:0]       vga_g          ,
   output      [7:0]       vga_b          
);
   //clk generate instruction
   wire clk_25,clk_50;
   assign clk_50=clk;
   clkgen #(25000000) my_vgaclk(clk,rst,1'b1,clk_25);
   //interface connection instruction
   reg [23:0] mem_vga [327679:0];//nvboard图片为524287不知道为什么没有修改
   wire [23:0] mem_out_wire;
   assign mem_out_wire=mem_vga[{h_addr,v_addr[8:0]}];
   initial begin
      $readmemh("/home/chang/programs/ysyx-workbench/projects/VGA_controler/resource/640x512.hex",mem_vga);
   end
   //design module instance 
   wire      [9:0]       h_addr         ;   //提供给上层模块的当前扫描像素点坐标
   wire      [9:0]       v_addr         ;
   ////vga控制器例化,注意内部参数为640*512的vga信号时序
   vga_ctrl i_vga_ctrl(
      .pclk       (clk),
      .reset      (rst),
      .vga_data   (mem_out_wire), //上层模块提供的VGA颜色数据
      .h_addr     (h_addr),   //提供给上层模块的当前扫描像素点坐标
      .v_addr     (v_addr),
      .hsync      (hsync),    //行同步和列同步信号
      .vsync      (vsync),
      .valid      (valid),    //消隐信号
      .vga_r      (vga_r),    //红绿蓝颜色信号
      .vga_g      (vga_g),
      .vga_b      (vga_b)
   );

   //board function&debug interface instruction

   // test instruction

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