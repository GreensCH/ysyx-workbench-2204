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
        tracep->chgCData(oldp+0,(vlSelf->a),4);
        tracep->chgCData(oldp+1,(vlSelf->b),4);
        tracep->chgCData(oldp+2,(vlSelf->sel),3);
        tracep->chgBit(oldp+3,(vlSelf->carry));
        tracep->chgBit(oldp+4,(vlSelf->zero));
        tracep->chgBit(oldp+5,(vlSelf->overflow));
        tracep->chgCData(oldp+6,(vlSelf->s),4);
        tracep->chgSData(oldp+7,(vlSelf->led_o),16);
        tracep->chgSData(oldp+8,(vlSelf->hex_o),16);
        tracep->chgCData(oldp+9,(((8U & (IData)(vlSelf->a))
                                   ? ((8U & (IData)(vlSelf->a)) 
                                      | (7U & ((IData)(1U) 
                                               + (~ (IData)(vlSelf->a)))))
                                   : (IData)(vlSelf->a))),4);
        tracep->chgCData(oldp+10,(((8U & (IData)(vlSelf->b))
                                    ? ((8U & (IData)(vlSelf->b)) 
                                       | (7U & ((IData)(1U) 
                                                + (~ (IData)(vlSelf->b)))))
                                    : (IData)(vlSelf->b))),4);
        tracep->chgCData(oldp+11,(vlSelf->top__DOT____Vcellout__ia__h),8);
        tracep->chgCData(oldp+12,(vlSelf->top__DOT____Vcellout__ib__h),8);
    }
}

void Vtop___024root__traceCleanup(void* voidSelf, VerilatedVcd* /*unused*/) {
    VlUnpacked<CData/*0:0*/, 1> __Vm_traceActivity;
    Vtop___024root* const __restrict vlSelf = static_cast<Vtop___024root*>(voidSelf);
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    // Body
    {
        vlSymsp->__Vm_activity = false;
        __Vm_traceActivity[0U] = 0U;
    }
}
