#include "verilated_dpi.h"
uint64_t *cpu_gpr = NULL;
uint64_t *cpu_pc = NULL;
extern "C" void set_gpr_ptr(const svOpenArrayHandle r) {
  cpu_gpr = (uint64_t *)(((VerilatedDpiOpenVar*)r)->datap());
}

extern "C" void set_pc_ptr(const svOpenArrayHandle r) {
  cpu_pc = (uint64_t *)(((VerilatedDpiOpenVar*)r)->datap());
}

// 一个输出RTL中通用寄存器的值的示例
void dump_gpr() {
  int i;
  for (i = 0; i < 32; i++) {
    printf("gpr[%d] = 0x%lx\n", i, cpu_gpr[i]);
  }
  printf("pc = 0x%lx\n",*cpu_pc);
}
