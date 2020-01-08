package com.cenesbeta.countrypicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by mandeep on 16/9/17.
 */

public class CountryUtils {

    public static List<String> getCalendarCountries() {
        List<String> calendarCountries = new ArrayList<>();
        calendarCountries.add("Australian Holidays");
        calendarCountries.add("Austrian Holidays");
        calendarCountries.add("Brazilian Holidays");
        calendarCountries.add("Canadian Holidays");
        calendarCountries.add("China Holidays");
        calendarCountries.add("Christian Holidays");
        calendarCountries.add("Danish Holidays");
        calendarCountries.add("Dutch Holidays");
        calendarCountries.add("Finnish Holidays");
        calendarCountries.add("French Holidays");
        calendarCountries.add("German Holidays");
        calendarCountries.add("Greek Holidays");
        calendarCountries.add("Hong Kong (C) Holidays");
        calendarCountries.add("Hong Kong Holidays");
        calendarCountries.add("Indian Holidays");
        calendarCountries.add("Indonesian Holidays");
        calendarCountries.add("Iranian Holidays");
        calendarCountries.add("Irish Holidays");
        calendarCountries.add("Islamic Holidays");
        calendarCountries.add("Italian Holidays");
        calendarCountries.add("Japanese Holidays");
        calendarCountries.add("Jewish Holidays");
        calendarCountries.add("Malaysian Holidays");
        calendarCountries.add("Mexican Holidays");
        calendarCountries.add("New Zealand Holidays");
        calendarCountries.add("Norwegian Holidays");
        calendarCountries.add("Philippines Holidays");
        calendarCountries.add("Polish Holidays");
        calendarCountries.add("Portuguese Holidays");
        calendarCountries.add("Russian Holidays");
        calendarCountries.add("Singapore Holidays");
        calendarCountries.add("South Africa Holidays");
        calendarCountries.add("South Korean Holidays");
        calendarCountries.add("Spain Holidays");
        calendarCountries.add("Swedish Holidays");
        calendarCountries.add("Taiwan Holidays");
        calendarCountries.add("Thai Holidays");
        calendarCountries.add("UK Holidays");
        calendarCountries.add("US Holidays");
        calendarCountries.add("Vietnamese Holidays");
        return calendarCountries;
    }

    public static Map<String, String> getCountryCalendarIdMap() {
        Map<String, String> countryIdMap = new HashMap<>();
        countryIdMap.put("Australian Holidays","en.australian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Austrian Holidays","en.austrian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Brazilian Holidays","en.brazilian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Canadian Holidays","en.canadian#holiday@group.v.calendar.google.com");
        countryIdMap.put("China Holidays","en.china#holiday@group.v.calendar.google.com");
        countryIdMap.put("Christian Holidays","en.christian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Danish Holidays","en.danish#holiday@group.v.calendar.google.com");
        countryIdMap.put("Dutch Holidays","en.dutch#holiday@group.v.calendar.google.com");
        countryIdMap.put("Finnish Holidays","en.finnish#holiday@group.v.calendar.google.com");
        countryIdMap.put("French Holidays","en.french#holiday@group.v.calendar.google.com");
        countryIdMap.put("German Holidays","en.german#holiday@group.v.calendar.google.com");
        countryIdMap.put("Greek Holidays","en.greek#holiday@group.v.calendar.google.com");
        countryIdMap.put("Hong Kong (C) Holidays","en.hong_kong_c#holiday@group.v.calendar.google.com");
        countryIdMap.put("Hong Kong Holidays","en.hong_kong#holiday@group.v.calendar.google.com");
        countryIdMap.put("Indian Holidays","en.indian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Indonesian Holidays","en.indonesian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Iranian Holidays","en.iranian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Irish Holidays","en.irish#holiday@group.v.calendar.google.com");
        countryIdMap.put("Islamic Holidays","en.islamic#holiday@group.v.calendar.google.com");
        countryIdMap.put("Italian Holidays","en.italian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Japanese Holidays","en.japanese#holiday@group.v.calendar.google.com");
        countryIdMap.put("Jewish Holidays","en.jewish#holiday@group.v.calendar.google.com");
        countryIdMap.put("Malaysian Holidays","en.malaysia#holiday@group.v.calendar.google.com");
        countryIdMap.put("Mexican Holidays","en.mexican#holiday@group.v.calendar.google.com");
        countryIdMap.put("New Zealand Holidays","en.new_zealand#holiday@group.v.calendar.google.com");
        countryIdMap.put("Norwegian Holidays","en.norwegian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Philippines Holidays","en.philippines#holiday@group.v.calendar.google.com");
        countryIdMap.put("Polish Holidays","en.polish#holiday@group.v.calendar.google.com");
        countryIdMap.put("Portuguese Holidays","en.portuguese#holiday@group.v.calendar.google.com");
        countryIdMap.put("Russian Holidays","en.russian#holiday@group.v.calendar.google.com");
        countryIdMap.put("Singapore Holidays","en.singapore#holiday@group.v.calendar.google.com");
        countryIdMap.put("South Africa Holidays","en.sa#holiday@group.v.calendar.google.com");
        countryIdMap.put("South Korean Holidays","en.south_korea#holiday@group.v.calendar.google.com");
        countryIdMap.put("Spain Holidays","en.spain#holiday@group.v.calendar.google.com");
        countryIdMap.put("Swedish Holidays","en.swedish#holiday@group.v.calendar.google.com");
        countryIdMap.put("Taiwan Holidays","en.taiwan#holiday@group.v.calendar.google.com");
        countryIdMap.put("Thai Holidays","en.thai#holiday@group.v.calendar.google.com");
        countryIdMap.put("UK Holidays","en.uk#holiday@group.v.calendar.google.com");
        countryIdMap.put("US Holidays","en.usa#holiday@group.v.calendar.google.com");
        countryIdMap.put("Vietnamese Holidays","en.vietnamese#holiday@group.v.calendar.google.com");
        return countryIdMap;
    }
    
