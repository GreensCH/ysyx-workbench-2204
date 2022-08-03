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

size_t events_read(void *buf, size_t offset, size_t len) {
  AM_INPUT_KEYBRD_T ev = io_read(AM_INPUT_KEYBRD);
  int keycode = ev.keycode;
  char keydown_char = (ev.keydown ? 'd' : 'u');
  if (keycode != AM_KEY_NONE) {
    return sprintf(buf, "k%c %s\n", keydown_char, keyname[keycode]) - 1;
    // return snprintf(buf, len, "k%c %s\n", keydown_char, keyname[keycode]) - 1; // snprintf not implemented
  }else{
    return 0;
  }
}

size_t dispinfo_read(void *buf, size_t offset, size_t len) {

  if (offset > 0){
    return 0;
  }
  int w = io_read(AM_GPU_CONFIG).width;
  int h = io_read(AM_GPU_CONFIG).height;
  int ret = sprintf(buf, "WIDTH:%d HEIGHT:%d", w, h);
  Log("%s", (char *)buf);
  if (ret >= len){
    assert(0);
  }
  return ret + 1;
}

size_t fb_write(const void *buf, size_t offset, size_t len) {
  uintptr_t *ptr;
  ptr = (uintptr_t *)(&buf);

  io_write(AM_GPU_MEMCPY, offset, (void *)*ptr, len);
  // io_write(AM_GPU_FBDRAW, 0, 0, NULL, 0, 0, true);
  
  return len;
}

void init_device() {
  Log("Initializing devices...");
  ioe_init();
}
