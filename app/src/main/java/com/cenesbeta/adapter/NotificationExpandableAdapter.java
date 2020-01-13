package com.cenesbeta.adapter;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.fragment.NotificationFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;


import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationExpandableAdapter extends BaseExpandableListAdapter {

    private NotificationFragment notificationFragment;
    private List<String> headers;
    private Map<String, List<Notification>> notificationListMap;

    public NotificationExpandableAdapter(NotificationFragment notificationFragment, List<String> headers, Map<String, List<Notification>> modsCastersListMap) {

        this.notificationFragment = notificationFragment;
        this.headers = headers;
        this.notificationListMap = modsCastersListMap;
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.notificationListMap.get(this.headers.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public Notification getChild(int groupPosition, int childPosition) {
        return this.notificationListMap.get(this.headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        NotificationHeaderViewHolder notificationHeaderViewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(notificationFragment.getContext()).inflate(R.layout.adapter_notification_group_header, parent, false);

            notificationHeaderViewHolder = new NotificationHeaderViewHolder();
            notificationHeaderViewHolder.header = (TextView) convertView.findViewById(R.id.tv_list_title);
            notificationHeaderViewHolder.header_separator = (View) convertView.findViewById(R.id.view_header_separator);
            convertView.setTag(notificationHeaderViewHolder);

        } else {
            notificationHeaderViewHolder =  (NotificationHeaderViewHolder) convertView.getTag();
        }

        String headerTitle = (String) getGroup(groupPosition);
        if(groupPosition == 0){
            notificationHeaderViewHolder.header_separator.setVisibility(View.GONE);
        }else{
            notificationHeaderViewHolder.header_separator.setVisibility(View.VISIBLE);
        }

        System.out.println("Header of table : "+headerTitle);

        notificationHeaderViewHolder.header.setText(headerTitle);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub\

       final ViewHolder holder;

        if(convertView==null){
            convertView = LayoutInflater.from(notificationFragment.getContext()).inflate(R.layout.adapter_notification_group_item, parent, false);
            holder = new ViewHolder();
            holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
            holder.notificationTitle = (TextView) convertView.findViewById(R.id.notification_title);
            holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_time);
            holder.notifcationReadStatus = (TextView) convertView.findViewById(R.id.notification_readstatus);
            holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
            holder.notificationDay = (TextView) convertView.findViewById(R.id.notification_day);
            holder.rlContainer = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            holder.rlUnreadDot = (RelativeLayout) convertView.findViewById(R.id.rl_unread_dot);
            convertView.setTag(holder);

        } else {
            holder =  (ViewHolder) convertView.getTag();
        }

        final Notification notification = (Notification) getChild(groupPosition, childPosition);

        System.out.println(notification.toString());
        String notificationText = notification.getMessage();
        //if (!(notification.getType() != null && notification.getType().equals("Welcome"))) {
        System.out.println("Event Title : "+notification.getTitle());
        holder.notificationTitle.setText(Html.fromHtml("("+notification.getTitle()+")"));
        //}

        notificationText = notificationText.replace("accepted", "<font color='#FFAA4E'>accepted</font>");
        notificationText = notificationText.replace("declined", "<font color='#FFAA4E'>declined</font>");
        notificationText = notificationText.replace("deleted", "<font color='#FFAA4E'>deleted</font>");
        notificationText = notificationText.replace("changed", "<font color='#FFAA4E'>changed</font>");
        notificationText = notificationText.replace("updated", "<font color='#FFAA4E'>updated</font>");
        notificationText = notificationText.replace("modified", "<font color='#FFAA4E'>modified</font>");
        notificationText = notificationText.replace("added", "<font color='#FFAA4E'>added</font>");
        notificationText = notificationText.replace("Invitation", "<font color='#FFAA4E'>Invitation</font>");
        System.out.println(notificationText);
        holder.notificationMessage.setText(Html.fromHtml( notificationText));


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
            if(daysDiff < 7) {
                holder.notificationDay.setText(daysDiff + "d");
            }else{
                long weekDiff = (new Date().getTime() - notification.getNotificationTime())/(1000*3600*24)/7;
                if(weekDiff < 4) {
                    holder.notificationDay.setText(weekDiff + "w");
                }else{
                    long monthDiff = (new Date().getTime() - notification.getNotificationTime())/(1000*3600*24)/30;
                    if(monthDiff < 12) {
                        holder.notificationDay.setText(monthDiff + "mo");
                    }else{
                        long yearDiff = monthDiff/12;
                        holder.notificationDay.setText(yearDiff + "yr");
                    }


                }

            }
        } else {

            int hours = Math.round((new Date().getTime() - notification.getNotificationTime())/(1000*3600));
            System.out.println("Hours Diff : "+hours);
            if (hours == 0) {
                System.out.println("abc 1");
                int minutes = Math.round((new Date().getTime() - notification.getNotificationTime())/(1000*60));
                if (minutes == 0) {
                    System.out.println("abc 2");
                    int seconds = Math.round((new Date().getTime() - notification.getNotificationTime())/(1000));
                    System.out.println("seconds  : "+seconds);
                    holder.notificationDay.setText(seconds+"s");
                } else {
                    System.out.println("abc 3");
                    holder.notificationDay.setText(minutes+"m");
                }
            } else {
                System.out.println("abc 4");
                System.out.println("hours hours hours");
                holder.notificationDay.setText(hours+"h");
            }
        }
        if (notification.getReadStatus().equals("Read")) {
            holder.rlUnreadDot.setVisibility(View.GONE);
        } else {
            holder.rlUnreadDot.setVisibility(View.VISIBLE);
        }

        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    holder.notificationTime.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_selectedText_color));
                    holder.notificationDay.setTextColor(notificationFragment.getActivity().getResources().getColor(R.color.cenes_selectedText_color));


                notificationFragment.notificationManagerImpl.updateNotificationReadStatus(notification);
                List<Notification> notifications = notificationFragment.notificationManagerImpl.fetchAllNotifications();
                notificationFragment.filterNotification(notifications);

                if (notificationFragment.internetManager.isInternetConnection(notificationFragment.getCenesActivity())) {

                    new NotificationAsyncTask(((CenesBaseActivity)notificationFragment.getActivity()).getCenesApplication(), notificationFragment.getActivity());
                    new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            System.out.println(response);
                        }
                    }).execute(notification.getNotificationTypeId());

                }


                if(notification.getEvent() != null) {
                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.event = notification.getEvent();
                    ((CenesBaseActivity) notificationFragment.getActivity()).replaceFragment(gatheringPreviewFragment, NotificationFragment.TAG);
                }

            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewHolder {
        private TextView notificationMessage, notificationTitle;
        private TextView notificationTime;
        private TextView notifcationReadStatus;
        private RoundedImageView senderPic;
        private RelativeLayout rlUnreadDot, rlContainer;
        private TextView notificationDay;
    }

    class NotificationHeaderViewHolder {
        TextView header;
        View header_separator;
    }
}
