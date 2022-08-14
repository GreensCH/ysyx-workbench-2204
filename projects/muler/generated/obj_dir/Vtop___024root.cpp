// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

extern const VlUnpacked<CData/*7:0*/, 16> Vtop__ConstPool__TABLE_2bbc5af4_0;

VL_INLINE_OPT void Vtop___024root___combo__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___combo__TOP__1\n"); );
    // Variables
    CData/*3:0*/ __Vtableidx1;
    CData/*3:0*/ __Vtableidx2;
    // Body
    __Vtableidx1 = vlSelf->a;
    vlSelf->top__DOT____Vcellout__ia__h = Vtop__ConstPool__TABLE_2bbc5af4_0
        [__Vtableidx1];
    __Vtableidx2 = vlSelf->b;
    vlSelf->top__DOT____Vcellout__ib__h = Vtop__ConstPool__TABLE_2bbc5af4_0
        [__Vtableidx2];
    if ((4U & (IData)(vlSelf->sel))) {
        if ((2U & (IData)(vlSelf->sel))) {
            if ((1U & (IData)(vlSelf->sel))) {
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__b 
                    = vlSelf->b;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__a 
                    = vlSelf->a;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__b 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__b;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__a;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as 
                    = (0xfU & ((IData)(1U) + (~ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__b))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__carry 
                    = (1U & (((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a) 
                              + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as)) 
                             >> 4U));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__s 
                    = (0xfU & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a) 
                               + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as)));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__overflow 
                    = (((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a) 
                               >> 3U)) == (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as) 
                                                 >> 3U))) 
                       & ((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__s) 
                                 >> 3U)) != (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a) 
                                                   >> 3U))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__zero 
                    = (1U & (~ (IData)((0U != (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__s)))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__carry 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__carry;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__zero 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__zero;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__overflow 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__overflow;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__s 
                    = (0xfU & (- (IData)((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__zero))));
                vlSelf->carry = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__carry;
                vlSelf->zero = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__zero;
                vlSelf->overflow = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__overflow;
                vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__s;
            } else {
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__b 
                    = vlSelf->b;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__a 
                    = vlSelf->a;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__b 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__b;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__a;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as 
                    = (0xfU & ((IData)(1U) + (~ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__b))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__carry 
                    = (1U & (((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a) 
                              + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as)) 
                             >> 4U));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__s 
                    = (0xfU & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a) 
                               + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as)));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__overflow 
                    = (((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a) 
                               >> 3U)) == (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as) 
                                                 >> 3U))) 
                       & ((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__s) 
                                 >> 3U)) != (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a) 
                                                   >> 3U))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__zero 
                    = (1U & (~ (IData)((0U != (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__s)))));
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__carry 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__carry;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__zero 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__zero;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__overflow 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__overflow;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__s 
                    = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__s;
                vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__s 
                    = (0xfU & (- (IData)((1U & (((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__s) 
                                                 >> 3U) 
                                                ^ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__overflow))))));
                vlSelf->carry = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__carry;
                vlSelf->zero = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__zero;
                vlSelf->overflow = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__overflow;
                vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__s;
            }
        } else if ((1U & (IData)(vlSelf->sel))) {
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__b 
                = vlSelf->b;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__a 
                = vlSelf->a;
            vlSelf->carry = 0U;
            vlSelf->zero = 0U;
            vlSelf->overflow = 0U;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__s 
                = ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__a) 
                   ^ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__b));
            vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__s;
        } else {
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__b 
                = vlSelf->b;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__a 
                = vlSelf->a;
            vlSelf->carry = 0U;
            vlSelf->zero = 0U;
            vlSelf->overflow = 0U;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__s 
                = ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__a) 
                   | (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__b));
            vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__s;
        }
    } else if ((2U & (IData)(vlSelf->sel))) {
        if ((1U & (IData)(vlSelf->sel))) {
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__b 
                = vlSelf->b;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__a 
                = vlSelf->a;
            vlSelf->carry = 0U;
            vlSelf->zero = 0U;
            vlSelf->overflow = 0U;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__s 
                = ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__a) 
                   & (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__b));
            vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__s;
        } else {
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__a 
                = vlSelf->a;
            vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__s 
                = (0xfU & (~ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__a)));
            vlSelf->carry = 0U;
            vlSelf->zero = 0U;
            vlSelf->overflow = 0U;
            vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__s;
        }
    } else if ((1U & (IData)(vlSelf->sel))) {
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__b 
            = vlSelf->b;
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a 
            = vlSelf->a;
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as 
            = (0xfU & ((IData)(1U) + (~ (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__b))));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__carry 
            = (1U & (((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a) 
                      + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as)) 
                     >> 4U));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__s 
            = (0xfU & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a) 
                       + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as)));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__overflow 
            = (((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a) 
                       >> 3U)) == (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as) 
                                         >> 3U))) & 
               ((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__s) 
                       >> 3U)) != (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a) 
                                         >> 3U))));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__zero 
            = (1U & (~ (IData)((0U != (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__s)))));
        vlSelf->carry = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__carry;
        vlSelf->zero = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__zero;
        vlSelf->overflow = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__overflow;
        vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__s;
    } else {
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__b 
            = vlSelf->b;
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a 
            = vlSelf->a;
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as 
            = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__b;
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__carry 
            = (1U & (((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a) 
                      + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as)) 
                     >> 4U));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__s 
            = (0xfU & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a) 
                       + (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as)));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__overflow 
            = (((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a) 
                       >> 3U)) == (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as) 
                                         >> 3U))) & 
               ((1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__s) 
                       >> 3U)) != (1U & ((IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a) 
                                         >> 3U))));
        vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__zero 
            = (1U & (~ (IData)((0U != (IData)(vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__s)))));
        vlSelf->carry = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__carry;
        vlSelf->zero = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__zero;
        vlSelf->overflow = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__overflow;
        vlSelf->s = vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__s;
    }
    vlSelf->hex_o = (((IData)(vlSelf->top__DOT____Vcellout__ia__h) 
                      << 8U) | (IData)(vlSelf->top__DOT____Vcellout__ib__h));
    vlSelf->led_o = ((0xfff0U & (IData)(vlSelf->led_o)) 
                     | (IData)(vlSelf->s));
    vlSelf->led_o = ((0x1fffU & (IData)(vlSelf->led_o)) 
                     | (((IData)(vlSelf->carry) << 0xfU) 
                        | (((IData)(vlSelf->zero) << 0xeU) 
                           | ((IData)(vlSelf->overflow) 
                              << 0xdU))));
}

void Vtop___024root___eval(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval\n"); );
    // Body
    Vtop___024root___combo__TOP__1(vlSelf);
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
    if (VL_UNLIKELY((vlSelf->a & 0xf0U))) {
        Verilated::overWidthError("a");}
    if (VL_UNLIKELY((vlSelf->b & 0xf0U))) {
        Verilated::overWidthError("b");}
    if (VL_UNLIKELY((vlSelf->sel & 0xf8U))) {
        Verilated::overWidthError("sel");}
}
#endif  // VL_DEBUG
