#include "include.h"

#include "include.h"

static inline word_t host_read(void *addr, int len) {
  switch (len) {
    case 1: return *(uint8_t  *)addr;
    case 2: return *(uint16_t *)addr;
    case 4: return *(uint32_t *)addr;
    case 8: return *(uint64_t *)addr;
    default: return *(uint64_t *)addr;
    //default: assert(0);
  }
}

static inline void host_write(void *addr, int len, word_t data) {
    return;
//  switch (len) {
//    case 1: *(uint8_t  *)addr = data; return;
//    case 2: *(uint16_t *)addr = data; return;
//    case 4: *(uint32_t *)addr = data; return;
//    case 8: *(uint64_t *)addr = data; return;
//    default: *(uint64_t *)addr = data; return;
//    // default: assert(0);
//  }
}

#define CONFIG_MSIZE 0x8000000
#define CONFIG_MBASE 0x80000000
#define CONFIG_PC_RESET_OFFSET 0x0
#define RESET_VECTOR (CONFIG_MBASE + CONFIG_PC_RESET_OFFSET)

static uint8_t pmem[CONFIG_MSIZE] = {};

uint8_t* guest_to_host(paddr_t paddr) { return pmem + paddr - CONFIG_MBASE; }
paddr_t host_to_guest(uint8_t *haddr) { return haddr - pmem + CONFIG_MBASE; }

extern "C" word_t pmem_read(paddr_t addr, int len) {
  printf("guest addr:%lx, host addr %lx \n",addr, guest_to_host(addr));
  word_t ret = host_read(guest_to_host(addr), len);
  return ret;
}

extern "C" void pmem_write(paddr_t addr, int len, word_t data) {
  host_write(guest_to_host(addr), len, data);
}


vluint64_t main_time = 0;       
double sc_time_stamp() {        
    return main_time;     
}

VTop *top; // Instantiation of model
VerilatedContext* contextp;

void step_and_dump_wave(){
  top->clock = 0; top->eval();
  // sleep(0.1);
  top->clock = 1; top->eval();
  // sleep(0.1);
  contextp->timeInc(1);
  // tfp->dump(contextp->time());
}

void reset(){
  step_and_dump_wave();
  top->reset = 1;
  step_and_dump_wave();
  top->reset = 0;
}


// this is not consistent with uint8_t
// but it is ok since we do not access the array directly
static const uint32_t img [] = {
  0x800002b7,  // lui t0,0x80000
  0x0002a023,  // sw  zero,0(t0)
  0x0002a503,  // lw  a0,0(t0)
  0x00100073,  // ebreak (used as nemu_trap)
};

static void restart() {
  reset();
}

void init_isa() {
  /* Load built-in image. */
  memcpy(guest_to_host(RESET_VECTOR), img, sizeof(img));
  /* Initialize this virtual computer system. */
  restart();
}

void init_verilator(int argc, char** argv){
    contextp  = new VerilatedContext;
    top = new VTop{contextp};// Create model
    contextp->commandArgs(argc, argv);// Remember args
    contextp->commandArgs(argc, argv);
    contextp->traceEverOn(true);// Enable wave trace
}

void quit_verilator(){
    //top->final();//to call systemverilog final process
    delete top;
    delete contextp;
}


int main(int argc, char** argv, char** env) {
    init_verilator(argc,argv);
    init_isa();
    while (main_time<10){ 
        step_and_dump_wave();
        printf("%lx\n",top->io_inst);
        main_time++; 
    }
    printf("quiting verilator\n");
    quit_verilator();
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