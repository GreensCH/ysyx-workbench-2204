// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Tracing implementation internals
#include "verilated_vcd_c.h"
#include "Vxor_gate__Syms.h"


void Vxor_gate___024root__traceChgSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep);

void Vxor_gate___024root__traceChgTop0(void* voidSelf, VerilatedVcd* tracep) {
    Vxor_gate___024root* const __restrict vlSelf = static_cast<Vxor_gate___024root*>(voidSelf);
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    if (VL_UNLIKELY(!vlSymsp->__Vm_activity)) return;
    // Body
    {
        Vxor_gate___024root__traceChgSub0((&vlSymsp->TOP), tracep);
    }
}

void Vxor_gate___024root__traceChgSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    vluint32_t* const oldp = tracep->oldp(vlSymsp->__Vm_baseCode + 1);
    if (false && oldp) {}  // Prevent unused
    // Body
    {
        tracep->chgBit(oldp+0,(vlSelf->a));
        tracep->chgBit(oldp+1,(vlSelf->b));
        tracep->chgBit(oldp+2,(vlSelf->f));
    }
}

void Vxor_gate___024root__traceCleanup(void* voidSelf, VerilatedVcd* /*unused*/) {
    VlUnpacked<CData/*0:0*/, 1> __Vm_traceActivity;
    Vxor_gate___024root* const __restrict vlSelf = static_cast<Vxor_gate___024root*>(voidSelf);
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        vlSymsp->__Vm_activity = false;
        __Vm_traceActivity[0U] = 0U;
    }
}
