YSYX_HOME=/home/chang/programs/ysyx-workbench
BUILD_DIR = ./build
NAME?= 
PROJECT_DIR= $(YSYX_HOME)/projects/$(NAME)


all:
	@echo "no arguments"

build_project:
	$(shell mkdir $(PROJECT_DIR))
	$(shell cp -r ./* $(PROJECT_DIR))

# SOURCE FROM `$(NPC_HOME)/Makefile`
test:
	mill -i __.test

verilog:
	$(call git_commit, "generate verilog")
	mkdir -p $(BUILD_DIR)
	mill -i __.test.runMain Elaborate -td $(BUILD_DIR)

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

## CHANGED ,Add other dirs
clean:
	-rm -rf $(BUILD_DIR)
## ENDCHANGED

.PHONY: test verilog help compile bsp reformat checkformat clean

## CHANGED ,Remove git_commit
sim:
	@echo "Write this Makefile by yourself."
# sim:
# 	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
# 	@echo "Write this Makefile by yourself."
## ENDCHANGED

include $(YSYX_HOME)/Makefile $(NVBOARD_HOME)/scripts/nvboard.mk

# END SOURCES
