// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Tracing implementation internals
#include "verilated_vcd_c.h"
#include "Vtop__Syms.h"


void Vtop___024root__traceInitSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep) VL_ATTR_COLD;

void Vtop___024root__traceInitTop(Vtop___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        Vtop___024root__traceInitSub0(vlSelf, tracep);
    }
}

void Vtop___024root__traceInitSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    const int c = vlSymsp->__Vm_baseCode;
    if (false && tracep && c) {}  // Prevent unused
    // Body
    {
        tracep->declBus(c+4,"sw_i", false,-1, 7,0);
        tracep->declBit(c+5,"en_i", false,-1);
        tracep->declBit(c+6,"led_o", false,-1);
        tracep->declBus(c+7,"hex_o", false,-1, 7,0);
        tracep->declBus(c+4,"top sw_i", false,-1, 7,0);
        tracep->declBit(c+5,"top en_i", false,-1);
        tracep->declBit(c+6,"top led_o", false,-1);
        tracep->declBus(c+7,"top hex_o", false,-1, 7,0);
        tracep->declBus(c+1,"top Y", false,-1, 2,0);
        tracep->declBus(c+8,"top b", false,-1, 3,0);
        tracep->declBus(c+4,"top i0 I", false,-1, 7,0);
        tracep->declBit(c+5,"top i0 EI", false,-1);
        tracep->declBus(c+1,"top i0 Y", false,-1, 2,0);
        tracep->declBit(c+6,"top i0 GS", false,-1);
        tracep->declBus(c+2,"top i1 b", false,-1, 3,0);
        tracep->declBus(c+3,"top i1 h", false,-1, 6,0);
    }
}

void Vtop___024root__traceFullTop0(void* voidSelf, VerilatedVcd* tracep) VL_ATTR_COLD;
void Vtop___024root__traceChgTop0(void* voidSelf, VerilatedVcd* tracep);
void Vtop___024root__traceCleanup(void* voidSelf, VerilatedVcd* /*unused*/);

void Vtop___024root__traceRegister(Vtop___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        tracep->addFullCb(&Vtop___024root__traceFullTop0, vlSelf);
        tracep->addChgCb(&Vtop___024root__traceChgTop0, vlSelf);
        tracep->addCleanupCb(&Vtop___024root__traceCleanup, vlSelf);
    }
}

void Vtop___024root__traceFullSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep) VL_ATTR_COLD;

void Vtop___024root__traceFullTop0(void* voidSelf, VerilatedVcd* tracep) {
    Vtop___024root* const __restrict vlSelf = static_cast<Vtop___024root*>(voidSelf);
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        Vtop___024root__traceFullSub0((&vlSymsp->TOP), tracep);
    }
}

void Vtop___024root__traceFullSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    vluint32_t* const oldp = tracep->oldp(vlSymsp->__Vm_baseCode);
    if (false && oldp) {}  // Prevent unused
    // Body
    {
        tracep->fullCData(oldp+1,(vlSelf->top__DOT__Y),3);
        tracep->fullCData(oldp+2,(vlSelf->top__DOT__Y),4);
        tracep->fullCData(oldp+3,(vlSelf->top__DOT____Vcellout__i1__h),7);
        tracep->fullCData(oldp+4,(vlSelf->sw_i),8);
        tracep->fullBit(oldp+5,(vlSelf->en_i));
        tracep->fullBit(oldp+6,(vlSelf->led_o));
        tracep->fullCData(oldp+7,(vlSelf->hex_o),8);
        tracep->fullCData(oldp+8,(vlSelf->top__DOT__b),4);
    }
}
