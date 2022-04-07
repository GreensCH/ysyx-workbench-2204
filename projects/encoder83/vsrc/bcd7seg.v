module bcd7seg(
  input  [3:0] b,
  output reg [6:0] h
);
// detailed implementation ...
    always@(*)begin
        case(b)
            4'b0000:h=7'b000000;
            4'b0001:h=7'b000001;
            4'b0010:h=7'b000010;
            4'b0011:h=7'b000100;
            4'b0100:h=7'b001000;//4
            4'b0101:h=7'b010000;
            4'b0110:h=7'b010000;
            4'b0111:h=7'b100000;
            4'b1001:h=7'b000000;//8
            4'b1010:h=7'b000000;//9
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