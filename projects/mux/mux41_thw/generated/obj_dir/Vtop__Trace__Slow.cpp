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
        tracep->declBus(c+16,"X0", false,-1, 1,0);
        tracep->declBus(c+17,"X1", false,-1, 1,0);
        tracep->declBus(c+18,"X2", false,-1, 1,0);
        tracep->declBus(c+19,"X3", false,-1, 1,0);
        tracep->declBus(c+20,"Y", false,-1, 1,0);
        tracep->declBus(c+21,"F", false,-1, 1,0);
        tracep->declBus(c+16,"top X0", false,-1, 1,0);
        tracep->declBus(c+17,"top X1", false,-1, 1,0);
        tracep->declBus(c+18,"top X2", false,-1, 1,0);
        tracep->declBus(c+19,"top X3", false,-1, 1,0);
        tracep->declBus(c+20,"top Y", false,-1, 1,0);
        tracep->declBus(c+21,"top F", false,-1, 1,0);
        tracep->declBus(c+16,"top i0 X0", false,-1, 1,0);
        tracep->declBus(c+17,"top i0 X1", false,-1, 1,0);
        tracep->declBus(c+18,"top i0 X2", false,-1, 1,0);
        tracep->declBus(c+19,"top i0 X3", false,-1, 1,0);
        tracep->declBus(c+20,"top i0 Y", false,-1, 1,0);
        tracep->declBus(c+21,"top i0 F", false,-1, 1,0);
        tracep->declBus(c+22,"top i0 i0 NR_KEY", false,-1, 31,0);
        tracep->declBus(c+23,"top i0 i0 KEY_LEN", false,-1, 31,0);
        tracep->declBus(c+23,"top i0 i0 DATA_LEN", false,-1, 31,0);
        tracep->declBus(c+21,"top i0 i0 out", false,-1, 1,0);
        tracep->declBus(c+20,"top i0 i0 key", false,-1, 1,0);
        tracep->declBus(c+24,"top i0 i0 default_out", false,-1, 1,0);
        tracep->declBus(c+1,"top i0 i0 lut", false,-1, 15,0);
        tracep->declBus(c+22,"top i0 i0 i0 NR_KEY", false,-1, 31,0);
        tracep->declBus(c+23,"top i0 i0 i0 KEY_LEN", false,-1, 31,0);
        tracep->declBus(c+23,"top i0 i0 i0 DATA_LEN", false,-1, 31,0);
        tracep->declBus(c+25,"top i0 i0 i0 HAS_DEFAULT", false,-1, 31,0);
        tracep->declBus(c+21,"top i0 i0 i0 out", false,-1, 1,0);
        tracep->declBus(c+20,"top i0 i0 i0 key", false,-1, 1,0);
        tracep->declBus(c+24,"top i0 i0 i0 default_out", false,-1, 1,0);
        tracep->declBus(c+1,"top i0 i0 i0 lut", false,-1, 15,0);
        tracep->declBus(c+22,"top i0 i0 i0 PAIR_LEN", false,-1, 31,0);
        {int i; for (i=0; i<4; i++) {
                tracep->declBus(c+2+i*1,"top i0 i0 i0 pair_list", true,(i+0), 3,0);}}
        {int i; for (i=0; i<4; i++) {
                tracep->declBus(c+6+i*1,"top i0 i0 i0 key_list", true,(i+0), 1,0);}}
        {int i; for (i=0; i<4; i++) {
                tracep->declBus(c+10+i*1,"top i0 i0 i0 data_list", true,(i+0), 1,0);}}
        tracep->declBus(c+14,"top i0 i0 i0 lut_out", false,-1, 1,0);
        tracep->declBit(c+15,"top i0 i0 i0 hit", false,-1);
        tracep->declBus(c+26,"top i0 i0 i0 i", false,-1, 31,0);
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
        tracep->fullSData(oldp+1,(vlSelf->top__DOT__i0__DOT____Vcellinp__i0____pinNumber4),16);
        tracep->fullCData(oldp+2,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[0]),4);
        tracep->fullCData(oldp+3,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[1]),4);
        tracep->fullCData(oldp+4,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[2]),4);
        tracep->fullCData(oldp+5,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__pair_list[3]),4);
        tracep->fullCData(oldp+6,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[0]),2);
        tracep->fullCData(oldp+7,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[1]),2);
        tracep->fullCData(oldp+8,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[2]),2);
        tracep->fullCData(oldp+9,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__key_list[3]),2);
        tracep->fullCData(oldp+10,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[0]),2);
        tracep->fullCData(oldp+11,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[1]),2);
        tracep->fullCData(oldp+12,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[2]),2);
        tracep->fullCData(oldp+13,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__data_list[3]),2);
        tracep->fullCData(oldp+14,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__lut_out),2);
        tracep->fullBit(oldp+15,(vlSelf->top__DOT__i0__DOT__i0__DOT__i0__DOT__hit));
        tracep->fullCData(oldp+16,(vlSelf->X0),2);
        tracep->fullCData(oldp+17,(vlSelf->X1),2);
        tracep->fullCData(oldp+18,(vlSelf->X2),2);
        tracep->fullCData(oldp+19,(vlSelf->X3),2);
        tracep->fullCData(oldp+20,(vlSelf->Y),2);
        tracep->fullCData(oldp+21,(vlSelf->F),2);
        tracep->fullIData(oldp+22,(4U),32);
        tracep->fullIData(oldp+23,(2U),32);
        tracep->fullCData(oldp+24,(3U),2);
        tracep->fullIData(oldp+25,(1U),32);
        tracep->fullIData(oldp+26,(4U),32);
    }
}
