import "DPI-C" function longint pmem_read(input longint addr, input int len);
import "DPI-C" context function void pmem_write(input longint addr, input longint wdata, input byte wmask);


module dpic_memory (
    input               rd_en,
    input   [63 : 0]    rd_addr,
    output  [63 : 0]    rd_data,
    input               we_en,
    input   [63 : 0]    we_addr,
    input   [63 : 0]    we_data,
    input   [7  : 0]    we_mask
);

  assign rd_data = rd_en ? 'h0 : pmem_read(rd_addr, 8);
  // always @(*) begin
  //   if(we_en) pmem_write(we_addr, we_data, we_mask);
  // end

endmodule


