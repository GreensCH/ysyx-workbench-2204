`timescale 1ps/1ps
//双端ram
module cii_table_ctrl(
    input                   clk             ,
    input                   rstn            ,
    input       [6:0]       char_x_rd       ,
    input       [4:0]       char_y_rd       ,

    input                   rd_vld          ,//悬空
    input                   we_vld          ,//ps2kbd ready
    input       [7:0]       ascii_i         ,       
    output      [7:0]       ascii_o        
);


    cii_table_ram cii_t_ram(
        .clk            (clk),      
        .char_x_rd      (char_x_rd),      
        .char_y_rd      (char_y_rd),      
        .char_x_we      (char_x_we),      
        .char_y_we      (char_y_we),      
        .rd             (rd_vld),      
        .we_vld         (ram_we_vld),      
        .we_rdy         (ram_we_rdy),      
        .ascii_we       (ram_we_ascii),      
        .ascii_rd       (ascii_o)    
    );

    reg [7:0] ram_we_ascii;
    reg ram_we_vld,ram_we_rdy;
    always@(posedge clk)begin
        // $display("cx%h,cy%h",char_x_rd,char_y_rd);
        if(ram_we_rdy&&we_vld)begin
            ram_we_ascii<=ascii_i;
            ram_we_vld<=1;
        end else begin
            ram_we_vld<=0;
            ram_we_ascii<=8'h10;
        end
    end
    //定义了两个写指针
    reg [6:0]       char_x_we;
    reg [4:0]       char_y_we;
    always@(posedge clk)begin
        if(!rstn)begin
            char_x_we<=7'd0;
            char_y_we<=5'd0;
        end else if(ram_we_rdy&&we_vld)begin
            char_x_we<=char_x_we+1;
        end else begin
            // char_x_we<=7'hff;
            // char_y_we<=5'hff;
        end
    end
    // always@(*)begin
    //     $display("cx%h,cy%h",char_x_rd,char_y_rd);
    // end

endmodule


    // //检测并缓存键盘输入数据
    // reg [7:0] ascii;
    // always@(posedge clk)begin
    //     $display("%h",we_vld);
    //     if(we_vld)begin
    //         ascii<=ascii_i;
    //     end else begin
    //         ascii<=8'h00;
    //     end
    // end
    // //mem[char_y_we*70+char_x_we] = ascii_we_ps2kbd;

    // //匹配键盘输入数据数值
    // always@(posedge clk)begin
    //     case(ascii)
    //         8'h00:begin
    //             ;//ascii=8'h00;
    //         end
    //         8'd08:begin//backspace
    //             // if(char_x_we==7'h00)begin
    //             //     char_x_we=
    //             // end
    //             char_x_we=(char_x_we==7'h00)?char_x_we:char_x_we-1;
    //         end
    //         8'h0D:begin//enter
    //             char_y_we=char_y_we+1;//(char_y_we==7'h00)?char_x_we:char_x_we-1;
    //         end
    //         default: begin
    //             char_x_we=char_x_we+1;
    //             char_y_we=char_y_we+1;
    //         end
    //     endcase
    // end