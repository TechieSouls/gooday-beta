package com.cenesbeta.AsyncTasks;

import android.os.AsyncTask;

import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.LocationApiManager;
import com.cenesbeta.bo.Location;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by mandeep on 4/1/19.
 */

public class LocationAsyncTask {

    private static CenesApplication cenesApplication;
    public LocationAsyncTask(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
    }

    public static class RecentLocationTask extends AsyncTask<Long, List<Location>, List<Location>> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(List<Location> locations);
        }
        public AsyncResponse delegate = null;

        public RecentLocationTask(AsyncResponse delegate){
            this.delegate = delegate;
        }


        @Override
        protected List<Location> doInBackground(Long... longs) {

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            LocationApiManager locationApiManager = coreManager.getLocationApiManager();

            int userId = userManager.getUser().getUserId();
            String params = "userId="+userId;
            return locationApiManager.fetchRecentEvents(params, userManager.getUser().getAuthToken());
        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            super.onPostExecute(locations);
            delegate.processFinish(locations);
        }
    }

    public static class FetchLatLngTask extends AsyncTask<String, JSONObject, JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject locations);
        }
        public AsyncResponse delegate = null;

        public FetchLatLngTask(AsyncResponse delegate){
            this.delegate = delegate;
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String keyword = strings[0];
            //locationPhotoUrl = "";
            try {
                String queryStr = "&placeid=" + URLEncoder.encode(keyword);
                CoreManager coreManager = cenesApplication.getCoreManager();
                ApiManager apiManager = coreManager.getApiManager();

                JSONObject job = apiManager.locationLatLng(queryStr);
                System.out.println("Selected Location Object : "+job.toString());

                return job;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            delegate.processFinish(jsonObject);

        }
    }

    public static class SearchGoogleLocationTask extends AsyncTask<String,JSONArray,JSONArray> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONArray jsonArray);
        }
        public AsyncResponse delegate = null;

        public SearchGoogleLocationTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            String keyword = strings[0];
            try {
                String queryStr = "&input="+ URLEncoder.encode(keyword);
                CoreManager coreManager = cenesApplication.getCoreManager();
                ApiManager apiManager = coreManager.getApiManager();
                JSONObject job = apiManager.locationSearch(queryStr);
                return job.getJSONArray("predictions");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            delegate.processFinish(jsonArray);
        }
    }


    public static class SearchNearByLocationTask extends AsyncTask<String,JSONObject,JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject jsonObject);
        }
        public AsyncResponse delegate = null;

        public SearchNearByLocationTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String queryStr = strings[0];
            try {
                CoreManager coreManager = cenesApplication.getCoreManager();
                LocationApiManager locationApiManager = coreManager.getLocationApiManager();
                JSONObject jsonObject = locationApiManager.nearByLocationSearch(queryStr);
                return  jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            delegate.processFinish(jsonObject);
        }
    }


    public static class SearchWorldWideLocationTask extends AsyncTask<String,JSONObject,JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject jsonObject);
        }
        public AsyncResponse delegate = null;

        public SearchWorldWideLocationTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String queryStr = strings[0];
            try {
                CoreManager coreManager = cenesApplication.getCoreManager();
                LocationApiManager locationApiManager = coreManager.getLocationApiManager();
                JSONObject jsonObject = locationApiManager.worldWideLocationSearch(queryStr);
                return  jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            delegate.processFinish(jsonObject);
        }
    }


}
