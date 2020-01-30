package com.cenesbeta.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.NotificationExpandableAdapter;
import com.cenesbeta.api.NotificationAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.NotificationManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.NotificationDto;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 20/11/17.
 */

public class NotificationFragment extends CenesFragment {

    public final static String TAG = "NotificationFragment";
    public enum NotificationApiCall {Counts, List};

    private ExpandableListView elvNotificationList;

    private RoundedImageView homeProfilePic;
    private ImageView homeIcon, ivListLoader;
    private SwipeRefreshLayout swipeRefreshNotifications;

    private TextView noNotificationsText;
    private View fragmentView;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    public InternetManager internetManager;
    private User loggedInUser;
    public NotificationManagerImpl notificationManagerImpl;
    private ShimmerFrameLayout shimmerFrameLayout;

    private NotificationExpandableAdapter notificationExpandableAdapter;
    private List<String> headers;
    private Map<String, List<Notification>> notificationMapList;
    private static String NEW_NOTIFICATION = "New";
    private static String SEEN_NOTIFICATION = "Seen";
    private NotificationDto notificationDto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (fragmentView != null) {

            return fragmentView;
        }

        View v = inflater.inflate(R.layout.activity_notifications, container, false);
        fragmentView = v;
        init(v);
        shimmerFrameLayout = (ShimmerFrameLayout) v.findViewById(R.id.shimmer_view_container);


