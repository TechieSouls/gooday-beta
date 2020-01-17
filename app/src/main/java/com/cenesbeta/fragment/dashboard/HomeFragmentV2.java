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
import com.cenesbeta.adapter.InvitationListItemAdapter;
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
import java.util.ArrayList;
import java.util.Collections;
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
    private ExpandableListView elvInvitationListView;


    private HomeScreenDto homeScreenDto;
    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    public  User loggedInUser;
    private CalendarTabExpandableListAdapter calendarTabExpandableListAdapter;
    private InvitationListItemAdapter invitationListItemAdapter;
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
        elvInvitationListView = (ExpandableListView)view.findViewById(R.id.elv_invitation_list_view);

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

        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Calendar);
        tvCalendarTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvInvitationTab.setBackground(null);
        processCalendarTabData(homeScreenDto.getHomeEvents());
    }

    public void invitationTabPressed() {

        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Invitation);
        tvInvitationTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvCalendarTab.setBackground(null);
        processCalendarTabData(homeScreenDto.getAcceptedEvents());

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

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_homescreen_events);
        asyncTaskDto.setQueryStr("user_id=" + loggedInUser.getUserId() + "&date="+new Date().getTime());
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        getAsyncDto(asyncTaskDto, HomeScreenDto.HomeScreenAPICall.Home);

        loadInvitationTabData();
    }

    public void loadInvitationTabData() {

        AsyncTaskDto acceptedAsyncTaskDto = new AsyncTaskDto();
        acceptedAsyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_gathering_evnets);
        acceptedAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=Going&timestamp="+new Date().getTime()+"&pageNumber=0&offSet=20");
        acceptedAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(acceptedAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Accepted);

        AsyncTaskDto penidngAsyncTaskDto = new AsyncTaskDto();
        penidngAsyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_gathering_evnets);
        penidngAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=Pending&timestamp="+new Date().getTime()+"&pageNumber=0&offSet=20");
        penidngAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(penidngAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Pending);

        AsyncTaskDto declinedAsyncTaskDto = new AsyncTaskDto();
        declinedAsyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_gathering_evnets);
        declinedAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=NotGoing&timestamp="+new Date().getTime()+"&pageNumber=0&offSet=20");
        declinedAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(declinedAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Declined);
    }

    public void getAsyncDto(AsyncTaskDto asyncTaskDto, final HomeScreenDto.HomeScreenAPICall homeScreenAPICall){

       new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
           @Override
           public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");

                    if (success) {
                        Gson gson = new GsonBuilder().create();
                        Type listType = new TypeToken<List<Event>>(){}.getType();
                        List<Event> events = gson.fromJson(response.getJSONArray("data").toString(), listType);

                        if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Home)) {
                            homeScreenDto.setHomeEvents(events);
                            processCalendarTabData(events);

                        } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Accepted)) {
                            homeScreenDto.setAcceptedEvents(events);

                        } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Pending)) {
                            homeScreenDto.setPendingEvents(events);

                        } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Declined)) {
                            homeScreenDto.setDeclinedEvents(events);

                        }

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
        if (events != null) {
            for (Event event : events) {
                try{

                    String headerTitle = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()));

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
        }
        Collections.sort(headersTimstamp);
        for (Long timestamp : headersTimstamp){
            String headerTitle = CenesUtils.EEEMMMMdd.format(new Date(timestamp));
            headers.add(headerTitle);
        }
        homeScreenDto.setHomeDataHeaders(headers);
        homeScreenDto.setHomeDataListMap(mapListEvent);

        if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
            elvHomeListView.setVisibility(View.VISIBLE);
            elvInvitationListView.setVisibility(View.GONE);
            calendarTabExpandableListAdapter = new CalendarTabExpandableListAdapter(this, homeScreenDto);
            elvHomeListView.setAdapter(calendarTabExpandableListAdapter);
        } else {
            elvHomeListView.setVisibility(View.GONE);
            elvInvitationListView.setVisibility(View.VISIBLE);
            invitationListItemAdapter = new InvitationListItemAdapter(this, homeScreenDto);
            elvInvitationListView.setAdapter(invitationListItemAdapter);
        }
    }
}
