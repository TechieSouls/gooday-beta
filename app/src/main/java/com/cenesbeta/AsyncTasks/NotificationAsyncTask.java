package com.cenesbeta.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.NotificationAPiManager;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;

import org.json.JSONObject;

public class NotificationAsyncTask {

    private static CenesApplication cenesApplication;
    private static Activity activity;

    public NotificationAsyncTask(CenesApplication cenesApplication, Activity activity) {
        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }

    public static class  NotificationListTask extends AsyncTask<String, String, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;
        ProgressDialog progressDialog;

        public NotificationListTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            NotificationAPiManager notificationAPiManager = coreManager.getNotificationAPiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();
            String queryStr = "userId=" + user.getUserId();
            JSONObject response = notificationAPiManager.getUserNotifications(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            delegate.processFinish(response);
        }

    }

    public static class MarkNotificationReadTask extends AsyncTask<Long, String, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public MarkNotificationReadTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {

            Long notificationTypeId = longs[0];

            NotificationAPiManager notificationAPiManager = coreManager.getNotificationAPiManager();
            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            String queryStr = "userId=" + user.getUserId()+"&notificationTypeId="+notificationTypeId;
            JSONObject response = notificationAPiManager.markNotificationAsRead(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            delegate.processFinish(response);
        }
    }

}
