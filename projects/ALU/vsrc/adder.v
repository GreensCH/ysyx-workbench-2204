`timescale 1ps/1ps
//有符号的补码加减法器
//最高位n-1为符号为
module adder #
(
    parameter n = 4
)
(
   input      [n-1:0]       a   ,
   input      [n-1:0]       b   ,
   input                    add_sub  ,//0-add 1-sub
   
   output                   carry,//进位
   output                   zero,//结果是否为0
   output                   overflow,//溢出
   output     [n-1:0]       s 
);
    //减数计算
    wire [n-1:0] t_as;
    assign t_as = { {n{add_sub}} ^ b } + //与add_sub异或，0为加法b不变，1为减法b取反
                            {{(n-1){1'b0}} , add_sub};//b反+1变补码

    wire carry_f;
    assign {carry_f,s} =  a + t_as ;
    assign carry = carry_f ^ add_sub;//carry的减法错位
    //溢出逻辑
    assign overflow = ((a[n-1]==t_as[n-1])&&(s[n-1]!=a[n-1]));
    //全0逻辑
    assign zero = ~(|s);

endmodule


    // wire carry_f ;
    // assign {carry_f,s} = add_sub ? a + b_f ://1 为 sub
    //                              a + b;// 0 为 add
    // assign carry = carry_f ^ add_sub;//carry的减法错位
    // assign overflow = ((a[n-1]==b[n-1])&&(s[n-1]!=a[n-1]));
    // assign zero = ~(|s);

    // //减数按位取反后+1
    // wire [n-1:0] b_f;
    // assign b_f = ~b + 1'b1;//取反