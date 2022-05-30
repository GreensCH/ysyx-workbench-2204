ifdef CONFIG_ITRACE
CXXSRC = src/utils/disasm.cpp
CXXFLAGS += $(shell llvm-config --cxxflags) -fPIE
LIBS += $(shell llvm-config --libs)
endif
