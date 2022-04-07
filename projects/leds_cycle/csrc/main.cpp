#include "../generated/obj_dir/Vtop.h"
#include <verilated.h>
#include <iostream>  
#include <nvboard.h>

//declare auto bind function
void nvboard_bind_all_pins(Vtop* top);

vluint64_t main_time = 0;       // Current simulation time
// This is a 64-bit integer to reduce wrap over issues and
// allow modulus.  This is in units of the timeprecision
// used in Verilog (or from --timescale-override)
double sc_time_stamp() {        // Called by $time in Verilog
    return main_time;           // converts to double, to match
                                // what SystemC does
}

Vtop *top; // Instantiation of model
VerilatedContext* contextp;
void nvboard_bind_all_pins(Vtop* top);

void init_verilator(int argc, char** argv){
    contextp  = new VerilatedContext;
    top = new Vtop{contextp};// Create model
    contextp->commandArgs(argc, argv);// Remember args
    contextp->commandArgs(argc, argv);
    contextp->traceEverOn(true);// Enable wave trace
}

void init_nvboard(){
    // Below is arguments of nvboard_bind_pin()
    // pins , is_realtime_signal , is_output , width , phy_pins
   
    // if auto_bind is used , below line should be uncomment
    nvboard_bind_all_pins(top);

    nvboard_init();//init nvboard
}

void quit_verilator(){
    delete top;
    delete contextp;
}


void initialization(){
    ;
}

void single_cycle() {
  top->clk = 0; top->eval();
  top->clk = 1; top->eval();
}

void reset(int n) {
  top->rst = 1;
  while (n -- > 0) single_cycle();// equal verilog code  '#n'
  top->rst = 0;
}


int main(int argc, char** argv, char** env) {

    init_verilator(argc,argv);
    init_nvboard();
    reset(10);  // 复位10个周期
    while (1/*!contextp->gotFinish()&&main_time<1000*/) { 
        single_cycle();
        top->eval();
        main_time++; 
        nvboard_update();//update nvboard
    }
    printf("Testbench: Verilator End");
    nvboard_quit(); //quit nvboard
    quit_verilator();

    return 0;
  }

