// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vxor_gate.h for the primary calling header

#include "Vxor_gate___024root.h"
#include "Vxor_gate__Syms.h"

//==========

VL_INLINE_OPT void Vxor_gate___024root___combo__TOP__2(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___combo__TOP__2\n"); );
    // Body
    vlSelf->f = ((IData)(vlSelf->a) ^ (IData)(vlSelf->b));
}

void Vxor_gate___024root___eval(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___eval\n"); );
    // Body
    Vxor_gate___024root___combo__TOP__2(vlSelf);
}

QData Vxor_gate___024root___change_request_1(Vxor_gate___024root* vlSelf);

VL_INLINE_OPT QData Vxor_gate___024root___change_request(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___change_request\n"); );
    // Body
    return (Vxor_gate___024root___change_request_1(vlSelf));
}

VL_INLINE_OPT QData Vxor_gate___024root___change_request_1(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___change_request_1\n"); );
    // Body
    // Change detection
    QData __req = false;  // Logically a bool
    return __req;
}

#ifdef VL_DEBUG
void Vxor_gate___024root___eval_debug_assertions(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___eval_debug_assertions\n"); );
    // Body
    if (VL_UNLIKELY((vlSelf->a & 0xfeU))) {
        Verilated::overWidthError("a");}
    if (VL_UNLIKELY((vlSelf->b & 0xfeU))) {
        Verilated::overWidthError("b");}
}
#endif  // VL_DEBUG
