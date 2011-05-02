#! /bin/bash

#android --verbose create avd --target <target> --name AVD_${ANDROID_VERSION}-${ANDROID_API_VERSION} --sdcard 1000M --skin <skin>
android --verbose create avd --target 6 --name AVD_2.1_r2-7_r1 --sdcard 1000M --skin nexusone
