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


compile_git:
	$(call git_commit, "compile NPC")

sim:
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	$(shell find ./ -name "*.vcd" )
	gtkwave $(shell find ./ -name "*.vcd" )
# @echo "Write this Makefile by yourself."

#DIFF REF
include $(FILELIST_MK)


DIFF_REF_PATH = $(NEMU_HOME)
DIFF_REF_SO = $(NEMU_HOME)/build/riscv64-nemu-interpreter-so
ARGS_DIFF = --diff=$(DIFF_REF_SO)

$(DIFF_REF_SO):
	$(MAKE) -s -C $(DIFF_REF_PATH) run-env


# rules for verilator
# INC_PATH ?= $(OBJ_DIR)/V$(TOPNAME).h
INC_PATH ?= $(EMUSRC_DIR)/include
# INC_PATH += $(NEMU_HOME)/include/generated
INCFLAGS = $(addprefix -I, $(INC_PATH)) 
CFLAGS += -I$(shell llvm-config --cxxflags) -fPIE 
CFLAGS += $(INCFLAGS) -DTOP_NAME="\"V$(TOPNAME)\"" 
LDFLAGS += -ldl -lreadline -lSDL2 $(shell llvm-config --libs) 

# @rm -rf $(OBJ_DIR)
$(BIN): verilog $(VSRCS) $(CSRCS) 
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $(VSRCS) $(CSRCS) \
		$(addprefix -CFLAGS , $(CFLAGS)) $(addprefix -LDFLAGS , $(LDFLAGS)) \
		--trace \
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN)) -CFLAGS "-g"

# Some convenient rules
override ARGS ?= --log=$(BUILD_DIR)/nemu-log.txt
override ARGS += $(ARGS_DIFF)

# Command to execute NPC
IMG ?=
NPC_EXEC := $(BIN) $(ARGS) $(IMG)

run: $(BIN) $(DIFF_REF_SO)
	$(call git_commit, "run NPC")
	$(NPC_EXEC)

gdb: $(BIN) $(DIFF_REF_SO)
	@rm -rf $(OBJ_DIR)
	$(call git_commit, "gdb NPC")
	gdb -s $(BIN) --args $(NPC_EXEC)


.PHONY: test verilog help compile bsp reformat checkformat clean $(DIFF_REF_SO)

include $(NPC_HOME)/../Makefile


