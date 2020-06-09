package com.cenesbeta.service;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cenesbeta.adapter.FriendHorizontalScrollAdapter;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.dto.PredictiveData;
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static Map<String,Set<CalendarDay>> parsePredictiveData(CreateGatheringFragment createGatheringFragment, List<PredictiveData> predictiveArray, Event event, Long originalStarTime, Long originalEndTime, Long predStartLong, Long predEndLong) {

        Map<String,Set<CalendarDay>> calMap  = new HashMap<>();
        JSONObject predictiveCalendarData = new JSONObject();
        for (PredictiveData predictiveData: predictiveArray) {
            try {

                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.setTime(new Date());

                Calendar cal = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date readableDate = sdf.parse(predictiveData.getReadableDate());

                Calendar predictiveDateCal = Calendar.getInstance();
                predictiveDateCal.setTimeInMillis(predictiveData.getDate());

                if (predictiveDateCal.get(Calendar.DAY_OF_MONTH) < todayCalendar.get(Calendar.DAY_OF_MONTH) &&
                        predictiveDateCal.get(Calendar.MONTH) <= todayCalendar.get(Calendar.MONTH) &&
                        predictiveDateCal.get(Calendar.YEAR) <= todayCalendar.get(Calendar.YEAR)) {
                    continue;
                }

                if (predictiveDateCal.get(Calendar.DAY_OF_MONTH) >= todayCalendar.get(Calendar.DAY_OF_MONTH) &&
                        predictiveDateCal.get(Calendar.MONTH) < todayCalendar.get(Calendar.MONTH) &&
                        predictiveDateCal.get(Calendar.YEAR) <= todayCalendar.get(Calendar.YEAR)) {
                    continue;
                }

                CalendarDay calDay = new CalendarDay(predictiveDateCal);
                System.out.println("Day : "+calDay.getDay()+", Month : "+calDay.getMonth()+", Year : "+calDay.getYear()+", Percentage : "+predictiveData.getPredictivePercentage());

                //if (job.getInt("predictivePercentage") == 0 && job.getInt("predictivePercentage") <= 24) {
                int predictivePercentage = predictiveData.getPredictivePercentage();

                if (originalStarTime != null && originalEndTime != null) {

                    Calendar origStartCal = Calendar.getInstance();
                    origStartCal.setTimeInMillis(originalStarTime);

                    Calendar origEndCal = Calendar.getInstance();
                    origEndCal.setTimeInMillis(originalEndTime);


                    Calendar predictiveSearchStartCal = Calendar.getInstance();
                    predictiveSearchStartCal.setTimeInMillis(predStartLong);

                    Calendar predictiveSearchEndCal = Calendar.getInstance();
                    predictiveSearchEndCal.setTimeInMillis(predEndLong);

                    System.out.println(predictiveDateCal.get(Calendar.DAY_OF_MONTH)+" == "+origStartCal.get(Calendar.DAY_OF_MONTH)+", "+predStartLong.equals(originalStarTime) +"&&"+ predEndLong.equals(originalEndTime));

                    if (predictiveDateCal.get(Calendar.DAY_OF_MONTH) == origStartCal.get(Calendar.DAY_OF_MONTH) &&
                            predictiveSearchStartCal.get(Calendar.HOUR_OF_DAY) == origStartCal.get(Calendar.HOUR_OF_DAY) &&
                            predictiveSearchStartCal.get(Calendar.MINUTE) == origStartCal.get(Calendar.MINUTE) &&
                            predictiveSearchEndCal.get(Calendar.HOUR_OF_DAY) == origEndCal.get(Calendar.HOUR_OF_DAY) &&
                            predictiveSearchEndCal.get(Calendar.MINUTE) == origEndCal.get(Calendar.MINUTE)) {

                        System.out.println("Inside DAY");
                        List<EventMember> eventMembers = event.getEventMembers();
                        int totatEventMembers = eventMembers.size();
                        int eventMembersAttending = 0;
                        for (EventMember eventMember: eventMembers) {
                            if (eventMember.getEventMemberId() != null) {
                                eventMembersAttending++;
                            }
                        }
                        predictivePercentage = (eventMembersAttending/totatEventMembers)*100;
                        System.out.println("predictivePercentage : "+predictivePercentage);

                        String attendingFriends = "";
                        if (predictiveData.getAttendingFriendsList() != null) {
                            attendingFriends = predictiveData.getAttendingFriendsList();
                        }
                        List<String> attendingMembersLst = Arrays.asList(attendingFriends.split(","));
                        System.out.println("attendingMembersArr : "+attendingMembersLst.size());
                        for (EventMember eventMember: eventMembers) {
                            if (eventMember.getUserId() != null) {
                                if (!attendingMembersLst.contains(eventMember.getUserId())) {
                                    if (attendingFriends.length() == 0) {
                                        attendingFriends += eventMember.getUserId();
                                    } else {
                                        attendingFriends += ","+eventMember.getUserId();
                                    }
                                }
                            }
                        }
                        predictiveData.setAttendingFriendsList(attendingFriends);
                        createGatheringFragment.predictiveDataForDate = predictiveData;
                        createGatheringFragment.populateFriendCollectionView();
                    } else {

                        Calendar eventCal = Calendar.getInstance();
                        eventCal.setTimeInMillis(event.getStartTime());

                        if (predictiveDateCal.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)) {
                            createGatheringFragment.predictiveDataForDate = predictiveData;
                            createGatheringFragment.populateFriendCollectionView();
                        }
                    }
                }

                if (predictivePercentage == 0) {
                    //RED
                    if (calMap.containsKey("WHITE")) {


                        Set<CalendarDay> redSet = calMap.get("WHITE");
                        redSet.add(calDay);
                        calMap.put("WHITE",redSet);

                            /*-------------------------*/
                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("WHITE");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("WHITE",redArray);
                            /*-------------------------*/

                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("WHITE",redSet);

                            /*-------------------------*/
                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("WHITE",redArray);
                            /*---------------------------*/
                    }
                } else if (predictivePercentage >= 1 && predictivePercentage <= 33) {
                    //ORANGE
                    if (calMap.containsKey("RED")) {
                        Set<CalendarDay> redSet = calMap.get("RED");
                        redSet.add(calDay);
                        calMap.put("RED",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("RED");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("RED",redArray);

                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("RED",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("RED",redArray);

                    }

                } else if (predictivePercentage >= 34 && predictivePercentage <= 66) {
                    //YELLOW
                    if (calMap.containsKey("PINK")) {
                        Set<CalendarDay> redSet = calMap.get("PINK");
                        redSet.add(calDay);
                        calMap.put("PINK",redSet);

                        JSONArray redArray = (JSONArray) predictiveCalendarData.get("PINK");

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        redArray.put(redObj);
                        predictiveCalendarData.put("PINK",redArray);
                    } else {
                        Set<CalendarDay> redSet = new HashSet<>();
                        redSet.add(calDay);
                        calMap.put("PINK",redSet);

                        JSONObject redObj = new JSONObject();
                        redObj.put("Year",calDay.getYear());
                        redObj.put("Month",calDay.getMonth());
                        redObj.put("Day",calDay.getDay());

                        JSONArray redArray = new JSONArray();
                        redArray.put(redObj);
                        predictiveCalendarData.put("PINK",redArray);

                    }
                } else if (predictivePercentage >= 67 && predictivePercentage <= 99) {
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

                } else if (predictivePercentage == 100) {
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
