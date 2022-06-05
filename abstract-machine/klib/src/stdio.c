#include <am.h>
#include <klib.h>
#include <klib-macros.h>
#include <stdarg.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

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
