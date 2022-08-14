//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 08/12/2022 12:05:04 PM
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

