#ifndef __SIM_COMP_H__
#define __SIM_COMP_H__

#include "include.h"

extern VerilatedContext* contextp ;
extern VerilatedVcdC* tfp ;
extern VTop* top;
extern word_t old_pc;

double sc_time_stamp();
void step_and_dump_wave();
void reset(int n);
// void sim_init();
void sim_init(int argc, char** argv);
void sim_exit();
#endif