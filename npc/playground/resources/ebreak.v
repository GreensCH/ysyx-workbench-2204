import "DPI-C" function void ebreak();

module ebreak (
    input   ebreak_in
);

    always @(*) begin
        if(ebreak_in)
            ebreak();
    end
  
endmodule