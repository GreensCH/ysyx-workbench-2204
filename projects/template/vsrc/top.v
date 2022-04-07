`timescale 1ps/1ps
module top(
  input a,
  input b,
  output f
);
    initial begin
        $dumpfile("./vlt_dump.vcd");
        $dumpvars(1,xor_gate);
    end
    assign f = a ^ b;
endmodule