    public static Map<String,String> getCountryCodeMap() {
        Map<String,String> countryCodeMap = new HashMap<>();
        countryCodeMap.put("Australian Holidays","au");
        countryCodeMap.put("Austrian Holidays","at");
        countryCodeMap.put("Brazilian Holidays","br");
        countryCodeMap.put("Canadian Holidays","ca");
        countryCodeMap.put("China Holidays","cn");
        countryCodeMap.put("Christian Holidays","christ");
        countryCodeMap.put("Danish Holidays","dk");
        countryCodeMap.put("Dutch Holidays","dut");
        countryCodeMap.put("Finnish Holidays","fi");
        countryCodeMap.put("French Holidays","fr");
        countryCodeMap.put("German Holidays","de");
        countryCodeMap.put("Greek Holidays","gr");
        countryCodeMap.put("Hong Kong (C) Holidays","hk");
        countryCodeMap.put("Hong Kong Holidays","hk");
        countryCodeMap.put("Indian Holidays","in");
        countryCodeMap.put("Indonesian Holidays","id");
        countryCodeMap.put("Iranian Holidays","ir");
        countryCodeMap.put("Irish Holidays","ie");
        countryCodeMap.put("Islamic Holidays","isl");
        countryCodeMap.put("Italian Holidays","it");
        countryCodeMap.put("Japanese Holidays","jp");
        countryCodeMap.put("Jewish Holidays","jew");
        countryCodeMap.put("Malaysian Holidays","my");
        countryCodeMap.put("Mexican Holidays","mx");
        countryCodeMap.put("New Zealand Holidays","nz");
        countryCodeMap.put("Norwegian Holidays","nw");
        countryCodeMap.put("Philippines Holidays","ph");
        countryCodeMap.put("Polish Holidays","pl");
        countryCodeMap.put("Portuguese Holidays","pt");
        countryCodeMap.put("Russian Holidays","ru");
        countryCodeMap.put("Singapore Holidays","sg");
        countryCodeMap.put("South Africa Holidays","za");
        countryCodeMap.put("South Korean Holidays","kr");
        countryCodeMap.put("Spain Holidays","es");
        countryCodeMap.put("Swedish Holidays","se");
        countryCodeMap.put("Taiwan Holidays","tw");
        countryCodeMap.put("Thai Holidays","th");
        countryCodeMap.put("UK Holidays","ac");
        countryCodeMap.put("US Holidays","us");
        countryCodeMap.put("Vietnamese Holidays","vn");
        return countryCodeMap;
    }

}
