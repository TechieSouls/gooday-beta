package com.cenes.backendManager;

import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.JsonParsing;
import com.cenes.api.CenesCommonAPI;
import com.cenes.application.CenesApplication;

import org.json.JSONObject;

public class CenesCommonAPIManager {

    CenesApplication cenesApplication;

    public CenesCommonAPIManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public JSONObject getBadgeCounts(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ CenesCommonAPI.get_badge_counts_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
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
