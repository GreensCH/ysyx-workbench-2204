#include <common.h>

#if defined(MULTIPROGRAM) && !defined(TIME_SHARING)
# define MULTIPROGRAM_YIELD() yield()
#else
# define MULTIPROGRAM_YIELD()
#endif

#define NAME(key) \
  [AM_KEY_##key] = #key,

static const char *keyname[256] __attribute__((used)) = {
  [AM_KEY_NONE] = "NONE",
  AM_KEYS(NAME)
};

size_t serial_write(const void *buf, size_t offset, size_t len) {//对应的字节序列没有"位置"的概念, 因此serial_write()中的offset参数可以忽略
  for (int i = 0; i < len; i ++) {
    putch( ((char *)buf)[i] );
  }
  return len;
}

size_t events_read(const void *buf, size_t offset, size_t len) {
  // int key = 0;// _read_key();
  // char keydown_char = (key & 0x8000 ? 'd' : 'u');
  // key &= ~0x8000;
  // if (key != AM_KEY_NONE) {
  //   return snprintf(buf, len, "k%c %s\n", keydown_char, keyname[key]) - 1;
  // }
  // else {
  //   unsigned long time_ms = _uptime();
  //   return snprintf(buf, len, "t %d\n", time_ms) - 1;
  // }
}

size_t dispinfo_read(void *buf, size_t offset, size_t len) {
  return 0;
}

size_t fb_write(const void *buf, size_t offset, size_t len) {
  return 0;
}

void init_device() {
  Log("Initializing devices...");
  ioe_init();
}
