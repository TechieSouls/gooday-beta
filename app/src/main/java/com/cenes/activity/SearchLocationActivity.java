package com.cenes.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.cenes.Manager.ApiManager;
import com.cenes.R;
import com.cenes.adapter.SearchLocationAdapter;
import com.cenes.application.CenesApplication;
import com.cenes.coremanager.CoreManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Timer;

/**
 * Created by mandeep on 6/9/17.
 */

public class SearchLocationActivity extends CenesActivity {

    private ImageView closeSearchLocationBtn;
    private ListView gathSearchLocationListView;
    private EditText locationSearchEditText;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private ApiManager apiManager;

    private SearchLocationAdapter searchLocationAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);
        initializeComponents();
        addClickListeners();

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
                                new SearchLocationTask().execute(editable.toString());
                            /*}
                        },
                        DELAY
                );*/
            }
        });
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
            }
        }
    };

    public void initializeComponents() {
        closeSearchLocationBtn = (ImageView) findViewById(R.id.close_search_location_btn);
        gathSearchLocationListView = (ListView) findViewById(R.id.gath_search_location_list_view);
        locationSearchEditText = (EditText) findViewById(R.id.search_location_edit_text);

        cenesApplication = getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        apiManager = coreManager.getApiManager();

    }

    public void addClickListeners() {
        closeSearchLocationBtn.setOnClickListener(onClickListener);
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

}