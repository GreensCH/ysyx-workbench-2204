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
        tracep->declBit(c+51,"clk", false,-1);
        tracep->declBit(c+52,"rst", false,-1);
        tracep->declBit(c+53,"rstn", false,-1);
        tracep->declBit(c+54,"ps2_clk", false,-1);
        tracep->declBit(c+55,"ps2_data", false,-1);
        tracep->declBit(c+56,"hsync", false,-1);
        tracep->declBit(c+57,"vsync", false,-1);
        tracep->declBit(c+58,"valid", false,-1);
        tracep->declBus(c+59,"vga_r", false,-1, 7,0);
        tracep->declBus(c+60,"vga_g", false,-1, 7,0);
        tracep->declBus(c+61,"vga_b", false,-1, 7,0);
        tracep->declBit(c+51,"top clk", false,-1);
        tracep->declBit(c+52,"top rst", false,-1);
        tracep->declBit(c+53,"top rstn", false,-1);
        tracep->declBit(c+54,"top ps2_clk", false,-1);
        tracep->declBit(c+55,"top ps2_data", false,-1);
        tracep->declBit(c+56,"top hsync", false,-1);
        tracep->declBit(c+57,"top vsync", false,-1);
        tracep->declBit(c+58,"top valid", false,-1);
        tracep->declBus(c+59,"top vga_r", false,-1, 7,0);
        tracep->declBus(c+60,"top vga_g", false,-1, 7,0);
        tracep->declBus(c+61,"top vga_b", false,-1, 7,0);
        tracep->declBit(c+51,"top i_cii_top clk", false,-1);
        tracep->declBit(c+52,"top i_cii_top rst", false,-1);
        tracep->declBit(c+53,"top i_cii_top rstn", false,-1);
        tracep->declBit(c+54,"top i_cii_top ps2_clk", false,-1);
        tracep->declBit(c+55,"top i_cii_top ps2_data", false,-1);
        tracep->declBit(c+56,"top i_cii_top hsync", false,-1);
        tracep->declBit(c+57,"top i_cii_top vsync", false,-1);
        tracep->declBit(c+58,"top i_cii_top valid", false,-1);
        tracep->declBus(c+59,"top i_cii_top vga_r", false,-1, 7,0);
        tracep->declBus(c+60,"top i_cii_top vga_g", false,-1, 7,0);
        tracep->declBus(c+61,"top i_cii_top vga_b", false,-1, 7,0);
        tracep->declBus(c+48,"top i_cii_top h_addr", false,-1, 9,0);
        tracep->declBus(c+1,"top i_cii_top v_addr", false,-1, 9,0);
        tracep->declBus(c+2,"top i_cii_top mem_vga_wire", false,-1, 23,0);
        tracep->declBus(c+3,"top i_cii_top char_x", false,-1, 6,0);
        tracep->declBus(c+4,"top i_cii_top char_y", false,-1, 4,0);
        tracep->declBus(c+5,"top i_cii_top pixel_x", false,-1, 3,0);
        tracep->declBus(c+6,"top i_cii_top pixel_y", false,-1, 3,0);
        tracep->declBus(c+7,"top i_cii_top char_ascii", false,-1, 7,0);
        tracep->declBus(c+8,"top i_cii_top base", false,-1, 11,0);
        tracep->declBus(c+5,"top i_cii_top offset", false,-1, 3,0);
        tracep->declBit(c+9,"top i_cii_top pixel", false,-1);
        tracep->declBus(c+2,"top i_cii_top mem_ascii_signal", false,-1, 23,0);
        tracep->declBus(c+10,"top i_cii_top ps2kbd_data", false,-1, 7,0);
        tracep->declBus(c+11,"top i_cii_top ps2ctrl_ascii", false,-1, 7,0);
        tracep->declBit(c+12,"top i_cii_top ps2kbd_ready", false,-1);
        tracep->declBit(c+13,"top i_cii_top ps2ctrl_nextdata_n", false,-1);
        tracep->declBit(c+14,"top i_cii_top ps2kbd_overflow", false,-1);
        tracep->declBit(c+15,"top i_cii_top ps2ctrl_vld", false,-1);
        tracep->declBit(c+51,"top i_cii_top i_vga_ctrl pclk", false,-1);
        tracep->declBit(c+52,"top i_cii_top i_vga_ctrl reset", false,-1);
        tracep->declBus(c+2,"top i_cii_top i_vga_ctrl vga_data", false,-1, 23,0);
        tracep->declBus(c+48,"top i_cii_top i_vga_ctrl h_addr", false,-1, 9,0);
        tracep->declBus(c+1,"top i_cii_top i_vga_ctrl v_addr", false,-1, 9,0);
        tracep->declBit(c+56,"top i_cii_top i_vga_ctrl hsync", false,-1);
        tracep->declBit(c+57,"top i_cii_top i_vga_ctrl vsync", false,-1);
        tracep->declBit(c+58,"top i_cii_top i_vga_ctrl valid", false,-1);
        tracep->declBus(c+59,"top i_cii_top i_vga_ctrl vga_r", false,-1, 7,0);
        tracep->declBus(c+60,"top i_cii_top i_vga_ctrl vga_g", false,-1, 7,0);
        tracep->declBus(c+61,"top i_cii_top i_vga_ctrl vga_b", false,-1, 7,0);
        tracep->declBus(c+62,"top i_cii_top i_vga_ctrl h_frontporch", false,-1, 31,0);
        tracep->declBus(c+63,"top i_cii_top i_vga_ctrl h_active", false,-1, 31,0);
        tracep->declBus(c+64,"top i_cii_top i_vga_ctrl h_backporch", false,-1, 31,0);
        tracep->declBus(c+65,"top i_cii_top i_vga_ctrl h_total", false,-1, 31,0);
        tracep->declBus(c+66,"top i_cii_top i_vga_ctrl v_frontporch", false,-1, 31,0);
        tracep->declBus(c+67,"top i_cii_top i_vga_ctrl v_active", false,-1, 31,0);
        tracep->declBus(c+68,"top i_cii_top i_vga_ctrl v_backporch", false,-1, 31,0);
        tracep->declBus(c+69,"top i_cii_top i_vga_ctrl v_total", false,-1, 31,0);
        tracep->declBus(c+49,"top i_cii_top i_vga_ctrl x_cnt", false,-1, 9,0);
        tracep->declBus(c+16,"top i_cii_top i_vga_ctrl y_cnt", false,-1, 9,0);
        tracep->declBit(c+50,"top i_cii_top i_vga_ctrl h_valid", false,-1);
        tracep->declBit(c+17,"top i_cii_top i_vga_ctrl v_valid", false,-1);
        tracep->declBit(c+51,"top i_cii_top i_hvaddr_conv clk", false,-1);
        tracep->declBit(c+52,"top i_cii_top i_hvaddr_conv rst", false,-1);
        tracep->declBus(c+48,"top i_cii_top i_hvaddr_conv h_addr", false,-1, 9,0);
        tracep->declBus(c+1,"top i_cii_top i_hvaddr_conv v_addr", false,-1, 9,0);
        tracep->declBus(c+3,"top i_cii_top i_hvaddr_conv char_x", false,-1, 6,0);
        tracep->declBus(c+4,"top i_cii_top i_hvaddr_conv char_y", false,-1, 4,0);
        tracep->declBus(c+5,"top i_cii_top i_hvaddr_conv pixel_x", false,-1, 3,0);
        tracep->declBus(c+6,"top i_cii_top i_hvaddr_conv pixel_y", false,-1, 3,0);
        tracep->declBus(c+18,"top i_cii_top i_hvaddr_conv hcounter", false,-1, 15,0);
        tracep->declBus(c+19,"top i_cii_top i_hvaddr_conv vcounter", false,-1, 15,0);
        tracep->declBus(c+20,"top i_cii_top i_hvaddr_conv cxcounter", false,-1, 4,0);
        tracep->declBus(c+21,"top i_cii_top i_hvaddr_conv cycounter", false,-1, 4,0);
        tracep->declBus(c+22,"top i_cii_top i_hvaddr_conv h_addr_old", false,-1, 9,0);
        tracep->declBus(c+23,"top i_cii_top i_hvaddr_conv v_addr_old", false,-1, 9,0);
        tracep->declBit(c+51,"top i_cii_top i_cii_tab_ctrl clk", false,-1);
        tracep->declBit(c+53,"top i_cii_top i_cii_tab_ctrl rstn", false,-1);
        tracep->declBus(c+3,"top i_cii_top i_cii_tab_ctrl char_x_rd", false,-1, 6,0);
        tracep->declBus(c+4,"top i_cii_top i_cii_tab_ctrl char_y_rd", false,-1, 4,0);
        tracep->declBit(c+70,"top i_cii_top i_cii_tab_ctrl rd_vld", false,-1);
        tracep->declBit(c+15,"top i_cii_top i_cii_tab_ctrl we_vld", false,-1);
        tracep->declBus(c+11,"top i_cii_top i_cii_tab_ctrl ascii_i", false,-1, 7,0);
        tracep->declBus(c+7,"top i_cii_top i_cii_tab_ctrl ascii_o", false,-1, 7,0);
        tracep->declBus(c+24,"top i_cii_top i_cii_tab_ctrl ram_we_ascii", false,-1, 7,0);
        tracep->declBit(c+25,"top i_cii_top i_cii_tab_ctrl ram_we_vld", false,-1);
        tracep->declBit(c+26,"top i_cii_top i_cii_tab_ctrl ram_we_rdy", false,-1);
        tracep->declBus(c+27,"top i_cii_top i_cii_tab_ctrl char_x_we", false,-1, 6,0);
        tracep->declBus(c+28,"top i_cii_top i_cii_tab_ctrl char_y_we", false,-1, 4,0);
        tracep->declBit(c+51,"top i_cii_top i_cii_tab_ctrl cii_t_ram clk", false,-1);
        tracep->declBus(c+3,"top i_cii_top i_cii_tab_ctrl cii_t_ram char_x_rd", false,-1, 6,0);
        tracep->declBus(c+4,"top i_cii_top i_cii_tab_ctrl cii_t_ram char_y_rd", false,-1, 4,0);
        tracep->declBus(c+27,"top i_cii_top i_cii_tab_ctrl cii_t_ram char_x_we", false,-1, 6,0);
        tracep->declBus(c+28,"top i_cii_top i_cii_tab_ctrl cii_t_ram char_y_we", false,-1, 4,0);
        tracep->declBit(c+70,"top i_cii_top i_cii_tab_ctrl cii_t_ram rd", false,-1);
        tracep->declBit(c+25,"top i_cii_top i_cii_tab_ctrl cii_t_ram we_vld", false,-1);
        tracep->declBit(c+26,"top i_cii_top i_cii_tab_ctrl cii_t_ram we_rdy", false,-1);
        tracep->declBus(c+24,"top i_cii_top i_cii_tab_ctrl cii_t_ram ascii_we", false,-1, 7,0);
        tracep->declBus(c+7,"top i_cii_top i_cii_tab_ctrl cii_t_ram ascii_rd", false,-1, 7,0);
        tracep->declBus(c+29,"top i_cii_top i_cii_tab_ctrl cii_t_ram uchar_x_we", false,-1, 6,0);
        tracep->declBus(c+30,"top i_cii_top i_cii_tab_ctrl cii_t_ram uchar_y_we", false,-1, 4,0);
        tracep->declBus(c+7,"top i_cii_top i_cii_pix_conv ascii", false,-1, 7,0);
        tracep->declBus(c+5,"top i_cii_top i_cii_pix_conv pixel_x", false,-1, 3,0);
        tracep->declBus(c+6,"top i_cii_top i_cii_pix_conv pixel_y", false,-1, 3,0);
        tracep->declBus(c+8,"top i_cii_top i_cii_pix_conv base", false,-1, 11,0);
        tracep->declBus(c+5,"top i_cii_top i_cii_pix_conv offset", false,-1, 3,0);
        tracep->declBit(c+51,"top i_cii_top i_kbd_control clk", false,-1);
        tracep->declBit(c+53,"top i_cii_top i_kbd_control clrn", false,-1);
        tracep->declBit(c+54,"top i_cii_top i_kbd_control ps2_clk", false,-1);
        tracep->declBit(c+55,"top i_cii_top i_kbd_control ps2_data", false,-1);
        tracep->declBit(c+13,"top i_cii_top i_kbd_control nextdata_n", false,-1);
        tracep->declBus(c+10,"top i_cii_top i_kbd_control data", false,-1, 7,0);
        tracep->declBit(c+12,"top i_cii_top i_kbd_control ready", false,-1);
        tracep->declBit(c+14,"top i_cii_top i_kbd_control overflow", false,-1);
        tracep->declBus(c+31,"top i_cii_top i_kbd_control buffer", false,-1, 9,0);
        {int i; for (i=0; i<8; i++) {
                tracep->declBus(c+32+i*1,"top i_cii_top i_kbd_control fifo", true,(i+0), 7,0);}}
        tracep->declBus(c+40,"top i_cii_top i_kbd_control w_ptr", false,-1, 2,0);
        tracep->declBus(c+41,"top i_cii_top i_kbd_control r_ptr", false,-1, 2,0);
        tracep->declBus(c+42,"top i_cii_top i_kbd_control count", false,-1, 3,0);
        tracep->declBus(c+43,"top i_cii_top i_kbd_control ps2_clk_sync", false,-1, 2,0);
        tracep->declBit(c+44,"top i_cii_top i_kbd_control sampling", false,-1);
        tracep->declBit(c+51,"top i_cii_top i_ps2kbd_transfer clk", false,-1);
        tracep->declBit(c+53,"top i_cii_top i_ps2kbd_transfer clrn", false,-1);
        tracep->declBus(c+10,"top i_cii_top i_ps2kbd_transfer data", false,-1, 7,0);
        tracep->declBit(c+12,"top i_cii_top i_ps2kbd_transfer ready", false,-1);
        tracep->declBus(c+11,"top i_cii_top i_ps2kbd_transfer ascii", false,-1, 7,0);
        tracep->declBit(c+15,"top i_cii_top i_ps2kbd_transfer valid", false,-1);
        tracep->declBit(c+13,"top i_cii_top i_ps2kbd_transfer nextdata_n", false,-1);
        tracep->declBus(c+45,"top i_cii_top i_ps2kbd_transfer data_cache1", false,-1, 7,0);
        tracep->declBus(c+71,"top i_cii_top i_ps2kbd_transfer IDLE", false,-1, 31,0);
        tracep->declBus(c+72,"top i_cii_top i_ps2kbd_transfer OUT", false,-1, 31,0);
        tracep->declBus(c+46,"top i_cii_top i_ps2kbd_transfer state", false,-1, 3,0);
        tracep->declBus(c+47,"top i_cii_top i_ps2kbd_transfer data_cache2", false,-1, 7,0);
        tracep->declBus(c+47,"top i_cii_top i_ps2kbd_transfer i_rpa addr", false,-1, 7,0);
        tracep->declBus(c+11,"top i_cii_top i_ps2kbd_transfer i_rpa data", false,-1, 7,0);
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
        tracep->fullSData(oldp+1,(vlSelf->top__DOT__i_cii_top__DOT__v_addr),10);
        tracep->fullIData(oldp+2,(((IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel)
                                    ? 0xffffffU : 0U)),24);
        tracep->fullCData(oldp+3,(vlSelf->top__DOT__i_cii_top__DOT__char_x),7);
        tracep->fullCData(oldp+4,(vlSelf->top__DOT__i_cii_top__DOT__char_y),5);
        tracep->fullCData(oldp+5,(vlSelf->top__DOT__i_cii_top__DOT__pixel_x),4);
        tracep->fullCData(oldp+6,(vlSelf->top__DOT__i_cii_top__DOT__pixel_y),4);
        tracep->fullCData(oldp+7,(((0x1dU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))
                                    ? vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem
                                   [((0x45U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))
                                      ? (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)
                                      : 0U)][vlSelf->top__DOT__i_cii_top__DOT__char_y]
                                    : 0U)),8);
        tracep->fullSData(oldp+8,(((((0x1dU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))
                                      ? vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem
                                     [((0x45U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))
                                        ? (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)
                                        : 0U)][vlSelf->top__DOT__i_cii_top__DOT__char_y]
                                      : 0U) << 4U) 
                                   | (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))),12);
        tracep->fullBit(oldp+9,(vlSelf->top__DOT__i_cii_top__DOT__pixel));
        tracep->fullCData(oldp+10,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo
                                   [vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr]),8);
        tracep->fullCData(oldp+11,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii),8);
        tracep->fullBit(oldp+12,(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready));
        tracep->fullBit(oldp+13,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n));
        tracep->fullBit(oldp+14,(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow));
        tracep->fullBit(oldp+15,(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld));
        tracep->fullSData(oldp+16,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt),10);
        tracep->fullBit(oldp+17,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid));
        tracep->fullSData(oldp+18,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter),16);
        tracep->fullSData(oldp+19,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter),16);
        tracep->fullCData(oldp+20,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter),5);
        tracep->fullCData(oldp+21,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter),5);
        tracep->fullSData(oldp+22,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old),10);
        tracep->fullSData(oldp+23,(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old),10);
        tracep->fullCData(oldp+24,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii),8);
        tracep->fullBit(oldp+25,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld));
        tracep->fullBit(oldp+26,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_rdy));
        tracep->fullCData(oldp+27,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we),7);
        tracep->fullCData(oldp+28,(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we),5);
        tracep->fullCData(oldp+29,(((0x7fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))
                                     ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))),7);
        tracep->fullCData(oldp+30,(((0x1fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))
                                     ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))),5);
        tracep->fullSData(oldp+31,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer),10);
        tracep->fullCData(oldp+32,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[0]),8);
        tracep->fullCData(oldp+33,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[1]),8);
        tracep->fullCData(oldp+34,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[2]),8);
        tracep->fullCData(oldp+35,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[3]),8);
        tracep->fullCData(oldp+36,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[4]),8);
        tracep->fullCData(oldp+37,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[5]),8);
        tracep->fullCData(oldp+38,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[6]),8);
        tracep->fullCData(oldp+39,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[7]),8);
        tracep->fullCData(oldp+40,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr),3);
        tracep->fullCData(oldp+41,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr),3);
        tracep->fullCData(oldp+42,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count),4);
        tracep->fullCData(oldp+43,(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync),3);
        tracep->fullBit(oldp+44,((IData)((4U == (6U 
                                                 & (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync))))));
        tracep->fullCData(oldp+45,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1),8);
        tracep->fullCData(oldp+46,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state),4);
        tracep->fullCData(oldp+47,(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2),8);
        tracep->fullSData(oldp+48,(vlSelf->top__DOT__i_cii_top__DOT__h_addr),10);
        tracep->fullSData(oldp+49,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt),10);
        tracep->fullBit(oldp+50,(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid));
        tracep->fullBit(oldp+51,(vlSelf->clk));
        tracep->fullBit(oldp+52,(vlSelf->rst));
        tracep->fullBit(oldp+53,(vlSelf->rstn));
        tracep->fullBit(oldp+54,(vlSelf->ps2_clk));
        tracep->fullBit(oldp+55,(vlSelf->ps2_data));
        tracep->fullBit(oldp+56,(vlSelf->hsync));
        tracep->fullBit(oldp+57,(vlSelf->vsync));
        tracep->fullBit(oldp+58,(vlSelf->valid));
        tracep->fullCData(oldp+59,(vlSelf->vga_r),8);
        tracep->fullCData(oldp+60,(vlSelf->vga_g),8);
        tracep->fullCData(oldp+61,(vlSelf->vga_b),8);
        tracep->fullIData(oldp+62,(0x60U),32);
        tracep->fullIData(oldp+63,(0x90U),32);
        tracep->fullIData(oldp+64,(0x310U),32);
        tracep->fullIData(oldp+65,(0x320U),32);
        tracep->fullIData(oldp+66,(2U),32);
        tracep->fullIData(oldp+67,(0x23U),32);
        tracep->fullIData(oldp+68,(0x203U),32);
        tracep->fullIData(oldp+69,(0x20dU),32);
        tracep->fullBit(oldp+70,(0U));
        tracep->fullIData(oldp+71,(0U),32);
        tracep->fullIData(oldp+72,(1U),32);
    }
}
