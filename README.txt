OVERVIEW/INTRODUCTION
---------------------

ChargeFinder is the "best" way to re-fuel your E-Car using an Android
Phone. The application connects to a Postgres/PostGIS backend and 
finds/displays charging stations that are still in range.

To test the backend go to ...

http://chargefinder.tritsch.org/stations.php?point_x=8&point_y=50&radius=0.3

... and try radius=0.1, radius=0.2, radius=0.3.

INSTALLATION
------------

1. Android Platform

Please install the complete Android Platform with all platforms and
all revisions.

http://developer.android.com/sdk/index.html

2. Maven

Please install maven.

http://maven.apache.org/download.html

3. Edit/Run setup_env.sh to set env vars

4. Run setup_mvn_repo.sh to install android jar files in maven repo

5. Run mvn install to build application

6. Run android list target to find the suitable target. Edit and run setup_avd.sh to create avd for emulator

7. Run emulator -verbose -avd AVD_${ANDROID_VERSION}-${ANDROID_API_VERSION}

Wait until emulator is started.

8. Run mvn android:deploy to deploy application onto the emulator

9. Goto to the emulator and start ChargeFinder :)
