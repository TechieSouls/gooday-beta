package com.cenesbeta.backendManager;

import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.JsonParsing;
import com.cenesbeta.api.CenesCommonAPI;
import com.cenesbeta.application.CenesApplication;

import org.json.JSONObject;

public class CenesCommonAPIManager {

    CenesApplication cenesApplication;

    public CenesCommonAPIManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject updateBadgeCountsToZero(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ CenesCommonAPI.update_badge_counts_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
