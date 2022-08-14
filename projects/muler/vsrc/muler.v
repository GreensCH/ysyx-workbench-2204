`timescale 1ps / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 08/11/2022 04:57:42 PM
// Design Name: 
// Module Name: muler
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////

//乘法器端口信号


`define XLEN 64
module muler(
  input                   clock       ,
  input                   reset       ,// high active
  input                   mul_valid   ,// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
  input                   flush       ,// 为高表示取消乘法
  input                   mulw        ,// 为高表示是 32 位乘法
  input     [1 : 0]       mul_signed  ,// 2'b11（signed x signed）；2'b10（signed x unsigned）；2'b00（unsigned x unsigned）；
  input     [`XLEN-1 : 0] multiplicand,// 被乘数，xlen 表示乘法器位数
  input     [`XLEN-1 : 0] multiplier  ,// 乘数

  output                  mul_ready   ,// 高表示乘法器准备好，表示可以输入数据
  output                  out_valid   ,// 高表示乘法器输出的结果有效
  output    [`XLEN-1 : 0] result_hi   ,// 高 xlen bits 结果
  output    [`XLEN-1 : 0] result_lo    // 低 xlen bits 结果    
  );

  wire [`XLEN : 0] multiplier_1;
  wire [`XLEN : 0] multiplicand_1;
  assign multiplier_1   = {1'b0, multiplicand};
  assign multiplicand_1 = {1'b0, multiplicand};


  reg [2*`XLEN : 0] result_128;
  //output
  assign result_hi = result_128[127 : 64];
  assign result_hi = result_128[63 : 0];

endmodule


module booth3_pmgen(
  input y,//y_i+1 y y_i-1
  input y_add,
  input y_sub,
  input x,
  input x_sub,
  output p
);

//booth选择信号的生成
///y+1,y,y-1///
wire sel_negative, sel_double_negative, sel_positive, sel_double_positive;


assign sel_negative =  y_add & (y & ~y_sub | ~y & y_sub);
assign sel_positive = ~y_add & (y & ~y_sub | ~y & y_sub);
assign sel_double_negative =  y_add & ~y & ~y_sub;
assign sel_double_positive = ~y_add &  y &  y_sub;

//结果选择逻辑
assign p = ~(~(sel_negative & ~x) & ~(sel_double_negative & ~x_sub) 
           & ~(sel_positive & x ) & ~(sel_double_positive &  x_sub));

endmodule