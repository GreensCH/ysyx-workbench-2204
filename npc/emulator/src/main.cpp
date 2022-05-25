#include "include.h"

// this is not consistent with uint8_t
// but it is ok since we do not access the array directly
static const uint32_t img [] = {
  0x800002b7,  // lui t0,0x80000
  0x0002a023,  // sw  zero,0(t0)
  0x0002a503,  // lw  a0,0(t0)
  0x00100073,  // ebreak (used as nemu_trap)
};

static void restart() {
  reset(1);
}

void init_isa() {
  memcpy(guest_to_host(RESET_VECTOR), img, sizeof(img));
  restart();
}

int main(int argc, char** argv, char** env) {
    sim_init(argc,argv);
    init_isa();
    step_and_dump_wave();
    while (sc_time_stamp()<10){ 
        step_and_dump_wave();
        printf("pc: %lx  inst: %lx\n",top->io_pc , top->io_inst);
    }
    printf("quiting verilator\n");
    sim_exit();
    return 0;
  }



// static long load_img() {
//   if (img_file == NULL) {
//     Log("No image is given. Use the default build-in image.");
//     return 4096; // built-in image size
//   }
//
//   FILE *fp = fopen(img_file, "rb");
//   Assert(fp, "Can not open '%s'", img_file);
//
//   fseek(fp, 0, SEEK_END);
//   long size = ftell(fp);
//
//   Log("The image is %s, size = %ld", img_file, size);
//
//   fseek(fp, 0, SEEK_SET);
//   int ret = fread(guest_to_host(RESET_VECTOR), size, 1, fp);
//   assert(ret == 1);
//
//   fclose(fp);
//   return size;
// }