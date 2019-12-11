package com.cenesbeta.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.fragment.NotificationFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragmentBkup;
import com.cenesbeta.fragment.gathering.GatheringsFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private List<Notification> notifications;
    private NotificationFragment notificationFragment;
    private LayoutInflater inflter;

    public NotificationAdapter(NotificationFragment notificationFragment, List<Notification> notifications) {
        this.notificationFragment = notificationFragment;
        this.notifications = notifications;
        this.inflter = (LayoutInflater.from(notificationFragment.getActivity()));
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_notifications, null);
            holder = new ViewHolder();
            holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
            holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_time);
            holder.notifcationReadStatus = (TextView) convertView.findViewById(R.id.notification_readstatus);
            holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
            holder.notificationDay = (TextView) convertView.findViewById(R.id.notification_day);
            holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notification notification = (Notification) notifications.get(position);

        System.out.println(notification.toString());
        String notificationText = notification.getMessage();
        if (!(notification.getType() != null && notification.getType().equals("Welcome"))) {
            notificationText = notificationText +  " <b>" + notification.getTitle() + "</b>";
        }
        holder.notificationMessage.setText(Html.fromHtml( notificationText));
        holder.notificationTime.setText(CenesUtils.ddMMM.format(notification.getNotificationTime()).toUpperCase());

        //if (notification.getSenderImage() != null && notification.getSenderImage() != "" && notification.getSenderImage() != "null") {
        //    Glide.with(activity).load(notification.getSenderImage()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
        //} else {
       //     holder.senderPic.setImageResource(R.drawable.default_profile_icon);
        //}

        try {

            if (notification.getType() != null && notification.getType().equals("Welcome")) {

                holder.senderPic.setImageResource(R.drawable.notification_alert_icon);

            } else {

                if (notification.getUser() != null && notification.getUser().getPicture() != null && notification.getUser().getPicture().length() != 0) {
                    Glide.with(notificationFragment.getActivity()).load(notification.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(holder.senderPic);
                } else {
                    holder.senderPic.setImageResource(R.drawable.profile_pic_no_image);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long daysDiff  = (new Date().getTime() - notification.getNotificationTime())/(1000*3600*24);
        System.out.println("Different in Days : "+daysDiff/(1000*3600*24)+"   -----   "+daysDiff);
        if (daysDiff > 0) {
            holder.notificationDay.setText(daysDiff +" Days Ago");
        } else {

            //int hours = CenesUtils.differenceInHours(notification.getNotificationTime(), new Date().getTime());
            int hours = Math.round((new Date().getTime() - notification.getNotificationTime())/(1000*3600));
            System.out.println("Hours Diff : "+hours);
            if (hours == 0) {

                int minutes = Math.round((new Date().getTime() - notification.getNotificationTime())/(1000*60));
                if (minutes == 0) {
                    holder.notificationDay.setText("Just Now");
                } else {
                    holder.notificationDay.setText(minutes+" Minutes Ago");
                }
            } else if (hours == 1) {
                holder.notificationDay.setText(hours+" Hour Ago");
            } else {
                holder.notificationDay.setText(hours+" Hours Ago");
            }
        }
        if (notification.getReadStatus().equals("Read")) {
            holder.llContainer.setBackground(notificationFragment.getActivity().getResources().getDrawable(R.drawable.xml_curved_corner_markread_fill));
            holder.notificationTime.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_markread_color));
            holder.notificationDay.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_markread_color));
            holder.notifcationReadStatus.setVisibility(View.VISIBLE);
        } else {
            holder.llContainer.setBackground(notificationFragment.getActivity().getResources().getDrawable(R.drawable.xml_curved_corner_blue_fill));
            holder.notificationTime.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_selectedText_color));
            holder.notificationDay.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_selectedText_color));
            holder.notifcationReadStatus.setVisibility(View.GONE);
        }

        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notificationFragment.notificationManagerImpl.updateNotificationReadStatus(notification);
                if (notificationFragment.internetManager.isInternetConnection(notificationFragment.getCenesActivity())) {

                    new NotificationAsyncTask(((CenesBaseActivity)notificationFragment.getActivity()).getCenesApplication(), notificationFragment.getActivity());
                    new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            System.out.println(response);
                        }
                    }).execute(notification.getNotificationTypeId());

                }

                GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                gatheringPreviewFragment.event = notification.getEvent();
                ((CenesBaseActivity)notificationFragment.getActivity()).replaceFragment(gatheringPreviewFragment, NotificationFragment.TAG);

               /* if (notification.getType().equals("Gathering")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "notification");
                    bundle.putLong("eventId",notification.getNotificationTypeId());
                    bundle.putString("message", "Your have been invited to...");
                    bundle.putString("title", notification.getTitle());


                    new NotificationAsyncTask(((CenesBaseActivity)activity).getCenesApplication(), activity);
                    new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            System.out.println(response.toString());
                        }
                    }).execute(notification.getNotificationTypeId());

                    GatheringPreviewFragmentBkup gatheringPreviewFragmentBkup = new GatheringPreviewFragmentBkup();
                    gatheringPreviewFragmentBkup.setArguments(bundle);

                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragmentBkup, GatheringPreviewFragmentBkup.TAG);
                }*/ /*else if (notification.getType().equals("Gathering") && notification.getNotificationTypeStatus().equalsIgnoreCase("old")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId",notification.getNotificationTypeId());


                    GatheringPreviewFragmentBkup gatheringPreviewFragment = new GatheringPreviewFragmentBkup();
                    gatheringPreviewFragment.setArguments(bundle);


                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragment, GatheringPreviewFragmentBkup.TAG);

                }*/ /*else if (child.getType().equals("Reminder")) {
                    startActivity(new Intent(getActivity(), ReminderActivity.class));
                    getActivity().finish();
                }*/
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView notificationMessage;
        private TextView notificationTime;
        private TextView notifcationReadStatus;
        private RoundedImageView senderPic;
        private LinearLayout llContainer;
        private TextView notificationDay;
    }
}
