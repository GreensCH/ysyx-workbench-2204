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
  int cnt = 0;
  for (int i = 0; fmt[i]; i++)
  {
    if (fmt[i] != '%')
    {
      out[cnt++] = fmt[i];
      continue;
    }
    int num = 0, num_b_cnt = 0;
    int num_b[20] = {0};
    char *str = NULL;
    char chr;
    switch (fmt[i + 1])
    {
    case 'd':
      num_b_cnt = 0;
      num = va_arg(ap, int);
      if (num == 0)
        num_b[++num_b_cnt] = 0;
      else if (num < 0)
        out[cnt++] = '-', num = -num;
      while (num != 0)
      {
        num_b[++num_b_cnt] = num % 10;
        num /= 10;
      }
      for (int i = num_b_cnt; i >= 1; i--)
        out[cnt++] = (char)(num_b[i] + '0');
      break;
    case 's':
      str = va_arg(ap, char *);
      for (int i = 0; str[i]; i++)
        out[cnt++] = str[i];
      break;
    case 'c':
      chr = va_arg(ap, int);
      out[cnt++] = chr;
      break;
    default:
      break;
    }
    i++;
  }
  out[cnt++] = '\0';
  return cnt;
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
