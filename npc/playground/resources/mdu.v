module booth_pmgen
(
  input     [2   : 0]     y_in,
  input     [64  : 0]     x_in,
  output    [128 : 0]     p
  // output                c//负数补码(-[X]补, -2[X]补), 末位+1信号
);

//booth选择信号的生成
    wire y_add,y,y_sub;
    assign {y_add,y,y_sub} = y_in;
    
    
    wire sel_neg, sel_dneg, sel_pos, sel_dpos;
    
    assign sel_neg =  y_add & (y & ~y_sub | ~y & y_sub);
    assign sel_pos = ~y_add & (y & ~y_sub | ~y & y_sub);
    assign sel_dneg =  y_add & ~y & ~y_sub;
    assign sel_dpos = ~y_add &  y &  y_sub;
    
    //结果选择逻辑
    wire [128 : 0] x_in_129;
    wire [128 : 0] p_pos, p_neg, p_dpos, p_dneg;

    assign x_in_129 = {{64{x_in[64]}}, x_in};
    assign p_pos = x_in_129;
    assign p_neg = ~x_in_129 + 128'b1;
    assign p_dpos = x_in_129 << 1;
    assign p_dneg = p_neg << 1; 
    
    assign p =  sel_pos ? p_pos :
                sel_dpos? p_dpos:
                sel_neg ? p_neg :
                sel_dneg? p_dneg: {129{1'b0}};

endmodule


module muler(
    input                   clock       ,
    input                   reset       ,// high active
    input                   in_valid   ,// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
    input                   flush       ,// 为高表示取消乘法
    input                   mulw        ,// 为高表示是 32 位乘法
    input     [1 : 0]       mul_signed  ,// 2'b11（signed x signed）；2'b10（signed x unsigned）；2'b00（unsigned x unsigned）；
    input     [63 : 0]      multiplicand,// 被乘数，xlen 表示乘法器位数
    input     [63 : 0]      multiplier  ,// 乘数

    output                  out_ready   ,// 高表示乘法器准备好，表示可以输入数据
    output                  out_valid   ,// 高表示乘法器输出的结果有效
    output    [63 : 0]      result_hi   ,// 高 xlen bits 结果
    output    [63 : 0]      result_lo    // 低 xlen bits 结果    
  );

    wire [64 : 0] multiplier_0;//符号位扩展
    wire [64 : 0] multiplicand_0;
    assign multiplier_0 = mul_signed[0] == 1'b1 ? {multiplier[63], multiplier} : {1'b0, multiplier};//1 is signed, 0 is unsigned
    assign multiplicand_0 = mul_signed[1] == 1'b1 ? {multiplicand[63], multiplicand} : {1'b0, multiplicand};//1 is signed, 0 is unsigned
    

    reg [5:0] cnt;
    always @(posedge clock) begin
        if(reset) cnt <= 6'd0;
        else if(cnt == 6'd32) cnt <= 6'd0;
        else if(mul_valid) cnt <= 6'd0;
        else if(in_valid | mul_busy) cnt <= cnt + 'h1;
        else cnt <= 6'd0;
    end

    reg  [65 : 0] multiplier_1;//乘数右移,取低三位
    wire [65 : 0] multiplier_2;
    always @(posedge clock) begin
        if(reset) multiplier_1 <= 'h0;
        else if(in_valid & mul_ready) multiplier_1 <= {multiplier_0, 1'b0} >> 2;
        else if(mul_finish) multiplier_1 <= 'h0;
        else if(mul_busy) multiplier_1 <= multiplier_1 >> 2;
        else multiplier_1 <= 'h0;
    end

    reg [64 : 0] multiplicand_1;
    wire [64 : 0] multiplicand_2;
    always @(posedge clock) begin
        if(reset) multiplicand_1 <= 'h0;
        else if(in_valid & mul_ready) multiplicand_1 <= multiplicand_0;
        else if(mul_finish) multiplicand_1 <= 'h0;
        else if(mul_busy) multiplicand_1 <= multiplicand_1;
        else multiplicand_1 <= 'h0;
    end

    assign multiplier_2 = (in_valid & mul_ready) ? {multiplier_0, 1'b0} : multiplier_1;
    assign multiplicand_2 = (in_valid & mul_ready) ? multiplicand_0 : multiplicand_1;
    reg [128 : 0] result_buf;
    always @(posedge clock) begin
        if(reset) result_buf <= 'd0;
        else if(mul_busy) result_buf <= result_buf + p;
        else if(in_valid & mul_ready) result_buf <= p;
        else if(mul_valid) result_buf <= 'd0;
        else result_buf <= result_buf;
    end

    /* partial product */
    wire [128 : 0] pm_p;
    booth_pmgen u(.y_in(multiplier_2[2 : 0]), .x_in(multiplicand_0), .p(pm_p));
    wire [128 : 0] p;//部分积
    assign p = pm_p << 2*cnt;
 

    /* control signal */

    reg  mul_ready, mul_busy;

    always @(posedge clock) begin
        if(reset)begin
            mul_ready <= 1'b1;
        end else if(mul_finish)begin
            mul_ready <= 1'b1;
        end else if (in_valid)begin
            mul_ready <= 1'b0;
        end else begin
            mul_ready <= mul_ready;
        end
    end


    always @(posedge clock) begin
        if(reset)begin
            mul_busy <= 1'b0;
        end else if(mul_finish)begin
            mul_busy <= 1'b0;
        end else if(in_valid)begin
            mul_busy <= 1'b1;
        end else begin
            mul_busy <= mul_busy;
        end
    end

    wire mul_finish;
    assign mul_finish = cnt == 6'd32;
    
    /* output */
    reg mul_valid;
    always @(posedge clock) begin
        if(reset)   mul_valid <= 1'b0;
        else if(cnt == 6'd32) mul_valid <= 1'b1;
        else mul_valid <= 1'b0;
    end
    assign out_ready = mul_ready;
    assign out_valid = mul_valid;

    assign result_hi = mul_valid ? result_buf[127 : 64] : 'd0;
    assign result_lo = mul_valid ? result_buf[63 : 0]   : 'd0;
endmodule

module diver(
    input                   clock       ,
    input                   reset       ,// high active
    input                   flush       ,// 为高表示取消乘法
    input                   in_valid   ,// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
    input                   divw        ,// 为高表示是 32 位乘法
    input                   div_signed  ,// 表示是不是有符号除法，为高表示是有符号除法
    input     [63 : 0]      dividend    ,// 被除数，xlen 表示除法器位数
    input     [63 : 0]      divisor     ,// 除数

    output                  out_ready   ,// 高表示乘法器准备好，表示可以输入数据
    output                  out_valid   ,// 高表示乘法器输出的结果有效
    output    [63 : 0]      quotient    ,// 商
    output    [63 : 0]      remainder    // 余数
  );



    reg [6:0] cnt;
    always @(posedge clock) begin
        if(reset) cnt <= 'd63;
        else if(cnt == 'd0) cnt <= 'd63;
        else if(valid) cnt <= 'd63;
        else if(in_valid | busy) cnt <= cnt - 'd1;
        else cnt <= 'd63;
    end

    // 符号位缓存
    reg dsor_neg, dend_neg, div_signed_1;
    always @(posedge clock) begin
        if(reset)begin
            dsor_neg <= 1'b1;
            dend_neg <= 1'b1;
            div_signed_1 <= 1'b0;
        end else if(in_valid & ready)begin
            if(div_signed)begin
                dsor_neg <= divisor[63];
                dend_neg <= dividend[63];
                div_signed_1 <= 1'b1;
            end else begin
                dsor_neg <= 1'b0;
                dend_neg <= 1'b0;
                div_signed_1 <= 1'b0;
            end
        end else begin
            dsor_neg <= dsor_neg;
            dend_neg <= dend_neg;
            div_signed_1 <= div_signed_1;
        end
    end
    // 输入除数被除数绝对值计算
    wire [63:0] dividend_0;
    wire [63:0] divisor_0;
    assign dividend_0 = dividend[63] & div_signed ? (~dividend + 64'b1) : dividend;
    assign divisor_0  = divisor [63] & div_signed ? (~divisor + 64'b1) : divisor;
    //除数缓存
    reg  [64 : 0] divisor_1;
    wire [64 : 0] divisor_2;
    always @(posedge clock) begin
        if(reset) divisor_1 <= 'h0;
        else if(in_valid & ready) divisor_1 <= {1'b0, divisor_0};
        else if(finish) divisor_1 <= 'h0;
        else divisor_1 <= divisor_1;
    end
    assign divisor_2 = (in_valid==1'b1 && ready==1'b1) ? {1'b0, divisor_0} : divisor_1;
    //试商法,减数
    wire [64:0] subor;
    assign subor = divisor_2[64:0];
    wire [64:0] subend;
    assign subend = (in_valid & ready) ? {64'h0, dividend_0[63]} : dividend_1[127:63];
    wire [64:0] subres;
    assign subres = subend - subor;
    wire subneg;
    assign subneg = subres[64];

    //被除数,商,余数逻辑
    reg [191 : 0] dividend_1;
    always @(posedge clock) begin
        if(reset) dividend_1 <= 'h0;
        else if(in_valid & ready)begin
            if(subneg) dividend_1 <= ({128'h0, dividend_0})<<1;//64 + 64 + 64
            else        dividend_1 <= ({64'h0, subres, dividend_0[62:0]})<<1;
        end else if(valid)begin
            dividend_1 <= 'h0;
        end else if(busy)begin
            if(subneg) dividend_1 <= dividend_1 << 1;
            else        dividend_1 <= ({dividend_1[191:128], subres, dividend_1[62:0]})<<1;
        end else begin
            dividend_1 <= dividend_1;
        end
    end

    reg [63:0] q;
    always @(posedge clock) begin
        if(reset)begin
            q <= 64'h0;
        end else if(in_valid & ready)begin
            if(subneg) q[cnt[5:0]] <= 1'b0;
            else q[cnt[5:0]] <= 1'b1;
        end else if(busy)begin
            if(subneg) q[cnt[5:0]] <= 1'b0;
            else q[cnt[5:0]] <= 1'b1;
        end else if(valid)begin
            q <= 64'h0;
        end else begin
            q <= q;
        end
    end

    /* control signal */
    reg  ready, busy;
    always @(posedge clock) begin
        if(reset)begin
            ready <= 1'b1;
        end else if(finish)begin
            ready <= 1'b1;
        end else if (in_valid)begin
            ready <= 1'b0;
        end else begin
            ready <= ready;
        end
    end
    always @(posedge clock) begin
        if(reset)begin
            busy <= 1'b0;
        end else if(finish)begin
            busy <= 1'b0;
        end else if(in_valid)begin
            busy <= 1'b1;
        end else begin
            busy <= busy;
        end
    end

    wire finish;
    assign finish = cnt == 'd0;
    
    /* output */
    reg valid;
    always @(posedge clock) begin
        if(reset)   valid <= 1'b0;
        else if(cnt == 'd0) valid <= 1'b1;
        else valid <= 1'b0;
    end
    assign out_ready = ready;
    assign out_valid = valid;

    wire [63:0] q_neg;
    wire [63:0] r_neg;
    wire [63:0] q_pos;
    wire [63:0] r_pos;
    assign q_pos = q;
    assign r_pos = dividend_1[127:64];
    assign q_neg = (~q_pos) + 1'b1;
    assign r_neg = (~r_pos) + 1'b1;

    
    wire [63:0] q_res;
    wire [63:0] r_res;
    assign q_res = dend_neg ^ dsor_neg ? q_neg : q_pos;
    assign r_res = dend_neg == 1'b1    ? r_neg : r_pos;

    assign remainder = valid ? r_res : 'd0;
    assign quotient  = valid ? q_res : 'd0;

endmodule


module mdu (
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
  
  diver du(
    .clock     (clock),
    .reset     (reset | flush),
    .flush     (flush),
    .in_valid  (d_i_valid),
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

  wire m_i_valid;
  assign m_i_valid = mul | mulh | mulhu |mulhsu;
  wire [1:0] m_i_signed;
  assign m_i_signed[0] = mul | mulh;
  assign m_i_signed[1] = mul | mulh | mulhsu;
  wire [63:0] m_o_hi, m_o_lo, m_res;
  wire m_o_ready, m_o_valid;
  muler mu(
    .clock       (clock),
    .reset       (reset | flush),// high active
    .in_valid    (m_i_valid),// 为高表示输入的数据有效，如果没有新的乘法输入，在乘法被接受的下一个周期要置低
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
  assign ready = d_o_ready & m_o_ready;

endmodule