#!/bin/sh

LOCAL_PATH=`dirname $0`
LOCAL_PATH=`cd $LOCAL_PATH && pwd`

if [ \! -d BochsLauncher/bochsApp/src ] ; then
   ln -s ../../../../../androidsdl/project/src BochsLauncher/bochsApp/src
fi

if [ \! -d BochsLauncher/bochsApp/res ] ; then
   ln -s ../../../../../androidsdl/project/res BochsLauncher/bochsApp/res
fi

if [ \! -d BochsLauncher/app/src/main/jniLibs ] ; then
   ln -s ../../../../../../../androidsdl/project/libs BochsLauncher/app/src/main/jniLibs
fi

cd BochsLauncher
./gradlew assembleDebug
mv app/build/outputs/apk/debug/app-debug.apk ../bochs-debug.apk
cd ..
