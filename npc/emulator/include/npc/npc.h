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

// reg
extern CPU_state cpu;
void isa_reg_display();
word_t isa_reg_str2val(const char *name, bool *success);
const char *get_reg_name(int i);

// exec
struct Decode;
int isa_exec_once(struct Decode *s);


void cpu_exec(uint64_t n);

// void set_nemu_state(int state, vaddr_t pc, int halt_ret);
// void invalid_inst(vaddr_t thispc);

// #define NEMUTRAP(thispc, code) set_nemu_state(NEMU_END, thispc, code)
// #define INV(thispc) invalid_inst(thispc)

#endif