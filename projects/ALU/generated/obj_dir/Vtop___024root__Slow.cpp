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

void Vtop___024root___eval_initial(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_initial\n"); );
}

void Vtop___024root___combo__TOP__1(Vtop___024root* vlSelf);

void Vtop___024root___eval_settle(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_settle\n"); );
    // Body
    Vtop___024root___combo__TOP__1(vlSelf);
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
    vlSelf->a = 0;
    vlSelf->b = 0;
    vlSelf->sel = 0;
    vlSelf->carry = 0;
    vlSelf->zero = 0;
    vlSelf->overflow = 0;
    vlSelf->s = 0;
    vlSelf->led_o = 0;
    vlSelf->hex_o = 0;
    vlSelf->top__DOT____Vcellout__ia__h = 0;
    vlSelf->top__DOT____Vcellout__ib__h = 0;
    vlSelf->top__DOT__i_alu__DOT__flag_add = 0;
    vlSelf->top__DOT__i_alu__DOT__s_add = 0;
    vlSelf->top__DOT__i_alu__DOT__flag_logic = 0;
    vlSelf->top__DOT__i_alu__DOT__s_logic = 0;
    vlSelf->top__DOT__i_alu__DOT__s_not = 0;
    vlSelf->top__DOT__i_alu__DOT__s_and = 0;
    vlSelf->top__DOT__i_alu__DOT__s_or = 0;
    vlSelf->top__DOT__i_alu__DOT__s_xor = 0;
    vlSelf->top__DOT__i_alu__DOT__s_cmp = 0;
    vlSelf->top__DOT__i_alu__DOT__s_equ = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_equ__0__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_cmp__2__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_xor__4__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_or__5__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_and__6__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_not__7__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__a = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__b = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__carry = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__zero = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__overflow = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__s = 0;
    vlSelf->__Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as = 0;
}
