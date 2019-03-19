package com.cenes.AsyncTasks;

import android.os.AsyncTask;

import com.cenes.application.CenesApplication;
import com.cenes.backendManager.LocationApiManager;
import com.cenes.bo.Location;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;

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
}
