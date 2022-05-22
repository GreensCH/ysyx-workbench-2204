// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See VTop.h for the primary calling header

#ifndef VERILATED_VTOP___024ROOT_H_
#define VERILATED_VTOP___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class VTop__Syms;

//----------

VL_MODULE(VTop___024root) {
  public:

    // INTERNAL VARIABLES
    VTop__Syms* vlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(VTop___024root);  ///< Copying not allowed
  public:
    VTop___024root(const char* name);
    ~VTop___024root();

    // INTERNAL METHODS
    void __Vconfigure(VTop__Syms* symsp, bool first);
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
