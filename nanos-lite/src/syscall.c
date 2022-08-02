#include <common.h>
#include "syscall.h"
#include "fs.h"

// static inline uintptr_t sys_open(uintptr_t pathname, uintptr_t flags, uintptr_t mode) {
//   TODO();
//   return 1;
// }

// static inline uintptr_t sys_write(uintptr_t fd, uintptr_t buf, uintptr_t len) {
//   TODO();
//   return 1;
// }

// static inline uintptr_t sys_read(uintptr_t fd, uintptr_t buf, uintptr_t len) {
//   TODO();
//   return 1;
// }

// static inline uintptr_t sys_lseek(uintptr_t fd, uintptr_t offset, uintptr_t whence) {
//   return fs_lseek(fd, offset, whence);
// }

// static inline uintptr_t sys_close(uintptr_t fd) {
//   TODO();
//   return 1;
// }

// static inline uintptr_t sys_brk(uintptr_t new_brk) {
//   TODO();
//   return 1;
// }

// struct timeval {
// 	time_t		tv_sec;		/* seconds */
// 	suseconds_t	tv_usec;	/* and microseconds */
// };
// struct timezone {
// 	int	tz_minuteswest;	/* minutes west of Greenwich */
// 	int	tz_dsttime;	/* type of dst correction */
// };

struct timeval {
	uintptr_t		tv_sec;		/* seconds */
	uintptr_t	tv_usec;	/* and microseconds */
};
struct timezone {
	uintptr_t	tz_minuteswest;	/* minutes west of Greenwich */
	uintptr_t	tz_dsttime;	/* type of dst correction */
};
static inline uintptr_t sys_gettimeofday(struct timeval *tv, struct timezone *tz) {
  // #ifdef CONFIG_STRACE
  //   Log("Strace SYS_gettimeofday");
  // #endif
  // printf("********1********\n");
  static AM_TIMER_RTC_T rtc;
  static int sec = 1;
  static int usec = 0;
  // printf("********2********\n");

  usec = io_read(AM_TIMER_UPTIME).us;
  sec = usec/1000000;

  // printf("********3********\n");
  if(usec == -1)  return -1;

  // printf("********4********\n");
  // printf("sec%d,usec%d",);
  tv->tv_sec  = sec;
  tv->tv_usec = usec;
  tz->tz_minuteswest = 0;
  tz->tz_dsttime = 0;
  // printf("********5********\n");
  return 0;
}



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
        Log("Strace SYS_exit");
      #endif
      halt(0);
    break;
    case SYS_yield:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_yield");
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
        Log("Strace SYS_write");
      #endif
      c->GPRx = fs_write(a[1], a[2], a[3]);
    break;
    case SYS_brk:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_brk");
      #endif
      c->GPRx = 0;//单任务操作系统, 空闲的内存都可以让用户程序自由使用, 因此我们只需要让SYS_brk系统调用总是返回0
    break;
    case SYS_open:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_open");
      #endif
      c->GPRx = fs_open(a[1], a[2], a[3]);
    break;
    case SYS_read:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_read");
      #endif
      c->GPRx = fs_read(a[1], a[2], a[3]);
    break;
    case SYS_close:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_close");
      #endif
      c->GPRx = fs_close(a[1]);
    break;
    case SYS_lseek:
      #ifdef CONFIG_STRACE
        Log("Strace SYS_lseek");
      #endif
      c->GPRx = fs_lseek(a[1], a[2], a[3]);
    break;
    case SYS_gettimeofday:
      c->GPRx = sys_gettimeofday(a[1], a[2]);
    break;
    
    default: panic("Unhandled syscall ID = %d", a[0]);
  }
}



// void do_syscall(Context *c) {
//   uintptr_t a[4];
//   a[0] = c->GPR1;
//   a[1] = c->GPR2;
//   a[2] = c->GPR3;
//   a[3] = c->GPR4;
//   //GPRx 返回值为 0 表示成功。 -1 返回值 表示错误

//   switch (a[0]) {
//     case SYS_exit:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_exit.");
//       #endif
//       halt(0);
//     break;
//     case SYS_yield:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_yield.");
//       #endif
//       //dummy程序, 它触发了一个SYS_yield系统调用. 我们约定, 这个系统调用直接调用CTE的yield()即可, 然后返回0
//       yield();
//       c->GPRx = 0;
//       //处理系统调用的最后一件事就是设置系统调用的返回值. 
//       //对于不同的ISA, 系统调用的返回值存放在不同的寄存器中, 
//       //宏GPRx用于实现这一抽象, 所以我们通过GPRx来进行设置系统调用返回值即可.
//     break;
//     case SYS_write:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_write.");
//       #endif
//       if(a[1] == 1 || a[1] == 2){
//         for (int i = 0; i < a[3]; ++i) {
//           putch(*(char*)(a[2] + i));
//         }
//         c->GPRx = a[3];
//       }
//       else
//         c->GPRx = -1;
//     break;
//     case SYS_brk:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_brk.");
//       #endif
//       c->GPRx = 0;//单任务操作系统, 空闲的内存都可以让用户程序自由使用, 因此我们只需要让SYS_brk系统调用总是返回0
//     break;
//     case SYS_open:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_open.");
//       #endif
//       c->GPRx = fs_open(a[1], a[2], a[3]);
//     break;
//     case SYS_read:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_read.");
//       #endif
//       c->GPRx = fs_read(a[1], a[2], a[3]);
//     break;
//     case SYS_close:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_close.");
//       #endif
//       c->GPRx = fs_close(a[1]);
//     break;
//     case SYS_lseek:
//       #ifdef CONFIG_STRACE
//         Log("Strace SYS_lseek.");
//       #endif
//       c->GPRx = fs_lseek(a[1], a[2], a[3]);
//     break;
    
//     default: panic("Unhandled syscall ID = %d", a[0]);
//   }
// }

