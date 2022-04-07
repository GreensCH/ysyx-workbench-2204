// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Model implementation (design independent parts)

#include "Vxor_gate.h"
#include "Vxor_gate__Syms.h"

//============================================================
// Constructors

Vxor_gate::Vxor_gate(VerilatedContext* _vcontextp__, const char* _vcname__)
    : vlSymsp{new Vxor_gate__Syms(_vcontextp__, _vcname__, this)}
    , a{vlSymsp->TOP.a}
    , b{vlSymsp->TOP.b}
    , f{vlSymsp->TOP.f}
    , rootp{&(vlSymsp->TOP)}
{
}

Vxor_gate::Vxor_gate(const char* _vcname__)
    : Vxor_gate(nullptr, _vcname__)
{
}

//============================================================
// Destructor

Vxor_gate::~Vxor_gate() {
    delete vlSymsp;
}

//============================================================
// Evaluation loop

void Vxor_gate___024root___eval_initial(Vxor_gate___024root* vlSelf);
void Vxor_gate___024root___eval_settle(Vxor_gate___024root* vlSelf);
void Vxor_gate___024root___eval(Vxor_gate___024root* vlSelf);
QData Vxor_gate___024root___change_request(Vxor_gate___024root* vlSelf);
#ifdef VL_DEBUG
void Vxor_gate___024root___eval_debug_assertions(Vxor_gate___024root* vlSelf);
#endif  // VL_DEBUG
void Vxor_gate___024root___final(Vxor_gate___024root* vlSelf);

static void _eval_initial_loop(Vxor_gate__Syms* __restrict vlSymsp) {
    vlSymsp->__Vm_didInit = true;
    Vxor_gate___024root___eval_initial(&(vlSymsp->TOP));
    // Evaluate till stable
    int __VclockLoop = 0;
    QData __Vchange = 1;
    do {
        VL_DEBUG_IF(VL_DBG_MSGF("+ Initial loop\n"););
        Vxor_gate___024root___eval_settle(&(vlSymsp->TOP));
        Vxor_gate___024root___eval(&(vlSymsp->TOP));
        if (VL_UNLIKELY(++__VclockLoop > 100)) {
            // About to fail, so enable debug to see what's not settling.
            // Note you must run make with OPT=-DVL_DEBUG for debug prints.
            int __Vsaved_debug = Verilated::debug();
            Verilated::debug(1);
            __Vchange = Vxor_gate___024root___change_request(&(vlSymsp->TOP));
            Verilated::debug(__Vsaved_debug);
            VL_FATAL_MT("xor_gate.v", 2, "",
                "Verilated model didn't DC converge\n"
                "- See https://verilator.org/warn/DIDNOTCONVERGE");
        } else {
            __Vchange = Vxor_gate___024root___change_request(&(vlSymsp->TOP));
        }
    } while (VL_UNLIKELY(__Vchange));
}

void Vxor_gate::eval_step() {
    VL_DEBUG_IF(VL_DBG_MSGF("+++++TOP Evaluate Vxor_gate::eval_step\n"); );
#ifdef VL_DEBUG
    // Debug assertions
    Vxor_gate___024root___eval_debug_assertions(&(vlSymsp->TOP));
#endif  // VL_DEBUG
    // Initialize
    if (VL_UNLIKELY(!vlSymsp->__Vm_didInit)) _eval_initial_loop(vlSymsp);
    // Evaluate till stable
    int __VclockLoop = 0;
    QData __Vchange = 1;
    do {
        VL_DEBUG_IF(VL_DBG_MSGF("+ Clock loop\n"););
        Vxor_gate___024root___eval(&(vlSymsp->TOP));
        if (VL_UNLIKELY(++__VclockLoop > 100)) {
            // About to fail, so enable debug to see what's not settling.
            // Note you must run make with OPT=-DVL_DEBUG for debug prints.
            int __Vsaved_debug = Verilated::debug();
            Verilated::debug(1);
            __Vchange = Vxor_gate___024root___change_request(&(vlSymsp->TOP));
            Verilated::debug(__Vsaved_debug);
            VL_FATAL_MT("xor_gate.v", 2, "",
                "Verilated model didn't converge\n"
                "- See https://verilator.org/warn/DIDNOTCONVERGE");
        } else {
            __Vchange = Vxor_gate___024root___change_request(&(vlSymsp->TOP));
        }
    } while (VL_UNLIKELY(__Vchange));
}

//============================================================
// Invoke final blocks

void Vxor_gate::final() {
    Vxor_gate___024root___final(&(vlSymsp->TOP));
}

//============================================================
// Utilities

VerilatedContext* Vxor_gate::contextp() const {
    return vlSymsp->_vm_contextp__;
}

const char* Vxor_gate::name() const {
    return vlSymsp->name();
}
