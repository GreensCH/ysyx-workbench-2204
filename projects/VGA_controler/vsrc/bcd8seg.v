`timescale 1ps/1ps

module bcd8seg(
  input  [3:0] b,
  output reg [7:0] h
);
// detailed implementation ...
    always @(*) begin
        case(b)
            4'b0000 : h=8'b0000_0011;//0
            4'b0001 : h=8'b1001_1111;//1
            4'b0010 : h=8'b0010_0101;//2
            4'b0011 : h=8'b0000_1101;//3
            4'b0100 : h=8'b1001_1001;//4
            4'b0101 : h=8'b0100_1001;//5
            4'b0110 : h=8'b0100_0001;//6
            4'b0111 : h=8'b0001_1111;//7
            4'b1000 : h=8'b0000_0001;//8
            4'b1001 : h=8'b0000_1001;//9
            4'b1010 : h=8'b0001_0001;//A
            4'b1011 : h=8'b1100_0001;//B
            4'b1100 : h=8'b0110_0011;//C
            4'b1101 : h=8'b1000_0101;//D
            4'b1110 : h=8'b0110_0001;//E
            4'b1111 : h=8'b0111_0001;//F
            default : h=8'b0000_0000;//error
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