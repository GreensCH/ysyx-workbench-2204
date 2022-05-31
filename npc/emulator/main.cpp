#include "VTop.h"
#include <verilated.h>
#include "verilated_vcd_c.h"
#include <iostream>  

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