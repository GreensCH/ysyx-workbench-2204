// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

VL_INLINE_OPT void Vtop___024root___combo__TOP__1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___combo__TOP__1\n"); );
    // Variables
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__EN;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__A_L;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__L_R;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Dout;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__EN;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__A_L;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__L_R;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Dout;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__EN;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__A_L;
    CData/*0:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__L_R;
    CData/*7:0*/ __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Dout;
    // Body
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__L_R 
        = vlSelf->L_R;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__A_L 
        = vlSelf->A_L;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din 
        = vlSelf->Din;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__EN 
        = (1U & (IData)(vlSelf->Shamt));
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Dout 
        = ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__EN)
            ? ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__L_R)
                ? (0xfeU & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din) 
                            << 1U)) : ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__A_L)
                                        ? ((0x80U & (IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din)) 
                                           | (0x7fU 
                                              & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din) 
                                                 >> 1U)))
                                        : (0x7fU & 
                                           ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din) 
                                            >> 1U))))
            : (IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Din));
    vlSelf->top__DOT__i_barrel_shifter__DOT__Q_1 = __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_1__0__Dout;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__L_R 
        = vlSelf->L_R;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__A_L 
        = vlSelf->A_L;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din 
        = vlSelf->top__DOT__i_barrel_shifter__DOT__Q_1;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__EN 
        = (1U & ((IData)(vlSelf->Shamt) >> 1U));
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Dout 
        = ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__EN)
            ? ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__L_R)
                ? (0xfcU & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din) 
                            << 2U)) : ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__A_L)
                                        ? ((0xc0U & 
                                            ((- (IData)(
                                                        (1U 
                                                         & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din) 
                                                            >> 7U)))) 
                                             << 6U)) 
                                           | (0x3fU 
                                              & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din) 
                                                 >> 2U)))
                                        : (0x3fU & 
                                           ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din) 
                                            >> 2U))))
            : (IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Din));
    vlSelf->top__DOT__i_barrel_shifter__DOT__Q_2 = __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_2__1__Dout;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__L_R 
        = vlSelf->L_R;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__A_L 
        = vlSelf->A_L;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din 
        = vlSelf->top__DOT__i_barrel_shifter__DOT__Q_2;
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__EN 
        = (1U & ((IData)(vlSelf->Shamt) >> 2U));
    __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Dout 
        = ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__EN)
            ? ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__L_R)
                ? (0xf0U & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din) 
                            << 4U)) : ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__A_L)
                                        ? ((0xf0U & 
                                            ((- (IData)(
                                                        (1U 
                                                         & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din) 
                                                            >> 7U)))) 
                                             << 4U)) 
                                           | (0xfU 
                                              & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din) 
                                                 >> 4U)))
                                        : (0xfU & ((IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din) 
                                                   >> 4U))))
            : (IData)(__Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Din));
    vlSelf->Dout = __Vtask_top__DOT__i_barrel_shifter__DOT__shifter_N_4__2__Dout;
    vlSelf->led_o = ((0xff00U & (IData)(vlSelf->led_o)) 
                     | (IData)(vlSelf->Dout));
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
    if (VL_UNLIKELY((vlSelf->Shamt & 0xf8U))) {
        Verilated::overWidthError("Shamt");}
    if (VL_UNLIKELY((vlSelf->A_L & 0xfeU))) {
        Verilated::overWidthError("A_L");}
    if (VL_UNLIKELY((vlSelf->L_R & 0xfeU))) {
        Verilated::overWidthError("L_R");}
}
#endif  // VL_DEBUG
