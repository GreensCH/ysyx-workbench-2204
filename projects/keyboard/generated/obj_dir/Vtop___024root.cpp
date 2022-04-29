// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

extern const VlUnpacked<CData/*7:0*/, 16> Vtop__ConstPool__TABLE_4980be08_0;
extern const VlUnpacked<CData/*7:0*/, 256> Vtop__ConstPool__TABLE_94dbecf1_0;

VL_INLINE_OPT void Vtop___024root___sequent__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__1\n"); );
    // Variables
    CData/*7:0*/ __Vtableidx1;
    CData/*3:0*/ __Vtableidx2;
    CData/*3:0*/ __Vtableidx3;
    CData/*3:0*/ __Vtableidx4;
    CData/*3:0*/ __Vtableidx5;
    CData/*3:0*/ __Vtableidx6;
    CData/*3:0*/ __Vtableidx7;
    CData/*2:0*/ __Vdly__top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    CData/*2:0*/ __Vdly__top__DOT__i_kbd_control__DOT__r_ptr;
    CData/*0:0*/ __Vdly__top__DOT__ready;
    CData/*2:0*/ __Vdlyvdim0__top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*7:0*/ __Vdlyvval__top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*0:0*/ __Vdlyvset__top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*2:0*/ __Vdly__top__DOT__i_kbd_control__DOT__w_ptr;
    CData/*3:0*/ __Vdly__top__DOT__i_kbd_control__DOT__count;
    CData/*3:0*/ __Vdly__top__DOT__i_show_ps2kbd__DOT__state;
    // Body
    __Vdly__top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    __Vdly__top__DOT__i_kbd_control__DOT__count = vlSelf->top__DOT__i_kbd_control__DOT__count;
    __Vdly__top__DOT__i_kbd_control__DOT__w_ptr = vlSelf->top__DOT__i_kbd_control__DOT__w_ptr;
    __Vdly__top__DOT__i_kbd_control__DOT__r_ptr = vlSelf->top__DOT__i_kbd_control__DOT__r_ptr;
    __Vdlyvset__top__DOT__i_kbd_control__DOT__fifo__v0 = 0U;
    __Vdly__top__DOT__ready = vlSelf->top__DOT__ready;
    __Vdly__top__DOT__i_show_ps2kbd__DOT__state = vlSelf->top__DOT__i_show_ps2kbd__DOT__state;
    __Vdly__top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = ((6U & ((IData)(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync) 
                  << 1U)) | (IData)(vlSelf->ps2_clk));
    if (VL_UNLIKELY(vlSelf->top__DOT__overflow)) {
        VL_WRITEF("overflow...\n");
    }
    if (vlSelf->clrn) {
        if (vlSelf->top__DOT__ready) {
            if ((1U & (~ (IData)(vlSelf->top__DOT__nextdata_n)))) {
                __Vdly__top__DOT__i_kbd_control__DOT__r_ptr 
                    = (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_kbd_control__DOT__r_ptr)));
                if (((IData)(vlSelf->top__DOT__i_kbd_control__DOT__w_ptr) 
                     == (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_kbd_control__DOT__r_ptr))))) {
                    __Vdly__top__DOT__ready = 0U;
                }
            }
        }
        if ((IData)((4U == (6U & (IData)(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync))))) {
            if ((0xaU == (IData)(vlSelf->top__DOT__i_kbd_control__DOT__count))) {
                if ((((~ (IData)(vlSelf->top__DOT__i_kbd_control__DOT__buffer)) 
                      & (IData)(vlSelf->ps2_data)) 
                     & VL_REDXOR_32((0x1ffU & ((IData)(vlSelf->top__DOT__i_kbd_control__DOT__buffer) 
                                               >> 1U))))) {
                    __Vdlyvval__top__DOT__i_kbd_control__DOT__fifo__v0 
                        = (0xffU & ((IData)(vlSelf->top__DOT__i_kbd_control__DOT__buffer) 
                                    >> 1U));
                    __Vdlyvset__top__DOT__i_kbd_control__DOT__fifo__v0 = 1U;
                    __Vdlyvdim0__top__DOT__i_kbd_control__DOT__fifo__v0 
                        = vlSelf->top__DOT__i_kbd_control__DOT__w_ptr;
                    __Vdly__top__DOT__ready = 1U;
                    __Vdly__top__DOT__i_kbd_control__DOT__w_ptr 
                        = (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_kbd_control__DOT__w_ptr)));
                    vlSelf->top__DOT__overflow = ((IData)(vlSelf->top__DOT__overflow) 
                                                  | ((IData)(vlSelf->top__DOT__i_kbd_control__DOT__r_ptr) 
                                                     == 
                                                     (7U 
                                                      & ((IData)(1U) 
                                                         + (IData)(vlSelf->top__DOT__i_kbd_control__DOT__w_ptr)))));
                }
                __Vdly__top__DOT__i_kbd_control__DOT__count = 0U;
            } else {
                vlSelf->top__DOT__i_kbd_control__DOT____Vlvbound1 
                    = vlSelf->ps2_data;
                if ((9U >= (IData)(vlSelf->top__DOT__i_kbd_control__DOT__count))) {
                    vlSelf->top__DOT__i_kbd_control__DOT__buffer 
                        = (((~ ((IData)(1U) << (IData)(vlSelf->top__DOT__i_kbd_control__DOT__count))) 
                            & (IData)(vlSelf->top__DOT__i_kbd_control__DOT__buffer)) 
                           | (0x3ffU & ((IData)(vlSelf->top__DOT__i_kbd_control__DOT____Vlvbound1) 
                                        << (IData)(vlSelf->top__DOT__i_kbd_control__DOT__count))));
                }
                __Vdly__top__DOT__i_kbd_control__DOT__count 
                    = (0xfU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_kbd_control__DOT__count)));
            }
        }
    } else {
        __Vdly__top__DOT__i_kbd_control__DOT__count = 0U;
        __Vdly__top__DOT__i_kbd_control__DOT__w_ptr = 0U;
        __Vdly__top__DOT__i_kbd_control__DOT__r_ptr = 0U;
        vlSelf->top__DOT__overflow = 0U;
        __Vdly__top__DOT__ready = 0U;
    }
    if (vlSelf->clrn) {
        if (((IData)(vlSelf->top__DOT__ready) & (~ (IData)(vlSelf->top__DOT__nextdata_n)))) {
            if ((0U == (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__state))) {
                if ((0xf0U == (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1))) {
                    __Vdly__top__DOT__i_show_ps2kbd__DOT__state = 1U;
                    vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0U;
                } else {
                    __Vdly__top__DOT__i_show_ps2kbd__DOT__state = 0U;
                    vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 1U;
                }
                if ((0xf0U == (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1))) {
                    VL_WRITEF("OUT\n");
                    vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt 
                        = (0xffU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt)));
                } else {
                    VL_WRITEF("GET DATA\n");
                    vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2_old 
                        = vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2;
                }
                vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2 
                    = ((0xf0U == (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1))
                        ? 0U : (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1));
            } else if ((1U == (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__state))) {
                VL_WRITEF("COME BACK TO WAIT\n");
                __Vdly__top__DOT__i_show_ps2kbd__DOT__state = 0U;
                vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2 = 0U;
                vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0U;
            } else {
                VL_WRITEF("ERROR\n");
                __Vdly__top__DOT__i_show_ps2kbd__DOT__state = 0U;
                vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2 = 0U;
                vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0U;
            }
        } else {
            vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0U;
        }
    } else {
        vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt = 0U;
        __Vdly__top__DOT__i_show_ps2kbd__DOT__state = 0U;
        vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2 = 0U;
        vlSelf->top__DOT__i_show_ps2kbd__DOT__busy = 0U;
    }
    vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = __Vdly__top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    vlSelf->top__DOT__i_kbd_control__DOT__w_ptr = __Vdly__top__DOT__i_kbd_control__DOT__w_ptr;
    vlSelf->top__DOT__i_kbd_control__DOT__count = __Vdly__top__DOT__i_kbd_control__DOT__count;
    vlSelf->top__DOT__i_show_ps2kbd__DOT__state = __Vdly__top__DOT__i_show_ps2kbd__DOT__state;
    if (vlSelf->clrn) {
        if (((IData)(vlSelf->top__DOT__ready) & (IData)(vlSelf->top__DOT__nextdata_n))) {
            vlSelf->top__DOT__nextdata_n = 0U;
            vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1 
                = vlSelf->top__DOT__i_kbd_control__DOT__fifo
                [vlSelf->top__DOT__i_kbd_control__DOT__r_ptr];
        } else {
            vlSelf->top__DOT__nextdata_n = 1U;
            vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1 = 0U;
        }
    } else {
        vlSelf->top__DOT__nextdata_n = 1U;
        vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1 = 0U;
    }
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
    vlSelf->top__DOT__i_kbd_control__DOT__r_ptr = __Vdly__top__DOT__i_kbd_control__DOT__r_ptr;
    if (__Vdlyvset__top__DOT__i_kbd_control__DOT__fifo__v0) {
        vlSelf->top__DOT__i_kbd_control__DOT__fifo[__Vdlyvdim0__top__DOT__i_kbd_control__DOT__fifo__v0] 
            = __Vdlyvval__top__DOT__i_kbd_control__DOT__fifo__v0;
    }
    vlSelf->top__DOT__ready = __Vdly__top__DOT__ready;
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
    vlSelf->led_o = ((0xfffcU & (IData)(vlSelf->led_o)) 
                     | (((IData)(vlSelf->top__DOT__overflow) 
                         << 1U) | (IData)(vlSelf->top__DOT__ready)));
    vlSelf->hex_o = ((0xffff00000000ULL & vlSelf->hex_o) 
                     | (IData)((IData)((((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h) 
                                         << 0x18U) 
                                        | (((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h) 
                                            << 0x10U) 
                                           | (((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h) 
                                               << 8U) 
                                              | (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h)))))));
}

