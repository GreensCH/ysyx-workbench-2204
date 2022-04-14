`timescale 1ps/1ps


module cii_top(
   input                   clk              ,//50Mhz clk
   input                   rst              ,
   input                   rstn             ,
   input                   ps2_clk          ,
   input                   ps2_data         ,
   output                  hsync            ,    //行同步和列同步信号
   output                  vsync            ,
   output                  valid            ,    //消隐信号
   output      [7:0]       vga_r            ,    //红绿蓝颜色信号
   output      [7:0]       vga_g            ,
   output      [7:0]       vga_b          
);
    always @(*) begin
        // $display("ascii%h",ps2kbd_ascii);
        //$display("h%h,v%h,cx%h,cy%h,px%h,py%h,pixel%b",h_addr,v_addr,char_x,char_y,pixel_x,pixel_y,pixel);
    end
    reg [11:0] mem_graph_ascii [4095:0];//ascii字模
    //测试用系统函数
    initial begin
        $readmemh("/home/chang/programs/ysyx-workbench/projects/char_input_interface/resource/vga_font.txt",mem_graph_ascii);
    end
    //定义vga扫描点
    wire [9:0] h_addr;
    wire [9:0] v_addr;
    //vga控制器输入像素信息选择
    wire[23:0] mem_vga_wire;//输入vgactrl的数据线
    assign mem_vga_wire=mem_ascii_signal;//vgactrl数据线取字符数据
    //vga控制器例化
    vga_ctrl i_vga_ctrl(
       .pclk       (clk),
       .reset      (rst),
       .vga_data   (mem_vga_wire), //上层模块提供的VGA颜色数据
       .h_addr     (h_addr),   //提供给上层模块的当前扫描像素点坐标
       .v_addr     (v_addr),
       .hsync      (hsync),    //行同步和列同步信号
       .vsync      (vsync),
       .valid      (valid),    //消隐信号
       .vga_r      (vga_r),    //红绿蓝颜色信号
       .vga_g      (vga_g),
       .vga_b      (vga_b)
    );
    //vga扫描点转换
    wire [6:0] char_x;wire [4:0] char_y;
    wire [3:0] pixel_x;wire [3:0] pixel_y;
    cii_hvaddr_converter i_hvaddr_conv(
       .clk        (clk),
       .rst        (rst),
       .h_addr     (h_addr),   
       .v_addr     (v_addr),
       .char_x     (char_x ),    
       .char_y     (char_y ),
       .pixel_x    (pixel_x),    
       .pixel_y    (pixel_y)    
    );
    //坐标检索字符界面信息
    //////////
   //  cii_table_ram i_ciittabelram(
   //  .clk           (clk),
   //  .char_x_rd     (char_x),
   //  .char_y_rd     (char_y),
   //  .char_x_we     (7'hzz),
   //  .char_y_we     (5'hzz),
   //  .rd            (1'b1),
   //  .we_vld        (),
   //  .we_rdy        (),
   //  .ascii_we      (8'hzz),       
   //  .ascii_rd      (char_ascii)
   //  );
    wire  [7:0] char_ascii;
    ///////
    cii_table_ctrl i_cii_tab_ctrl(
    .clk            (clk),
    .rstn           (rstn),
    .char_x_rd      (char_x),
    .char_y_rd      (char_y),
    .rd_vld         (1'bz),
    .we_vld         (ps2ctrl_vld),
    .ascii_i        (ps2ctrl_ascii),       
    .ascii_o        (char_ascii)
    );
    ////////
    //字符字模坐标转换
    wire [11:0] base;
    wire [3:0] offset;
    cii_pixel_converter i_cii_pix_conv(
    .ascii(char_ascii),   
    .pixel_x(pixel_x),
    .pixel_y(pixel_y),
    .base(base),
    .offset(offset)
    );
    //像素点索引
    wire pixel;
    assign pixel = mem_graph_ascii[base][offset]; 
    wire[23:0] mem_ascii_signal;//输入vga的字符数据
    //像素点扩展到24位vga信号
    assign mem_ascii_signal=(pixel)?24'hffffff:24'h0;//ff->蓝色


    //ps2 keyboard
    wire [7:0] ps2kbd_data,ps2ctrl_ascii;
    wire ps2kbd_ready,ps2ctrl_nextdata_n,ps2kbd_overflow,ps2ctrl_vld;
    ps2_keyboard i_kbd_control(
       .clk(clk),
       .clrn(rstn),
       .ps2_clk(ps2_clk),
       .ps2_data(ps2_data),
       .data(ps2kbd_data),
       .ready(ps2kbd_ready),
       .nextdata_n(ps2ctrl_nextdata_n),
       .overflow(ps2kbd_overflow)
    );
    ps2kbd_ascii_transfer i_ps2kbd_transfer(
         .clk         (clk),
         .clrn        (rstn),
         .data        (ps2kbd_data),
         .ready       (ps2kbd_ready),
         .ascii       (ps2ctrl_ascii),
         .valid       (ps2ctrl_vld),
         .nextdata_n  (ps2ctrl_nextdata_n)
    );
endmodule
