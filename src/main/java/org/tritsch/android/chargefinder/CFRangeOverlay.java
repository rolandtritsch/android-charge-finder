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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import junit.framework.Assert;

/**
 * <code>CFRangeOverlay</code> draws a circle on the screen/map
 * to indicate the range of the car.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 *
 * @depend 1 - 1 Mapview
 */
public final class CFRangeOverlay extends Overlay {
    private static final String TAG = "CFRangeOverlay";

    private static final int LIGHT = 64;
    private static final int GREY = 75;
    private static final int WHITE = 255;
    private static final int WIDTH = 5;
    
    private Paint circlePaint = new Paint();
    private Paint borderPaint = new Paint();

    private int range = 0;

    /**
     * Creates a new <code>CFRangeOverlay</code> instance.
     * Sets the circle paint to light grey and the border paint to white.
     */
    public CFRangeOverlay() {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: CFRangeOverlay()");

        circlePaint.setARGB(LIGHT, GREY, GREY, GREY);

        borderPaint.setARGB(WHITE, WHITE, WHITE, WHITE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(WIDTH);

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: CFRangeOverlay()");
    }

    @Override
    public void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: draw()");
	Assert.assertNotNull(canvas);
	Assert.assertNotNull(mapView);

        GeoPoint center = mapView.getMapCenter();
	Assert.assertNotNull(center);
        Projection projection = mapView.getProjection();
	Assert.assertNotNull(projection);
        Point p = projection.toPixels(center, null);
	Assert.assertNotNull(p);
        float radius = projection.metersToEquatorPixels(range);

        canvas.drawCircle(p.x, p.y, radius, circlePaint);
        canvas.drawCircle(p.x, p.y, radius, borderPaint);

        super.draw(canvas, mapView, shadow);

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: draw()");
        return;
    }

    /**
     * <code>setRange</code> sets the range of the car.
     *
     * @param range an <code>int</code> value
     */
    public void setRange(final int r) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: setRange()");
	Assert.assertTrue(r > 0);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Range:" + r);

        this.range = r;

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: setRange()");
        return;
    }
}
