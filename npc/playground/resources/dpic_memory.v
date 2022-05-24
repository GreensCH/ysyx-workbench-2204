import "DPI-C" context function longint pmem_read(input longint addr, input int len);
//import "DPI-C" context function void pmem_write(input longint addr, input longint wdata, input byte wmask);


module dpic_memory (
    input   [63 : 0]    raddr,
    output  [63 : 0]    rdata,
    input   [63 : 0]    waddr,
    input   [63 : 0]    wdata,
    input   [7  : 0]    wmask
);

  assign rdata = pmem_read(raddr, 8);
//  always @(*) begin
//    pmem_write(waddr, wdata, wmask);
//  end

endmodule