void Vtop___024root___eval(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval\n"); );
    // Body
    if (((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk)))) {
        Vtop___024root___sequent__TOP__1(vlSelf);
        vlSelf->__Vm_traceActivity[1U] = 1U;
    }
    // Final
    vlSelf->__Vclklast__TOP__clk = vlSelf->clk;
}

QData Vtop___024root___change_request_1(Vtop___024root* vlSelf);

VL_INLINE_OPT QData Vtop___024root___change_request(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___change_request\n"); );
    // Body
    return (Vtop___024root___change_request_1(vlSelf));
}

VL_INLINE_OPT QData Vtop___024root___change_request_1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___change_request_1\n"); );
    // Body
    // Change detection
    QData __req = false;  // Logically a bool
    return __req;
}

#ifdef VL_DEBUG
void Vtop___024root___eval_debug_assertions(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_debug_assertions\n"); );
    // Body
    if (VL_UNLIKELY((vlSelf->clk & 0xfeU))) {
        Verilated::overWidthError("clk");}
    if (VL_UNLIKELY((vlSelf->clrn & 0xfeU))) {
        Verilated::overWidthError("clrn");}
    if (VL_UNLIKELY((vlSelf->ps2_clk & 0xfeU))) {
        Verilated::overWidthError("ps2_clk");}
    if (VL_UNLIKELY((vlSelf->ps2_data & 0xfeU))) {
        Verilated::overWidthError("ps2_data");}
}
#endif  // VL_DEBUG
