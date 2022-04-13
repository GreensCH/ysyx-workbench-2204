#include "../generated/obj_dir/Vtop.h"
#include <verilated.h>
#include <iostream>  
#include <nvboard.h>



vluint64_t main_time = 0;       // Current simulation time
// This is a 64-bit integer to reduce wrap over issues and
// allow modulus.  This is in units of the timeprecision
// used in Verilog (or from --timescale-override)
double sc_time_stamp() {        // Called by $time in Verilog
    return main_time;           // converts to double, to match
                                // what SystemC does
}

Vtop *top = NULL; // Instantiation of model
VerilatedContext* contextp = NULL;
void nvboard_bind_all_pins(Vtop* top);

void init_verilator(int argc, char** argv){
    contextp  = new VerilatedContext;
    top = new Vtop{contextp};// Create model
    contextp->commandArgs(argc, argv);// Remember args
    contextp->commandArgs(argc, argv);
    contextp->traceEverOn(true);// Enable wave trace
}

// Declare auto bind function
// If auto_bind is used , below line should be uncomment
void nvboard_bind_all_pins(Vtop* top);

void init_nvboard(){
    // Below is arguments of nvboard_bind_pin()
    // pins , is_realtime_signal , is_output , width , phy_pins
    //nvboard_bind_pin( &top->f, BIND_RATE_SCR/*BIND_RATE_RT*/, BIND_DIR_OUT, 1, LD0);//bind pin to phy board
    
    // If auto_bind is used , below line should be uncomment
    nvboard_bind_all_pins(top);

    nvboard_init();//init nvboard
}

void quit_verilator(){
    //top->final();//to call systemverilog final process
    delete top;
    delete contextp;
}

////clk generator
// void single_cycle() {
//   top->clk = 0; top->eval();
//   top->clk = 1; top->eval();
// }

// rst=1 ; #10 rst=0;
// void reset(int n) {
//   top->rst = 1;
//   while (n -- > 0) single_cycle();// equal verilog code  '#n'
//   top->rst = 0;
// }


//选择开发板上的SW0和SW1作为控制端Y，
//SW2—SW9作为四个两位数据输入端X0–X3，
//将两位的输出端F接到发光二极管LEDR0和LEDR1上显示输出，
//完成设计，对自己的设计进行功能仿真，并下载到开发板上验证电路性能。

int main(int argc, char** argv, char** env) {

    init_verilator(argc,argv);
    init_nvboard();

    while (1/*!contextp->gotFinish()&&main_time<1000*/) { 
        top->eval();
        nvboard_update();//update nvboard
        main_time++; 
    }
    
    nvboard_quit(); //quit nvboard
    quit_verilator();

    return 0;
  }



