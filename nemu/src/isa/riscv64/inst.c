#include "local-include/reg.h"
#include <cpu/cpu.h>
#include <cpu/ifetch.h>
#include <cpu/decode.h>

#define R(i) gpr(i)
#define Mr vaddr_read
#define Mw vaddr_write

enum {
  TYPE_I, TYPE_U, TYPE_S, TYPE_R, TYPE_J, TYPE_B,
  TYPE_N, // none
};

// xxxR指把reg的值存入xxx, xxxI指根据immediate存入xxx
#define src1R(n) do { *src1 = R(n); } while (0)
#define src2R(n) do { *src2 = R(n); } while (0)
#define destR(n) do { *dest = n; } while (0)
#define src1I(i) do { *src1 = i; } while (0)
#define src2I(i) do { *src2 = i; } while (0)
#define destI(i) do { *dest = i; } while (0) //立即数i放入dstR

static word_t immI(uint32_t i) { return SEXT(BITS(i, 31, 20), 12); }//i是指令码而不是立即数
static word_t immU(uint32_t i) { return SEXT(BITS(i, 31, 12), 20) << 12; }
static word_t immS(uint32_t i) { return (SEXT(BITS(i, 31, 25), 7) << 5) | BITS(i, 11, 7); }
//TODO：SEXT.n = imm的最高位 BITS截取的是指令码
static word_t immB(uint32_t i) { return SEXT(BITS(i, 31, 31), 1) << 12 | BITS(i, 6, 6) << 11 | BITS(i, 30, 25) << 5 | BITS(i, 11, 8) << 1 ; }
static word_t immJ(uint32_t i) { return SEXT(BITS(i, 31, 31), 1) << 20 | BITS(i, 19, 12) << 12 | BITS(i, 30, 21) << 1 ; }


static void decode_operand(Decode *s, word_t *dest, word_t *src1, word_t *src2, int type) {
  uint32_t i = s->isa.inst.val;
  int rd  = BITS(i, 11, 7);
  int rs1 = BITS(i, 19, 15);
  int rs2 = BITS(i, 24, 20);
  // destR(rd);
  switch (type) {
    //TODO
    case TYPE_R: 
      destR(rd); 
      src1R(rs1); 
      src2R(rs2); 
    break;
    case TYPE_I:
      destR(rd);//redundancy
      src1R(rs1);    
      src2I(immI(i)); 
    break;
    case TYPE_S: 
      destI(immS(i)); 
      src1R(rs1); 
      src2R(rs2); 
    break;
    //TODO
    case TYPE_B: 
      destI(immB(i)); //TODO
      src1R(rs1); 
      src2R(rs2); 
    break;
    case TYPE_U:
      destR(rd);//redundancy
      src1I(immU(i)); 
    break;
    //TODO
    case TYPE_J: 
      destR(rd); 
      src1I(immJ(i)); //TODO
    break;
  }
}

static int decode_exec(Decode *s) {
  word_t dest = 0, src1 = 0, src2 = 0;
  s->dnpc = s->snpc;

#define INSTPAT_INST(s) ((s)->isa.inst.val)
#define INSTPAT_MATCH(s, name, type, ... /* body */ ) { \
  decode_operand(s, &dest, &src1, &src2, concat(TYPE_, type)); \
  __VA_ARGS__ ; \
}

  INSTPAT_START();
  INSTPAT("??????? ????? ????? ??? ????? 0010111", auipc  , U, R(dest) = src1 + s->pc);
  INSTPAT("??????? ????? ????? 011 ????? 0000011", ld     , I, R(dest) = Mr(src1 + src2, 8));
  INSTPAT("??????? ????? ????? 011 ????? 0100011", sd     , S, Mw(src1 + dest, 8, src2));
  //R-type
  INSTPAT("0000000 ????? ????? 000 ????? 0110011", add    , R, R(dest) = src1 + src2);
  INSTPAT("0100000 ????? ????? 000 ????? 0110011", sub    , R, R(dest) = src1 - src2);
  INSTPAT("0000000 ????? ????? 001 ????? 0110011", sll    , R, R(dest) = src1 << BITS(src2, 5, 0));//diff from 32I
  INSTPAT("0000000 ????? ????? 010 ????? 0110011", slt    , R, R(dest) = ((sword_t)src1 < (sword_t)src2));
  INSTPAT("0000000 ????? ????? 011 ????? 0110011", slu    , R, R(dest) = ((word_t)src1 < (word_t)src2));
  INSTPAT("0000000 ????? ????? 100 ????? 0110011", xor    , R, R(dest) = (src1 ^ src2));
  INSTPAT("0000000 ????? ????? 101 ????? 0110011", srl    , R, R(dest) = ((word_t)src1 >> BITS(src2, 5, 0)));//diff from 32I
  INSTPAT("0100000 ????? ????? 101 ????? 0110011", srl    , R, R(dest) = ((sword_t)src1 >> BITS(src2, 5, 0)));//diff from 32I
  



  INSTPAT("0000000 00001 00000 000 00000 11100 11", ebreak , N, NEMUTRAP(s->pc, R(10))); // R(10) is $a0
  INSTPAT("??????? ????? ????? ??? ????? ????? ??", inv    , N, INV(s->pc));
  INSTPAT_END();

  R(0) = 0; // reset $zero to 0

  return 0;
}

int isa_exec_once(Decode *s) {
  s->isa.inst.val = inst_fetch(&s->snpc, 4);
  return decode_exec(s);
}
