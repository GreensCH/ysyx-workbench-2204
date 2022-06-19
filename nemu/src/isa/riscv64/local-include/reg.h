#ifndef __RISCV64_REG_H__
#define __RISCV64_REG_H__

#include <common.h>

static inline int check_reg_idx(int idx) {
  IFDEF(CONFIG_RT_CHECK, assert(idx >= 0 && idx < 32));
  return idx;
}

static inline int check_sysreg_idx(int idx) {
  // IFDEF(CONFIG_RT_CHECK, assert(idx >= 0 && idx < 32));
  switch (idx)
  {
  case 0x305://mtvec
    return 0;
  case 0x341://mepc
    return 1;
  case 0x342://mcause
    return 2;
  case 0x304://mie
    return 3;
  case 0x344://mip
    return 4;
  case 0x343://mtval
    return 5;
  case 0x340://mscratch
    return 6;
  case 0x300://mstatus
    return 7;
  default:
    Assert(0, "CSR index:%d overflow(default 8 registers only)", idx);
    return 0;
  }
  return 0;
}

#define gpr(idx) (cpu.gpr[check_reg_idx(idx)])
#define sr(idx)  (cpu.csr[check_sysreg_idx(idx)])

#define mtvec     (cpu.csr[check_sysreg_idx(0)])
#define mepc      (cpu.csr[check_sysreg_idx(1)])
#define mcause    (cpu.csr[check_sysreg_idx(2)])
#define mie       (cpu.csr[check_sysreg_idx(3)])
#define mip       (cpu.csr[check_sysreg_idx(4)])
#define mtval     (cpu.csr[check_sysreg_idx(5)])
#define mscratch  (cpu.csr[check_sysreg_idx(6)])
#define mstatus   (cpu.csr[check_sysreg_idx(7)])

static inline const char* reg_name(int idx, int width) {
  extern const char* regs[];
  return regs[check_reg_idx(idx)];
}

#endif
