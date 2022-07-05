#include "include.h"

#include "axi4.hpp"
#include "axi4_mem.hpp"
#include "axi4_xbar.hpp"
#include "mmio_mem.hpp"
#include "uartlite.hpp"

#include <iostream>
#include <termios.h>
#include <unistd.h>
#include <thread>


void connect_wire(axi4_ptr <31,64,4> &mmio_ptr, axi4_ptr <32,64,4> &mem_ptr, VExampleRocketSystem *top) {
    // connect
    // mmio
    // aw   
    mmio_ptr.awaddr     = &(top->mmio_axi4_0_aw_bits_addr);
    mmio_ptr.awburst    = &(top->mmio_axi4_0_aw_bits_burst);
    mmio_ptr.awid       = &(top->mmio_axi4_0_aw_bits_id);
    mmio_ptr.awlen      = &(top->mmio_axi4_0_aw_bits_len);
    mmio_ptr.awready    = &(top->mmio_axi4_0_aw_ready);
    mmio_ptr.awsize     = &(top->mmio_axi4_0_aw_bits_size);
    mmio_ptr.awvalid    = &(top->mmio_axi4_0_aw_valid);
    // w
    mmio_ptr.wdata      = &(top->mmio_axi4_0_w_bits_data);
    mmio_ptr.wlast      = &(top->mmio_axi4_0_w_bits_last);
    mmio_ptr.wready     = &(top->mmio_axi4_0_w_ready);
    mmio_ptr.wstrb      = &(top->mmio_axi4_0_w_bits_strb);
    mmio_ptr.wvalid     = &(top->mmio_axi4_0_w_valid);
    // b
    mmio_ptr.bid        = &(top->mmio_axi4_0_b_bits_id);
    mmio_ptr.bready     = &(top->mmio_axi4_0_b_ready);
    mmio_ptr.bresp      = &(top->mmio_axi4_0_b_bits_resp);
    mmio_ptr.bvalid     = &(top->mmio_axi4_0_b_valid);
    // ar
    mmio_ptr.araddr     = &(top->mmio_axi4_0_ar_bits_addr);
    mmio_ptr.arburst    = &(top->mmio_axi4_0_ar_bits_burst);
    mmio_ptr.arid       = &(top->mmio_axi4_0_ar_bits_id);
    mmio_ptr.arlen      = &(top->mmio_axi4_0_ar_bits_len);
    mmio_ptr.arready    = &(top->mmio_axi4_0_ar_ready);
    mmio_ptr.arsize     = &(top->mmio_axi4_0_ar_bits_size);
    mmio_ptr.arvalid    = &(top->mmio_axi4_0_ar_valid);
    // r
    mmio_ptr.rdata      = &(top->mmio_axi4_0_r_bits_data);
    mmio_ptr.rid        = &(top->mmio_axi4_0_r_bits_id);
    mmio_ptr.rlast      = &(top->mmio_axi4_0_r_bits_last);
    mmio_ptr.rready     = &(top->mmio_axi4_0_r_ready);
    mmio_ptr.rresp      = &(top->mmio_axi4_0_r_bits_resp);
    mmio_ptr.rvalid     = &(top->mmio_axi4_0_r_valid);
    // mem
    // aw
    mem_ptr.awaddr  = &(top->mem_axi4_0_aw_bits_addr);
    mem_ptr.awburst = &(top->mem_axi4_0_aw_bits_burst);
    mem_ptr.awid    = &(top->mem_axi4_0_aw_bits_id);
    mem_ptr.awlen   = &(top->mem_axi4_0_aw_bits_len);
    mem_ptr.awready = &(top->mem_axi4_0_aw_ready);
    mem_ptr.awsize  = &(top->mem_axi4_0_aw_bits_size);
    mem_ptr.awvalid = &(top->mem_axi4_0_aw_valid);
    // w
    mem_ptr.wdata   = &(top->mem_axi4_0_w_bits_data);
    mem_ptr.wlast   = &(top->mem_axi4_0_w_bits_last);
    mem_ptr.wready  = &(top->mem_axi4_0_w_ready);
    mem_ptr.wstrb   = &(top->mem_axi4_0_w_bits_strb);
    mem_ptr.wvalid  = &(top->mem_axi4_0_w_valid);
    // b
    mem_ptr.bid     = &(top->mem_axi4_0_b_bits_id);
    mem_ptr.bready  = &(top->mem_axi4_0_b_ready);
    mem_ptr.bresp   = &(top->mem_axi4_0_b_bits_resp);
    mem_ptr.bvalid  = &(top->mem_axi4_0_b_valid);
    // ar
    mem_ptr.araddr  = &(top->mem_axi4_0_ar_bits_addr);
    mem_ptr.arburst = &(top->mem_axi4_0_ar_bits_burst);
    mem_ptr.arid    = &(top->mem_axi4_0_ar_bits_id);
    mem_ptr.arlen   = &(top->mem_axi4_0_ar_bits_len);
    mem_ptr.arready = &(top->mem_axi4_0_ar_ready);
    mem_ptr.arsize  = &(top->mem_axi4_0_ar_bits_size);
    mem_ptr.arvalid = &(top->mem_axi4_0_ar_valid);
    // r
    mem_ptr.rdata   = &(top->mem_axi4_0_r_bits_data);
    mem_ptr.rid     = &(top->mem_axi4_0_r_bits_id);
    mem_ptr.rlast   = &(top->mem_axi4_0_r_bits_last);
    mem_ptr.rready  = &(top->mem_axi4_0_r_ready);
    mem_ptr.rresp   = &(top->mem_axi4_0_r_bits_resp);
    mem_ptr.rvalid  = &(top->mem_axi4_0_r_valid);
}
