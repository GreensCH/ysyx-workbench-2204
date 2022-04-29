// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Tracing implementation internals
#include "verilated_vcd_c.h"
#include "Vtop__Syms.h"


void Vtop___024root__traceChgSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep);

void Vtop___024root__traceChgTop0(void* voidSelf, VerilatedVcd* tracep) {
    Vtop___024root* const __restrict vlSelf = static_cast<Vtop___024root*>(voidSelf);
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    if (VL_UNLIKELY(!vlSymsp->__Vm_activity)) return;
    // Body
    {
        Vtop___024root__traceChgSub0((&vlSymsp->TOP), tracep);
    }
}

void Vtop___024root__traceChgSub0(Vtop___024root* vlSelf, VerilatedVcd* tracep) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    vluint32_t* const oldp = tracep->oldp(vlSymsp->__Vm_baseCode + 1);
    if (false && oldp) {}  // Prevent unused
    // Body
    {
        if (VL_UNLIKELY(vlSelf->__Vm_traceActivity[1U])) {
            tracep->chgSData(oldp+0,(vlSelf->top__DOT__i0__DOT____Vcellinp__i0____pinNumber4),16);
            tracep->chgCData(oldp+1,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[0]),4);
            tracep->chgCData(oldp+2,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[1]),4);
            tracep->chgCData(oldp+3,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[2]),4);
            tracep->chgCData(oldp+4,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[3]),4);
            tracep->chgCData(oldp+5,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[0]),2);
            tracep->chgCData(oldp+6,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[1]),2);
            tracep->chgCData(oldp+7,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[2]),2);
            tracep->chgCData(oldp+8,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[3]),2);
            tracep->chgCData(oldp+9,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[0]),2);
            tracep->chgCData(oldp+10,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[1]),2);
            tracep->chgCData(oldp+11,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[2]),2);
            tracep->chgCData(oldp+12,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[3]),2);
            tracep->chgCData(oldp+13,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__lut_out),2);
            tracep->chgBit(oldp+14,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__hit));
        }
        tracep->chgCData(oldp+15,(vlSelf->X0),2);
        tracep->chgCData(oldp+16,(vlSelf->X1),2);
        tracep->chgCData(oldp+17,(vlSelf->X2),2);
        tracep->chgCData(oldp+18,(vlSelf->X3),2);
        tracep->chgCData(oldp+19,(vlSelf->Y),2);
        tracep->chgCData(oldp+20,(vlSelf->F),2);
    }
}

void Vtop___024root__traceCleanup(void* voidSelf, VerilatedVcd* /*unused*/) {
    Vtop___024root* const __restrict vlSelf = static_cast<Vtop___024root*>(voidSelf);
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        vlSymsp->__Vm_activity = false;
        vlSymsp->TOP.__Vm_traceActivity[0U] = 0U;
        vlSymsp->TOP.__Vm_traceActivity[1U] = 0U;
    }
}
