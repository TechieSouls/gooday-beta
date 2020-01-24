package com.cenesbeta.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.NotificationAdapter;
import com.cenesbeta.adapter.NotificationExpandableAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.NotificationManagerImpl;
import com.cenesbeta.database.manager.UserManager;
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

    private NotificationAdapter notificationAdapter;

    private ListView notificationExpandablelv;
    private ExpandableListView elvNotificationList;

    private RoundedImageView homeProfilePic;
    private ImageView homeIcon;

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
       // notificationManagerImpl.deleteAllNotifications();
       loadNotifications();

        ((CenesBaseActivity)getActivity()).ivNotificationFloatingIcon.setVisibility(View.GONE);

        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity)  getActivity()).activateFooterIcon(NotificationFragment.TAG);
        headers = new ArrayList<>();

        callMixPanel();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
       try {
           ((CenesBaseActivity) getActivity()).showFooter();
           ((CenesBaseActivity)  getActivity()).activateFooterIcon(NotificationFragment.TAG);

           /*  if (getActivity() instanceof CenesBaseActivity) {
                ((CenesBaseActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof GatheringScreenActivity) {
                ((GatheringScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof DiaryActivity) {
                ((DiaryActivity) getActivity()).hideFooter();
            } */
        } catch (Exception e) {

        }
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();

        loggedInUser = userManager.getUser();

        elvNotificationList = (ExpandableListView) view.findViewById(R.id.notification_expandable_lv);
        homeProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) view.findViewById(R.id.home_icon);
        noNotificationsText = (TextView) view.findViewById(R.id.no_notifications_text);

        homeIcon.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
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
    public void loadNotifications(){

        List<Notification> notifications = notificationManagerImpl.fetchAllNotifications();
        if(notifications.size() == 0 ){

            System.out.println("I am here 3333...................");

            if (internetManager.isInternetConnection(getCenesActivity())) {
                System.out.println("I am here ...................");
                shimmerFrameLayout.setVisibility(View.VISIBLE);
            }
        }else {
            filterNotification(notifications);
        }

        /****/
        if (internetManager.isInternetConnection(getCenesActivity())) {


            new NotificationAsyncTask(cenesApplication, getActivity());
            new NotificationAsyncTask.NotificationListTask(new NotificationAsyncTask.NotificationListTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    try {
                        JSONObject notificationResponse = response;
                        if (notificationResponse != null && notificationResponse.getBoolean("success")) {
                            JSONArray notificationArray = notificationResponse.getJSONArray("data");
                            if (notificationArray.length() == 0) {
                                noNotificationsText.setVisibility(View.VISIBLE);
                            } else {
                                noNotificationsText.setVisibility(View.GONE);

                                Type listType = new TypeToken<List<Notification>>() {}.getType();
                                final List<Notification> notificationsTemp = new Gson().fromJson(response.getJSONArray("data").toString(), listType);

                                //To Run Code of block in background
                                AsyncTask.execute(new Runnable() {
                                      @Override
                                      public void run() {
                                          notificationManagerImpl.deleteAllNotifications();
                                          notificationManagerImpl.addNotification(notificationsTemp);
                                      }
                                  });
                                filterNotification(notificationsTemp);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        }

    }
    public void filterNotification(List<Notification> notifications){
        notificationMapList = new HashMap<>();
        headers = new ArrayList<>();
        List<Notification> newNotifications = new ArrayList<>();
        List<Notification> seenNotifications = new ArrayList<>();

        for (Notification notification: notifications) {
            //Seen notification
            if (notification.getReadStatus().equals("Read")) {
                seenNotifications.add(notification);
            } else {
                newNotifications.add(notification);
            }
        }

        if (newNotifications.size() > 0) {
            notificationMapList.put(NEW_NOTIFICATION, newNotifications);
            headers.add(NEW_NOTIFICATION);
        }

        if (seenNotifications.size() > 0) {
            notificationMapList.put(SEEN_NOTIFICATION, seenNotifications);
            headers.add(SEEN_NOTIFICATION);
        }

        shimmerFrameLayout.hideShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        notificationExpandableAdapter = new NotificationExpandableAdapter(NotificationFragment.this, headers, notificationMapList);
        elvNotificationList.setAdapter(notificationExpandableAdapter);
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
}
