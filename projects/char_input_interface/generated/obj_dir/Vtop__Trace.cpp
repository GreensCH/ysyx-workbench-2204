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
            tracep->chgSData(oldp+0,(vlSelf->top__DOT__i_cii_top__DOT__v_addr),10);
            tracep->chgIData(oldp+1,(((IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel)
                                       ? 0xffffffU : 0U)),24);
            tracep->chgCData(oldp+2,(vlSelf->top__DOT__i_cii_top__DOT__char_x),7);
            tracep->chgCData(oldp+3,(vlSelf->top__DOT__i_cii_top__DOT__char_y),5);
            tracep->chgCData(oldp+4,(vlSelf->top__DOT__i_cii_top__DOT__pixel_x),4);
            tracep->chgCData(oldp+5,(vlSelf->top__DOT__i_cii_top__DOT__pixel_y),4);
            tracep->chgCData(oldp+6,(((0x1dU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))
                                       ? vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem
                                      [((0x45U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))
                                         ? (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)
                                         : 0U)][vlSelf->top__DOT__i_cii_top__DOT__char_y]
                                       : 0U)),8);
            tracep->chgSData(oldp+7,(((((0x1dU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))
                                         ? vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem
                                        [((0x45U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))
                                           ? (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)
                                           : 0U)][vlSelf->top__DOT__i_cii_top__DOT__char_y]
                                         : 0U) << 4U) 
                                      | (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))),12);
            tracep->chgBit(oldp+8,(vlSelf->top__DOT__i_cii_top__DOT__pixel));
            tracep->chgCData(oldp+9,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo
                                     [vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr]),8);
            tracep->chgCData(oldp+10,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii),8);
            tracep->chgBit(oldp+11,(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready));
            tracep->chgBit(oldp+12,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n));
            tracep->chgBit(oldp+13,(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow));
            tracep->chgBit(oldp+14,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld));
            tracep->chgSData(oldp+15,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt),10);
            tracep->chgBit(oldp+16,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid));
            tracep->chgSData(oldp+17,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter),16);
            tracep->chgSData(oldp+18,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter),16);
            tracep->chgCData(oldp+19,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter),5);
            tracep->chgCData(oldp+20,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter),5);
            tracep->chgSData(oldp+21,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old),10);
            tracep->chgSData(oldp+22,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old),10);
            tracep->chgCData(oldp+23,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii),8);
            tracep->chgBit(oldp+24,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld));
            tracep->chgBit(oldp+25,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_rdy));
            tracep->chgCData(oldp+26,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we),7);
            tracep->chgCData(oldp+27,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we),5);
            tracep->chgCData(oldp+28,(((0x7fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))
                                        ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))),7);
            tracep->chgCData(oldp+29,(((0x1fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))
                                        ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))),5);
            tracep->chgSData(oldp+30,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer),10);
            tracep->chgCData(oldp+31,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[0]),8);
            tracep->chgCData(oldp+32,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[1]),8);
            tracep->chgCData(oldp+33,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[2]),8);
            tracep->chgCData(oldp+34,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[3]),8);
            tracep->chgCData(oldp+35,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[4]),8);
            tracep->chgCData(oldp+36,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[5]),8);
            tracep->chgCData(oldp+37,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[6]),8);
            tracep->chgCData(oldp+38,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[7]),8);
            tracep->chgCData(oldp+39,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr),3);
            tracep->chgCData(oldp+40,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr),3);
            tracep->chgCData(oldp+41,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count),4);
            tracep->chgCData(oldp+42,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync),3);
            tracep->chgBit(oldp+43,((IData)((4U == 
                                             (6U & (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync))))));
            tracep->chgCData(oldp+44,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1),8);
            tracep->chgCData(oldp+45,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state),4);
            tracep->chgCData(oldp+46,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2),8);
        }
        if (VL_UNLIKELY(vlSelf->__Vm_traceActivity[2U])) {
            tracep->chgSData(oldp+47,(vlSelf->top__DOT__i_cii_top__DOT__h_addr),10);
            tracep->chgSData(oldp+48,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt),10);
            tracep->chgBit(oldp+49,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid));
        }
        tracep->chgBit(oldp+50,(vlSelf->clk));
        tracep->chgBit(oldp+51,(vlSelf->rst));
        tracep->chgBit(oldp+52,(vlSelf->rstn));
        tracep->chgBit(oldp+53,(vlSelf->ps2_clk));
        tracep->chgBit(oldp+54,(vlSelf->ps2_data));
        tracep->chgBit(oldp+55,(vlSelf->hsync));
        tracep->chgBit(oldp+56,(vlSelf->vsync));
        tracep->chgBit(oldp+57,(vlSelf->valid));
        tracep->chgCData(oldp+58,(vlSelf->vga_r),8);
        tracep->chgCData(oldp+59,(vlSelf->vga_g),8);
        tracep->chgCData(oldp+60,(vlSelf->vga_b),8);
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
        vlSymsp->TOP.__Vm_traceActivity[2U] = 0U;
    }
}
