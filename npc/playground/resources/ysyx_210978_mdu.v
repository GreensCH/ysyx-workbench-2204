module ysyx_210978_mdu (
  input             clock   ,
  input             reset   ,
  input             flush   ,
  input             mul     ,
  input             mulh    ,
  input             mulhu   ,
  input             mulhsu  ,
  input             div     ,
  input             divu    ,
  input             rem     ,
  input             remu    ,
  input   [63: 0]   src1    ,
  input   [63: 0]   src2    ,
  output  [63: 0]   result  ,
  output            ready 
);

  ///////////例化////////////
  wire    d_i_valid, d_i_signed;
  assign  d_i_valid = div | divu | rem | remu;
  assign  d_i_signed = div | rem;
  wire    d_o_ready, d_o_valid;
  wire [63:0] d_o_q, d_o_r, d_res;
  ///////////除法器//////////
  ysyx_210978_diver du(
    .clock     (clock),
    .reset     (reset),
    .flush     (flush),
    .in_valid  (d_i_valid & ~d_o_valid),
    .divw      (1'b0),
    .div_signed(d_i_signed),
    .dividend  (src1),
    .divisor   (src2),
    .out_ready (d_o_ready),
    .out_valid (d_o_valid),
    .quotient  (d_o_q),
    .remainder (d_o_r)
    );
  assign d_res = (div | divu) ? d_o_q : d_o_r;
  ///////////非法除法检查//////
  ///////////乘法器//////////
  wire m_i_valid;
  assign m_i_valid = mul | mulh | mulhu |mulhsu;
  wire [1:0] m_i_signed;
  assign m_i_signed[0] = mul | mulh;
  assign m_i_signed[1] = mul | mulh | mulhsu;
  wire [63:0] m_o_hi, m_o_lo, m_res;
  wire m_o_ready, m_o_valid;
  ysyx_210978_muler mu(
    .clock       (clock),
    .reset       (reset),// high active
    .in_valid    (m_i_valid & ~m_o_valid),// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
    .flush       (flush),// 为高表示取消乘法
    .mulw        (1'b0),// 为高表示是 32 位乘法
    .mul_signed  (m_i_signed),// 2'b11（signed x signed）；2'b10（signed x unsigned）；2'b00（unsigned x unsigned）；
    .multiplicand(src1),// 被乘数，xlen 表示乘法器位数
    .multiplier  (src2),// 乘数

    .out_ready   (m_o_ready),// 高表示乘法器准备好，表示可以输入数据
    .out_valid   (m_o_valid),// 高表示乘法器输出的结果有效
    .result_hi   (m_o_hi),// 高 xlen bits 结果
    .result_lo   (m_o_lo)// 低 xlen bits 结果    
  );
  assign m_res =  mul   ? m_o_lo : m_o_hi;

  ///////////输出////////////
  assign result = d_i_valid ? d_res : m_res;
  assign ready = d_o_valid | m_o_valid | ~(d_i_valid | m_i_valid) ;//d_o_ready & m_o_ready;

endmodule

