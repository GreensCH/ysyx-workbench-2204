// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vxor_gate.h for the primary calling header

#ifndef VERILATED_VXOR_GATE___024ROOT_H_
#define VERILATED_VXOR_GATE___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class Vxor_gate__Syms;

//----------

VL_MODULE(Vxor_gate___024root) {
  public:

    // PORTS
    VL_IN8(a,0,0);
    VL_IN8(b,0,0);
    VL_OUT8(f,0,0);

    // INTERNAL VARIABLES
    Vxor_gate__Syms* vlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(Vxor_gate___024root);  ///< Copying not allowed
  public:
    Vxor_gate___024root(const char* name);
    ~Vxor_gate___024root();

    // INTERNAL METHODS
    void __Vconfigure(Vxor_gate__Syms* symsp, bool first);
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
