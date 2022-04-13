YSYX_HOME=/home/chang/programs/ysyx-workbench
NAME?=default
PROJECT_DIR= $(YSYX_HOME)/projects/$(NAME)


default: 
	@echo "Please execute 'make run'"

build_project:
	$(shell mkdir $(PROJECT_DIR))
	$(shell cp -r ./* $(PROJECT_DIR))
##### Sources from example in NVboard

TOPNAME = top
NXDC_FILES = constr/top.nxdc
INC_PATH ?=

VERILATOR = verilator
VERILATOR_CFLAGS += -MMD --build -cc  \
				-O3 --x-assign fast --x-initial fast --noassert 
## --x-assign fast : Xs 初始化时快速自动推断
## -CFLAGS <flags> : C++ compiler arguments for makefile
## -03 			   : High performance optimizations
## -LDFLAGS <flags>: Linker pre-object arguments for makefile

# build project constructino
BUILD_DIR = ./generated
OBJ_DIR = $(BUILD_DIR)/obj_dir
BIN = $(BUILD_DIR)/$(TOPNAME)
BIN_NB = $(BUILD_DIR)/$(TOPNAME) + _NB
$(shell mkdir -p $(BUILD_DIR))
## constraint file
SRC_AUTO_BIND = $(abspath $(BUILD_DIR)/auto_bind.cpp)
## project source
VSRCS = $(shell find $(abspath ./vsrc) -name "*.v")
CSRCS = $(shell find $(abspath ./csrc) -name "*.c" -or -name "*.cc" -or -name "*.cpp")
CSRCS += $(SRC_AUTO_BIND)


# create auto constraint file
$(SRC_AUTO_BIND): $(NXDC_FILES)
	python3 $(NVBOARD_HOME)/scripts/auto_pin_bind.py $^ $@

# include rules for NVBoard
include $(NVBOARD_HOME)/scripts/nvboard.mk

# rules for verilator
INCFLAGS = $(addprefix -I, $(INC_PATH))
CFLAGS += $(INCFLAGS) -DTOP_NAME="\"V$(TOPNAME)\""
LDFLAGS += -lSDL2 -lSDL2_image

$(BIN): $(VSRCS) $(CSRCS) $(NVBOARD_ARCHIVE)
	@rm -rf $(OBJ_DIR)
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $^ \
		$(addprefix -CFLAGS , $(CFLAGS)) $(addprefix -LDFLAGS , $(LDFLAGS)) \
		--trace \
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN))

# rules for verilator without auto bind
CSRCS_NB = $(shell find $(abspath ./csrc) -name "*.c" -or -name "*.cc" -or -name "*.cpp")
run_nb: $(VSRCS) $(CSRCS_NB) $(NVBOARD_ARCHIVE)
	@echo "****remove obj_dir start****"
	-rm -rf $(OBJ_DIR)
	@echo "****remove obj_dir success****"
	@echo "****verilator compiltion start****"
	$(VERILATOR) $(VERILATOR_CFLAGS) \
		--top-module $(TOPNAME) $^ \
		$(addprefix -CFLAGS , $(CFLAGS)) \
		$(addprefix -LDFLAGS , $(LDFLAGS)) \
		--trace	\
		--Mdir $(OBJ_DIR) --exe -o $(abspath $(BIN))
	$(BIN) 
	@echo "****verilator compiltion end****"

# rulse for link
run_build: $(VSRCS) $(CSRCS_NB) $(NVBOARD_ARCHIVE)
	@echo "****remove obj_dir start****"
	-rm -rf $(OBJ_DIR)
	@echo "****remove obj_dir success****"
	@echo "****verilator build start****"
	$(VERILATOR) --cc --build \
		--top-module $(TOPNAME) $^ \
		$(addprefix -CFLAGS , $(CFLAGS)) \
		$(addprefix -LDFLAGS , $(LDFLAGS)) \
		--trace	\
		--Mdir $(OBJ_DIR) 
	@echo "****verilator build end****"

# rules for simulation using gtkwave
sim:
	gtkwave $(shell find ./ -name "*.vcd" )

#run : 运行可执行文件
run_exe:
	$(BIN)

# all : 运行default
all: default

# run : 运行所有依赖项
run: $(BIN)
	@$^

# clean workbench
clean:
	rm -rf $(BUILD_DIR)

.PHONY: default all clean run
