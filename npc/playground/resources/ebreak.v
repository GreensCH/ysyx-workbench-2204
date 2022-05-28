module ebreak (
    input   valid
);
    always @(*)
        if(valid) $finish();
  
endmodule