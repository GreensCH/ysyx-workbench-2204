#include "include.h"


extern "C" void ebreak() {
  NPCTRAP(cpu_pc, cpu_gpr[10]);
  printf("*************************886***********************\n");
}