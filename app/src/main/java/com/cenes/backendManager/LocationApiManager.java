package com.deploy.backendManager;

import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.JsonParsing;
import com.deploy.api.LocationAPI;
import com.deploy.application.CenesApplication;
import com.deploy.bo.Location;
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
        String url = UrlManagerImpl.prodAPIUrl+ LocationAPI.get_recentLocations+"?"+params;
        JsonParsing jsonParsing = new JsonParsing();
        JSONArray locationArray = jsonParsing.httpGet(url, authToken);

        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Location>>(){}.getType();
        return gson.fromJson( locationArray.toString(), listType);
    }
}
