package com.example.rafal.rmaps;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private Marker p;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
    }

    public void onSearchClick(View v) {
        performSearch();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (p != null)
            p.remove();
        p = mMap.addMarker(new MarkerOptions().position(latLng));
        new ReverseGeoCoding().execute(latLng);
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.rafal.rmaps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        findViewById(R.id.searchBox).setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "ENTER!!");
                    InputMethodManager inputManager =
                            (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return false;
                }
                return true;
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.rafal.rmaps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private void performSearch() {
        Log.d(TAG, "search is being performed!!!!");
    }
    private class ReverseGeoCoding extends AsyncTask<LatLng, Void, String> {
        @Override
        protected String doInBackground(LatLng... latLng) {
            String result = "";

            try {
                URL url = new URL(
                        "http://maps.googleapis.com/maps/api/geocode/json?address=" + latLng[0].latitude + "," + latLng[0].longitude + "&sensor=true ");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output, full = "";
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    full += output;
                }

                GoogleGeoCodeResponse gson = new Gson().fromJson(full, GoogleGeoCodeResponse.class);

                try {
                    result = gson.results[0].formatted_address;

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            p.setSnippet(result);
            p.setTitle("Pin");
            p.showInfoWindow();
        }
    }

    private class GeoCoding extends AsyncTask<String, Void, LatLng> {
        @Override
        protected LatLng doInBackground(String... querry) {
            LatLng result = new LatLng(0, 0);/*
            try {
                URL url = new URL(
                        "http://maps.googleapis.com/maps/api/geocode/json?address=" + latLng[0].latitude + "," + latLng[0].longitude + "&sensor=true ");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output, full = "";
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    full += output;
                }

                GoogleGeoCodeResponse gson = new Gson().fromJson(full, GoogleGeoCodeResponse.class);

                try {
                    result = gson.results[0].formatted_address;

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(LatLng result) {
            Log.d(TAG, "found address:: " + result);
        }
    }
}
