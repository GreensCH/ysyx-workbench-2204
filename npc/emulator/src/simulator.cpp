#include "include.h"

VerilatedContext* contextp = NULL;
VerilatedVcdC* tfp = NULL;
VTop* top = NULL;

void sim_soc_dump(VTop *top);

vluint64_t main_time = 0;       // Current simulation time
// This is a 64-bit integer to reduce wrap over issues and
// allow modulus.  This is in units of the timeprecision
// used in Verilog (or from --timescale-override)
double sc_time_stamp() {        // Called by $time in Verilog
    return main_time;           // converts to double, to match
                                // what SystemC does
}

void step_and_dump_wave(){


  #ifdef CONFIG_SOC_SIMULATOR
  sim_soc_dump(top);
  main_time += 1;
  #else
  top->clock = 0;
  top->eval(); contextp->timeInc(1);tfp->dump(contextp->time());
  top->clock = 1;
  top->eval(); contextp->timeInc(1);tfp->dump(contextp->time());
  main_time += 1;
  #endif
}

void reset(int n){
  top->eval(); contextp->timeInc(1);tfp->dump(contextp->time());
  top->reset = 1; top->clock = 1; 
  top->eval(); contextp->timeInc(1);tfp->dump(contextp->time());

  top->reset = 0;

}

void sim_exit(){
  if(tfp != NULL) tfp->close();
  if(top != NULL) delete top;
  if(top != NULL) delete contextp;
}

void sim_init(int argc, char** argv){
    contextp  = new VerilatedContext;
    top = new VTop{contextp};// Create model
    tfp = new VerilatedVcdC;
    
    contextp->commandArgs(argc, argv);// Remember args
    IFDEF(CONFIG_WAVE, contextp->traceEverOn(true)); // Enable wave trace
    IFDEF(CONFIG_WAVE, top->trace(tfp, 0));//trace 0 level of hierarchy
    IFDEF(CONFIG_WAVE, tfp->set_time_resolution("ns"));//时间分辨率)
    IFDEF(CONFIG_WAVE, tfp->open("npc_dump.vcd"));
      /* Load the image to axi memory.*/
    void sim_soc_init(VTop *top);
    sim_soc_init(top);
    top->reset = 1;
}

/*void reset(int n){

  step_and_dump_wave();
  top->reset = 1;
  while(n>0){
    step_and_dump_wave();
    n--;
  }
  top->reset = 0;
}
*/