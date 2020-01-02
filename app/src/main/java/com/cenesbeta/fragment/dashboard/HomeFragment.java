package com.cenesbeta.fragment.dashboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.HomeScreenAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.HomeScreenApiManager;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
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

/**
 * Created by rohan on 9/11/17.
 */

public class HomeFragment extends CenesFragment {

    public final static String TAG = "HomeFragment";

    private int GATHERING_SUMMARY_RESULT_CODE = 1001, CREATE_REMINDER_RESULT_CODE = 1003;
    // Array of strings...
    ExpandableListView homeScreenEventsList;
    RoundedImageView homePageProfilePic;
    TextView tvSelectedDate;
    TextView gatheringBtn, homeNoEvents, tvCalendarSwitcher;
    ImageView homeCalenderSearchViewIcon;
    MaterialCalendarView homeCalSearchView;
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
    private EventManagerImpl eventManagerImpl;
    private View fragmentView;

    private Map<String, Set<CalendarDay>> calendarHighlights;

    boolean calModeMonth;

    private GatheringAsyncTask eventsTask;
    private HolidayCalendarTask holidayCalendarTask;
    private CurrentDateDecorator currentDateDecorator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Obtain the shared Tracker instance.
        CenesApplication application = (CenesApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        if (fragmentView != null) {
            return fragmentView;
        }

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentView = v;
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

        eventsTask = new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        eventManagerImpl = new EventManagerImpl(cenesApplication);

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

        tvSelectedDate = (TextView) v.findViewById(R.id.tv_selected_date);
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
        if (user != null && !CenesUtils.isEmpty(user.getPicture())) {
            Glide.with(getActivity()).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homePageProfilePic);
        }

        tvSelectedDate.setText(Html.fromHtml(CenesUtils.ddMMM.format(new Date()).toUpperCase()+"<b>"+CenesUtils.EEEE.format(new Date()).toUpperCase()+"</b>"));

        initialSync();

        // setting list adapter

        tvCalendarSwitcher.setOnClickListener(onClickListener);
        homeCalenderSearchViewIcon.setOnClickListener(onClickListener);
        fab.setOnClickListener(onClickListener);
        closeFabMenuBtn.setOnClickListener(onClickListener);
        gatheringFabMenuBtn.setOnClickListener(onClickListener);
        reminderFabMenuBtn.setOnClickListener(onClickListener);
        alarmFabMenuBtn.setOnClickListener(onClickListener);
        homePageProfilePic.setOnClickListener(onClickListener);

        homeCalSearchView.setOnDateChangedListener(onDateSelectedListener);

    }

    public void initialSync() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {

            List<Event> events = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.HOME.toString());
            summarizeEventsForHomeScreen(events);

            final String queryStr = "user_id="+loggedInUser.getUserId()+"&date=" + cal.getTimeInMillis();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    //eventsTask = new EventsTask();
                    //eventsTask.execute(queryStr);
                    makeHomeEventsApiCall(queryStr);
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
            //Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            List<Event> events = eventManagerImpl.fetchAllEventsByScreen(Event.EventDisplayScreen.HOME.toString());
            System.out.println("All Offline Events : "+events.size());
            summarizeEventsForHomeScreen(events);
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

    OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
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

            String queryStr = "user_id="+loggedInUser.getUserId()+"&date=" + cal.getTimeInMillis();
            if (internetManager.isInternetConnection((CenesBaseActivity) getActivity())) {
                try {
                    //eventsTask.execute(queryStr);
                    makeHomeEventsApiCall(queryStr);
                } catch (Exception e) {
                    Toast.makeText((CenesBaseActivity) getActivity(), "Bad Request", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText((CenesBaseActivity) getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            }
        }
    };
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
            //eventsTask.cancel(true);
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
        @Override
        protected void onPreExecute() {

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
                return null;//apiManager.getUserEvents(user, queryStr, (CenesBaseActivity) getActivity());
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (getActivity() == null) {
                return;
            }
           if (!getActivity().isFinishing()) {
               if(jsonObject != null) {
                   JSONArray jsonArray = new JSONArray();
                   try {
                       jsonArray = (JSONArray) jsonObject.getJSONArray("data");


                       List<String> headers = new LinkedList<>();
                       Map<String, List<Event>> eventMap = new HashMap<>();

                       Gson gson = new GsonBuilder().create();
                       Type listType = new TypeToken<List<Event>>(){}.getType();
                       List<Event> events = gson.fromJson( jsonObject.getJSONArray("data").toString(), listType);
                       for (Event event: events) {

                           String dateKey = CenesUtils.yyyyMMdd.format(new Date(event.getStartTime()));

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

                       Collections.sort(headers);
                       listAdapter = new HomeScreenAdapter(HomeFragment.this, headers, eventMap);

                       if (events.size() == 0) {
                           homeScreenEventsList.setVisibility(View.GONE);
                           homeNoEvents.setVisibility(View.VISIBLE);
                           homeNoEvents.setTextSize(20f);
                           homeNoEvents.setText("No Event Exists For This Date");
                       } else {
                           homeScreenEventsList.setVisibility(View.VISIBLE);
                           homeScreenEventsList.setAdapter(listAdapter);
                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }


               } else {
                   getCenesActivity().showRequestTimeoutDialog();
               }
           }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //eventsTask.cancel(true);
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

    public void makeHomeEventsApiCall(String queryStr) {

        new GatheringAsyncTask.HomeEventsTask(new GatheringAsyncTask.HomeEventsTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    boolean success = response.getBoolean("success");
                    if (success) {

                        try {

                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<List<Event>>(){}.getType();
                            List<Event> events = gson.fromJson(response.getJSONArray("data").toString(), listType);

                            eventManagerImpl.deleteAllEventsByDisplayAtScreen(Event.EventDisplayScreen.HOME.toString());
                            eventManagerImpl.addEvent(events, Event.EventDisplayScreen.HOME.toString());
                            summarizeEventsForHomeScreen(events);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        showAlert("Alert", response.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(queryStr);
    }

    public void summarizeEventsForHomeScreen(List<Event> events) {
        List<String> headers = new LinkedList<>();
        Map<String, List<Event>> eventMap = new HashMap<>();

        for (Event event: events) {

            String dateKey = CenesUtils.yyyyMMdd.format(new Date(event.getStartTime()));

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

            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
            });
        }

        Collections.sort(headers);
        listAdapter = new HomeScreenAdapter(HomeFragment.this, headers, eventMap);

        if (events.size() == 0) {
            homeScreenEventsList.setVisibility(View.GONE);
            homeNoEvents.setVisibility(View.VISIBLE);
            homeNoEvents.setTextSize(20f);
            homeNoEvents.setText("No Event Exists For This Date");
        } else {
            homeScreenEventsList.setVisibility(View.VISIBLE);
            homeScreenEventsList.setAdapter(listAdapter);
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
