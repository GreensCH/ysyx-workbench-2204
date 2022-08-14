`timescale 1ps/1ps

`define XLEN 64
module top(
   input                   clock       ,
   input                   reset       ,
   input                   src_valid   ,
   input                   flush       ,
   input                   dutw        ,
   input     [1 : 0]       dut_signed  ,
   input     [`XLEN-1 : 0] src1        ,
   input     [`XLEN-1 : 0] src2        ,
   output                  dut_ready   ,
   output                  dut_valid   
);

   muler dut(
      .clock       (clock       ),
      .reset       (reset       ),
      .mul_valid   (src_valid   ),
      .flush       (flush       ),
      .mulw        (dutw        ),
      .mul_signed  (dut_signed  ),
      .multiplicand(src1),
      .multiplier  (src2),
      .mul_ready   (dut_ready   ),
      .out_valid   (dut_valid   ),
      .result_hi   (),
      .result_lo   ()
   );


endmodule

