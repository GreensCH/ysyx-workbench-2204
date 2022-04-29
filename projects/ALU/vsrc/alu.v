`timescale 1ps/1ps
//有符号数的补码加法器
module alu #
(
    parameter N = 4
)
(
   input      [N-1:0]       a   ,
   input      [N-1:0]       b   ,
   input      [2:0]         sel  ,
   output                   carry,//进位
   output                   zero,//结果是否为0
   output                   overflow,//溢出
   output   reg  [N-1:0]    s  //输出指示数码管
);


    localparam  ADD = 3'b000,
                SUB = 3'b001,
                NOT = 3'b010,
                AND = 3'b011,
                OR  = 3'b100,
                XOR = 3'b101,
                CMP = 3'b110,
                EQU = 3'b111;

    wire [2:0]   flag_add;  //internal add/sub flag signal
    wire [N-1:0] s_add;     //internal add/sub result signal
    wire [2:0]   flag_logic;//internal logic flag signal
    wire [N-1:0] s_logic;   //internal logic  result signal

    wire [N-1:0] s_not;//internal not result signal
    wire [N-1:0] s_and;//internal and result signal
    wire [N-1:0] s_or;//internal or  result signal
    wire [N-1:0] s_xor;//internal xor result signal
    wire [N-1:0] s_cmp;//internal cmp result signal
    wire [N-1:0] s_equ;//internal equ result signal

    //mux 
    always @(*) begin
        case (sel)
            ADD:t_adder(a,b,0,carry,zero,overflow,s);
            SUB:t_adder(a,b,1,carry,zero,overflow,s); 
            NOT:t_not(a,carry,zero,overflow,s);
            AND:t_and(a,b,carry,zero,overflow,s);
            OR :t_or (a,b,carry,zero,overflow,s);
            XOR:t_xor(a,b,carry,zero,overflow,s);
            CMP:t_cmp(a,b,carry,zero,overflow,s);
            EQU:t_equ(a,b,carry,zero,overflow,s);
            default:s={N{1'b1}};
        endcase
    end

//ADD or SUB instructions
task t_adder;
input      [N-1:0]      a        ;
input      [N-1:0]      b        ;
input                  add_sub  ;//0-add 1-sub

output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;

begin
    //减数计算
    reg [N-1:0] t_as;
    reg carry_f;
    t_as = { {N{add_sub}} ^ b } + //与add_sub异或，0为加法b不变，1为减法b取反
                            {{(N-1){1'b0}} , add_sub};//b反+1变补码
    {carry,s} =  a + t_as ;
    //carry = 1;//carry_f ^ add_sub;//carry的减法错位
    //溢出逻辑
    overflow = ((a[N-1]==t_as[N-1])&&(s[N-1]!=a[N-1]));
    //全0逻辑
    zero = ~(|s);
end
endtask

//NOT instructions
task t_not;
input      [N-1:0]      a        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    s=~a;
    carry=0;
    zero=0;
    overflow=0;
end
endtask

//AND instructions
task t_and;
input      [N-1:0]      a        ;
input      [N-1:0]      b        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    s=a&b;
    carry=0;
    zero=0;
    overflow=0;
end
endtask

//OR instructions
task t_or;
input      [N-1:0]      a        ;
input      [N-1:0]      b        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    s=a|b;
    carry=0;
    zero=0;
    overflow=0;
end
endtask

//XOR instructions
task t_xor;
input      [N-1:0]      a        ;
input      [N-1:0]      b        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    s=a^b;
    carry=0;
    zero=0;
    overflow=0;
end
endtask

//CMP (a less than b) instructions
//适用于有符号数,无符号数使用进位和加减符进行比较
task t_cmp;
input      [N-1:0]      a        ;
input      [N-1:0]      b        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    t_adder(.a(a), .b(b), .add_sub(1'b1), .carry(carry), 
            .zero(zero), .overflow(overflow), .s(s) );
    s = {N{s[N-1]^overflow}};
end
endtask

//EQU instructions
task t_equ;
input      [N-1:0]       a        ;
input      [N-1:0]       b        ;
output                 carry    ;//进位
output                 zero     ;//结果是否为0
output                 overflow ;//溢出
output     [N-1:0]     s        ;
begin
    t_adder(.a(a), .b(b), .add_sub(1'b1), .carry(carry), 
            .zero(zero), .overflow(overflow), .s(s) );
    s = {N{zero}};
end
endtask


endmodule





    // wire s_add_sub;//internal add sub flag signal
    // assign s_add_sub=(sel==SUB);//加0减1
    // adder #(N) i_add(a,b,s_add_sub,carry,zero,overflow,s_add);
    //logic
