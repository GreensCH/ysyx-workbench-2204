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
}

void sim_init(){
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  top = new Vtop{contextp};
}

void sim_exit(){
  step_and_dump_wave();
  tfp->close();
}


#include <string.h>
#define BIT_WIDTH 4
void monitor(){
  if(top->Dout!=0b11111111){
    cout
        <<" In="  <<bitset<1>(top->Din)     //Din
        <<" Dout=" <<bitset<8>(top->Dout)
        <<" L_R="  <<bitset<1>(top->L_R)//L_R
        <<endl;
  }

}

// void stimulator(){
//   top->Din=0b0001;

// }


int sim_main() {
  monitor();
  step_and_dump_wave();
  return 0;
}





