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

   reg [15:0] hcounter, vcounter;//为了固定画面因此进行规定
   reg [4:0] cxcounter, cycounter;//为了固定画面因此进行规定
   reg [9:0] h_addr_old, v_addr_old;
   always@(posedge clk)begin
      if(h_addr==10'd0)begin
         hcounter<='d0; 
         pixel_x<='b0;
         char_x<='b0; 
         cxcounter<='b0;
         h_addr_old=h_addr;
      end else if(h_addr!=h_addr_old)begin
         h_addr_old=h_addr;
         hcounter<=hcounter+1;
         if(hcounter>16'd639)begin
            $display("none");
            pixel_x<='b0;
            char_x<='b0; 
            cxcounter<='b0; 
         end else begin
            if(pixel_x==4'd8) pixel_x<='b0; else pixel_x<=pixel_x+1;
            if(char_x==7'd71)begin
               char_x<='b0;
               cxcounter<='b0;
            end else begin
               cxcounter<=(cxcounter==5'd8)?5'd0:cxcounter+1;
               char_x<=(cxcounter==5'd8)?char_x+1:char_x;
            end
         end
      end
   end
   always@(posedge clk)begin
      if(v_addr==10'd0)begin
         vcounter<='d0; 
         pixel_y<='b0;
         v_addr_old<=v_addr;
         char_y<='b0; 
         cycounter<='b0;
      end else if(v_addr!=v_addr_old)begin
         v_addr_old<=v_addr;
         vcounter<=vcounter+1;
         if(vcounter>16'd479)begin 
            char_y<='b0; 
            pixel_y<='b0;
            cycounter<='b0;
         end else begin
            if(pixel_y==4'd15) pixel_y<='b0; else pixel_y<=pixel_y+1;
            if(char_y==5'd31)begin
               char_y<='b0;
               cycounter<='b0;
            end else begin
               cycounter<=(cycounter==5'd15)?5'd0:cycounter+1;
               char_y<=(cycounter==5'd15)?char_y+1:char_y;
            end
         end
      end 
   end
   
endmodule





// module cii_hvaddr_converter(
//    input                   clk          ,//50Mhz clk
//    input                   rst          ,
//    input      [9:0]       h_addr        ,  
//    input      [9:0]       v_addr        ,
//    output   reg   [6:0]       char_x        ,
//    output   reg   [4:0]       char_y        ,
//    output   reg   [3:0]       pixel_x       ,
//    output   reg   [3:0]       pixel_y       

// );

//    reg [15:0] hcounter, vcounter;//为了固定画面因此进行规定
//    reg [9:0] h_addr_old, v_addr_old;
//    always@(posedge clk)begin
//       if(h_addr==10'd0)begin
//          hcounter<='d0; 
//          char_x<='b0; 
//          pixel_x<='b0;
//          h_addr_old=h_addr;
//       end else if(h_addr!=h_addr_old)begin
//          h_addr_old=h_addr;
//          hcounter<=hcounter+1;
//          if(hcounter>16'd639)begin
//             $display("none");
//             char_x<='b0; 
//             pixel_x<='b0;
//          end else begin
//             if(char_x==7'd69) char_x<='b0; else char_x<=char_x+1;
//             if(pixel_x==4'd8) pixel_x<='b0; else pixel_x<=pixel_x+1;
//          end
//       end
//    end
//    always@(posedge clk)begin
//       if(v_addr==10'd0)begin
//          vcounter<='d0; 
//          char_y<='b0; 
//          pixel_y<='b0;
//          v_addr_old<=v_addr;
//       end else if(v_addr!=v_addr_old)begin
//          v_addr_old<=v_addr;
//          vcounter<=vcounter+1;
//          if(vcounter>16'd479)begin 
//             char_y<='b0; 
//             pixel_y<='b0;
//          end else begin
//             if(char_y==5'd29)  char_y<='b0; else char_y<=char_y+1;
//             if(pixel_y==4'd15) pixel_y<='b0; else pixel_y<=pixel_y+1;
//          end
//       end 
//    end
   
// endmodule
