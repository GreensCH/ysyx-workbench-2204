
module ysyx_040978_mdu (
  input             clock   ,
  input             reset   ,
  input             is_mu      ,
  input             mul_32     ,
  input   [1:0]     mul_signed ,
  input             is_du      ,
  input             is_div     ,
  input             div_signed ,
  input             remu    ,
  input   [63: 0]   src1    ,
  input   [63: 0]   src2    ,
  output  [63: 0]   result  ,
  output            ready 
);

  ///////////例化////////////
  wire    d_o_valid;
  wire [63:0] d_o_q, d_o_r, d_res;
  ///////////除法器//////////
  ysyx_040978_diver du(
    .clock     (clock),
    .reset     (reset),
    .in_valid  (is_du & ~d_o_valid),
    .div_signed(div_signed),
    .dividend  (src1),
    .divisor   (src2),
    
    .out_valid (d_o_valid),
    .quotient  (d_o_q),
    .remainder (d_o_r)
    );
  assign d_res = is_div ? d_o_q : d_o_r;
  ///////////非法除法检查//////
  ///////////乘法器//////////
  wire [63:0] m_o_hi, m_o_lo, m_res;
  wire m_o_valid;

  ysyx_040978_muler mu(
    .clock       (clock),
    .reset       (reset),// high active
    .in_valid    (is_mu & ~m_o_valid),// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
    .mul_signed  (mul_signed),// 2'b11（signed x signed）；2'b10（signed x unsigned）；2'b00（unsigned x unsigned）；
    .multiplicand(src1),// 被乘数，xlen 表示乘法器位数
    .multiplier  (src2),// 乘数

    .out_valid   (m_o_valid),// 高表示乘法器输出的结果有效
    .result_hi   (m_o_hi),// 高 xlen bits 结果
    .result_lo   (m_o_lo)// 低 xlen bits 结果    
  );
  assign m_res =  mul_32   ? m_o_lo : m_o_hi;

  ///////////输出////////////
  assign result = is_du ? d_res : m_res;
  assign ready = d_o_valid | m_o_valid | ~(is_du | is_mu) ;//d_o_ready & m_o_ready;

endmodule
