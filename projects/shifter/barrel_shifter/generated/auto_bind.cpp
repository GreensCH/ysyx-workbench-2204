#include <nvboard.h>
#include "Vtop.h"

void nvboard_bind_all_pins(Vtop* top) {
	nvboard_bind_pin( &top->Din, BIND_RATE_SCR, BIND_DIR_IN , 8, SW7, SW6, SW5, SW4, SW3, SW2, SW1, SW0);
	nvboard_bind_pin( &top->Shamt, BIND_RATE_SCR, BIND_DIR_IN , 4, SW11, SW10, SW9, SW8);
	nvboard_bind_pin( &top->A_L, BIND_RATE_SCR, BIND_DIR_IN , 1, SW15);
	nvboard_bind_pin( &top->L_R, BIND_RATE_SCR, BIND_DIR_IN , 1, SW14);
	nvboard_bind_pin( &top->led_o, BIND_RATE_SCR, BIND_DIR_OUT, 16, LD15, LD14, LD13, LD12, LD11, LD10, LD9, LD8, LD7, LD6, LD5, LD4, LD3, LD2, LD1, LD0);
	nvboard_bind_pin( &top->hex_o, BIND_RATE_SCR, BIND_DIR_OUT, 16, SEG1A, SEG1B, SEG1C, SEG1D, SEG1E, SEG1F, SEG1G, DEC1P, SEG0A, SEG0B, SEG0C, SEG0D, SEG0E, SEG0F, SEG0G, DEC0P);
}
