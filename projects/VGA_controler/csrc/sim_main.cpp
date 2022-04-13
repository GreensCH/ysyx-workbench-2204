#include "include.h"
#include <bitset>

using namespace std;



void reset(int n){
  step_and_dump_wave();
  top->rst = 1;
  while(n>0){
    step_and_dump_wave();
    n--;
  }
  top->rst = 0;
}

void step_and_dump_wave(){
  top->clk = 0; top->eval();
  top->clk = 1; top->eval();
  contextp->timeInc(1);
  // tfp->dump(contextp->time());
}


void sim_init(){
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  top = new Vtop{contextp};
  // contextp->traceEverOn(true);
  // top->trace(tfp, 0);
  // tfp->set_time_resolution("us");
  // tfp->open("vlt_dump.vcd");
  reset(10);
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





