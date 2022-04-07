// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design internal header
// See Vour.h for the primary calling header

#ifndef VERILATED_VOUR___024ROOT_H_
#define VERILATED_VOUR___024ROOT_H_  // guard

#include "verilated_heavy.h"

//==========

class Vour__Syms;

//----------

VL_MODULE(Vour___024root) {
  public:

    // INTERNAL VARIABLES
    Vour__Syms* vlSymsp;  // Symbol table

    // CONSTRUCTORS
  private:
    VL_UNCOPYABLE(Vour___024root);  ///< Copying not allowed
  public:
    Vour___024root(const char* name);
    ~Vour___024root();

    // INTERNAL METHODS
    void __Vconfigure(Vour__Syms* symsp, bool first);
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

//----------


#endif  // guard
