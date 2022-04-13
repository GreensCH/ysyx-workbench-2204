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

   reg [7:0] mem [2099:0];

   wire [15:0] point;
   reg [15:0] counter;
   reg [4:0] char_y_rd_old;
   // always@(posedge clk)begin
   //    if(char_y_rd_old!=char_y_rd)begin//更新扫描点
   //       char_y_rd_old<=char_y_rd;
   //       if(char_y_rd == 5'd0)begin//检测是否为新周期
   //          counter<='b0;//计数器清零
   //       end else begin
   //          counter<=counter+{{8'd0},7'd69};
   //       end
   //    end
   // end

   // assign point = counter + {{8'd0},char_x_rd};//计算当前指示序列id
   // assign ascii_rd = mem[point[11:0]];//当前指向mem的ascii码数值
   assign ascii_rd = mem[char_y_rd*70+char_x_rd];//当前指向mem的ascii码数值

   //assign we_point = ty_counter + {{8'd0},char_x_rd};//计算当前指示序列id
   //写入ram数值
   always@(posedge clk)begin
      //$display("readascii%h,cx%h,cy%h",ascii_rd,char_x_rd,char_y_rd);
      if(we_vld&&ascii_we!=8'h00)begin
         we_rdy=0;
         mem[char_y_we*70+char_x_we] = ascii_we;
         $display("write:%h,counter:%h:@cx%h,cy%h,",ascii_we,counter,char_x_we,char_y_we);
      end else begin
         we_rdy=1;
      end
   end

endmodule