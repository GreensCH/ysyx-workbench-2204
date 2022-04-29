`timescale 1ps/1ps


module ps2kbd_ascii_transfer(
   input                      clk         ,
   input                      clrn        ,
   input          [7:0]       data        ,
   input                      ready       ,
   output         [7:0]       ascii       ,
   output   reg               valid       ,
   output   reg               nextdata_n  
);

   reg [7:0] data_cache1;
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
   always@(posedge clk)begin
      if(!clrn)begin
         state<=IDLE;
         data_cache2<=8'h00;
         valid<=1'b0;
      end
      else if(ready&&~nextdata_n)begin
         //$display("1st:%h,data:%h,c1:%h,c2:%h,ready%b,nextn%b,vld:%b,",state,data,data_cache1,data_cache2,ready,nextdata_n,valid);
         case(state)
            IDLE:begin 
               state<=(data_cache1==8'hf0)?OUT:IDLE;
               data_cache2<=(data_cache1==8'hf0)?8'h00:data_cache1;
               valid<=(data_cache1==8'hf0)?0:1;
               if(data_cache1==8'hf0)$display("GOT OUT");else $display("GET DATA");
            end
            OUT:begin 
               state<=IDLE;
               data_cache2<=8'h00;
               valid<=1'b0;
               $display("COME BACK TO WAIT");
            end
            default:begin 
               state<=IDLE;
               data_cache2<=8'h00;
               valid<=1'b0;
               $display("ERROR");
            end
         endcase
      end else begin
         data_cache2<=8'h00;
         valid<=1'b0;
      end
   end

   // assign data_cache2 = (state==GET)?data_cache1:8'h00;
   rom_ps2kbd_ascii i_rpa(data_cache2,ascii);

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