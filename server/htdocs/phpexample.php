<?php
// Connecting, selecting database
$dbconn = pg_connect("host=localhost dbname=roland user=roland password=") or die('Could not connect: ' . pg_last_error());

// Performing SQL query
// $query = 'SELECT * FROM stations';
$query = 'select name, ST_Distance(the_geom, \'POINT(13 51)\'), ST_AsText(the_geom) from stations where ST_DWithin(the_geom, \'POINT(13 51)\', 0.5)';
$result = pg_query($query) or die('Query failed: ' . pg_last_error());

// Printing results in HTML
echo "<table>\n";
while ($line = pg_fetch_array($result, null, PGSQL_ASSOC)) {
    echo "\t<tr>\n";
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }
    echo "\t</tr>\n";
}
echo "</table>\n";

// Free resultset
pg_free_result($result);

// Closing connection
pg_close($dbconn);
?>