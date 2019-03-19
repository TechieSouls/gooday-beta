package com.cenes.backendManager;

import android.support.v7.app.AppCompatActivity;

import com.cenes.Manager.Impl.UrlManagerImpl;
import com.cenes.Manager.JsonParsing;
import com.cenes.api.GatheringAPI;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public JSONObject createGathering(String authToken, JSONObject eventObj){

        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+GatheringAPI.post_create_gathering;
            JsonParsing jsonParsing = new JsonParsing();
            return jsonParsing.httpPost(apiUrl,eventObj,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getGatheringData(String authToken, Long eventId) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_gathering_data+eventId;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject uploadEventPhoto(String queryStr,String authToken, File file) {
        try {
            String apiUrl = UrlManagerImpl.prodAPIUrl+GatheringAPI.post_upload_image;
            JsonParsing jsonParsing = new JsonParsing();
            Map<String, String> formFields = new HashMap<>();
            for (String queryStrArray : queryStr.split("&")) {
                formFields.put(queryStrArray.split("=")[0], queryStrArray.split("=")[1]);
            }
            return jsonParsing.httpPostMultipartWithFormData(apiUrl,formFields, file, authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject updateGatheringStatus(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_update_invitation_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject deleteGatheringByUserId(String queryStr, String authToken) {
        try {
            JsonParsing jsonParsing = new JsonParsing();
            String apiUrl = UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_delete_event_api+"?"+queryStr;
            return jsonParsing.httpGetJsonObject(apiUrl,authToken);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
