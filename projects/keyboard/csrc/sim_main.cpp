#include "include.h"
#include <bitset>

using namespace std;

void step_and_dump_wave(){
  top->clk = 0; top->eval();
  top->clk = 1; top->eval();
  contextp->timeInc(1);
  tfp->dump(contextp->time());
  tfp->set_time_resolution("us");
}



void sim_init(){
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  top = new Vtop{contextp};
  contextp->traceEverOn(true);
  top->trace(tfp, 0);
  tfp->open("vlt_dump.vcd");
  top->clrn=0;top->eval();
  top->clrn=1;
}

void sim_exit(){
  step_and_dump_wave();
  tfp->close();
}

void monitor(){
  ;
}

// void stimulator(){
//   top->Din=0b0001;

// }


int sim_main() {
  monitor();
  step_and_dump_wave();
  return 0;
}





