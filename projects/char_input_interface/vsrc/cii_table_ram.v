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
   output    reg  [7:0]       ascii_rd       
);
   localparam ERROR = -1;

   // wire [4:0] uchar_y_rd;
   // wire [6:0] uchar_x_rd;
   // assign uchar_x_rd = (char_x_rd!=7'd70)?(char_x_rd!=7'd0)?char_x_rd:0:0;
   // assign uchar_y_rd = (char_y_rd!=5'd30)?char_y_rd:0;

      // if(ascii_rd!=8'h00)
      //    $display("ram@assci:%h,rdx%d,rdy%d,incx:%d,incy:%d",ascii_rd,char_x_rd,char_y_rd,uchar_x_we,uchar_y_we);
   reg [7:0] mem [70:0][30:0];
   always@(posedge clk)begin
      if(char_x_rd!=ERROR&&char_y_rd!=ERROR)begin
         ascii_rd = mem[char_x_rd][char_y_rd];//当前指向mem的ascii码数值
      end
      else
         ascii_rd = 8'h00;
   end

   //写入ram数值
   wire [4:0] uchar_y_we;
   wire [6:0] uchar_x_we;
   assign uchar_x_we=(char_x_we==7'hff)?7'h00:char_x_we;
   assign uchar_y_we=(char_y_we==5'hff)?5'h00:char_y_we;
   always@(posedge clk)begin
      if(we_vld)begin
         // $display("ram@assci:%h,incx:%d,incy:%d",ascii_we,uchar_x_we,uchar_y_we);
         we_rdy=0;
         mem[uchar_x_we][uchar_y_we] = ascii_we;
      end else begin
         we_rdy=1;
      end
   end
   
endmodule