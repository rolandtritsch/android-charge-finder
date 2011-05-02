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

import android.app.AlertDialog;
import android.app.Activity;

import android.content.DialogInterface;

import android.graphics.drawable.Drawable;

import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import android.content.Intent;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import java.util.List;
import java.util.Locale;

import junit.framework.Assert;
import android.widget.Toast;

/**
 * <code>CFMain</code> is the Main Activity class.
 *
 * This class sets up the main view, including the menue and all overlays on the map.
 *
 * @author <a href="mailto:roland@tritsch.org">Roland Tritsch</a>
 * @version $Id$
 *
 * @composed 1 - 1 MapView
 * @composed 1 - 1 CFChargeStationsOverlay
 * @composed 1 - 1 CFRangeOverlay
 */

public final class CFMain extends MapActivity {
    private static final String TAG = "CFMain";

    /**
     * <code>GEOPOINT_FACTOR</code> is used to convert from/to Long/Latitude.
     */
    protected static final double GEOPOINT_FACTOR = 1E6;

    /**
     * <code>mapView</code> shows the map on screen.
     */
    private MapView mapView = null;

    /**
     * <code>stationsOverlay</code> show all stations on the map.
     */
    private CFChargeStationsOverlay stationsOverlay = null;

    /**
     * <code>rangeOverlay</code> shows the range of the car on the map (as a grey circle).
     */
    private CFRangeOverlay rangeOverlay = null;

    /**
     * <code>currentLocation</code> keeps track of the current location.
     */
    private GeoPoint currentLocation = null;

    /**
     * <code>currentRange</code> keeps track of the current range.
     */
    private int currentRange = 0;

