import "DPI-C" function void set_gpr_ptr(input logic [63:0] a []);

module dpic_difftest (
    input   [63 : 0]    rf,
    input               clk,
    input               rst
);
  initial set_gpr_ptr(rf);  // rf为通用寄存器的二维数组变量
endmodule