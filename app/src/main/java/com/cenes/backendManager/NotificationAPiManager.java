package com.cenes.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.JsonParsing;
import com.cenes.api.NotificationAPI;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;

import org.json.JSONObject;

/**
 * Created by mandeep on 5/1/19.
 */

public class NotificationAPiManager {

    CenesApplication cenesApplication;

    public NotificationAPiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject getNotificationCounts(String queryStr, String authToken) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+ NotificationAPI.get_notificationCounts+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getUserNotifications(String queryStr, String authToken) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+ NotificationAPI.get_notification_by_user+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject markNotificationAsRead(String queryStr, String authToken) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+ NotificationAPI.get_mark_notification_as_read+"?"+queryStr;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
