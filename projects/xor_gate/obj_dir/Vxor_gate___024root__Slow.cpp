// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vxor_gate.h for the primary calling header

#include "Vxor_gate___024root.h"
#include "Vxor_gate__Syms.h"

//==========


void Vxor_gate___024root___ctor_var_reset(Vxor_gate___024root* vlSelf);

Vxor_gate___024root::Vxor_gate___024root(const char* _vcname__)
    : VerilatedModule(_vcname__)
 {
    // Reset structure values
    Vxor_gate___024root___ctor_var_reset(this);
}

void Vxor_gate___024root::__Vconfigure(Vxor_gate__Syms* _vlSymsp, bool first) {
    if (false && first) {}  // Prevent unused
    this->vlSymsp = _vlSymsp;
}

Vxor_gate___024root::~Vxor_gate___024root() {
}

void Vxor_gate___024root___initial__TOP__1(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___initial__TOP__1\n"); );
    // Variables
    VlWide<4>/*127:0*/ __Vtemp1;
    // Body
    __Vtemp1[0U] = 0x2e766364U;
    __Vtemp1[1U] = 0x64756d70U;
    __Vtemp1[2U] = 0x766c745fU;
    __Vtemp1[3U] = 0x2e2fU;
    vlSymsp->_vm_contextp__->dumpfile(VL_CVT_PACK_STR_NW(4, __Vtemp1));
    VL_PRINTF_MT("-Info: xor_gate.v:9: $dumpvar ignored, as Verilated without --trace\n");
}

void Vxor_gate___024root___eval_initial(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___eval_initial\n"); );
    // Body
    Vxor_gate___024root___initial__TOP__1(vlSelf);
}

void Vxor_gate___024root___combo__TOP__2(Vxor_gate___024root* vlSelf);

void Vxor_gate___024root___eval_settle(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___eval_settle\n"); );
    // Body
    Vxor_gate___024root___combo__TOP__2(vlSelf);
}

void Vxor_gate___024root___final(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___final\n"); );
}

void Vxor_gate___024root___ctor_var_reset(Vxor_gate___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vxor_gate___024root___ctor_var_reset\n"); );
    // Body
    vlSelf->a = VL_RAND_RESET_I(1);
    vlSelf->b = VL_RAND_RESET_I(1);
    vlSelf->f = VL_RAND_RESET_I(1);
}
