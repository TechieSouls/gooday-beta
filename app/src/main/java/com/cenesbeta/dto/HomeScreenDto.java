package com.cenesbeta.dto;

import com.cenesbeta.bo.Event;

import java.util.List;
import java.util.Map;

public class HomeScreenDto {

    public enum HomeScreenAPICall{Home,Accepted,Pending,Declined};
    public enum HomeTabs {Calendar,Invitation};
    public enum InvitationTabs {Accepted,Pending,Declined};

    private boolean isCalendarOpen;
    private Map<String, List<Event>> homeDataListMap;
    private List<String> homeDataHeaders;
    private HomeScreenAPICall homeScreenAPICall;
    private List<Event> homeEvents;
    private List<Event> acceptedEvents;
    private List<Event> pendingEvents;
    private List<Event> declinedEvents;
    private HomeTabs tabSelected = HomeTabs.Calendar;
    public static int calendarTabPageNumber = 0;
    public static int offsetToFetchData = 20;

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

    public List<Event> getAcceptedEvents() {
        return acceptedEvents;
    }

    public void setAcceptedEvents(List<Event> acceptedEvents) {
        this.acceptedEvents = acceptedEvents;
    }

    public List<Event> getPendingEvents() {
        return pendingEvents;
    }

    public void setPendingEvents(List<Event> pendingEvents) {
        this.pendingEvents = pendingEvents;
    }

    public List<Event> getDeclinedEvents() {
        return declinedEvents;
    }

    public void setDeclinedEvents(List<Event> declinedEvents) {
        this.declinedEvents = declinedEvents;
    }

    public List<Event> getHomeEvents() {
        return homeEvents;
    }

    public void setHomeEvents(List<Event> homeEvents) {
        this.homeEvents = homeEvents;
    }

    public HomeTabs getTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(HomeTabs tabSelected) {
        this.tabSelected = tabSelected;
    }
}
