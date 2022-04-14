// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See Vtop.h for the primary calling header

#include "Vtop___024root.h"
#include "Vtop__Syms.h"

//==========

extern const VlUnpacked<CData/*7:0*/, 256> Vtop__ConstPool__TABLE_e8611664_0;

VL_INLINE_OPT void Vtop___024root___sequent__TOP__2(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__2\n"); );
    // Variables
    CData/*7:0*/ __Vtableidx1;
    CData/*3:0*/ __Vdly__top__DOT__i_cii_top__DOT__pixel_x;
    CData/*6:0*/ __Vdly__top__DOT__i_cii_top__DOT__char_x;
    CData/*4:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter;
    CData/*3:0*/ __Vdly__top__DOT__i_cii_top__DOT__pixel_y;
    CData/*4:0*/ __Vdly__top__DOT__i_cii_top__DOT__char_y;
    CData/*4:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter;
    CData/*2:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    CData/*2:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr;
    CData/*0:0*/ __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready;
    CData/*2:0*/ __Vdlyvdim0__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*7:0*/ __Vdlyvval__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*0:0*/ __Vdlyvset__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0;
    CData/*2:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr;
    CData/*3:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count;
    CData/*3:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state;
    SData/*9:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt;
    SData/*15:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter;
    SData/*15:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter;
    SData/*9:0*/ __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old;
    // Body
    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count 
        = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count;
    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr 
        = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr;
    __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready 
        = vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready;
    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr 
        = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr;
    __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state 
        = vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state;
    __Vdlyvset__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0 = 0U;
    __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter 
        = vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter;
    __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter 
        = vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter;
    __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter 
        = vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter;
    __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old 
        = vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old;
    __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter 
        = vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter;
    __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt 
        = vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt;
    __Vdly__top__DOT__i_cii_top__DOT__char_x = vlSelf->top__DOT__i_cii_top__DOT__char_x;
    __Vdly__top__DOT__i_cii_top__DOT__pixel_x = vlSelf->top__DOT__i_cii_top__DOT__pixel_x;
    __Vdly__top__DOT__i_cii_top__DOT__char_y = vlSelf->top__DOT__i_cii_top__DOT__char_y;
    __Vdly__top__DOT__i_cii_top__DOT__pixel_y = vlSelf->top__DOT__i_cii_top__DOT__pixel_y;
    if (VL_UNLIKELY(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld)) {
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT____Vlvbound1 
            = vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii;
        VL_WRITEF("ram@assci:%x,incx:%x,incx:%x\n",
                  8,vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii,
                  7,((0x7fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))
                      ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we)),
                  5,((0x1fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))
                      ? 0U : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we)));
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_rdy = 0U;
        if ((0x1dU >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))) {
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem[0U][vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we] 
                = vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT____Vlvbound1;
        }
    } else {
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_rdy = 1U;
    }
    if (vlSelf->rstn) {
        if (VL_UNLIKELY(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld)) {
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we 
                = ((0x45U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))
                    ? 0U : (0x7fU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))));
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii 
                = vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii;
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we 
                = (0x1fU & ((0x45U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we))
                             ? ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we))
                             : (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we)));
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld = 1U;
            VL_WRITEF("ctrl@assci:%x,incx:%3#,incx:%2#\n",
                      8,vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii,
                      7,(IData)(vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we),
                      5,vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we);
        } else {
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld = 0U;
            vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii = 0U;
        }
    } else {
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_x_we = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__char_y_we = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_vld = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__ram_we_ascii = 0U;
    }
    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = ((6U & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync) 
                  << 1U)) | (IData)(vlSelf->ps2_clk));
    if (VL_UNLIKELY(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow)) {
        VL_WRITEF("overflow...\n");
    }
    if (vlSelf->rstn) {
        if (vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready) {
            if ((1U & (~ (IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n)))) {
                __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr 
                    = (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr)));
                if (((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr) 
                     == (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr))))) {
                    __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready = 0U;
                }
            }
        }
        if ((IData)((4U == (6U & (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync))))) {
            if ((0xaU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count))) {
                if ((((~ (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer)) 
                      & (IData)(vlSelf->ps2_data)) 
                     & VL_REDXOR_32((0x1ffU & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer) 
                                               >> 1U))))) {
                    __Vdlyvval__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0 
                        = (0xffU & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer) 
                                    >> 1U));
                    __Vdlyvset__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0 = 1U;
                    __Vdlyvdim0__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0 
                        = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr;
                    __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready = 1U;
                    __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr 
                        = (7U & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr)));
                    vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow 
                        = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow) 
                           | ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr) 
                              == (7U & ((IData)(1U) 
                                        + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr)))));
                }
                __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count = 0U;
            } else {
                vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT____Vlvbound1 
                    = vlSelf->ps2_data;
                if ((9U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count))) {
                    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer 
                        = (((~ ((IData)(1U) << (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count))) 
                            & (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__buffer)) 
                           | (0x3ffU & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT____Vlvbound1) 
                                        << (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count))));
                }
                __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count 
                    = (0xfU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count)));
            }
        }
    } else {
        __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_overflow = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready = 0U;
    }
    if (vlSelf->rst) {
        __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt = 1U;
    } else if (((0x20dU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)) 
                & (0x320U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)))) {
        __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt = 1U;
    } else if ((0x320U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt))) {
        __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt 
            = (0x3ffU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)));
    }
    if ((0U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__h_addr))) {
        vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old 
            = vlSelf->top__DOT__i_cii_top__DOT__h_addr;
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__pixel_x = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__char_x = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter = 0U;
    } else if (((IData)(vlSelf->top__DOT__i_cii_top__DOT__h_addr) 
                != (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old))) {
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter 
            = (0xffffU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter)));
        vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__h_addr_old 
            = vlSelf->top__DOT__i_cii_top__DOT__h_addr;
        if (VL_UNLIKELY((0x27fU < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter)))) {
            VL_WRITEF("none\n");
            __Vdly__top__DOT__i_cii_top__DOT__pixel_x = 0U;
            __Vdly__top__DOT__i_cii_top__DOT__char_x = 0U;
            __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter = 0U;
        } else {
            __Vdly__top__DOT__i_cii_top__DOT__pixel_x 
                = ((8U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x))
                    ? 0U : (0xfU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x))));
            if ((0x47U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))) {
                __Vdly__top__DOT__i_cii_top__DOT__char_x = 0U;
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter = 0U;
            } else if ((8U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter))) {
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter = 0U;
                __Vdly__top__DOT__i_cii_top__DOT__char_x 
                    = (0x7fU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)));
            } else {
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter 
                    = (0x1fU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter)));
                __Vdly__top__DOT__i_cii_top__DOT__char_x 
                    = (0x7fU & (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x));
            }
        }
    }
    if ((0U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__v_addr))) {
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__pixel_y = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old 
            = vlSelf->top__DOT__i_cii_top__DOT__v_addr;
        __Vdly__top__DOT__i_cii_top__DOT__char_y = 0U;
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter = 0U;
    } else if (((IData)(vlSelf->top__DOT__i_cii_top__DOT__v_addr) 
                != (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old))) {
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter 
            = (0xffffU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter)));
        if ((0x1dfU < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter))) {
            __Vdly__top__DOT__i_cii_top__DOT__char_y = 0U;
            __Vdly__top__DOT__i_cii_top__DOT__pixel_y = 0U;
            __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter = 0U;
        } else {
            __Vdly__top__DOT__i_cii_top__DOT__pixel_y 
                = ((0xfU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))
                    ? 0U : (0xfU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))));
            if ((0x1fU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))) {
                __Vdly__top__DOT__i_cii_top__DOT__char_y = 0U;
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter = 0U;
            } else if ((0xfU == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter))) {
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter = 0U;
                __Vdly__top__DOT__i_cii_top__DOT__char_y 
                    = (0x1fU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y)));
            } else {
                __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter 
                    = (0x1fU & ((IData)(1U) + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter)));
                __Vdly__top__DOT__i_cii_top__DOT__char_y 
                    = (0x1fU & (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y));
            }
        }
        __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old 
            = vlSelf->top__DOT__i_cii_top__DOT__v_addr;
    }
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync 
        = __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__ps2_clk_sync;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr 
        = __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__w_ptr;
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count 
        = __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__count;
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt 
        = __Vdly__top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter 
        = __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__hcounter;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter 
        = __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cxcounter;
    vlSelf->top__DOT__i_cii_top__DOT__pixel_x = __Vdly__top__DOT__i_cii_top__DOT__pixel_x;
    vlSelf->top__DOT__i_cii_top__DOT__char_x = __Vdly__top__DOT__i_cii_top__DOT__char_x;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter 
        = __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__vcounter;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old 
        = __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__v_addr_old;
    vlSelf->top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter 
        = __Vdly__top__DOT__i_cii_top__DOT__i_hvaddr_conv__DOT__cycounter;
    vlSelf->top__DOT__i_cii_top__DOT__pixel_y = __Vdly__top__DOT__i_cii_top__DOT__pixel_y;
    vlSelf->top__DOT__i_cii_top__DOT__char_y = __Vdly__top__DOT__i_cii_top__DOT__char_y;
    if (vlSelf->rstn) {
        if (((IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready) 
             & (~ (IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n)))) {
            if ((0U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state))) {
                if ((0xf0U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1))) {
                    VL_WRITEF("GOT OUT\n");
                } else {
                    VL_WRITEF("GET DATA\n");
                }
                if ((0xf0U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1))) {
                    __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 1U;
                    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0U;
                    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0U;
                } else {
                    __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 0U;
                    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 
                        = vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1;
                    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 1U;
                }
            } else if ((1U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state))) {
                VL_WRITEF("COME BACK TO WAIT\n");
                __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 0U;
                vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0U;
                vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0U;
            } else {
                VL_WRITEF("ERROR\n");
                __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 0U;
                vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0U;
                vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0U;
            }
        } else {
            vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0U;
            vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0U;
        }
    } else {
        __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2 = 0U;
        vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_vld = 0U;
    }
    vlSelf->vsync = (2U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt));
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid 
        = ((0x23U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)) 
           & (0x203U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt)));
    vlSelf->top__DOT__i_cii_top__DOT__pixel = ((0xbU 
                                                >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x)) 
                                               & (vlSelf->top__DOT__i_cii_top__DOT__mem_graph_ascii
                                                  [
                                                  ((((0x1dU 
                                                      >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_y))
                                                      ? 
                                                     vlSelf->top__DOT__i_cii_top__DOT__i_cii_tab_ctrl__DOT__cii_t_ram__DOT__mem
                                                     [
                                                     ((0x45U 
                                                       >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x))
                                                       ? (IData)(vlSelf->top__DOT__i_cii_top__DOT__char_x)
                                                       : 0U)]
                                                     [vlSelf->top__DOT__i_cii_top__DOT__char_y]
                                                      : 0U) 
                                                    << 4U) 
                                                   | (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_y))] 
                                                  >> (IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel_x)));
    vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state 
        = __Vdly__top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__state;
    vlSelf->top__DOT__i_cii_top__DOT__v_addr = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid)
                                                 ? 
                                                (0x3ffU 
                                                 & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__y_cnt) 
                                                    - (IData)(0x24U)))
                                                 : 0U);
    vlSelf->vga_r = (0xffU & (((IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel)
                                ? 0xffffffU : 0U) >> 0x10U));
    vlSelf->vga_g = (0xffU & (((IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel)
                                ? 0xffffffU : 0U) >> 8U));
    vlSelf->vga_b = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__pixel)
                      ? 0xffU : 0U);
    __Vtableidx1 = vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache2;
    vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_ascii 
        = Vtop__ConstPool__TABLE_e8611664_0[__Vtableidx1];
    if (vlSelf->rstn) {
        if (((IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready) 
             & (IData)(vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n))) {
            vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n = 0U;
            vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1 
                = vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo
                [vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr];
        } else {
            vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n = 1U;
            vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1 = 0U;
        }
    } else {
        vlSelf->top__DOT__i_cii_top__DOT__ps2ctrl_nextdata_n = 1U;
        vlSelf->top__DOT__i_cii_top__DOT__i_ps2kbd_transfer__DOT__data_cache1 = 0U;
    }
    vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr 
        = __Vdly__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__r_ptr;
    vlSelf->top__DOT__i_cii_top__DOT__ps2kbd_ready 
        = __Vdly__top__DOT__i_cii_top__DOT__ps2kbd_ready;
    if (__Vdlyvset__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0) {
        vlSelf->top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo[__Vdlyvdim0__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0] 
            = __Vdlyvval__top__DOT__i_cii_top__DOT__i_kbd_control__DOT__fifo__v0;
    }
}

