package com.cenesbeta.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.NotificationAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventChat;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.dto.SelectedEventChatDto;
import com.cenesbeta.fragment.NotificationFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class NotificationAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private NotificationFragment notificationFragment;
    private List<Object> notificationList;
    private LayoutInflater layoutInflater;

    public NotificationAdapter(NotificationFragment notificationFragment, List<Object> notificationList) {
        this.notificationFragment = notificationFragment;
        this.notificationList = notificationList;
        this.layoutInflater = LayoutInflater.from(notificationFragment.getContext());
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == VIEW_TYPE_SECTION) {
            return getSectionView(position, convertView, parent);
        } else if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            return getItemView(position, convertView, parent);
        }
        return convertView;
    }

    @NonNull
    private View getItemView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_notification_group_item, parent, false);
            holder = new ViewHolder();
            holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
            holder.notificationTitle = (TextView) convertView.findViewById(R.id.notification_title);
            holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_time);
            holder.notifcationReadStatus = (TextView) convertView.findViewById(R.id.notification_readstatus);
            holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
            holder.notificationDay = (TextView) convertView.findViewById(R.id.notification_day);
            holder.rlContainer = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            holder.rlUnreadDot = (RelativeLayout) convertView.findViewById(R.id.rl_unread_dot);
            holder.notificationActionIcon = (ImageView) convertView.findViewById(R.id.iv_notification_action_icon);

            convertView.setTag(R.layout.adapter_notification_group_item,holder);

        } else if (convertView.getTag(R.layout.adapter_notification_group_item) == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_notification_group_item, parent, false);
            holder = new ViewHolder();
            holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
            holder.notificationTitle = (TextView) convertView.findViewById(R.id.notification_title);
            holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_time);
            holder.notifcationReadStatus = (TextView) convertView.findViewById(R.id.notification_readstatus);
            holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
            holder.notificationDay = (TextView) convertView.findViewById(R.id.notification_day);
            holder.rlContainer = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            holder.rlUnreadDot = (RelativeLayout) convertView.findViewById(R.id.rl_unread_dot);
            holder.notificationActionIcon = (ImageView) convertView.findViewById(R.id.iv_notification_action_icon);
            convertView.setTag(R.layout.adapter_notification_group_item,holder);
        } else {
            holder =  (ViewHolder) convertView.getTag(R.layout.adapter_notification_group_item);
        }

        final Notification notification = (Notification) getItem(position);

        //System.out.println(notification.toString());
        String notificationText = notification.getMessage();
        //if (!(notification.getType() != null && notification.getType().equals("Welcome"))) {
        //System.out.println("Event Title : "+notification.getTitle());
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
        //System.out.println(notificationText);
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
        //System.out.println("Different in Days : "+daysDiff/(1000*3600*24)+"   -----   "+daysDiff);
        if (daysDiff > 0) {
            if(daysDiff < 7) {
                holder.notificationDay.setText(daysDiff + "d");
            }else{
                long weekDiff = (new Date().getTime() - notification.getNotificationTime())/(1000*3600*24)/7;
                holder.notificationDay.setText(weekDiff + "w");
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
            //holder.rlUnreadDot.setVisibility(View.GONE);
            holder.notificationMessage.setTextColor(notificationFragment.getResources().getColor(R.color.notification_read_text));
            holder.notificationTitle.setTextColor(notificationFragment.getResources().getColor(R.color.notification_read_text));
            holder.rlContainer.setBackgroundColor(notificationFragment.getResources().getColor(R.color.white));

        } else {
            //holder.rlUnreadDot.setVisibility(View.VISIBLE);
            holder.notificationTitle.setTextColor(notificationFragment.getResources().getColor(R.color.black));
            holder.notificationMessage.setTextColor(notificationFragment.getResources().getColor(R.color.black));
            holder.rlContainer.setBackgroundColor(notificationFragment.getResources().getColor(R.color.notification_read_bg));

        }

        holder.notificationActionIcon.setVisibility(View.GONE);
        if (notification.getType().equals("Gathering")) {
            if (notification.getAction().equals("Chat")) {
                holder.notificationActionIcon.setVisibility(View.VISIBLE);
                holder.notificationActionIcon.setImageResource(R.drawable.notificaiton_chat_icon);
            } else if (notification.getAction().equals("Create")) {
                holder.notificationActionIcon.setVisibility(View.VISIBLE);
                holder.notificationActionIcon.setImageResource(R.drawable.mdi_party_popper);
            }
        }


        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (notification.getReadStatus().equals("UnRead")) {

                    notification.setReadStatus("Read");
                    notificationFragment.notificationManagerImpl.updateNotificationReadStatus(notification);
                    List<Notification> notifications = notificationFragment.notificationManagerImpl.fetchAllNotifications();

                    notificationFragment.markNotificationAsReadInMap(notification);

                    if (notificationFragment.internetManager.isInternetConnection(notificationFragment.getCenesActivity())) {

                        new NotificationAsyncTask(((CenesBaseActivity)notificationFragment.getActivity()).getCenesApplication(), notificationFragment.getActivity());
                        new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                System.out.println(response);
                            }
                        }).execute(notification.getNotificationTypeId());

                    }
                    holder.rlContainer.setBackgroundColor(notificationFragment.getResources().getColor(R.color.white));
                    holder.notificationMessage.setTextColor(notificationFragment.getResources().getColor(R.color.notification_read_text));
                    holder.notificationTitle.setTextColor(notificationFragment.getResources().getColor(R.color.notification_read_text));
                    //notifyDataSetChanged();
                }

                if(notification.getEvent() != null) {
                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.event = notification.getEvent();
                    if (notification.getAction().equals("Chat")) {
                        SelectedEventChatDto selectedEventChatDto = new SelectedEventChatDto();
                        selectedEventChatDto.setShowChatWindow(true);
                        selectedEventChatDto.setMessage(notification.getMessage());
                        gatheringPreviewFragment.selectedEventChatDto = selectedEventChatDto;
                    }
                    ((CenesBaseActivity) notificationFragment.getActivity()).replaceFragment(gatheringPreviewFragment, NotificationFragment.TAG);
                }

            }
        });
        return convertView;
    }

    @NonNull
    private View getSectionView(int position, View convertView, ViewGroup parent) {
        NotificationHeaderViewHolder notificationHeaderViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_notification_group_header, null);
            notificationHeaderViewHolder = new NotificationHeaderViewHolder();
            notificationHeaderViewHolder.header = (TextView) convertView.findViewById(R.id.tv_list_title);
            notificationHeaderViewHolder.header_separator = (View) convertView.findViewById(R.id.view_header_separator);
            convertView.setTag(R.layout.adapter_notification_group_header, notificationHeaderViewHolder);

        } else if (convertView.getTag(R.layout.adapter_event_chats_group_header) == null) {

            convertView = layoutInflater.inflate(R.layout.adapter_notification_group_header, null);
            notificationHeaderViewHolder = new NotificationHeaderViewHolder();
            notificationHeaderViewHolder.header = (TextView) convertView.findViewById(R.id.tv_list_title);
            notificationHeaderViewHolder.header_separator = (View) convertView.findViewById(R.id.view_header_separator);
            convertView.setTag(R.layout.adapter_notification_group_header, notificationHeaderViewHolder);

        } else {
            notificationHeaderViewHolder = (NotificationHeaderViewHolder) convertView.getTag();
        }

        String headerTitle = (String) getItem(position);
        if(position == 0){
            notificationHeaderViewHolder.header_separator.setVisibility(View.GONE);
        }else{
            notificationHeaderViewHolder.header_separator.setVisibility(View.VISIBLE);
        }

        System.out.println("Header of table : "+headerTitle);
        notificationHeaderViewHolder.header.setText(headerTitle);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        Object listItem = getItem(position);
        if (listItem instanceof Notification) {
            return VIEW_TYPE_ITEM;
        } else if (listItem instanceof String) {
            return VIEW_TYPE_SECTION;
        }
        return VIEW_TYPE_NONE;
    }

    public void refreshItems(List<Object> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        private TextView notificationMessage, notificationTitle;
        private TextView notificationTime;
        private TextView notifcationReadStatus;
        private RoundedImageView senderPic;
        private RelativeLayout rlUnreadDot, rlContainer;
        private TextView notificationDay;
        private ImageView notificationActionIcon;
    }

    class NotificationHeaderViewHolder {
        TextView header;
        View header_separator;
    }

}
