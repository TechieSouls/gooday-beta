package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.EventChat;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EventChatExpandableAdapter extends BaseExpandableListAdapter {

    private GatheringPreviewFragment gatheringPreviewFragment;
    private Map<String, List<EventChat>> eventChatsListMap;
    private List<String> headers;
    LayoutInflater inflter;

    public EventChatExpandableAdapter(GatheringPreviewFragment gatheringPreviewFragment,List<String> headers, Map<String, List<EventChat>> eventChatsListMap){


        this.gatheringPreviewFragment = gatheringPreviewFragment;
        this.eventChatsListMap = eventChatsListMap;
        this.headers = headers;
        this.inflter = (LayoutInflater.from(this.gatheringPreviewFragment.getContext()));

    }


    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.eventChatsListMap.get(this.headers.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public EventChat getChild(int groupPosition, int childPosition) {
        return this.eventChatsListMap.get(this.headers.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertViewHeader, ViewGroup parent) {

        // TODO Auto-generated method stub

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        EventHeaderViewHolder eventHeaderViewHolder;
        if(convertViewHeader == null){
            convertViewHeader = inflter.inflate(R.layout.adapter_event_chats_group_header, null);

            eventHeaderViewHolder = new EventHeaderViewHolder();
            eventHeaderViewHolder.header = (TextView) convertViewHeader.findViewById(R.id.tv_list_title);
            convertViewHeader.setTag(R.layout.adapter_event_chats_group_header, eventHeaderViewHolder);

        } else {
            eventHeaderViewHolder =  (EventHeaderViewHolder) convertViewHeader.getTag(R.layout.adapter_event_chats_group_header);
        }

        String headerTitle = (String) getGroup(groupPosition);

        System.out.println("Header of table : "+headerTitle);

        eventHeaderViewHolder.header.setText(headerTitle);

        return convertViewHeader;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            convertView = inflter.inflate(R.layout.adapter_chat_window, null);

            holder = new ViewHolder();
            holder.chatReadStatus = (ImageView) convertView.findViewById(R.id.chat_read_status);
            holder.chatSentTimeFrom = (TextView) convertView.findViewById(R.id.chat_created_time);
            holder.chatSentTimeSender = (TextView) convertView.findViewById(R.id.chat_created_time_sender);
            holder.chatMessageTo = (TextView) convertView.findViewById(R.id.chat_msg_to);
            holder.chatMessageFrom = (TextView) convertView.findViewById(R.id.chat_msg_from);
            holder.chatFromPic = (RoundedImageView) convertView.findViewById(R.id.chat_from_picture);
            holder.rlToChat = (RelativeLayout) convertView.findViewById(R.id.rl_to_chat);
            holder.llFromChat = (LinearLayout) convertView.findViewById(R.id.ll_from_chat);
            holder.rlChatFromProfileContainer = (RelativeLayout) convertView.findViewById(R.id.rl_chat_from_profile_container);
            convertView.setTag(R.layout.adapter_chat_window,holder);
        } else {

            holder = (ViewHolder) convertView.getTag(R.layout.adapter_chat_window);

        }

        EventChat eventChat = getChild(groupPosition,childPosition);

        if(eventChat.getSenderId().equals(gatheringPreviewFragment.loggedInUser.getUserId())) { // From Chat

           holder.llFromChat.setVisibility(View.VISIBLE);
           holder.rlToChat.setVisibility(View.GONE);
           holder.chatFromPic.setVisibility(View.GONE);
            holder.rlChatFromProfileContainer.setVisibility(View.GONE);

            holder.chatMessageFrom.setText(eventChat.getChat());
            String editedText = "";
            System.out.println("Edited : "+eventChat.getChatEdited());
            if(eventChat.getChatEdited().equals("Yes")) {
                editedText = "(Edited) ";
            } else {
                System.out.println("Edited NO");
            }
            holder.chatSentTimeFrom.setText(editedText + CenesUtils.hhmmaa.format(new Date(eventChat.getCreatedAt()))  );
            System.out.println("Chat Status ::: " +eventChat.getChatStatus());
            if(eventChat.getChatStatus().equals("Read")) {

                holder.chatReadStatus.setImageDrawable(gatheringPreviewFragment.getResources().getDrawable(R.drawable.read_tick));

            } else {

                holder.chatReadStatus.setImageDrawable(gatheringPreviewFragment.getResources().getDrawable(R.drawable.unread_tick));

            }

        } else { // To Chat

            holder.llFromChat.setVisibility(View.GONE);
            System.out.println("rlToChat : : : : : : : ");
            holder.rlToChat.setVisibility(View.VISIBLE);
            holder.chatFromPic.setVisibility(View.VISIBLE);
            holder.rlChatFromProfileContainer.setVisibility(View.VISIBLE);

            holder.chatMessageTo.setText(eventChat.getChat());
            String editedText = "";
            if(eventChat.getChatEdited().equals("Yes")) {
                editedText = "(Edited) ";
            } else {
                System.out.println("Edited NO");
            }
            holder.chatSentTimeSender.setText(editedText + CenesUtils.hhmmaa.format(new Date(eventChat.getCreatedAt())) );
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            requestOptions.circleCrop();

            Glide.with(gatheringPreviewFragment.getContext()).load(eventChat.getUser().getPicture()).apply(requestOptions).into(holder.chatFromPic);

            if (eventChat.getSenderId().equals(gatheringPreviewFragment.event.getCreatedById())) {
                holder.rlChatFromProfileContainer.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.host_gradient_circle));
            } else {
                holder.rlChatFromProfileContainer.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.xml_circle_profile_pic_white_border));
            }
        }
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewHolder {
        private TextView chatSentTimeFrom, chatSentTimeSender, chatMessageTo,chatMessageFrom;
        private ImageView chatReadStatus;
        private RoundedImageView chatFromPic;
        private RelativeLayout rlToChat, rlChatFromProfileContainer;
        private LinearLayout llFromChat;
    }

    class EventHeaderViewHolder {
        TextView header;
    }

}
