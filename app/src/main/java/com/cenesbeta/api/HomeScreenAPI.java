package com.cenesbeta.api;

/**
 * Created by mandeep on 25/10/18.
 */

public class HomeScreenAPI {

    //GET
    public static String get_refreshGoogleEvents = "/api/google/refreshEvents";
    public static String get_refreshGOutlookEvents = "/api/outlook/refreshevents";
    public static String get_homescreen_events = "/api/getEvents"; //user_id, date(in millis)
    public static String get_gathering_evnets =  "/api/user/gatherings/v2";
    public static String get_homescreen_events_v2 = "/api/getEvents/android/v2"; //user_id, timestamp, pagenumber, offset
    public static String get_homescreen_past_events = "/api/getEventsMonthWise/v3"; //userId, startTimestamp, endtimeStamp, pastEvents
    public static String get_past_and_current_events = "/api/event/pastAndCurrent"; //userId, timestamp, pageNumber, offSet

}
