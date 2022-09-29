
module ysyx_040978_booth_pmgen
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

