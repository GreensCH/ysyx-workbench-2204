#include "include.h"

#include "axi4.hpp"
#include "axi4_mem.hpp"

#include <iostream>
#include <termios.h>
#include <unistd.h>
#include <thread>


void connect_wire(axi4_ptr <32,64,4> &mem_ptr, VTop *top) {
    //aw
    mem_ptr.awaddr  = &(top->io_master_awaddr);
    mem_ptr.awburst = &(top->io_master_awburst);
    mem_ptr.awid    = &(top->io_master_awid);
    mem_ptr.awlen   = &(top->io_master_awlen);
    mem_ptr.awready = &(top->io_master_awready);
    mem_ptr.awsize  = &(top->io_master_awsize);
    mem_ptr.awvalid = &(top->io_master_awvalid);
    // w
    mem_ptr.wdata   = &(top->io_master_wdata);
    mem_ptr.wlast   = &(top->io_master_wlast);
    mem_ptr.wready  = &(top->io_master_wready);
    mem_ptr.wstrb   = &(top->io_master_wstrb);
    mem_ptr.wvalid  = &(top->io_master_wvalid);
    // b
    mem_ptr.bid     = &(top->io_master_bid);
    mem_ptr.bready  = &(top->io_master_bready);
    mem_ptr.bresp   = &(top->io_master_bresp);
    mem_ptr.bvalid  = &(top->io_master_bvalid);
    // ar
    mem_ptr.araddr  = &(top->io_master_araddr);
    mem_ptr.arburst = &(top->io_master_arburst);
    mem_ptr.arid    = &(top->io_master_arid);
    mem_ptr.arlen   = &(top->io_master_arlen);
    mem_ptr.arready = &(top->io_master_arready);
    mem_ptr.arsize  = &(top->io_master_arsize);
    mem_ptr.arvalid = &(top->io_master_arvalid);
    // r
    mem_ptr.rdata   = &(top->io_master_rdata);
    mem_ptr.rid     = &(top->io_master_rid);
    mem_ptr.rlast   = &(top->io_master_rlast);
    mem_ptr.rready  = &(top->io_master_rready);
    mem_ptr.rresp   = &(top->io_master_rresp);
    mem_ptr.rvalid  = &(top->io_master_rvalid);
    
}


static axi4_ptr <32,64,4> mem_ptr;
static axi4     <32,64,4> mem_sigs;
static axi4_ref <32,64,4> mem_sigs_ref(mem_sigs);
static axi4_mem <32,64,4> mem;

void sim_soc_init(VTop *top) {
    connect_wire(mem_ptr,top);
}

// void sim_soc_init(VTop *top) {
//     connect_wire(mmio_ptr,mem_ptr,top);
//     assert(mmio_ptr.check());
//     assert(mem_ptr.check());
//     mmio_ref = axi4_ref <31,64,4> (mmio_ptr);
//     mmio_sigs_ref = axi4_ref <31,64,4> (mmio_sigs);
//     uart_input_thread = std::thread (uart_input,std::ref(uart));
//     assert(mmio.add_dev(0x60100000,1024*1024,&uart));
//     mem.load_binary(img_file,0x80000000);
// }
extern long int total_step;
unsigned long ticks = 0;
long max_trace_ticks = 1000;
unsigned long uart_tx_bytes = 0;
void sim_soc_dump(VTop *top) {
    static axi4_ref <32,64,4> mem_ref(mem_ptr);
    top->clock = 0;
    top->eval();contextp->timeInc(1);
    IFDEF(CONFIG_WAVE, tfp->dump(contextp->time()););
    ticks ++;
    if (ticks == 9) top->reset = 0;
    top->clock = 1;
    /* posedge */
    mem_sigs.update_input(mem_ref);
    top->eval();contextp->timeInc(1);
    // if(total_step>3200000)
    IFDEF(CONFIG_WAVE, tfp->dump(contextp->time()););
    
    ticks ++;
    if (!top->reset) {
        mem.beat(mem_sigs_ref);   
    }  
    mem_sigs.update_output(mem_ref);
    // top->interrupts = uart.irq();
    top->clock = 0;
  return ;
}


// void sim_soc_mem_read(word_t addr){
//     char temp[16];
//     memset(temp, 0, 16);
//     mem.read((off_t)addr, (size_t)16, (uint8_t *)temp);
//     for(int i = 0; i < 8; i++){
//         unsigned char a = (unsigned char)(temp[7-i]);
//         printf("%02x", a);
//     }
//     printf("\n");
// }

