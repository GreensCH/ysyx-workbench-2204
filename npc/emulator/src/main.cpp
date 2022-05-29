#include "include.h"



static const uint32_t img [] = {
  0x800002b7,  // lui t0,0x80000 0
  0x0002a023,  // sw  zero,0(t0) 
  0x0002a503,  // lw  a0,0(t0)   
  0x00100073,  // ebreak 
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
    while (!contextp->gotFinish() && sc_time_stamp()<10000){ 
      step_and_dump_wave();
      // printf("@vlt:finish=%d\n",contextp->gotFinish());
      // printf("pc: 0x%lx  inst: 0x%lx\n",(word_t)top->io_pc , (word_t)top->io_inst);
    }
    step_and_dump_wave();
    printf( "quiting verilator\n");
    sim_exit();
    
    
    return 0;
}


