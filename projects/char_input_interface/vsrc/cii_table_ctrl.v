`timescale 1ps/1ps
//双端ram
module cii_table_ctrl
#(
   parameter WIDTH = 640,
   parameter HEIGHT = 480,
   parameter CHARW = 70,
   parameter CHARH = 30
)
(
    input                   clk             ,
    input                   rstn            ,
    input       [6:0]       char_x_rd       ,
    input       [4:0]       char_y_rd       ,

    input                   rd_vld          ,//悬空
    input                   we_vld         ,//
    input       [7:0]       ascii_i         ,       
    output   reg   [7:0]       ascii_o        
);
    localparam ERROR = -1;
    //
    reg [7:0] mem [70:0][30:0];
    //读逻辑
    always@(posedge clk)begin
      if(char_x_rd!=ERROR&&char_y_rd!=ERROR)begin
         ascii_o = mem[char_x_rd][char_y_rd];//当前指向mem的ascii码数值
      end
      else
         ascii_o = 8'h00;
    end
    //传入ram的ascii值
    reg [6:0] char_x_we;
    reg [4:0] char_y_we;
    //定义了两个写指针
    reg [6:0]       point_x;
    reg [4:0]       point_y;
    //写逻辑
    always@(posedge clk)begin
        if(!rstn)begin
            point_x=7'h00;
            point_y=5'd00;
            mem[0][0]=8'd222;
        end 
        else if(we_vld)begin
            case(ascii_i)
            8'd08:begin//backspace
                mem[point_x][point_y]=8'h00;
                mem[point_x+1][point_y]=8'h00;
                if(point_y==0)begin
                    point_y=0;
                    point_x=(point_x==0)?0:point_x-1;
                end else begin
                    point_y=(point_x==0) ? point_y-1 : point_y;
                    point_x=(point_x==0) ? CHARW-1  : point_x-1;
                end
                mem[point_x][point_y] =8'd222;
            end
            8'h0D:begin//enter
                mem[point_x][point_y]=8'h00;
                point_x=0;
                point_y=((point_y + 1) == CHARH ) ? 0 : point_y + 1;
                mem[0][point_y]=8'd222;
            end
            default:begin 
                mem[point_x][point_y]   =ascii_i;
                point_x=!(point_x == (CHARW-1)) ? point_x + 1 : 0;
                point_y=!(point_x == (CHARW-1)) ? point_y : //输入行溢出逻辑可在此处修改
                            ((point_y + 1) == CHARH ) ? 0 : point_y + 1;
                mem[point_x][point_y] =8'd222;
            end
            endcase
            $display("mem[point_x][point_y]%d,point_x%d,point_y%d",mem[point_x][point_y],point_x,point_y);
            // $display("%bctrl@assci:%h,asscii_i%d,pocx%d,pocy%d,incx:%d,incy:%d",ram_we_vld,ram_we_ascii,ascii_i,point_x,point_y,char_x_we,char_y_we);
        end
        else begin
            // ram_we_vld_ctrl<=0;
            // ram_we_ascii_ctrl<=8'h00;
        end
    end

endmodule

// task assci_we_font;
//     input   [7:0]   ascii;
//     output  reg [7:0]   ram_we_ascii;
//     inout  reg [6:0]   char_x_we;
//     inout  reg [4:0]   char_y_we;
// begin
    
//     ram_we_ascii = ascii;
//     char_y_we = !(char_x_we == (CHARW-1)) ? char_y_we : 
//                                         ((char_y_we + 1) == CHARH ) ? 0 : char_y_we + 1;
//     char_x_we = !(char_x_we == (CHARW-1)) ? char_x_we + 1 : 0;
// end
// endtask
// task assci_we_back;
//     input   [7:0]   ascii;
//     output  reg [7:0]   ram_we_ascii;
//     inout  reg [6:0]   char_x_we;
//     inout  reg [4:0]   char_y_we;
// begin
    
//     ram_we_ascii = ascii;
//     char_y_we = (char_x_we != -1) ? char_y_we : 
//                                         ((char_y_we - 1) == -1 ) ? 0 : char_y_we - 1;
//     char_x_we = (char_x_we != -1) ? char_x_we  : CHARW-1;
// end
// endtask


/*
    //写逻辑溢出逻辑
    always@(posedge clk)begin
        if(!rstn)begin
            char_x_we=7'd68;
            char_y_we=5'd00;
            ram_we_ascii=8'h00;
            ram_we_vld=0;
        end 
        else if(we_vld)begin

            case(ascii_i)
            8'd08:begin//backspace
                $display("delete noraml");
                char_x_we = char_x_we -1;
                assci_we_back(ascii_i,ram_we_ascii,char_x_we,char_y_we);
                // ram_we_ascii = 8'h00;
                // char_y_we = !(char_x_we == 0) ? char_y_we : 
                //                                     ((char_y_we - 1) == -1 ) ? 0 : char_y_we - 1;
            end
            // 8'h0D:;//enter
            default:assci_we_font(ascii_i,ram_we_ascii,char_x_we,char_y_we);
            endcase
            // char_y_we = !(char_x_we == (CHARW-1)) ? char_y_we : 
            //                             ((char_y_we + 1) == CHARH ) ? 0 : char_y_we + 1;
            // char_x_we = char_x_we + 1;
            $display("ctrl@assci:%h,incx:%d,incy:%d",ascii_i,char_x_we,char_y_we);
            ram_we_vld=1;
        end
        else begin
            ram_we_vld=0;
            ram_we_ascii=8'h00;
        end
    end*/

/*
//写逻辑控制逻辑
// task ascii_we_ctrl;
//     input   [7:0]   ascii;
//     output  [7:0]   ram_we_ascii;
//     output  [6:0]   char_x_we;
//     output  [4:0]   char_y_we;
//     case(ascii)
//         8'd08:begin//backspace
//             $display("delete");
//             if(|char_x_rd)begin
//                 char_x_we=char_x_we-1;
//                 assci_we(ascii,ram_we_ascii,char_x_we,char_y_we);
//             end
//             else begin
//                 char_x_we=CHARW-1;
//                 char_y_we=char_y_we-1;
//                 assci_we(ascii,ram_we_ascii,char_x_we,char_y_we);
//             end
//         end
//         8'h0D:;//enter
//     default:assci_we(ascii,ram_we_ascii,char_x_we,char_y_we);
//     endcase
// endtask
*/

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