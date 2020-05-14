package com.cenesbeta.AsyncTasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.MeTimeApiManager;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.Location;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.service.MeTimeService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 10/1/19.
 */

public class MeTimeAsyncTask {

    private static CenesApplication cenesApplication;
    private static CenesBaseActivity activity;

    public MeTimeAsyncTask (CenesApplication cenesApplication, CenesBaseActivity activity) {

        this.cenesApplication = cenesApplication;
        this.activity = activity;
    }


    public static class MeTimePosting extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        private MeTimeService meTimeService;
        private CoreManager coreManager = cenesApplication.getCoreManager();

        //ProgressDialog processDialog;

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public MeTimePosting(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            meTimeService = new MeTimeService();
            /*processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Processing..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();*/

        }

        @Override
        protected JSONObject doInBackground(JSONObject... lists) {

            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();

            Calendar mcurrentTime = Calendar.getInstance();
            JSONObject meTimeData = lists[0];

            try {
                Log.d("MeTimeRequest", meTimeData.toString());
                User user = userManager.getUser();
                meTimeData.put("userId", user.getUserId());
                meTimeData.put("timezone", mcurrentTime.getTimeZone().getID());

                JSONObject responseObj = meTimeApiManager.saveMeTime(user, meTimeData);
                Log.e("Me Time response ", responseObj.toString());
                return responseObj;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject metimeResponse) {
            super.onPostExecute(metimeResponse);
            /*if (processDialog.isShowing()) {
                processDialog.dismiss();
            }
            processDialog = null;*/
            delegate.processFinish(metimeResponse);
        }
    }

    public static class GetMeTimeDataTask extends AsyncTask<String, JSONObject, JSONObject> {

        CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public GetMeTimeDataTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(String... lists) {


            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();


            User user = userManager.getUser();
            String queryStr = "userId=" + user.getUserId();
            try {
                JSONObject response = meTimeApiManager.getUserMeTimeData(queryStr, user.getAuthToken());
                System.out.println(response.toString());
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

    public static class DeleteMeTimeDataTask extends AsyncTask<Long, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public DeleteMeTimeDataTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {

            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();


            User user = userManager.getUser();
            Long recurringEventId = longs[0];
            String queryStr = "recurringEventId=" + recurringEventId;
            try {
                JSONObject response = meTimeApiManager.deleteMeTimeData(queryStr, user.getAuthToken());
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

    public static class UploadPhotoTask extends AsyncTask<Map<String, Object>, JSONObject, JSONObject> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        public interface AsyncResponse {
            void processFinish(JSONObject response);
        }
        public AsyncResponse delegate = null;

        public UploadPhotoTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected JSONObject doInBackground(Map<String, Object>... mapObjects) {
            UserManager userManager = coreManager.getUserManager();
            MeTimeApiManager meTimeApiManager= coreManager.getMeTimeApiManager();

            User user = userManager.getUser();
            Map<String, Object> mapPostObject = mapObjects[0];
            try {
                String queryStr = "recurringEventId="+(Long)mapPostObject.get("recurringEventId");
                File file = (File)mapPostObject.get("file");
                JSONObject response = meTimeApiManager.uploadMeTimePhoto(queryStr, user.getAuthToken(), file);
                return response;
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
