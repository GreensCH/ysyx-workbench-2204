import "DPI-C" function longint pmem_read(input longint raddr, input int len);
import "DPI-C" function void pmem_write(input longint waddr, input longint wdata, input byte wmask);


module dpic_memory (
    input   [63 : 0]    raddr,
    output  [63 : 0]    rdata,
    input   [63 : 0]    waddr,
    input   [63 : 0]    wdata,
    input   [7  : 0]    wmask
);

  assign rdata = pmem_read(raddr, 8);
  always @(*) begin
    pmem_write(waddr, wdata, wmask);
  end

endmodule


