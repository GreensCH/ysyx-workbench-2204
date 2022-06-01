#include "include.h"


extern "C" void ebreak() {
  NPCTRAP(cpu.pc, cpu_gpr[10]);

}