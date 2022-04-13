// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vtop.h for the primary calling header

#ifndef VERILATED_VTOP___024ROOT_H_
#define VERILATED_VTOP___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class Vtop__Syms;
class Vtop_VerilatedVcd;


//----------

VL_MODULE(Vtop___024root) {
  public:

    // PORTS
    VL_IN8(clk,0,0);
    VL_IN8(rst,0,0);
    VL_OUT8(hsync,0,0);
    VL_OUT8(vsync,0,0);
    VL_OUT8(valid,0,0);
    VL_OUT8(vga_r,7,0);
    VL_OUT8(vga_g,7,0);
    VL_OUT8(vga_b,7,0);

    // LOCAL SIGNALS
    CData/*0:0*/ top__DOT__clk_25;
    CData/*0:0*/ top__DOT__i_vga_ctrl__DOT__h_valid;
    CData/*0:0*/ top__DOT__i_vga_ctrl__DOT__v_valid;
    SData/*9:0*/ top__DOT__h_addr;
    SData/*9:0*/ top__DOT__v_addr;
    SData/*9:0*/ top__DOT__i_vga_ctrl__DOT__x_cnt;
    SData/*9:0*/ top__DOT__i_vga_ctrl__DOT__y_cnt;
    IData/*23:0*/ top__DOT__mem_out_wire;
    IData/*31:0*/ top__DOT__my_vgaclk__DOT__clkcount;
    VlUnpacked<IData/*23:0*/, 327680> top__DOT__mem_vga;

    // LOCAL VARIABLES
    CData/*0:0*/ __Vclklast__TOP__clk;
    CData/*0:0*/ __Vclklast__TOP__rst;
    SData/*9:0*/ __Vdly__top__DOT__i_vga_ctrl__DOT__x_cnt;
    VlUnpacked<CData/*0:0*/, 3> __Vm_traceActivity;

    // INTERNAL VARIABLES
    Vtop__Syms* vlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(Vtop___024root);  ///< Copying not allowed
  public:
    Vtop___024root(const char* name);
    ~Vtop___024root();

    // INTERNAL METHODS
    void __Vconfigure(Vtop__Syms* symsp, bool first);
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
