package com.cenesbeta.fragment.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.AlarmActivity;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.CreateReminderActivity;
import com.cenesbeta.adapter.HomeScreenAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.HomeScreenApiManager;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.fragment.gathering.CreateGatheringFragment;
import com.cenesbeta.leolin.shortcurtbadger.ShortcutBadger;
import com.cenesbeta.materialcalendarview.CalendarDay;
import com.cenesbeta.materialcalendarview.CalendarMode;
import com.cenesbeta.materialcalendarview.MaterialCalendarView;
import com.cenesbeta.materialcalendarview.OnDateSelectedListener;
import com.cenesbeta.materialcalendarview.decorators.CurrentDateDecorator;
import com.cenesbeta.materialcalendarview.decorators.EventDecorator;
import com.cenesbeta.materialcalendarview.decorators.OneDayDecorator;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rohan on 9/11/17.
 */

public class HomeFragment extends CenesFragment {

    public final static String TAG = "HomeFragment";

    private int GATHERING_SUMMARY_RESULT_CODE = 1001, CREATE_GATHERING_RESULT_CODE = 1002, CREATE_REMINDER_RESULT_CODE = 1003, NOTIFICATION_RESULT_CODE = 1004;
    // Array of strings...
    ExpandableListView homeScreenEventsList;
    RoundedImageView homePageProfilePic;
    TextView tvSelectedDate;
    TextView gatheringBtn, homeNoEvents, tvCalendarSwitcher;
    ImageView homeCalenderSearchViewIcon;
    MaterialCalendarView homeCalSearchView;
    //private ImageView footerHomeIcon, footerGatheringIcon, footerReminderIcon, footerAlarmIcon, footerDiaryIcon, footerMeTimeIcon;
    private FloatingActionButton fab, closeFabMenuBtn, gatheringFabMenuBtn, reminderFabMenuBtn, alarmFabMenuBtn;
    HomeScreenAdapter listAdapter;
    RelativeLayout rlFabMenu;
    SwipeRefreshLayout pullToRefresh;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    ApiManager apiManager;
    UserApiManager userApiManager;
    UrlManager urlManager;
    InternetManager internetManager;
    HomeScreenApiManager homeScreenApiManager;
    User loggedInUser;
    Tracker mTracker;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Map<String, Set<CalendarDay>> calendarHighlights;

    boolean calModeMonth;

    private ProgressDialog mProgressDialog;
    private EventsTask eventsTask;
    private HolidayCalendarTask holidayCalendarTask;
    private CurrentDateDecorator currentDateDecorator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if (((CenesBaseActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true)) {
            ((CenesBaseActivity) getActivity()).sharedPrefs.edit().putBoolean("isFirstLogin", false).commit();
        }

        init(v);
        ((CenesBaseActivity)getActivity()).rlLoadingBlock.setVisibility(View.GONE);
        return v;
    }

    public void init(View v) {
        cenesApplication = ((CenesBaseActivity) getActivity()).getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();
        userApiManager = coreManager.getUserAppiManager();
        loggedInUser = userManager.getUser();
        homeScreenApiManager = coreManager.getHomeScreenApiManager();

        getActivity().getSharedPreferences("CenesPrefs", getActivity().MODE_PRIVATE).edit().putString("userId", loggedInUser.getUserId()+"").apply();
        getActivity().getSharedPreferences("CenesPrefs", getActivity().MODE_PRIVATE).edit().putString("authToken", loggedInUser.getAuthToken()).apply();

        CenesUtils.logEntries(loggedInUser, "User Lands On Home Screen Page", getActivity().getApplicationContext());

        pullToRefresh = (SwipeRefreshLayout)v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);

