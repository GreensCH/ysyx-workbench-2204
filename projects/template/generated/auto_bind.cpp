#include <nvboard.h>
#include "Vtop.h"

void nvboard_bind_all_pins(Vtop* top) {
	nvboard_bind_pin( &top->f, BIND_RATE_SCR, BIND_DIR_OUT, 1, LD0);
}
