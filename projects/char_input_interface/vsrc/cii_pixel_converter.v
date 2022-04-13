`timescale 1ps/1ps

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

   assign offset = pixel_x ;
   // assign pixel = line[pixel_x+3'd3]; 
endmodule
