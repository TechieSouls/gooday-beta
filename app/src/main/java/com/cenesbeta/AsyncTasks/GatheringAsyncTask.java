package com.cenesbeta.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GatheringScreenActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.GatheringApiManager;
import com.cenesbeta.backendManager.HomeScreenApiManager;
import com.cenesbeta.backendManager.NotificationAPiManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 5/1/19.
 */

public class GatheringAsyncTask {

    private static CenesApplication cenesApplication;
    private static CenesBaseActivity activity;
    public GatheringAsyncTask(CenesApplication cenesApplication, CenesBaseActivity activity) {
        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }

    public static class GatheringsTask extends AsyncTask<String, String, JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public GatheringsTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            if (!isCancelled()) {
                String status = strings[0];

                CoreManager coreManager = cenesApplication.getCoreManager();
                UserManager userManager = coreManager.getUserManager();
                GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();

                User user = userManager.getUser();

                String queryStr = "user_id=" + user.getUserId() + "&status=" + status;
                return gatheringApiManager.getUserGatherings(queryStr, user.getAuthToken());

            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject dataExists) {
            super.onPostExecute(dataExists);
            delegate.processFinish(dataExists);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }


    public static class NotificationCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public NotificationCountTask(AsyncResponse delegate){
            this.delegate = delegate;
        }
        @Override
        protected JSONObject doInBackground(JSONObject... strings) {

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            NotificationAPiManager notificationAPiManager = coreManager.getNotificationAPiManager();
            User user = userManager.getUser();

            String queryStr = "userId="+user.getUserId();
            return notificationAPiManager.getNotificationCounts(queryStr, user.getAuthToken());
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            delegate.processFinish(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancel(true);
        }
    }

    public static class CreateGatheringTask extends AsyncTask<JSONObject, Object, JSONObject> {


