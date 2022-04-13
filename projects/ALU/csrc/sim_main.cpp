#include "include.h"
#include <bitset>

using namespace std;

void step_and_dump_wave(){
  top->eval();
  contextp->timeInc(1);
  tfp->dump(contextp->time());
  //tfp->set_time_resolution("us");
}

void sim_init(){
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  top = new Vtop{contextp};
  contextp->traceEverOn(true);
  top->trace(tfp, 0);//trace 0 level of hierarchy
  //tfp->dumpvars(1,"t");//trace 1 level under "t"
  // tfp->set_time_unit("ps");//时间单位，似乎没用
  tfp->set_time_resolution("ns");//时间分辨率
  tfp->open("vlt_dump.vcd");
}

void sim_exit(){
  step_and_dump_wave();
  tfp->close();
}


#include <string.h>
#define BIT_WIDTH 4
int32_t data_converter(int32_t a_i,int32_t b_i){
    bitset<32> a(a_i),b(a_i);
    top->a=(int32_t)a.to_ulong();
    top->b=(int32_t)b.to_ulong();
    step_and_dump_wave();
    //由硬件取到的s需要进行左移符号处理
    bitset<BIT_WIDTH> s(top->s);
    bitset<32-BIT_WIDTH> one_temp(-1);//全1bits
    bitset<32-BIT_WIDTH> zero_temp(0);//全0bits
    bitset<32> s_32;//32位s结果
    //符号位扩展
    if(s[BIT_WIDTH-1])
      s_32 = bitset<32>(one_temp.to_string()+s.to_string());
    else
      s_32 = bitset<32>(zero_temp.to_string()+s.to_string());
    cout
      // << s[0] <<s[1] <<s[2]<<s[32]<<s[31]<<s[30]
        <<"a = "  <<a.to_string().substr(32-BIT_WIDTH)//4-bits a
        <<"("<<(int32_t)a.to_ulong()<<")" //int a
        <<", b = "<<b.to_string().substr(32-BIT_WIDTH)//4-bits b
        <<"("<<(int32_t)b.to_ulong()<<")" //int b
        <<", s = "<<s_32.to_string().substr(32-BIT_WIDTH)//4-bits s
        <<"("<<(int32_t)s_32.to_ulong()<<")" //int s
        <<", overflow("<<top->overflow<<")"
        <<", carry("<<top->carry<<")"
        <<", zero("<<top->zero<<")"
        <<endl;
    return (int32_t)s_32.to_ulong();
}
void test_add_sub(){
  cout<<"begin\n"<<endl;
  for(int i = -8; i < 8 ; i++ ){
    for(int j=-8; j<8 ; j++){
      bitset<32> a(i),b(j);
      top->a=(int32_t)a.to_ulong();
      top->b=(int32_t)b.to_ulong();
      step_and_dump_wave();
      //由硬件取到的s需要进行左移符号处理
      bitset<BIT_WIDTH> s(top->s);
      bitset<32-BIT_WIDTH> one_temp(-1);//全1bits
      bitset<32-BIT_WIDTH> zero_temp(0);//全0bits
      bitset<32> s_32;//32位s结果
      //符号位扩展
      if(s[BIT_WIDTH-1])
        s_32 = bitset<32>(one_temp.to_string()+s.to_string());
      else
        s_32 = bitset<32>(zero_temp.to_string()+s.to_string());
      printf("a=%s(%2d) ",\
          a.to_string().substr(32-BIT_WIDTH).c_str(),(int32_t)a.to_ulong());
      printf("b=%s(%2d) ",\
          b.to_string().substr(32-BIT_WIDTH).c_str(),(int32_t)b.to_ulong());
      printf("s=%s(%2d) ",\
          s_32.to_string().substr(32-BIT_WIDTH).c_str(),(int32_t)s_32.to_ulong());
      printf("overflow=(%2d) carry(%2d) zero(%2d)\n",\
          (int)(top->overflow),(int)(top->carry),(int)(top->zero));
      // cout
      //   // << s[0] <<s[1] <<s[2]<<s[32]<<s[31]<<s[30]
      //     <<"a = "  <<a.to_string().substr(32-BIT_WIDTH)//4-bits a
      //     <<"("<<(int32_t)a.to_ulong()<<")" //int a
      //     <<", b = "<<b.to_string().substr(32-BIT_WIDTH)//4-bits b
      //     <<"("<<(int32_t)b.to_ulong()<<")" //int b
      //     <<", s = "<<s_32.to_string().substr(32-BIT_WIDTH)//4-bits s
      //     <<"("<<(int32_t)s_32.to_ulong()<<")" //int s
      //     <<", overflow("<<(int)(top->overflow)<<")"
      //     <<", carry("<<(int)(top->carry)<<")"
      //     <<", zero("<<(int)(top->zero)<<")"
      //     <<endl;
    }
  }
  cout<<"end\n"<<endl;
}


int sim_main() {

  cout<<"******ADD TEST*******";
  top->sel=0b000;
  test_add_sub();  
  cout<<"******SUB TEST*******";
  top->sel=0b001;
  test_add_sub();  
  cout<<"******NOT TEST*******";
  top->sel=0b010;
  test_add_sub();  
  cout<<"******AND TEST*******";
  top->sel=0b011;
  test_add_sub();    
  cout<<"******OR  TEST*******";
  top->sel=0b100;
  test_add_sub();  
  cout<<"******XOR TEST*******";
  top->sel=0b101;
  test_add_sub();  
  cout<<"*****CMP TEST*******";
  top->sel=0b110;
  test_add_sub();  
  cout<<"******EQU TEST*******";
  top->sel=0b111;
  test_add_sub();
  cout<<"******END TEST*******"<<endl;
  return 0;
}





