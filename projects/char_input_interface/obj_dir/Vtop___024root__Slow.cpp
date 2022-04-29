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

extern const VlWide<22>/*703:0*/ Vtop__ConstPool__CONST_00f1f826_0;

void Vtop___024root___initial__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___initial__TOP__1\n"); );
    // Body
    VL_READMEM_N(true, 12, 4096, 0, VL_CVT_PACK_STR_NW(22, Vtop__ConstPool__CONST_00f1f826_0)
                 ,  &(vlSelf->top__DOT__i_cii_top__DOT__mem_graph_ascii)
                 , 0, ~0ULL);
}

extern const VlUnpacked<CData/*7:0*/, 256> Vtop__ConstPool__TABLE_e8611664_0;

void Vtop___024root___settle__TOP__4(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___settle__TOP__4\n"); );
    // Variables
    CData/*0:0*/ top__DOT__i_cii_top__DOT__pixel;
    CData/*0:0*/ top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid;
    CData/*7:0*/ __Vtableidx1;
    // Body
    vlSelf->hsync = (0x60U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt));
    vlSelf->vsync = (2U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt));
    top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid 
        = ((0x90U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)) 
           & (0x310U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)));
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid 
        = ((0x23U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)) 
           & (0x203U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)));
    __Vtableidx1 = vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2;
    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii 
        = Vtop__ConstPool__TABLE_e8611664_0[__Vtableidx1];
    top__DOT__i_cii_top__DOT__pixel = ((0xbU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x)) 
                                       & (vlSelf->top__DOT__i_cii_top__DOT__mem_graph_ascii
                                          [(((IData)(vlSelf->top__DOT__i_cii_top__DOT__char_ascii) 
                                             << 4U) 
                                            | (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))] 
                                          >> (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x)));
    if (top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid) {
        vlSelf->top__DOT__i_cii_top__DOT__h_addr = 
            (0x3ffU & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt) 
                       - (IData)(0x91U)));
        vlSelf->valid = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid) 
                         & 1U);
    } else {
        vlSelf->top__DOT__i_cii_top__DOT__h_addr = 0U;
        vlSelf->valid = 0U;
    }
    vlSelf->top__DOT__i_cii_top__DOT__v_addr = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid)
                                                 ? 
                                                (0x3ffU 
                                                 & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt) 
                                                    - (IData)(0x24U)))
                                                 : 0U);
    vlSelf->vga_r = (0xffU & (((IData)(top__DOT__i_cii_top__DOT__pixel)
                                ? 0xffffffU : 0U) >> 0x10U));
    vlSelf->vga_g = (0xffU & (((IData)(top__DOT__i_cii_top__DOT__pixel)
                                ? 0xffffffU : 0U) >> 8U));
    vlSelf->vga_b = ((IData)(top__DOT__i_cii_top__DOT__pixel)
                      ? 0xffU : 0U);
}

void Vtop___024root___eval_initial(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_initial\n"); );
    // Body
    Vtop___024root___initial__TOP__1(vlSelf);
    vlSelf->__Vclklast__TOP__clk = vlSelf->clk;
    vlSelf->__Vclklast__TOP__rst = vlSelf->rst;
}

void Vtop___024root___eval_settle(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_settle\n"); );
    // Body
    Vtop___024root___settle__TOP__4(vlSelf);
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
    vlSelf->rst = 0;
    vlSelf->rstn = 0;
    vlSelf->ps2_clk = 0;
    vlSelf->ps2_data = 0;
    vlSelf->hsync = 0;
    vlSelf->vsync = 0;
    vlSelf->valid = 0;
    vlSelf->vga_r = 0;
    vlSelf->vga_g = 0;
    vlSelf->vga_b = 0;
    for (int __Vi0=0; __Vi0<4096; ++__Vi0) {
        vlSelf->top__DOT__i_cii_top__DOT__mem_graph_ascii[__Vi0] = 0;
    }
    vlSelf->top__DOT__i_cii_top__DOT__h_addr = 0;
    vlSelf->top__DOT__i_cii_top__DOT__v_addr = 0;
    vlSelf->top__DOT__i_cii_top__DOT__char_x = 0;
    vlSelf->top__DOT__i_cii_top__DOT__char_y = 0;
    vlSelf->top__DOT__i_cii_top__DOT__pixel_x = 0;
    vlSelf->top__DOT__i_cii_top__DOT__pixel_y = 0;
    vlSelf->top__DOT__i_cii_top__DOT__char_ascii = 0;
    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii = 0;
    vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready = 0;
    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n = 0;
    vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow = 0;
    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old = 0;
    for (int __Vi0=0; __Vi0<71; ++__Vi0) {
        for (int __Vi1=0; __Vi1<31; ++__Vi1) {
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__mem[__Vi0][__Vi1] = 0;
        }
    }
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__point_x = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__point_y = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound1 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound2 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound3 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound4 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound5 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT____Vlvbound6 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer = 0;
    for (int __Vi0=0; __Vi0<8; ++__Vi0) {
        vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[__Vi0] = 0;
    }
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT____Vlvbound1 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1 = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 0;
    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0;
}
