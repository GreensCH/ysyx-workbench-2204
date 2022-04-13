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

extern const VlWide<20>/*639:0*/ Vtop__ConstPool__CONST_6fe75230_0;

void Vtop___024root___initial__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___initial__TOP__1\n"); );
    // Body
    VL_READMEM_N(true, 24, 327680, 0, VL_CVT_PACK_STR_NW(20, Vtop__ConstPool__CONST_6fe75230_0)
                 ,  &(vlSelf->top__DOT__mem_vga), 0
                 , ~0ULL);
}

void Vtop___024root___settle__TOP__4(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___settle__TOP__4\n"); );
    // Body
    vlSelf->hsync = (0x60U < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt));
    vlSelf->vsync = (2U < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt));
    vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid = ((0x90U 
                                                   < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)) 
                                                  & (0x310U 
                                                     >= (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)));
    vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid = ((0x23U 
                                                   < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)) 
                                                  & (0x203U 
                                                     >= (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)));
    if (vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid) {
        vlSelf->top__DOT__h_addr = (0x3ffU & ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt) 
                                              - (IData)(0x91U)));
        vlSelf->valid = ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid) 
                         & 1U);
    } else {
        vlSelf->top__DOT__h_addr = 0U;
        vlSelf->valid = 0U;
    }
    vlSelf->top__DOT__v_addr = ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid)
                                 ? (0x3ffU & ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt) 
                                              - (IData)(0x24U)))
                                 : 0U);
    vlSelf->top__DOT__mem_out_wire = ((0x4ffffU >= 
                                       (((IData)(vlSelf->top__DOT__h_addr) 
                                         << 9U) | (0x1ffU 
                                                   & (IData)(vlSelf->top__DOT__v_addr))))
                                       ? vlSelf->top__DOT__mem_vga
                                      [(((IData)(vlSelf->top__DOT__h_addr) 
                                         << 9U) | (0x1ffU 
                                                   & (IData)(vlSelf->top__DOT__v_addr)))]
                                       : 0U);
    vlSelf->vga_r = (0xffU & (vlSelf->top__DOT__mem_out_wire 
                              >> 0x10U));
    vlSelf->vga_g = (0xffU & (vlSelf->top__DOT__mem_out_wire 
                              >> 8U));
    vlSelf->vga_b = (0xffU & vlSelf->top__DOT__mem_out_wire);
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
    vlSelf->__Vm_traceActivity[2U] = 1U;
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
    vlSelf->rst = 0;
    vlSelf->hsync = 0;
    vlSelf->vsync = 0;
    vlSelf->valid = 0;
    vlSelf->vga_r = 0;
    vlSelf->vga_g = 0;
    vlSelf->vga_b = 0;
    vlSelf->top__DOT__clk_25 = 0;
    for (int __Vi0=0; __Vi0<327680; ++__Vi0) {
        vlSelf->top__DOT__mem_vga[__Vi0] = 0;
    }
    vlSelf->top__DOT__mem_out_wire = 0;
    vlSelf->top__DOT__h_addr = 0;
    vlSelf->top__DOT__v_addr = 0;
    vlSelf->top__DOT__my_vgaclk__DOT__clkcount = 0;
    vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt = 0;
    vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt = 0;
    vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid = 0;
    vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid = 0;
    vlSelf->__Vdly__top__DOT__i_vga_ctrl__DOT__x_cnt = 0;
    for (int __Vi0=0; __Vi0<3; ++__Vi0) {
        vlSelf->__Vm_traceActivity[__Vi0] = 0;
    }
}
