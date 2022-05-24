

import "DPI-C" function void pmem_read(input longint raddr, output longint rdata);
import "DPI-C" function void pmem_write(input longint waddr, input longint wdata, input byte wmask);


module dpic_ram (
    input   [63 : 0]    raddr,
    output  [63 : 0]    rdata,
    input   [63 : 0]    waddr,
    input   [63 : 0]    wdata,
    input   [7  : 0]    wmask
);

  always @(*) begin
    pmem_read(raddr, rdata);
    pmem_write(waddr, wdata, wmask);
  end

endmodule


