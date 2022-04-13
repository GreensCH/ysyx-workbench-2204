`timescale 1ps/1ps


module top(
   input                   clk            ,//50Mhz clk
   input                   rst            ,
   input                   rstn           ,
   input                   ps2_clk        ,
   input                   ps2_data       ,
   output                  hsync          ,    //行同步和列同步信号
   output                  vsync          ,
   output                  valid          ,    //消隐信号
   output      [7:0]       vga_r          ,    //红绿蓝颜色信号
   output      [7:0]       vga_g          ,
   output      [7:0]       vga_b          
);
   cii_top i_cii_top(
      .clk           (clk     ),//50Mhz clk
      .rst           (rst     ),
      .rstn          (rstn    ),
      .ps2_clk       (ps2_clk ),
      .ps2_data      (ps2_data),
      .hsync         (hsync   ),    //行同步和列同步信号
      .vsync         (vsync   ),
      .valid         (valid   ),    //消隐信号
      .vga_r         (vga_r   ),    //红绿蓝颜色信号
      .vga_g         (vga_g   ),
      .vga_b         (vga_b   ) 
   );
endmodule
