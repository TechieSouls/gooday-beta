package com.deploy.service;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 27/12/18.
 */

public class SearchFriendService {

    public static List<Map<String, String>> getListOfMapFromJsonList(Collection<JSONObject> jsonObjects) {
        List<Map<String, String>> mapList = new ArrayList<>();
        try {
            for (JSONObject jsonObject: jsonObjects) {
                Iterator<String> jsonKeys = jsonObject.keys();
                Map<String, String> response = new HashMap<>();
                while (jsonKeys.hasNext()) {
                    String key = jsonKeys.next();
                    response.put(key, jsonObject.getString(key)) ;
                }
                mapList.add(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  mapList;
    }
}
