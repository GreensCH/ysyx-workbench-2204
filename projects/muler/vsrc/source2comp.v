`timescale 1ps/1ps    
// warning n should > 2
module source2comp
#(
    parameter N = 4
)
(
   input      [N-1:0]       a,
   output     [N-1:0]       a_comp   
);
 
    assign a_comp = a[N-1] ? 
            { a[N-1] , ~a[N-2:0] + 1'b1 } : a;
//逻辑不太复杂时，用位拼接，二选一
endmodule
