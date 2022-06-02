import "DPI-C" function void set_gpr_ptr(input logic [63:0] a []);

module dpic_regfile (
    input   [63 : 0]    gpr_0 ,
    input   [63 : 0]    gpr_1 ,
    input   [63 : 0]    gpr_2 ,
    input   [63 : 0]    gpr_3 ,
    input   [63 : 0]    gpr_4 ,
    input   [63 : 0]    gpr_5 ,
    input   [63 : 0]    gpr_6 ,
    input   [63 : 0]    gpr_7 ,
    input   [63 : 0]    gpr_8 ,
    input   [63 : 0]    gpr_9 ,
    input   [63 : 0]    gpr_10,
    input   [63 : 0]    gpr_11,
    input   [63 : 0]    gpr_12,
    input   [63 : 0]    gpr_13,
    input   [63 : 0]    gpr_14,
    input   [63 : 0]    gpr_15,
    input   [63 : 0]    gpr_16,
    input   [63 : 0]    gpr_17,
    input   [63 : 0]    gpr_18,
    input   [63 : 0]    gpr_19,
    input   [63 : 0]    gpr_20,
    input   [63 : 0]    gpr_21,
    input   [63 : 0]    gpr_22,
    input   [63 : 0]    gpr_23,
    input   [63 : 0]    gpr_24,
    input   [63 : 0]    gpr_25,
    input   [63 : 0]    gpr_26,
    input   [63 : 0]    gpr_27,
    input   [63 : 0]    gpr_28,
    input   [63 : 0]    gpr_29,
    input   [63 : 0]    gpr_30,
    input   [63 : 0]    gpr_31,
    input               clk,
    input               rst
);
  wire [63:0] rf [32:0];
  assign rf [0]  =  gpr_0;
  assign rf [1]  =  gpr_1;
  assign rf [2]  =  gpr_2;
  assign rf [3]  =  gpr_3;
  assign rf [4]  =  gpr_4;
  assign rf [5]  =  gpr_5;
  assign rf [6]  =  gpr_6;
  assign rf [7]  =  gpr_7;
  assign rf [8]  =  gpr_8;
  assign rf [9]  =  gpr_9;
  assign rf [10] =  gpr_10;
  assign rf [11] =  gpr_11;
  assign rf [12] =  gpr_12;
  assign rf [13] =  gpr_13;
  assign rf [14] =  gpr_14;
  assign rf [15] =  gpr_15;
  assign rf [16] =  gpr_16;
  assign rf [17] =  gpr_17;
  assign rf [18] =  gpr_18;
  assign rf [19] =  gpr_19;
  assign rf [20] =  gpr_20;
  assign rf [21] =  gpr_21;
  assign rf [22] =  gpr_22;
  assign rf [23] =  gpr_23;
  assign rf [24] =  gpr_24;
  assign rf [25] =  gpr_25;
  assign rf [26] =  gpr_26;
  assign rf [27] =  gpr_27;
  assign rf [28] =  gpr_28;
  assign rf [29] =  gpr_29;
  assign rf [30] =  gpr_30;
  assign rf [31] =  gpr_31;
  initial set_gpr_ptr(rf);  // rf为通用寄存器的二维数组变量
  always@(*)begin
    $display("%d\n",rf[17]);
    $display("%d\n",rf[18]);
    $display("%d\n",rf[19]);
end
endmodule