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
        tracep->declBit(c+1,"Clk", false,-1);
        tracep->declBus(c+2,"Q", false,-1, 7,0);
        tracep->declBit(c+3,"Dout", false,-1);
        tracep->declBus(c+4,"led_o", false,-1, 15,0);
        tracep->declBus(c+5,"hex_o", false,-1, 15,0);
        tracep->declBit(c+1,"top Clk", false,-1);
        tracep->declBus(c+2,"top Q", false,-1, 7,0);
        tracep->declBit(c+3,"top Dout", false,-1);
        tracep->declBus(c+4,"top led_o", false,-1, 15,0);
        tracep->declBus(c+5,"top hex_o", false,-1, 15,0);
        tracep->declBit(c+6,"top In_1", false,-1);
        tracep->declBit(c+7,"top In_2", false,-1);
        tracep->declBus(c+8,"top i_shifter N", false,-1, 31,0);
        tracep->declBit(c+7,"top i_shifter In", false,-1);
        tracep->declBit(c+1,"top i_shifter Clk", false,-1);
        tracep->declBit(c+9,"top i_shifter L_R", false,-1);
        tracep->declBus(c+2,"top i_shifter Q", false,-1, 7,0);
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
        tracep->fullBit(oldp+1,(vlSelf->Clk));
        tracep->fullCData(oldp+2,(vlSelf->Q),8);
        tracep->fullBit(oldp+3,(vlSelf->Dout));
        tracep->fullSData(oldp+4,(vlSelf->led_o),16);
        tracep->fullSData(oldp+5,(vlSelf->hex_o),16);
        tracep->fullBit(oldp+6,((1U & VL_REDXOR_32(
                                                   (0x1dU 
                                                    & (IData)(vlSelf->Q))))));
        tracep->fullBit(oldp+7,(((0U == (IData)(vlSelf->Q))
                                  ? 1U : (1U & VL_REDXOR_32(
                                                            (0x1dU 
                                                             & (IData)(vlSelf->Q)))))));
        tracep->fullIData(oldp+8,(8U),32);
        tracep->fullBit(oldp+9,(0U));
    }
}
