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

static void out_of_bound(paddr_t addr) {
  panic("address = " FMT_PADDR " is out of bound of pmem [" FMT_PADDR ", " FMT_PADDR ") at pc = " FMT_WORD,
      addr, CONFIG_MBASE, CONFIG_MBASE + CONFIG_MSIZE, cpu.pc);
}
enum {PROGRAM_MEMORY, DATA_MEMORY};

extern "C" word_t pmem_read(paddr_t addr, int len) {
  if(!in_pmem(addr)){
    printf("read fail" FMT_PADDR "\n", addr);
    return 0;
  }
  IFDEF(CONFIG_MTRACE, mtrace_rd_log(host_read(guest_to_host(addr), len), addr););
  return host_read(guest_to_host(addr), len);
}

extern "C" void  pmem_write(paddr_t addr, int len, word_t data) {
  if(!in_pmem(addr)){
    printf("write fail" FMT_PADDR "\n", addr);
    return;
  }
  IFDEF(CONFIG_MTRACE,  mtrace_we_log(data, addr););
  host_write(guest_to_host(addr), len, data);
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


word_t paddr_read(paddr_t addr, int len) {

  if (likely(in_pmem(addr))) return host_read(guest_to_host(addr), len);
  else return 0;
  // IFDEF(CONFIG_DEVICE, return mmio_read(addr, len));
  out_of_bound(addr);
  return 0;
}

void paddr_write(paddr_t addr, int len, word_t data) {

  if (likely(in_pmem(addr))) { host_write(guest_to_host(addr), len, data); return; }
  else return;
  // IFDEF(CONFIG_DEVICE, mmio_write(addr, len, data); return);
  out_of_bound(addr);
}

