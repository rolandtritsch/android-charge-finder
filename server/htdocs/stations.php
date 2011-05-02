<?php
/*
Copyright (C) 2010 Roland Tritsch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

// configuration
$hostname = 'localhost';
$dbname = 'roland';
$username = 'roland';
$password = '';

// set defaults (EDS, Ruesselsheim)
$point_x = 8.432478904724121;
$point_y = 49.9789082159918;
$radius = 1.0;

// get paramters
if(isset($_GET['point_x']) && $_GET['point_x'] !="") {$point_x = $_GET['point_x'];};
if(isset($_GET['point_y']) && $_GET['point_y'] !="") {$point_y = $_GET['point_y'];};
if(isset($_GET['radius']) && $_GET['radius'] !="") {$radius = $_GET['radius'];};

// connect to database
$dbconn = pg_connect('host='.$hostname.' dbname='.$dbname.' user='.$username.' password='.$password) or die('Could not connect: ' . pg_last_error());

// get all stations
$query = 'select name, ST_X(the_geom), ST_Y(the_geom) from stations where ST_DWithin(the_geom, \'POINT('.$point_x.' '.$point_y.')\', '.$radius.')';

// process result into json
$result = pg_query($query) or die('Query failed: ' . pg_last_error());
$arr = array();
while ($row = pg_fetch_array($result, null, PGSQL_ASSOC)) {
  $arr[] = $row;
}
echo '{"stations":'.json_encode($arr) .'}';

// Free resultset
pg_free_result($result);

// close connection
pg_close($dbconn);
?>