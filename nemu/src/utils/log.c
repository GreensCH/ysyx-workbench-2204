#include <common.h>
#ifdef FTRACE
  #include <elf.h>
#endif

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
#ifdef CONFIG_ITRACE
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
  char out[72];
  int index = get_itrace(s);
  for(int i = 0 ;i < ITRACE_STEP + 1; i ++){
    if(s[0] != '\0'){
      if(index == 0){
        sprintf(out, "   -->%s", s);
        //printf("%s\n", out);
        Log("%s", out);
        break;
      }
      else{
        sprintf(out, "      %s", s);
        //printf("%s\n", out);
        Log("%s", out);
      }
    }
    index = get_itrace(s);
  }
}
#endif

/*
* mtrace
*/
#ifdef CONFIG_MTRACE
#include <memory/paddr.h>
word_t isa_reg_str2val(const char *s, bool *success);
void mtrace_rd_log(word_t data, word_t addr){
  bool flag = true;
  if(isa_reg_str2val("PC", &flag) == addr) return;
  if (likely(in_pmem(addr))) Log("PMEM-RD:PC(0x%016lx) 0x%016lx @0x%016lx", isa_reg_str2val("PC", &flag), data, addr); 
  IFDEF(CONFIG_DEVICE, Log("MMIO-RD:PC(0x%016lx) 0x%016lx @0x%016lx", isa_reg_str2val("PC", &flag), data, addr)); 
}
void mtrace_we_log(word_t data, word_t addr){
  bool flag = true;
  if(isa_reg_str2val("PC", &flag) == addr) return;
  if (likely(in_pmem(addr))) Log("PMEM-WE:PC(0x%016lx) 0x%016lx @0x%016lx", isa_reg_str2val("PC", &flag), data, addr); 
  IFDEF(CONFIG_DEVICE, Log("MMIO-WE:PC(0x%016lx) 0x%016lx @0x%016lx", isa_reg_str2val("PC", &flag), data, addr)); 
}
#endif


/*
* ftrace
*/
#ifdef CONFIG_FTRACE
#include <elf.h>
#include <cpu/decode.h>

typedef struct
{
    uint64_t fun_addr;
    uint64_t fun_size;
    char *fun_name;
} elf_info;
elf_info elf_func[1000];
int elf_cnt = 0;

void read_elf(const char *elf_name)
{
    if (elf_name == NULL)
    {
        Log("Ftrace fail, no elf file input");
        return;
    }
    FILE *stream;
    stream = fopen(elf_name, "rb");
    Assert(stream, "Ftrace fail, can not open '%s'", elf_name);

    unsigned char *buffer;
    buffer = (unsigned char *)malloc(100500 * sizeof(unsigned char));
    int ret = fread(buffer, sizeof(unsigned char), 100500, stream);
    Assert(ret != 0, "Ftrace fail, can not open '%s'", elf_name);

    Elf64_Ehdr *ehdr = (Elf64_Ehdr *)buffer;
    Elf64_Shdr *shdr = (Elf64_Shdr *)(buffer + ehdr->e_shoff);
    Elf64_Shdr *shdr_strtab = NULL;
    Elf64_Shdr *shdr_symtab = NULL;

    for (int i = 0; shdr_strtab == NULL || shdr_symtab == NULL; i++)
    {
        if (shdr[i].sh_type == SHT_SYMTAB)
        {
            shdr_symtab = &shdr[i];
        }
        else if (shdr[i].sh_type == SHT_STRTAB)
        {
            shdr_strtab = &shdr[i];
            break;
        }
    }

    Elf64_Sym *table_sym = (Elf64_Sym *)(buffer + shdr_symtab->sh_offset);

    for (int i = 0; i <= shdr_symtab->sh_size / shdr_symtab->sh_entsize; i++)
    {
        if (ELF64_ST_TYPE(table_sym[i].st_info) == STT_FUNC)
        {
            elf_func[elf_cnt].fun_addr = table_sym[i].st_value;
            elf_func[elf_cnt].fun_size = table_sym[i].st_size;
            elf_func[elf_cnt].fun_name = (char *)(buffer + shdr_strtab->sh_offset + table_sym[i].st_name);
            elf_cnt++;
        }
    }

    // for (int i = 0; i < elf_cnt; i++)
    //    Log("%lx %lx %s", elf_func[i].fun_addr, elf_func[i].fun_size, elf_func[i].fun_name);
    return;
}
void ftrace_log(Decode *s, vaddr_t dnpc){
  uint64_t fpc = 0;
  uint64_t fdnpc = 0;
  static word_t print_start = 0;
  for (int i = 0; i < elf_cnt; i++){
    if(elf_func[i].fun_addr <= s->pc && s->pc < elf_func[i].fun_addr + elf_func[i].fun_size)
      fpc = i;
    if(elf_func[i].fun_addr <= dnpc && dnpc < elf_func[i].fun_addr + elf_func[i].fun_size)
      fdnpc = i;
  }
  if (fpc == fdnpc)
    return;
  if(elf_func[fdnpc].fun_addr == dnpc){
    if(strstr(s->logbuf, "jal")){
      _Log(ASNI_FMT("[%s:%d %s] 0x%08lx:\t", ASNI_FG_BLUE),__FILE__ ,__LINE__ ,__func__ ,s->pc );
      print_start += 2;
      for(int i = print_start; i > 0; i--)
        _Log(" ");
      _Log(ASNI_FMT("call [%s@0x%08lx]\n", ASNI_FG_BLUE), elf_func[fdnpc].fun_name, elf_func[fdnpc].fun_addr);
    }
  }
  else if(strstr(s->logbuf, "ret"))
  {
      _Log(ASNI_FMT("[%s:%d %s] 0x%08lx:\t", ASNI_FG_BLUE),__FILE__ ,__LINE__ ,__func__ ,s->pc );
    for(int i = print_start; i > 0; i--)
      _Log(" ");
    _Log(ASNI_FMT("ret  [%s]\n", ASNI_FG_BLUE), elf_func[fpc].fun_name);
    print_start = print_start > 1 ? print_start - 2 : 0;
  }
}

#endif

