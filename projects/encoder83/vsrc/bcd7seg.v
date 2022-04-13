`timescale 1ps/1ps

module bcd7seg(
  input  [3:0] b,
  output reg [6:0] h
);
// detailed implementation ...
    always @(*) begin
        case(b)
            4'b0000 : h=7'b000_0001;//0
            4'b0001 : h=7'b100_1111;//1
            4'b0010 : h=7'b001_0010;//2
            4'b0011 : h=7'b000_0110;//3
            4'b0100 : h=7'b100_1100;//4
            4'b0101 : h=7'b010_0100;//5
            4'b0110 : h=7'b010_0000;//6
            4'b0111 : h=7'b000_1111;//7
            4'b1000 : h=7'b000_0000;//8
            4'b1001 : h=7'b000_0100;//9
            default : h=7'b000_1001;//error
        endcase
    end

endmodule
            // 4'b0000:h=7'b000000;
            // 4'b0001:h=7'b000000;
            // 4'b0010:h=7'b000000;
            // 4'b0011:h=7'b000000;
            // 4'b0100:h=7'b000000;//4
            // 4'b0101:h=7'b000000;
            // 4'b0110:h=7'b000000;
            // 4'b0111:h=7'b000000;
            // 4'b1001:h=7'b000000;//8
            // 4'b1010:h=7'b000000;//9
// assign segs[0] = 8'b11111101;
// assign segs[1] = 8'b01100000;
// assign segs[2] = 8'b11011010;
// assign segs[3] = 8'b11110010;
// assign segs[4] = 8'b01100110;
// assign segs[5] = 8'b10110110;
// assign segs[6] = 8'b10111110;
// assign segs[7] = 8'b11100000;