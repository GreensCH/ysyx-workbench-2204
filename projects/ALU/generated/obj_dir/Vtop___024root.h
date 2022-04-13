// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vtop.h for the primary calling header

#ifndef VERILATED_VTOP___024ROOT_H_
#define VERILATED_VTOP___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class Vtop__Syms;
class Vtop_VerilatedVcd;


//----------

VL_MODULE(Vtop___024root) {
  public:

    // PORTS
    VL_IN8(a,3,0);
    VL_IN8(b,3,0);
    VL_IN8(sel,2,0);
    VL_OUT8(carry,0,0);
    VL_OUT8(zero,0,0);
    VL_OUT8(overflow,0,0);
    VL_OUT8(s,3,0);
    VL_OUT16(led_o,15,0);
    VL_OUT16(hex_o,15,0);

    // LOCAL SIGNALS
    CData/*2:0*/ top__DOT__i_alu__DOT__flag_add;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_add;
    CData/*2:0*/ top__DOT__i_alu__DOT__flag_logic;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_logic;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_not;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_and;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_or;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_xor;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_cmp;
    CData/*3:0*/ top__DOT__i_alu__DOT__s_equ;

    // LOCAL VARIABLES
    CData/*7:0*/ top__DOT____Vcellout__ia__h;
    CData/*7:0*/ top__DOT____Vcellout__ib__h;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_equ__0__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__1__unnamedblk1__DOT__t_as;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_cmp__2__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__3__unnamedblk1__DOT__t_as;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_xor__4__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_xor__4__b;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_xor__4__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_or__5__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_or__5__b;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_or__5__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_and__6__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_and__6__b;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_and__6__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_not__7__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_not__7__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__8__unnamedblk1__DOT__t_as;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__a;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__b;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__carry;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__zero;
    CData/*0:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__overflow;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__s;
    CData/*3:0*/ __Vtask_top__DOT__i_alu__DOT__t_adder__9__unnamedblk1__DOT__t_as;

    // INTERNAL VARIABLES
    Vtop__Syms* vlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(Vtop___024root);  ///< Copying not allowed
  public:
    Vtop___024root(const char* name);
    ~Vtop___024root();

    // INTERNAL METHODS
    void __Vconfigure(Vtop__Syms* symsp, bool first);
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
