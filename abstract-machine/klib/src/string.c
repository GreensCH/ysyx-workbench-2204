/*
 *  string.c
 *
 *  Copyright (C) 1991, 1992  Linus Torvalds
 */
#include <klib.h>
#include <klib-macros.h>
#include <stdint.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

size_t strlen(const char *s) 
{
	const char *sc;

	for (sc = s; *sc != '\0'; ++sc)
		/* nothing */;
	return sc - s;
}

char *strcpy(char *dst, const char *src) 
{
	// char *tmp = dst;

	// while ((*dst++ = *src++) != '\0')
	// 	/* nothing */;
	// return tmp;
}

char *strncpy(char *dst, const char *src, size_t n) 
{
	char *tmp = dst;

	while (n) {
		if ((*tmp = *src) != 0)
			src++;
		tmp++;
		n--;
	}
	return dst;
}

char *strcat(char *dst, const char *src) 
{
	char *tmp = dst;

	while (*dst)
		dst++;
	while ((*dst++ = *src++) != '\0')
		;
	return tmp;
}

/*
 * 如果返回值小于 0，则表示 str1 小于 str2。
 * 如果返回值大于 0，则表示 str1 大于 str2。
 * 如果返回值等于 0，则表示 str1 等于 str2。
*/

int strcmp(const char *s1, const char *s2) 
{
	unsigned char c1, c2;

	while (1) {
		c1 = *s1++;
		c2 = *s2++;
		if (c1 != c2)
			return c1 < c2 ? -1 : 1;
		if (!c1)
			break;
	}
	return 0;
}

int strncmp(const char *s1, const char *s2, size_t n) 
{
	unsigned char c1, c2;

	while (n) {
		c1 = *s1++;
		c2 = *s2++;
		if (c1 != c2)
			return c1 < c2 ? -1 : 1;
		if (!c1)
			break;
		n--;
	}
	return 0;
}

void *memset(void *s, int c, size_t n)
{
	char *xs = s;

	while (n--)
		*xs++ = c;
	return s;
}

void *memmove(void *dst, const void *src, size_t n)
{
	char *tmp;
	const char *s;

	if (dst <= src) {
		tmp = dst;
		s = src;
		while (n--)
			*tmp++ = *s++;
	} else {
		tmp = dst;
		tmp += n;
		s = src;
		s += n;
		while (n--)
			*--tmp = *--s;
	}
  return dst;
}

void *memcpy(void *out, const void *in, size_t n)
{
	char *tmp = out;
	const char *s = in;

	while (n--)
		*tmp++ = *s++;
	return out;
}

int memcmp(const void *s1, const void *s2, size_t n)
{
	const unsigned char *su1, *su2;
	int res = 0;

	for (su1 = s1, su2 = s2; 0 < n; ++su1, ++su2, n--)
		if ((res = *su1 - *su2) != 0)
			break;
	return res;
}

#endif