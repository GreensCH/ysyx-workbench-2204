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
  word_t regs_length = sizeof(regs) / (8 * sizeof(char));
  printf("Regisiter List:\n");
  for(int i = 0; i < regs_length ; i++){
    printf("%3s(0x%08lx)",regs[i],cpu.gpr[i]);
    if((i+1)%4==0)
      printf("\n");
    else
      printf("\t");
  }
  printf("PC address:\n PC(0x%8lx)\n",cpu.pc);
  
}

/*
* 获取寄存器的值
*/
void my_strlwr(void *c) {
  char *p = (char *)c;
  for(; *p; p++)
    if(*p >= 'A' && *p <='Z')
      *p = (*p - 'A') + 'a';
}

word_t isa_reg_str2val(const char *s, bool *success) {

  word_t regs_length = sizeof(regs) / (8 * sizeof(char));
  char buff[8];

  //大写转小写  
  strcpy(buff, s);
  buff[7] = '\0';
  my_strlwr(buff);
  printf("*** %s ***\n", buff);

  for(int i = 0; i < regs_length ; i++){
    if(!strcmp(s, regs[i]))
      return cpu.gpr[i];
  }

  if(!strcmp(buff, "pc"))
    return cpu.pc;
  return -1;

}

 
