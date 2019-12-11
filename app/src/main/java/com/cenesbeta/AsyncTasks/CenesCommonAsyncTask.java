package com.cenesbeta.AsyncTasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.api.CenesCommonAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.CenesCommonAPIManager;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import org.json.JSONObject;

import java.util.Map;

public class CenesCommonAsyncTask {

    private static CenesApplication cenesApplication;
    private static Activity activity;

    public CenesCommonAsyncTask(CenesApplication cenesApplication, Activity activity) {
        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }

    public static class NotificationBadgeCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public NotificationBadgeCountTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... strings) {

            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();
            CenesCommonAPIManager cenesCommonAPIManager = coreManager.getCenesCommonAPIManager();
            int userId = 0;
            String token = "";

            if (user != null) {
                if (user.getUserId() != null) {
                    userId = user.getUserId();
                }
                if (user.getAuthToken() != null) {
                    token = user.getAuthToken();
                }
            }

            String queryStr = "userId="+userId;
            return  cenesCommonAPIManager.getBadgeCounts(queryStr, token);
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);

            delegate.processFinish(stringObjectMap);
        }
    }


    public static class NotificationUpdateBadgeCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public NotificationUpdateBadgeCountTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(JSONObject... strings) {

            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();
            CenesCommonAPIManager cenesCommonAPIManager = coreManager.getCenesCommonAPIManager();

            String queryStr = "userId="+user.getUserId();
            return  cenesCommonAPIManager.updateBadgeCountsToZero(queryStr, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject stringObjectMap) {
            super.onPostExecute(stringObjectMap);

            delegate.processFinish(stringObjectMap);
        }
    }

}
