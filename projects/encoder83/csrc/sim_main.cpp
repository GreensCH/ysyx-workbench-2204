#include "include.h"


void step_and_dump_wave(){
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
  top->trace(tfp, 0);//trace 0 level of hierarchy
  //tfp->dumpvars(1,"t");//trace 1 level under "t"
  // tfp->set_time_unit("ps");//时间单位，似乎没用
  tfp->set_time_resolution("ns");//时间分辨率
  tfp->open("vlt_dump.vcd");
}

void sim_exit(){
  step_and_dump_wave();
  tfp->close();
}

int sim_main() {
  step_and_dump_wave();
  std::cout<<"contextp->time="<<contextp->time()<<std::endl;
  // top->Y=0b00;  top->X0=0b1110;  step_and_dump_wave();
  //               top->X0=0b0001;  step_and_dump_wave();
  // top->Y=0b01;  top->X0=0b1110;  step_and_dump_wave();
  //               top->X0=0b0010;  step_and_dump_wave();
  // top->Y=0b10;  top->X0=0b1010;  step_and_dump_wave();
  //               top->X0=0b0100;  step_and_dump_wave();
  // top->Y=0b11;  top->X0=0b0111;  step_and_dump_wave();
  //               top->X0=0b1001;  step_and_dump_wave();
  return 0;
}




