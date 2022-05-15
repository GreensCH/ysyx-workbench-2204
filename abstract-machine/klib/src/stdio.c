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
  int len = 0 ;
  const char *sfmt = fmt;

  while (*sfmt)
  {
    if(*sfmt != '%'){
      *(out+len) = *sfmt;
      sfmt++;
      len++;
      continue;
    }
    switch (*(sfmt+1)) //sfmt = %; sfmt+1 = d; sfmt+2 =?
    {
      case 's':              /* string */
        s = va_arg(ap, char *);
        strcat(out, s);
        break;
      case 'd':              /* int */
        d = va_arg(ap, int);
        char szd[32];
        itoa(d, szd, 10);
        strcat(out, szd);
        break;
      case 'c':              /* char */
        /* need a cast here since va_arg only takes fully promoted types */
        c = (char) va_arg(ap, int);
        *(out+len) = c;
        break;
    }
    /* 转移sfmt指针 */
    memmove((void *)sfmt, sfmt+2, 2);
    len += 2;
  }
  return strlen(out);
}

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
