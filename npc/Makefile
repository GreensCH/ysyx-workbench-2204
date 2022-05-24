TOPNAME = Top
BUILD_DIR = ./build
EMUSRC_DIR = ./emulator
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
	-rm -rf $(VERILOG_DIR)

sim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	@echo "Write this Makefile by yourself."

# rules for verilator
INC_PATH ?= $(OBJ_DIR)/V$(TOPNAME).h
INCFLAGS = $(addprefix -I, $(INC_PATH))
NEMU_PATH = $(shell find $(abspath $(NEMU_HOME)/include) -name "*.h")
# NEMUFLAGS = $(addprefix -I, $(NEMU_PATH))
CFLAGS += $(NEMUFLAGS) $(INCFLAGS) -DTOP_NAME="\"V$(TOPNAME)\""

$(BIN): verilog $(VSRCS) $(CSRCS)
	@rm -rf $(OBJ_DIR)
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $(VSRCS) $(CSRCS) \
		$(addprefix -CFLAGS , $(CFLAGS)) \
		--trace \
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN))

run: $(BIN)
	@$^

.PHONY: test verilog help compile bsp reformat checkformat clean

include $(NPC_HOME)/../Makefile


