package com.deploy.AsyncTasks;

import android.os.AsyncTask;

import com.deploy.application.CenesApplication;
import com.deploy.backendManager.GatheringApiManager;
import com.deploy.backendManager.NotificationAPiManager;
import com.deploy.bo.Event;
import com.deploy.bo.EventMember;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.util.CenesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 5/1/19.
 */

public class GatheringAsyncTask {

    private static CenesApplication cenesApplication;

    public GatheringAsyncTask(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
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
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d\nEEE");

                            if (eventObj.has("eventId")) {
                                event.setEventId(eventObj.getLong("eventId"));
                            }
                            if (eventObj.has("title")) {
                                event.setTitle(eventObj.getString("title"));
                            }
                            if (eventObj.has("event_picture")) {
                                event.setEventPicture(eventObj.getString("event_picture"));
                            }
                            if (eventObj.has("location") && eventObj.getString("location") != "null") {
                                event.setLocation(eventObj.getString("location"));
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
                                event.setEventMemberId(eventObj.getLong("event_member_id"));
                            }

                            if (eventObj.has("eventMembers")) {
                                JSONArray membersArray = eventObj.getJSONArray("eventMembers");
                                List<EventMember> members = new ArrayList<>();
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
                                    members.add(eventMember);
                                }
                                event.setEventMembers(members);
                            }
                        }

                        boolean isInvitation = false;
                        if (status.equalsIgnoreCase("pending")) {
                            isInvitation = true;
                        }

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
}
