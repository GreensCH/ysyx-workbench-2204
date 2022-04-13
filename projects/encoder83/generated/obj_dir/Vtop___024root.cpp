// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

extern const VlUnpacked<CData/*6:0*/, 16> Vtop__ConstPool__TABLE_a005ec2b_0;

VL_INLINE_OPT void Vtop___024root___combo__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___combo__TOP__1\n"); );
    // Variables
    CData/*3:0*/ __Vtableidx1;
    // Body
    vlSelf->led_o = ((IData)(vlSelf->en_i) & (IData)(
                                                     (0U 
                                                      != 
                                                      (0xffU 
                                                       & (IData)(vlSelf->sw_i)))));
    vlSelf->top__DOT__Y = ((((IData)(vlSelf->en_i) 
                             & (IData)((0U != (0xf0U 
                                               & (IData)(vlSelf->sw_i))))) 
                            << 2U) | ((((IData)(vlSelf->en_i) 
                                        & (((IData)(
                                                    (0U 
                                                     != 
                                                     (0xc0U 
                                                      & (IData)(vlSelf->sw_i)))) 
                                            | (IData)(
                                                      (8U 
                                                       == 
                                                       (0x38U 
                                                        & (IData)(vlSelf->sw_i))))) 
                                           | (IData)(
                                                     (4U 
                                                      == 
                                                      (0x34U 
                                                       & (IData)(vlSelf->sw_i)))))) 
                                       << 1U) | ((IData)(vlSelf->en_i) 
                                                 & (((((IData)(vlSelf->sw_i) 
                                                       >> 7U) 
                                                      | (IData)(
                                                                (0x20U 
                                                                 == 
                                                                 (0x60U 
                                                                  & (IData)(vlSelf->sw_i))))) 
                                                     | (IData)(
                                                               (8U 
                                                                == 
                                                                (0x58U 
                                                                 & (IData)(vlSelf->sw_i))))) 
                                                    | (IData)(
                                                              (2U 
                                                               == 
                                                               (0x56U 
                                                                & (IData)(vlSelf->sw_i))))))));
    __Vtableidx1 = vlSelf->top__DOT__Y;
    vlSelf->top__DOT____Vcellout__i1__h = Vtop__ConstPool__TABLE_a005ec2b_0
        [__Vtableidx1];
    vlSelf->hex_o = (1U | ((IData)(vlSelf->top__DOT____Vcellout__i1__h) 
                           << 1U));
}

void Vtop___024root___eval(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval\n"); );
    // Body
    Vtop___024root___combo__TOP__1(vlSelf);
    vlSelf->__Vm_traceActivity[1U] = 1U;
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
    if (VL_UNLIKELY((vlSelf->en_i & 0xfeU))) {
        Verilated::overWidthError("en_i");}
}
#endif  // VL_DEBUG
