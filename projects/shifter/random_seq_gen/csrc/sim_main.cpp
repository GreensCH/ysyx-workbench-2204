#include "include.h"
#include <bitset>

using namespace std;

// void single_cycle() {
//   top->Clk = 0; top->eval();
//   top->Clk = 1; top->eval();
// }

void step_and_dump_wave(){
  top->Clk = 0; top->eval();
  top->Clk = 1; top->eval();
  top->eval();
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
}

void sim_exit(){
  step_and_dump_wave();
  tfp->close();
}


void monitor(){
  cout
      <<" Q=" <<bitset<8>(top->Q)
      <<endl;
}

// void stimulator(){
//   top->Din=0b0001;

// }


int sim_main() {
  monitor();
  step_and_dump_wave();
  return 0;
}





