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
            tracep->chgBit(oldp+0,(vlSelf->top__DOT__clk_25));
            tracep->chgSData(oldp+1,(vlSelf->top__DOT__v_addr),10);
            tracep->chgIData(oldp+2,(vlSelf->top__DOT__my_vgaclk__DOT__clkcount),32);
            tracep->chgSData(oldp+3,(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt),10);
            tracep->chgBit(oldp+4,(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid));
        }
        if (VL_UNLIKELY(vlSelf->__Vm_traceActivity[2U])) {
            tracep->chgIData(oldp+5,(vlSelf->top__DOT__mem_out_wire),24);
            tracep->chgSData(oldp+6,(vlSelf->top__DOT__h_addr),10);
            tracep->chgSData(oldp+7,(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt),10);
            tracep->chgBit(oldp+8,(vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid));
        }
        tracep->chgBit(oldp+9,(vlSelf->clk));
        tracep->chgBit(oldp+10,(vlSelf->rst));
        tracep->chgBit(oldp+11,(vlSelf->hsync));
        tracep->chgBit(oldp+12,(vlSelf->vsync));
        tracep->chgBit(oldp+13,(vlSelf->valid));
        tracep->chgCData(oldp+14,(vlSelf->vga_r),8);
        tracep->chgCData(oldp+15,(vlSelf->vga_g),8);
        tracep->chgCData(oldp+16,(vlSelf->vga_b),8);
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