                //refreshData(); // your code
                try {
                    new ContactsSyncTask().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

        homeScreenEventsList = (ExpandableListView) v.findViewById(R.id.home_events_list_view);
        homePageProfilePic = (RoundedImageView) v.findViewById(R.id.home_profile_pic);

        homeNoEvents = (TextView) v.findViewById(R.id.home_no_events);
        //footerHomeIcon = ((CenesBaseActivity) getActivity()).footerHomeIcon;
        //footerReminderIcon = ((CenesBaseActivity) getActivity()).footerReminderIcon;
        //footerGatheringIcon = ((CenesBaseActivity) getActivity()).footerGatheringIcon;
        //footerAlarmIcon = ((CenesBaseActivity) getActivity()).footerAlarmIcon;
        //footerDiaryIcon = ((CenesBaseActivity) getActivity()).footerDiaryIcon;
        //footerMeTimeIcon = ((CenesBaseActivity) getActivity()).footerMeTimeIcon;

        tvSelectedDate = (TextView) v.findViewById(R.id.tv_selected_date);
        //footerHomeIcon.setCompoundDrawables(getResources().getDrawable(R.drawable.home_icon),null,null,null);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        closeFabMenuBtn = (FloatingActionButton) v.findViewById(R.id.close_fab_menu_btn);
        gatheringFabMenuBtn = (FloatingActionButton) v.findViewById(R.id.gathering_fab_menu_btn);
        reminderFabMenuBtn = (FloatingActionButton) v.findViewById(R.id.reminder_fab_menu_btn);
        alarmFabMenuBtn = (FloatingActionButton) v.findViewById(R.id.alarm_fab_menu_btn);

        rlFabMenu = (RelativeLayout) v.findViewById(R.id.fab_menu);
        rlFabMenu.setOnClickListener(null);

        tvCalendarSwitcher = (TextView) v.findViewById(R.id.tvCalendarSwitcher);

        homeNoEvents.setVisibility(View.GONE);

        //ImageViews
        homeCalenderSearchViewIcon = (ImageView) v.findViewById(R.id.home_cal_search_view_icon);

        homeCalSearchView = (MaterialCalendarView) v.findViewById(R.id.home_cal_search_view);

        currentDateDecorator = new CurrentDateDecorator((CenesBaseActivity) getActivity(), R.drawable.calendar_selector_orange);
        homeCalSearchView.addDecorator(currentDateDecorator);
        homeCalSearchView.setCurrentDate(new Date(System.currentTimeMillis()));
        //homeCalSearchView.newState().setCalendarDisplayMode(CalendarMode.values()[CalendarMode.WEEKS.ordinal()]).commit();

        homeCalSearchView.setVisibility(View.GONE);
        tvCalendarSwitcher.setVisibility(View.GONE);

        //get month and get how many days in current month

        User user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(getActivity()).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homePageProfilePic);
        }

        SimpleDateFormat weekCategory = new SimpleDateFormat("EEEE");
        SimpleDateFormat calCategory = new SimpleDateFormat("ddMMM");
        tvSelectedDate.setText(Html.fromHtml(calCategory.format(new Date()).toUpperCase()+"<b>"+weekCategory.format(new Date()).toUpperCase()+"</b>"));


        initialSync();

        System.out.println(CenesUtils.getDeviceManufacturer()+" -------  "+CenesUtils.getDeviceModel()+" ---  "+CenesUtils.getDeviceVersion());

        // setting list adapter

        tvCalendarSwitcher.setOnClickListener(onClickListener);
        homeCalenderSearchViewIcon.setOnClickListener(onClickListener);
        fab.setOnClickListener(onClickListener);
        closeFabMenuBtn.setOnClickListener(onClickListener);
        gatheringFabMenuBtn.setOnClickListener(onClickListener);
        reminderFabMenuBtn.setOnClickListener(onClickListener);
        alarmFabMenuBtn.setOnClickListener(onClickListener);
        homePageProfilePic.setOnClickListener(onClickListener);

        homeCalSearchView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // TODO Auto-generated method stub

                homeCalSearchView.removeDecorator(currentDateDecorator);
                homeCalSearchView.removeDecorator(mOneDayDecorator);

                Boolean isDefault = true;
                if (calendarHighlights != null) {

                    if (calendarHighlights.containsKey("Holiday")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Holiday");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_blue);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);
                        }
                    }

                    if (calendarHighlights.containsKey("Holiday")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Holiday");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_teal);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }
                    if (calendarHighlights.containsKey("Google")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Google");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_red);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }
                    if (calendarHighlights.containsKey("Outlook")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Outlook");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_outlook_blue);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }
                    if (calendarHighlights.containsKey("Facebook")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Facebook");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_blue);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }
                    if (calendarHighlights.containsKey("Apple")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Apple");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_gray);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }
                    if (calendarHighlights.containsKey("Cenes")) {
                        Set<CalendarDay> calendarDays = calendarHighlights.get("Cenes");
                        if (calendarDays.contains(date)) {
                            isDefault = false;
                            //mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);

                        }
                    }

                }
                if (isDefault) {
                    mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_orange);
                }

                homeCalSearchView.addDecorator(mOneDayDecorator);

                Calendar cal = Calendar.getInstance();
                cal.set(date.getYear(), date.getMonth(), date.getDay());
                cal.set(Calendar.HOUR_OF_DAY,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);

                SimpleDateFormat weekCategory = new SimpleDateFormat("EEEE");
                SimpleDateFormat calCategory = new SimpleDateFormat("ddMMM");
                tvSelectedDate.setText(Html.fromHtml(calCategory.format(new Date()).toUpperCase()+"<b>"+weekCategory.format(new Date()).toUpperCase()+"</b>"));

                String queryStr = "&date=" + cal.getTimeInMillis();
                if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {
                    try {
                        eventsTask = new EventsTask();
                        eventsTask.execute(queryStr);
                    } catch (Exception e) {
                        Toast.makeText((CenesBaseActivity) getActivity(), "Bad Request", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText((CenesBaseActivity) getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void initialSync() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        final String queryStr = "&date=" + cal.getTimeInMillis();
        if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    eventsTask = new EventsTask();
                    eventsTask.execute(queryStr);
                }
            });


            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //TODO your background code
                        holidayCalendarTask = new HolidayCalendarTask();
                        holidayCalendarTask.execute();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Bad Request", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
        }
    }

    OneDayDecorator mOneDayDecorator;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GATHERING_SUMMARY_RESULT_CODE || requestCode == CREATE_REMINDER_RESULT_CODE) && resultCode == Activity.RESULT_OK) {
            initialSync();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_profile_pic:
                    ((CenesBaseActivity)getActivity()).fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();
                    ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.tvCalendarSwitcher:
                    if (calModeMonth) {
                        homeCalSearchView.newState().setCalendarDisplayMode(CalendarMode.values()[CalendarMode.WEEKS.ordinal()]).commit();
                        tvCalendarSwitcher.setText(getString(R.string.switch_to_monthly_view));
                    } else {
                        homeCalSearchView.newState().setCalendarDisplayMode(CalendarMode.values()[CalendarMode.MONTHS.ordinal()]).commit();
                        tvCalendarSwitcher.setText(getString(R.string.switch_to_weekly_view));
                    }
                    calModeMonth = !calModeMonth;
                    break;
                case R.id.home_cal_search_view_icon:
                    if (homeCalSearchView.getVisibility() == View.GONE) {
                        homeCalSearchView.setCurrentDate(new Date(System.currentTimeMillis()));
                        homeCalSearchView.setVisibility(View.VISIBLE);
                        //tvCalendarSwitcher.setVisibility(View.VISIBLE);
                        homeCalenderSearchViewIcon.setImageResource(R.drawable.calendar_open);
                    } else {
                        homeCalSearchView.setVisibility(View.GONE);
                        //tvCalendarSwitcher.setVisibility(View.GONE);
                        homeCalenderSearchViewIcon.setImageResource(R.drawable.calendar_close);
                    }
                    break;
                case R.id.fab:
                    rlFabMenu.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                    break;
                case R.id.close_fab_menu_btn:
                    rlFabMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    break;
                case R.id.gathering_fab_menu_btn:
                    rlFabMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    //startActivityForResult(new Intent(CenesBaseActivity.this, CreateGatheringActivity.class), CREATE_GATHERING_RESULT_CODE);
                    //Intent data = new Intent(getActivity().getApplicationContext(), GatheringScreenActivity.class);
                    //data.putExtra("dataFrom", "fabButton");
                    //context.startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                    //startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                    CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                    ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, CreateGatheringFragment.TAG);
                    break;
                case R.id.reminder_fab_menu_btn:
                    /*rlFabMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    startActivityForResult(new Intent((CenesBaseActivity) getActivity(), CreateReminderActivity.class), CREATE_REMINDER_RESULT_CODE);
                    //finish();*/
                    rlFabMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    Intent data = new Intent(new Intent((CenesBaseActivity) getActivity(), CreateReminderActivity.class));
                    data.putExtra("dataFrom", "fabButton");
                    startActivityForResult(data,CREATE_REMINDER_RESULT_CODE);
                    break;
                case R.id.alarm_fab_menu_btn:
                    rlFabMenu.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                    data = new Intent(new Intent((CenesBaseActivity) getActivity(), AlarmActivity.class));
                    data.putExtra("dataFrom", "fabButton");
                    startActivityForResult(data,0001);
                    break;
            }
        }
    };


    //    In some mobiles image will get rotate s`o to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            System.out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

         ShortcutBadger.applyCount(getActivity().getApplicationContext(),0);

        SharedPreferences.Editor editor = ((CenesBaseActivity) getActivity()).sharedPrefs.edit();
        editor.putInt("badgeCounts", 0);
        editor.apply();

        mTracker.setScreenName("HomeScreen");
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("User")
                .setAction("logged In")
                .setLabel(loggedInUser.getEmail()+" android")
                .build());

        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(HomeFragment.TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (eventsTask != null) {
            eventsTask.cancel(true);
        }
        if (holidayCalendarTask != null) {
            holidayCalendarTask.cancel(true);
        }

    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    class EventsTask extends AsyncTask<String, Void, JSONObject> {
        ImageView bmImage;
        //ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            //mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
            //mProgressDialog.setCancelable(true);
            //mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... queryStrs) {
            if(!isCancelled()) {
                String queryStr = "";
                if (queryStrs != null && queryStrs.length != 0) {
                    queryStr = queryStrs[0];
                }

                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));
                return apiManager.getUserEvents(user, queryStr, (CenesBaseActivity) getActivity());
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //mProgressDialog.hide();
            //mProgressDialog.dismiss();
            //mProgressDialog = null;
            if (getActivity() == null) {
                return;
            }
           if (!getActivity().isFinishing()) {
               if(jsonObject != null) {
                   JSONArray jsonArray = new JSONArray();
                   try {
                       jsonArray = (JSONArray) jsonObject.getJSONArray("data");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                   List<String> headers = new ArrayList<>();
                   Map<String, List<Event>> eventMap = new HashMap<>();
                   List<Event> events = new ArrayList<>();
                   Boolean eventsExists = false;
                   if (jsonArray.length() > 0) {
                       eventsExists = true;
                       for (int i = 0; i < jsonArray.length(); i++) {
                           try {

                               Event event = new Event();
                               JSONObject eventObj = (JSONObject) jsonArray.getJSONObject(i);
                               SimpleDateFormat weekCategory = new SimpleDateFormat("EEEE");
                               SimpleDateFormat calCategory = new SimpleDateFormat("ddMMM");
                               SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

                               if (eventObj.has("type") && eventObj.getString("type").equals("Reminder")) {
                                   continue;
                               }

                               if (eventObj.has("id")) {
                                   event.setEventId(eventObj.getLong("id"));
                               }
                               if (eventObj.has("title")) {
                                   event.setTitle(eventObj.getString("title"));
                               }
                               if (eventObj.has("type")) {
                                   event.setType(eventObj.getString("type"));
                               }
                               if (eventObj.has("createdById")) {
                                   event.setCreatedById(eventObj.getLong("createdById"));
                               }
                               if (eventObj.has("eventPicture")) {
                                   event.setEventPicture(eventObj.getString("eventPicture"));
                               }
                               if (eventObj.has("location") && eventObj.getString("location") != "null") {
                                   event.setLocation(eventObj.getString("location"));
                               }
                               if (eventObj.has("isFullDay") && !eventObj.isNull("isFullDay")) {
                                   event.setIsFullDay(eventObj.getBoolean("isFullDay"));
                               }

                               if (eventObj.has("isExpired") && !eventObj.isNull("isExpired")) {
                                   event.setExpired(eventObj.getBoolean("isExpired"));
                               }
                        /*if (eventObj.has("startTime")) {
                            Date startDate = new Date(eventObj.getLong("startTime"));
                            parentEvent.setStartTime(timeFormat.format(startDate));
                        }*/
                               if (eventObj.has("source") && eventObj.getString("source") != null) {
                                   event.setSource(eventObj.getString("source"));
                               }
                               if (eventObj.has("scheduleAs") && eventObj.getString("scheduleAs") != null) {
                                   event.setScheduleAs(eventObj.getString("scheduleAs"));
                               }

                               if (eventObj.has("members")) {
                                   JSONArray membersArray = eventObj.getJSONArray("members");
                                   List<EventMember> members = new ArrayList<>();
                                   EventMember owner = null;
                                   EventMember loggedInUserEventMemberData = null;

                                   for (int idx = 0; idx < membersArray.length(); idx++) {
                                       JSONObject memberObj = (JSONObject) membersArray.get(idx);
                                       EventMember eventMember = new EventMember();

                                       Gson gson = new Gson();
                                       eventMember = gson.fromJson(memberObj.toString(), EventMember.class);

                                       if (eventMember.getUserId() != null && event.getCreatedById().equals(eventMember.getUserId())) {
                                           owner = eventMember;
                                       }
                                       if (eventMember.getUserId() != null && loggedInUser.getUserId() == eventMember.getUserId()) {
                                           System.out.println("Inside Logegd IN User");
                                           loggedInUserEventMemberData = eventMember;
                                           System.out.println("Event Member Id : "+loggedInUserEventMemberData.toString());
                                       }

                                       members.add(eventMember);
                                   }
                                   event.setEventMembers(members);

                                   //This is needed to show owner image next to gathering
                                   event.setOwner(owner);

                                   System.out.println("Logged IN User member id : "+loggedInUserEventMemberData.getEventMemberId());
                                   event.setUserEventMemberData(loggedInUserEventMemberData);
                               }

                               if (loggedInUser.getUserId() == event.getCreatedById()) {
                                   event.setIsOwner(true);
                               }


                               if (eventObj.has("fullDayStartTime") && eventObj.getString("fullDayStartTime") != "null") {
                                   eventObj.put("startTime", CenesUtils.yyyyMMdd.parse(eventObj.getString("fullDayStartTime")).getTime());
                               }

                               if (eventObj.has("startTime") && eventObj.getString("startTime") != "null") {
                                   Date startDate = new Date(eventObj.getLong("startTime"));
                                   event.setStartTime(timeFormat.format(startDate));
                                     event.setStartTimeMillis(eventObj.getLong("startTime"));
                                   String dateKey = calCategory.format(startDate).toUpperCase() + "<b>"+weekCategory.format(startDate).toUpperCase()+"</b>";

                                  //String dateKey = calCategory.format(startDate) + CenesUtils.getDateSuffix(startDate.getDate());
                                   if (sdf.format(startDate).equals(sdf.format(new Date()))) {
                                       dateKey = "TODAY ";
                                   }
                                   Calendar cal = Calendar.getInstance();
                                   cal.setTime(new Date());
                                   cal.add(Calendar.DATE, 1);
                                   if (sdf.format(startDate).equals(sdf.format(cal.getTime()))) {
                                       dateKey = "TOMORROW";
                                   }
                                   if (!headers.contains(dateKey)) {
                                       headers.add(dateKey);
                                   }
                                   if (eventMap.containsKey(dateKey)) {
                                       events = eventMap.get(dateKey);
                                   } else {
                                       events = new ArrayList<>();
                                   }
                                   events.add(event);
                                   eventMap.put(dateKey, events);
                               }
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }

                       if (eventMap != null) {


                           for (Map.Entry<String, List<Event>> eventMapEntrySet : eventMap.entrySet()) {

                               Collections.sort(eventMapEntrySet.getValue(), new Comparator<Event>() {
                                   public int compare(Event o1, Event o2) {
                                       return o1.getIsFullDay().compareTo(true) > o2.getIsFullDay().compareTo(true) ? -1 : o1.getIsFullDay().compareTo(true) == o2.getFullDay().compareTo(true) ? 0 : 1;
                                   }
                               });
                           }

                           Iterator iterator = eventMap.entrySet().iterator();
                           while (iterator.hasNext()) {
                               Map.Entry<String, List<Event>> entry = (Map.Entry<String, List<Event>>) iterator.next();

                               List<Event> eventList = entry.getValue();
                               List<Event> tempEvents = new ArrayList<>();
                               List<Event> tempReminders = new ArrayList<>();

                               for (int i = 0; i < eventList.size(); i++) {
                                   Event event = eventList.get(i);

                                   if(event.getType().equalsIgnoreCase("Reminder")) {
                                       tempReminders.add(event);
                                   } else {
                                       tempEvents.add(event);
                                   }
                               }

                               eventList.clear();
                               eventList.addAll(tempEvents);
                               eventList.addAll(tempReminders);
                           }
                       }

                       listAdapter = new HomeScreenAdapter((CenesBaseActivity) getActivity(), headers, eventMap);
                   }

                   if (!eventsExists) {
                       homeScreenEventsList.setVisibility(View.GONE);
                       homeNoEvents.setVisibility(View.VISIBLE);
                       homeNoEvents.setTextSize(20f);
                       homeNoEvents.setText("No Event Exists For This Date");
                   } else {
                       homeScreenEventsList.setVisibility(View.VISIBLE);
                       homeScreenEventsList.setAdapter(listAdapter);
                   }
               } else {
                   getCenesActivity().showRequestTimeoutDialog();
               }
           }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            eventsTask.cancel(true);
        }
    }

    class HolidayCalendarTask extends AsyncTask<String, JSONObject, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?user_id=" + user.getUserId();
            JSONObject response = apiManager.getUserHolidays(user, queryStr, (CenesBaseActivity) getActivity());

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            if (getActivity() == null) {
                return;
            }
            if (!getActivity().isFinishing()) {
                calendarHighlights = new HashMap<>();
                try {

                   /* Calendar cal1 = Calendar.getInstance();
                    cal1.add(Calendar.DAY_OF_MONTH, 1);
                    int year1 = cal1.get(Calendar.YEAR);
                    int month1 = cal1.get(Calendar.MONTH);
                    int dayOfMonth1 = cal1.get(Calendar.DAY_OF_MONTH);
                    CalendarDay calendarDay1 = new CalendarDay(year1, month1, dayOfMonth1);

                    Set<CalendarDay> todaySet1 = new HashSet<>();
                    todaySet1.add(calendarDay1);

                    //homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_teal), calendarHighlights.get("Holiday")));

                    List<CalendarFilter> calendarFilters

                    new MultipleEventDecorator()
                    homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), todaySet1));

                    Set<CalendarDay> todaySet2 = new HashSet<>();
                    todaySet2.add(calendarDay1);

                    //homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_teal), calendarHighlights.get("Holiday")));
                    homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_blue), todaySet2));

*/


                    if (response.getBoolean("success") && response.getJSONArray("data").length() > 0) {
                        JSONArray holidaysArray = response.getJSONArray("data");

                        for (int i = 0; i < holidaysArray.length(); i++) {
                            JSONObject holiday = holidaysArray.getJSONObject(i);

                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(holiday.getLong("startTime"));
                            int year = cal.get(Calendar.YEAR);
                            int month = cal.get(Calendar.MONTH);
                            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                            CalendarDay calendarDay = new CalendarDay(year, month, dayOfMonth);
                            if (holiday.getString("source").equals("Cenes") || holiday.getString("source").equals("Facebook") || holiday.getString("source").equals("Outlook") || holiday.getString("source").equals("Apple") || holiday.getString("source").equals("Google")) {
                                if (holiday.getString("source").equals("Google")) {
                                    if (holiday.getString("scheduleAs").equals("Holiday")) {
                                        Set<CalendarDay> homeScreenCalendarDates = new HashSet<>();
                                        if (calendarHighlights.containsKey("Holiday")) {
                                            homeScreenCalendarDates = calendarHighlights.get("Holiday");
                                        }
                                        homeScreenCalendarDates.add(calendarDay);
                                        calendarHighlights.put(holiday.getString("scheduleAs"), homeScreenCalendarDates);
                                    }
                                    if (holiday.getString("scheduleAs").equals("Event")) {
                                        Set<CalendarDay> homeScreenCalendarDates = new HashSet<>();
                                        if (calendarHighlights.containsKey("Google")) {
                                            homeScreenCalendarDates = calendarHighlights.get("Google");
                                        }
                                        homeScreenCalendarDates.add(calendarDay);
                                        calendarHighlights.put("Google", homeScreenCalendarDates);
                                    }
                                } else {
                                    Set<CalendarDay> homeScreenCalendarDates = new HashSet<>();
                                    if (calendarHighlights.containsKey(holiday.getString("source"))) {
                                        homeScreenCalendarDates = calendarHighlights.get(holiday.getString("source"));
                                    }
                                    homeScreenCalendarDates.add(calendarDay);
                                    calendarHighlights.put(holiday.getString("source"), homeScreenCalendarDates);
                                }
                            }

                            /*if (calendarHighlights.containsKey(holiday.getString("scheduleAs"))) {
                                Set<CalendarDay> holidaysBackgroundDates = calendarHighlights.get(holiday.getString("scheduleAs"));
                                holidaysBackgroundDates.add(calendarDay);
                                calendarHighlights.put(holiday.getString("scheduleAs"), holidaysBackgroundDates);
                            } else {
                                Set<CalendarDay> holidaysBackgroundDates = new HashSet<>();
                                holidaysBackgroundDates.add(calendarDay);
                                calendarHighlights.put(holiday.getString("scheduleAs"), holidaysBackgroundDates);
                            }*/
                        }

                        if (calendarHighlights.containsKey("Holiday")) {
                            //homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_teal), calendarHighlights.get("Holiday")));
                            homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Holiday")));

                        }
                        if (calendarHighlights.containsKey("Google")) {
                            //homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.google_plus_red), calendarHighlights.get("Google")));
                            homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Google")));

                        }
                        if (calendarHighlights.containsKey("Outlook")) {
                           // homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.outlook_blue), calendarHighlights.get("Outlook")));
                            homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Outlook")));

                        }
                        if (calendarHighlights.containsKey("Facebook")) {
                           // homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.facebook_blue), calendarHighlights.get("Facebook")));
                            homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Facebook")));

                        }
                        if (calendarHighlights.containsKey("Apple")) {
                          //  homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_light_gray), calendarHighlights.get("Apple")))
                            homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Apple")));

                        }
                        if (calendarHighlights.containsKey("Cenes")) {
                           homeCalSearchView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_new_orange), calendarHighlights.get("Cenes")));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            holidayCalendarTask.cancel(true);
        }
    }

    public void refreshContacts() {
                try  {

                    Map<String, String> contactsArrayMap = new HashMap<>();

                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                            null, null, null, null);

                    if ((cur != null ? cur.getCount() : 0) > 0) {
                        while (cur != null && cur.moveToNext()) {
                            String id = cur.getString(
                                    cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));

                            if (cur.getInt(cur.getColumnIndex(
                                    ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor pCur = cr.query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                        new String[]{id}, null);

                                while (pCur.moveToNext()) {
                                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    //Log.e("phoneNo : "+phoneNo , "Name : "+name);

                                    if (phoneNo.indexOf("\\*") != -1 || phoneNo.indexOf("\\#") != -1 || phoneNo.length() < 7) {
                                        continue;
                                    }
                                    try {
                                        String parsedPhone = phoneNo.replaceAll(" ","").replaceAll("\\s","").replaceAll("-","").replaceAll("\\(","").replaceAll("\\)","");
                                        if (parsedPhone.indexOf("+") == -1) {
                                            parsedPhone = "+"+parsedPhone;
                                        }
                                        //contactObject.put(parsedPhone, name);
                                        //contactsArray.put(contactObject);
                                        if (!contactsArrayMap.containsKey(parsedPhone)) {
                                            contactsArrayMap.put(parsedPhone, name);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                pCur.close();
                            }
                        }

                    }
                    if(cur!=null){
                        cur.close();
                    }

                    JSONArray contactsArray = new JSONArray();
                    for (Map.Entry<String, String> entryMap: contactsArrayMap.entrySet()) {
                        JSONObject contactObject = new JSONObject();
                        try {
                            contactObject.put(entryMap.getKey(), entryMap.getValue());
                            contactsArray.put(contactObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    final JSONObject userContact = new JSONObject();
                    try {
                        userContact.put("userId",loggedInUser.getUserId());
                        userContact.put("contacts",contactsArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        userApiManager.syncDevicePhone(userContact, loggedInUser.getAuthToken());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    private class ContactsSyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Syncing..!");
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                refreshContacts();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            pd = null;
            Toast.makeText(getActivity(), "Contacts Synced..!",Toast.LENGTH_SHORT).show();
            new GoogleCalendarSyncTask().execute();
        }
    }


    private class GoogleCalendarSyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Syncing..!");
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                String params = "userId="+ loggedInUser.getUserId();
                homeScreenApiManager.refreshGoogleEvents(params, loggedInUser.getAuthToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            pd = null;
            Toast.makeText(getActivity(), "Google Synced..!",Toast.LENGTH_SHORT).show();
            new OutlookCalendarSyncTask().execute();
        }
    }

    private class OutlookCalendarSyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Syncing..!");
            pd.setIndeterminate(false);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                String params = "userId="+ loggedInUser.getUserId();
                homeScreenApiManager.refreshOutlookEvents(params, loggedInUser.getAuthToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // onPostExecute displays the results of the doInBackgroud and also we
        // can hide progress dialog.
        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            pd = null;
            Toast.makeText(getActivity(), "Outlook Synced..!",Toast.LENGTH_SHORT).show();
            initialSync();
        }
    }
}
