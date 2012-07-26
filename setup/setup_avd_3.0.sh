#! /bin/bash

#android --verbose create avd --target <target> --name AVD_${ANDROID_VERSION}-${ANDROID_API_VERSION} --sdcard 1000M --skin <skin>
android --verbose create avd --target 17 --name AVD_3.0_r1-11_r1 --sdcard 2048M --skin WXGA --snapshot
