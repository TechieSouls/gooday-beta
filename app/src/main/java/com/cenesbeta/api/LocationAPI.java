package com.cenesbeta.api;

/**
 * Created by mandeep on 4/1/19.
 */

public class LocationAPI {

    //GET
    public static String get_recentLocations = "/api/event/locations";
    public static String GOOGLE_LOCATION_API = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";
    public static String GOOGLE_PLACE_DETAILS_API = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";
    public static String get_googleNearByLocations = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";
    public static String get_googleWorldWideLocations = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58";

}
