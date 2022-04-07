// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Symbol table internal header
//
// Internal details; most calling programs do not need this header,
// unless using verilator public meta comments.

#ifndef VERILATED_VXOR_GATE__SYMS_H_
#define VERILATED_VXOR_GATE__SYMS_H_  // guard

#include "verilated_heavy.h"

// INCLUDE MODEL CLASS

#include "Vxor_gate.h"

// INCLUDE MODULE CLASSES
#include "Vxor_gate___024root.h"

// SYMS CLASS (contains all model state)
class Vxor_gate__Syms final : public VerilatedSyms {
  public:
    // INTERNAL STATE
    Vxor_gate* const __Vm_modelp;
    bool __Vm_didInit = false;

    // MODULE INSTANCE STATE
    Vxor_gate___024root            TOP;

    // CONSTRUCTORS
    Vxor_gate__Syms(VerilatedContext* contextp, const char* namep, Vxor_gate* modelp);
    ~Vxor_gate__Syms();

    // METHODS
    const char* name() { return TOP.name(); }
} VL_ATTR_ALIGNED(VL_CACHE_LINE_BYTES);

#endif  // guard
