/*
 * Copyright (C) 2010 Roland Tritsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tritsch.android.chargefinder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

/**
 * <code>CFChargeStationsOverlay</code> loads all of the charge stations
 * that are with in a radius of range*TRITSCH_FACTOR.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 *
 * @compond 1 - N OverlayItem
 * @depend 1 - 1 Toast
 * @depend 1 - 1 CFService
 * @depend 1 - 1 CFStation
 */
public final class CFChargeStationsOverlay extends ItemizedOverlay {
    private static final String TAG = "CFChargeStationsOverlay";

    /**
     * based on hermann's calculation, this is the factor that we need to apply
     * in germany to convert to go from GeoPoints to meters 
     */
    private static final double SAUER_FACTOR = 64.774831883062347;

    /**
     * herman got a factor, so i wanted to have one too :). it determines
     * how much bigger the radius is to the range
     */
    private static final int TRITSCH_FACTOR = 2;

    /**
     * <code>KILOMETERS</code> converts the range to KMs.
     */
    private static final int KILOMETERS = 1000;

    /**
     * <code>stations</code> holds all stations that are displayed on the map.
     */
    private ArrayList<OverlayItem> stations = new ArrayList<OverlayItem>();

    private Context context = null;

    /**
     * Creates a new <code>CFChargeStationsOverlay</code> instance.
     *
     * @param defaultMarker a <code>Drawable</code> value
     * @param context a <code>Context</code> value
     */
    public CFChargeStationsOverlay(final Drawable defaultMarker, final Context ctx) {
        super(boundCenterBottom(defaultMarker));
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: CFChargeStationsOverlay()");
	Assert.assertNotNull(defaultMarker);
        Assert.assertNotNull(ctx);

        this.context = ctx;

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: CFChargeStationsOverlay()");
    }

    @Override
    protected OverlayItem createItem(final int i) {
        return stations.get(i);
    }

    @Override
    public int size() {
        return stations.size();
    }

    @Override
    protected boolean onTap(final int i) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onTap()");

        OverlayItem item = stations.get(i);
        Assert.assertNotNull(item);
        Toast.makeText(context, item.getSnippet() + " - (" + item.getTitle() + ")", Toast.LENGTH_LONG).show();

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onTap()");
        return true;
    }

    /**
     * <code>addStation</code> add a station to the list
     * of stations that will be displayed.
     *
     * @param station an <code>OverlayItem</code> value
     */
    public void addStation(final OverlayItem station) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: addStation()");
	Assert.assertNotNull(station);

        stations.add(station);
        populate();

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: addStation()");
	return;
    }

    /**
     * <code>update</code> the ChargeStationsOverlay by finding all stations that
     * within a radius of range*TRITSCH_FACTOR around a given location, add these 
     * stations to the overlay and finally display these stations on the map.
     *
     * @param location a <code>GeoPoint</code> value
     * @param range an <code>int</code> value
     */
    public void update(final GeoPoint location, final int range) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: update()");
	Assert.assertNotNull(location);
	Assert.assertTrue(range > 0);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Location: " + location.toString());
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Range: " + range);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "converting the parameters into strings ...");
        String pointX = Double.toString(((double) location.getLongitudeE6()) / CFMain.GEOPOINT_FACTOR);
        String pointY = Double.toString(((double) location.getLatitudeE6()) / CFMain.GEOPOINT_FACTOR);
        String radius = Double.toString((range / SAUER_FACTOR / KILOMETERS)*TRITSCH_FACTOR);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "lookup the new set of stations ...");
        List<CFStation> newStations = CFService.lookup(pointX, pointY, radius);
        Assert.assertNotNull(newStations);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "putting " + newStations.size() + " stations on the map ...");
        Iterator<CFStation> i = newStations.iterator();
        while(i.hasNext()) {
            CFStation station = i.next();
            try {            
                GeoPoint stationPoint = new GeoPoint((int) (station.getY() * CFMain.GEOPOINT_FACTOR), (int) (station.getX() * CFMain.GEOPOINT_FACTOR));
                OverlayItem stationOverlayItem = new OverlayItem(stationPoint, stationPoint.toString(), station.getName());
                addStation(stationOverlayItem);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail();
            }
        }

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: update()");
        return;
    }
}
