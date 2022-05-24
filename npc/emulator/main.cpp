#include "include.h"

#include "include.h"

static inline word_t host_read(void *addr, int len) {
  switch (len) {
    case 1: return *(uint8_t  *)addr;
    case 2: return *(uint16_t *)addr;
    case 4: return *(uint32_t *)addr;
    case 8: return *(uint64_t *)addr;
    default: assert(0);
  }
}

static inline void host_write(void *addr, int len, word_t data) {
  switch (len) {
    case 1: *(uint8_t  *)addr = data; return;
    case 2: *(uint16_t *)addr = data; return;
    case 4: *(uint32_t *)addr = data; return;
    case 8: *(uint64_t *)addr = data; return;
    default: assert(0);
  }
}

#define CONFIG_MSIZE 0x8000000
#define CONFIG_MBASE 0x80000000

#define PG_ALIGN __attribute((aligned(4096)))
static uint8_t pmem[CONFIG_MSIZE] PG_ALIGN = {};

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t *haddr) { return haddr - pmem + CONFIG_MBASE; }

extern "C" word_t pmem_read(paddr_t addr, int len) {
  word_t ret = host_read(guest_to_host(addr), len);
  return ret;
}

extern "C" void pmem_write(paddr_t addr, int len, word_t data) {
  host_write(guest_to_host(addr), len, data);
}

int main(int argc, char** argv, char** env) {
    VerilatedContext* contextp = new VerilatedContext;
    contextp->commandArgs(argc, argv);
    VTop* top = new VTop{contextp};
    printf("hello world\n");

    // while (!contextp->gotFinish()) {
    //   top->eval();
    //   if(argv[i] != NULL){
    //     printf("******%s\n", argv[i]);
    //     i++;  
    //   }
    //   else
    //     break;
    // }
    delete top;
    delete contextp;
    return 0;
}


// static long load_img() {
//   if (img_file == NULL) {
//     Log("No image is given. Use the default build-in image.");
//     return 4096; // built-in image size
//   }

//   FILE *fp = fopen(img_file, "rb");
//   Assert(fp, "Can not open '%s'", img_file);

//   fseek(fp, 0, SEEK_END);
//   long size = ftell(fp);

//   Log("The image is %s, size = %ld", img_file, size);

//   fseek(fp, 0, SEEK_SET);
//   int ret = fread(guest_to_host(RESET_VECTOR), size, 1, fp);
//   assert(ret == 1);

//   fclose(fp);
//   return size;
// }