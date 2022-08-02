#include <common.h>
#include "syscall.h"
#include "fs.h"

void do_syscall(Context *c) {
  uintptr_t a[4];
  a[0] = c->GPR1;
  a[1] = c->GPR2;
  a[2] = c->GPR3;
  a[3] = c->GPR4;
  //GPRx 返回值为 0 表示成功。 -1 返回值 表示错误

  switch (a[0]) {
    case SYS_exit:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_exit.");
      #endif
      halt(0);
    break;
    case SYS_yield:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_yield.");
      #endif
      //dummy程序, 它触发了一个SYS_yield系统调用. 我们约定, 这个系统调用直接调用CTE的yield()即可, 然后返回0
      yield();
      c->GPRx = 0;
      //处理系统调用的最后一件事就是设置系统调用的返回值. 
      //对于不同的ISA, 系统调用的返回值存放在不同的寄存器中, 
      //宏GPRx用于实现这一抽象, 所以我们通过GPRx来进行设置系统调用返回值即可.
    break;
    case SYS_write:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_write.");
      #endif
      if(a[1] == 1 || a[1] == 2){
        for (int i = 0; i < a[3]; ++i) {
          putch(*(char*)(a[2] + i));
        }
        c->GPRx = a[3];
      }
      else
        c->GPRx = -1;
    break;
    case SYS_brk:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_brk.");
      #endif
      c->GPRx = 0;//单任务操作系统, 空闲的内存都可以让用户程序自由使用, 因此我们只需要让SYS_brk系统调用总是返回0
    break;
    case SYS_open:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_open.");
      #endif
      c->GPRx = fs_open(a[0], a[1], a[2]);
    break;
    case SYS_read:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_read.");
      #endif
      c->GPRx = fs_read(a[0], a[1], a[2]);
    break;
    case SYS_close:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_close.");
      #endif
      c->GPRx = fs_close(a[0]);
    break;
    
    default: panic("Unhandled syscall ID = %d", a[0]);
  }
}

// static inline uintptr_t sys_open(uintptr_t pathname, uintptr_t flags, uintptr_t mode) {  
//   return fs_open((char *)pathname,flags,mode);
// }
// static inline uintptr_t sys_write(uintptr_t fd, uintptr_t buf, uintptr_t len) {  
//   return fs_write(fd,(void *)buf,len);
// }