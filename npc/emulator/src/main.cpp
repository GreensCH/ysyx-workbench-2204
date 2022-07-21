#include "include.h"

//#include "/home/chang/programs/ysyx-workbench/npc/emulator/src/soc/uartlite.hpp"
//#include <thread>


static void restart() {
  reset(1);
}


void init_monitor(int, char *[]);
void am_init_monitor();
void engine_start();
int is_exit_status_bad();

#define EMULATOR

int main(int argc, char *argv[], char** env) {

#ifdef EMULATOR 
  /* ATTENTION! */
  /* init_monitor must beyond the sim_init */
  #ifdef CONFIG_TARGET_AM
  am_init_monitor();
  #else
  init_monitor(argc, argv);
  #endif

  sim_init(argc,argv);
  reset(1);
  dump_gpr();

  printf("start npc\n");
  engine_start();
  step_and_dump_wave();
  printf( "quiting verilator\n");
  sim_exit();
    
  return is_exit_status_bad();

#else

  sim_init(argc,argv);
  reset(1);
  dump_gpr();
  printf("start npc\n");
  while (!contextp->gotFinish() && sc_time_stamp()<1000){ 
    dump_gpr();
    step_and_dump_wave();
    printf("@vlt:finish=%d\n",contextp->gotFinish());
    printf("pc: 0x%lx  inst: 0x%lx\n",(word_t)top->io_pc , (word_t)top->io_inst);
  }
  step_and_dump_wave();
  printf( "quiting verilator\n");
  sim_exit();
    
  return 0;
  
#endif

}


