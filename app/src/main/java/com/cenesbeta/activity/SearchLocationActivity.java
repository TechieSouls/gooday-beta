package com.cenesbeta.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.LocationAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.adapter.SearchLocationAdapter;
import com.cenesbeta.application.CenesApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchLocationActivity extends CenesActivity implements LocationListener {

    private static Integer LOCATION_PERMISSION_CODE = 1001;
    private ImageView closeSearchLocationBtn;
    private ListView gathSearchLocationListView;
    private EditText locationSearchEditText;
    private TextView tvPreviousSearchHeader;
    private Button btnCustomLocation;

    private CenesApplication cenesApplication;

    private SearchLocationAdapter searchLocationAdapter;
    private String customLocation;
    private LocationManager locationManager;
    private String mprovider;
    private com.cenesbeta.bo.Location currentLocation;
    private List<com.cenesbeta.bo.Location> recentLocations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        initializeComponents();
        addClickListeners();

        try {
            locationSearchEditText.addTextChangedListener(new TextWatcher() {

                CountDownTimer timer = null;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(final CharSequence charSequence, int start, int before, int count) {

                    Log.e("Searchable Text : ", charSequence.toString());
                    if (timer != null) {
                        timer.cancel();
                    }

                    timer = new CountDownTimer(800, 500) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            if (charSequence.toString() == "" || charSequence.toString().length() == 0) {
                                tvPreviousSearchHeader.setVisibility(View.VISIBLE);

                                btnCustomLocation.setVisibility(View.GONE);
                                if (recentLocations.size() == 0) {
                                    tvPreviousSearchHeader.setVisibility(View.GONE);
                                }
                                searchLocationAdapter = new SearchLocationAdapter(SearchLocationActivity.this, recentLocations);
                                gathSearchLocationListView.setAdapter(searchLocationAdapter);
                            } else {

                                btnCustomLocation.setVisibility(View.VISIBLE);

                                if (currentLocation != null && currentLocation.getLatitude() != null && currentLocation.getLongitude() != null) {
                                    String queryStr = "location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&radius=5000&name=" + charSequence.toString().replaceAll(" ", "+");

                                    System.out.println(queryStr);
                                    new LocationAsyncTask.SearchNearByLocationTask(new LocationAsyncTask.SearchNearByLocationTask.AsyncResponse() {
                                        @Override
                                        public void processFinish(JSONObject response) {
                                            try {

                                                String status = response.getString("status");
                                                if (status.equals("OK") || status.equals("ZERO_RESULTS")) {
                                                    tvPreviousSearchHeader.setVisibility(View.GONE);

                                                    JSONArray locationsNSArray = response.getJSONArray("results");

                                                    if (locationsNSArray.length() > 0) {

                                                        List<com.cenesbeta.bo.Location> locations = new ArrayList<>();
                                                        for (int i = 0; i < locationsNSArray.length(); i++) {

                                                            JSONObject locationDict = locationsNSArray.getJSONObject(i);

                                                            com.cenesbeta.bo.Location location = new com.cenesbeta.bo.Location();
                                                            location.setLocation(locationDict.getString("name"));
                                                            location.setAddress(locationDict.getString("vicinity"));
                                                            location.setPlaceId(locationDict.getString("place_id"));

                                                            JSONObject geomatery = locationDict.getJSONObject("geometry");
                                                            JSONObject geomateryLocation = geomatery.getJSONObject("location");

                                                            float kms = getKmFromLatLong(Float.valueOf(currentLocation.getLatitude()), Float.valueOf(currentLocation.getLongitude()), Float.valueOf(geomateryLocation.getString("lat")), Float.valueOf(geomateryLocation.getString("lng")));
                                                            location.setKilometers(String.valueOf((kms)) + "Km");
                                                            locations.add(location);
                                                        }

                                                        searchLocationAdapter = new SearchLocationAdapter(SearchLocationActivity.this, locations);
                                                        gathSearchLocationListView.setAdapter(searchLocationAdapter);

                                                    } else {

                                                        fetchWorldWideLocations(charSequence.toString());

                                                    }

                                                } else {

                                                    fetchWorldWideLocations(charSequence.toString());

                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).execute(queryStr);

                                } else {
                                    fetchWorldWideLocations(charSequence.toString());
                                }
                            }
                        }
                    }.start();
                }

                @Override
                public void afterTextChanged(final Editable editable) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.close_search_location_btn:
                    onBackPressed();
                    break;
                case R.id.ll_loc_title_add:
                    break;
                case R.id.btn_custom_location:

                    if (locationSearchEditText.getText().toString().length() > 0) {

                        Intent intent = new Intent();
                        intent.putExtra("title", locationSearchEditText.getText().toString());
                        intent.putExtra("selection", "done");
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                    }
                    break;
            }
        }
    };

    public void initializeComponents() {

        try {
            closeSearchLocationBtn = (ImageView) findViewById(R.id.close_search_location_btn);
            gathSearchLocationListView = (ListView) findViewById(R.id.gath_search_location_list_view);
            locationSearchEditText = (EditText) findViewById(R.id.search_location_edit_text);
            btnCustomLocation = (Button) findViewById(R.id.btn_custom_location);
            tvPreviousSearchHeader = (TextView) findViewById(R.id.tv_previous_search_header);
            //recyclerViewRecentLocations = (RecyclerView) findViewById(R.id.rv_recent_places);
            cenesApplication = getCenesApplication();

            customLocation = "";
            btnCustomLocation.setVisibility(View.GONE);

            new LocationAsyncTask(cenesApplication);


            checkLocationPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchWorldWideLocations(String text) {

        String queryStr = "input=" + text.replaceAll(" ", "+");

        new LocationAsyncTask.SearchWorldWideLocationTask(new LocationAsyncTask.SearchWorldWideLocationTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {

                    String status = response.getString("status");
                    if (status.equals("OK") || status.equals("ZERO_RESULTS")) {
                        tvPreviousSearchHeader.setVisibility(View.GONE);

                        JSONArray locationsNSArray = response.getJSONArray("predictions");

                        List<com.cenesbeta.bo.Location> locations = new ArrayList<>();
                        for (int i = 0; i < locationsNSArray.length(); i++) {

                            JSONObject locationDict = locationsNSArray.getJSONObject(i);
                            JSONObject structuredFormatting = locationDict.getJSONObject("structured_formatting");

                            com.cenesbeta.bo.Location location = new com.cenesbeta.bo.Location();
                            location.setLocation(structuredFormatting.getString("main_text"));
                            if (structuredFormatting.has("secondary_text")) {
                                location.setAddress(structuredFormatting.getString("secondary_text"));
                            } else {
                                location.setAddress(structuredFormatting.getString("main_text"));
                            }
                            location.setPlaceId(locationDict.getString("place_id"));
                            locations.add(location);

                        }
                        searchLocationAdapter = new SearchLocationAdapter(SearchLocationActivity.this, locations);
                        gathSearchLocationListView.setAdapter(searchLocationAdapter);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).execute(queryStr);
    }

    public void recentLocationCall() {
        new LocationAsyncTask.RecentLocationTask(new LocationAsyncTask.RecentLocationTask.AsyncResponse() {
            @Override
            public void processFinish(List<com.cenesbeta.bo.Location> locations) {

                recentLocations = new ArrayList<>();

                tvPreviousSearchHeader.setVisibility(View.VISIBLE);

                if (locations.size() == 0) {
                    tvPreviousSearchHeader.setVisibility(View.GONE);
                }


                if (locations != null && locations.size() > 0) {
                    recentLocations = locations;

                    if (currentLocation != null && currentLocation.getLatitude() != null && currentLocation.getLongitude() != null) {
                        for (com.cenesbeta.bo.Location loc : recentLocations) {
                            float kms = getKmFromLatLong(Float.valueOf(currentLocation.getLatitude()), Float.valueOf(currentLocation.getLongitude()), Float.valueOf(loc.getLatitude()), Float.valueOf(loc.getLongitude()));
                            loc.setKilometers(String.valueOf((kms)) + "Km");
                        }
                    }

                    searchLocationAdapter = new SearchLocationAdapter(SearchLocationActivity.this, recentLocations);
                    gathSearchLocationListView.setAdapter(searchLocationAdapter);


                }
            }
        }).execute();
    }

    public void addClickListeners() {
        closeSearchLocationBtn.setOnClickListener(onClickListener);
        btnCustomLocation.setOnClickListener(onClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                List<String> providers = locationManager.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {

                    Location l = locationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }

                currentLocation = new com.cenesbeta.bo.Location();

                //Log.e("Current Longitude:", bestLocation.getLongitude() + "");
                //Log.e("Current Latitude:", bestLocation.getLatitude() + "");

                currentLocation.setLatitude(String.valueOf(bestLocation.getLatitude()));
                currentLocation.setLongitude(String.valueOf(bestLocation.getLongitude()));

                recentLocationCall();
            }

        }
    }

    public float getKmFromLatLong(float lat1, float lng1, float lat2, float lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        float distanceInMeters = loc1.distanceTo(loc2);

        float dist = Float.parseFloat(String.format("%.1f", distanceInMeters/1000));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        System.out.println("Kilomerets : "+Float.valueOf(decimalFormat.format(dist)));
        return Float.valueOf(decimalFormat.format(dist)); // output is 102.24
    }

    public void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);

        } else {

            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {

                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }

            currentLocation = new com.cenesbeta.bo.Location();

            //Log.e("Current Longitude:", bestLocation.getLongitude() + "");
            //Log.e("Current Latitude:", bestLocation.getLatitude() + "");

            currentLocation.setLatitude(String.valueOf(bestLocation.getLatitude()));
            currentLocation.setLongitude(String.valueOf(bestLocation.getLongitude()));

            recentLocationCall();
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        currentLocation = new com.cenesbeta.bo.Location();

        Log.e("Current Longitude:", location.getLongitude() + "");
        Log.e("Current Latitude:", location.getLatitude() + "");

        currentLocation.setLatitude(String.valueOf(location.getLatitude()));
        currentLocation.setLongitude(String.valueOf(location.getLongitude()));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        System.out.println("onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("onProviderEnabled");

    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("onProviderDisabled");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println(data.toString());
        // Initialize the location fields
        System.out.println("Provider " + mprovider + " has been selected.");


    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("On Resume Called.");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        System.out.println("On Resume Next...");
        //locationManager.requestLocationUpdates(mprovider, 400, 1, this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        mprovider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(mprovider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + mprovider + " has been selected.");
            onLocationChanged(location);
        }
    }
}
