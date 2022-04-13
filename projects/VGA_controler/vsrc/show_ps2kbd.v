`timescale 1ps/1ps


module show_ps2kbd(
   input                      clk         ,
   input                      clrn        ,
   input          [7:0]       data        ,
   input                      ready       ,
   output         [47:0]      hex_o       ,
   output   reg               nextdata_n  


);
   wire  [3:0] h1,h2,h3,h4;
   wire [3:0] h5,h6;
   reg  [7:0] cnt;
   wire [7:0] ascii_data;
   reg [7:0] data_cache1;
   reg [7:0] data_cache2;
   //与ps2fifo的接口电路
   always@(posedge clk)begin
      //$display("data:%b|%h|%d,c1%b,c2%b,ready%b,nextn%b",data,data,data,data_cache1,data_cache2,ready,nextdata_n);
      if(!clrn)begin
         nextdata_n<=1;
         cnt<='h0;   
      end else if(ready&&nextdata_n)begin//置零与取数据phase
         nextdata_n<=0;
      end else begin//01
         nextdata_n<=1;
      end
   end

   //data 二级缓存
   always@(posedge clk)begin
      if(!clrn)begin
         cnt<=8'h0;
         data_cache2<=8'h0;
      end
      if(ready&&nextdata_n)begin
         if(data_cache1==8'h0)begin//正常数据
            if(data==8'hf0)begin//抬起信号，需清零
               data_cache2<=8'h0;
               cnt<=cnt+1;
            end else begin
               data_cache2<=data;
            end
         end else if(data_cache1==data)begin//非正常数据
            data_cache2<=8'h0;
         end else if(data_cache1!=data)begin//非正常数据
            if(data==8'hf0)begin
               data_cache2<=8'h0;
               cnt<=cnt+1;
            end else begin
               data_cache2<=data;
            end
         end
      end else if(ready&&(~nextdata_n))begin
            data_cache2<=data_cache2;
      end else begin//f0卡住时清零
         if(data==8'hf0)
            data_cache2<=8'h0;   
      end
      data_cache1<=data;//缓存此拍数据
   end
   //ascii与pskbd显示
   assign {h2,h1}=data_cache2;
   assign {h4,h3}=ascii_data;
   assign {h6,h5}=cnt;
   rom_ps2kbd_ascii i_rpa(data_cache2,ascii_data);
   //编码输出
   bcd8seg ihex_1( .b(h1), .h(hex_o[7:0]) );//output a to hex0
   bcd8seg ihex_2( .b(h2), .h(hex_o[15:8]) );//output a to hex0
   bcd8seg ihex_3( .b(h3), .h(hex_o[23:16]) );//output a to hex0
   bcd8seg ihex_4( .b(h4), .h(hex_o[31:24]) );//output a to hex0
   bcd8seg ihex_5( .b(h5), .h(hex_o[39:32]) );//output a to hex0
   bcd8seg ihex_6( .b(h6), .h(hex_o[47:40]) );//output a to hex0
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