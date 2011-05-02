#! /bin/bash

rm -rf output
mkdir output

ogr2ogr -f "ESRI Shapefile" output LEMnet_D.kml
shp2pgsql -d output/LEMnet*.shp stations > LEMnet_D.sql 
psql -d roland -f LEMnet_D.sql