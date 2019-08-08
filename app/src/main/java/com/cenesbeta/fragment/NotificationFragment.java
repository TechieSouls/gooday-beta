package com.cenesbeta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.AlarmActivity;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.DiaryActivity;
import com.cenesbeta.activity.GatheringScreenActivity;
import com.cenesbeta.activity.ReminderActivity;
import com.cenesbeta.adapter.NotificationAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by mandeep on 20/11/17.
 */

public class NotificationFragment extends CenesFragment {

    public final static String TAG = "NotificationFragment";

    private NotificationAdapter notificationAdapter;

    private ListView notificationExpandablelv;

    private RoundedImageView homeProfilePic;
    private ImageView homeIcon;

    private TextView noNotificationsText;

    final int MILLI_TO_HOUR = 1000 * 60 * 60;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;

    private User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_notifications, container, false);
        init(v);
        if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(NotificationFragment.this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homeProfilePic);
        }

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

                            Gson gson = new Gson();

                            Type listType = new TypeToken<List<Notification>>() {}.getType();
                            List<Notification> notifications = new Gson().fromJson(response.getJSONArray("data").toString(), listType);
                            notificationAdapter = new NotificationAdapter(getActivity(), notifications);
                            notificationExpandablelv.setAdapter(notificationAdapter);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();

        ((CenesBaseActivity)getActivity()).ivNotificationFloatingIcon.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getActivity() instanceof CenesBaseActivity) {
                ((CenesBaseActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof ReminderActivity) {
                ((ReminderActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof GatheringScreenActivity) {
                ((GatheringScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof DiaryActivity) {
                ((DiaryActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof AlarmActivity) {
                ((AlarmActivity) getActivity()).hideFooter();
            }
        } catch (Exception e) {

        }
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();

        loggedInUser = userManager.getUser();

        notificationExpandablelv = (ListView) view.findViewById(R.id.notification_expandable_lv);
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
}
