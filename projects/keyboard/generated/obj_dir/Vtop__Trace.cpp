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
            tracep->chgCData(oldp+0,(vlSelf->top__DOT__i_kbd_control__DOT__fifo
                                     [vlSelf->top__DOT__i_kbd_control__DOT__r_ptr]),8);
            tracep->chgBit(oldp+1,(vlSelf->top__DOT__ready));
            tracep->chgBit(oldp+2,(vlSelf->top__DOT__nextdata_n));
            tracep->chgBit(oldp+3,(vlSelf->top__DOT__overflow));
            tracep->chgSData(oldp+4,(vlSelf->top__DOT__i_kbd_control__DOT__buffer),10);
            tracep->chgCData(oldp+5,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[0]),8);
            tracep->chgCData(oldp+6,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[1]),8);
            tracep->chgCData(oldp+7,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[2]),8);
            tracep->chgCData(oldp+8,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[3]),8);
            tracep->chgCData(oldp+9,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[4]),8);
            tracep->chgCData(oldp+10,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[5]),8);
            tracep->chgCData(oldp+11,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[6]),8);
            tracep->chgCData(oldp+12,(vlSelf->top__DOT__i_kbd_control__DOT__fifo[7]),8);
            tracep->chgCData(oldp+13,(vlSelf->top__DOT__i_kbd_control__DOT__w_ptr),3);
            tracep->chgCData(oldp+14,(vlSelf->top__DOT__i_kbd_control__DOT__r_ptr),3);
            tracep->chgCData(oldp+15,(vlSelf->top__DOT__i_kbd_control__DOT__count),4);
            tracep->chgCData(oldp+16,(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync),3);
            tracep->chgBit(oldp+17,((IData)((4U == 
                                             (6U & (IData)(vlSelf->top__DOT__i_kbd_control__DOT__ps2_clk_sync))))));
            tracep->chgCData(oldp+18,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2))),4);
            tracep->chgCData(oldp+19,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2) 
                                               >> 4U))),4);
            tracep->chgCData(oldp+20,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data))),4);
            tracep->chgCData(oldp+21,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data) 
                                               >> 4U))),4);
            tracep->chgCData(oldp+22,((0xfU & (IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt))),4);
            tracep->chgCData(oldp+23,((0xfU & ((IData)(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt) 
                                               >> 4U))),4);
            tracep->chgCData(oldp+24,(vlSelf->top__DOT__i_show_ps2kbd__DOT__cnt),8);
            tracep->chgCData(oldp+25,(vlSelf->top__DOT__i_show_ps2kbd__DOT__ascii_data),8);
            tracep->chgCData(oldp+26,(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache1),8);
            tracep->chgBit(oldp+27,(vlSelf->top__DOT__i_show_ps2kbd__DOT__busy));
            tracep->chgCData(oldp+28,(vlSelf->top__DOT__i_show_ps2kbd__DOT__state),4);
            tracep->chgCData(oldp+29,(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2),8);
            tracep->chgCData(oldp+30,(vlSelf->top__DOT__i_show_ps2kbd__DOT__data_cache2_old),8);
            tracep->chgCData(oldp+31,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_1__h),8);
            tracep->chgCData(oldp+32,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_2__h),8);
            tracep->chgCData(oldp+33,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_3__h),8);
            tracep->chgCData(oldp+34,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_4__h),8);
            tracep->chgCData(oldp+35,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_5__h),8);
            tracep->chgCData(oldp+36,(vlSelf->top__DOT__i_show_ps2kbd__DOT____Vcellout__ihex_6__h),8);
        }
        tracep->chgBit(oldp+37,(vlSelf->clk));
        tracep->chgBit(oldp+38,(vlSelf->clrn));
        tracep->chgBit(oldp+39,(vlSelf->ps2_clk));
        tracep->chgBit(oldp+40,(vlSelf->ps2_data));
        tracep->chgSData(oldp+41,(vlSelf->led_o),16);
        tracep->chgQData(oldp+42,(vlSelf->hex_o),48);
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
