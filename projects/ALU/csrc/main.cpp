#include "include.h"


VerilatedContext* contextp = NULL;
VerilatedVcdC* tfp = NULL;
Vtop* top = NULL;

vluint64_t main_time = 0;       // Current simulation time
// This is a 64-bit integer to reduce wrap over issues and
// allow modulus.  This is in units of the timeprecision
// used in Verilog (or from --timescale-override)
double sc_time_stamp() {        // Called by $time in Verilog
    return main_time;           // converts to double, to match
                                // what SystemC does
}


void init_nvboard(){
    // Below is arguments of nvboard_bind_pin()
    // pins , is_realtime_signal , is_output , width , phy_pins
    //nvboard_bind_pin( &top->f, BIND_RATE_SCR/*BIND_RATE_RT*/, BIND_DIR_OUT, 1, LD0);//bind pin to phy board
    
    // If auto_bind is used , below line should be uncomment
    nvboard_bind_all_pins(top);

    nvboard_init();//init nvboard
}



int main(int argc, char** argv, char** env) {

    sim_init();
    init_nvboard();

    while (1/*!contextp->gotFinish()&&main_time<1000*/) { 
        sim_main();
        break;
        nvboard_update();
    }
    
    nvboard_quit(); //quit nvboard
    sim_exit();

    return 0;
  }

