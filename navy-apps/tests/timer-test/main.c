#include <stdio.h>
#include <assert.h>
#include <time.h>
#include <sys/time.h>


int main() {
  struct timeval  tv;
  struct timezone tz;
  gettimeofday(&tv, &tz);
  while(1){
  gettimeofday(&tv, &tz);
  printf("current second %d\n",tv.tv_sec);
    // int old = 0;
    // if(old != tv.tv_sec){
    //   old = tv.tv_sec;
    //   printf("current second %d\n",tv.tv_usec);
    // }
  }
  return 0;
}
