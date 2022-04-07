`timescale 1ms/1ps
module xor_gate(
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
