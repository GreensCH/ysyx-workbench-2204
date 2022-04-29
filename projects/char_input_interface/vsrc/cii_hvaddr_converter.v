`timescale 1ps/1ps


module cii_hvaddr_converter
#(
   parameter WIDTH = 640,
   parameter HEIGHT = 480,
   parameter CHARW = 70,
   parameter CHARH = 30,
   parameter PIXW = 9,
   parameter PIXH = 16

)
(
   input                   clk          ,//50Mhz clk
   input                   rst          ,
   input      [9:0]       h_addr        ,  
   input      [9:0]       v_addr        ,
   output   reg   [6:0]       char_x        ,
   output   reg   [4:0]       char_y        ,
   output   reg   [3:0]       pixel_x       ,
   output   reg   [3:0]       pixel_y       

);
   localparam ERROR=-1;
   localparam IWIDTH = CHARW*PIXW;
   localparam IHEIGHT = CHARH*PIXH;
   // always @(*) begin
   //    $display("haddr%h,vaddr%h,cx%d,cy%d,px%d,py%d",h_addr,v_addr,char_x,char_y,pixel_x,pixel_y);
   // end

   reg [9:0] h_addr_old, v_addr_old;
   reg [4:0] xcount, ycount;

   always@(posedge clk)begin
      if(rst)begin
         h_addr_old<=h_addr;
         pixel_x<=0;
         char_x<=0; 
      end 
      else if(h_addr_old!=h_addr)begin
         h_addr_old<=h_addr;
         if(h_addr==(WIDTH))begin
            // $display("ERROR");
            pixel_x  <= 0;
            char_x   <= 0;
         end else if(h_addr==10'h0)begin
            pixel_x  <= 0;
            char_x   <= 0;        
         end
         else if(h_addr<IWIDTH) begin
            pixel_x  <= (pixel_x==(PIXW-1)) ? 0 : pixel_x+1;
            char_x   <= (char_x == (CHARW)) ? 0 :
                              (pixel_x==8) ? char_x+1 : char_x;
         end 
         else begin
            pixel_x  <= 0;
            char_x   <= 0; 
         end
      end 
      else begin
         h_addr_old<=h_addr_old;
      end
   end

   always@(posedge clk)begin
      if(rst)begin
         v_addr_old<=v_addr;
         pixel_y  <=0;
         char_y   <=0; 
      end 
      else if(v_addr_old!=v_addr)begin
         v_addr_old<=v_addr;
         if(v_addr>(HEIGHT))begin
            pixel_y  <= 0;
            char_y   <= ERROR;
         end 
         else if(h_addr==10'h0&&v_addr==10'h0)begin//h_addr而不是v_addr
            pixel_y  <= 0;
            char_y   <= 0;        
         end
         else if(v_addr<IHEIGHT) begin
            pixel_y  <= ({1'b0,pixel_y}==(PIXH-1)) ? 0 : pixel_y+1;
            char_y   <= (char_y == (CHARH))? 0 : 
                              (pixel_y==15) ? char_y + 1 : char_y;
         end
         else begin
            pixel_y  <= 0;
            char_y   <= 0; 
         end
      end 
      else begin
         v_addr_old<=v_addr_old;
      end
   end


   // reg [9:0] h_addr_old, v_addr_old;
   // reg [15:0] hcounter, vcounter;//为了固定画面因此进行规定
   // reg [4:0] cxcounter, cycounter;//为了固定画面因此进行规定
   // always@(posedge clk)begin
   //    if(h_addr==10'd0)begin
   //       hcounter<='d0; 
   //       pixel_x<='b0;
   //       char_x<='b0; 
   //       cxcounter<='b0;
   //       h_addr_old=h_addr;
   //    end else if(h_addr!=h_addr_old)begin
   //       h_addr_old=h_addr;
   //       hcounter<=hcounter+1;
   //       if(hcounter>16'd639)begin
   //          $display("none");
   //          pixel_x<='b0;
   //          char_x<=ERROR; 
   //          cxcounter<='b0; 
   //       end else begin
   //          if(pixel_x==4'd8) pixel_x<='b0; else pixel_x<=pixel_x+1;
   //          if(char_x==7'd71)begin
   //             char_x<=ERROR;
   //             cxcounter<='b0;
   //          end else begin
   //             cxcounter<=(cxcounter==5'd8)?5'd0:cxcounter+1;
   //             char_x<=(cxcounter==5'd8)?char_x+1:char_x;
   //          end
   //       end
   //    end
   // end
   // always@(posedge clk)begin
   //    if(v_addr==10'd0)begin
   //       vcounter<='d0; 
   //       pixel_y<='b0;
   //       v_addr_old<=v_addr;
   //       char_y<='b0; 
   //       cycounter<='b0;
   //    end else if(v_addr!=v_addr_old)begin
   //       v_addr_old<=v_addr;
   //       vcounter<=vcounter+1;
   //       if(vcounter>16'd479)begin 
   //          char_y<='b0; 
   //          pixel_y<='b0;
   //          cycounter<='b0;
   //       end else begin
   //          if(pixel_y==4'd15) pixel_y<='b0; else pixel_y<=pixel_y+1;
   //          if(char_y==5'd31)begin
   //             char_y<='b0;
   //             cycounter<='b0;
   //          end else begin
   //             cycounter<=(cycounter==5'd15)?5'd0:cycounter+1;
   //             char_y<=(cycounter==5'd15)?char_y+1:char_y;
   //          end
   //       end
   //    end 
   // end
   
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
