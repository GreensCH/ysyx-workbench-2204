`timescale 1ps/1ps

// 用选择器模板实现一个2位4选1的选择器，如下图所示，
// 选择器有5个2位输入端，分别为X0, X1, X2, X3和Y，
// 输出端为F；X0, X1, X2, X3是四个2位的输入变量。
// 输出F端受控制端Y的控制，选择其中的一个X输出，
// 当Y = 00时，输出端输出X0，即F = X0；
// 当Y = 01时，输出端输出X1，即F = X1；以此类推

module mux412(
    input   [1:0] X0,//input
    input   [1:0] X1,
    input   [1:0] X2,
    input   [1:0] X3,
    input   [1:0] Y,//selcet
    output  [1:0] F//output
);

    MuxKeyWithDefault #( .NR_KEY(4), .KEY_LEN(2), .DATA_LEN(2)) 
    i0 
    ( 
        F,
        Y, 
        2'b11, //default output value
        {2'b00, X0,2'b01, X1,2'b10, X2,2'b11, X3 }
    );
endmodule