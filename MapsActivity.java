package com.example.loginregister;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    //private static final double SAN_MARCOS_LAT =29.892515, SAN_MARCOS_LNG = -97.940393;

    private GoogleApiClient mLocationClient;
    Marker marker;
    Circle shape;

    String placesSearchStr;
    private static String browserKeyString = "AIzaSyAwTLnPRHf46vE5vnWFLvj_tIWGZbGdLF8";

    private static String keyString = "AIzaSyAwTLnPRHf46vE5vnWFLvj_tIWGZbGdLF8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_maps);
            if (initMap()) {
                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mLocationClient.connect();
            } else {
                Toast.makeText(MapsActivity.this, "Map not connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    public boolean servicesOK() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleAPI.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleAPI.getErrorDialog(this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MapsActivity.this, "Can't Connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
            mMap.getUiSettings().setZoomControlsEnabled(true);
        }

        return (mMap != null); // Tells whether the initialization process was successful
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(update);
    }

    private void removeEverything() {
        marker.remove();
        marker = null;
        shape.remove();
        shape = null;
    }

    public void updateHospitals(double lat, double lng) {

        String types = "hospital|doctor|health";
        try {
            types = URLEncoder.encode(types, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location=" + lat + "," + lng +
                "&radius=5000&sensor=true" +
                "&types=" + types +
                "&key=" + browserKeyString;

        new GetPlaces().execute(placesSearchStr);

    }


    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Drawable drawable = menu.findItem(R.id.currentLocation).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }


    public void showCurrentLocation(MenuItem item) throws IOException {

        //noinspection ResourceType
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(MapsActivity.this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            if (list.size() > 0) {
                Address ad = list.get(0);
                String locality = ad.getLocality();
                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();
                mMap.animateCamera(update);
                MarkerOptions options = new MarkerOptions()
                        .title(locality)
                        .snippet("Your current location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

                CircleOptions circleOptions = new CircleOptions()
                        .strokeWidth(3)
                        .fillColor(0x330000FF)
                        .strokeColor(Color.BLUE)
                        .center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                        .radius(5000);
                mMap.addMarker(options);
                shape = mMap.addCircle(circleOptions);

            }

            updateHospitals(currentLocation.getLatitude(), currentLocation.getLongitude());

        }

    }

    public void geoLocate(View v) throws IOException {

        hideSoftKeyboard(v);
        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();


        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address ad = list.get(0);
            String locality = ad.getLocality();
            Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();

            double lat = ad.getLatitude();
            double lng = ad.getLongitude();

            gotoLocation(lat, lng, 15);

            MarkerOptions options = new MarkerOptions()
                    .title(locality)
                    .snippet("Your location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(new LatLng(lat, lng));

            CircleOptions circleOptions = new CircleOptions()
                    .strokeWidth(3)
                    .fillColor(0x330000FF)
                    .strokeColor(Color.BLUE)
                    .center(new LatLng(lat, lng))
                    .radius(5000);
            mMap.addMarker(options);
            shape = mMap.addCircle(circleOptions);
            updateHospitals(lat, lng);
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(MapsActivity.this, "Ready to Map", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class GetPlaces extends AsyncTask<String, Void, String> {


        private Marker[] placeMarkers;
        private final int MAX_PLACES = 20;
        private MarkerOptions[] places;

        @Override
        protected String doInBackground(String... placesURL) {   //doInBackground method to request and retrieve the data
            //fetch places
            StringBuilder placesBuilder = new StringBuilder();

            //process search parameter string(s)
            for (String placeSearchURL : placesURL) {
                //execute search
                HttpClient placesClient = new DefaultHttpClient();
                try {
                    //try to fetch the data
                    HttpGet placesGet = new HttpGet(placeSearchURL);
                    HttpResponse placesResponse = placesClient.execute(placesGet);

                    StatusLine placeSearchStatus = placesResponse.getStatusLine();

                    if (placeSearchStatus.getStatusCode() == 200) {
                        //we have an OK response
                        HttpEntity placesEntity = placesResponse.getEntity();
                        InputStream placesContent = placesEntity.getContent(); // retrieving the JSON string content

                        InputStreamReader placesInput = new InputStreamReader(placesContent);

                        BufferedReader placesReader = new BufferedReader(placesInput);

                        String lineIn;
                        while ((lineIn = placesReader.readLine()) != null) {
                            placesBuilder.append(lineIn);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return placesBuilder.toString();
        }

        //Implement the onPostExecute method to parse the JSON string returned from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            placeMarkers = new Marker[MAX_PLACES];


            //remove any existing markers on the map
            if (placeMarkers != null) {
                for (int pm = 0; pm < placeMarkers.length; pm++) {
                    if (placeMarkers[pm] != null)
                        placeMarkers[pm].remove();
                }
            }

            try {
                //parse JSON
                JSONObject resultObject = new JSONObject(result);

                //the places are contained within an array named "results"
                JSONArray placesArray = resultObject.getJSONArray("results");

                places = new MarkerOptions[placesArray.length()]; //This should give us a MarkerOptions object for each place returned

                //loop through places
                for (int p = 0; p < placesArray.length(); p++) {
                    //parse each place


                    //If any of the values are missing in the returned JSON feed, we will simply not
                    //display a Marker for that place, in case of Exceptions. To keep track of this, add a boolean flag
                    boolean missingValue = false;

                    LatLng placeLL = null;
                    String placeName = "";
                    String vicinity = "";
                    try {
                        //attempt to retrieve place data values
                        missingValue = false;

                        JSONObject placeObject = placesArray.getJSONObject(p);
                        JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");

                        placeLL = new LatLng(
                                Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));

                        vicinity = placeObject.getString("vicinity");

                        placeName = placeObject.getString("name");

                    } catch (JSONException jse) {
                        missingValue = true;
                        jse.printStackTrace();
                    }

                    //check that value and set the place MarkerOptions object to null,
                    // so that we don't attempt to instantiate any Marker objects with missing data
                    if (missingValue)
                        places[p] = null;

                    else
                        places[p] = new MarkerOptions()
                                .position(placeLL)
                                .title(placeName)
                                .snippet(vicinity);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (places != null && placeMarkers != null) {
                for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
                    //will be null if a value was missing
                    if (places[p] != null)
                        placeMarkers[p] = mMap.addMarker(places[p]);
                }
            }

        }


    }
}