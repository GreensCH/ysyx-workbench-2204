#include <klib.h>
#include <klib-macros.h>
#include <stdint.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

size_t strlen(const char *s) {
  size_t len = 0;
  while(s[len])
    len++;
  return len;
}

char *strcpy(char *dst, const char *src) {
  char *p = dst;
  while(*src != '\0')
  {
    *p++ = *src++;
  }
  return dst;
}

char *strncpy(char *dst, const char *src, size_t n) {
  char *p = dst;
  while(n--)
  {
    if(*src != '\0')
      *p++ = *src++;
    else
      *p++ = '\0';
  }
  return dst;
}

char *strcat(char *dst, const char *src) {
  char *p = dst;
  if(*src == '\0')
    return dst;
  while(*p != '\0')
    p++;
  while(*src != '\0')
    *p++ = *src++;
  return dst;
}

/*
 * 如果返回值小于 0，则表示 str1 小于 str2。
 * 如果返回值大于 0，则表示 str1 大于 str2。
 * 如果返回值等于 0，则表示 str1 等于 str2。
*/

int strcmp(const char *s1, const char *s2) {
  while(*s1 != '\0' && *s2 !='\0' )
  {
    if(*s1 > *s2)
      return 1;
    else if(*s1 < *s2)
      return -1;
    s1++;
    s2++;
  }
  if(*s1 > *s2)
    return 1;
  else if(*s1 < *s2)
    return -1;
  else 
    return 0;
}

int strncmp(const char *s1, const char *s2, size_t n) {
  while(*s1 != '\0' && *s2 !='\0' && --n)
  {
    if(*s1 > *s2)
      return 1;
    else if(*s1 < *s2)
      return -1;
    s1++;
    s2++;
  }
  if(*s1 > *s2)
    return 1;
  else if(*s1 < *s2)
    return -1;
  else 
    return 0;
}

void *memset(void *s, int c, size_t n)
{
  if (s == NULL || n < 0)
    return NULL;

  void *tmp = s;
  char *str = s;
  for (int i = 0; i < n; i++)
    str[i] = c;
  return tmp;
}

void *memmove(void *dst, const void *src, size_t n)
{
  if (dst == NULL || n < 0 || src == NULL)
    return NULL;

  char *str_dst = dst;
  const char *str_src = src;
  if (str_dst > str_src && str_src + n > str_dst)
  {
    for (int i = n - 1; i >= 0; i--)
      str_dst[i] = str_src[i];
  }
  else
  {
    for (int i = 0; i < n; i++)
      str_dst[i] = str_src[i];
  }
  return dst;
}

void *memcpy(void *out, const void *in, size_t n)
{
  if (out == NULL || n < 0 || in == NULL)
    return NULL;

  char *str_out = out;
  const char *str_in = in;
  for (int i = 0; i < n; i++)
    str_out[i] = str_in[i];
  return out;
}

int memcmp(const void *s1, const void *s2, size_t n)
{
  if (s1 == NULL || n < 0 || s2 == NULL)
    return -1;
  const char *str_s1 = s1;
  const char *str_s2 = s2;

  for (int i = 0; i < n; i++)
  {
    if (str_s1[i] > str_s2[i])
      return 1;
    else if (str_s1[i] < str_s2[i])
      return -1;
  }
  return 0;
}

#endif