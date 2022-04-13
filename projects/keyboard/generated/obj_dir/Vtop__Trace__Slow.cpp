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
        tracep->declBit(c+35,"clk", false,-1);
        tracep->declBit(c+36,"clrn", false,-1);
        tracep->declBit(c+37,"ps2_clk", false,-1);
        tracep->declBit(c+38,"ps2_data", false,-1);
        tracep->declBus(c+39,"led_o", false,-1, 15,0);
        tracep->declQuad(c+40,"hex_o", false,-1, 47,0);
        tracep->declBit(c+35,"top clk", false,-1);
        tracep->declBit(c+36,"top clrn", false,-1);
        tracep->declBit(c+37,"top ps2_clk", false,-1);
        tracep->declBit(c+38,"top ps2_data", false,-1);
        tracep->declBus(c+39,"top led_o", false,-1, 15,0);
        tracep->declQuad(c+40,"top hex_o", false,-1, 47,0);
        tracep->declBus(c+1,"top data", false,-1, 7,0);
        tracep->declBit(c+2,"top ready", false,-1);
        tracep->declBit(c+3,"top nextdata_n", false,-1);
        tracep->declBit(c+4,"top overflow", false,-1);
        tracep->declBit(c+35,"top i_kbd_control clk", false,-1);
        tracep->declBit(c+36,"top i_kbd_control clrn", false,-1);
        tracep->declBit(c+37,"top i_kbd_control ps2_clk", false,-1);
        tracep->declBit(c+38,"top i_kbd_control ps2_data", false,-1);
        tracep->declBit(c+3,"top i_kbd_control nextdata_n", false,-1);
        tracep->declBus(c+1,"top i_kbd_control data", false,-1, 7,0);
        tracep->declBit(c+2,"top i_kbd_control ready", false,-1);
        tracep->declBit(c+4,"top i_kbd_control overflow", false,-1);
        tracep->declBus(c+5,"top i_kbd_control buffer", false,-1, 9,0);
        {int i; for (i=0; i<8; i++) {
                tracep->declBus(c+6+i*1,"top i_kbd_control fifo", true,(i+0), 7,0);}}
        tracep->declBus(c+14,"top i_kbd_control w_ptr", false,-1, 2,0);
        tracep->declBus(c+15,"top i_kbd_control r_ptr", false,-1, 2,0);
        tracep->declBus(c+16,"top i_kbd_control count", false,-1, 3,0);
        tracep->declBus(c+17,"top i_kbd_control ps2_clk_sync", false,-1, 2,0);
        tracep->declBit(c+18,"top i_kbd_control sampling", false,-1);
        tracep->declBit(c+35,"top i_show_ps2kbd clk", false,-1);
        tracep->declBit(c+36,"top i_show_ps2kbd clrn", false,-1);
        tracep->declBus(c+1,"top i_show_ps2kbd data", false,-1, 7,0);
        tracep->declBit(c+2,"top i_show_ps2kbd ready", false,-1);
        tracep->declQuad(c+40,"top i_show_ps2kbd hex_o", false,-1, 47,0);
        tracep->declBit(c+3,"top i_show_ps2kbd nextdata_n", false,-1);
        tracep->declBus(c+19,"top i_show_ps2kbd h1", false,-1, 3,0);
        tracep->declBus(c+20,"top i_show_ps2kbd h2", false,-1, 3,0);
        tracep->declBus(c+21,"top i_show_ps2kbd h3", false,-1, 3,0);
        tracep->declBus(c+22,"top i_show_ps2kbd h4", false,-1, 3,0);
        tracep->declBus(c+23,"top i_show_ps2kbd h5", false,-1, 3,0);
        tracep->declBus(c+24,"top i_show_ps2kbd h6", false,-1, 3,0);
        tracep->declBus(c+25,"top i_show_ps2kbd cnt", false,-1, 7,0);
        tracep->declBus(c+26,"top i_show_ps2kbd ascii_data", false,-1, 7,0);
        tracep->declBus(c+27,"top i_show_ps2kbd data_cache1", false,-1, 7,0);
        tracep->declBus(c+28,"top i_show_ps2kbd data_cache2", false,-1, 7,0);
        tracep->declBus(c+28,"top i_show_ps2kbd i_rpa addr", false,-1, 7,0);
        tracep->declBus(c+26,"top i_show_ps2kbd i_rpa data", false,-1, 7,0);
        tracep->declBus(c+19,"top i_show_ps2kbd ihex_1 b", false,-1, 3,0);
        tracep->declBus(c+29,"top i_show_ps2kbd ihex_1 h", false,-1, 7,0);
        tracep->declBus(c+20,"top i_show_ps2kbd ihex_2 b", false,-1, 3,0);
        tracep->declBus(c+30,"top i_show_ps2kbd ihex_2 h", false,-1, 7,0);
        tracep->declBus(c+21,"top i_show_ps2kbd ihex_3 b", false,-1, 3,0);
        tracep->declBus(c+31,"top i_show_ps2kbd ihex_3 h", false,-1, 7,0);
        tracep->declBus(c+22,"top i_show_ps2kbd ihex_4 b", false,-1, 3,0);
        tracep->declBus(c+32,"top i_show_ps2kbd ihex_4 h", false,-1, 7,0);
        tracep->declBus(c+23,"top i_show_ps2kbd ihex_5 b", false,-1, 3,0);
        tracep->declBus(c+33,"top i_show_ps2kbd ihex_5 h", false,-1, 7,0);
        tracep->declBus(c+24,"top i_show_ps2kbd ihex_6 b", false,-1, 3,0);
        tracep->declBus(c+34,"top i_show_ps2kbd ihex_6 h", false,-1, 7,0);
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
        tracep->fullCData(oldp+1,(vlSelf->top__DOT__data),8);
        tracep->fullBit(oldp+2,(vlSelf->top__DOT__ready));
        tracep->fullBit(oldp+3,(vlSelf->top__DOT__nextdata_n));
        tracep->fullBit(oldp+4,(vlSelf->top__DOT__overflow));
        tracep->fullSData(oldp+5,(vlSelf->top__DOT__i_kbd_control__DOT__buffer),10);
        tracep->fullCData(oldp+6,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[0]),8);
        tracep->fullCData(oldp+7,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[1]),8);
        tracep->fullCData(oldp+8,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[2]),8);
        tracep->fullCData(oldp+9,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[3]),8);
        tracep->fullCData(oldp+10,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[4]),8);
        tracep->fullCData(oldp+11,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[5]),8);
        tracep->fullCData(oldp+12,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[6]),8);
        tracep->fullCData(oldp+13,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[7]),8);
        tracep->fullCData(oldp+14,(vlSelf->top__DOT__i_kbd_control__DOT__w_ptr),3);
        tracep->fullCData(oldp+15,(vlSelf->top__DOT__i_kbd_control__DOT__r_ptr),3);
        tracep->fullCData(oldp+16,(vlSelf->top__DOT__i_kbd_control__DOT__count),4);
        tracep->fullCData(oldp+17,(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync),3);
        tracep->fullBit(oldp+18,((IData)((4U == (6U 
                                                 & (IData)(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync))))));
        tracep->fullCData(oldp+19,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2))),4);
        tracep->fullCData(oldp+20,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2) 
                                            >> 4U))),4);
        tracep->fullCData(oldp+21,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data))),4);
        tracep->fullCData(oldp+22,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data) 
                                            >> 4U))),4);
        tracep->fullCData(oldp+23,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt))),4);
        tracep->fullCData(oldp+24,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt) 
                                            >> 4U))),4);
        tracep->fullCData(oldp+25,(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt),8);
        tracep->fullCData(oldp+26,(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data),8);
        tracep->fullCData(oldp+27,(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1),8);
        tracep->fullCData(oldp+28,(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2),8);
        tracep->fullCData(oldp+29,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h),8);
        tracep->fullCData(oldp+30,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h),8);
        tracep->fullCData(oldp+31,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h),8);
        tracep->fullCData(oldp+32,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h),8);
        tracep->fullCData(oldp+33,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_5__h),8);
        tracep->fullCData(oldp+34,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_6__h),8);
        tracep->fullBit(oldp+35,(vlSelf->clk));
        tracep->fullBit(oldp+36,(vlSelf->clrn));
        tracep->fullBit(oldp+37,(vlSelf->ps2_clk));
        tracep->fullBit(oldp+38,(vlSelf->ps2_data));
        tracep->fullSData(oldp+39,(vlSelf->led_o),16);
        tracep->fullQData(oldp+40,(vlSelf->hex_o),48);
    }
}
