#include <common.h>
#include "syscall.h"
void do_syscall(Context *c) {
  uintptr_t a[4];
  a[0] = c->GPR1;

  switch (a[0]) {
    // case SYS_exit:
    //   halt(0);
    // break;
    case SYS_yield:
      //dummy程序, 它触发了一个SYS_yield系统调用. 我们约定, 这个系统调用直接调用CTE的yield()即可, 然后返回0
      yield();
      c->GPRx = 0;
      //处理系统调用的最后一件事就是设置系统调用的返回值. 
      //对于不同的ISA, 系统调用的返回值存放在不同的寄存器中, 
      //宏GPRx用于实现这一抽象, 所以我们通过GPRx来进行设置系统调用返回值即可.
    break;

    default: panic("Unhandled syscall ID = %d", a[0]);
  }
}
