#include "include.h"
#include <bitset>

using namespace std;



void reset(int n){
  step_and_dump_wave();
  top->rst = 1;
  top->rstn = 0;
  while(n>0){
    step_and_dump_wave();
    n--;
  }
  top->rst = 0;
  top->rstn = 1;
}

void step_and_dump_wave(){
  top->clk = 0; top->eval();
  // sleep(0.1);
  top->clk = 1; top->eval();
  // sleep(0.1);
  contextp->timeInc(1);
  // tfp->dump(contextp->time());
}


void sim_init(){
  contextp = new VerilatedContext;
  top = new Vtop{contextp};
  contextp->traceEverOn(false);
  // tfp = new VerilatedVcdC;
  // top->trace(tfp, 0);
  // tfp->set_time_resolution("us");
  // tfp->open("vlt_dump.vcd");
  reset(10);
}

void sim_exit(){
  step_and_dump_wave();
  // tfp->close();
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





