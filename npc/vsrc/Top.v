module Top(
  input   clock,
  input   reset,
  input   io_i_test,
  output  io_o_test
);
  assign io_o_test = io_i_test; // @[Top.scala 14:13]
endmodule
