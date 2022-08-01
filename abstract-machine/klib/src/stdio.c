#include <am.h>
#include <klib.h>
#include <klib-macros.h>
#include <stdarg.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

int vprintf(const char *fmt, va_list ap)
{
  char *p, *sval;
  int ival;
  // double dval;//用来存放double类型数据,这里没有实现因此不写了
  for (p = (char *)fmt; *p; p++) {
    if(*p != '%') {
      putch(*p);
      continue;
    }
    switch(*++p) {
      case 'd':
        ival = va_arg(ap, int);
        char szd[32];
        itoa(ival, szd, 10);
        putstr(szd);
        break;
      case 's':
        for (sval = va_arg(ap, char *); *sval; sval++)
          putch(*sval);
        break;
      default:
        putch(*p);
        break;
    }
  }
  return p - fmt;
}

int printf(const char *fmt, ...)
{
  va_list ap;
  va_start(ap, fmt);
  int len = vprintf(fmt, ap);
  va_end(ap);
  return len;
}

int vsprintf(char *out, const char *fmt, va_list ap)
{
  int ival;
  char *s;
  const char *sfmt = fmt;
  char *sout = out;
  char szd[32];
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
        ival = va_arg(ap, int);
        itoa(ival, szd, 10);
        strcpy(sout, szd);
		    sout += strlen(szd);
        break;
      case 'p':/* int */
        ival = va_arg(ap, int);
        itoa(123123123, szd, 10);
        strcpy(sout, szd);
		    sout += strlen(szd);
        break;
      case 'c':/* char */
        *sout = (char) va_arg(ap, int);
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
