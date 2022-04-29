#include "Vxor_gate.h"
#include <verilated.h>
#include <iostream>  
#include <nvboard.h>

Vxor_gate *top; // Instantiation of model


vluint64_t main_time = 0;       // Current simulation time
// This is a 64-bit integer to reduce wrap over issues and
// allow modulus.  This is in units of the timeprecision
// used in Verilog (or from --timescale-override)
double sc_time_stamp() {        // Called by $time in Verilog
    return main_time;           // converts to double, to match
                                // what SystemC does
}

int main(int argc, char** argv, char** env) {
    
    nvboard_bind_pin(&top->a, true , false, 1, 'a');//bind pins in nvboard
    nvboard_bind_pin(&top->a, true , false, 1, 'b');//bind pins in nvboard
    nvboard_bind_pin(&top->a, true , true, 1, 'f');//bind pins in nvboard
    //pins , is_realtime_signal , is_output , width , pins_id

    nvboard_init();//init nvboard


    Verilated::commandArgs(argc, argv);   // Remember args
    VerilatedContext* contextp = new VerilatedContext;
    top = new Vxor_gate{contextp};// Create model

    contextp->commandArgs(argc, argv);
    contextp->traceEverOn(true);// Enable wave trace
    while (!contextp->gotFinish()&&main_time<1000) { 
        int a = rand() & 1;
        int b = rand() & 1;
        top->a = a;
        top->b = b;
        top->eval();
        printf("a = %d, b = %d, f = %d\n", a, b, top->f);
        assert(top->f == a ^ b);
        main_time++; 

        nvboard_update();//update nvboard
    }
    
    nvboard_quit(); //quit nvboard

    delete top;
    delete contextp;

    return 0;
  }

