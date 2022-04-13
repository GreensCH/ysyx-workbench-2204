`timescale 1ps/1ps


module cii_hvaddr_converter(
   input                   clk          ,//50Mhz clk
   input                   rst          ,
   input      [9:0]       h_addr        ,  
   input      [9:0]       v_addr        ,
   output   reg   [6:0]       char_x        ,
   output   reg   [4:0]       char_y        ,
   output   reg   [3:0]       pixel_x       ,
   output   reg   [3:0]       pixel_y       

);
   //monitor instruction
   // always @(*) begin
   //    $display("haddr%h,vaddr%h",h_addr,v_addr);
   // end
   reg [15:0] hcounter, vcounter;//为了固定画面因此进行规定
   reg [9:0] h_addr_old, v_addr_old;
   always@(posedge clk)begin
      if(h_addr!=h_addr_old)begin
         h_addr_old<=h_addr;
         if(hcounter==16'd639) hcounter<='d0; else hcounter<=hcounter+1;
         if(char_x==7'd69) char_x<='b0; else char_x<=char_x+1;
         if(pixel_x==4'd8) pixel_x<='b0; else pixel_x<=pixel_x+1;
      end else begin
         h_addr_old<='b0;
         hcounter<='b0;
         char_x<='b0;
         pixel_x<='b0;
      end
   end
   always@(posedge clk)begin
      if(v_addr!=v_addr_old)begin
         v_addr_old<=v_addr;
         if(vcounter==16'd479) vcounter<='d0;else vcounter<=vcounter+1;
         if(char_y==5'd29) char_y<='b0; else char_y<=char_y+1;
         if(pixel_y==4'd15) pixel_y<='b0; else pixel_y<=pixel_y+1;
      end else begin
         v_addr_old<='b0;
         vcounter<='b0;
         char_y<='b0;
         pixel_y<='b0;
      end
   end
endmodule


//双端ram
module cii_table_ram(
   input                   clk            ,
   input       [6:0]       char_x_rd      ,
   input       [4:0]       char_y_rd      ,
   input       [6:0]       char_x_we      ,
   input       [4:0]       char_y_we      ,
   input                   rd             ,
   input                   we             ,
   input       [7:0]       ascii_we       ,       
   output      [7:0]       ascii_rd       
);

   reg [7:0] mem [2100-1:0];

   wire [15:0] point;
   reg [15:0] ty_counter;
   reg [4:0] char_y_rd_old;
   always@(posedge clk)begin
      if(char_y_rd_old!=char_y_rd)begin//更新扫描点
         char_y_rd_old<=char_y_rd;
         if(char_y_rd == 5'd0)begin//检测是否为新周期
            ty_counter<='b0;//计数器清零
         end else begin
            ty_counter<=ty_counter+{{10'd0},5'd29};
         end
      end
   end
   assign point = ty_counter + {{8'd0},char_x_rd};//计算当前指示序列id
   assign ascii_rd = mem[point[11:0]];//当前指向mem的ascii码数值
   //写入ram数值
   always@(posedge clk)begin
      if(we)
         mem[char_y_we*70+char_x_we] = ascii_we;
   end

endmodule

//ascii字模读写地址转换
module cii_pixel_converter(
   input       [7:0]       ascii,//0-255
   
   input       [3:0]       pixel_x,//0px-8px 
   input       [3:0]       pixel_y,//0-15
   output      [11:0]      base,
   output      [3:0]       offset
   // output                  pixel
);
   // reg [11:0] mem_graph_ascii [4095:0];//ascii字模
   assign base = {ascii,pixel_y[3:0]};

   // wire [11:0] line;//8-0
   // assign line = mem_graph_ascii[base];

   assign offset = pixel_x + 3'd3;
   // assign pixel = line[pixel_x+3'd3]; 
endmodule



   // reg [9:0] h_addr_old, v_addr_old;

   // always@(posedge clk)begin
   //    if(h_addr!=h_addr_old)begin
   //       h_addr_old<=h_addr;
   //       if(tx_counter=='d69) tx_counter<='b0; else tx_counter<=tx_counter+1;;
   //    end else begin
   //       h_addr_old<='b0;
   //       tx_counter<='b0;
   //    end
   // end
   // always@(posedge clk)begin
   //    if(v_addr!=v_addr_old)begin
   //       v_addr_old<=v_addr;
   //       if(ty_counter=='d29) ty_counter<='b0; else ty_counter<=ty_counter+1;;
   //    end else begin
   //       v_addr_old<='b0;
   //       ty_counter<='b0;
   //    end
   // end