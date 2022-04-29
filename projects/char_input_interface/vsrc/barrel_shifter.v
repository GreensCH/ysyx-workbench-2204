`timescale 1ps/1ps
//N(8)-bits barrel shifter

module barrel_shifter #
(
    parameter N = 8
)
(
    input    [N-1:0]     Din    ,
    input    [2:0]      Shamt  ,
    input                A_L    ,//0算术移位,1逻辑意味
    input                L_R    ,//0左移,1右移
    output   [N-1:0]     Dout
);

    wire [N-1:0] Q_1,Q_2;
    always @(*) begin
        shifter_N_1(Shamt[0],Din,A_L,L_R,Q_1);
        shifter_N_2(Shamt[1],Q_1,A_L,L_R,Q_2);
        shifter_N_4(Shamt[2],Q_2,A_L,L_R,Dout);
    end

task shifter_N_1;
    input                EN     ;//使能信号
    input    [N-1:0]     Din    ;
    input                A_L    ;//0算术移位,1逻辑意味
    input                L_R    ;//0左移,1右移
    output   [N-1:0]     Dout   ;
    if(EN)
        Dout = L_R ?{Din[N-2:0],1'b0}: //L_R=1左移(both a_l),L_R=0右移
                A_L ? {Din[N-1],Din[N-1:1]}:{1'b0,Din[N-1:1]};//A_L=1算术右移,A_L=0逻辑右移
    else
        Dout = Din;
endtask

task shifter_N_2;
    input                EN     ;//使能信号
    input    [N-1:0]     Din    ;
    input                A_L    ;//0算术移位,1逻辑意味
    input                L_R    ;//0左移,1右移
    output   [N-1:0]     Dout   ;
    if(EN)
        Dout = L_R ?{Din[N-3:0],2'b00}: //L_R=1左移(both a_l),L_R=0右移
                A_L ? {{2{Din[N-1]}},Din[N-1:2]}:{2'b00,Din[N-1:2]};//A_L=1算术右移,A_L=0逻辑右移
    else
        Dout = Din;
endtask

task shifter_N_4;
    input                EN     ;//使能信号
    input    [N-1:0]     Din    ;
    input                A_L    ;//0算术移位,1逻辑意味
    input                L_R    ;//0左移,1右移
    output   [N-1:0]     Dout   ;
    if(EN)
        Dout = L_R ?{Din[N-5:0],4'b0000}: //L_R=1左移(both a_l),L_R=0右移
                A_L ? {{4{Din[N-1]}},Din[N-1:4]}:{4'b0000,Din[N-1:4]};//A_L=1算术右移,A_L=0逻辑右移
    else
        Dout = Din;
endtask

endmodule