VL_INLINE_OPT void Vtop___024root___sequent__TOP__3(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___sequent__TOP__3\n"); );
    // Body
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt 
        = ((IData)(vlSelf->rst) ? 1U : ((0x320U == (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt))
                                         ? 1U : (0x3ffU 
                                                 & ((IData)(1U) 
                                                    + (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)))));
    vlSelf->hsync = (0x60U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt));
    vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid 
        = ((0x90U < (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)) 
           & (0x310U >= (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt)));
    vlSelf->valid = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid) 
                     & (IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__v_valid));
    vlSelf->top__DOT__i_cii_top__DOT__h_addr = ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__h_valid)
                                                 ? 
                                                (0x3ffU 
                                                 & ((IData)(vlSelf->top__DOT__i_cii_top__DOT__i_vga_ctrl__DOT__x_cnt) 
                                                    - (IData)(0x91U)))
                                                 : 0U);
}

void Vtop___024root___eval(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval\n"); );
    // Body
    if (((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk)))) {
        Vtop___024root___sequent__TOP__2(vlSelf);
        vlSelf->__Vm_traceActivity[1U] = 1U;
    }
    if ((((IData)(vlSelf->clk) & (~ (IData)(vlSelf->__Vclklast__TOP__clk))) 
         | ((IData)(vlSelf->rst) & (~ (IData)(vlSelf->__Vclklast__TOP__rst))))) {
        Vtop___024root___sequent__TOP__3(vlSelf);
        vlSelf->__Vm_traceActivity[2U] = 1U;
    }
    // Final
    vlSelf->__Vclklast__TOP__clk = vlSelf->clk;
    vlSelf->__Vclklast__TOP__rst = vlSelf->rst;
}

