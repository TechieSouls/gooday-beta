package com.cenes.service;

import com.cenes.materialcalendarview.CalendarDay;
import com.cenes.materialcalendarview.decorators.HighlightWeekendsDecorator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mandeep on 10/10/17.
 */

public class GatheringService {

    public static void main() {
        CalendarDay currentDate = new CalendarDay(2017,10,26);
        System.out.println(getEnableDates(currentDate));
    }


    public static List<CalendarDay> getEnableDates(CalendarDay currentDate) {

        List<CalendarDay> enabledDates = new ArrayList<>();
        Calendar monthStartCal = Calendar.getInstance();
        monthStartCal.set(Calendar.DAY_OF_MONTH,1);

        Calendar monthEndCal = Calendar.getInstance();
        monthEndCal.set(Calendar.DAY_OF_MONTH,monthEndCal.getMaximum(Calendar.DAY_OF_MONTH));

        while (monthStartCal.get(Calendar.DAY_OF_MONTH) != monthEndCal.get(Calendar.DAY_OF_MONTH)) {

            System.out.println("Current Month : "+monthStartCal.get(Calendar.MONTH)+", Current Date : "+currentDate.getMonth());

            System.out.println("Current DAy Month : "+monthStartCal.get(Calendar.DAY_OF_MONTH)+", Current Date : "+currentDate.getDay());
            if (monthStartCal.get(Calendar.DAY_OF_MONTH) < currentDate.getDay() && monthStartCal.get(Calendar.MONTH) >= currentDate.getMonth()) {
                monthStartCal.add(Calendar.DAY_OF_MONTH,1);
                continue;
            }

            CalendarDay calendarDay =  new CalendarDay(monthStartCal.get(Calendar.YEAR),monthStartCal.get(Calendar.MONTH),monthStartCal.get(Calendar.DAY_OF_MONTH));
            enabledDates.add(calendarDay);
            System.out.println(calendarDay);
            monthStartCal.add(Calendar.DAY_OF_MONTH,1);
        }
        System.out.println(enabledDates.toString());
        return enabledDates;
    }

    public static Map<String,Set<CalendarDay>> parsePredictiveData(JSONArray predictiveArray) {
        Map<String,Set<CalendarDay>> calMap  = new HashMap<>();
        JSONObject predictiveCalendarData = new JSONObject();
        for (int i =0; i < predictiveArray.length(); i++) {
            try {
                JSONObject job = (JSONObject)predictiveArray.get(i);

                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.setTime(new Date());

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(job.getLong("date"));
                if (cal.get(Calendar.DAY_OF_MONTH) < todayCalendar.get(Calendar.DAY_OF_MONTH) && cal.getTimeInMillis() < todayCalendar.getTimeInMillis()) {
                    continue;
                }

                /*if (cal.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH) &&
                        cal.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                        cal.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)) {
                    continue;
                }*/
                CalendarDay calDay = new CalendarDay(cal);

                Set<CalendarDay> calSet = new HashSet<>();
                //calSet.add(calDay);

                if (job.getInt("predictivePercentage") >= 0 && job.getInt("predictivePercentage") <= 24) {
                    //RED
                    if (calMap.containsKey("RED")) {


                        Set<CalendarDay> redSet = calMap.get("RED");
                        redSet.add(calDay);
                        calMap.put("RED",redSet);

                            /*-------------------------*/
                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("RED");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("RED",redArray);
                            /*-------------------------*/

                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("RED",redSet);

                            /*-------------------------*/
                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("RED",redArray);
                            /*---------------------------*/
                    }
                } else if (job.getInt("predictivePercentage") >= 25 && job.getInt("predictivePercentage") <= 49) {
                    //ORANGE
                    if (calMap.containsKey("ORANGE")) {
                        Set<CalendarDay> redSet = calMap.get("ORANGE");
                        redSet.add(calDay);
                        calMap.put("ORANGE",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("ORANGE");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("ORANGE",redArray);

                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("ORANGE",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("ORANGE",redArray);

                    }

                } else if (job.getInt("predictivePercentage") >= 50 && job.getInt("predictivePercentage") <= 74) {
                    //YELLOW
                    if (calMap.containsKey("YELLOW")) {
                        Set<CalendarDay> redSet = calMap.get("YELLOW");
                        redSet.add(calDay);
                        calMap.put("YELLOW",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("YELLOW");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("YELLOW",redArray);
                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("YELLOW",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("YELLOW",redArray);

                    }
                } else if (job.getInt("predictivePercentage") >= 75 && job.getInt("predictivePercentage") <= 99) {
                    //LIME
                    if (calMap.containsKey("LIME")) {
                        Set<CalendarDay> redSet = calMap.get("LIME");
                        redSet.add(calDay);
                        calMap.put("LIME",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("LIME");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("LIME",redArray);
                    } else {

                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("LIME",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("LIME",redArray);
                    }

                } else if (job.getInt("predictivePercentage") == 100) {
                    //GREEN
                    if (calMap.containsKey("GREEN")) {
                        Set<CalendarDay> redSet = calMap.get("GREEN");
                        redSet.add(calDay);
                        calMap.put("GREEN",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("GREEN");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("GREEN",redArray);

                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("GREEN",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("GREEN",redArray);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return calMap;
    }
}
