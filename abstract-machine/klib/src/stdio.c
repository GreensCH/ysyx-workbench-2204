#include <am.h>
#include <klib.h>
#include <klib-macros.h>
#include <stdarg.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

typedef long long ll;

char* num2str(char *str, ll num, ll base) {
  char tmp[32];
  if (num < 0) {
    *str++ = '-';
    num = -num;
  }
  int len = 0;
  if (num == 0) tmp[len++] = 0;
  else while (num) {
    tmp[len++] = num % base;
    num = num / base;
  }
  while (len-- > 0) {
    if (tmp[len] < 10) *str++ = tmp[len] + '0';
    else *str++ = tmp[len] - 10 + 'A';
  }
  return str;
}

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

int printf(const char *fmt, ...) {

  va_list valist;
  va_start(valist, fmt);

  int buf[8];
  int i;
  unsigned long long int tens ;
  int allbits;
  int bits;
  int integer;
  int zerobits;
  int k;
  char chnum;

  while( *fmt ) {
    if( *fmt == '%') {
      const char *ret = fmt + 1 ;

      switch ( *ret ) {

        case '0' : 
        case '1' :
        case '2' :
        case '3' :
        case '4' :
        case '5' :
        case '6' :
        case '7' :
        case '8' :
        case '9' :
                    i = 0 ;
                    tens = 1;
                    allbits = 0;
                    while( *ret>= '0' && *ret <= '9') {
                      buf[i] = *ret - '0' ;
                      i++ ;
                      ret ++ ;
                    }
                    while(i--) {
                      allbits = buf[i] * tens + allbits ;
                      tens = tens * 10;
                    }
                    switch ( *ret ) {
                      case 'd' :
                                bits=0;
                                tens= 1;
                                integer = va_arg(valist, int);
                                while( integer / (tens) != 0 ) {
                                  bits++;
                                  tens = tens * 10 ;
                                }
                                tens = tens / 10;
                                zerobits = allbits - bits;
                                for(k=0; k<zerobits; k++) {
                                  putch('0');

                                }
                                for( k=0; k<bits; k++ ) {
                                  char num = (char)(integer / tens + '0' ); 
                                  integer = integer % tens ;
                                  putch(num);
                                  tens = tens / 10;
                                }
                                fmt = ret ;
                                break;

                      case 'x' : break ;
                      default  : break ;

                    }
                    break;

        case 'c' : {
                  char ch = va_arg(valist, int);
                   putch(ch);
                   fmt = ret ;
                   break;}
        case 's' : {char *pc = va_arg(valist, char *);
                   while(*pc) {
                     putch(*pc);
                     pc++;
                   }  
                   fmt = ret ;
                   break;}
        case 'd' : bits=0ull;
                   tens= 1ull;
                   integer = va_arg(valist, unsigned long long );
                   if( integer == 0ull ) {
                     putch('0');
                   }
                   else {
                   while( integer / (tens) != 0ull ) {
                     bits++;
                     tens = tens * 10ull ;
                   }
                   tens = tens / 10ull;
                   for( int k=0; k<bits; k++ ) {
                     char num = (char)(integer / tens + '0' ); 
                     putch(num);
                     integer = integer % tens ;
                     tens = tens / 10ull;
                   }
                   }
                   fmt = ret ;
                   break;

        //case 'p' :
        case 'x' : bits=0;
                   tens= 1ull;
                   integer = va_arg(valist, unsigned int);
                   while( (integer / tens) != 0 ) {
                     bits++;
                     tens = tens * 16 ;
                   }
                   tens = tens / 16;
                   for( k=0; k<bits; k++ ) {
                     unsigned int num = (unsigned)integer/(unsigned)tens;
                     if( num >= 10 ) {
                       chnum = (char)( num + 'a' -10 ); 
                     }
                     else {
                       chnum = (char)( num + '0' ); 
                     }
                     putch(chnum);
                     integer = integer % tens ;
                     tens = tens / 16;
                   }
                   fmt = ret ;
                   break;

        case 'u' : bits=0;
                   unsigned int tens= 1;
                   unsigned int integer = va_arg(valist, unsigned int);
                   while( integer / (tens) != 0 ) {
                     bits++;
                     tens = tens * 10 ;
                   }
                   tens = tens / 10;
                   for( k=0; k<bits; k++ ) {
                     char num = (char)(integer / tens + '0' ); 
                     integer = integer % tens ;
                     putch(num);
                     tens = tens / 10;
                   }
                   fmt = ret ;
                   break;

        default : putch(*fmt); fmt++; break;

      }
    }
    else {
      putch(*fmt);
    }
    fmt ++ ;

  }
}

int sprintf(char *out, const char *fmt, ...) {
  int n;
  va_list args;
  va_start(args, fmt);
  n = vsprintf(out, fmt, args);
  va_end(args);
  return n;
}

int vsprintf(char *out, const char *fmt, va_list ap) {
  char *s;
  char *str;
  for (str = out; *fmt; fmt++) {
    if (*fmt != '%') {
      *str++ = *fmt;
      continue;
    }
    fmt++;
    switch (*fmt) {
      case 'c':
        *str++ = va_arg(ap, int);
        break;
      case 'd':
        // cannot handle %02d etc. maybe fix future.
        str = num2str(str, va_arg(ap, ll), 10);
        break;
      case 'p':
        str = num2str(str, va_arg(ap, ll), 16);
        break;
      case 's':
        s = va_arg(ap, char *);
        while (*s) *str++ = *s++;
        break;
      default:
        if (*fmt != '%') *str++ = '%';
        if (*fmt) *str++ = *fmt;
        else fmt--;
        break;
    }
  }
  *str = '\0';
  return str - out;
}

int snprintf(char *out, size_t n, const char *fmt, ...) {
  panic("Not implemented");
}

int vsnprintf(char *out, size_t n, const char *fmt, va_list ap) {
  panic("Not implemented");
}

#endif