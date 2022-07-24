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


void connect_wire(axi4_ptr <31,64,4> &mmio_ptr, axi4_ptr <32,64,4> &mem_ptr, VTop *top) {
    // connect
    // mmio
    // aw   
    mmio_ptr.awaddr     = &(top->io_mmio_axi4_aw_bits_addr);
    mmio_ptr.awburst    = &(top->io_mmio_axi4_aw_bits_burst);
    mmio_ptr.awid       = &(top->io_mmio_axi4_aw_bits_id);
    mmio_ptr.awlen      = &(top->io_mmio_axi4_aw_bits_len);
    mmio_ptr.awready    = &(top->io_mmio_axi4_aw_ready);
    mmio_ptr.awsize     = &(top->io_mmio_axi4_aw_bits_size);
    mmio_ptr.awvalid    = &(top->io_mmio_axi4_aw_valid);
    // w
    mmio_ptr.wdata      = &(top->io_mmio_axi4_w_bits_data);
    mmio_ptr.wlast      = &(top->io_mmio_axi4_w_bits_last);
    mmio_ptr.wready     = &(top->io_mmio_axi4_w_ready);
    mmio_ptr.wstrb      = &(top->io_mmio_axi4_w_bits_strb);
    mmio_ptr.wvalid     = &(top->io_mmio_axi4_w_valid);
    // b
    mmio_ptr.bid        = &(top->io_mmio_axi4_b_bits_id);
    mmio_ptr.bready     = &(top->io_mmio_axi4_b_ready);
    mmio_ptr.bresp      = &(top->io_mmio_axi4_b_bits_resp);
    mmio_ptr.bvalid     = &(top->io_mmio_axi4_b_valid);
    // ar
    mmio_ptr.araddr     = &(top->io_mmio_axi4_ar_bits_addr);
    mmio_ptr.arburst    = &(top->io_mmio_axi4_ar_bits_burst);
    mmio_ptr.arid       = &(top->io_mmio_axi4_ar_bits_id);
    mmio_ptr.arlen      = &(top->io_mmio_axi4_ar_bits_len);
    mmio_ptr.arready    = &(top->io_mmio_axi4_ar_ready);
    mmio_ptr.arsize     = &(top->io_mmio_axi4_ar_bits_size);
    mmio_ptr.arvalid    = &(top->io_mmio_axi4_ar_valid);
    // r
    mmio_ptr.rdata      = &(top->io_mmio_axi4_r_bits_data);
    mmio_ptr.rid        = &(top->io_mmio_axi4_r_bits_id);
    mmio_ptr.rlast      = &(top->io_mmio_axi4_r_bits_last);
    mmio_ptr.rready     = &(top->io_mmio_axi4_r_ready);
    mmio_ptr.rresp      = &(top->io_mmio_axi4_r_bits_resp);
    mmio_ptr.rvalid     = &(top->io_mmio_axi4_r_valid);
    // mem
    // aw
    mem_ptr.awaddr  = &(top->io_mem_axi4_aw_bits_addr);
    mem_ptr.awburst = &(top->io_mem_axi4_aw_bits_burst);
    mem_ptr.awid    = &(top->io_mem_axi4_aw_bits_id);
    mem_ptr.awlen   = &(top->io_mem_axi4_aw_bits_len);
    mem_ptr.awready = &(top->io_mem_axi4_aw_ready);
    mem_ptr.awsize  = &(top->io_mem_axi4_aw_bits_size);
    mem_ptr.awvalid = &(top->io_mem_axi4_aw_valid);
    // w
    mem_ptr.wdata   = &(top->io_mem_axi4_w_bits_data);
    mem_ptr.wlast   = &(top->io_mem_axi4_w_bits_last);
    mem_ptr.wready  = &(top->io_mem_axi4_w_ready);
    mem_ptr.wstrb   = &(top->io_mem_axi4_w_bits_strb);
    mem_ptr.wvalid  = &(top->io_mem_axi4_w_valid);
    // b
    mem_ptr.bid     = &(top->io_mem_axi4_b_bits_id);
    mem_ptr.bready  = &(top->io_mem_axi4_b_ready);
    mem_ptr.bresp   = &(top->io_mem_axi4_b_bits_resp);
    mem_ptr.bvalid  = &(top->io_mem_axi4_b_valid);
    // ar
    mem_ptr.araddr  = &(top->io_mem_axi4_ar_bits_addr);
    mem_ptr.arburst = &(top->io_mem_axi4_ar_bits_burst);
    mem_ptr.arid    = &(top->io_mem_axi4_ar_bits_id);
    mem_ptr.arlen   = &(top->io_mem_axi4_ar_bits_len);
    mem_ptr.arready = &(top->io_mem_axi4_ar_ready);
    mem_ptr.arsize  = &(top->io_mem_axi4_ar_bits_size);
    mem_ptr.arvalid = &(top->io_mem_axi4_ar_valid);
    // r
    mem_ptr.rdata   = &(top->io_mem_axi4_r_bits_data);
    mem_ptr.rid     = &(top->io_mem_axi4_r_bits_id);
    mem_ptr.rlast   = &(top->io_mem_axi4_r_bits_last);
    mem_ptr.rready  = &(top->io_mem_axi4_r_ready);
    mem_ptr.rresp   = &(top->io_mem_axi4_r_bits_resp);
    mem_ptr.rvalid  = &(top->io_mem_axi4_r_valid);
}

