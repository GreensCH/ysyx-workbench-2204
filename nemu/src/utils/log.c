#include <common.h>

extern uint64_t g_nr_guest_inst;
FILE *log_fp = NULL;

void init_log(const char *log_file) {
  log_fp = stdout;
  if (log_file != NULL) {
    FILE *fp = fopen(log_file, "w");
    Assert(fp, "Can not open '%s'", log_file);
    log_fp = fp;
  }
  Log("Log is written to %s", log_file ? log_file : "stdout");
}

bool log_enable() {
  return MUXDEF(CONFIG_TRACE, (g_nr_guest_inst >= CONFIG_TRACE_START) &&
         (g_nr_guest_inst <= CONFIG_TRACE_END), false);
}

/*
* itrace
*/
#define ITRACE_STEP 16
char  iringbuf[ITRACE_STEP][64];
int   iringbuf_index = 0;

void add_itrace(char *s){
  strcpy(iringbuf[iringbuf_index], s);
  if(iringbuf_index < ITRACE_STEP - 1)
    iringbuf_index += 1;
  else
    iringbuf_index = 0;
}

int get_itrace(char *s){
  static int _i = 0;
  static int _irindex = 0;
  static char *_s = '\0';
  if(_s != s){
    _s = s;
    _i = iringbuf_index;
    _irindex = iringbuf_index;
  }
  if(_i < ITRACE_STEP){
    strcpy(s, iringbuf[_i]);
  }
  else if(_i < ITRACE_STEP + _irindex + 1){
    strcpy(s, iringbuf[_i - ITRACE_STEP]);
  }
  else{
    return 0;
  }
  _i += 1;
  return ITRACE_STEP - (_i - _irindex);
}

void itrace_log(){
  char s[64];
  char out[67];
  int index = get_itrace(s);
  for(int i = 0 ;i < ITRACE_STEP + 1; i ++){
    if(s[0] != '\0'){
      if(index == 0){
        sprintf(out, "-->%s", s);
        //printf("%s\n", out);
        Log("%s", out);
        break;
      }
      else{
        sprintf(out, "   %s", s);
        //printf("%s\n", out);
        Log("%s", out);
      }
    }
    index = get_itrace(s);
  }
}

/*
* mtrace
*/
#include <memory/paddr.h>
void mtrace_rd_log(word_t data, word_t addr){
  if (likely(in_pmem(addr))) Log("PMEM-RD:0x%016lx @0x%016lx\n", data, addr); 
  IFDEF(CONFIG_DEVICE, Log("MMIO-RD:0x%016lx @0x%016lx\n", data, addr)); 
}
void mtrace_we_log(word_t data, word_t addr){
  if (likely(in_pmem(addr))) Log("PMEM-WE:0x%016lx @0x%016lx\n", data, addr); 
  IFDEF(CONFIG_DEVICE, Log("MMIO-WE:0x%016lx @0x%016lx\n", data, addr)); 
}