        if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(NotificationFragment.this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homeProfilePic);
        }

        notificationManagerImpl = new NotificationManagerImpl(cenesApplication);
        //notificationManagerImpl.deleteAllNotifications();
        loadNotifications();
        ((CenesBaseActivity)getActivity()).ivNotificationFloatingIcon.setVisibility(View.GONE);
        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(NotificationFragment.TAG);
        callMixPanel();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
       try {
           ((CenesBaseActivity) getActivity()).showFooter();
           ((CenesBaseActivity)  getActivity()).activateFooterIcon(NotificationFragment.TAG);
        } catch (Exception e) {

        }
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();

        loggedInUser = userManager.getUser();

        notificationMapList = new HashMap<>();
        headers = new ArrayList<>();
        notificationDto = new NotificationDto();
        notificationDto.setPageNumber(0);
        notificationDto.setTotalNotificationCounts(0);
        notificationDto.setAllNotifications(new ArrayList<Notification>());
        notificationDto.setNewNotifications(new ArrayList<Notification>());
        notificationDto.setSeenNotifications(new ArrayList<Notification>());

        elvNotificationList = (ExpandableListView) view.findViewById(R.id.notification_expandable_lv);
        homeProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) view.findViewById(R.id.home_icon);
        noNotificationsText = (TextView) view.findViewById(R.id.no_notifications_text);
        ivListLoader = (ImageView) view.findViewById(R.id.iv_list_loader);
        swipeRefreshNotifications = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh_notifications);


        homeIcon.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
        elvNotificationList.setOnScrollListener(notificationListListener);
        swipeRefreshNotifications.setOnRefreshListener(swipeDownListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_icon:

                    ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                    ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);
                    break;

                case R.id.home_profile_pic:
                    if (getActivity() instanceof CenesBaseActivity) {
                        ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;
            }
        }
    };

    AbsListView.OnScrollListener notificationListListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            //Algorithm to check if the last item is visible or not
            final int lastItem = firstVisibleItem + visibleItemCount;

            //System.out.println("firstVisibleItem , visibleItemCount, totalItemCount : "+firstVisibleItem+" -- "+visibleItemCount+" -- "+totalItemCount);
            if(lastItem == totalItemCount && totalItemCount != 0) {
                // you have reached end of list, load more data
                if (notificationDto.getAllNotifications().size() < notificationDto.getTotalNotificationCounts()) {
                    if (notificationDto.isMadeApiCall()) {
                        prepareNotificationListCall();
                        notificationDto.setMadeApiCall(false);
                    }
                }

            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener swipeDownListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            notificationMapList = new HashMap<>();
            headers = new ArrayList<>();
            notificationDto = new NotificationDto();
            notificationDto.setPageNumber(0);
            notificationDto.setTotalNotificationCounts(0);
            notificationDto.setAllNotifications(new ArrayList<Notification>());
            notificationDto.setNewNotifications(new ArrayList<Notification>());
            notificationDto.setSeenNotifications(new ArrayList<Notification>());
            prepareNotificationCountCall();
        }
    };


    public void onRefreshCalled() {
        //If its an offline mode
        if (!internetManager.isInternetConnection(getCenesActivity())) {
            swipeRefreshNotifications.setRefreshing(false);
        } else {
            prepareNotificationCountCall();

        }
    }
    public void loadNotifications(){

        //If its an offline mode
        if (!internetManager.isInternetConnection(getCenesActivity())) {

            List<Notification> notifications = notificationManagerImpl.fetchAllNotifications();
            if (notifications.size() > 0) {
                filterNotification(notifications);
            }
        } else {

            shimmerFrameLayout.setVisibility(View.VISIBLE);
            prepareNotificationCountCall();

        }
    }
    public void filterNotification(List<Notification> notifications) {

        List<Notification> newNotifications = new ArrayList<>();
        if (notificationMapList.containsKey(NEW_NOTIFICATION)) {
            newNotifications = notificationMapList.get(NEW_NOTIFICATION);
        }
        List<Notification> seenNotifications = new ArrayList<>();
        if (notificationMapList.containsKey(SEEN_NOTIFICATION)) {
            seenNotifications = notificationMapList.get(SEEN_NOTIFICATION);
        }

        for (Notification notification: notifications) {
            //Seen notification
            if (notification.getReadStatus().equals("Read")) {
                seenNotifications.add(notification);
            } else {
                newNotifications.add(notification);
            }
        }

        notificationDto.setNewNotifications(newNotifications);
        notificationDto.setSeenNotifications(seenNotifications);

        if (newNotifications.size() > 0) {
            notificationMapList.put(NEW_NOTIFICATION, newNotifications);
            if (headers.size() == 0) {
                headers.add(NEW_NOTIFICATION);
            }
        }

        if (seenNotifications.size() > 0) {
            notificationMapList.put(SEEN_NOTIFICATION, seenNotifications);
            if (headers.size() == 1) {
                headers.add(SEEN_NOTIFICATION);
            }
        }

        shimmerFrameLayout.hideShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        if (notificationDto.getPageNumber() == 0) {
            notificationExpandableAdapter = new NotificationExpandableAdapter(NotificationFragment.this, headers, notificationMapList);
            elvNotificationList.setAdapter(notificationExpandableAdapter);
        } else {
            notificationExpandableAdapter.notifyDataSetChanged();
        }
    }

    public void callMixPanel() {
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("Action","Notification Screen Opened");
            props.put("UserEmail",loggedInUser.getEmail());
            props.put("UserName",loggedInUser.getName());
            props.put("Device","Android");
            mixpanel.track("NotificationScreen", props);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void prepareNotificationListCall() {

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ NotificationAPI.get_pageable_notifications);
        asyncTaskDto.setQueryStr("userId="+loggedInUser.getUserId()+"&pageNumber="+notificationDto.getPageNumber()+"&offset=20");

        if (notificationDto.getPageNumber() != 0) {
            ivListLoader.setVisibility(View.VISIBLE);
            Glide.with(getContext()).asGif().load(R.drawable.ios_spinner).into(ivListLoader);
        }
        makeGetAsyncCall(asyncTaskDto, NotificationApiCall.List);
    }


    public void prepareNotificationCountCall() {

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ NotificationAPI.get_notification_counts);
        asyncTaskDto.setQueryStr("recepientId="+loggedInUser.getUserId());

        makeGetAsyncCall(asyncTaskDto, NotificationApiCall.Counts);
    }

    public void makeGetAsyncCall(AsyncTaskDto asyncTaskDto, final NotificationApiCall notificationApiCall) {

        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {
                try {

                    boolean success = false;

                    if( response != null) {

                        success = response.getBoolean("success");
                    }

                    if (notificationApiCall.equals(NotificationApiCall.Counts)) {
                        System.out.println("A1");
                        if (success) {
                            System.out.println("A1 1");
                            notificationDto.setTotalNotificationCounts(response.getInt("data"));
                            if (notificationDto.getTotalNotificationCounts() > 0) {
                                prepareNotificationListCall();
                            }
                        }else{
                            shimmerFrameLayout.setVisibility(View.GONE);
                        }
                    } else if (notificationApiCall.equals(NotificationApiCall.List)) {
                        System.out.println("A2");
                        ivListLoader.setVisibility(View.GONE);

                        Type listType = new TypeToken<List<Notification>>() {}.getType();
                        final List<Notification> notificationsTemp = new Gson().fromJson(response.getJSONArray("data").toString(), listType);

                        List<Notification> allNotificationTemp = notificationDto.getAllNotifications();
                        allNotificationTemp.addAll(notificationsTemp);
                        notificationDto.setAllNotifications(allNotificationTemp);
                        //We will reload the notification in local database only once
                        //that is at very first time when page loads
                            //To Run Code of block in background
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (notificationDto.getPageNumber() == 0) {
                                        System.out.println("A3");
                                        notificationManagerImpl.deleteAllNotifications();
                                    }
                                    notificationManagerImpl.addNotification(notificationsTemp);
                                }
                            });

                        if (notificationDto.getPageNumber() == 0) {
                            System.out.println("A4");
                            swipeRefreshNotifications.setRefreshing(false);
                            notificationDto.setSeenNotifications(new ArrayList<Notification>());
                            notificationDto.setNewNotifications(new ArrayList<Notification>());
                        }
                        if (notificationsTemp.size() > 0) {
                            System.out.println("A5");
                            notificationDto.setMadeApiCall(true);
                            filterNotification(notificationsTemp);
                            int pageNumber = notificationDto.getPageNumber() + 20;
                            notificationDto.setPageNumber(pageNumber);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(asyncTaskDto);
    }

    public void scrollToTop() {
        elvNotificationList.smoothScrollToPositionFromTop(0, 0, 1);
    }

}
