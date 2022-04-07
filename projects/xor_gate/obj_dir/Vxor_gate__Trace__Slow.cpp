// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Tracing implementation internals
#include "verilated_vcd_c.h"
#include "Vxor_gate__Syms.h"


void Vxor_gate___024root__traceInitSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) VL_ATTR_COLD;

void Vxor_gate___024root__traceInitTop(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        Vxor_gate___024root__traceInitSub0(vlSelf, tracep);
    }
}

void Vxor_gate___024root__traceInitSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    const int c = vlSymsp->__Vm_baseCode;
    if (false && tracep && c) {}  // Prevent unused
    // Body
    {
        tracep->declBit(c+1,"a", false,-1);
        tracep->declBit(c+2,"b", false,-1);
        tracep->declBit(c+3,"f", false,-1);
        tracep->declBit(c+1,"xor_gate a", false,-1);
        tracep->declBit(c+2,"xor_gate b", false,-1);
        tracep->declBit(c+3,"xor_gate f", false,-1);
    }
}

void Vxor_gate___024root__traceFullTop0(void* voidSelf, VerilatedVcd* tracep) VL_ATTR_COLD;
void Vxor_gate___024root__traceChgTop0(void* voidSelf, VerilatedVcd* tracep);
void Vxor_gate___024root__traceCleanup(void* voidSelf, VerilatedVcd* /*unused*/);

void Vxor_gate___024root__traceRegister(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        tracep->addFullCb(&Vxor_gate___024root__traceFullTop0, vlSelf);
        tracep->addChgCb(&Vxor_gate___024root__traceChgTop0, vlSelf);
        tracep->addCleanupCb(&Vxor_gate___024root__traceCleanup, vlSelf);
    }
}

void Vxor_gate___024root__traceFullSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) VL_ATTR_COLD;

void Vxor_gate___024root__traceFullTop0(void* voidSelf, VerilatedVcd* tracep) {
    Vxor_gate___024root* const __restrict vlSelf = static_cast<Vxor_gate___024root*>(voidSelf);
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        Vxor_gate___024root__traceFullSub0((&vlSymsp->TOP), tracep);
    }
}

void Vxor_gate___024root__traceFullSub0(Vxor_gate___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vxor_gate__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    vluint32_t* const oldp = tracep->oldp(vlSymsp->__Vm_baseCode);
    if (false && oldp) {}  // Prevent unused
    // Body
    {
        tracep->fullBit(oldp+1,(vlSelf->a));
        tracep->fullBit(oldp+2,(vlSelf->b));
        tracep->fullBit(oldp+3,(vlSelf->f));
    }
}
