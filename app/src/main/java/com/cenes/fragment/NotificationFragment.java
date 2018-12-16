package com.cenes.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Notification;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 20/11/17.
 */

public class NotificationFragment extends CenesFragment {

    public final static String TAG = "NotificationFragment";

    private NotificationAdapter notificationAdapter;

    private ExpandableListView notificationExpandablelv;

    private RoundedImageView homeProfilePic;

    private TextView noNotificationsText;

    final int MILLI_TO_HOUR = 1000 * 60 * 60;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    private AlertManager alertManager;
    private DeviceManager deviceManager;
    private ValidationManager validationManager;

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
        new NotificationListTask().execute();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                new MarkNotificationReadTask().execute();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getActivity() instanceof HomeScreenActivity) {
                ((HomeScreenActivity) getActivity()).hideFooter();
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
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        validationManager = coreManager.getValidatioManager();
        alertManager = coreManager.getAlertManager();

        loggedInUser = userManager.getUser();

        notificationExpandablelv = (ExpandableListView) view.findViewById(R.id.notification_expandable_lv);
        homeProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);

        noNotificationsText = (TextView) view.findViewById(R.id.no_notifications_text);

        homeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }


    class NotificationListTask extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId();
            JSONObject response = apiManager.getNotificationsByUserId(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
            List<String> headers = new ArrayList<>();

            Map<String, List<Notification>> notificationMap = new HashMap<>();
            try {
                JSONObject notificationResponse = response;
                if (notificationResponse != null && notificationResponse.getBoolean("success")) {
                    JSONArray notificationArray = notificationResponse.getJSONArray("data");
                    if (notificationArray.length() == 0) {
                        noNotificationsText.setVisibility(View.VISIBLE);
                    } else {
                        noNotificationsText.setVisibility(View.GONE);
                        for (int i = 0; i < notificationArray.length(); i++) {

                            List<Notification> notifications = null;
                            JSONObject notificationObj = notificationArray.getJSONObject(i);

                            Boolean isNew = false;
                            long hours = (new Date().getTime() - notificationObj.getLong("createdAt")) / (60 * 60 * 1000);

                            if (hours < 1) {
                                isNew = true;
                            }

                            if (isNew) {
                                if (notificationMap.containsKey("NEW")) {
                                    notifications = notificationMap.get("NEW");
                                }
                            } else {
                                if (notificationMap.containsKey("EARLIER")) {
                                    notifications = notificationMap.get("EARLIER");
                                }
                            }

                            Notification notification = new Notification();
                            notification.setMessage(notificationObj.getString("message"));
                            notification.setTitle(notificationObj.getString("title"));
                            notification.setSenderName(notificationObj.getString("sender"));
                            try { notification.setSenderImage(notificationObj.getString("senderPicture")); } catch (Exception e) { }
                            notification.setNotificationTypeStatus(notificationObj.getString("notificationTypeStatus"));
                            notification.setType(notificationObj.getString("type"));
                            notification.setNotificationTypeId(notificationObj.getLong("notificationTypeId"));
                            notification.setNotificationTime(notificationObj.getLong("createdAt"));

                            if (notifications == null) {
                                notifications = new ArrayList<>();
                                if (isNew) {
                                    headers.add("NEW");
                                } else {
                                    headers.add("EARLIER");
                                }
                            }
                            notifications.add(notification);
                            if (isNew) {
                                notificationMap.put("NEW", notifications);
                            } else {
                                notificationMap.put("EARLIER", notifications);
                            }
                        }
                        notificationAdapter = new NotificationAdapter(NotificationFragment.this, headers, notificationMap);
                        notificationExpandablelv.setAdapter(notificationAdapter);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class MarkNotificationReadTask extends AsyncTask<String, String, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(getActivity());
            //progressDialog.setMessage("Loading...");
            //progressDialog.setIndeterminate(false);
            //progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setCancelable(false);
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId();
            JSONObject response = apiManager.markNotificationAsReadByUserIdAndNotificatonId(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            //progressDialog.hide();
            //progressDialog.dismiss();
            //progressDialog = null;
        }
    }

    public class NotificationAdapter extends BaseExpandableListAdapter {
        NotificationFragment context;
        List<String> headers;
        Map<String, List<Notification>> notificationsMap;
        LayoutInflater inflter;

        public NotificationAdapter(NotificationFragment applicationContext, List<String> headers, Map<String, List<Notification>> notificationsMap) {
            this.context = applicationContext;
            this.headers = headers;
            this.notificationsMap = notificationsMap;
            inflter = (LayoutInflater.from(context.getContext()));
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.notificationsMap.get(this.headers.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.adapter_notifications, null);
                holder = new ViewHolder();
                holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
                holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_date);
                holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
                holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Notification child = (Notification) getChild(groupPosition, childPosition);

            holder.notificationMessage.setText(Html.fromHtml("<b>" + child.getSenderName() + "</b> " + child.getMessage() + " <b>" + child.getTitle() + "</b>"));
            holder.notificationTime.setText(CenesUtils.MMMdd.format(child.getNotificationTime()) + " at " + CenesUtils.hhmmaa.format(child.getNotificationTime()));

            if (child.getSenderImage() != null && child.getSenderImage() != "" && child.getSenderImage() != "null") {
                Glide.with(this.context).load(child.getSenderImage()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
            } else {
                holder.senderPic.setImageResource(R.drawable.default_profile_icon);
            }

            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (child.getType().equals("Gathering") && !child.getNotificationTypeStatus().equalsIgnoreCase("old")) {
                        Intent intent = new Intent(getActivity(), GatheringScreenActivity.class);
                        intent.putExtra("dataFrom", "push");
                        intent.putExtra("eventId", child.getNotificationTypeId());
                        intent.putExtra("message", "Your have been invited to...");
                        intent.putExtra("title", child.getTitle());
                        startActivity(intent);
                        getActivity().finish();
                    } else if (child.getType().equals("Gathering") && child.getNotificationTypeStatus().equalsIgnoreCase("old")) {
                        Intent intent = new Intent(getActivity(), GatheringScreenActivity.class);
                        intent.putExtra("dataFrom", "gathering_push");
                        intent.putExtra("eventId", child.getNotificationTypeId());
                        startActivity(intent);
                        getActivity().finish();
                    } else if (child.getType().equals("Reminder")) {
                        startActivity(new Intent(getActivity(), ReminderActivity.class));
                        getActivity().finish();
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.notificationsMap.get(this.headers.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headers.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.headers.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            ExpandableListView mExpandableListView = (ExpandableListView) parent;
            mExpandableListView.expandGroup(groupPosition);
            HeaderViewHolder holder;
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.adapter_notification_headers, null);
                holder = new HeaderViewHolder();
                holder.notificationTitle = (TextView) convertView.findViewById(R.id.notification_title);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            String headerTitle = (String) getGroup(groupPosition);
            holder.notificationTitle.setText(headerTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class ViewHolder {
            private TextView notificationMessage;
            private TextView notificationTime;
            private RoundedImageView senderPic;
            private LinearLayout llContainer;
        }

        class HeaderViewHolder {
            private ExpandableListView expandableListView;
            private TextView notificationTitle;
        }
    }

}
