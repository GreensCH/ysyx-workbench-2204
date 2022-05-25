#include "include.h"
#include "memory.h"

static uint8_t pmem[CONFIG_MSIZE] = {};

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t *haddr) { return haddr - pmem + CONFIG_MBASE; }

extern "C" word_t pmem_read(paddr_t addr, int len) {
  if(addr - CONFIG_MBASE < 0x80000000){
    printf("read fail\n");
    return 0;
  }
  word_t ret = host_read(guest_to_host(addr), len);
  return ret;
}

void pmem_write(paddr_t addr, int len, word_t data) {
  if(addr - CONFIG_MBASE < 0x80000000){
    printf("write fail\n");
    return;
  }
  host_write(guest_to_host(addr), len, data);
}
