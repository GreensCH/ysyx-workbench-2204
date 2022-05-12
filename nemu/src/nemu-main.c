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

  word_t b = BITS(0b1011111001011,62,0);
  word_t i = SEXT(BITS(b, 31, 31), 1) << 20 | BITS(b, 19, 12) << 12 | BITS(b, 20, 20) << 11 | BITS(b, 30, 21) << 1 ;
  printf("bits:0b");bprint(i);
  test1 = SEXT(i,12);
  printf("sext12:0x");bprint(test1);
  test1 = SEXT(i,13);
  printf("sext13:0x");bprint(test1);
  /* Start engine. */

  engine_start();

  return is_exit_status_bad();
}
