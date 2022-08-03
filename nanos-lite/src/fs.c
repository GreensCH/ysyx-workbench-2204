#include <fs.h>

void ramdisk_read(void *, uint32_t, uint32_t);
void ramdisk_write(const void *, uint32_t, uint32_t);
size_t serial_write(const void *buf, size_t offset, size_t len);
size_t events_read(void *buf, size_t len);
void dispinfo_read(void *buf, size_t offset, size_t len);
void fb_write(const void *buf, size_t offset, size_t len);




typedef size_t (*ReadFn) (void *buf, size_t offset, size_t len);
typedef size_t (*WriteFn) (const void *buf, size_t offset, size_t len);

typedef struct {
  char *name;
  size_t size;
  size_t disk_offset;
  size_t open_offset;
  ReadFn read;
  WriteFn write;
} Finfo;

#define NR_FILES (sizeof(file_table) / sizeof(file_table[0]))

enum {FD_STDIN, FD_STDOUT, FD_STDERR, FD_FB, FD_EVENTS, FD_DISPINFO, FD_NORMAL};

size_t invalid_read(void *buf, size_t offset, size_t len) {
  panic("should not reach here");
  return 0;
}

size_t invalid_write(const void *buf, size_t offset, size_t len) {
  panic("should not reach here");
  return 0;
}

/* This is the information about all files in disk. */
static Finfo file_table[] __attribute__((used)) = {
  [FD_STDIN]  = {"stdin", 0, 0, 0, invalid_read, invalid_write},
  [FD_STDOUT] = {"stdout", 0, 0, 0, invalid_read, serial_write},
  [FD_STDERR] = {"stderr", 0, 0, 0, invalid_read, serial_write},
  [FD_FB]     = {"/dev/fb", 0, 0, 0, invalid_read, fb_write},
  [FD_EVENTS] = {"/dev/events", 0, 0, 0, events_read, invalid_write},
  [FD_DISPINFO] = {"/proc/dispinfo", 128, 0, 0, dispinfo_read, invalid_write},
#include "files.h"
};//当上述的函数指针为NULL时, 表示相应文件是一个普通文件

void init_fs() {
  // TODO: initialize the size of /dev/fb
}

size_t fs_filesz(int fd) {
  return file_table[fd].size;
}

int fs_open(const char *pathname, int flags, int mode) {
  int i;
  for (i = 0; i < NR_FILES; i ++) {
    if (strcmp(file_table[i].name, pathname) == 0) {
      file_table[i].open_offset = 0;
      return i;
    }
  }

  panic("No such file: %s", pathname);
  return -1;
}

size_t fs_read(int fd, void *buf, size_t len) {
  assert(fd > 2);
  Finfo *f = file_table + fd;
  if (fd == FD_EVENTS) {
    return f->read(buf, 0, len);
  }
  int remain_bytes = f->size - f->open_offset;
  int bytes_to_read = (remain_bytes > len ? len : remain_bytes);
  if (fd == FD_DISPINFO) {
    f->read(buf, f->disk_offset + f->open_offset, bytes_to_read);
  }
  else {
    ramdisk_read(buf, f->disk_offset + f->open_offset, bytes_to_read);
  }
  f->open_offset += bytes_to_read;
  return bytes_to_read;
}

size_t fs_write(int fd, const void *buf, size_t len) {
  Finfo *f = file_table + fd;
  int remain_bytes = f->size - f->open_offset;
  int bytes_to_write = (remain_bytes > len ? len : remain_bytes);
  switch (fd) {
    case FD_STDOUT:
    case FD_STDERR:
      f->write(buf, 0, len);
      return len;
    case FD_EVENTS:
      return len;
    case FD_FB:
      f->write(buf, f->open_offset, bytes_to_write);
      break;
    default:
      ramdisk_write(buf, f->disk_offset + f->open_offset, bytes_to_write);
      break;
  }
  f->open_offset += bytes_to_write;
  printf(" open_offset:%d",f->open_offset);
  return bytes_to_write;
}

size_t fs_lseek(int fd, size_t offset, int whence){
  Finfo *f = file_table + fd;
  int new_offset = f->open_offset;
  int file_size = file_table[fd].size;
  switch (whence) {
    case SEEK_CUR: new_offset += offset; break;
    case SEEK_SET: new_offset = offset; break;
    case SEEK_END: new_offset = file_size + offset; break;
    default: assert(0);
  }
  if (new_offset < 0) {
    new_offset = 0;
  }
  else if (new_offset > file_size) {
    new_offset = file_size;
  }
  f->open_offset = new_offset;

  return new_offset;
}

int fs_close(int fd) {
  return 0;
}
// size_t lseek(int fd, size_t offset, int whence); // in navy libos
// int fs_open(const char *pathname, int flags, int mode);
// size_t fs_read(int fd, void *buf, size_t len);
// size_t fs_write(int fd, const void *buf, size_t len);
// size_t fs_lseek(int fd, size_t offset, int whence);
// int fs_close(int fd);