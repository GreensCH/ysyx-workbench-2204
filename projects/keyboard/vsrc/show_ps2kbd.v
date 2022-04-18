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
   reg [7:0] ascii_data;
   reg [7:0] data_cache1;
   reg busy;
   //与ps2fifo的接口电路
   always@(posedge clk)begin
      if(!clrn)begin
         nextdata_n<=1;   
         data_cache1<=8'h00;
      end else if(ready&&nextdata_n)begin//置零与取数据phase
         //$display("1st:%h,data:%h,c1:%h,c2:%h,ready%b,nextn%b,vld:%b,",state,data,data_cache1,data_cache2,ready,nextdata_n,valid);
         nextdata_n<=0;
         data_cache1<=data;
      end else begin//01
         //$display("2st:%h,data:%h,c1:%h,c2:%h,ready%b,nextn%b,vld:%b,",state,data,data_cache1,data_cache2,ready,nextdata_n,valid);
         nextdata_n<=1;
         data_cache1<=8'h00;
      end
   end
   localparam IDLE = 0, OUT = 1 ;//, OUT =2;
   reg [3:0] state;
   reg [7:0] data_cache2;
   reg [7:0] data_cache2_old;
   always@(posedge clk)begin
      if(!clrn)begin
         state<=IDLE;
         data_cache2<=8'h00;
         busy<=1'b0;
         cnt<=0;
      end
      else if(ready&&~nextdata_n)begin
         case(state)
            IDLE:begin 
               state<=(data_cache1==8'hf0)?OUT:IDLE;
               data_cache2<=(data_cache1==8'hf0)?8'h00:data_cache1;
               busy<=(data_cache1==8'hf0)?0:1;
               if(data_cache1==8'hf0)begin
                  $display("OUT");
                  cnt<=cnt+1;
               end
               else begin
                  data_cache2_old<=data_cache2;
                  $display("GET DATA");
               end
            end
            OUT:begin 
               state<=IDLE;
               data_cache2<=8'h00;
               busy<=1'b0;
               $display("COME BACK TO WAIT");
            end
            default:begin 
               state<=IDLE;
               data_cache2<=8'h00;
               busy<=1'b0;
               $display("ERROR");
            end
         endcase
      end else begin
         // data_cache2<=8'h00;
         busy<=1'b0;
      end
   end

   // assign data_cache2 = (state==GET)?data_cache1:8'h00;
   rom_ps2kbd_ascii i_rpa(data_cache2,ascii_data);
   //ascii与pskbd显示
   assign {h2,h1}=data_cache2;
   assign {h4,h3}=ascii_data;
   assign {h6,h5}=cnt;
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