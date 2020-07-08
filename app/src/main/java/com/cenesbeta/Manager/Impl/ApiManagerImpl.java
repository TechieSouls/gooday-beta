package com.cenesbeta.Manager.Impl;

import android.util.Log;

import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.JsonParsing;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by puneet on 15/8/17.
 */

public class ApiManagerImpl implements ApiManager {

    CenesApplication cenesApplication;


    public String SIGNUPURL= "/api/users/";
    public String IMAGE_UPLOAD_URL = "/api/profile/upload";
    public String DIARY_UPLOAD_URL = "/api/diary/upload";
    public String GOOGLE_EVENTS_URL = "/api/google/events";
    public String FACEBOOK_EVENTS_URL = "/api/facebook/events";
    public String OUTLOOK_EVENTS_URL = "/api/outlook/events";
    public String USER_PREDICTIVE_URL = "/api/predictive/calendar";
    public String GOOGLE_LOCATION_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";
    public String GOOGLE_PLACE_DETAILS_API = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";
    public String SEARCH_FRIEND_API = "/api/user/phonefriends";
    public String HOLIDAY_SYNC_API = "/api/holiday/calendar/events";
    public String REMINDER_SAVE_API = "/api/reminder/save";
    public String USER_REMINDERS_API = "/api/reminder/list";
    public String USER_HOLIDAYS_API = "/api/events/holidays";
    public String GET_EVENT_BY_ID_API = "/api/event/";
    public String REMINDER_UPDATE_FINISH_API = "/api/reminder/updateToFinish";
    public String REMINDER_ACCEPT_DECLINE_API = "/api/reminder/updateReminderMemberStatus";
    public String DELETE_EVENT_API = "/api/event/delete";
    public String UPDATE_INVITATION_API = "/api/event/update";
    public String FETCH_REMINDER_API = "/api/event/fetch";
    public String DELETE_REMINDER_API = "/api/reminder/delete";
    public String ADD_DIARY_API = "/api/diary/save";
    public String GET_ALL_DIARIES_API = "/api/diary/list";
    public String DELETE_DIARY_API = "/api/diary/delete";
    public String USER_SYNC_STATUS_API = "/api/user/calendarsyncstatus/";
    public String USER_LOGOUT_API = "/api/user/logout";
    public String NOTIFICATION_COUNTS_API = "/api/notification/unreadbyuser";
    public String SYNC_PHONE_CONTACTS = "/api/syncContacts";
    public ApiManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    @Override
    public JSONObject signUpByEmail(User user, AppCompatActivity appCompatActivity){

        //This will be null in case of signup request
        String authToken = null;

        JSONObject postData = new JSONObject();
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+SIGNUPURL,postData,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject postMultipart(User user, File file, AppCompatActivity appCompatActivity){
        JSONObject postData = new JSONObject();
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPostMultipart(UrlManagerImpl.prodAPIUrl+IMAGE_UPLOAD_URL,user,file);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray googleEvents(User user, String queryStr, AppCompatActivity activity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+GOOGLE_EVENTS_URL+queryStr;
            return jsonParsing.httpGet(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray syncFacebookEvents(User user,AppCompatActivity appCompatActivity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+FACEBOOK_EVENTS_URL+"/"+user.getFacebookId()+"/"+user.getFacebookAuthToken();
            Log.e("Facebook events ",apiUrl);
            return jsonParsing.httpGet(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject locationSearch(String queryString) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = GOOGLE_LOCATION_API+queryString;
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject locationLatLng(String queryString) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = GOOGLE_PLACE_DETAILS_API + queryString;
            return jsonParsing.httpGetJsonObject(apiUrl, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray searchFriends(User user,String queryString,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+SEARCH_FRIEND_API+queryString;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGet(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray syncOutlookEvents(User user,String queryStr,AppCompatActivity activity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+OUTLOOK_EVENTS_URL+queryStr;
            return jsonParsing.httpGet(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject saveReminder(User user,JSONObject jsonObject,AppCompatActivity activity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+REMINDER_SAVE_API;
            return jsonParsing.httpPost(apiUrl,jsonObject,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getReminders(User user,String  queryStr,AppCompatActivity activity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+USER_REMINDERS_API+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getUserHolidays(User user,String  queryStr,AppCompatActivity activity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+USER_HOLIDAYS_API+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getEventById(User user,Long eventId,AppCompatActivity appCompatActivity) {

        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+GET_EVENT_BY_ID_API+eventId;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject updateReminderToFinish(User user,String queryStr,AppCompatActivity appCompatActivity) {

        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+REMINDER_UPDATE_FINISH_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject deleteEventById(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+DELETE_EVENT_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject updateInvitation(User user,String queryString, AppCompatActivity appCompatActivity) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+UPDATE_INVITATION_API+queryString;
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject deleteReminderById(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+DELETE_REMINDER_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject addDiary(User user,JSONObject postDataJson,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+ADD_DIARY_API;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(apiUrl,postDataJson,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getAllUserDiaries(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+GET_ALL_DIARIES_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject postDiaryMedia(User user, File file, AppCompatActivity appCompatActivity){
        JSONObject postData = new JSONObject();
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPostMultipart(UrlManagerImpl.prodAPIUrl+DIARY_UPLOAD_URL,user,file);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject deleteDiaryById(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+DELETE_DIARY_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getUserCalendarSyncStatus(User user,int userId,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+USER_SYNC_STATUS_API+userId;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject updateReminderInvitation(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+REMINDER_ACCEPT_DECLINE_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject logout(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+USER_LOGOUT_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getNotificationCounts(User user,String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+NOTIFICATION_COUNTS_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject markNotificationAsReadByUserIdAndNotificatonId(User user, String queryStr,AppCompatActivity appCompatActivity) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+NOTIFICATION_COUNTS_API+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject syncDeviceConcats(User user, JSONObject postData,AppCompatActivity appCompatActivity){
        try {
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(UrlManagerImpl.prodAPIUrl+SYNC_PHONE_CONTACTS,postData,user.getAuthToken());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
