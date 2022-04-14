`timescale 1ps/1ps
//双端ram
module cii_table_ram(
   input                   clk            ,
   input       [6:0]       char_x_rd      ,
   input       [4:0]       char_y_rd      ,
   input       [6:0]       char_x_we      ,
   input       [4:0]       char_y_we      ,
   input                   rd             ,
   input                   we_vld         ,
   output    reg           we_rdy       ,
   input       [7:0]       ascii_we       ,       
   output      [7:0]       ascii_rd       
);

   reg [7:0] mem [69:0][29:0];

   assign ascii_rd = mem[char_x_rd][char_y_rd];//当前指向mem的ascii码数值

   //写入ram数值
   wire [6:0] uchar_x_we;
   wire [4:0] uchar_y_we;
   assign uchar_x_we=(char_x_we==7'hff)?7'h00:char_x_we;
   assign uchar_y_we=(char_y_we==5'hff)?5'h00:char_y_we;
   always@(posedge clk)begin
      if(we_vld)begin
         $display("ram@assci:%h,incx:%h,incx:%h",ascii_we,uchar_x_we,uchar_y_we);
         we_rdy=0;
         mem[0][char_y_we] = ascii_we;
      end else begin
         we_rdy=1;
      end
   end
   
endmodule