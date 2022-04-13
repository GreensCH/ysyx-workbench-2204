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
        tracep->declBit(c+10,"clk", false,-1);
        tracep->declBit(c+11,"rst", false,-1);
        tracep->declBit(c+12,"hsync", false,-1);
        tracep->declBit(c+13,"vsync", false,-1);
        tracep->declBit(c+14,"valid", false,-1);
        tracep->declBus(c+15,"vga_r", false,-1, 7,0);
        tracep->declBus(c+16,"vga_g", false,-1, 7,0);
        tracep->declBus(c+17,"vga_b", false,-1, 7,0);
        tracep->declBit(c+10,"top clk", false,-1);
        tracep->declBit(c+11,"top rst", false,-1);
        tracep->declBit(c+12,"top hsync", false,-1);
        tracep->declBit(c+13,"top vsync", false,-1);
        tracep->declBit(c+14,"top valid", false,-1);
        tracep->declBus(c+15,"top vga_r", false,-1, 7,0);
        tracep->declBus(c+16,"top vga_g", false,-1, 7,0);
        tracep->declBus(c+17,"top vga_b", false,-1, 7,0);
        tracep->declBit(c+1,"top clk_25", false,-1);
        tracep->declBit(c+10,"top clk_50", false,-1);
        tracep->declBus(c+6,"top mem_out_wire", false,-1, 23,0);
        tracep->declBus(c+7,"top h_addr", false,-1, 9,0);
        tracep->declBus(c+2,"top v_addr", false,-1, 9,0);
        tracep->declBus(c+18,"top my_vgaclk clk_freq", false,-1, 31,0);
        tracep->declBit(c+10,"top my_vgaclk clkin", false,-1);
        tracep->declBit(c+11,"top my_vgaclk rst", false,-1);
        tracep->declBit(c+19,"top my_vgaclk clken", false,-1);
        tracep->declBit(c+1,"top my_vgaclk clkout", false,-1);
        tracep->declBus(c+20,"top my_vgaclk countlimit", false,-1, 31,0);
        tracep->declBus(c+3,"top my_vgaclk clkcount", false,-1, 31,0);
        tracep->declBit(c+10,"top i_vga_ctrl pclk", false,-1);
        tracep->declBit(c+11,"top i_vga_ctrl reset", false,-1);
        tracep->declBus(c+6,"top i_vga_ctrl vga_data", false,-1, 23,0);
        tracep->declBus(c+7,"top i_vga_ctrl h_addr", false,-1, 9,0);
        tracep->declBus(c+2,"top i_vga_ctrl v_addr", false,-1, 9,0);
        tracep->declBit(c+12,"top i_vga_ctrl hsync", false,-1);
        tracep->declBit(c+13,"top i_vga_ctrl vsync", false,-1);
        tracep->declBit(c+14,"top i_vga_ctrl valid", false,-1);
        tracep->declBus(c+15,"top i_vga_ctrl vga_r", false,-1, 7,0);
        tracep->declBus(c+16,"top i_vga_ctrl vga_g", false,-1, 7,0);
        tracep->declBus(c+17,"top i_vga_ctrl vga_b", false,-1, 7,0);
        tracep->declBus(c+21,"top i_vga_ctrl h_frontporch", false,-1, 31,0);
        tracep->declBus(c+22,"top i_vga_ctrl h_active", false,-1, 31,0);
        tracep->declBus(c+23,"top i_vga_ctrl h_backporch", false,-1, 31,0);
        tracep->declBus(c+24,"top i_vga_ctrl h_total", false,-1, 31,0);
        tracep->declBus(c+25,"top i_vga_ctrl v_frontporch", false,-1, 31,0);
        tracep->declBus(c+26,"top i_vga_ctrl v_active", false,-1, 31,0);
        tracep->declBus(c+27,"top i_vga_ctrl v_backporch", false,-1, 31,0);
        tracep->declBus(c+28,"top i_vga_ctrl v_total", false,-1, 31,0);
        tracep->declBus(c+8,"top i_vga_ctrl x_cnt", false,-1, 9,0);
        tracep->declBus(c+4,"top i_vga_ctrl y_cnt", false,-1, 9,0);
        tracep->declBit(c+9,"top i_vga_ctrl h_valid", false,-1);
        tracep->declBit(c+5,"top i_vga_ctrl v_valid", false,-1);
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
        tracep->fullBit(oldp+1,(vlSelf->top__DOT__clk_25));
        tracep->fullSData(oldp+2,(vlSelf->top__DOT__v_addr),10);
        tracep->fullIData(oldp+3,(vlSelf->top__DOT__my_vgaclk__DOT__clkcount),32);
        tracep->fullSData(oldp+4,(vlSelf->top__DOT__i_vga_ctrl__DOT__y_cnt),10);
        tracep->fullBit(oldp+5,(vlSelf->top__DOT__i_vga_ctrl__DOT__v_valid));
        tracep->fullIData(oldp+6,(vlSelf->top__DOT__mem_out_wire),24);
        tracep->fullSData(oldp+7,(vlSelf->top__DOT__h_addr),10);
        tracep->fullSData(oldp+8,(vlSelf->top__DOT__i_vga_ctrl__DOT__x_cnt),10);
        tracep->fullBit(oldp+9,(vlSelf->top__DOT__i_vga_ctrl__DOT__h_valid));
        tracep->fullBit(oldp+10,(vlSelf->clk));
        tracep->fullBit(oldp+11,(vlSelf->rst));
        tracep->fullBit(oldp+12,(vlSelf->hsync));
        tracep->fullBit(oldp+13,(vlSelf->vsync));
        tracep->fullBit(oldp+14,(vlSelf->valid));
        tracep->fullCData(oldp+15,(vlSelf->vga_r),8);
        tracep->fullCData(oldp+16,(vlSelf->vga_g),8);
        tracep->fullCData(oldp+17,(vlSelf->vga_b),8);
        tracep->fullIData(oldp+18,(0x17d7840U),32);
        tracep->fullBit(oldp+19,(1U));
        tracep->fullIData(oldp+20,(1U),32);
        tracep->fullIData(oldp+21,(0x60U),32);
        tracep->fullIData(oldp+22,(0x90U),32);
        tracep->fullIData(oldp+23,(0x310U),32);
        tracep->fullIData(oldp+24,(0x320U),32);
        tracep->fullIData(oldp+25,(2U),32);
        tracep->fullIData(oldp+26,(0x23U),32);
        tracep->fullIData(oldp+27,(0x203U),32);
        tracep->fullIData(oldp+28,(0x20dU),32);
    }
}
