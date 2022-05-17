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
* Add itrace
*/

char  iringbuf[16][64];
int   iringbuf_index = 0;

void add_itrace(char *s){
  strcpy(iringbuf[iringbuf_index], s);
  if(iringbuf_index < 15)
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
  if(_i < 16){
    strcpy(s, iringbuf[_i]);
  }
  else if(_i < 16 + _irindex + 1){
    strcpy(s, iringbuf[_i - 16]);
  }
  else{
    return 0;
  }
  _i += 1;
  return 16 - (_i - _irindex);
}

void itrace_log(){
  char s[64];
  char out[67];
  int index = get_itrace(s);
  for(int i = 0 ;i < 16; i ++){
    if(s[0] != '\0'){
      if(index == 0){
        sprintf(out, "-->%s", s);
        printf("%s\n", out);
        break;
      }
      else{
        sprintf(out, "   %s", s);
        printf("%s\n", out);
      }
    }
    index = get_itrace(s);
  }
}