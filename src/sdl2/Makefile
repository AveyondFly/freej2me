#!/bin/sh

# Make file for Anbu and SDL_interface
all: anbu.cpp
	@echo "Building Anbu, sdl_interface for FreeJ2ME ..."
	@/images/test/distribution/build.JELOS-RK3566.aarch64/toolchain/bin/aarch64-libreelec-linux-gnueabi-g++ -std=c++11 -o sdl_interface anbu.cpp -lSDL2 -lpthread -lfreeimage 

install:
	@echo "Installing interface to /usr/local/bin/"
	@cp ./sdl_interface /usr/local/bin/sdl_interface

clean:
	rm ./sdl_interface
