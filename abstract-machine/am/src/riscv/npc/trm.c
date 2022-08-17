#include <am.h>
#include <klib-macros.h>

extern char _heap_start;
int main(const char *args);

extern char _pmem_start;
#define PMEM_SIZE (128 * 1024 * 1024)
#define PMEM_END  ((uintptr_t)&_pmem_start + PMEM_SIZE)

Area heap = RANGE(&_heap_start, PMEM_END);
#ifndef MAINARGS
#define MAINARGS ""
#endif
static const char mainargs[] = MAINARGS;

// #define SOC_SIMULATOR
#ifdef SOC_SIMULATOR
#define SERIAL_PORT 0x10000000 // 0x1000_0000 ~ 0x1000_0fff
void putch(char ch) {
  *(volatile uint8_t *)(0x10000000 + 4) = ch;
}
#else
#include "emulator.h"
void putch(char ch) {
  outb(SERIAL_PORT, ch);
}
#endif

void halt(int code) {
  asm volatile("mv a0, %0; ebreak" : :"r"(code));
  // should not reach here
  while (1);
}

void _trm_init() {
  int ret = main(mainargs);
  halt(ret);
}
