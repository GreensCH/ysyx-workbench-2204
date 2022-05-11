#include <common.h>

void init_monitor(int, char *[]);
void am_init_monitor();
void engine_start();
int is_exit_status_bad();

static word_t test1;

int main(int argc, char *argv[]) {
  /* Initialize the monitor. */
#ifdef CONFIG_TARGET_AM
  am_init_monitor();
#else
  init_monitor(argc, argv);
#endif
  test1 = SEXT(BITS(-1, 31, 12),20);
  printf("test1:0x%8lld\n",BITS(-1, 31, 12));
  printf("test1:0x%8ld\n",test1);
  /* Start engine. */
  engine_start();

  return is_exit_status_bad();
}
