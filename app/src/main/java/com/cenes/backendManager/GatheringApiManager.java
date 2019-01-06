package com.deploy.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.GatheringAPI;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;

import org.json.JSONObject;

/**
 * Created by mandeep on 5/1/19.
 */

public class GatheringApiManager {

    CenesApplication cenesApplication;

    public GatheringApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject getUserGatherings(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_gatherings+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
