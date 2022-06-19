#include <isa.h>
#include <cpu/difftest.h>
#include "../local-include/reg.h"

bool isa_difftest_checkregs(CPU_state *ref_r, vaddr_t pc) {//pc=npc
  for(int i = 0; i < 32; i++){
    if(ref_r->gpr[i] != cpu.gpr[i]){
      Log("*** Difftest fail: reg %s dismatch ref(0x%016lx) dut(0x%016lx) ***", get_reg_name(i), ref_r->gpr[i], cpu.gpr[i]);
      return false;
    }
  }
  if(ref_r->pc != cpu.pc){
    Log("*** Difftest fail: pc dismatch ref(0x%016lx) dut(0x%016lx) ***", ref_r->pc, cpu.pc);
    return false;
  }
  return true;
}

void isa_difftest_attach() {
}
