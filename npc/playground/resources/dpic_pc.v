import "DPI-C" function void set_pc(input longint pc, input longint npc, input bit is_device);

module dpic_pc (
    input   [63 : 0]    pc,
    input   [63 : 0]    npc,
    input               is_device,
    input               clk,
    input               rst
);

  always @(*) begin
    if(pc != 0)
        set_pc(pc, npc, is_device);  // rf为通用寄存器的二维数组变量
  end
endmodule