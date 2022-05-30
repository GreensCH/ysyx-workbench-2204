#include "include.h"



static const uint32_t img [] = {
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
void am_init_monitor();
void engine_start();
int is_exit_status_bad();


int main(int argc, char *argv[], char** env) {

#ifdef CONFIG_TARGET_AM
  am_init_monitor();
#else
  init_monitor(argc, argv);
#endif
  sim_init(argc,argv);

  reset(1);
  printf("start npc\n");
  // engine_start();
  while (!contextp->gotFinish() && sc_time_stamp()<1000){ 
    dump_gpr();
    step_and_dump_wave();
    // printf("@vlt:finish=%d\n",contextp->gotFinish());
    // printf("pc: 0x%lx  inst: 0x%lx\n",(word_t)top->io_pc , (word_t)top->io_inst);
  }
  step_and_dump_wave();
  printf( "quiting verilator\n");
  sim_exit();
  
  
  return is_exit_status_bad();
}


