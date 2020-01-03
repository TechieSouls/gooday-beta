package com.cenesbeta.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.MeTimeActivity;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 12/1/19.
 */

public class MeTimeService {

    public Map<String,Integer> dayIndexMap() {
        Map<String,Integer> daysIndexMap = new HashMap<>();
        daysIndexMap.put("Sunday",1);
        daysIndexMap.put("Monday",2);
        daysIndexMap.put("Tuesday",3);
        daysIndexMap.put("Wednesday",4);
        daysIndexMap.put("Thursday",5);
        daysIndexMap.put("Friday",6);
        daysIndexMap.put("Saturday",7);
        return daysIndexMap;
    }

    public Map<Integer,String> IndexDayMap() {
        Map<Integer,String> daysIndexMap = new HashMap<>();
        daysIndexMap.put(1,"Sunday");
        daysIndexMap.put(2,"Monday");
        daysIndexMap.put(3,"Tuesday");
        daysIndexMap.put(4, "Wednesday");
        daysIndexMap.put(5, "Thursday");
        daysIndexMap.put(6, "Friday");
        daysIndexMap.put(7, "Saturday");
        return daysIndexMap;
    }

    public List<String> daysInStr() {
        List<String> daysInStrList = new ArrayList<>();
        daysInStrList.add("Monday");
        daysInStrList.add("Tuesday");
        daysInStrList.add("Wednesday");
        daysInStrList.add("Thursday");
        daysInStrList.add("Friday");
        return daysInStrList;
    }

