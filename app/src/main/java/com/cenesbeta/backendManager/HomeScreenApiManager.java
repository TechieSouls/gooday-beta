package com.cenesbeta.backendManager;

import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.JsonParsing;
import com.cenesbeta.api.HomeScreenAPI;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.application.CenesApplication;

import org.json.JSONObject;

/**
 * Created by mandeep on 25/10/18.
 */

public class HomeScreenApiManager {

    CenesApplication cenesApplication;

    public HomeScreenApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject refreshGoogleEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_refreshGoogleEvents+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }

    public JSONObject refreshOutlookEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_refreshGOutlookEvents+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }


    public JSONObject getHomescreenEvents(String params, String authToken) {
        String url = UrlManagerImpl.prodAPIUrl+ HomeScreenAPI.get_homescreen_events+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        return jsonParsing.httpGetJsonObject(url, authToken);
    }

}
