#include <isa.h>
#include "local-include/reg.h"

const char *regs[] = {
  "$0", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
  "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
  "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
  "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
};
// typedef struct {
//   word_t gpr[32];
//   vaddr_t pc;
// } riscv64_CPU_state;
void isa_reg_display() {
  // word_t regs_length = sizeof(regs) / (8 * sizeof(char));

  for(int i=0;i<32;i++)
    printf("%4s - %10lX \t %10ld\n",regs[i],cpu.gpr[i],cpu.gpr[i]);
  printf("  pc - %10lx \t %10ld\n",cpu.pc,cpu.pc);
  return ;
}

word_t isa_reg_str2val(const char *s, bool *success) {
  return 0;
}
