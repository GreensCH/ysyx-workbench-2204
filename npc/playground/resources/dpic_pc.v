import "DPI-C" function void set_pc_ptr(input logic [63:0] a []);

module dpic_regfile (
    input   [63 : 0]    pc,
    input               clk,
    input               rst
);

  initial set_pc_ptr(pc);  // rf为通用寄存器的二维数组变量
endmodule