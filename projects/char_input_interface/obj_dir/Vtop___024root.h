// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vtop.h for the primary calling header

#ifndef VERILATED_VTOP___024ROOT_H_
#define VERILATED_VTOP___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class Vtop__Syms;

//----------

VL_MODULE(Vtop___024root) {
  public:

    // PORTS
    VL_IN8(clk,0,0);
    VL_IN8(rst,0,0);
    VL_IN8(rstn,0,0);
    VL_IN8(ps2_clk,0,0);
    VL_IN8(ps2_data,0,0);
    VL_OUT8(hsync,0,0);
    VL_OUT8(vsync,0,0);
    VL_OUT8(valid,0,0);
    VL_OUT8(vga_r,7,0);
    VL_OUT8(vga_g,7,0);
    VL_OUT8(vga_b,7,0);

    // LOCAL SIGNALS
    CData/*6:0*/ top__DOT__i_cii_top__DOT__char_x;
    CData/*4:0*/ top__DOT__i_cii_top__DOT__char_y;
    CData/*3:0*/ top__DOT__i_cii_top__DOT__pixel_x;
    CData/*3:0*/ top__DOT__i_cii_top__DOT__pixel_y;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__char_ascii;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__ps2ctrl_ascii;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__ps2kbd_ready;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__ps2kbd_overflow;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__ps2ctrl_vld;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid;
    CData/*6:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__point_x;
    CData/*4:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__point_y;
    CData/*2:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr;
    CData/*2:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr;
    CData/*3:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count;
    CData/*2:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1;
    CData/*3:0*/ top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__h_addr;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__v_addr;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old;
    SData/*9:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer;
    VlUnpacked<SData/*11:0*/, 4096> top__DOT__i_cii_top__DOT__mem_graph_ascii;
    VlUnpacked<VlUnpacked<CData/*7:0*/, 31>, 71> top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__mem;
    VlUnpacked<CData/*7:0*/, 8> top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo;

    // LOCAL VARIABLES
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound1;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound2;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound3;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound4;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound5;
    CData/*7:0*/ top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound6;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__i_kbd_control__DOT____Vlvbound1;
    CData/*0:0*/ __Vclklast__TOP__clk;
    CData/*0:0*/ __Vclklast__TOP__rst;

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
