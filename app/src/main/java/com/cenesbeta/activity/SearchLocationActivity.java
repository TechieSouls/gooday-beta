package com.cenesbeta.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cenesbeta.AsyncTasks.LocationAsyncTask;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.R;
import com.cenesbeta.adapter.RecentLocationAdapter;
import com.cenesbeta.adapter.SearchLocationAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.coremanager.CoreManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;
import java.util.Timer;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchLocationActivity extends CenesActivity implements LocationListener {

    private ImageView closeSearchLocationBtn;
    private ListView gathSearchLocationListView;
    private EditText locationSearchEditText;
    private Button btnCustomLocation;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewRecentLocations;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private ApiManager apiManager;


    private SearchLocationAdapter searchLocationAdapter;
    private String customLocation;
    private LocationManager locationManager;
    private String mprovider;
    private LocationAsyncTask locationAsyncTask;
    private Boolean previousSearchesExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        initializeComponents();
        addClickListeners();

        try {
            locationSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }



                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                    //if (before == 0 && count == 1 && charSequence.charAt(start) == '\n') {
                    Log.e("Searchable Text : ",charSequence.toString());
                    //b.performClick();
                    //e.getText().replace(start, start + 1, ""); //remove the <enter>
                    //}



                }
                private Timer timer=new Timer();
                private final long DELAY = 500; // milliseconds
                @Override
                public void afterTextChanged(final Editable editable) {
                /*timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                Log.e("Keyword ",editable.toString());*/

                    customLocation = editable.toString();
                    if (editable.toString() == "" || editable.toString().length() == 0) {
                        //recyclerViewRecentLocations.setVisibility(View.VISIBLE);
                        if (previousSearchesExists) {
                            findViewById(R.id.ll_recent_recycler_view).setVisibility(View.VISIBLE);
                            gathSearchLocationListView.setVisibility(View.GONE);
                        }
                    } else {
                        //recyclerViewRecentLocations.setVisibility(View.GONE);
                        findViewById(R.id.ll_recent_recycler_view).setVisibility(View.GONE);
                        gathSearchLocationListView.setVisibility(View.VISIBLE);
                        new SearchLocationTask().execute(editable.toString());
                    }

                            /*}
                        },
                        DELAY
                );*/
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
                    Intent intent = new Intent();
                    intent.putExtra("title",customLocation);
                    intent.putExtra("selection","done");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
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

            recyclerViewRecentLocations = (RecyclerView) findViewById(R.id.rv_recent_places);
            cenesApplication = getCenesApplication();
            coreManager = cenesApplication.getCoreManager();
            apiManager = coreManager.getApiManager();

            customLocation = "";


            locationAsyncTask = new LocationAsyncTask(cenesApplication);
            new LocationAsyncTask.RecentLocationTask(new LocationAsyncTask.RecentLocationTask.AsyncResponse() {
                @Override
                public void processFinish(List<com.cenesbeta.bo.Location> locations) {

                    if (locations != null && locations.size() > 0) {
                        previousSearchesExists = true;

                        findViewById(R.id.ll_recent_recycler_view).setVisibility(View.VISIBLE);
                        RecentLocationAdapter rla = new RecentLocationAdapter(SearchLocationActivity.this, locations);

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewRecentLocations.setLayoutManager(mLayoutManager);
                        recyclerViewRecentLocations.setItemAnimator(new DefaultItemAnimator());
                        recyclerViewRecentLocations.setAdapter(rla);
                    }
                }
            }).execute();

            checkUserLocationServiceOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClickListeners() {
        closeSearchLocationBtn.setOnClickListener(onClickListener);
        btnCustomLocation.setOnClickListener(onClickListener);
    }

    class SearchLocationTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String keyword = strings[0];
            try {
                String queryStr = "&input="+ URLEncoder.encode(keyword);
                JSONObject job = apiManager.locationSearch(queryStr);
                JSONArray locations = job.getJSONArray("predictions");
                searchLocationAdapter = new SearchLocationAdapter(SearchLocationActivity.this,locations);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null) {
                showRequestTimeoutDialog();
            } else {
                gathSearchLocationListView.setAdapter(searchLocationAdapter);
            }
        }
    }

    public void checkUserLocationServiceOn() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e("Current Longitude:" , location.getLongitude()+"");
        Log.e("Current Latitude:" , location.getLatitude()+"");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}