        //ProgressDialog processDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public CreateGatheringTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            /*processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Creating..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();*/
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjList) {
            JSONObject jsonObject = jsonObjList[0];

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();

            User user = userManager.getUser();
            JSONObject job = gatheringApiManager.createGathering(user.getAuthToken(), jsonObject);
            return job;
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            if (obj != null) {
                Log.e("Gathering Obj ", obj.toString());
            }
            /*if (processDialog != null) {
                processDialog.dismiss();
                processDialog = null;
            }*/
            delegate.processFinish(obj);
        }
    }

    public static class EventInfoTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog eventInfoDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public EventInfoTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventInfoDialog = new ProgressDialog(activity);
            eventInfoDialog.setMessage("Loading");
            eventInfoDialog.setIndeterminate(false);
            eventInfoDialog.setCanceledOnTouchOutside(false);
            eventInfoDialog.setCancelable(false);
            eventInfoDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... eventIds) {
            Long evventId = eventIds[0];

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();

            User user = userManager.getUser();

            JSONObject gatheringObj = gatheringApiManager.getGatheringData(user.getAuthToken(), evventId);
            return gatheringObj;
        }

        @Override
        protected void onPostExecute(JSONObject responseObj) {
            super.onPostExecute(responseObj);
            if (eventInfoDialog != null) {
                eventInfoDialog.dismiss();
                eventInfoDialog = null;
            }
            delegate.processFinish(responseObj);
        }
    }

    public static class UploadImageTask extends AsyncTask<Map<String, Object>, Object, JSONObject> {
        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog uploadDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public UploadImageTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uploadDialog = new ProgressDialog(activity);
            uploadDialog.setMessage("Uploading..");
            uploadDialog.setIndeterminate(false);
            uploadDialog.setCanceledOnTouchOutside(false);
            uploadDialog.setCancelable(false);
            uploadDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Map<String, Object>... mapObjects) {

            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            Map<String, Object> mapPostObject = mapObjects[0];
            try {
                String queryStr = "eventId="+(Long)mapPostObject.get("eventId");
                File file = (File)mapPostObject.get("file");
                JSONObject response = gatheringApiManager.uploadEventPhoto(queryStr, user.getAuthToken(), file);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (uploadDialog != null) {
                uploadDialog.dismiss();
                uploadDialog = null;
            }
            delegate.processFinish(response);
        }
    }


    public static class UploadOnlyImageTask extends AsyncTask<File, JSONObject, JSONObject> {
        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public UploadOnlyImageTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(File... files) {

            UserManager userManager = coreManager.getUserManager();
            User user = userManager.getUser();

            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            File filesToUpload = files[0];
            try {
                JSONObject response = gatheringApiManager.uploadOnlyPhoto(user.getAuthToken(), filesToUpload);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            delegate.processFinish(response);
        }
    }

    public static class UpdateStatusActionTask extends AsyncTask<String, Void, Boolean> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        //ProgressDialog mProgressDialog;

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(Boolean response);
        }

        public AsyncResponse delegate = null;

        public UpdateStatusActionTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }



        @Override
        protected void onPreExecute() {
            /*mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();*/
        }

        @Override
        protected Boolean doInBackground(String... queryStrs) {
            String queryStr = "";
            if (queryStrs != null && queryStrs.length != 0) {
                queryStr = queryStrs[0];
            }

            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            User user = userManager.getUser();
            JSONObject jObj = gatheringApiManager.updateGatheringStatus(queryStr, user.getAuthToken());

            try {
                System.out.println("blah: response: " + jObj.toString());
                return jObj.getBoolean("success");
            } catch (Exception e) {

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            /*if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }*/
            delegate.processFinish(success);
        }
    }

    public static class DeleteGatheringTask extends AsyncTask<Long, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public DeleteGatheringTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        ProgressDialog deleteGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deleteGathDialog = new ProgressDialog(activity);
            deleteGathDialog.setMessage("Deleting..");
            deleteGathDialog.setIndeterminate(false);
            deleteGathDialog.setCanceledOnTouchOutside(false);
            deleteGathDialog.setCancelable(false);
            deleteGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            User user = userManager.getUser();

            Long eventId = longs[0];
            String queryStr = "event_id=" + eventId;
            JSONObject response = gatheringApiManager.deleteGatheringByUserId(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (deleteGathDialog != null) {
                deleteGathDialog.dismiss();
                deleteGathDialog = null;
            }
            delegate.processFinish(response);
        }
    }

    public static class PredictiveCalendarTask extends AsyncTask<String, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public PredictiveCalendarTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        ProgressDialog predictiveGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            predictiveGathDialog = new ProgressDialog(activity);
            predictiveGathDialog.setMessage("Loading...");
            predictiveGathDialog.setIndeterminate(false);
            predictiveGathDialog.setCanceledOnTouchOutside(false);
            predictiveGathDialog.setCancelable(false);
            predictiveGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... queryStrs) {
            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            User user = userManager.getUser();

            String queryStr = queryStrs[0];
            JSONObject response = gatheringApiManager.predictiveDataUserId(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (predictiveGathDialog != null) {
                predictiveGathDialog.dismiss();
                predictiveGathDialog = null;
            }
            delegate.processFinish(response);
        }
    }

    public static class HomeEventsTask extends AsyncTask<String, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public HomeEventsTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... queryStrs) {
            UserManager userManager = coreManager.getUserManager();
            HomeScreenApiManager homeScreenApiManager = coreManager.getHomeScreenApiManager();
            User user = userManager.getUser();

            String queryStr = queryStrs[0];
            JSONObject response = homeScreenApiManager.getHomescreenEvents(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            delegate.processFinish(response);
        }
    }

    public static class GatheringByKeyTask extends AsyncTask<String, JSONObject, JSONObject> {


        private CoreManager coreManager = cenesApplication.getCoreManager();

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }

        public AsyncResponse delegate = null;

        public GatheringByKeyTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... queryStrs) {

            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();
            User user = userManager.getUser();

            String queryStr = queryStrs[0];
            JSONObject response =  gatheringApiManager.getGatheringByPrivateKet(queryStr, user.getAuthToken());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            delegate.processFinish(response);
        }
    }

}
