package com.cenesbeta.dto;

import com.cenesbeta.bo.Event;

import java.util.List;
import java.util.Map;

public class HomeScreenDto {

    public enum HomeScreenAPICall{Home,Accepted,Pending,Declined};

    private boolean isCalendarOpen;
    private Map<String, List<Event>> homeDataListMap;
    private List<String> homeDataHeaders;
    private HomeScreenAPICall homeScreenAPICall;


    public List<String> getHomeDataHeaders() {
        return homeDataHeaders;
    }

    public void setHomeDataHeaders(List<String> homeDataHeaders) {
        this.homeDataHeaders = homeDataHeaders;
    }

    public Map<String, List<Event>> getHomeDataListMap() {
        return homeDataListMap;
    }

    public void setHomeDataListMap(Map<String, List<Event>> homeDataListMap) {
        this.homeDataListMap = homeDataListMap;
    }

    public boolean isCalendarOpen() {
        return isCalendarOpen;
    }

    public void setCalendarOpen(boolean calendarOpen) {
        isCalendarOpen = calendarOpen;
    }

    public HomeScreenAPICall getHomeScreenAPICall() {
        return homeScreenAPICall;
    }

    public void setHomeScreenAPICall(HomeScreenAPICall homeScreenAPICall) {
        this.homeScreenAPICall = homeScreenAPICall;
    }
}
