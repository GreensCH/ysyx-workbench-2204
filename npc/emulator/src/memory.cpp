#include "include.h"
#include "memory.h"

#if   defined(CONFIG_PMEM_MALLOC)
static uint8_t *pmem = NULL;
#else // CONFIG_PMEM_GARRAY
static uint8_t pmem[CONFIG_MSIZE] PG_ALIGN = {};
#endif
#ifdef CONFIG_MTRACE
  void mtrace_rd_log(word_t data, word_t addr);
  void mtrace_we_log(word_t data, word_t addr);
#endif

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t *haddr) { return haddr - pmem + CONFIG_MBASE; }

extern "C" word_t pmem_read(paddr_t addr, int len) {
  printf("\33[1;34m VLT\tREAD addr:0x%016lx, len:%d \33[0m \n" ,addr, len);
  if(addr < 0x8000000){
    // printf("read fail\n");
    return 0;
  }
  if(addr > 0x90000000)
    return 0;
  // printf("\n");
  word_t ret = host_read(guest_to_host(addr), len);
  return ret;
}

extern "C" void  pmem_write(paddr_t addr, int len, word_t data) {
//  _Log(ASNI_FMT("VLT\tWRITE addr0x%016lx, len:%d ,data0x%016lx\n", ASNI_FG_BLUE ASNI_NONE),addr, len, data );
  if(addr < 0x80000000){
    // printf("write fail\n");
    return;
  }
  // printf("\n");
  host_write(guest_to_host(addr), len, data);
}

static void out_of_bound(paddr_t addr) {
  panic("address = " FMT_PADDR " is out of bound of pmem [" FMT_PADDR ", " FMT_PADDR ") at pc = " FMT_WORD,
      addr, CONFIG_MBASE, CONFIG_MBASE + CONFIG_MSIZE, top->io_pc);
}

void init_mem() {
#if   defined(CONFIG_PMEM_MALLOC)
  pmem = malloc(CONFIG_MSIZE);
  assert(pmem);
#endif
#ifdef CONFIG_MEM_RANDOM
  uint32_t *p = (uint32_t *)pmem;
  int i;
  for (i = 0; i < (int) (CONFIG_MSIZE / sizeof(p[0])); i ++) {
    p[i] = rand();
  }
#endif
  Log("physical memory area [" FMT_PADDR ", " FMT_PADDR "]",
      (paddr_t)CONFIG_MBASE, (paddr_t)CONFIG_MBASE + CONFIG_MSIZE);
}