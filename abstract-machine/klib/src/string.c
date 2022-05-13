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
  while(*src != '\0'){
    *p++ = *src++;
  }
  return dst;
}

char *strncpy(char *dst, const char *src, size_t n) {
  char *p = dst;
  while(n--){
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
  while(*p != '\0'){
    p++;
  }
  while(*src != '\0'){
    *p++ = *src++;
  }
  return dst;
}

int strcmp(const char *s1, const char *s2) {
  panic("Not implemented");
}

int strncmp(const char *s1, const char *s2, size_t n) {
  panic("Not implemented");
}

void *memset(void *s, int c, size_t n) {
  panic("Not implemented");
}

void *memmove(void *dst, const void *src, size_t n) {
  panic("Not implemented");
}

void *memcpy(void *out, const void *in, size_t n) {
  panic("Not implemented");
}

int memcmp(const void *s1, const void *s2, size_t n) {
  panic("Not implemented");
}

#endif
