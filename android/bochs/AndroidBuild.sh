#!/bin/sh


LOCAL_PATH=`dirname $0`
LOCAL_PATH=`cd $LOCAL_PATH && pwd`

ln -sf libsdl-1.2.so $LOCAL_PATH/../../../obj/local/armeabi-v7a/libSDL.so
ln -sf libsdl-1.2.so $LOCAL_PATH/../../../obj/local/armeabi-v7a/libpthread.so
ln -sf libsdl_image.so $LOCAL_PATH/../../../obj/local/armeabi-v7a/libSDL_image.so
ln -sf libsdl_ttf.so $LOCAL_PATH/../../../obj/local/armeabi-v7a/libSDL_ttf.so


if [ "$1" = armeabi-v7a ]; then
if [ \! -f bochs/configure ] ; then
	sh -c "cd bochs && ./bootstrap.sh"
fi

if [ \! -f bochs/Makefile ] ; then
env CFLAGS="-Ofast -ffast-math" \ 
env LIBS="-lgnustl_static" \
	../setEnvironment-armeabi-v7a.sh sh -c "cd bochs && ./configure --build=x86_64-unknown-linux-gnu --host=arm-linux-androideabi --with-sdl --enable-sb16 --enable-clgd54xx --enable-voodoo --disable-gameport --enable-repeat-speedups --enable-handlers-chaining --enable-ne2000 --enable-pnic --enable-e1000 --enable-usb"
fi

make -C bochs && mv -f bochs/bochs libapplication-armeabi-v7a.so
fi
