package com.cenesbeta.AsyncTasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.GatheringApiManager;
import com.cenesbeta.backendManager.NotificationAPiManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
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

    public static class GatheringsTask extends AsyncTask<String, String, Map<String, Object>> {

        // you may separate this or combined to caller class.
        public interface AsyncResponse {
            void processFinish(Map<String, Object> response);
        }
        public AsyncResponse delegate = null;

        public GatheringsTask(AsyncResponse delegate){
            this.delegate = delegate;
        }

        @Override
        protected Map<String, Object> doInBackground(String... strings) {

            if (!isCancelled()) {
                String status = strings[0];

                CoreManager coreManager = cenesApplication.getCoreManager();
                UserManager userManager = coreManager.getUserManager();
                GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();

                User user = userManager.getUser();

                String queryStr = "user_id=" + user.getUserId() + "&status=" + status;
                JSONObject gatheringObj = gatheringApiManager.getUserGatherings(queryStr, user.getAuthToken());

                Map<String, Object> responseMap =  null;
                try {
                    if (gatheringObj.getJSONArray("data") == null || gatheringObj.getJSONArray("data").length() == 0) {
                        responseMap = null;
                        //homeNoEvents.setVisibility(View.VISIBLE);
                        //homeNoEvents.setText("No Event Exists For This Date");
                    } else {

                        JSONArray gatherings = gatheringObj.getJSONArray("data");
                        List<String> headers = new ArrayList<>();
                        Map<String, List<Event>> eventMap = new HashMap<>();
                        List<Event> events = new ArrayList<>();

                        for (int i = 0; i < gatherings.length(); i++) {

                            Event event = new Event();
                            JSONObject eventObj = (JSONObject) gatherings.getJSONObject(i);

                            SimpleDateFormat weekCategory = new SimpleDateFormat("EEEE");
                            SimpleDateFormat calCategory = new SimpleDateFormat("ddMMM");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d\nEEE");

                            if (eventObj.has("eventId")) {
                                event.setEventId(eventObj.getLong("eventId"));
                            }
                            if (eventObj.has("title")) {
                                event.setTitle(eventObj.getString("title"));
                            }
                            if (eventObj.has("createdById")) {
                                event.setCreatedById(eventObj.getLong("createdById"));
                            }
                            if (eventObj.has("event_picture")) {
                                event.setEventPicture(eventObj.getString("event_picture"));
                            }
                            if (eventObj.has("location") && eventObj.getString("location") != "null") {
                                event.setLocation(eventObj.getString("location"));
                            }
                            if (eventObj.has("expired") && eventObj.getString("expired") != null) {
                                event.setExpired(eventObj.getBoolean("expired"));
                            }
                            if (eventObj.has("startTime")) {
                                Date startDate = new Date(eventObj.getLong("startTime"));
                                event.setStartTime(timeFormat.format(startDate));
                                event.setEventDate(dateFormat.format(startDate));
                            }
                            if (eventObj.has("startTime")) {
                                Date startDate = new Date(eventObj.getLong("startTime"));
                                String dateKey = calCategory.format(startDate).toUpperCase() + "<b>"+weekCategory.format(startDate).toUpperCase()+"</b>";
                                //String dateKey = weekCategory.format(startDate) + "<b>"+calCategory.format(startDate)+"</b>" + CenesUtils.getDateSuffix(startDate.getDate());
                                /*if (CenesUtils.yyyyMMdd.format(startDate).equals(CenesUtils.yyyyMMdd.format(new Date()))) {
                                    dateKey = "TODAY " + dateKey;
                                }*/
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.DATE, 1);
                                /*if (CenesUtils.yyyyMMdd.format(startDate).equals(CenesUtils.yyyyMMdd.format(cal.getTime()))) {
                                    dateKey = "TOMORROW " + dateKey;
                                }*/
                                if (!headers.contains(dateKey)) {
                                    headers.add(dateKey);
                                }
                                if (eventMap.containsKey(dateKey)) {
                                    events = eventMap.get(dateKey);
                                } else {
                                    events = new ArrayList<>();
                                }
                                events.add(event);
                                eventMap.put(dateKey, events);
                            }
                            if (eventObj.has("sender")) {
                                event.setSender(eventObj.getString("sender"));
                            }
                            if (eventObj.has("event_member_id")) {
                                event.setCreatedById(eventObj.getLong("event_member_id"));
                            }

                            if (eventObj.has("eventMembers")) {
                                JSONArray membersArray = eventObj.getJSONArray("eventMembers");



                                /*List<EventMember> members = new ArrayList<>();
                                EventMember owner = null;
                                for (int idx = 0; idx < membersArray.length(); idx++) {
                                    JSONObject memberObj = (JSONObject) membersArray.get(idx);
                                    EventMember eventMember = new EventMember();
                                    if (memberObj.has("picture")) {
                                        eventMember.setPicture(memberObj.getString("picture"));
                                    }
                                    if (memberObj.has("name")) {
                                        eventMember.setName(memberObj.getString("name"));
                                    }
                                    if (memberObj.has("owner")) {
                                        eventMember.setOwner(memberObj.getBoolean("owner"));
                                    }
                                    if (memberObj.has("userId")) {
                                        eventMember.setUserId(memberObj.getLong("userId"));
                                    }


                                    if (event.getCreatedById() == eventMember.getUserId()) {
                                        owner = eventMember;
                                    }

                                    members.add(eventMember);
                                }*/

                                //System.out.println("Owner Found : "+owner);
                                //event.setOwner(owner);

                                Type listType = new TypeToken<List<EventMember>>() {}.getType();
                                List<EventMember> eventMembers = new Gson().fromJson(membersArray.toString(), listType);
                                event.setEventMembers(eventMembers);
                            }

                            if (user.getUserId() == event.getCreatedById()) {
                                event.setIsOwner(true);
                            }
                        }

                        boolean isInvitation = false;
                        if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("NotGoing")) {
                            isInvitation = true;
                        }

                        Collections.sort(headers);
                        responseMap = new HashMap<>();
                        responseMap.put("headers", headers);
                        responseMap.put("eventMap", eventMap);
                        responseMap.put("isInvitation", isInvitation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responseMap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Map<String, Object> dataExists) {
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


        ProgressDialog processDialog;

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
            processDialog = new ProgressDialog(activity);
            processDialog.setMessage("Creating..");
            processDialog.setIndeterminate(false);
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjList) {
            JSONObject jsonObject = jsonObjList[0];

            CoreManager coreManager = cenesApplication.getCoreManager();
            UserManager userManager = coreManager.getUserManager();
            GatheringApiManager gatheringApiManager = coreManager.getGatheringApiManager();

            User user = userManager.getUser();
            JSONObject job = gatheringApiManager.createGathering(user.getAuthToken(), jsonObject);
            //Log.e("Resp", job.toString());
            return job;
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            Log.e("Gathering Obj ", obj.toString());
            if (processDialog != null) {
                processDialog.dismiss();
                processDialog = null;
            }
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

    public static class UpdateStatusActionTask extends AsyncTask<String, Void, Boolean> {

        private CoreManager coreManager = cenesApplication.getCoreManager();

        ProgressDialog mProgressDialog;

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
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
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

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
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
}
