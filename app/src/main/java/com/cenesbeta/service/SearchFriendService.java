package com.cenesbeta.service;

import com.cenesbeta.bo.EventMember;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<String> allAphabets() {

        String alphabets[] ={"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

        List<String> alphabetsList = Arrays.asList(alphabets);

        return  alphabetsList;
    }

    public static List<EventMember> getCenesContacts (List<EventMember> allContacts) {

        List<EventMember> cenesContacts = new ArrayList<>();
        List<Integer> userContactIDTracking = new ArrayList<>();


        for (EventMember eventMember: allContacts) {
            try {

                if ("yes".equals(eventMember.getCenesMember())) {
                    if (userContactIDTracking.contains(eventMember.getUserContactId())) {
                        continue;
                    }
                    cenesContacts.add(eventMember);
                    userContactIDTracking.add(eventMember.getUserContactId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cenesContacts;
    }

}
