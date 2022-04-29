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
        tracep->declBus(c+1,"a", false,-1, 3,0);
        tracep->declBus(c+2,"b", false,-1, 3,0);
        tracep->declBus(c+3,"sel", false,-1, 2,0);
        tracep->declBit(c+4,"carry", false,-1);
        tracep->declBit(c+5,"zero", false,-1);
        tracep->declBit(c+6,"overflow", false,-1);
        tracep->declBus(c+7,"s", false,-1, 3,0);
        tracep->declBus(c+8,"led_o", false,-1, 15,0);
        tracep->declBus(c+9,"hex_o", false,-1, 15,0);
        tracep->declBus(c+1,"top a", false,-1, 3,0);
        tracep->declBus(c+2,"top b", false,-1, 3,0);
        tracep->declBus(c+3,"top sel", false,-1, 2,0);
        tracep->declBit(c+4,"top carry", false,-1);
        tracep->declBit(c+5,"top zero", false,-1);
        tracep->declBit(c+6,"top overflow", false,-1);
        tracep->declBus(c+7,"top s", false,-1, 3,0);
        tracep->declBus(c+8,"top led_o", false,-1, 15,0);
        tracep->declBus(c+9,"top hex_o", false,-1, 15,0);
        tracep->declBus(c+10,"top a_comp", false,-1, 3,0);
        tracep->declBus(c+11,"top b_comp", false,-1, 3,0);
        tracep->declBus(c+14,"top i_a N", false,-1, 31,0);
        tracep->declBus(c+1,"top i_a a", false,-1, 3,0);
        tracep->declBus(c+10,"top i_a a_comp", false,-1, 3,0);
        tracep->declBus(c+14,"top i_b N", false,-1, 31,0);
        tracep->declBus(c+2,"top i_b a", false,-1, 3,0);
        tracep->declBus(c+11,"top i_b a_comp", false,-1, 3,0);
        tracep->declBus(c+14,"top i_alu N", false,-1, 31,0);
        tracep->declBus(c+1,"top i_alu a", false,-1, 3,0);
        tracep->declBus(c+2,"top i_alu b", false,-1, 3,0);
        tracep->declBus(c+3,"top i_alu sel", false,-1, 2,0);
        tracep->declBit(c+4,"top i_alu carry", false,-1);
        tracep->declBit(c+5,"top i_alu zero", false,-1);
        tracep->declBit(c+6,"top i_alu overflow", false,-1);
        tracep->declBus(c+7,"top i_alu s", false,-1, 3,0);
        tracep->declBus(c+15,"top i_alu ADD", false,-1, 2,0);
        tracep->declBus(c+16,"top i_alu SUB", false,-1, 2,0);
        tracep->declBus(c+17,"top i_alu NOT", false,-1, 2,0);
        tracep->declBus(c+18,"top i_alu AND", false,-1, 2,0);
        tracep->declBus(c+19,"top i_alu OR", false,-1, 2,0);
        tracep->declBus(c+20,"top i_alu XOR", false,-1, 2,0);
        tracep->declBus(c+21,"top i_alu CMP", false,-1, 2,0);
        tracep->declBus(c+22,"top i_alu EQU", false,-1, 2,0);
        tracep->declBus(c+23,"top i_alu flag_add", false,-1, 2,0);
        tracep->declBus(c+24,"top i_alu s_add", false,-1, 3,0);
        tracep->declBus(c+25,"top i_alu flag_logic", false,-1, 2,0);
        tracep->declBus(c+26,"top i_alu s_logic", false,-1, 3,0);
        tracep->declBus(c+27,"top i_alu s_not", false,-1, 3,0);
        tracep->declBus(c+28,"top i_alu s_and", false,-1, 3,0);
        tracep->declBus(c+29,"top i_alu s_or", false,-1, 3,0);
        tracep->declBus(c+30,"top i_alu s_xor", false,-1, 3,0);
        tracep->declBus(c+31,"top i_alu s_cmp", false,-1, 3,0);
        tracep->declBus(c+32,"top i_alu s_equ", false,-1, 3,0);
        tracep->declBus(c+1,"top ia b", false,-1, 3,0);
        tracep->declBus(c+12,"top ia h", false,-1, 7,0);
        tracep->declBus(c+2,"top ib b", false,-1, 3,0);
        tracep->declBus(c+13,"top ib h", false,-1, 7,0);
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
        tracep->fullCData(oldp+1,(vlSelf->a),4);
        tracep->fullCData(oldp+2,(vlSelf->b),4);
        tracep->fullCData(oldp+3,(vlSelf->sel),3);
        tracep->fullBit(oldp+4,(vlSelf->carry));
        tracep->fullBit(oldp+5,(vlSelf->zero));
        tracep->fullBit(oldp+6,(vlSelf->overflow));
        tracep->fullCData(oldp+7,(vlSelf->s),4);
        tracep->fullSData(oldp+8,(vlSelf->led_o),16);
        tracep->fullSData(oldp+9,(vlSelf->hex_o),16);
        tracep->fullCData(oldp+10,(((8U & (IData)(vlSelf->a))
                                     ? ((8U & (IData)(vlSelf->a)) 
                                        | (7U & ((IData)(1U) 
                                                 + 
                                                 (~ (IData)(vlSelf->a)))))
                                     : (IData)(vlSelf->a))),4);
        tracep->fullCData(oldp+11,(((8U & (IData)(vlSelf->b))
                                     ? ((8U & (IData)(vlSelf->b)) 
                                        | (7U & ((IData)(1U) 
                                                 + 
                                                 (~ (IData)(vlSelf->b)))))
                                     : (IData)(vlSelf->b))),4);
        tracep->fullCData(oldp+12,(vlSelf->top__DOT____Vcellout__ia__h),8);
        tracep->fullCData(oldp+13,(vlSelf->top__DOT____Vcellout__ib__h),8);
        tracep->fullIData(oldp+14,(4U),32);
        tracep->fullCData(oldp+15,(0U),3);
        tracep->fullCData(oldp+16,(1U),3);
        tracep->fullCData(oldp+17,(2U),3);
        tracep->fullCData(oldp+18,(3U),3);
        tracep->fullCData(oldp+19,(4U),3);
        tracep->fullCData(oldp+20,(5U),3);
        tracep->fullCData(oldp+21,(6U),3);
        tracep->fullCData(oldp+22,(7U),3);
        tracep->fullCData(oldp+23,(vlSelf->top__DOT__i_alu__DOT__flag_add),3);
        tracep->fullCData(oldp+24,(vlSelf->top__DOT__i_alu__DOT__s_add),4);
        tracep->fullCData(oldp+25,(vlSelf->top__DOT__i_alu__DOT__flag_logic),3);
        tracep->fullCData(oldp+26,(vlSelf->top__DOT__i_alu__DOT__s_logic),4);
        tracep->fullCData(oldp+27,(vlSelf->top__DOT__i_alu__DOT__s_not),4);
        tracep->fullCData(oldp+28,(vlSelf->top__DOT__i_alu__DOT__s_and),4);
        tracep->fullCData(oldp+29,(vlSelf->top__DOT__i_alu__DOT__s_or),4);
        tracep->fullCData(oldp+30,(vlSelf->top__DOT__i_alu__DOT__s_xor),4);
        tracep->fullCData(oldp+31,(vlSelf->top__DOT__i_alu__DOT__s_cmp),4);
        tracep->fullCData(oldp+32,(vlSelf->top__DOT__i_alu__DOT__s_equ),4);
    }
}
