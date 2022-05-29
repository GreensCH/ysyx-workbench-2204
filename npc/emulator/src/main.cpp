#include "include.h"



static const uint32_t img [] = {
  0b00000000000100000000000010010011,
  0b00000000001000000000000010010011,
  0b00000000001100000000000010010011,
  0b00000000000000000000000010010011,
  0x00100073,  // ebreak (used as nemu_trap)
  0x00000297,  // auipc t0,0
  0x0002b823,  // sd  zero,16(t0)
  0x0102b503,  // ld  a0,16(t0)
  0x00100073,  // ebreak (used as nemu_trap)
  0xdeadbeef,  // some data
};

static void restart() {
  reset(1);
}

void init_isa() {
  memcpy(guest_to_host(RESET_VECTOR), img, sizeof(img));

}

void init_monitor(int, char *[]);

int main(int argc, char *argv[], char** env) {

    init_monitor(argc, argv);
    
    sim_init(argc,argv);

    reset(1);
    printf("start npc\n");
    while (!contextp->gotFinish() && sc_time_stamp()<1000){ 
      step_and_dump_wave();
      // printf("@vlt:finish=%d\n",contextp->gotFinish());
      // printf("pc: 0x%lx  inst: 0x%lx\n",(word_t)top->io_pc , (word_t)top->io_inst);
      dump_gpr();
    }
    step_and_dump_wave();
    printf( "quiting verilator\n");
    sim_exit();
    
    
    return 0;
}


