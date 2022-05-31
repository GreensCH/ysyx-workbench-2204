import "DPI-C" function void ebreak();

module ebreak (
    input   valid
);
    always @(*)
        if(valid) ebreak();
    // always @(*)
    //     if(valid) $finish();
  
endmodule