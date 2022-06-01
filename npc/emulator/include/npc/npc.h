#ifndef __NPC_H__
#define __NPC_H__

#include "include.h"

typedef struct {
  word_t gpr[32];
  vaddr_t pc;
} CPU_state;

// decode
typedef struct {
  union {
    uint32_t val;
  } inst;
} ISADecodeInfo;

typedef struct Decode {
  vaddr_t pc;
  vaddr_t snpc; // static next pc
  vaddr_t dnpc; // dynamic next pc
  ISADecodeInfo isa;
  IFDEF(CONFIG_ITRACE, char logbuf[128]);
} Decode;

// monitor
extern char isa_logo[];
void init_isa();

// reg
extern CPU_state cpu;
extern uint64_t *cpu_gpr;
// extern word_t cpu_pc;
// extern word_t cpu_npc;

void isa_reg_display();
void common_reg_display(CPU_state *state);
word_t isa_reg_str2val(const char *name, bool *success);
const char *get_reg_name(int i);

// exec
struct Decode;
int isa_exec_once(struct Decode *s);


void cpu_exec(uint64_t n);

void set_npc_state(int state, vaddr_t pc, int halt_ret);
// void invalid_inst(vaddr_t thispc);

#define NPCTRAP(thispc, code) set_npc_state(NPC_END, thispc, code)
// #define INV(thispc) invalid_inst(thispc)


// memory
enum { MMU_DIRECT, MMU_TRANSLATE, MMU_FAIL };
enum { MEM_TYPE_IFETCH, MEM_TYPE_READ, MEM_TYPE_WRITE };
enum { MEM_RET_OK, MEM_RET_FAIL, MEM_RET_CROSS_PAGE };
#ifndef isa_mmu_check
int isa_mmu_check(vaddr_t vaddr, int len, int type);
#endif
paddr_t isa_mmu_translate(vaddr_t vaddr, int len, int type);

// interrupt/exception
vaddr_t isa_raise_intr(word_t NO, vaddr_t epc);
#define INTR_EMPTY ((word_t)-1)
word_t isa_query_intr();

// difftest
bool isa_difftest_checkregs(CPU_state *ref_r, vaddr_t pc);
void isa_difftest_attach();

#endif