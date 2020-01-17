package com.cenesbeta.fragment.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.CalendarTabExpandableListAdapter;
import com.cenesbeta.api.HomeScreenAPI;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.materialcalendarview.MaterialCalendarView;
import com.cenesbeta.util.CenesUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragmentV2 extends CenesFragment {

    public static String TAG = "HomeFragmentV2";
    private TextView tvCalendarTab, tvInvitationTab, tvCalDate;
    private ImageView ivPlusBtn, ivCalDateBarArrow, ivRefreshCalsBtn;
    private LinearLayout llCalendarDateBar;
    private MaterialCalendarView mcvHomeCalendar;
    private ExpandableListView elvHomeListView;


    private HomeScreenDto homeScreenDto;
    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    public  User loggedInUser;
    private CalendarTabExpandableListAdapter calendarTabExpandableListAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_home_v2, container, false);

        tvCalendarTab = (TextView) view.findViewById(R.id.tv_calendar_tab);
        tvInvitationTab = (TextView) view.findViewById(R.id.tv_invitation_tab);
        tvCalDate = (TextView) view.findViewById(R.id.tv_cal_date);
        ivCalDateBarArrow = (ImageView) view.findViewById(R.id.iv_cal_date_bar_arrow);
        ivRefreshCalsBtn = (ImageView) view.findViewById(R.id.iv_refresh_cals_btn);
        ivPlusBtn = (ImageView) view.findViewById(R.id.iv_plus_btn);
        llCalendarDateBar = (LinearLayout) view.findViewById(R.id.ll_calendar_date_bar);
        mcvHomeCalendar = (MaterialCalendarView) view.findViewById(R.id.mcv_home_calendar);
        elvHomeListView = (ExpandableListView) view.findViewById(R.id.elv_home_list_view);

        //Click Listeners
        tvCalendarTab.setOnClickListener(onClickListener);
        tvInvitationTab.setOnClickListener(onClickListener);
        ivPlusBtn.setOnClickListener(onClickListener);
        ivRefreshCalsBtn.setOnClickListener(onClickListener);
        llCalendarDateBar.setOnClickListener(onClickListener);

        //Java Variables
        homeScreenDto = new HomeScreenDto();
        coreManager = ((CenesBaseActivity)getActivity()).getCenesApplication().getCoreManager();
        internetManager = coreManager.getInternetManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        loadCalendarTabData();
        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(HomeFragmentV2.TAG);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.tv_calendar_tab:
                    calendarTabPressed();
                    break;

                case R.id.tv_invitation_tab:
                    invitationTabPressed();
                    break;

                case R.id.iv_plus_btn:
                    plusButtonPressed();
                    break;

                case R.id.iv_refresh_cals_btn:
                    refreshCalBtnPressed();
                    break;

                case R.id.ll_calendar_date_bar:
                    calendarDateBarPressed();
                    break;
            }
        }
    };

    public void calendarTabPressed() {

        tvCalendarTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvInvitationTab.setBackground(null);
    }

    public void invitationTabPressed() {

        tvInvitationTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvCalendarTab.setBackground(null);
    }

    public void plusButtonPressed() {
        ((CenesBaseActivity)getActivity()).replaceFragment(new FriendListFragment(), HomeFragmentV2.TAG);
    }

    public void calendarDateBarPressed() {

        //If it was open..Lets close it
        if (homeScreenDto.isCalendarOpen() == true) {
            ivCalDateBarArrow.setImageResource(R.drawable.close_calendar);
            homeScreenDto.setCalendarOpen(false);
            mcvHomeCalendar.setVisibility(View.GONE);
        } else {
            ivCalDateBarArrow.setImageResource(R.drawable.open_calendar);
            homeScreenDto.setCalendarOpen(true);
            mcvHomeCalendar.setVisibility(View.VISIBLE);
        }
    }

    public void refreshCalBtnPressed() {

    }

    public void loadCalendarTabData(){

        homeScreenDto.setHomeScreenAPICall(HomeScreenDto.HomeScreenAPICall.Home);
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_homescreen_events);
        asyncTaskDto.setQueryStr("user_id=" + loggedInUser.getUserId() + "&date="+new Date().getTime());
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        getAsyncDto(asyncTaskDto);
    }

    public void getAsyncDto(AsyncTaskDto asyncTaskDto){

       new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
           @Override
           public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");

                    if ( success ) {
                        Gson gson = new GsonBuilder().create();
                        Type listType = new TypeToken<List<Event>>(){}.getType();
                        List<Event> events = gson.fromJson(response.getJSONArray("data").toString(), listType);
                        processCalendarTabData(events);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
           }
       }).execute(asyncTaskDto);

    }

    public void processCalendarTabData(List<Event> events){

        List<String> headers = new ArrayList<>();
        List<Long> headersTimstamp = new ArrayList<>();

        Map<String,List<Event>> mapListEvent = new HashMap<>();


        for (Event event : events) {
            try{

                String headerTitle = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()));
                String monthTitle = CenesUtils.MMyyyy.format(new Date(event.getStartTime()));

                if( mapListEvent.containsKey(monthTitle) ){
                    List<Event> eventList = mapListEvent.get(monthTitle);
                    Event eventNew = new Event();
                    eventList.add(eventNew);
                    mapListEvent.put(monthTitle,eventList);
                }
                if( mapListEvent.containsKey(headerTitle) ){
                    List<Event> eventList = mapListEvent.get(headerTitle);
                    eventList.add(event);
                    mapListEvent.put(headerTitle,eventList);
                } else {
                    List<Event> eventList = new ArrayList<>();
                    eventList.add(event);
                    mapListEvent.put(headerTitle,eventList);
                    headersTimstamp.add(event.getStartTime());

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        Collections.sort(headersTimstamp);
        for (Long timestamp : headersTimstamp){
            String headerTitle = CenesUtils.EEEMMMMdd.format(new Date(timestamp));
            headers.add(headerTitle);
        }

        for (int i=0; i < headers.size(); i++) {

            if (i != 0) {

                List<Event> previousListEvents = mapListEvent.get(headers.get(i-1));

                List<Event> currentListEvents = mapListEvent.get(headers.get(i));


                Event previoustListFirstEvent = previousListEvents.get(0);
                Event currentListFirstEvent = currentListEvents.get(0);

                Calendar previstListEventDateCal = Calendar.getInstance();
                previstListEventDateCal.setTimeInMillis(previoustListFirstEvent.getStartTime());

                Calendar currentListEventDateCal = Calendar.getInstance();
                currentListEventDateCal.setTimeInMillis(currentListFirstEvent.getStartTime());

                if (previstListEventDateCal.get(Calendar.MONTH) != currentListEventDateCal.get(Calendar.MONTH) ) {

                    String monthTitle = CenesUtils.MMyyyy.format(new Date(currentListFirstEvent.getStartTime()));


                    //Lets make it at the end of the day
                    Calendar monthSeparatorCal = Calendar.getInstance();
                    monthSeparatorCal.setTimeInMillis(previoustListFirstEvent.getStartTime());
                    monthSeparatorCal.set(Calendar.HOUR_OF_DAY, 23);
                    monthSeparatorCal.set(Calendar.MINUTE, 59);
                    monthSeparatorCal.set(Calendar.SECOND, 59);
                    monthSeparatorCal.set(Calendar.MILLISECOND, 59);

                    //Lets create new event
                    Event event = new Event();
                    event.setScheduleAs("MonthSeparator");
                    event.setTitle(monthTitle);
                    event.setStartTime(monthSeparatorCal.getTimeInMillis());
                    previousListEvents.add(event);
                    mapListEvent.put(headers.get(i-1), previousListEvents);
                }
            } else{

            }
        }

        homeScreenDto.setHomeDataHeaders(headers);
        homeScreenDto.setHomeDataListMap(mapListEvent);

        calendarTabExpandableListAdapter = new CalendarTabExpandableListAdapter(this, homeScreenDto);
        elvHomeListView.setAdapter(calendarTabExpandableListAdapter);
    }

}
