package com.cenes.backendManager;

import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.JsonParsing;
import com.cenes.api.HomeScreenAPI;
import com.cenes.api.UserAPI;
import com.cenes.application.CenesApplication;

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
}
