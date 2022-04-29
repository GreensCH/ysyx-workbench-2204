`timescale 1ps/1ps
// 2*8=16
// Is the PLI supported?
// Only somewhat. More specifically, the common PLI-ish calls $display, $finish, $stop, $time, $write are converted to C++ equivalents. You can also use the “import DPI” SystemVerilog feature to call C code (see the chapter above). There is also limited VPI access to public signals.
// If you want something more complex, since Verilator emits standard C++ code, you can simply write your own C++ routines that can access and modify signal values without needing any PLI interface code, and call it with $c(“{any_c++_statement}”).
// See the Connecting to Verilated Models section.
module alu_tb
#(
    parameter N = 4
)
(

   input                   carry  ,//进位
   input                   zero   ,//结果是否为0
   input                   overflow,//溢出
   input     [N-1:0]       s      ,//结果

   output   reg  [N-1:0]       a_o    ,
   output   reg  [N-1:0]       b_o    ,
   output     [2:0]         sel_o  
);

/* verilator lint_off UNOPTFLAT */
/* verilator lint_off INFINITELOOP */
/* verilator lint_off WIDTH */
   wire clk;
   assign #5 clk=~clk;


   always@(posedge clk)begin
      a_o=4'b0101;b_o=4'b0111;
      $display("\$sel: %b, a=%b(%d), b=%b(%d), res=%b(%d),carry=%b zero=%b, overf=%b",sel_o,a_o,a_o,b_o,b_o,s,s,carry,zero,overflow); 
   end

   
// //   initial begin
// //       a_o=4'b0101;b_o=4'b0111;
// //       a_o=4'b1111;b_o=4'b1111;
// //    end
//    initial begin
//       a_o=4'b0101;b_o=4'b0111;
//       $display("\$sel: %b, a=%b(%d), b=%b(%d), res=%b(%d),carry=%b zero=%b, overf=%b",sel_o,a_o,a_o,b_o,b_o,s,s,carry,zero,overflow); 

//       // for(integer m=0;m<10;m++)begin
//       //       $display("\$%4d sel: %b, a=%b(%d), b=%b(%d), res=%b(%d),carry=%b zero=%b, overf=%b"
//       //       ,m,sel_o,a_o,a_o,b_o,b_o,s,s,carry,zero,overflow); 
//       // end
//    end

/* verilator lint_on WIDTH */
/* verilator lint_on INFINITELOOP */
/* verilator lint_on UNOPTFLAT */
endmodule
