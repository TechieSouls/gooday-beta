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
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private List<Notification> notifications;
    private Activity activity;
    private LayoutInflater inflter;

    public NotificationAdapter(Activity activity, List<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
        this.inflter = (LayoutInflater.from(activity));
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
        holder.notificationMessage.setText(Html.fromHtml("<b>" + notification.getSenderName() + "</b> " + notification.getMessage() + " <b>" + notification.getTitle() + "</b>"));
        holder.notificationTime.setText(CenesUtils.ddMMM.format(notification.getNotificationTime()).toUpperCase());

        //if (notification.getSenderImage() != null && notification.getSenderImage() != "" && notification.getSenderImage() != "null") {
        //    Glide.with(activity).load(notification.getSenderImage()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
        //} else {
       //     holder.senderPic.setImageResource(R.drawable.default_profile_icon);
        //}

        try {
            Glide.with(activity).load(notification.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int daysDiff = CenesUtils.daysBetween(notification.getNotificationTime(), new Date().getTime());
        if (daysDiff > 0) {
            holder.notificationDay.setText(daysDiff +" Days Ago");
        } else {

            int hours = CenesUtils.differenceInHours(notification.getNotificationTime(), new Date().getTime());
            System.out.println("Hours Diff : "+hours);
            if (hours == 0) {
                holder.notificationDay.setText("Just Now");
            } else if (hours == 1) {
                holder.notificationDay.setText(hours+" Hour Ago");
            } else {
                holder.notificationDay.setText(hours+" Hours Ago");
            }
        }
        if (notification.getReadStatus().equals("Read")) {
            holder.llContainer.setBackground(activity.getResources().getDrawable(R.drawable.xml_curved_corner_markread_fill));
            holder.notificationTime.setTextColor(activity.getResources().getColor(R.color.cenes_markread_color));
            holder.notificationDay.setTextColor(activity.getResources().getColor(R.color.cenes_markread_color));
            holder.notifcationReadStatus.setVisibility(View.VISIBLE);
        } else {
            holder.llContainer.setBackground(activity.getResources().getDrawable(R.drawable.xml_curved_corner_blue_fill));
            holder.notificationTime.setTextColor(activity.getResources().getColor(R.color.cenes_selectedText_color));
            holder.notificationDay.setTextColor(activity.getResources().getColor(R.color.cenes_selectedText_color));
            holder.notifcationReadStatus.setVisibility(View.GONE);
        }



        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getType().equals("Gathering")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "notification");
                    bundle.putLong("eventId",notification.getNotificationTypeId());
                    bundle.putString("message", "Your have been invited to...");
                    bundle.putString("title", notification.getTitle());

                    /*Intent intent = new Intent(getActivity(), GatheringScreenActivity.class);
                    intent.putExtra("dataFrom", "push");
                    intent.putExtra("eventId", child.getNotificationTypeId());
                    intent.putExtra("message", "Your have been invited to...");
                    intent.putExtra("title", child.getTitle());
                    startActivity(intent);
                    getActivity().finish();*/

                    new NotificationAsyncTask(((CenesBaseActivity)activity).getCenesApplication(), activity);
                    new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            System.out.println(response.toString());
                        }
                    }).execute(notification.getNotificationTypeId());

                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.setArguments(bundle);

                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragment, GatheringPreviewFragment.TAG);
                } /*else if (notification.getType().equals("Gathering") && notification.getNotificationTypeStatus().equalsIgnoreCase("old")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId",notification.getNotificationTypeId());


                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.setArguments(bundle);


                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragment, GatheringPreviewFragment.TAG);

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