QData Vtop___024root___change_request_1(Vtop___024root* vlSelf);

VL_INLINE_OPT QData Vtop___024root___change_request(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___change_request\n"); );
    // Body
    return (Vtop___024root___change_request_1(vlSelf));
}

VL_INLINE_OPT QData Vtop___024root___change_request_1(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___change_request_1\n"); );
    // Body
    // Change detection
    QData __req = false;  // Logically a bool
    return __req;
}

#ifdef VL_DEBUG
void Vtop___024root___eval_debug_assertions(Vtop___024root* vlSelf) {
    if (false && vlSelf) {}  // Prevent unused
    Vtop__Syms* const __restrict vlSymsp VL_ATTR_UNUSED = vlSelf->vlSymsp;
    VL_DEBUG_IF(VL_DBG_MSGF("+    Vtop___024root___eval_debug_assertions\n"); );
    // Body
    if (VL_UNLIKELY((vlSelf->clk & 0xfeU))) {
        Verilated::overWidthError("clk");}
    if (VL_UNLIKELY((vlSelf->rst & 0xfeU))) {
        Verilated::overWidthError("rst");}
    if (VL_UNLIKELY((vlSelf->rstn & 0xfeU))) {
        Verilated::overWidthError("rstn");}
    if (VL_UNLIKELY((vlSelf->ps2_clk & 0xfeU))) {
        Verilated::overWidthError("ps2_clk");}
    if (VL_UNLIKELY((vlSelf->ps2_data & 0xfeU))) {
        Verilated::overWidthError("ps2_data");}
}
#endif  // VL_DEBUG
