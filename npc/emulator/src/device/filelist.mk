DIRS-y += src/device/io
SRCS-$(CONFIG_DEVICE) += src/device/device.c src/device/alarm.c src/device/intr.cpp
SRCS-$(CONFIG_HAS_SERIAL) += src/device/serial.cpp
SRCS-$(CONFIG_HAS_TIMER) += src/device/timer.cpp
SRCS-$(CONFIG_HAS_KEYBOARD) += src/device/keyboard.cpp
SRCS-$(CONFIG_HAS_VGA) += src/device/vga.cpp
SRCS-$(CONFIG_HAS_AUDIO) += src/device/audio.cpp
SRCS-$(CONFIG_HAS_DISK) += src/device/disk.cpp
SRCS-$(CONFIG_HAS_SDCARD) += src/device/sdcard.cpp

SRCS-BLACKLIST-$(CONFIG_TARGET_AM) += src/device/alarm.c

ifdef CONFIG_DEVICE
ifndef CONFIG_TARGET_AM
LIBS += -lSDL2
endif
endif
