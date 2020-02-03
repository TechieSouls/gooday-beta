package com.cenesbeta.fragment.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.CalendarTabExpandableListAdapter;
import com.cenesbeta.adapter.HomeScreenAdapter;
import com.cenesbeta.adapter.InvitationListItemAdapter;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.api.HomeScreenAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.fragment.profile.ProfileMyCalendarsFragment;
import com.cenesbeta.materialcalendarview.CalendarDay;
import com.cenesbeta.materialcalendarview.MaterialCalendarView;
import com.cenesbeta.materialcalendarview.decorators.EventDecorator;
import com.cenesbeta.util.CenesUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragmentV2 extends CenesFragment {

    public static String TAG = "HomeFragmentV2";
    public enum SyncCallFor {Google, Outlook, Contacts}

    private TextView tvCalendarTab, tvInvitationTab, tvCalDate, tvConfirmedBtn, tvPendingBtn, tvDeclinedBtn, tvSyncCalToastText;
    private ImageView ivPlusBtn, ivCalDateBarArrow, ivRefreshCalsBtn, ivIosSpinner;
    private LinearLayout llCalendarDateBar, llInvitationTabView, llSyncCalToast;
    private MaterialCalendarView mcvHomeCalendar;
    private ExpandableListView elvHomeListView;
    private ExpandableListView elvInvitationListView;
    private View homeFragementView;
    private RelativeLayout rlNoGatheringText;
    private Button btCreateGathering, btSyncCalendar;


    private HomeScreenDto homeScreenDto;
    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    public  User loggedInUser;
    private CalendarTabExpandableListAdapter calendarTabExpandableListAdapter;
    private InvitationListItemAdapter invitationListItemAdapter;
    private Map<SyncCallFor, Boolean> calendarRefreshed = new HashMap<>();
    private ShimmerFrameLayout shimmerFrameLayout, shimmerViewOnscroll;
    private EventManagerImpl eventManagerImpl;
    private CenesApplication cenesApplication;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        if (homeFragementView != null) {
            return homeFragementView;
        }
        View view = inflater.inflate(R.layout.fragment_home_v2, container, false);

        homeFragementView = view;

        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        shimmerViewOnscroll = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_onscroll);

        tvCalendarTab = (TextView) view.findViewById(R.id.tv_calendar_tab);
        tvInvitationTab = (TextView) view.findViewById(R.id.tv_invitation_tab);
        tvCalDate = (TextView) view.findViewById(R.id.tv_cal_date);
        tvConfirmedBtn = (TextView) view.findViewById(R.id.tv_confirmed_btn);
        tvPendingBtn = (TextView) view.findViewById(R.id.tv_pending_btn);
        tvDeclinedBtn = (TextView) view.findViewById(R.id.tv_declined_btn);
        tvSyncCalToastText = (TextView) view.findViewById(R.id.tv_sync_cal_toast_text);
        ivCalDateBarArrow = (ImageView) view.findViewById(R.id.iv_cal_date_bar_arrow);
        ivRefreshCalsBtn = (ImageView) view.findViewById(R.id.iv_refresh_cals_btn);
        ivPlusBtn = (ImageView) view.findViewById(R.id.iv_plus_btn);
        ivIosSpinner = (ImageView) view.findViewById(R.id.iv_ios_spinner);
        llCalendarDateBar = (LinearLayout) view.findViewById(R.id.ll_calendar_date_bar);
        llInvitationTabView= (LinearLayout)view.findViewById(R.id.ll_invitation_tab_view);
        llSyncCalToast = (LinearLayout) view.findViewById(R.id.ll_sync_cal_toast);
        rlNoGatheringText = (RelativeLayout) view.findViewById(R.id.rl_no_gathering_text);
        btCreateGathering = (Button) view.findViewById(R.id.bt_create_gathering);
        btSyncCalendar = (Button) view.findViewById(R.id.bt_sync_calendar);

        mcvHomeCalendar = (MaterialCalendarView) view.findViewById(R.id.mcv_home_calendar);
        elvHomeListView = (ExpandableListView) view.findViewById(R.id.elv_home_list_view);
        elvInvitationListView = (ExpandableListView)view.findViewById(R.id.elv_invitation_list_view);

        //Click Listeners
        tvCalendarTab.setOnClickListener(onClickListener);
        tvInvitationTab.setOnClickListener(onClickListener);
        tvConfirmedBtn.setOnClickListener(onClickListener);
        tvPendingBtn.setOnClickListener(onClickListener);
        tvDeclinedBtn.setOnClickListener(onClickListener);
        ivPlusBtn.setOnClickListener(onClickListener);
        ivRefreshCalsBtn.setOnClickListener(onClickListener);
        llCalendarDateBar.setOnClickListener(onClickListener);
        btCreateGathering.setOnClickListener(onClickListener);
        btSyncCalendar.setOnClickListener(onClickListener);
        elvHomeListView.setOnScrollListener(calendarTabListScrollListener);

        //Java Variables
        homeScreenDto = new HomeScreenDto();
        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = ((CenesBaseActivity)getActivity()).getCenesApplication().getCoreManager();
        internetManager = coreManager.getInternetManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        eventManagerImpl = new EventManagerImpl(cenesApplication);

        mcvHomeCalendar.sourceFragment = this;

        //When internet connection is off/first time loading...
        eventManagerImpl.deleteAllEvents();
        firstTimeLoadData();
        if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            loadHomeScreenData();
        }

        makeMixPanelCall();
        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(HomeFragmentV2.TAG);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(HomeFragmentV2.TAG);
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("homescreenrefresh"));
    }

    @Override
    public void onPause() {
        super.onPause();
        //getActivity().unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Broadcaster Processing");
            //do other stuff here
            loadHomeScreenData();
        }
    };

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

                case R.id.tv_confirmed_btn:
                    invitationStatusTabsPressed(HomeScreenDto.InvitationTabs.Accepted);
                    break;
                case R.id.tv_pending_btn:
                    invitationStatusTabsPressed(HomeScreenDto.InvitationTabs.Pending);

                    break;
                case R.id.tv_declined_btn:
                    invitationStatusTabsPressed(HomeScreenDto.InvitationTabs.Declined);

                    break;

                case R.id.bt_create_gathering:
                    plusButtonPressed();
                    break;

                case R.id.bt_sync_calendar:
                    buttonSyncPressed();
                    break;
            }
        }
    };

    ExpandableListView.OnScrollListener calendarTabListScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            try {
                //Algorithm to check if the last item is visible or not
                final int lastItem = firstVisibleItem + visibleItemCount;

                //System.out.println("firstVisibleItem , visibleItemCount, totalItemCount : "+firstVisibleItem+" -- "+visibleItemCount+" -- "+totalItemCount);
                if(lastItem == totalItemCount && totalItemCount != 0) {
                    // you have reached end of list, load more data

                    System.out.println("Home Screen Events Size : "+homeScreenDto.getHomeEvents().size());
                    if (homeScreenDto.getHomeEvents().size() < HomeScreenDto.totalCalendarDataCounts) {
                        if (HomeScreenDto.madeApiCall) {
                            shimmerViewOnscroll.setVisibility(View.VISIBLE);
                            shimmerViewOnscroll.showShimmer(true);
                            HomeScreenDto.madeApiCall = false;
                            loadCalendarTabData();
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void onCalendarPageChangeListener(CalendarDay currentPage) {

        String monthTitle = CenesUtils.MMMM_yyyy.format(currentPage.getDate());
        System.out.println("Current Month : "+monthTitle);
        if (HomeScreenDto.homeListGroupAndMonthHolder.containsKey(monthTitle)) {
            int groupPosition = HomeScreenDto.homeListGroupAndMonthHolder.get(monthTitle);
            elvHomeListView.setSelectedGroup(groupPosition);
        } else if (monthTitle == CenesUtils.MMMM_yyyy.format(new Date()))  {
            elvHomeListView.smoothScrollToPosition(0);
        }
    }

    public void updateCalendarLabelDate(String calHeaderDate) {
        tvCalDate.setText(calHeaderDate);
    }

    public void calendarTabPressed() {

        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Calendar);
        tvCalendarTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvInvitationTab.setBackground(null);
        elvInvitationListView.scrollTo(0, 0);
        llInvitationTabView.setVisibility(View.GONE);
        if(homeScreenDto.getHomeEvents().size() == 0 && homeScreenDto.getPastEvents().size() == 0){
            rlNoGatheringText.setVisibility(View.VISIBLE);
        }else{
            rlNoGatheringText.setVisibility(View.GONE);
        }
        processCalendarTabData(homeScreenDto.getHomeEvents(), false);
        elvHomeListView.setVisibility(View.VISIBLE);
    }

    public void invitationTabPressed() {

        tvCalDate.setText(CenesUtils.MMMM.format(new Date()));
        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Invitation);
        tvInvitationTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvCalendarTab.setBackground(null);
        elvHomeListView.scrollTo(0, 0);
        calendarTabExpandableListAdapter = null;
        elvHomeListView.setVisibility(View.GONE);
        rlNoGatheringText.setVisibility(View.GONE);
        processInvitationEvents(homeScreenDto.getAcceptedEvents());
        llInvitationTabView.setVisibility(View.VISIBLE);
        highlightInvitationTabs(tvConfirmedBtn);
    }

    public void plusButtonPressed() {
        ((CenesBaseActivity)getActivity()).replaceFragment(new FriendListFragment(), HomeFragmentV2.TAG);
    }

    public void buttonSyncPressed() {
        ((CenesBaseActivity)getActivity()).replaceFragment(new ProfileMyCalendarsFragment(), HomeFragmentV2.TAG);
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
            mcvHomeCalendar.setCurrentDate(new Date());
        }
    }

    public void invitationStatusTabsPressed(HomeScreenDto.InvitationTabs invitationTabName) {
        if (invitationTabName.equals(HomeScreenDto.InvitationTabs.Accepted)) {
            processInvitationEvents(homeScreenDto.getAcceptedEvents());
            highlightInvitationTabs(tvConfirmedBtn);
            homeScreenDto.setInvitationTabSelected(HomeScreenDto.InvitationTabs.Accepted);
        } else if (invitationTabName.equals(HomeScreenDto.InvitationTabs.Pending)) {
            processInvitationEvents(homeScreenDto.getPendingEvents());
            highlightInvitationTabs(tvPendingBtn);
            homeScreenDto.setInvitationTabSelected(HomeScreenDto.InvitationTabs.Pending);

        } else if (invitationTabName.equals(HomeScreenDto.InvitationTabs.Declined)) {
            processInvitationEvents(homeScreenDto.getDeclinedEvents());
            highlightInvitationTabs(tvDeclinedBtn);
            homeScreenDto.setInvitationTabSelected(HomeScreenDto.InvitationTabs.Declined);
        }
    }

    public void highlightInvitationTabs(TextView selection) {
        tvConfirmedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        tvConfirmedBtn.setTextColor(getResources().getColor(R.color.black));
        tvConfirmedBtn.setTypeface(Typeface.DEFAULT);

        tvPendingBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        tvPendingBtn.setTextColor(getResources().getColor(R.color.black));
        tvPendingBtn.setTypeface(Typeface.DEFAULT);

        tvDeclinedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        tvDeclinedBtn.setTextColor(getResources().getColor(R.color.black));
        tvDeclinedBtn.setTypeface(Typeface.DEFAULT);

        selection.setBackground(getResources().getDrawable(R.drawable.border_bottom_orange));
        selection.setTextColor(getResources().getColor(R.color.black));
        //selection.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void homeButtonPressed() {

        elvHomeListView.setSelectedGroup(HomeScreenDto.currentDateGroupPosition);

        String groupStr = homeScreenDto.getHomeDataHeaders().get(HomeScreenDto.currentDateGroupPosition);
        Event event = homeScreenDto.getHomeDataListMap().get(groupStr).get(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getStartTime());
        mcvHomeCalendar.setCurrentDate(calendar.getTime());
    }

    public void refreshCalBtnPressed() {

        rotate(360, ivRefreshCalsBtn);
        llSyncCalToast.setVisibility(View.VISIBLE);
        tvSyncCalToastText.setText("Syncing calendar...");
        ivIosSpinner.setVisibility(View.VISIBLE);
        Glide.with(getContext()).asGif().load(R.drawable.ios_spinner).into(ivIosSpinner);

        prepareCalendarSyncCalls(SyncCallFor.Google);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareCalendarSyncCalls(SyncCallFor.Outlook);
            }
        }, 1000);
    }

    public void eventDeleteButtonPressed(Event event) {

        this.eventManagerImpl.deleteEventByEventId(event.getEventId());

        Map<String, List<Event>> homeDataListMap = homeScreenDto.getHomeDataListMap();

        String key = "";
        Calendar calendar = Calendar.getInstance();
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTimeInMillis(event.getStartTime());
        if (calendar.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
            key = CenesUtils.EEEMMMMdd.format(event.getStartTime());
        } else {
            key = CenesUtils.EEEMMMMddcmyyyy.format(event.getStartTime());
        }
        List<Event> eventList = homeDataListMap.get(key);
        for (Event evenToDelete: eventList) {
            if (evenToDelete.getEventId().equals(event.getEventId())) {
                eventList.remove(event);
                break;
            }
        }
        homeDataListMap.put(key, eventList);
        homeScreenDto.setHomeDataListMap(homeDataListMap);
        calendarTabExpandableListAdapter.notifyDataSetChanged();

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setQueryStr("event_id="+event.getEventId());
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_delete_event_api);
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

            }
        }).execute(asyncTaskDto);
    }

    public void updateAttendingStatus(Event event) {

        this.eventManagerImpl.updateByDisplayAtScreenByEventId(event.getEventId(), loggedInUser.getUserId(), Event.EventDisplayScreen.DECLINED.toString());

        Map<String, List<Event>> homeDataListMap = homeScreenDto.getHomeDataListMap();

        String key = "";
        Calendar calendar = Calendar.getInstance();
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTimeInMillis(event.getStartTime());
        if (calendar.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
            key = CenesUtils.EEEMMMMdd.format(event.getStartTime());
        } else {
            key = CenesUtils.EEEMMMMddcmyyyy.format(event.getStartTime());
        }
        List<Event> eventList = homeDataListMap.get(key);
        if (eventList != null) {

            for (Event evenToDelete: eventList) {
                if (evenToDelete.getEventId().equals(event.getEventId())) {
                    eventList.remove(event);
                    break;
                }
            }

            if (eventList.size() == 0) {
                homeDataListMap.remove(key);
                List<String> tableHeaders = homeScreenDto.getHomeDataHeaders();
                tableHeaders.remove(key);
                homeScreenDto.setHomeDataHeaders(tableHeaders);
            } else {
                homeDataListMap.put(key, eventList);
            }
            homeScreenDto.setHomeDataListMap(homeDataListMap);
            calendarTabExpandableListAdapter.notifyDataSetChanged();
        }

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        asyncTaskDto.setQueryStr("eventId="+ event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing");
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+GatheringAPI.get_update_invitation_api);

        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

            }
        }).execute(asyncTaskDto);
    }

    public void makeMixPanelCall() {

        //Mix Panel Tracking
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("Action","Home Screen Opened");
            props.put("UserEmail",loggedInUser.getEmail());
            props.put("UserName",loggedInUser.getName());
            props.put("Device","Android");
            mixpanel.track("Homescreen", props);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareCalendarSyncCalls(SyncCallFor syncCallFor) {
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();

        if (syncCallFor.equals(SyncCallFor.Google)) {
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+HomeScreenAPI.get_refreshGoogleEvents);
        } else if (syncCallFor.equals(SyncCallFor.Outlook)) {
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+HomeScreenAPI.get_refreshGOutlookEvents);
        }

        asyncTaskDto.setQueryStr("userId="+loggedInUser.getUserId());
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getSyncCallAsyncCall(asyncTaskDto, syncCallFor);
    }

    public void loadHomeScreenData() {

        HomeScreenDto.HomeTabs tabSelected = homeScreenDto.getTabSelected();
        HomeScreenDto.InvitationTabs invitationTabSelected = homeScreenDto.getInvitationTabSelected();
        //Java Variables
        homeScreenDto = new HomeScreenDto();
        homeScreenDto.setTabSelected(tabSelected);
        if (tabSelected.equals(HomeScreenDto.HomeTabs.Invitation)) {
            homeScreenDto.setInvitationTabSelected(invitationTabSelected);
        }
        HomeScreenDto.currentDateGroupPosition = 0;
        HomeScreenDto.calendarTabPageNumber = 0;
        HomeScreenDto.calendarDataHeaders = new ArrayList<>();
        HomeScreenDto.totalCalendarDataCounts = 0;
        HomeScreenDto.madeApiCall = false;

        if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {

            loadPastEvents();
            loadInvitationTabData();

        } else {

            loadInvitationTabData();
            loadPastEvents();
        }

    }

    public void loadPastEvents() {

        Calendar todayDate = Calendar.getInstance();
        todayDate.set(Calendar.HOUR_OF_DAY, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.MILLISECOND, 0);

        Calendar sixMonthsBackDate = Calendar.getInstance();
        sixMonthsBackDate.set(Calendar.MONTH, todayDate.get(Calendar.MONTH) - 6);
        sixMonthsBackDate.set(Calendar.HOUR_OF_DAY, 0);
        sixMonthsBackDate.set(Calendar.MINUTE, 0);
        sixMonthsBackDate.set(Calendar.SECOND, 0);
        sixMonthsBackDate.set(Calendar.MILLISECOND, 0);

        Calendar yesterdayDate = Calendar.getInstance();
        yesterdayDate.set(Calendar.DAY_OF_MONTH, todayDate.get(Calendar.DAY_OF_MONTH) - 1);
        yesterdayDate.set(Calendar.HOUR_OF_DAY, 0);
        yesterdayDate.set(Calendar.MINUTE, 0);
        yesterdayDate.set(Calendar.SECOND, 0);
        yesterdayDate.set(Calendar.MILLISECOND, 0);

        String queryStr = "userId="+loggedInUser.getUserId()+"&startimeStamp="+sixMonthsBackDate.getTimeInMillis()+"" +
                "&endtimeStamp="+yesterdayDate.getTimeInMillis()+"";
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_homescreen_past_events);
        asyncTaskDto.setQueryStr(queryStr);
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        getAsyncDto(asyncTaskDto, HomeScreenDto.HomeScreenAPICall.PastEvents);
    }
    public void loadCalendarTabData() {

        Calendar todayDate = Calendar.getInstance();
        todayDate.set(Calendar.HOUR_OF_DAY, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.MILLISECOND, 0);


        String queryStr = "userId="+loggedInUser.getUserId()+"&timestamp="+todayDate.getTimeInMillis()+"" +
                "&pageNumber="+HomeScreenDto.calendarTabPageNumber+"&offSet="+HomeScreenDto.offsetToFetchData+"";
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_homescreen_events_v2);
        asyncTaskDto.setQueryStr(queryStr);
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        getAsyncDto(asyncTaskDto, HomeScreenDto.HomeScreenAPICall.Home);
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

        if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {
            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        shimmerViewOnscroll.setVisibility(View.GONE);
                        shimmerViewOnscroll.hideShimmer();

                        boolean success = false;

                        if( response != null) {

                            success = response.getBoolean("success");
                        }

                        if (success) {

                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<List<Event>>() {
                            }.getType();
                            final List<Event> events = gson.fromJson(response.getJSONArray("data").toString(), listType);

                            if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.PastEvents)) {

                                if (HomeScreenDto.calendarTabPageNumber == 0) {
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            homeScreenDto.setHomeEvents(new ArrayList<Event>());
                                            eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.HOME.toString());
                                            eventManagerImpl.addEvent(events, Event.EventDisplayScreen.HOME.toString());
                                        }
                                    });
                                }

                                homeScreenDto.setPastEvents(events);
                                processCalendarDotEvents(events);

                                System.out.println("ProcessPrevious CalendarTabData Called");
                                processCalendarTabData(events, true);
                                loadCalendarTabData();

                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Home)) {

                                HomeScreenDto.totalCalendarDataCounts = response.getInt("totalCounts");
                                System.out.println("Total Calendar Data Counts : "+HomeScreenDto.totalCalendarDataCounts);

                                //If there are no post Events and the current Events List is also empty
                                //Then we will hide shimer and show message
                                if (HomeScreenDto.calendarTabPageNumber == 0 && events.size() == 0 && homeScreenDto.getPastEvents().size() == 0) {

                                    elvHomeListView.setVisibility(View.GONE);
                                    rlNoGatheringText.setVisibility(View.VISIBLE);
                                    shimmerFrameLayout.hideShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);

                                } else {

                                    rlNoGatheringText.setVisibility(View.GONE);
                                    processCalendarDotEvents(events);

                                    //Saving Data Locally
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            eventManagerImpl.addEvent(events, Event.EventDisplayScreen.HOME.toString());
                                        }
                                    });

                                    List<Event> previousEvents = homeScreenDto.getHomeEvents();
                                    previousEvents.addAll(events);
                                    homeScreenDto.setHomeEvents(previousEvents);

                                    System.out.println("Previoous Events Size : "+previousEvents.size());
                                    System.out.println("Home Events Size : "+homeScreenDto.getHomeEvents());
                                    if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
                                        System.out.println("ProcessCalendarTabData Called");
                                        processCalendarTabData(events, false);
                                    }
                                    HomeScreenDto.calendarTabPageNumber += 20;
                                }
                                HomeScreenDto.madeApiCall = true;
                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Accepted)) {
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.ACCEPTED.toString());
                                        eventManagerImpl.addEvent(events, Event.EventDisplayScreen.ACCEPTED.toString());
                                    }
                                });
                                homeScreenDto.setAcceptedEvents(events);
                                //invitationTabPressed();
                                if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Invitation)) {
                                    if (homeScreenDto.getInvitationTabSelected().equals(HomeScreenDto.InvitationTabs.Accepted)) {
                                        processInvitationEvents(homeScreenDto.getAcceptedEvents());
                                    }
                                }
                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Pending)) {

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.PENDING.toString());
                                        eventManagerImpl.addEvent(events, Event.EventDisplayScreen.PENDING.toString());

                                    }
                                });

                                homeScreenDto.setPendingEvents(events);
                                //invitationTabPressed();
                                if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Invitation)) {
                                    if (homeScreenDto.getInvitationTabSelected().equals(HomeScreenDto.InvitationTabs.Pending)) {
                                        processInvitationEvents(homeScreenDto.getPendingEvents());
                                    }
                                }
                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Declined)) {

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.DECLINED.toString());
                                        eventManagerImpl.addEvent(events, Event.EventDisplayScreen.DECLINED.toString());
                                    }
                                });
                                homeScreenDto.setDeclinedEvents(events);
                                //invitationTabPressed();
                                if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Invitation)) {
                                    if (homeScreenDto.getInvitationTabSelected().equals(HomeScreenDto.InvitationTabs.Declined)) {
                                        processInvitationEvents(homeScreenDto.getDeclinedEvents());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        }
    }

    public void getSyncCallAsyncCall(AsyncTaskDto asyncTaskDto, final SyncCallFor syncCallFor) {

        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                if (syncCallFor.equals(SyncCallFor.Google)) {
                    calendarRefreshed.put(SyncCallFor.Google, true);
                }
                if (syncCallFor.equals(SyncCallFor.Outlook)) {
                    calendarRefreshed.put(SyncCallFor.Outlook, true);
                }

                if (calendarRefreshed.size() == 2) {
                    calendarRefreshed = new HashMap<>();
                    tvSyncCalToastText.setText("Done");
                    ivIosSpinner.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llSyncCalToast.setVisibility(View.GONE);

                        }
                    }, 1000);
                    loadCalendarTabData();
                }
            }
        }).execute(asyncTaskDto);
    }


    public void processCalendarTabData(List<Event> events, boolean pastEvents){

        System.out.println(events.size());
        List<String> headers = homeScreenDto.getHomeDataHeaders() == null ? new ArrayList<String>() : homeScreenDto.getHomeDataHeaders();
        List<Long> headersTimstamp = new ArrayList<>();

        Calendar currentDateCal = Calendar.getInstance();

        //Creating Data List for Home Screen
        Map<String,List<Event>> mapListEvent = homeScreenDto.getHomeDataListMap() == null ? new HashMap<String, List<Event>>() : homeScreenDto.getHomeDataListMap();
        if (events != null) {

            for (Event event : events) {
                try {
                    String headerTitle = "";

                    Calendar eventCal = Calendar.getInstance();
                    eventCal.setTimeInMillis(event.getStartTime());
                    if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                        headerTitle = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()));
                    } else {
                        headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(event.getStartTime()));
                    }
                    eventCal = null;

                    if (mapListEvent.containsKey(headerTitle)) {
                        List<Event> eventList = mapListEvent.get(headerTitle);
                        eventList.add(event);
                        mapListEvent.put(headerTitle, eventList);
                    } else {
                        List<Event> eventList = new ArrayList<>();
                        eventList.add(event);
                        mapListEvent.put(headerTitle, eventList);
                        headersTimstamp.add(event.getStartTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Creating Headers for Home Screen
        Collections.sort(headersTimstamp);
        for (Long timestamp : headersTimstamp){
            String headerTitle = "";

            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(timestamp);
            if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                headerTitle = CenesUtils.EEEMMMMdd.format(new Date(timestamp));
            } else {
                headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(timestamp));
            }
            eventCal = null;

            boolean headerExist = false;
            for (String header: headers) {
                if (header.equals(headerTitle)) {
                    headerExist = true;
                }
            }
            if (!headerExist) {
                headers.add(headerTitle);
            }
        }

        //Lets check the position of the first event group to be added.
        if (pastEvents == false && HomeScreenDto.calendarTabPageNumber == 0) {

            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return o1.getStartTime() < o2.getStartTime() ? -1 : 1;
                }
            });

            String headerTitle = "";
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(events.get(0).getStartTime());
            if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                headerTitle = CenesUtils.EEEMMMMdd.format(new Date(events.get(0).getStartTime()));
            } else {
                headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(events.get(0).getStartTime()));
            }
            eventCal = null;
            for (int i=0; i<headers.size(); i++) {
                if (headerTitle.equals(headers.get(i))) {
                    HomeScreenDto.currentDateGroupPosition = i;
                    break;
                }
            }
        }


        if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
            //Lets add Month Separator At Home Screen Events
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

                        String monthTitle = CenesUtils.MMMM_yyyy.format(new Date(currentListFirstEvent.getStartTime()));


                        //Lets make it at the end of the day
                        Calendar monthSeparatorCal = Calendar.getInstance();
                        monthSeparatorCal.setTimeInMillis(previoustListFirstEvent.getStartTime());
                        monthSeparatorCal.set(Calendar.HOUR_OF_DAY, 23);
                        monthSeparatorCal.set(Calendar.MINUTE, 59);
                        monthSeparatorCal.set(Calendar.SECOND, 59);
                        monthSeparatorCal.set(Calendar.MILLISECOND, 59);

                        //This case will happen, when we have 30 events and pageable evnts are 0,20.
                        //In this case we will already have MonthSeparator and the month separator
                        //Lets find out if the month separator already exists in the list
                        //then we will remove it and add the new one at the end.
                        boolean monthSeparatorExists = false;
                        for (Event lastEvent: previousListEvents) {
                            if (lastEvent.getScheduleAs().equals("MonthSeparator")) {
                                monthSeparatorExists = true;
                            }
                        }

                        if (!monthSeparatorExists) {
                            //Lets create new event
                            Event event = new Event();
                            event.setScheduleAs("MonthSeparator");
                            event.setTitle(monthTitle);
                            event.setStartTime(monthSeparatorCal.getTimeInMillis());
                            previousListEvents.add(event);
                            mapListEvent.put(headers.get(i-1), previousListEvents);


                            HomeScreenDto.homeListGroupAndMonthHolder.put(monthTitle, i);
                        }
                    }
                } else{

                }
            }
        }

        homeScreenDto.setHomeDataHeaders(headers);
        homeScreenDto.setHomeDataListMap(mapListEvent);

        //PAST EVENTS CALL
        //For Past Events we will initialize the adapter.
        if (pastEvents == true && HomeScreenDto.calendarTabPageNumber == 0) {
            calendarTabExpandableListAdapter = new CalendarTabExpandableListAdapter(this, homeScreenDto);
            elvHomeListView.setAdapter(calendarTabExpandableListAdapter);

        } else {

            //For Current and future events we will just notify the Adapter for
            //Any Change
            if (calendarTabExpandableListAdapter != null) {
                calendarTabExpandableListAdapter.notifyDataSetChanged();
            }
        }

        //This is when the past events and current date events are fetched..
        //Then we will hide shimmer and scroll home screen to current date..
        if (pastEvents == false && HomeScreenDto.calendarTabPageNumber == 0) {
            elvHomeListView.post(new Runnable() {
                @Override
                public void run() {

                    shimmerFrameLayout.hideShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
                        elvHomeListView.setVisibility(View.VISIBLE);
                        homeButtonPressed();
                    }
                }
            });
        }
    }

    public void processInvitationEvents(List<Event> invitationEvents) {

        List<String> headers = new ArrayList<String>();
        List<Long> headersTimstamp = new ArrayList<>();
        Calendar currentDateCal = Calendar.getInstance();

        Map<String,List<Event>> mapListEvent = new HashMap<String, List<Event>>();
        if (invitationEvents != null) {

            for (Event event : invitationEvents) {
                try {

                    String headerTitle = "";
                    Calendar eventCal = Calendar.getInstance();
                    eventCal.setTimeInMillis(event.getStartTime());
                    if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                        headerTitle = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()));
                    } else {
                        headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(event.getStartTime()));
                    }
                    eventCal = null;
                    if (mapListEvent.containsKey(headerTitle)) {
                        List<Event> eventList = mapListEvent.get(headerTitle);
                        eventList.add(event);
                        mapListEvent.put(headerTitle, eventList);
                    } else {
                        List<Event> eventList = new ArrayList<>();
                        eventList.add(event);
                        mapListEvent.put(headerTitle, eventList);
                        headersTimstamp.add(event.getStartTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Collections.sort(headersTimstamp);
        for (Long timestamp : headersTimstamp){
            String headerTitle = "";
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(timestamp);
            if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                headerTitle = CenesUtils.EEEMMMMdd.format(new Date(timestamp));
            } else {
                headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(timestamp));
            }
            eventCal = null;
            boolean headerExist = false;
            for (String header: headers) {
                if (header.equals(headerTitle)) {
                    headerExist = true;
                }
            }
            if (!headerExist) {
                headers.add(headerTitle);
            }
        }

        homeScreenDto.setInvitaitonDataHeaders(headers);
        homeScreenDto.setInvitationDataListMap(mapListEvent);
        invitationListItemAdapter = new InvitationListItemAdapter(this, homeScreenDto);
        elvInvitationListView.setAdapter(invitationListItemAdapter);
    }


    private void processCalendarDotEvents(List<Event> calendarDotEvents){

        Set<CalendarDay> calendarDays = new HashSet<>();
        for(Event event : calendarDotEvents){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getStartTime());
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            CalendarDay calendarDay = new CalendarDay(year, month, dayOfMonth);
            calendarDays.add(calendarDay);
        }
        mcvHomeCalendar.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarDays));

    }

    private void firstTimeLoadData(){
        List<Event> homeEvents = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.HOME.toString());
        if(homeEvents.size() == 0){
            if (internetManager.isInternetConnection(getCenesActivity())) {
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
            }
        } else{
            processCalendarTabData(homeEvents, false);
            homeScreenDto.setHomeEvents(homeEvents);
        }
        List<Event> acceptedEvents = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.ACCEPTED.toString());
        homeScreenDto.setAcceptedEvents(acceptedEvents);

        List<Event> pendingEvents = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.PENDING.toString());
        homeScreenDto.setPendingEvents(pendingEvents);

        List<Event> declinedEvents = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.DECLINED.toString());
        homeScreenDto.setDeclinedEvents(declinedEvents);
    }


}
