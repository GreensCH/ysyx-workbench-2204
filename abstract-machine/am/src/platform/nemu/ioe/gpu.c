#include <am.h>
#include <nemu.h>

#define SYNC_ADDR (VGACTL_ADDR + 4)

#include <klib.h>

uint32_t vgaw = 0, vgah = 0;  

void __am_gpu_init() {
  uint32_t vgactl = inl(VGACTL_ADDR);
  vgaw = vgactl >> 16;
  vgah = vgactl & 0xFFFF;
  // int i;
  // uint32_t *fb = (uint32_t *)(uintptr_t)FB_ADDR;
  // for (i = 0; i < w * h; i ++) fb[i] = i;
  // outl(SYNC_ADDR, 1);
}

void __am_gpu_config(AM_GPU_CONFIG_T *cfg) {
  uint32_t vgactl = inl(VGACTL_ADDR);
  vgaw = vgactl >> 16;
  vgah = vgactl & 0xFFFF;
  *cfg = (AM_GPU_CONFIG_T) {
    .present = true, .has_accel = false,
    .width = vgaw, .height = vgah,
    .vmemsz = 0
  };
}

void __am_gpu_fbdraw(AM_GPU_FBDRAW_T *ctl) {//屏幕大小寄存器
  uint32_t *fb = (uint32_t *)(uintptr_t)FB_ADDR;
  int i, j;
  int cnt = 0;
  for(j = 0; j < ctl->h; j ++){
    for (i = 0; i < ctl->w; i ++){
      int p = ctl->y + j;
      int q = ctl->x + i;
      fb[p * vgaw + q] = ((uint32_t*)ctl->pixels)[++cnt];
    }
  }
  printf("%d %d\n",ctl->h ,ctl->w);
  //ctl am gpu
  //fb  vga mem 
  if (ctl->sync) {//同步寄存器
    outl(SYNC_ADDR, 1);
  }
}

void __am_gpu_status(AM_GPU_STATUS_T *status) {
  status->ready = true;
}
