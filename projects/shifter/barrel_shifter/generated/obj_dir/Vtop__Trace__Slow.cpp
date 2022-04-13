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
        tracep->declBus(c+1,"Din", false,-1, 7,0);
        tracep->declBus(c+2,"Shamt", false,-1, 2,0);
        tracep->declBit(c+3,"A_L", false,-1);
        tracep->declBit(c+4,"L_R", false,-1);
        tracep->declBus(c+5,"Dout", false,-1, 7,0);
        tracep->declBus(c+6,"led_o", false,-1, 15,0);
        tracep->declBus(c+7,"hex_o", false,-1, 15,0);
        tracep->declBus(c+1,"top Din", false,-1, 7,0);
        tracep->declBus(c+2,"top Shamt", false,-1, 2,0);
        tracep->declBit(c+3,"top A_L", false,-1);
        tracep->declBit(c+4,"top L_R", false,-1);
        tracep->declBus(c+5,"top Dout", false,-1, 7,0);
        tracep->declBus(c+6,"top led_o", false,-1, 15,0);
        tracep->declBus(c+7,"top hex_o", false,-1, 15,0);
        tracep->declBus(c+10,"top i_barrel_shifter N", false,-1, 31,0);
        tracep->declBus(c+1,"top i_barrel_shifter Din", false,-1, 7,0);
        tracep->declBus(c+2,"top i_barrel_shifter Shamt", false,-1, 2,0);
        tracep->declBit(c+3,"top i_barrel_shifter A_L", false,-1);
        tracep->declBit(c+4,"top i_barrel_shifter L_R", false,-1);
        tracep->declBus(c+5,"top i_barrel_shifter Dout", false,-1, 7,0);
        tracep->declBus(c+8,"top i_barrel_shifter Q_1", false,-1, 7,0);
        tracep->declBus(c+9,"top i_barrel_shifter Q_2", false,-1, 7,0);
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
        tracep->fullCData(oldp+1,(vlSelf->Din),8);
        tracep->fullCData(oldp+2,(vlSelf->Shamt),3);
        tracep->fullBit(oldp+3,(vlSelf->A_L));
        tracep->fullBit(oldp+4,(vlSelf->L_R));
        tracep->fullCData(oldp+5,(vlSelf->Dout),8);
        tracep->fullSData(oldp+6,(vlSelf->led_o),16);
        tracep->fullSData(oldp+7,(vlSelf->hex_o),16);
        tracep->fullCData(oldp+8,(vlSelf->top__DOT__i_barrel_shifter__DOT__Q_1),8);
        tracep->fullCData(oldp+9,(vlSelf->top__DOT__i_barrel_shifter__DOT__Q_2),8);
        tracep->fullIData(oldp+10,(8U),32);
    }
}
