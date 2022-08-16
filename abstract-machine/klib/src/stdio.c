#include <am.h>
#include <klib.h>
#include <klib-macros.h>
#include <stdarg.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

//第一个参数是要转换的数字，第二个参数是要写入转换结果的目标字符串，第三个参数是转移数字时所用的基数，10：十进制；2：二进制...
char* itoa(int num,char* str,int radix)
{/*索引表*/
    char index[]="0123456789ABCDEF";
    unsigned unum;/*中间变量*/
    int i=0,j,k;
    /*确定unum的值*/
    if(radix==10&&num<0)/*十进制负数*/
    {
        unum=(unsigned)-num;
        str[i++]='-';
    }
    else unum=(unsigned)num;/*其他情况*/
    /*转换*/
    do{
        str[i++]=index[unum%(unsigned)radix];
        unum/=radix;
       }while(unum);
    str[i]='\0';
    /*逆序*/
    if(str[0]=='-')
        k=1;/*十进制负数*/
    else
        k=0;
     
    for(j=k;j<=(i-1)/2;j++)
    {       char temp;
        temp=str[j];
        str[j]=str[i-1+k-j];
        str[i-1+k-j]=temp;
    }
    return str;
}


int vprintf(const char *fmt, va_list ap)
{
  panic("Not implemented");
}

int printf(const char *fmt, ...)
{
  panic("Not implemented");
}

int vsprintf(char *out, const char *fmt, va_list ap)
{
  int d;
  char c;
  char *s;
  const char *sfmt = fmt;
  char *sout = out;

  while (*sfmt)
  {
    if(*sfmt != '%')
    {
      *sout = *sfmt;
      sfmt++;
      sout++;
      continue;
    }
    switch (*(sfmt+1)) //sfmt = %; sfmt+1 = d; sfmt+2 =?
    {
      case 's':/* string */
        s = va_arg(ap, char *);
        strcpy(sout, s);
		    sout += strlen(s);
        break;
      case 'd':/* int */
        d = va_arg(ap, int);
        char szd[32];
        itoa(d, szd, 10);
        strcpy(sout, szd);
		    sout += strlen(szd);
        break;
      case 'c':/* char */
        /* need a cast here since va_arg only takes fully promoted types */
        c = (char) va_arg(ap, int);
        *sout = c;
		    sout ++;
        break;
    }
    /* 转移sfmt指针 */
    sfmt += 2;
  }
  *sout = '\0';
  return sout - out;
}//2,773 us

int sprintf(char *out, const char *fmt, ...)
{
  va_list args;
  va_start(args, fmt);
  int len = vsprintf(out, fmt, args);
  va_end(args);
  return len;
}

int snprintf(char *out, size_t n, const char *fmt, ...) 
{
  panic("Not implemented");
}

int vsnprintf(char *out, size_t n, const char *fmt, va_list ap) 
{
  panic("Not implemented");
}

#endif

// #include <am.h>
// #include <klib.h>
// #include <klib-macros.h>
// #include <stdarg.h>

// #if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

// typedef long long ll;

// char* num2str(char *str, ll num, ll base) {
//   char tmp[32];
//   if (num < 0) {
//     *str++ = '-';
//     num = -num;
//   }
//   int len = 0;
//   if (num == 0) tmp[len++] = 0;
//   else while (num) {
//     tmp[len++] = num % base;
//     num = num / base;
//   }
//   while (len-- > 0) {
//     if (tmp[len] < 10) *str++ = tmp[len] + '0';
//     else *str++ = tmp[len] - 10 + 'A';
//   }
//   return str;
// }

// int printf(const char *fmt, ...) {
//   int n;
//   char buf[8192];
//   va_list args;
//   va_start(args, fmt);
//   n = vsprintf(buf, fmt, args);
//   va_end(args);
//   putstr(buf);
//   return n;
// }

// int sprintf(char *out, const char *fmt, ...) {
//   int n;
//   va_list args;
//   va_start(args, fmt);
//   n = vsprintf(out, fmt, args);
//   va_end(args);
//   return n;
// }

// int vsprintf(char *out, const char *fmt, va_list ap) {
//   char *s;
//   char *str;
//   for (str = out; *fmt; fmt++) {
//     if (*fmt != '%') {
//       *str++ = *fmt;
//       continue;
//     }
//     fmt++;
//     switch (*fmt) {
//       case 'c':
//         *str++ = va_arg(ap, int);
//         break;
//       case 'd':
//         // cannot handle %02d etc. maybe fix future.
//         str = num2str(str, va_arg(ap, ll), 10);
//         break;
//       case 'p':
//         str = num2str(str, va_arg(ap, ll), 16);
//         break;
//       case 's':
//         s = va_arg(ap, char *);
//         while (*s) *str++ = *s++;
//         break;
//       default:
//         if (*fmt != '%') *str++ = '%';
//         if (*fmt) *str++ = *fmt;
//         else fmt--;
//         break;
//     }
//   }
//   *str = '\0';
//   return str - out;
// }

// int snprintf(char *out, size_t n, const char *fmt, ...) {
//   panic("Not implemented");
// }

// int vsnprintf(char *out, size_t n, const char *fmt, va_list ap) {
//   panic("Not implemented");
// }

// #endif