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
        tracep->declBit(c+1,"Din", false,-1);
        tracep->declBit(c+2,"Clk", false,-1);
        tracep->declBit(c+3,"L_R", false,-1);
        tracep->declBus(c+4,"Dout", false,-1, 7,0);
        tracep->declBus(c+5,"led_o", false,-1, 15,0);
        tracep->declBus(c+6,"hex_o", false,-1, 15,0);
        tracep->declBit(c+1,"top Din", false,-1);
        tracep->declBit(c+2,"top Clk", false,-1);
        tracep->declBit(c+3,"top L_R", false,-1);
        tracep->declBus(c+4,"top Dout", false,-1, 7,0);
        tracep->declBus(c+5,"top led_o", false,-1, 15,0);
        tracep->declBus(c+6,"top hex_o", false,-1, 15,0);
        tracep->declBus(c+7,"top i_shifter N", false,-1, 31,0);
        tracep->declBit(c+1,"top i_shifter In", false,-1);
        tracep->declBit(c+2,"top i_shifter Clk", false,-1);
        tracep->declBit(c+3,"top i_shifter L_R", false,-1);
        tracep->declBus(c+4,"top i_shifter Q", false,-1, 7,0);
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
        tracep->fullBit(oldp+1,(vlSelf->Din));
        tracep->fullBit(oldp+2,(vlSelf->Clk));
        tracep->fullBit(oldp+3,(vlSelf->L_R));
        tracep->fullCData(oldp+4,(vlSelf->Dout),8);
        tracep->fullSData(oldp+5,(vlSelf->led_o),16);
        tracep->fullSData(oldp+6,(vlSelf->hex_o),16);
        tracep->fullIData(oldp+7,(8U),32);
    }
}
