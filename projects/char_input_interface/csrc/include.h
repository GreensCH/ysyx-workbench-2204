#include "../generated/obj_dir/Vtop.h"
#include <verilated.h>
#include "verilated_vcd_c.h"
#include <iostream>  
#include <nvboard.h>


extern VerilatedContext* contextp ;
extern VerilatedVcdC* tfp ;
extern Vtop* top;

// Declare auto bind function
// If auto_bind is used , below line should be uncomment
void nvboard_bind_all_pins(Vtop* top);

void step_and_dump_wave();
void sim_init();
void sim_exit();
int  sim_main();