    /**
     * Creates a new <code>CFMain</code> instance.
     */
    public CFMain() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: CFMain()");
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: CFMain()");
    }

    @Override
    protected boolean isRouteDisplayed() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: isRouteDisplayed()");
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: isRouteDisplayed()");
        return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onCreate()");
	// seems this can be null - Assert.assertNotNull(savedInstanceState);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "ensure a/the geocoder implementation is present ...");
	// platform 9 API - Assert.assertTrue(Geocoder.isPresent());

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "associate this activity with a/the (main) view ...");
        this.setContentView(R.layout.main);
 
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get the mapview and init/show the zoom buttons ...");
        mapView = (MapView) this.findViewById(R.id.mapview);
        Assert.assertNotNull(mapView);
        mapView.setBuiltInZoomControls(true);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get the default address/range ...");
        currentLocation = getLocationFromAddress(getResources().getString(R.string.default_address));
        Assert.assertNotNull(currentLocation);
        currentRange = Integer.parseInt(getResources().getString(R.string.default_range));
	Assert.assertTrue(currentRange > 0);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create the stations overlay with all the charging stations on it that are within a range from the location ...");
        Drawable stationMarker = getResources().getDrawable(R.drawable.plug_tiny_red);
        Assert.assertNotNull(stationMarker);
        stationsOverlay = new CFChargeStationsOverlay(stationMarker, this);
        mapView.getOverlays().add(stationsOverlay);
 
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "create the range overlay (a transparent grey cricle) ...");
        rangeOverlay = new CFRangeOverlay();
        mapView.getOverlays().add(rangeOverlay);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "display the map for the first time ...");
	update();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onCreate()");
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onCreateOptionsMenu()");
	Assert.assertNotNull(menu);

        MenuInflater inflater = getMenuInflater();
        Assert.assertNotNull(inflater);
        inflater.inflate(R.menu.main, menu);

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onCreateOptionsMenu()");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onOptionsItemSelected()");
	Assert.assertNotNull(item);

        boolean done = false;
        switch (item.getItemId()) {
        case R.id.menu_lookup_address:
            showDialogLookupAddress();
            done = true;
            break;
        case R.id.menu_set_address:
            showDialogEnterText(R.string.alert_dialog_title_set_address);
            done = true;
            break;
        case R.id.menu_set_range:
            showDialogEnterText(R.string.alert_dialog_title_set_range);
            done = true;
            break;
        case R.id.menu_show_about:
            showDialogAbout();
            done = true;
            break;
        default:
            done = super.onOptionsItemSelected(item);
            break;
        }

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onOptionsItemSelected()");
        return done;
    }

    /**
     * <code>showDialogLookupAddress</code> picks an address from the addressbook.
     */
    private static final int CONTACT_PICKER_RESULT = 1001;
    private void showDialogLookupAddress() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogLookupAdress()");

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "send the intent to pick a/the contact ...");
	this.startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), CONTACT_PICKER_RESULT);  

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogLookupAdress()");
	return;
    }

    /**
     * <code>onActivityResult</code> catches all activity result callbacks. In our
     * case (at least right now) there is just the CONTACT_PICKER_RESULT that can/will
     * be processed here.
     *
     * @param requestCode an <code>int</code> value to map the result to the request
     * @param resultCode an <code>int</code> value to indicate if the result is OK
     * @param data an <code>Intent</code> value that contains the result
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {  
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: onActivityResult()");

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "right now the only activity result we expect is CONTACT_PICKER_RESULT...");
	Assert.assertTrue(requestCode == CONTACT_PICKER_RESULT);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "check if the pick was executed ...");
	if(resultCode != Activity.RESULT_OK) {
            if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "otherwise ... do nothing!");
	    return;
	}
	Assert.assertNotNull(data);
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Result:" + data.toString());

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "build and execute the query ...");
	final Uri queryUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
	final String queryCols[] = {ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};
        final String queryWhere = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + data.getData().getLastPathSegment();
	final String queryWhereParams[] = null;
	final String querySortBy = null;
        Cursor queryResultCursor = managedQuery(queryUri, queryCols, queryWhere, queryWhereParams, querySortBy);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "process query result ...");
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Query Result - Number of Cols: " + queryResultCursor.getColumnCount());
	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Query Result - Number of Rows: " + queryResultCursor.getCount());
	final String queryResultColumns[] = queryResultCursor.getColumnNames();
	for (String c: queryResultColumns) {
	    if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Query Result - Column:" + c);
	}
	if(queryResultCursor.getCount() == 0) {
	    Toast.makeText(getApplicationContext(), getResources().getString(R.string.query_no_address_found), Toast.LENGTH_SHORT).show();
	    return;
	}
	Assert.assertTrue(queryResultCursor.moveToFirst());
	final String queryResultAddressString = queryResultCursor.getString(0);
	if(queryResultAddressString == null) {
	    Toast.makeText(getApplicationContext(), getResources().getString(R.string.query_no_address_found), Toast.LENGTH_SHORT).show();
	    return;
	}
	Assert.assertNotNull(queryResultAddressString);
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Address:" + queryResultAddressString);
	queryResultCursor.close();

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "update map with new location ...");
        currentLocation = getLocationFromAddress(queryResultAddressString);
	update();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: onActivityResult()");
        return;
    }  

    /**
     * <code>showDialogEnterText</code> generic dialog to manually enter text.
     *
     * @param enterTextTitleId an <code>int</code> value to specify the title id for the dialog.
     */
    private void showDialogEnterText(final int enterTextTitleId) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogEnterText()");

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "get/init the enter text dialog ...");
        LayoutInflater factory = LayoutInflater.from(this);
        Assert.assertNotNull(factory);
        final View enterTextView = factory.inflate(R.layout.alert_dialog_enter_text, null);
        Assert.assertNotNull(enterTextView);
        AlertDialog.Builder enterTextBuilder = new AlertDialog.Builder(this);
        Assert.assertNotNull(enterTextBuilder);

        enterTextBuilder.setIcon(R.drawable.plug_tiny_gray);
        enterTextBuilder.setTitle(enterTextTitleId);
        enterTextBuilder.setView(enterTextView);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Setting up the event listeners ...");
        DialogInterface.OnClickListener onClickOk = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogEnterText.positiveButton.onClick()");

                if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "access the text field to read the value ...");
                EditText enterText = (EditText) enterTextView.findViewById(R.id.enter_text);
                Assert.assertNotNull(enterText);
                String enterTextString = enterText.getText().toString();
		Assert.assertNotNull(enterTextString);
                if(Log.isLoggable(TAG, Log.VERBOSE)) Log.d(TAG, "Text: " + enterTextString);

		// REVISIT - this is UGLY :(
		if (enterTextString.length()>0) {
		    switch (enterTextTitleId) {
		    case R.string.alert_dialog_title_set_address:
			currentLocation = getLocationFromAddress(enterTextString);
			break;
		    case R.string.alert_dialog_title_set_range:
			currentRange = Integer.parseInt(enterTextString);
			break;
		    default:
			Assert.assertTrue(false);
			break;
		    }

		    if(Log.isLoggable(TAG, Log.VERBOSE)) Log.d(TAG, "update the map with new location/range ...");
		    update();
		}

                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogEnterText.positiveButton.onClick()");
                return;
            }
        };
	Assert.assertNotNull(onClickOk);

        DialogInterface.OnClickListener onClickCancel = new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogEnterText.negativeButton.onClick()");
                if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogEnterText.negativeButton.onClick()");
            }
        };
	Assert.assertNotNull(onClickCancel);

	if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "hook the listeners to the UI ...");
        enterTextBuilder.setPositiveButton(R.string.alert_dialog_ok, onClickOk);
        enterTextBuilder.setNegativeButton(R.string.alert_dialog_cancel, onClickCancel);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "show the enter text dialog ...");
        enterTextBuilder.show();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogEnterText()");
        return;
    }

    /**
     * <code>showDialogAbout</code> builds and shows the About Dialog.
     */
    private void showDialogAbout() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: showDialogAbout()");

        AlertDialog.Builder about = new AlertDialog.Builder(this);
        Assert.assertNotNull(about);
        about.setPositiveButton("OK", null);
        about.setTitle(R.string.about_title);
        about.setMessage(R.string.about_message);
        about.show();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: showDialogAbout()");
        return;
    }

    /**
     * <code>getLocationFromAddress</code> translates an Address into a GeoPoint
     *
     * @param address a <code>String</code> value
     * @return a <code>GeoPoint</code> value
     */
    private GeoPoint getLocationFromAddress(final String address) {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: getLocationFromAddress()");
	Assert.assertNotNull(address);
	Assert.assertFalse(address.length() == 0);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Address: " + address);

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocationName(address, 1);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        if(addresses.size() == 0) return null;

        Assert.assertTrue(addresses.size() == 1);
        GeoPoint location = new GeoPoint((int) (addresses.get(0).getLatitude() * GEOPOINT_FACTOR), (int) (addresses.get(0).getLongitude() * GEOPOINT_FACTOR));
        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "Location: " + location.toString());

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: getLocationFromAddress()");
        return location;
    }

    /**
     * <code>update</code> moves the map to the current location and shows the current range.
     * It also resets the zoom level to the default zoom level (just to make sure you can 
     * see something).
     */
    private void update() {
        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Enter: update()");
        Assert.assertNotNull(currentLocation);
	Assert.assertTrue(currentRange > 0);

        stationsOverlay.update(currentLocation, currentRange);
        rangeOverlay.setRange(currentRange);

        if(Log.isLoggable(TAG, Log.VERBOSE)) Log.v(TAG, "update the map with the current location/range ...");
        mapView.getController().animateTo(currentLocation);
        mapView.getController().setZoom(Integer.parseInt(getResources().getString(R.string.default_zoom_level))); 
        mapView.invalidate();

        if(Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Leave: update()");
        return;
    }
}
