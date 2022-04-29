// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

VL_INLINE_OPT void Vtop___024root___sequent__TOP__2(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__2\n"); );
    // Body
    vlSelf->__Vdly__top__DOT__i_vga_ctrl__DOT__x_cnt 
        = vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt;
    vlSelf->__Vdly__top__DOT__i_vga_ctrl__DOT__x_cnt 
        = ((IData)(vlSelf->rst) ? 1U : ((0x320U == (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt))
                                         ? 1U : (0x3ffU 
                                                 & ((IData)(1U) 
                                                    + (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)))));
}

VL_INLINE_OPT void Vtop___024root___sequent__TOP__3(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__3\n"); );
    // Variables
    SData/*9:0*/ __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt;
    // Body
    if (vlSelf->rst) {
        vlSelf->top__DOT__my_vgaclk__DOT__clkcount = 0U;
        vlSelf->top__DOT__clk_25 = 0U;
    } else {
        vlSelf->top__DOT__my_vgaclk__DOT__clkcount 
            = ((IData)(1U) + vlSelf->top__DOT__my_vgaclk__DOT__clkcount);
        if ((1U <= vlSelf->top__DOT__my_vgaclk__DOT__clkcount)) {
            vlSelf->top__DOT__clk_25 = (1U & (~ (IData)(vlSelf->top__DOT__clk_25)));
            vlSelf->top__DOT__my_vgaclk__DOT__clkcount = 0U;
        }
    }
    __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt = vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt;
    if (vlSelf->rst) {
        __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt = 1U;
    } else if (((0x20dU == (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)) 
                & (0x320U == (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)))) {
        __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt = 1U;
    } else if ((0x320U == (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt))) {
        __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt = 
            (0x3ffU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)));
    }
    vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt = __Vdly__top__DOT__i_vga_ctrl__DOT__y_cnt;
    vlSelf->vsync = (2U < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt));
    vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid = ((0x23U 
                                                   < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)) 
                                                  & (0x203U 
                                                     >= (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt)));
    vlSelf->top__DOT__v_addr = ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid)
                                 ? (0x3ffU & ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt) 
                                              - (IData)(0x24U)))
                                 : 0U);
}

VL_INLINE_OPT void Vtop___024root___sequent__TOP__5(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__5\n"); );
    // Body
    vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt = vlSelf->__Vdly__top__DOT__i_vga_ctrl__DOT__x_cnt;
    vlSelf->hsync = (0x60U < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt));
    vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid = ((0x90U 
                                                   < (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)) 
                                                  & (0x310U 
                                                     >= (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt)));
    vlSelf->valid = ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid) 
                     & (IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid));
    vlSelf->top__DOT__h_addr = ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid)
                                 ? (0x3ffU & ((IData)(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt) 
                                              - (IData)(0x91U)))
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

void Vtop___024root___eval(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval\n"); );
    // Body
    if ((((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk))) 
         | ((IData)(vlSelf->rst) & (~ (IData)(vlSelf->__Vclklast__TOP__rst))))) {
        Vtop___024root___sequent__TOP__2(vlSelf);
    }
    if (((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk)))) {
        Vtop___024root___sequent__TOP__3(vlSelf);
        vlSelf->__Vm_traceActivity[1U] = 1U;
    }
    if ((((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk))) 
         | ((IData)(vlSelf->rst) & (~ (IData)(vlSelf->__Vclklast__TOP__rst))))) {
        Vtop___024root___sequent__TOP__5(vlSelf);
        vlSelf->__Vm_traceActivity[2U] = 1U;
    }
    // Final
    vlSelf->__Vclklast__TOP__clk = vlSelf->clk;
    vlSelf->__Vclklast__TOP__rst = vlSelf->rst;
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
    if (VL_UNLIKELY((vlSelf->rst & 0xfeU))) {
        Verilated::overWidthError("rst");}
}
#endif  // VL_DEBUG
