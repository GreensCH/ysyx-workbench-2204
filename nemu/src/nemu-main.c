#include <common.h>

void init_monitor(int, char *[]);
void am_init_monitor();
void engine_start();
int is_exit_status_bad();

static word_t test1;

void bprint(int p){
        int i=0;
        int bit1 = 1;
        for (i=sizeof (p) * 8-1;i>=0; i--){
                unsigned int x = (((bit1<<i)&p) !=0);
                printf("%d", x);
        }

        printf("\n");
}


int main(int argc, char *argv[]) {
  /* Initialize the monitor. */
#ifdef CONFIG_TARGET_AM
  am_init_monitor();
#else
  init_monitor(argc, argv);
#endif

  word_t b = BITS(0b1001011,3,0);
  test1 = SEXT(b,27);

  printf("bits:0b");bprint(b);
  printf("sext:0x");bprint(test1);
  /* Start engine. */
  engine_start();

  return is_exit_status_bad();
}
