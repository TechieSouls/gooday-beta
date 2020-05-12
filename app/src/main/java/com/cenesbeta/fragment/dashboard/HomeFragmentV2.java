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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.CalendarTabExpandableListAdapter;
import com.cenesbeta.adapter.CalendarTabListViewAdapter;
import com.cenesbeta.adapter.InvitationListItemAdapter;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.api.HomeScreenAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
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
import com.cenesbeta.materialcalendarview.OnDateSelectedListener;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragmentV2 extends CenesFragment {

    public static String TAG = "HomeFragmentV2";
    public enum SyncCallFor {Google, Outlook, Contacts}

    private TextView tvCalendarTab, tvInvitationTab, tvCalDate, tvConfirmedBtn, tvPendingBtn, tvDeclinedBtn, tvSyncCalToastText;
    private ImageView ivPlusBtn, ivCalDateBarArrow, ivRefreshCalsBtn, ivIosSpinner;
    private LinearLayout llCalendarDateBar, llInvitationTabView, llSyncCalToast, llSwitchableHeadres;
    private MaterialCalendarView mcvHomeCalendar;
    private ExpandableListView elvHomeListView;
    private ExpandableListView elvInvitationListView;
    private ListView lvHomeListView;
    private View homeFragementView;
    private RelativeLayout rlNoGatheringText;
    private Button btCreateGathering, btSyncCalendar;
    private SwipeRefreshLayout swiperefreshInvitations;


    private HomeScreenDto homeScreenDto;
    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    public  User loggedInUser;
    private CalendarTabExpandableListAdapter calendarTabExpandableListAdapter;
    private CalendarTabListViewAdapter calendarTabListViewAdapter;
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
        System.out.println("onCreateView Called");

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
        llSwitchableHeadres = (LinearLayout) view.findViewById(R.id.ll_switchable_headres);

        rlNoGatheringText = (RelativeLayout) view.findViewById(R.id.rl_no_gathering_text);
        btCreateGathering = (Button) view.findViewById(R.id.bt_create_gathering);
        btSyncCalendar = (Button) view.findViewById(R.id.bt_sync_calendar);

        mcvHomeCalendar = (MaterialCalendarView) view.findViewById(R.id.mcv_home_calendar);
        elvHomeListView = (ExpandableListView) view.findViewById(R.id.elv_home_list_view);
        elvInvitationListView = (ExpandableListView)view.findViewById(R.id.elv_invitation_list_view);
        lvHomeListView = (ListView) view.findViewById(R.id.lv_home_list_view);
        swiperefreshInvitations = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_invitations);

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
        //elvHomeListView.setOnScrollListener(calendarTabListScrollListener);
        lvHomeListView.setOnScrollListener(calendarTabListScrollListener);
        mcvHomeCalendar.setOnDateChangedListener(onDateSelectedListener);
        swiperefreshInvitations.setOnRefreshListener(swipeDownInvitationListener);

        //Java Variables
        homeScreenDto = new HomeScreenDto();
        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = ((CenesBaseActivity)getActivity()).getCenesApplication().getCoreManager();
        internetManager = coreManager.getInternetManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        eventManagerImpl = new EventManagerImpl(cenesApplication);
        //elvHomeListView.setNestedScrollingEnabled(true);

        mcvHomeCalendar.sourceFragment = this;
        mcvHomeCalendar.setCurrentDate(new Date());
        //When internet connection is off/first time loading...
        //eventManagerImpl.deleteAllEvents();
        firstTimeLoadData();
        if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            System.out.println("Inside On create View");
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

        //Post Offline events to Server
        if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            System.out.println("On Resume Called");
            uploadOfflineEvents();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("On Pause Called");
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Broadcaster Processing");
            //do other stuff here
            loadHomeScreenData();

            Fragment visibleFragment = ((CenesBaseActivity) getActivity()).getVisibleFragment();
            System.out.println("Visible Fragment.... : "+visibleFragment);

            if(visibleFragment instanceof GatheringPreviewFragment) {
                System.out.println("Visible Fragment.... : ");
                ((GatheringPreviewFragment) visibleFragment).getChatThread();
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener swipeDownInvitationListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
                homeScreenDto.setInvitationDataListMap(new HashMap<String, List<Event>>());
                homeScreenDto.setInvitaitonDataHeaders(new ArrayList<String>());
                homeScreenDto.setAcceptedEvents(new ArrayList<Event>());
                homeScreenDto.setPendingEvents(new ArrayList<Event>());
                homeScreenDto.setDeclinedEvents(new ArrayList<Event>());

                invitationListItemAdapter = null;

                loadInvitationTabData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperefreshInvitations.setRefreshing(false);
                    }
                }, 2000);
            } else {
                swiperefreshInvitations.setRefreshing(false);
            }
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

    ListView.OnScrollListener calendarTabListScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            try {
                //Algorithm to check if the last item is visible or not
                final int lastItem = firstVisibleItem + visibleItemCount;
                //System.out.println("lastItem : "+lastItem+", firstVisibleItem : "+firstVisibleItem+", "+visibleItemCount+", Past Events : "+homeScreenDto.getPastEvents().size()+", Headers Size : "+homeScreenDto.getHomeDataHeaders().size());

                //System.out.println("getItemAtPosition : "+(view.getItemAtPosition(firstVisibleItem).toString()));
                if (view.getItemAtPosition(firstVisibleItem) instanceof Event) {

                    Event visibleEvent = (Event)view.getItemAtPosition(firstVisibleItem);
                    System.out.println("Visible Event : Title : "+visibleEvent.getTitle()+", " +new Date(visibleEvent.getStartTime()));
                    Long eventTime = new Date().getTime();
                    if (visibleEvent.getScheduleAs().equals(Event.EventScheduleAs.MonthSeparator.toString())) {
                        eventTime = visibleEvent.getMonthSeparatorTimestamp();
                    } else {
                        eventTime = visibleEvent.getStartTime();
                    }

                    Calendar currentDateCal = Calendar.getInstance();
                    Calendar eventDateCal = Calendar.getInstance();
                    eventDateCal.setTimeInMillis(eventTime);
                    homeScreenDto.calendarPageDate = eventDateCal.getTime();

                    System.out.println("Calendar Label : "+CenesUtils.MMMM.format(new Date(eventTime)));

                    if (currentDateCal.get(Calendar.YEAR) == eventDateCal.get(Calendar.YEAR)) {
                        System.out.println("IF updateCalendarLabelDate");
                        updateCalendarLabelDate(CenesUtils.MMMM.format(new Date(eventTime)));
                    } else {
                        System.out.println("ELSE updateCalendarLabelDate");
                        updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(new Date(eventTime)));
                    }
                }

                //System.out.println("firstVisibleItem , visibleItemCount, totalItemCount : "+firstVisibleItem+" -- "+visibleItemCount+" -- "+totalItemCount);
                if(lastItem == totalItemCount && totalItemCount != 0) {
                    // you have reached end of list, load more data

                    System.out.println("Home Screen Events Size : "+homeScreenDto.getHomeEvents().size()+", Total Counts : "+HomeScreenDto.totalCalendarDataCounts);
                    if (homeScreenDto.getHomeEvents().size() < HomeScreenDto.totalCalendarDataCounts) {
                        if (HomeScreenDto.madeApiCall) {
                            //shimmerViewOnscroll.setVisibility(View.VISIBLE);
                            //shimmerViewOnscroll.showShimmer(true);
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

    OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            try {
                if (homeScreenDto.getHomeDataHeaders() != null && homeScreenDto.getHomeDataHeaders().size() > 0 &&
                        homeScreenDto.getHomeDataListMap() != null && homeScreenDto.getHomeDataListMap().size() > 0) {

                    Calendar currentDateCal = Calendar.getInstance();

                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.setTime(date.getDate());

                    String headerTitle = "";
                    if (currentDateCal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)) {
                        headerTitle = CenesUtils.EEEMMMMdd.format(new Date(selectedDate.getTimeInMillis()));
                    } else {
                        headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(selectedDate.getTimeInMillis()));
                    }

                    if (homeScreenDto.getHomeDataHeaders().contains(headerTitle)) {

                        int groupPosition = homeScreenDto.getHomeDataHeaders().indexOf(headerTitle);
                        int listViewScrollPosition = 0;
                        for (int i=0; i < groupPosition; i++) {
                            if (i < homeScreenDto.getHomeDataHeaders().size() &&
                                    homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)) != null) {
                                listViewScrollPosition += 1 + homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)).size();
                            }                        }
                        lvHomeListView.smoothScrollToPositionFromTop(listViewScrollPosition, 0, 200);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onCalendarPageChangeListener(CalendarDay currentPage) {

        if (mcvHomeCalendar.getVisibility() == View.VISIBLE) {
            if (homeScreenDto.getHomeDataListMap() != null && homeScreenDto.getHomeDataHeaders() != null &&
                    homeScreenDto.getHomeDataListMap().size() > 0 && homeScreenDto.getHomeDataHeaders().size() > 0) {

                String monthTitle = CenesUtils.MMMM_yyyy.format(currentPage.getDate());
                System.out.println("Current Month : "+monthTitle);
                if (HomeScreenDto.homeListGroupAndMonthHolder.containsKey(monthTitle)) {
                    int groupPosition = HomeScreenDto.homeListGroupAndMonthHolder.get(monthTitle);
                    int listViewScrollPosition = 0;
                    for (int i=0; i < groupPosition; i++) {
                        if (i < homeScreenDto.getHomeDataHeaders().size() && homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)) != null) {
                            listViewScrollPosition += 1 + homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)).size();
                        }
                    }
                    lvHomeListView.smoothScrollToPositionFromTop(listViewScrollPosition, 0, 100);
                } else {
                    //elvHomeListView.smoothScrollToPosition(0);

                    Calendar currentDate = Calendar.getInstance();

                    if (currentPage.getCalendar().get(Calendar.MONTH) < currentDate.get(Calendar.MONTH)
                    && currentPage.getCalendar().get(Calendar.YEAR) <= currentDate.get(Calendar.YEAR)) {
                        lvHomeListView.smoothScrollToPositionFromTop(0, 0, 100);
                    } else {

                        /*if (HomeScreenDto.calendarPreviousPage.getCalendar() != null) {
                            Calendar topCalendar = HomeScreenDto.calendarPreviousPage.getCalendar();

                            if (topCalendar.get(Calendar.MONTH) < currentPage.getCalendar().get(Calendar.MONTH)
                                    && topCalendar.get(Calendar.YEAR) <= currentPage.getCalendar().get(Calendar.YEAR)) {
                                int listViewScrollPosition = 0;
                                for (int i=0; i < homeScreenDto.getHomeDataHeaders().size(); i++) {
                                    if (i < homeScreenDto.getHomeDataHeaders().size() && homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)) != null) {
                                        listViewScrollPosition += 1 + homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(i)).size();
                                    }
                                }
                                lvHomeListView.smoothScrollToPositionFromTop(listViewScrollPosition, 0, 100);

                            }
                        }*/
                        loadCalendarTabData();

                    }
                }
                final CalendarDay currentPageTemp = currentPage;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Calendar currentYearCal = Calendar.getInstance();
                        if (currentYearCal.get(Calendar.YEAR) == currentPageTemp.getCalendar().get(Calendar.YEAR)) {
                            System.out.println("Stepxx_1 : "+currentPageTemp.getDate());
                            updateCalendarLabelDate(CenesUtils.MMMM.format(currentPageTemp.getDate()));
                        } else {
                            System.out.println("Stepxx_2 : ");
                            updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(currentPageTemp.getDate()));
                        }
                    }
                }, 500);
                HomeScreenDto.calendarPreviousPage = currentPage;
            }
        }
    }

    public void updateCalendarLabelDate(String headerCalTitle) {
        System.out.println("updateCalendarLabelDate  called ******* " + headerCalTitle);
        System.out.println("tvCalDate updateCalendarLabelDate "+ headerCalTitle);
        tvCalDate.setText(headerCalTitle);
    }

    public void calendarTabPressed() {

        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Calendar);
        tvCalendarTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvInvitationTab.setBackground(null);
        //elvInvitationListView.scrollTo(0, 0);

        llInvitationTabView.setVisibility(View.GONE);
        llSwitchableHeadres.setVisibility(View.GONE);

        if (homeScreenDto.getHomeEvents().size() == 0 && homeScreenDto.getPastEvents().size() == 0) {
            rlNoGatheringText.setVisibility(View.VISIBLE);
            lvHomeListView.setVisibility(View.GONE);
        }else{
            rlNoGatheringText.setVisibility(View.GONE);
            lvHomeListView.setVisibility(View.VISIBLE);
        }
        //processCalendarTabData(homeScreenDto.getHomeEvents(), false);
        //elvHomeListView.setVisibility(View.VISIBLE);
    }

    public void invitationTabPressed() {

        System.out.println("tvCalDate invitationTabPressed " + CenesUtils.MMMM.format(new Date()));
        tvCalDate.setText(CenesUtils.MMMM.format(new Date()));
        homeScreenDto.setTabSelected(HomeScreenDto.HomeTabs.Invitation);
        tvInvitationTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvCalendarTab.setBackground(null);
        //elvHomeListView.scrollTo(0, 0);
        //calendarTabExpandableListAdapter = null;
        //elvHomeListView.setVisibility(View.GONE);
        lvHomeListView.setVisibility(View.GONE);
        rlNoGatheringText.setVisibility(View.GONE);
        processInvitationEvents(homeScreenDto.getAcceptedEvents());
        llInvitationTabView.setVisibility(View.VISIBLE);
        llSwitchableHeadres.setVisibility(View.VISIBLE);
        highlightInvitationTabs(tvConfirmedBtn);

        homeButtonPressed();
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
            mcvHomeCalendar.setCurrentDate(homeScreenDto.calendarPageDate);
            final Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(homeScreenDto.calendarPageDate.getTime());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvCalDate.setText(CenesUtils.MMMM.format(calendar.getTime()));
                    System.out.println("tvCalDate calendarDateBarPressed " + CenesUtils.MMMM.format(calendar.getTime()) );
                }
            }, 100);


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

        //elvHomeListView.setSelectedGroup(HomeScreenDto.currentDateGroupPosition);
        /*int position = 0;
        for (int i=0; i < HomeScreenDto.currentDateGroupPosition; i++) {
            position = position + (calendarTabExpandableListAdapter.getChildrenCount(i));

        }

        elvHomeListView.smoothScrollToPositionFromTop(position, 0, 500);*/
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(HomeScreenDto.currentDateGroupPositionTimestamp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvCalDate.setText(CenesUtils.MMMM.format(calendar.getTime()));
                System.out.println("tvCalDate homeButtonPressed " + CenesUtils.MMMM.format(calendar.getTime()));
            }
        }, 700);

        lvHomeListView.setSelection(HomeScreenDto.currentDateGroupPosition);
        //lvHomeListView.smoothScrollToPositionFromTop(HomeScreenDto.currentDateGroupPosition, 0, 50);
        mcvHomeCalendar.setCurrentDate(calendar.getTime());

        /*String groupStr = homeScreenDto.getHomeDataHeaders().get(HomeScreenDto.currentDateGroupPosition);
        Event event = homeScreenDto.getHomeDataListMap().get(groupStr).get(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getStartTime());
        mcvHomeCalendar.setCurrentDate(calendar.getTime());*/
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

    public void removeCalendarEvents(Event event) {

        if (!internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            return;
        }
        List<Event> events = null;
        if (CenesUtils.isEmpty(event.getRecurringEventId())) {
            events = eventManagerImpl.findAllEventsByTitleAndSource(event.getTitle(), event.getSource());
        } else {
            events = eventManagerImpl.findAllEventsByRecurringEventId(event.getRecurringEventId());
        }

        Map<String, List<Event>> homeDataListMap = homeScreenDto.getHomeDataListMap();

        for (Event recurringEvent: events) {
            String key = "";
            Calendar calendar = Calendar.getInstance();
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(recurringEvent.getStartTime());
            if (calendar.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                key = CenesUtils.EEEMMMMdd.format(recurringEvent.getStartTime());
            } else {
                key = CenesUtils.EEEMMMMddcmyyyy.format(recurringEvent.getStartTime());
            }
            List<Event> eventList = homeDataListMap.get(key);
            if (eventList != null) {

                //Lets delete this event from list view data
                for (Event evenToDelete: eventList) {

                    if (evenToDelete.getEventId().equals(recurringEvent.getEventId())) {
                        eventList.remove(evenToDelete);
                        List<Object> listViewWithHeaders = homeScreenDto.getHomelistViewWithHeaders();
                        listViewWithHeaders.remove(evenToDelete);
                        homeScreenDto.setHomelistViewWithHeaders(listViewWithHeaders);
                        HomeScreenDto.totalCalendarDataCounts--;
                        break;
                    }
                }

                //if  events has
                if (eventList.size() == 0) {
                    homeDataListMap.remove(key);
                    List<String> tableHeaders = homeScreenDto.getHomeDataHeaders();
                    tableHeaders.remove(key);
                    List<Object> listViewWithHeaders = homeScreenDto.getHomelistViewWithHeaders();
                    listViewWithHeaders.remove(key);
                    homeScreenDto.setHomelistViewWithHeaders(listViewWithHeaders);
                    homeScreenDto.setHomeDataHeaders(tableHeaders);
                } else {
                    homeDataListMap.put(key, eventList);
                }
            }
            List<Event> homeEvents = homeScreenDto.getHomeEvents();
            for (Event homeEve: homeEvents) {
                if (homeEve.getEventId().equals(recurringEvent.getEventId())) {
                    homeEvents.remove(homeEve);
                    break;
                }
            }
            homeScreenDto.setHomeEvents(homeEvents);
        }

        //homeScreenDto.setHomeDataListMap(homeDataListMap);

        homeScreenDto.setHomeDataHeaders(new ArrayList<String>());
        homeScreenDto.setHomeDataListMap(new HashMap<String, List<Event>>());

        List<Event> newListToIterate = new ArrayList<>();
        newListToIterate.addAll(homeScreenDto.getPastEvents());
        newListToIterate.addAll(homeScreenDto.getHomeEvents());
        processCalendarTabData(newListToIterate, false);

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setQueryStr("event_id="+event.getEventId());
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_delete_event_api);
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                HomeScreenDto.totalCalendarDataCounts--;
            }
        }).execute(asyncTaskDto);
    }
    public void addOrRejectEvent(Event event, String status) {
        if (!internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            return;
        }
         List<Event> dbEvents = this.eventManagerImpl.findAllEventsByEventId(event.getEventId());
         if (dbEvents != null && dbEvents.size() > 0) {

             if (status.equals("NotGoing")) {
                 AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                 asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
                 asyncTaskDto.setQueryStr("eventId="+ event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing");
                 asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+GatheringAPI.get_update_invitation_api);

                 new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                     @Override
                     public void processFinish(JSONObject response) {
                         HomeScreenDto.totalCalendarDataCounts--;
                     }
                 }).execute(asyncTaskDto);
             } else if (status.equals("Going")) {

                 AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                 asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
                 asyncTaskDto.setQueryStr("eventId="+ event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going");
                 asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+GatheringAPI.get_update_invitation_api);

                 new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                     @Override
                     public void processFinish(JSONObject response) {
                         HomeScreenDto.totalCalendarDataCounts++;
                     }
                 }).execute(asyncTaskDto);
             } else if (status.equals("Delete")) {
                 AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
                 asyncTaskDto.setQueryStr("event_id="+event.getEventId());
                 asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_delete_event_api);
                 asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

                 new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                     @Override
                     public void processFinish(JSONObject response) {

                         HomeScreenDto.totalCalendarDataCounts--;
                     }
                 }).execute(asyncTaskDto);
             }
            for (Event dbEvent: dbEvents) {

                //Lets remove this event from database
                this.eventManagerImpl.deleteEventByEventId(dbEvent.getEventId());

                //If the Event was in Db and user declined it.
                //We will remove from Accepted List and add it to declined list
                if (status.equals("NotGoing") || status.equals("Delete") || status.equals("Refresh")) {

                    if (dbEvent.getDisplayAtScreen().equals(Event.EventDisplayScreen.ACCEPTED.toString())) {
                        List<Event> acceptedEvents = homeScreenDto.getAcceptedEvents();
                        for (Event acceptedEvent: acceptedEvents) {
                            if (acceptedEvent.getEventId().equals(dbEvent.getEventId())) {
                                acceptedEvents.remove(acceptedEvent);
                                break;
                            }
                        }
                        homeScreenDto.setAcceptedEvents(acceptedEvents);
                        processInvitationEvents(homeScreenDto.getAcceptedEvents());

                    } else if (dbEvent.getDisplayAtScreen().equals(Event.EventDisplayScreen.PENDING.toString())) {
                        List<Event> pendingEvents = homeScreenDto.getPendingEvents();
                        for (Event pendingEvent: pendingEvents) {
                            if (pendingEvent.getEventId().equals(dbEvent.getEventId())) {
                                pendingEvents.remove(pendingEvent);
                                break;
                            }
                        }
                        homeScreenDto.setPendingEvents(pendingEvents);
                        processInvitationEvents(homeScreenDto.getPendingEvents());
                    } else if (dbEvent.getDisplayAtScreen().equals(Event.EventDisplayScreen.HOME.toString())) {

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

                        if (homeDataListMap != null) {

                            List<Event> eventList = homeDataListMap.get(key);
                            if (eventList != null) {
                                for (Event evenToDelete: eventList) {
                                    if (evenToDelete.getEventId().equals(event.getEventId())) {
                                        eventList.remove(event);
                                        List<Object> listViewWithHeaders = homeScreenDto.getHomelistViewWithHeaders();
                                        listViewWithHeaders.remove(event);
                                        homeScreenDto.setHomelistViewWithHeaders(listViewWithHeaders);
                                        break;
                                    }
                                }
                                if (eventList.size() == 0) {
                                    homeDataListMap.remove(key);
                                    List<String> tableHeaders = homeScreenDto.getHomeDataHeaders();
                                    tableHeaders.remove(key);
                                    List<Object> listViewWithHeaders = homeScreenDto.getHomelistViewWithHeaders();
                                    listViewWithHeaders.remove(key);
                                    homeScreenDto.setHomelistViewWithHeaders(listViewWithHeaders);
                                    homeScreenDto.setHomeDataHeaders(tableHeaders);
                                } else {
                                    homeDataListMap.put(key, eventList);
                                }
                            }
                            homeScreenDto.setHomeDataListMap(homeDataListMap);
                            List<Event> homeEvents = homeScreenDto.getHomeEvents();
                            homeEvents.remove(event);
                            homeScreenDto.setHomeEvents(homeEvents);

                            List<Object> listView = new ArrayList<>();
                            for (String str: homeScreenDto.getHomeDataHeaders()) {
                                listView.add(str);
                                List<Event> eventListTemp = homeScreenDto.getHomeDataListMap().get(str);
                                listView.addAll(eventListTemp);
                            }
                            homeScreenDto.setHomelistViewWithHeaders(listView);
                            //calendarTabExpandableListAdapter.notifyDataSetChanged();
                            if (calendarTabListViewAdapter != null) {
                                calendarTabListViewAdapter.notifyDataSetChanged();
                            } else {

                                calendarTabListViewAdapter =  new CalendarTabListViewAdapter(this, homeScreenDto);
                                lvHomeListView.setAdapter(calendarTabListViewAdapter);

                                lvHomeListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        homeButtonPressed();
                                    }
                                });
                            }                        }
                    }

                } else if (status.equals("Going")) {

                    //Lets check if the event was under Declined List
                    //Remove it from declined list
                    if (dbEvent.getDisplayAtScreen().equals(Event.EventDisplayScreen.DECLINED.toString())) {
                        List<Event> declinedEvents = homeScreenDto.getDeclinedEvents();
                        for (Event declineddEvent: declinedEvents) {
                            if (declineddEvent.getEventId().equals(dbEvent.getEventId())) {
                                declinedEvents.remove(declineddEvent);
                                break;
                            }
                        }
                        homeScreenDto.setDeclinedEvents(declinedEvents);
                        processInvitationEvents(homeScreenDto.getDeclinedEvents());
                    } else if (dbEvent.getDisplayAtScreen().equals(Event.EventDisplayScreen.PENDING.toString())) {
                        List<Event> pendingEvents = homeScreenDto.getPendingEvents();
                        for (Event pendingEvent: pendingEvents) {
                            if (pendingEvent.getEventId().equals(dbEvent.getEventId())) {
                                pendingEvents.remove(pendingEvent);
                                break;
                            }
                        }
                        homeScreenDto.setPendingEvents(pendingEvents);
                        processInvitationEvents(homeScreenDto.getPendingEvents());
                    }
                }
            }
             addEventLocally(event, status);
        }
    }

    public void addEventLocally(Event event, String status) {

        List<EventMember> eventMembers = event.getEventMembers();
        if (eventMembers != null && eventMembers.size() > 0) {

            EventMember loggedInUserAsMember = null;
            for (EventMember eventMember: eventMembers) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    loggedInUserAsMember = eventMember;
                    if (status.equals("Going")) {
                        eventMember.setStatus("Going");

                    } else if (status.equals("NotGoing")) {

                        eventMember.setStatus("NotGoing");
                    }
                }
            }
        }
        if (status.equals("Going")) {

            event.setDisplayAtScreen(Event.EventDisplayScreen.ACCEPTED.toString());
            eventManagerImpl.addEvent(event);

            //Lets refresh the accepted list
            List<Event> acceptedEvents = homeScreenDto.getAcceptedEvents() == null ? (new ArrayList<Event>()) : homeScreenDto.getAcceptedEvents();
            acceptedEvents.add(event);
            homeScreenDto.setAcceptedEvents(acceptedEvents);
            //processInvitationEvents(homeScreenDto.getAcceptedEvents());

            event.setDisplayAtScreen(Event.EventDisplayScreen.HOME.toString());
            eventManagerImpl.addEvent(event);

            //Lets refresh home screen
            List<Event> homeScreenEvents = homeScreenDto.getHomeEvents() == null ? (new ArrayList<Event>()) : homeScreenDto.getHomeEvents();
            homeScreenEvents.add(event);
            homeScreenDto.setHomeEvents(homeScreenEvents);

            List<Event> newEventToAddList = new ArrayList<>();
            newEventToAddList.add(event);
            processCalendarTabData(newEventToAddList, false);

        } else if (status.equals("NotGoing")) {
            event.setDisplayAtScreen(Event.EventDisplayScreen.DECLINED.toString());
            eventManagerImpl.addEvent(event);

            //Lets refresh the Declined list
            List<Event> declinedEvents = homeScreenDto.getDeclinedEvents();
            declinedEvents.add(event);
            homeScreenDto.setDeclinedEvents(declinedEvents);
            //processInvitationEvents(homeScreenDto.getDeclinedEvents());
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

            HomeScreenDto.calendarPreviousPage = null;
            HomeScreenDto.currentDateGroupPositionTimestamp = 0;
            HomeScreenDto.currentDateGroupPosition = 0;
            HomeScreenDto.calendarTabPageNumber = 0;
            HomeScreenDto.totalCalendarDataCounts = 0;
            HomeScreenDto.madeApiCall = false;

            invitationListItemAdapter = null;
            calendarTabListViewAdapter = null;

        loadCalendarTabData();
        loadInvitationTabData();
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
        yesterdayDate.set(Calendar.HOUR_OF_DAY, 23);
        yesterdayDate.set(Calendar.MINUTE, 59);
        yesterdayDate.set(Calendar.SECOND, 59);
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
        acceptedAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=Going&timestamp="+new Date().getTime()+"&pageNumber=0&offSet="+HomeScreenDto.offsetToFetchData);
        acceptedAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(acceptedAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Accepted);

        AsyncTaskDto penidngAsyncTaskDto = new AsyncTaskDto();
        penidngAsyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_gathering_evnets);
        penidngAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=Pending&timestamp="+new Date().getTime()+"&pageNumber=0&offSet="+HomeScreenDto.offsetToFetchData);
        penidngAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(penidngAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Pending);

        AsyncTaskDto declinedAsyncTaskDto = new AsyncTaskDto();
        declinedAsyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl + HomeScreenAPI.get_gathering_evnets);
        declinedAsyncTaskDto.setQueryStr("userId=" + loggedInUser.getUserId() + "&status=NotGoing&timestamp="+new Date().getTime()+"&pageNumber=0&offSet="+HomeScreenDto.offsetToFetchData);
        declinedAsyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        getAsyncDto(declinedAsyncTaskDto, HomeScreenDto.HomeScreenAPICall.Declined);
    }

    public void getAsyncDto(AsyncTaskDto asyncTaskDto, final HomeScreenDto.HomeScreenAPICall homeScreenAPICall){

        if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {
            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        System.out.println("homeScreenAPICall : "+homeScreenAPICall);
                        //shimmerViewOnscroll.setVisibility(View.GONE);
                        //shimmerViewOnscroll.hideShimmer();

                        boolean success = false;

                        if( response != null) {

                            success = response.getBoolean("success");
                        }

                        if (success) {

                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<List<Event>>() {
                            }.getType();
                            List<Event> events = gson.fromJson(response.getJSONArray("data").toString(), listType);

                             if (events != null) {

                                for (Event eventToFix: events) {

                                    List<EventMember> eventMembersAfterFix = new ArrayList<>();
                                    List<Long> eventMemberIdList = new ArrayList<>();
                                    List<Integer> userIdList = new ArrayList<>();

                                    List<EventMember> eventMembsToFix = eventToFix.getEventMembers();
                                    for (EventMember eventMemToFix : eventMembsToFix) {
                                        if (eventMemberIdList.contains(eventMemToFix.getEventMemberId())) {
                                            continue;
                                        }

                                        if (eventMemToFix.getUserId() != null && userIdList.contains(eventMemToFix.getUserId())) {
                                            continue;
                                        }

                                        if (eventMemToFix.getUserId() != null) {
                                            userIdList.add(eventMemToFix.getUserId());
                                        }
                                        eventMemberIdList.add(eventMemToFix.getEventMemberId());
                                        eventMembersAfterFix.add(eventMemToFix);
                                    }
                                    eventToFix.setEventMembers(eventMembersAfterFix);
                                }
                            }

                            if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.PastEvents)) {

                                /*if (HomeScreenDto.calendarTabPageNumber == 0) {
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.HOME.toString());
                                            //eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.HOME.toString());
                                        }
                                    });
                                }*/

                                List<Long> uniqueEventIdList = homeScreenDto.getUniqueEventIdTracker();
                                List<Event> pastEventList = homeScreenDto.getPastEvents();
                                for (Event pastEvent: events) {

                                    if (uniqueEventIdList.contains(pastEvent.getEventId())) {
                                        events.remove(pastEvent);
                                    } else {
                                        uniqueEventIdList.add(pastEvent.getEventId());
                                    }
                                }
                                pastEventList.addAll(events);
                                homeScreenDto.setPastEvents(pastEventList);
                                processCalendarDotEvents(homeScreenDto.getPastEvents());

                                //Saving Data Locally
                                final List<Event> eventsToAdd = events;
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.PAST.toString());
                                    }
                                });

                                System.out.println("ProcessPrevious CalendarTabData Called");
                                processCalendarTabData(homeScreenDto.getPastEvents(), true);

                                if (homeScreenDto.getPastEvents().size() == 0 && homeScreenDto.getHomeEvents().size() == 0) {
                                    rlNoGatheringText.setVisibility(View.VISIBLE);
                                }

                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Home)) {

                                HomeScreenDto.totalCalendarDataCounts = response.getInt("totalCounts");
                                System.out.println("Total Calendar Data Counts : "+HomeScreenDto.totalCalendarDataCounts);

                                if (HomeScreenDto.calendarTabPageNumber == 0) {
                                    homeScreenDto.setHomeEvents(new ArrayList<Event>());
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            eventManagerImpl.deleteAllEvents();
                                            //eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.PAST.toString());
                                            //eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.HOME.toString());
                                            //eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.HOME.toString());
                                        }
                                    });
                                }
                                if (HomeScreenDto.calendarTabPageNumber == 0) {

                                    loadPastEvents();
                                }
                                //If there are no post Events and the current Events List is also empty
                                //Then we will hide shimer and show message.
                                if (HomeScreenDto.calendarTabPageNumber == 0 && events.size() == 0) {

                                    shimmerFrameLayout.hideShimmer();
                                    shimmerFrameLayout.setVisibility(View.GONE);

                                } else {
                                    if (events.size() != 0) {

                                        List<Long> uniqueEventIdList = homeScreenDto.getUniqueEventIdTracker();
                                        List<Event> newEventsToAdd = new ArrayList<>();
                                        for (Event currentEvent: events) {

                                            if (uniqueEventIdList.contains(currentEvent.getEventId())) {
                                                continue;
                                            }
                                            uniqueEventIdList.add(currentEvent.getEventId());
                                            newEventsToAdd.add(currentEvent);
                                        }

                                        rlNoGatheringText.setVisibility(View.GONE);
                                        processCalendarDotEvents(newEventsToAdd);

                                        //Saving Data Locally
                                        final List<Event> eventsToAdd = newEventsToAdd;
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.HOME.toString());
                                            }
                                        });

                                        List<Event> previousEvents = homeScreenDto.getHomeEvents();
                                        previousEvents.addAll(newEventsToAdd);
                                        homeScreenDto.setHomeEvents(previousEvents);

                                        System.out.println("Previoous Events Size : "+previousEvents.size());
                                        System.out.println("Home Events Size : "+homeScreenDto.getHomeEvents());

                                        if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
                                            System.out.println("ProcessCalendarTabData Called");
                                            processCalendarTabData(events, false);
                                        }
                                        HomeScreenDto.calendarTabPageNumber += HomeScreenDto.offsetToFetchData;
                                    }
                                }
                                HomeScreenDto.madeApiCall = true;
                            } else if (homeScreenAPICall.equals(HomeScreenDto.HomeScreenAPICall.Accepted)) {

                                final List<Event> eventsToAdd = events;
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        //eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.ACCEPTED.toString());
                                        eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.ACCEPTED.toString());
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

                                final List<Event> eventsToAdd = events;
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.PENDING.toString());
                                        eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.PENDING.toString());

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

                                final List<Event> eventsToAdd = events;
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.DECLINED.toString());
                                        eventManagerImpl.addEvent(eventsToAdd, Event.EventDisplayScreen.DECLINED.toString());
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

    public void uploadOfflineEvents() {
        try {
            List<Event> offlineEvnets = eventManagerImpl.findAllOfflineEvents();
            System.out.println("Offline Event Counts : "+offlineEvnets.size());
            if (offlineEvnets != null && offlineEvnets.size() > 0) {

                Event ooflineEvent = offlineEvnets.get(0);
                eventManagerImpl.deleteEventByEventId(ooflineEvent.getEventId());
                ooflineEvent.setEventId(null);
                ooflineEvent.setEventMembers(new ArrayList<EventMember>());
                JSONObject postata = new JSONObject(new Gson().toJson(ooflineEvent));

                new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
                new GatheringAsyncTask.CreateGatheringTask(new GatheringAsyncTask.CreateGatheringTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {

                        try {
                            ((CenesBaseActivity) getActivity()).homeFragmentV2.loadHomeScreenData();

                            if (response.getBoolean("success") == true) {
                                JSONObject data = response.getJSONObject("data");
                                Event eve = new Gson().fromJson(data.toString(), Event.class);

                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("Action","Offline Create Gathering Success");
                                    props.put("Title",eve.getTitle());
                                    props.put("UserEmail",loggedInUser.getEmail());
                                    props.put("UserName",loggedInUser.getName());
                                    mixpanel.track("Gathering", props);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                uploadOfflineEvents();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(postata);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processCalendarTabData(List<Event> events, boolean pastEvents){

        System.out.println(events.size());
        //List<String> headers = homeScreenDto.getHomeDataHeaders() == null ? new ArrayList<String>() : homeScreenDto.getHomeDataHeaders();
        List<Long> headersTimstamp = new ArrayList<>();
        for (Event pastEvent: homeScreenDto.getPastEvents()) {
            if (!headersTimstamp.contains(pastEvent.getStartTime())) {
                headersTimstamp.add(pastEvent.getStartTime());
            }
        }
        for (Event homeEvent: homeScreenDto.getHomeEvents()) {
            if (!headersTimstamp.contains(homeEvent.getStartTime())) {
                headersTimstamp.add(homeEvent.getStartTime());
            }
        }
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

                    List<Event> mapEvents = mapListEvent.get(headerTitle);
                    Collections.sort(mapEvents, new Comparator<Event>() {
                        @Override
                        public int compare(Event o1, Event o2) {
                            return o1.getStartTime() < o2.getStartTime() ? -1 : 1;
                        }
                    });
                    mapListEvent.put(headerTitle, mapEvents);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Creating Headers for Home Screen
        Collections.sort(headersTimstamp);
        List<String> headers = new ArrayList<>();
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

        homeScreenDto.setHomeDataHeaders(headers);
        homeScreenDto.setHomeDataListMap(mapListEvent);

        //Lets check the position of the first event group to be added.
        //this is just to find out the position at which list should scroll
        if (pastEvents == true) {
            findListViewScrollPosition();
            if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
                //Lets add Month Separator At Home Screen Events
                addMonthSeparatorToList();
            }
        }

        if (pastEvents == false && HomeScreenDto.calendarTabPageNumber > 0) {
            if (homeScreenDto.getTabSelected().equals(HomeScreenDto.HomeTabs.Calendar)) {
                //Lets add Month Separator At Home Screen Events
                addMonthSeparatorToList();
            }
        }

        //PAST EVENTS CALL
        //For Past Events we will initialize the adapter.
        if (pastEvents == false && HomeScreenDto.calendarTabPageNumber == 0) {
            //calendarTabExpandableListAdapter = new CalendarTabExpandableListAdapter(this, homeScreenDto);
            //elvHomeListView.setAdapter(calendarTabExpandableListAdapter);
            homeScreenDto.setHomelistViewWithHeaders(new ArrayList<Object>());
            shimmerFrameLayout.hideShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);

            List<Object> listView = homeScreenDto.getHomelistViewWithHeaders();
            for (String str: headers) {

                listView.add(str);
                List<Event> eventList = mapListEvent.get(str);
                listView.addAll(eventList);
            }

            homeScreenDto.setHomelistViewWithHeaders(listView);
            lvHomeListView.setVisibility(View.VISIBLE);

            if (calendarTabListViewAdapter != null) {
                calendarTabListViewAdapter.notifyDataSetChanged();
                if (mcvHomeCalendar.getVisibility() == View.VISIBLE) {
                    final CalendarDay currentPageTemp = mcvHomeCalendar.getCurrentDate();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Calendar currentYearCal = Calendar.getInstance();
                            if (currentYearCal.get(Calendar.YEAR) == currentPageTemp.getCalendar().get(Calendar.YEAR)) {
                                System.out.println("Stepxx_3 : ");
                                updateCalendarLabelDate(CenesUtils.MMMM.format(currentPageTemp.getDate()));
                            } else {
                                updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(currentPageTemp.getDate()));
                                System.out.println("Stepxx_4 : ");
                            }
                        }
                    }, 200);

                }
            } else {

                calendarTabListViewAdapter =  new CalendarTabListViewAdapter(this, homeScreenDto);
                lvHomeListView.setAdapter(calendarTabListViewAdapter);

                lvHomeListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeButtonPressed();
                    }
                }, 100);
            }
        } else {
            //For Current and future events we will just notify the Adapter for
            //Any Change
            List<Object> listView = new ArrayList<>();
            for (String str: headers) {

                listView.add(str);
                List<Event> eventList = mapListEvent.get(str);
                listView.addAll(eventList);
            }

            homeScreenDto.setHomelistViewWithHeaders(listView);
            if (calendarTabListViewAdapter != null) {
                calendarTabListViewAdapter.notifyDataSetChanged();
                if (mcvHomeCalendar.getVisibility() == View.VISIBLE) {
                    final CalendarDay currentPageTemp = mcvHomeCalendar.getCurrentDate();
                    //new Handler().postDelayed(new Runnable() {
                        //@Override
                       // public void run() {
                            Calendar currentYearCal = Calendar.getInstance();
                            if (currentYearCal.get(Calendar.YEAR) == currentPageTemp.getCalendar().get(Calendar.YEAR)) {
                                System.out.println("Stepxx_5");
                                updateCalendarLabelDate(CenesUtils.MMMM.format(currentPageTemp.getDate()));
                            } else {
                                System.out.println("Stepxx_6");
                                updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(currentPageTemp.getDate()));
                            }
                        //}
                   // }, 200);

                }
            } else {
                calendarTabListViewAdapter =  new CalendarTabListViewAdapter(this, homeScreenDto);
                lvHomeListView.setAdapter(calendarTabListViewAdapter);
            }

            if (pastEvents == true) {
                lvHomeListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeButtonPressed();
                    }
                }, 100);
            }
        }
    }

    public void findListViewScrollPosition() {

        if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
            List<Event> events = homeScreenDto.getPastEvents();
            if (events == null || events.size() == 0) {
                return;
            }

            Calendar currentDateCal = Calendar.getInstance();

            List<String> headers = homeScreenDto.getHomeDataHeaders();
            Map<String, List<Event>> mapListEvent = homeScreenDto.getHomeDataListMap();

            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.MILLISECOND, 0);
            currentTime.set(Calendar.SECOND, 0);

            List<Event> futureEvents = homeScreenDto.getHomeEvents();

            try {

                Collections.sort(futureEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        return o1.getStartTime() < o2.getStartTime() ? -1 : 1;
                    }
                });

                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        return o1.getStartTime() < o2.getStartTime() ? -1 : 1;
                    }
                });

                int positionOfEventToScrollAt = 0;
                Event eventForScrollPosition = null;
                if (futureEvents.size() > 0) {
                    for (Event fEvent: futureEvents) {
                        if (fEvent.getStartTime() > currentTime.getTimeInMillis()) {
                            eventForScrollPosition = fEvent;
                            positionOfEventToScrollAt++;
                            break;
                        }
                        positionOfEventToScrollAt++;
                    }
                } else {
                    eventForScrollPosition = events.get(events.size() - 1);
                }

                if (eventForScrollPosition != null) {

                    String headerTitle = "";
                    Calendar eventCal = Calendar.getInstance();
                    eventCal.setTimeInMillis(eventForScrollPosition.getStartTime());
                    if (currentDateCal.get(Calendar.YEAR) == eventCal.get(Calendar.YEAR)) {
                        headerTitle = CenesUtils.EEEMMMMdd.format(new Date(eventForScrollPosition.getStartTime()));
                    } else {
                        headerTitle = CenesUtils.EEEMMMMddcmyyyy.format(new Date(eventForScrollPosition.getStartTime()));
                    }
                    eventCal = null;
                    int itemPosition = 0;
                    for (int i=0; i<headers.size(); i++) {

                        if (headerTitle.equals(headers.get(i))) {

                            itemPosition += 1 + mapListEvent.get(headers.get(i)).size() + 1;
                            itemPosition += positionOfEventToScrollAt;

                            if (headers.get(i+1) != null) {
                                HomeScreenDto.currentDateGroupPositionTimestamp = mapListEvent.get(headers.get(i+1)).get(0).getStartTime();
                            }
                            HomeScreenDto.currentDateGroupPosition = itemPosition;
                            break;
                        }

                        itemPosition += 1 + mapListEvent.get(headers.get(i)).size();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void addMonthSeparatorToList() {


        List<String> headers = homeScreenDto.getHomeDataHeaders();
        Map<String, List<Event>> mapListEvent = homeScreenDto.getHomeDataListMap();

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
                        if (lastEvent.getScheduleAs().equals(Event.EventScheduleAs.MonthSeparator.toString())) {
                            monthSeparatorExists = true;
                        }
                    }

                    if (!monthSeparatorExists) {
                        //Lets create new event
                        Event event = new Event();
                        event.setScheduleAs(Event.EventScheduleAs.MonthSeparator.toString());
                        event.setTitle(monthTitle);
                        event.setStartTime(monthSeparatorCal.getTimeInMillis());
                        event.setMonthSeparatorTimestamp(currentListEventDateCal.getTimeInMillis());
                        previousListEvents.add(event);
                        mapListEvent.put(headers.get(i-1), previousListEvents);

                        HomeScreenDto.homeListGroupAndMonthHolder.put(monthTitle, i);

                        homeScreenDto.setHomeDataListMap(mapListEvent);
                    }
                }
            }
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

        if (invitationListItemAdapter != null) {
            invitationListItemAdapter.notifyDataSetChanged();
        } else {
            invitationListItemAdapter = new InvitationListItemAdapter(this, homeScreenDto);
            elvInvitationListView.setAdapter(invitationListItemAdapter);
        }
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

    public void firstTimeLoadData() {

        HomeScreenDto.HomeTabs tabSelected = homeScreenDto.getTabSelected();
        HomeScreenDto.InvitationTabs invitationTabSelected = homeScreenDto.getInvitationTabSelected();
        //Java Variables
        homeScreenDto = new HomeScreenDto();
        if (tabSelected == null) {
            tabSelected = HomeScreenDto.HomeTabs.Calendar;
        }
        homeScreenDto.setTabSelected(tabSelected);
        if (tabSelected.equals(HomeScreenDto.HomeTabs.Invitation)) {
            homeScreenDto.setInvitationTabSelected(invitationTabSelected);
        }
        HomeScreenDto.calendarPreviousPage = null;
        HomeScreenDto.currentDateGroupPositionTimestamp = 0;
        HomeScreenDto.currentDateGroupPosition = 0;
        HomeScreenDto.calendarTabPageNumber = 0;
        HomeScreenDto.totalCalendarDataCounts = 0;
        HomeScreenDto.madeApiCall = false;

        invitationListItemAdapter = null;
        calendarTabListViewAdapter = null;

        List<Event> homeEvents = eventManagerImpl.fetchHomeScreenPastEvents(loggedInUser.getUserId());
        homeScreenDto.setPastEvents(homeEvents);

        List<Event> homeEvents2 = eventManagerImpl.fetchHomeScreenFutureEvents(loggedInUser.getUserId());
        homeEvents.addAll(homeEvents2);

        if(homeEvents.size() == 0) {
            if (internetManager.isInternetConnection(getCenesActivity())) {
                    //shimmerFrameLayout.setVisibility(View.VISIBLE);
            }
        } else {
            homeScreenDto.setHomeEvents(homeEvents);
            processCalendarTabData(homeEvents, true);
        }
        List<Event> acceptedEvents = eventManagerImpl.fetchAcceptedTabEvents(loggedInUser.getUserId());
        homeScreenDto.setAcceptedEvents(acceptedEvents);

        List<Event> pendingEvents = eventManagerImpl.fetchPendingTabEvents(loggedInUser.getUserId());
        homeScreenDto.setPendingEvents(pendingEvents);

        List<Event> declinedEvents = eventManagerImpl.fetchDeclinedTabEvents(loggedInUser.getUserId());
        homeScreenDto.setDeclinedEvents(declinedEvents);
    }


}
