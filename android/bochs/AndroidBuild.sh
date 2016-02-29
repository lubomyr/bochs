#!/bin/sh


LOCAL_PATH=`dirname $0`
LOCAL_PATH=`cd $LOCAL_PATH && pwd`

ln -sf libsdl-1.2.so $LOCAL_PATH/../../../obj/local/$1/libSDL.so
ln -sf libsdl-1.2.so $LOCAL_PATH/../../../obj/local/$1/libpthread.so
ln -sf libsdl_image.so $LOCAL_PATH/../../../obj/local/$1/libSDL_image.so
ln -sf libsdl_ttf.so $LOCAL_PATH/../../../obj/local/$1/libSDL_ttf.so


if [ \! -f bochs/configure ] ; then
	sh -c "cd bochs && ./bootstrap.sh"
fi

if [ \! -f bochs/Makefile ] ; then

env CFLAGS="-Ofast -ffast-math" \ 
env LIBS="-lgnustl_static" \
	../setEnvironment-$1.sh sh -c "cd bochs && ./configure --build=x86_64-unknown-linux-gnu --host=$2 --with-sdl --enable-clgd54xx --enable-voodoo --enable-sb16 --disable-gameport --enable-ne2000 --enable-usb"
fi

make -C bochs && mv -f bochs/bochs libapplication-$1.so

