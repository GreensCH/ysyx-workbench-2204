#include <isa.h>
#include <cpu/cpu.h>
#include <readline/readline.h>
#include <readline/history.h>
#include "sdb.h"
#include <common.h>
#include <memory/paddr.h>

static int is_batch_mode = false;

void init_regex();
void init_wp_pool();

/* We use the `readline' library to provide more flexibility to read from stdin. */
static char* rl_gets() {
  static char *line_read = NULL;

  if (line_read) {
    free(line_read);
    line_read = NULL;
  }

  line_read = readline("(nemu) ");

  if (line_read && *line_read) {
    add_history(line_read);
  }

  return line_read;
}

static int cmd_c(char *args) {
  cpu_exec(-1);
  return 0;
}

static int cmd_q(char *args) {
  nemu_state.state = NEMU_END;//set_nemu_state
  return -1;
}

static int cmd_si(char *args) {
  if(args!=NULL){
    // printf("run %d",atoi(args));
    cpu_exec(atoi(args));
  }//可以再添加一个非法字串匹配的if
  else {
    cpu_exec(1);
  }
  return 0;
}

static int cmd_info(char *args) {
  if(args==NULL)
    Log("No arguments!");
  else if(args[0] == 'r' && args[1] == '\0')
    isa_reg_display();
  else if(args[0] == 'w' && args[1] == '\0')
    wp_list_display();
  else
    Log("Invalid parameter %s\n", args);
  return 0;
}

static int cmd_x(char *args) {
  
  char *token_N;//token1
  char *token_EXPR;//token2

  vaddr_t base = 0;
  vaddr_t offset = 0;
  /* get N */
  token_N = strtok(args," ");
  if(token_N != NULL){
    sscanf(token_N, "%ld", &offset);//get ram offset
  }
  else{
    return 0;
  }
  /* get EXPR */
  token_EXPR = strtok(NULL," ");
  if(token_EXPR != NULL){
    sscanf(token_EXPR, "%lx", &base);//get ram addr
    //printf("base:%lx\n", base);
  }
  else{
    return 0;
  }
  /* do addr convert */
  for(int p = 0; p<offset; p++){
    word_t  val;
    val = paddr_read(base + 4 * p,4);
    printf("addr(0x%08lx),value(0x%08lx)\n", (base + 4 * p), val);
  }

  return 0;
}//x 10 0x80000000

static int cmd_e(char *args) {
  bool success;
  // static word_t test = 666666166;
  // printf("number:%ld\n addr:%p\n",test,&test);
  if(args!=NULL){
    word_t res = expr(args, &success);
    printf("result:%ld,success:%d\n",res,success);
  }
  else
    Log("No arguments!");
  // system("pause");
  return 0;
}

static int cmd_watch(char *args){
  bool success = false;
  new_wp_expr(args, &success);
  return 0;
}

static int cmd_d(char *args) {
  bool success = false;
  delete_wp_expr(args, &success);
  return 0;
}


static int cmd_help(char *args);
static struct {
  const char *name;
  const char *description;
  int (*handler) (char *);
} cmd_table [] = {
  { "help", "Display informations about all supported commands", cmd_help },
  { "c", "Continue the execution of the program", cmd_c },
  { "q", "Exit NEMU", cmd_q },

  /* TODO: Add more commands */
  {"si", "Single step", cmd_si },
  {"info", "Print register value or watch point status", cmd_info },
  {"x", "Scan ram value", cmd_x },
  {"e", "Exculate expression", cmd_e },
  {"d", "Delete watchpoints or breakpoints", cmd_d },
  {"watch", "Add watchpoint", cmd_watch },
};

#define NR_CMD ARRLEN(cmd_table)

static int cmd_help(char *args) {
  /* extract the first argument */
  char *arg = strtok(NULL, " ");
  int i;

  if (arg == NULL) {
    /* no argument given */
    for (i = 0; i < NR_CMD; i ++) {
      printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
    }
  }
  else {
    for (i = 0; i < NR_CMD; i ++) {
      if (strcmp(arg, cmd_table[i].name) == 0) {
        printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
        return 0;
      }
    }
    Log("Unknown command '%s'\n", arg);
  }
  return 0;
}

void sdb_set_batch_mode() {
  is_batch_mode = true;
}

void sdb_mainloop() {
  if (is_batch_mode) {
    cmd_c(NULL);
    return;
  }

  for (char *str; (str = rl_gets()) != NULL; ) {
    char *str_end = str + strlen(str);

    /* extract the first token as the command */
    char *cmd = strtok(str, " ");
    if (cmd == NULL) { continue; }

    /* treat the remaining string as the arguments,
     * which may need further parsing
     */
    char *args = cmd + strlen(cmd) + 1;
    if (args >= str_end) {
      args = NULL;
    }

#ifdef CONFIG_DEVICE
    extern void sdl_clear_event_queue();
    sdl_clear_event_queue();
#endif

    int i;
    for (i = 0; i < NR_CMD; i ++) {
      if (strcmp(cmd, cmd_table[i].name) == 0) {
        if (cmd_table[i].handler(args) < 0) { return; }
        break;
      }
    }

    if (i == NR_CMD) { printf("Unknown command '%s'\n", cmd); }
  }
}

void init_sdb() {
  /* Compile the regular expressions. */
  init_regex();

  /* Initialize the watchpoint pool. */
  init_wp_pool();
}