void uart_input(uartlite &uart) {
    termios tmp;
    tcgetattr(STDIN_FILENO,&tmp);
    tmp.c_lflag &=(~ICANON & ~ECHO);
    tcsetattr(STDIN_FILENO,TCSANOW,&tmp);
    while (1) {
        char c = getchar();
        if (c == 10) c = 13; // convert lf to cr
        uart.putc(c);
    }
}

static axi4_ptr <31,64,4> mmio_ptr;
static axi4     <31,64,4> mmio_sigs;
static axi4_xbar<31,64,4> mmio;
static axi4_ptr <32,64,4> mem_ptr;
static axi4     <32,64,4> mem_sigs;
static axi4_ref <32,64,4> mem_sigs_ref(mem_sigs);
static axi4_mem <32,64,4> mem(4096l*1024*1024);
uartlite           uart;
#define SERIAL_PORT 0x10000000
void sim_soc_init(VTop *top) {
    connect_wire(mmio_ptr,mem_ptr,top);
    assert(mmio_ptr.check());
    assert(mem_ptr.check());
    
    IFDEF(CONFIG_MMIO_THREAD, std::thread uart_input_thread(uart_input,std::ref(uart));)
    IFDEF(CONFIG_MMIO_THREAD, uart_input_thread.detach();)
    
    assert(mmio.add_dev(SERIAL_PORT,1024*1024,&uart));
    mem.load_binary(img_file,0x80000000);
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

unsigned long ticks = 0;
long max_trace_ticks = 1000;
unsigned long uart_tx_bytes = 0;
void sim_soc_dump(VTop *top) {
    static axi4_ref <31,64,4> mmio_ref(mmio_ptr);
    static axi4_ref <31,64,4> mmio_sigs_ref(mmio_sigs);
    static axi4_ref <32,64,4> mem_ref(mem_ptr);
    top->clock = 0;
    top->eval();contextp->timeInc(1);tfp->dump(contextp->time());
    ticks ++;
    if (ticks == 9) top->reset = 0;
    top->clock = 1;
    /* posedge */
    
    mmio_sigs.update_input(mmio_ref);
    
    mem_sigs.update_input(mem_ref);
    top->eval();contextp->timeInc(1);tfp->dump(contextp->time());
    ticks ++;
    if (!top->reset) {
        mem.beat(mem_sigs_ref);
        
        mmio.beat(mmio_sigs_ref);
        while (uart.exist_tx()) {
            char c = uart.getc();
            printf("%c",c);
            fflush(stdout);
        }
        
    }
    
    mmio_sigs.update_output(mmio_ref); 
    
    mem_sigs.update_output(mem_ref);
    // top->interrupts = uart.irq();
    top->clock = 0;
  return ;
}


void sim_soc_mem_read(word_t addr){
    char temp[16];
    memset(temp, 0, 16);
    mem.read((off_t)addr, (size_t)16, (uint8_t *)temp);
    for(int i = 0; i < 8; i++){
        unsigned char a = (unsigned char)(temp[7-i]);
        printf("%02x", a);
    }
    printf("\n");
}

