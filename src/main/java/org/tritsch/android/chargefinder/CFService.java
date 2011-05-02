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

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <code>CFService</code> implements the chargefinder service.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 *
 * @depend 1 - 1 CFStation
 * @depend 1 - 1 HttpClient
 * @depend 1 - 1 HttpResponse
 */
public final class CFService {
    private static final String TAG = "CFService";
    private static final String BASE_URL = "http://chargefinder.tritsch.org/stations.php";

    /**
     * Does not creates a new <code>CFService</code> instance.
     */
    private CFService() {
    }

    /**
     * <code>lockup<code> will contact the chargefinder service and will retrieve
     * a/the list of stations described by the parameters.
     *
     * @param pointX - x coordinates to start the search from
     * @param pointY - y coordinates to start the search from
     * @param radius - the radius from x, y to include in the search
     *
     * @return a/the list of stations that are within the radius
     */
    public static List<CFStation> lookup(final String pointX, final String pointY, final String radius) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: lookup()");
	Assert.assertNotNull(pointX); Assert.assertFalse(pointX.length() == 0);
	Assert.assertNotNull(pointY); Assert.assertFalse(pointY.length() == 0);
	Assert.assertNotNull(radius); Assert.assertFalse(radius.length() == 0);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "pointX:" + pointX);
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "pointY:" + pointY);
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "radius:" + radius);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create the list we will return ...");
        List<CFStation> stations = new ArrayList<CFStation>();

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create http client ...");
        HttpClient httpClient = new DefaultHttpClient();
	Assert.assertNotNull(httpClient);

	String url = "" + BASE_URL + "?point_x=" + pointX + "&point_y=" + pointY + "&radius=" + radius;
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "URL:" + url);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "go and do it ...");
        HttpResponse response = null;
        try {
            response = httpClient.execute(new HttpGet(url));
            Assert.assertNotNull(response);
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "process response ...");
	JSONArray stationsObject = null;
        try {
	    HttpEntity entity = response.getEntity();
	    Assert.assertNotNull(entity);

	    String resultString = getString(entity.getContent());
	    Assert.assertNotNull(resultString);
	    if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Result:" + resultString);

	    JSONObject resultObject = new JSONObject(resultString);
	    Assert.assertNotNull(resultObject);

	    stationsObject = resultObject.getJSONArray("stations");
	    Assert.assertNotNull(stationsObject);
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "build list of stations ...");
        try {
	    for(int i=0; i<stationsObject.length(); i++) {
		JSONObject station = stationsObject.getJSONObject(i);
		Assert.assertNotNull(station);

		CFStation newStation = new CFStation();
		newStation.setName(station.getString("name"));
		newStation.setX(station.getDouble("st_x"));
		newStation.setY(station.getDouble("st_y"));

		Assert.assertTrue(stations.add(newStation));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Assert.fail();
	}

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: lookup()");
	return stations;
    }
                
    /**
     * <code>getString</code> extract a/the string from the input stream.
     *
     * @param is an <code>InputStream</code> value
     * @return a <code>String</code> value
     */
    private static String getString(final InputStream is) {
	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: getString()");
	Assert.assertNotNull(is);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	Assert.assertNotNull(reader);
        StringBuilder sb = new StringBuilder(); 
        String line = null;
        try {
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n"); 
            }
        } catch (Exception e) {
            e.printStackTrace(); Assert.fail();
        } finally { 
            try {
		is.close(); 
	    } catch(Exception e) {
		e.printStackTrace(); 
		Assert.fail(); 
	    }
        }

	if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: getString()");
        return sb.toString();
    }
}
