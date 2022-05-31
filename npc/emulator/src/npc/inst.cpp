#include "include.h"


extern "C" void ebreak() {
  NPCTRAP(cpu_pc, cpu_gpr[10]);
}