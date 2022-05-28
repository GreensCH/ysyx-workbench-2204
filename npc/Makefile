TOPNAME = Top
BUILD_DIR = $(NPC_HOME)/build
EMUSRC_DIR = $(NPC_HOME)/emulator
VERILOG_DIR = $(BUILD_DIR)/verilog
OBJ_DIR = $(BUILD_DIR)/obj_dir
BIN = $(BUILD_DIR)/$(TOPNAME)

VERILATOR = verilator
VERILATOR_CFLAGS += -MMD --build -cc  \
				-O3 --x-assign fast --x-initial fast --noassert
# project source
VSRCS  = $(shell find $(abspath $(VERILOG_DIR)) -name "*.v")
CSRCS  = $(shell find $(abspath $(EMUSRC_DIR)) -name "*.c" -or -name "*.cc" -or -name "*.cpp")

test:
	mill -i __.test

verilog:
	$(call git_commit, "generate verilog")
	mkdir -p $(VERILOG_DIR)
	mill -i __.test.runMain Elaborate -td $(VERILOG_DIR)

help:
	mill -i __.test.runMain Elaborate --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	-rm -rf $(BUILD_DIR)

sim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	@echo "Write this Makefile by yourself."

# rules for verilator
# INC_PATH ?= $(OBJ_DIR)/V$(TOPNAME).h
# INC_PATH += $(shell find $(abspath $(EMUSRC_DIR)) -name "*.h")
INC_PATH ?= $(EMUSRC_DIR)/include
INC_PATH += $(NEMU_HOME)/include
INC_PATH += $(NEMU_HOME)/include/generated
INCFLAGS = $(addprefix -I, $(INC_PATH))
CFLAGS += $(NEMUFLAGS) $(INCFLAGS) -DTOP_NAME="\"V$(TOPNAME)\""

# @rm -rf $(OBJ_DIR)
$(BIN): verilog $(VSRCS) $(CSRCS)
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $(VSRCS) $(CSRCS) \
		$(addprefix -CFLAGS , $(CFLAGS)) \
		--trace \
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN))

run: $(BIN)
	@$^



.PHONY: test verilog help compile bsp reformat checkformat clean

include $(NPC_HOME)/../Makefile


