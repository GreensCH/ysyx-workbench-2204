#include <am.h>
#include <nemu.h>

#define SYNC_ADDR (VGACTL_ADDR + 4)

#include <klib.h>

uint32_t w = 0, h = 0;  

void __am_gpu_init() {
  uint32_t vgactl = inl(VGACTL_ADDR);
  w = vgactl >> 16;
  h = vgactl & 0xFFFF;
  // int i;
  // uint32_t *fb = (uint32_t *)(uintptr_t)FB_ADDR;
  // for (i = 0; i < w * h; i ++) fb[i] = i;
  // outl(SYNC_ADDR, 1);
}

void __am_gpu_config(AM_GPU_CONFIG_T *cfg) {
  uint32_t vgactl = inl(VGACTL_ADDR);//屏幕大小寄存器
  w = vgactl >> 16;
  h = vgactl & 0xFFFF;
  *cfg = (AM_GPU_CONFIG_T) {
    .present = true, .has_accel = false,
    .width = w, .height = h,
    .vmemsz = 0
  };
}

void __am_gpu_fbdraw(AM_GPU_FBDRAW_T *ctl) {
  uint32_t *fb = (uint32_t *)(uintptr_t)FB_ADDR;
  int i;
  for (i = 0; i < w * h; i ++) 
    fb[i] = ((uint32_t*)ctl->pixels)[i + 1];
  if (ctl->sync) {//同步寄存器
    outl(SYNC_ADDR, 1);
  }
}

void __am_gpu_status(AM_GPU_STATUS_T *status) {
  status->ready = true;
}
