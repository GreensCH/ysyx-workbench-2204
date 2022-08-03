#include <stdio.h>
#include <NDL.h>


int main() {
  NDL_Init(0);
  while(1){
    printf("time%d\n",NDL_GetTicks()/1000000);
  }
  NDL_Quit();
  return 0;
}


// #include <assert.h>
// #include <time.h>
// #include <sys/time.h>
// int main() {
//   struct timeval  tv;
//   struct timezone tz;
//   gettimeofday(&tv, &tz);
//   static int old = 0;
//   while(1){
//     gettimeofday(&tv, &tz);
//     // printf("current sec  %d\n",tv.tv_sec);
//     // printf("current usec %d\n",tv.tv_usec);

//     if(old != tv.tv_sec){
//         old = tv.tv_sec;
//         printf("current second %d\n",tv.tv_sec);
//     }
//   }
//   return 0;
// }