    public JSONObject populateMetimeData(Map.Entry<String, JSONObject> meTimeEntryMap, String day) {

        JSONObject jsonEventObject = new JSONObject();
        try {
            Calendar dayOfWeekCalendar = Calendar.getInstance();
            dayOfWeekCalendar.setTimeInMillis(meTimeEntryMap.getValue().getLong("start_time"));
            dayOfWeekCalendar.set(Calendar.DAY_OF_WEEK,dayIndexMap().get(day));

            jsonEventObject.put("title", meTimeEntryMap.getKey());
            jsonEventObject.put("start_time", dayOfWeekCalendar.getTimeInMillis());
            jsonEventObject.put("end_time", meTimeEntryMap.getValue().getString("end_time"));
            jsonEventObject.put("description", meTimeEntryMap.getKey());
            jsonEventObject.put("day_of_week", day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonEventObject;
    }

    public JSONArray getMeTimeEvents(Map<String, JSONObject> meTimeData) {

        JSONArray events = new JSONArray();
        try {
            for (Map.Entry<String, JSONObject> meTimeEntryMap : meTimeData.entrySet()) {
                if (meTimeEntryMap.getValue().getString("start_time") != null && meTimeEntryMap.getValue().getString("end_time") != null) {

                    if (meTimeEntryMap.getValue().has("Sunday") && meTimeEntryMap.getValue().getBoolean("Sunday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Sunday"));
                    }
                    if (meTimeEntryMap.getValue().has("Monday") && meTimeEntryMap.getValue().getBoolean("Monday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Monday"));
                    }
                    if (meTimeEntryMap.getValue().has("Tuesday") && meTimeEntryMap.getValue().getBoolean("Tuesday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Tuesday"));
                    }
                    if (meTimeEntryMap.getValue().has("Wednesday") && meTimeEntryMap.getValue().getBoolean("Wednesday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Wednesday"));
                    }
                    if (meTimeEntryMap.getValue().has("Thursday") && meTimeEntryMap.getValue().getBoolean("Thursday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Thursday"));
                    }
                    if (meTimeEntryMap.getValue().has("Friday") && meTimeEntryMap.getValue().getBoolean("Friday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Friday"));
                    }
                    if (meTimeEntryMap.getValue().has("Saturday") && meTimeEntryMap.getValue().getBoolean("Saturday") == true) {
                        events.put(populateMetimeData(meTimeEntryMap, "Saturday"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<MeTime> getDefaultMeTimeValues(CenesBaseActivity activity) {
        List<MeTime> defaultMetimeCards = new ArrayList<>();
        JSONArray defaultMeTimeArray = new JSONArray();

        MeTime metimeCard = new MeTime();
        metimeCard.setTitle("Sleep Cycle");
        defaultMetimeCards.add(metimeCard);
        JSONObject meTimeCardJSON = null;
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }


        metimeCard = new MeTime();
        metimeCard.setTitle("Excercise");
        defaultMetimeCards.add(metimeCard);
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        metimeCard = new MeTime();
        metimeCard.setTitle("Family Time");
        defaultMetimeCards.add(metimeCard);
        try {
            meTimeCardJSON = new JSONObject();
            meTimeCardJSON.put("title","Sleep Cycle");
            defaultMeTimeArray.put(meTimeCardJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = activity.getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE).edit();
        editor.putString("defaultMeTimeJSON", defaultMeTimeArray.toString());
        editor.apply();

        return defaultMetimeCards;
    }

    public LinearLayout createMetimeCards(CenesBaseActivity activity, MeTime meTime) {
        LinearLayout.LayoutParams metimeTileParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        metimeTileParams.setMargins(0, CenesUtils.dpToPx(30), 0, 0);

        LinearLayout metimeTile = new LinearLayout(activity);
        metimeTile.setOrientation(LinearLayout.HORIZONTAL);
        metimeTile.setLayoutParams(metimeTileParams);
        metimeTile.setPadding(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));
        metimeTile.setBackgroundResource(R.drawable.xml_round_rect_whitebg);

        //MeTimeImage
        if (meTime.getPhoto() != null && meTime.getPhoto().length() > 0) {
            RoundedImageView meTimeImage = new RoundedImageView(activity);
            LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
            imageViewParams.gravity = Gravity.CENTER;
            imageViewParams.setMargins(CenesUtils.dpToPx(20),0,0,0);
            meTimeImage.setLayoutParams(imageViewParams);
            Glide.with(activity).load(CenesConstants.imageDomain+meTime.getPhoto()).apply(RequestOptions.placeholderOf(R.drawable.metime_default)).into(meTimeImage);
            metimeTile.addView(meTimeImage);
        } else {

            String imageName = "";
            String[] titleArr = meTime.getTitle().split(" ");
            int i=0;
            for (String str: titleArr) {
                if (i > 1) {
                    break;
                }
                imageName += str.substring(0,1).toUpperCase();
                i++;
            }
            TextView circleText = new TextView(activity);
            LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
            imageViewParams.setMargins(CenesUtils.dpToPx(20),0,0,0);
            circleText.setLayoutParams(imageViewParams);
            circleText.setText(imageName);
            circleText.setGravity(Gravity.CENTER);
            circleText.setTextColor(activity.getResources().getColor(R.color.cenes_blue));
            circleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            circleText.setBackground(activity.getResources().getDrawable(R.drawable.xml_circle_trans_blue_border));
            metimeTile.addView(circleText);
        }

        //MeTimeDetails
        LinearLayout detailsLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams detailLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailLayoutParams.setMargins(CenesUtils.dpToPx(10), 0, 0, 0);
        detailsLayout.setLayoutParams(detailLayoutParams);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);

        try {

            //The View for Mon, Tue, Wed, Thur, Fri, Sat
            TextView metimeTitle = new TextView(activity);
            metimeTitle.setText(meTime.getTitle());
            metimeTitle.setTextColor(activity.getResources().getColor(R.color.cenes_blue));
            metimeTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            detailsLayout.addView(metimeTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (meTime.getStartTime() != null && meTime.getStartTime() != 0) {
            TextView metimeDays = new TextView(activity);
            if (meTime.getDays() != null && meTime.getDays().length() > 0) {
                metimeDays.setText(meTime.getDays().replaceAll("-", ""));
            }
            metimeDays.setTextColor(activity.getResources().getColor(R.color.cenes_new_orange));
            metimeDays.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            detailsLayout.addView(metimeDays);

            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(meTime.getStartTime());

            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(meTime.getEndTime());

            TextView metimeHours = new TextView(activity);
            metimeHours.setText(CenesUtils.hmmaa.format(startCal.getTime()) +"-"+CenesUtils.hmmaa.format(endCal.getTime()));
            metimeHours.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            detailsLayout.addView(metimeHours);

        } else {
            TextView metimeDays = new TextView(activity);
            metimeDays.setText("Not Scheduled");
            metimeDays.setTextColor(activity.getResources().getColor(R.color.cenes_light_gray));
            metimeDays.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            detailsLayout.addView(metimeDays);
        }
        metimeTile.addView(detailsLayout);


        return metimeTile;
    }

}