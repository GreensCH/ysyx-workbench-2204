#include <am.h>
#include <nemu.h>

#define SYNC_ADDR (VGACTL_ADDR + 4)

#include <klib.h>

uint32_t vgaw = 0, vgah = 0;  

void __am_gpu_init() {
  uint32_t vgactl = inl(VGACTL_ADDR);
  vgaw = 800;//vgactl >> 16;
  vgah = 600;//vgactl & 0xFFFF;
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
  int j;
  int i;
  int cnt = 0;
  int t = ctl->y * vgaw + ctl->x;
  for(j = 0; j < ctl->h; j++){
    for (i = 0; i < ctl->w; i++){
      fb[t] = ((uint32_t*)ctl->pixels)[++cnt];
      t += 1;
    }
    t += (vgaw - ctl->w);
  }
  //ctl am gpu  --> small
  //fb  vga mem --> big 
  if (ctl->sync) {//同步寄存器
    outl(SYNC_ADDR, 1);
  }
}

void __am_gpu_status(AM_GPU_STATUS_T *status) {
  status->ready = true;
}
