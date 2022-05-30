import "DPI-C" function void set_pc_ptr(input logic [63:0] a [], input logic [63:0] b []);

module dpic_pc (
    input   [63 : 0]    pc,
    input   [63 : 0]    npc,
    input               clk,
    input               rst
);

  initial set_pc_ptr(pc, npc);  // rf为通用寄存器的二维数组变量
endmodule