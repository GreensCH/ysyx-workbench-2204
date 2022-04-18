// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========


void Vtop___024root___ctor_var_reset(Vtop___024root* vlSelf);

Vtop___024root::Vtop___024root(const char* _vcname__)
    : VerilatedModule(_vcname__)
 {
    // Reset structure values
    Vtop___024root___ctor_var_reset(this);
}

void Vtop___024root::__Vconfigure(Vtop__Syms* _vlSymsp, bool first) {
    if (false && first) {}  // Prevent unused
    this->vlSymsp = _vlSymsp;
}

Vtop___024root::~Vtop___024root() {
}

extern const VlUnpacked<CData/*7:0*/, 16> Vtop__ConstPool__TABLE_4980be08_0;
extern const VlUnpacked<CData/*7:0*/, 256> Vtop__ConstPool__TABLE_94dbecf1_0;

void Vtop___024root___settle__TOP__2(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___settle__TOP__2\n"); );
    // Variables
    CData/*7:0*/ __Vtableidx1;
    CData/*3:0*/ __Vtableidx2;
    CData/*3:0*/ __Vtableidx3;
    CData/*3:0*/ __Vtableidx4;
    CData/*3:0*/ __Vtableidx5;
    CData/*3:0*/ __Vtableidx6;
    CData/*3:0*/ __Vtableidx7;
    // Body
    __Vtableidx6 = (0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_5__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx6];
    __Vtableidx7 = (0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt) 
                            >> 4U));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_6__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx7];
    __Vtableidx2 = (0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx2];
    __Vtableidx3 = (0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2) 
                            >> 4U));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx3];
    __Vtableidx1 = vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data 
        = Vtop__ConstPool__TABLE_94dbecf1_0[__Vtableidx1];
    vlSelf->led_o = ((0xfffcU & (IData)(vlSelf->led_o)) 
                     | (((IData)(vlSelf->top__DOT__overflow) 
                         << 1U) | (IData)(vlSelf->top__DOT__ready)));
    vlSelf->hex_o = ((0xffffffffULL & vlSelf->hex_o) 
                     | ((QData)((IData)((((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_6__h) 
                                          << 8U) | (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_5__h)))) 
                        << 0x20U));
    __Vtableidx4 = (0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx4];
    __Vtableidx5 = (0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data) 
                            >> 4U));
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h 
        = Vtop__ConstPool__TABLE_4980be08_0[__Vtableidx5];
    vlSelf->hex_o = ((0xffff00000000ULL & vlSelf->hex_o) 
                     | (IData)((IData)((((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h) 
                                         << 0x18U) 
                                        | (((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h) 
                                            << 0x10U) 
                                           | (((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h) 
                                               << 8U) 
                                              | (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h)))))));
}

void Vtop___024root___eval_initial(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_initial\n"); );
    // Body
    vlSelf->__Vclklast__TOP__clk = vlSelf->clk;
}

void Vtop___024root___eval_settle(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_settle\n"); );
    // Body
    Vtop___024root___settle__TOP__2(vlSelf);
    vlSelf->__Vm_traceActivity[1U] = 1U;
    vlSelf->__Vm_traceActivity[0U] = 1U;
}

void Vtop___024root___final(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___final\n"); );
}

void Vtop___024root___ctor_var_reset(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___ctor_var_reset\n"); );
    // Body
    vlSelf->clk = 0;
    vlSelf->clrn = 0;
    vlSelf->ps2_clk = 0;
    vlSelf->ps2_data = 0;
    vlSelf->led_o = 0;
    vlSelf->hex_o = 0;
    vlSelf->top__DOT__ready = 0;
    vlSelf->top__DOT__nextdata_n = 0;
    vlSelf->top__DOT__overflow = 0;
    vlSelf->top__DOT__i_kbd_control__DOT__buffer = 0;
    for (int __Vi0=0; __Vi0<8; ++__Vi0) {
        vlSelf->top__DOT__i_kbd_control__DOT__fifo[__Vi0] = 0;
    }
    vlSelf->top__DOT__i_kbd_control__DOT__w_ptr = 0;
    vlSelf->top__DOT__i_kbd_control__DOT__r_ptr = 0;
    vlSelf->top__DOT__i_kbd_control__DOT__count = 0;
    vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync = 0;
    vlSelf->top__DOT__i_kbd_control__DOT____Vlvbound1 = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1 = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__state = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2 = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2_old = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_5__h = 0;
    vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_6__h = 0;
    for (int __Vi0=0; __Vi0<2; ++__Vi0) {
        vlSelf->__Vm_traceActivity[__Vi0] = 0;
    }
}
