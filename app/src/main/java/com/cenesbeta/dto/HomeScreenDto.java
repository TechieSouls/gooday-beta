package com.cenesbeta.dto;

import com.cenesbeta.bo.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenDto {

    public enum HomeScreenAPICall{PastEvents, Home,Accepted,Pending,Declined};
    public enum HomeTabs {Calendar,Invitation};
    public enum InvitationTabs {Accepted,Pending,Declined};

    private boolean isCalendarOpen;
    private Map<String, List<Event>> homeDataListMap;
    private List<String> homeDataHeaders;
    private HomeScreenAPICall homeScreenAPICall;
    private List<Event> homeEvents = new ArrayList<>();
    private List<Event> acceptedEvents;
    private List<Event> pendingEvents;
    private List<Event> declinedEvents;
    private List<Event> pastEvents = new ArrayList<>();
    private List<Object> homelistViewWithHeaders = new ArrayList<>();
    private List<Long> uniqueEventIdTracker = new ArrayList<>();

    private List<String> invitaitonDataHeaders = new ArrayList<>();
    private Map<String, List<Event>> invitationDataListMap = new HashMap<>();

    private HomeTabs tabSelected = HomeTabs.Calendar;
    private InvitationTabs invitationTabSelected = InvitationTabs.Accepted;

    public static int calendarTabPageNumber = 0;
    public static int offsetToFetchData = 20;
    public static int totalCalendarDataCounts = 0;
    public static int currentDateGroupPosition = 0;
    public static boolean madeApiCall = false;
    public static Map<String, Integer> homeListGroupAndMonthHolder = new HashMap<>();

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

    public List<Event> getPastEvents() {
        return pastEvents;
    }

    public void setPastEvents(List<Event> pastEvents) {
        this.pastEvents = pastEvents;
    }

    public List<String> getInvitaitonDataHeaders() {
        return invitaitonDataHeaders;
    }

    public void setInvitaitonDataHeaders(List<String> invitaitonDataHeaders) {
        this.invitaitonDataHeaders = invitaitonDataHeaders;
    }

    public Map<String, List<Event>> getInvitationDataListMap() {
        return invitationDataListMap;
    }

    public void setInvitationDataListMap(Map<String, List<Event>> invitationDataListMap) {
        this.invitationDataListMap = invitationDataListMap;
    }

    public InvitationTabs getInvitationTabSelected() {
        return invitationTabSelected;
    }

    public void setInvitationTabSelected(InvitationTabs invitationTabSelected) {
        this.invitationTabSelected = invitationTabSelected;
    }

    public List<Object> getHomelistViewWithHeaders() {
        return homelistViewWithHeaders;
    }

    public void setHomelistViewWithHeaders(List<Object> homelistViewWithHeaders) {
        this.homelistViewWithHeaders = homelistViewWithHeaders;
    }

    public List<Long> getUniqueEventIdTracker() {
        return uniqueEventIdTracker;
    }

    public void setUniqueEventIdTracker(List<Long> uniqueEventIdTracker) {
        this.uniqueEventIdTracker = uniqueEventIdTracker;
    }
}
