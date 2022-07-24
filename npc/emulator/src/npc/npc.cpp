#include "include.h"
#include <locale.h>


/* The assembly code of instructions executed is only output to the screen
 * when the number of instructions executed is less than this value.
 * This is useful when you use the `si' command.
 * You can modify this value as you want.
 */
#define MAX_INST_TO_PRINT 10


CPU_state cpu = {};
uint64_t g_nr_guest_inst = 0;
static uint64_t g_timer = 0; // unit: us
static bool g_print_step = false;

void device_update();
IFDEF(CONFIG_ITRACE, void add_itrace(char *s);)
IFDEF(CONFIG_ITRACE, void itrace_log();)
IFDEF(CONFIG_FTRACE, void ftrace_log(Decode *_this, vaddr_t dnpc);)
IFDEF(CONFIG_WATCHPOINT, bool wp_exec();)

void trace_and_difftest(Decode *_this, vaddr_t dnpc) {
  if(cpu.pc == 0x80000000 || cpu.pc == 0) return;//流水线前面的准备
  static word_t cmp;
  if(cmp == cpu.pc) 
    return;//流水线空泡
  else 
    cmp = cpu.pc;//正常情况
  if(cpu_device){
    ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
    return;
  }
  //ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
  IFDEF(CONFIG_ITRACE, add_itrace(_this->logbuf);)
  IFDEF(CONFIG_FTRACE, ftrace_log(_this, dnpc);)
  
  IFDEF(CONFIG_WATCHPOINT, if(wp_exec()) npc_state.state = NPC_STOP;)
  IFDEF(CONFIG_DIFFTEST, difftest_step(_this->pc, dnpc));
  IFDEF(CONFIG_REALTIME_PRTINT_INST, Log(ASNI_FG_BLACK "Current PC%s" ASNI_FG_BLACK,_this->logbuf);)
  // if (g_print_step) { IFDEF(CONFIG_ITRACE, printf("Current PC%s\n",_this->logbuf)); }//printf小于10条的命令
}

static void exec_once(Decode *s, vaddr_t pc) {
  cpu.pc = cpu_pc;//init pc
  s->pc = cpu.pc;//refresh decode structure
  s->snpc = cpu.pc + 4;
  s->isa.inst.val = paddr_read(cpu.pc, 4);
  // top->io_inst = paddr_read(cpu.pc, 4);//insert inst into npc
  step_and_dump_wave();//npc move on
  for (int i = 0; i < 32; i++) {//refresh gpr in test env
    cpu.gpr[i] = cpu_gpr[i];
  }
  cpu.pc = cpu_pc;//refresh pc
#ifdef CONFIG_ITRACE
  char *p = s->logbuf;
  if(!s->pc)//0不记录
    return;
  p += snprintf(p, sizeof(s->logbuf), FMT_WORD ":", s->pc);
  int ilen = s->snpc - s->pc;
  int i;
  uint8_t *inst = (uint8_t *)&s->isa.inst.val;
  for (i = 0; i < ilen; i ++) {
    p += snprintf(p, 4, " %02x", inst[i]);
  }
  int ilen_max = MUXDEF(CONFIG_ISA_x86, 8, 4);
  int space_len = ilen_max - ilen;
  if (space_len < 0) space_len = 0;
  space_len = space_len * 3 + 1;
  memset(p, ' ', space_len);
  p += space_len;

  void disassemble(char *str, int size, uint64_t pc, uint8_t *code, int nbyte);
  disassemble(p, s->logbuf + sizeof(s->logbuf) - p, s->pc, (uint8_t *)&s->isa.inst.val, ilen);

#endif
}

static void execute(uint64_t n) {
  Decode s;
  for (;n > 0; n --) {
    exec_once(&s, cpu.pc);
    g_nr_guest_inst ++;
    trace_and_difftest(&s, cpu.pc);
    if (npc_state.state != NPC_RUNNING) break;
    // IFDEF(CONFIG_DEVICE, device_update());
  }
}

static void statistic() {
  IFNDEF(CONFIG_TARGET_AM, setlocale(LC_NUMERIC, ""));
#define NUMBERIC_FMT MUXDEF(CONFIG_TARGET_AM, "%ld", "%'ld")
  Log("host time spent = " NUMBERIC_FMT " us", g_timer);
  Log("total guest instructions = " NUMBERIC_FMT, g_nr_guest_inst);
  if (g_timer > 0) Log("simulation frequency = " NUMBERIC_FMT " inst/s", g_nr_guest_inst * 1000000 / g_timer);
  else Log("Finish running in less than 1 us and can not calculate the simulation frequency");
}

void assert_fail_msg() {
  isa_reg_display();
  statistic();
}

/* Simulate how the CPU works. */
void cpu_exec(uint64_t n) {
  g_print_step = (n < MAX_INST_TO_PRINT);
  switch (npc_state.state) {
    case NPC_END: case NPC_ABORT:
      printf("Program execution has ended. To restart the program, exit NEMU and run again.\n");
      return;
    default: npc_state.state = NPC_RUNNING;
  }

  uint64_t timer_start = get_time();

  execute(n);

  uint64_t timer_end = get_time();
  g_timer += timer_end - timer_start;

  switch (npc_state.state) {
    case NPC_RUNNING: npc_state.state = NPC_STOP; break;

    case NPC_END: case NPC_ABORT:
    IFDEF(CONFIG_ITRACE, itrace_log());
      Log("nemu: %s at pc = " FMT_WORD,
          (npc_state.state == NPC_ABORT ? ASNI_FMT("ABORT", ASNI_FG_RED) :
           (npc_state.halt_ret == 0 ? ASNI_FMT("HIT GOOD TRAP", ASNI_FG_GREEN) :
            ASNI_FMT("HIT BAD TRAP", ASNI_FG_RED))),
          npc_state.halt_pc);
      // fall through
    case NPC_QUIT: statistic();
  }
}

