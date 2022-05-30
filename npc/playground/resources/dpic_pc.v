import "DPI-C" function void set_pc(output longint pc,  output longint npc);

module dpic_pc (
    output   [63 : 0]    pc,
    output   [63 : 0]    npc,
    input               clk,
    input               rst
);

  always @(*) begin  
    set_pc(pc, npc);  // rf为通用寄存器的二维数组变量
  end
endmodule