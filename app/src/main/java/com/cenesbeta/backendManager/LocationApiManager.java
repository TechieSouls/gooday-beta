package com.cenesbeta.backendManager;

import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.JsonParsing;
import com.cenesbeta.api.LocationAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 4/1/19.
 */

public class LocationApiManager {

    CenesApplication cenesApplication;

    public LocationApiManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
    }

    public List<Location> fetchRecentEvents(String params, String authToken) {

        try {
            String url = UrlManagerImpl.prodAPIUrl+ LocationAPI.get_recentLocations+"?"+params;
            System.out.println("Loaction API : "+url);
            JsonParsing jsonParsing = new JsonParsing();
            JSONArray locationArray = jsonParsing.httpGet(url, authToken);

            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<Location>>(){}.getType();
            return gson.fromJson( locationArray.toString(), listType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Location>();
    }

    public JSONObject nearByLocationSearch(String queryString) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = LocationAPI.get_googleNearByLocations+"&"+queryString;
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject worldWideLocationSearch(String queryString) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = LocationAPI.get_googleWorldWideLocations+"&"+queryString;
            return jsonParsing.httpGetJsonObject(apiUrl,null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
