#include "include.h"

void init_rand();
void init_log(const char *log_file);
void init_mem();
void init_difftest(char *ref_so_file, long img_size, int port);
void init_device();
void init_sdb();
void init_disasm(const char *triple);
IFDEF(CONFIG_FTRACE, void read_elf(const char *elf_file);)

static void welcome() {
  Log("Trace: %s", MUXDEF(CONFIG_TRACE, ASNI_FMT("ON", ASNI_FG_GREEN), ASNI_FMT("OFF", ASNI_FG_RED)));
  IFDEF(CONFIG_TRACE, Log("If trace is enabled, a log file will be generated "
        "to record the trace. This may lead to a large log file. "
        "If it is not necessary, you can disable it in menuconfig"));
  Log("Build time: %s, %s", __TIME__, __DATE__);
  printf("Welcome to %s-NEMU!\n", ASNI_FMT(str(__GUEST_ISA__), ASNI_FG_YELLOW ASNI_BG_RED));
  printf("For help, type \"help\"\n");
  // Log("Exercise: Please remove me in the source code and compile NEMU again.");
  // assert(0);
}

#ifndef CONFIG_TARGET_AM
#include <getopt.h>

void sdb_set_batch_mode();

static char *log_file = NULL;
static char *diff_so_file = NULL;
static char *img_file = NULL;
static char *elf_file = NULL;
static int difftest_port = 1234;

static long load_img() {
  if (img_file == NULL) {
    Log("No image is given. Use the default build-in image.");
    return 4096; // built-in image size
  }

  FILE *fp = fopen(img_file, "rb");
  Assert(fp, "Can not open '%s'", img_file);

  fseek(fp, 0, SEEK_END);
  long size = ftell(fp);

  Log("The image is %s, size = %ld", img_file, size);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(guest_to_host(RESET_VECTOR), size, 1, fp);
  assert(ret == 1);

  fclose(fp);
  return size;
}

static int parse_args(int argc, char *argv[]) {
  const struct option table[] = {
    {"batch"    , no_argument      , NULL, 'b'},
    {"log"      , required_argument, NULL, 'l'},
    {"diff"     , required_argument, NULL, 'd'},
    {"port"     , required_argument, NULL, 'p'},
    {"help"     , no_argument      , NULL, 'h'},
    {"elf"      , required_argument, NULL, 'e'},
    {0          , 0                , NULL,  0 },
  };
  int o;
  while ( (o = getopt_long(argc, argv, "-bhl:d:p:e:", table, NULL)) != -1) {
    switch (o) {
      case 'b': sdb_set_batch_mode(); break;
      case 'p': sscanf(optarg, "%d", &difftest_port); break;
      case 'l': log_file = optarg; break;
      case 'd': diff_so_file = optarg; break;
      case 'e': elf_file = optarg; break;
      case 1: img_file = optarg; return 0;
      default:
        printf("Usage: %s [OPTION...] IMAGE [args]\n\n", argv[0]);
        printf("\t-b,--batch              run with batch mode\n");
        printf("\t-l,--log=FILE           output log to FILE\n");
        printf("\t-d,--diff=REF_SO        run DiffTest with reference REF_SO\n");
        printf("\t-p,--port=PORT          run DiffTest with port PORT\n");
        printf("\t-e,--elf=FILE           input elf\n");
        printf("\n");
        exit(0);
    }
  }
  return 0;
}


#include <elf.h>
#include "npc/decode.h"

typedef struct
{
    uint64_t fun_addr;
    uint64_t fun_size;
    char *fun_name;
} elf_info;
elf_info elf_func[1000];
int elf_cnt = 0;

void read_elf(char *elf_name)
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

    for (int i = 0; i < elf_cnt; i++)
       Log("%lx %lx %s", elf_func[i].fun_addr, elf_func[i].fun_size, elf_func[i].fun_name);
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

void init_isa();
void init_monitor(int argc, char *argv[]) {
  /* Perform some global initialization. */
    printf("argc:%d\n",argc);
    for(int i = 0; i < argc; i++) 
      printf("%s\n",argv[i]);
  /* Parse arguments. */
  parse_args(argc, argv);

  /* Set random seed. */
  init_rand();

  /* Open the log file. */
  init_log(log_file);

  /* Initialize memory. */
  init_mem();

  /* Initialize devices. */
  IFDEF(CONFIG_DEVICE, init_device());

  /* Perform ISA dependent initialization. */
  init_isa();

  /* Load the image to memory. This will overwrite the built-in image. */
  long img_size = load_img();

  /* read elf file*/
  IFDEF(CONFIG_FTRACE, read_elf(elf_file));

  /* Initialize differential testing. */
  init_difftest(diff_so_file, img_size, difftest_port);

  /* Initialize the simple debugger. */
  init_sdb();

  IFDEF(CONFIG_ITRACE, init_disasm(
    MUXDEF(CONFIG_ISA_x86,     "i686",
    MUXDEF(CONFIG_ISA_mips32,  "mipsel",
    MUXDEF(CONFIG_ISA_riscv32, "riscv32",
    MUXDEF(CONFIG_ISA_riscv64, "riscv64", "bad")))) "-pc-linux-gnu"
  ));

  /* Display welcome message. */
  welcome();
  
  printf("log_file\t:%s\n",log_file);
  printf("img_file\t:%s\n",img_file);
  printf("elf_file\t:%s\n",elf_file);
  printf("diff_so_file\t:%s\n",diff_so_file);
}
#else // CONFIG_TARGET_AM
static long load_img() {
  extern char bin_start, bin_end;
  size_t size = &bin_end - &bin_start;
  Log("img size = %ld", size);
  memcpy(guest_to_host(RESET_VECTOR), &bin_start, size);
  return size;
}

void am_init_monitor() {
  init_rand();
  init_mem();
  init_isa();
  load_img();
  IFDEF(CONFIG_DEVICE, init_device());
  welcome();
}
#endif
