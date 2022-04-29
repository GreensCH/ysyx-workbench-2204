#include "include.h"
#include <bitset>

using namespace std;

void step_and_dump_wave(){
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
  cout
      <<" Din="  <<bitset<8>(top->Din)     //Din
      <<" Dout=" <<bitset<8>(top->Dout)
      <<" Shamt="<<bitset<3>(top->Shamt)//Shamt
      <<" A_L="  <<bitset<1>(top->A_L)//A_L
      <<" L_R="  <<bitset<1>(top->L_R)//L_R
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





