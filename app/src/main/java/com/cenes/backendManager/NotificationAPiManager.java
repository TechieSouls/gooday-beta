package com.deploy.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.NotificationAPI;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;

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
}
