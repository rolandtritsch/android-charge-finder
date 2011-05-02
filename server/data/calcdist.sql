\timing on
\set FROM_X '\'point(53.27855829933471 -6.184949411621094)\''
\set TO_X '\'point(53.29466984445274 -6.168994903564453)\''
\set FROM '\'point(50.03444380113511 8.122823238372803)\''
\set TO '\'point(50.033265353706945 8.123434782028198)\''
\set SRID_0 4326
\set SRID_1 2163
\set SRID_2 900913
\set SRID_3 27500
\set FACTOR 64.774831883062347

select st_distance(st_geomfromtext(:FROM, :SRID_0), st_geomfromtext(:TO, :SRID_0))*:FACTOR as distance;
-- select st_distance(st_geomfromtext(:FROM, :SRID_1), st_geomfromtext(:TO, :SRID_1)) as distance;
-- select st_distance(st_geomfromtext(:FROM, :SRID_2), st_geomfromtext(:TO, :SRID_2)) as distance;
-- select st_distance(st_geomfromtext(:FROM, :SRID_3), st_geomfromtext(:TO, :SRID_3)) as distance;
