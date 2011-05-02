#! /bin/bash

mvn install:install-file -DgroupId=com.google.android.maps -DartifactId=maps -Dversion=${ANDROID_API_VERSION} -Dpackaging=jar -Dfile=${ANDROID_HOME}/add-ons/addon_google_apis_google_inc_${ANDROID_API_PLATFORM}/libs/maps.jar

mvn install:install-file -DgroupId=android -DartifactId=android -Dversion=${ANDROID_VERSION} -Dpackaging=jar -Dfile=${ANDROID_HOME}/platforms/android-${ANDROID_API_PLATFORM}/android.jar
