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
import com.cenesbeta.R;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventChat;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class EventChatBaseAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private GatheringPreviewFragment gatheringPreviewFragment;
    private List<Object> eventChatList;
    private LayoutInflater layoutInflater;

    public EventChatBaseAdapter(GatheringPreviewFragment gatheringPreviewFragment, List<Object> eventChatList) {
        this.gatheringPreviewFragment = gatheringPreviewFragment;
        this.eventChatList = eventChatList;
        this.layoutInflater = LayoutInflater.from(gatheringPreviewFragment.getContext());
    }

    @Override
    public int getCount() {
        return eventChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventChatList.get(position);
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

        ChatViewHolder chatViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_chat_window, parent, false);
            chatViewHolder = new ChatViewHolder();

            chatViewHolder.chatReadStatus = (ImageView) convertView.findViewById(R.id.chat_read_status);
            chatViewHolder.chatSentTimeFrom = (TextView) convertView.findViewById(R.id.chat_created_time);
            chatViewHolder.chatSentTimeSender = (TextView) convertView.findViewById(R.id.chat_created_time_sender);
            chatViewHolder.chatMessageTo = (TextView) convertView.findViewById(R.id.chat_msg_to);
            chatViewHolder.chatMessageFrom = (TextView) convertView.findViewById(R.id.chat_msg_from);
            chatViewHolder.chatFromPic = (RoundedImageView) convertView.findViewById(R.id.chat_from_picture);
            chatViewHolder.rlToChat = (LinearLayout) convertView.findViewById(R.id.rl_to_chat);
            chatViewHolder.llFromChat = (LinearLayout) convertView.findViewById(R.id.ll_from_chat);
            chatViewHolder.rlChatFromProfileContainer = (RelativeLayout) convertView.findViewById(R.id.rl_chat_from_profile_container);
            convertView.setTag(R.layout.adapter_chat_window,chatViewHolder);
        } else if (convertView.getTag(R.layout.adapter_chat_window) == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_chat_window, parent, false);
            chatViewHolder = new ChatViewHolder();

            chatViewHolder.chatReadStatus = (ImageView) convertView.findViewById(R.id.chat_read_status);
            chatViewHolder.chatSentTimeFrom = (TextView) convertView.findViewById(R.id.chat_created_time);
            chatViewHolder.chatSentTimeSender = (TextView) convertView.findViewById(R.id.chat_created_time_sender);
            chatViewHolder.chatMessageTo = (TextView) convertView.findViewById(R.id.chat_msg_to);
            chatViewHolder.chatMessageFrom = (TextView) convertView.findViewById(R.id.chat_msg_from);
            chatViewHolder.chatFromPic = (RoundedImageView) convertView.findViewById(R.id.chat_from_picture);
            chatViewHolder.rlToChat = (LinearLayout) convertView.findViewById(R.id.rl_to_chat);
            chatViewHolder.llFromChat = (LinearLayout) convertView.findViewById(R.id.ll_from_chat);
            chatViewHolder.rlChatFromProfileContainer = (RelativeLayout) convertView.findViewById(R.id.rl_chat_from_profile_container);
            convertView.setTag(R.layout.adapter_chat_window,chatViewHolder);
        } else {
            chatViewHolder = (ChatViewHolder) convertView.getTag(R.layout.adapter_chat_window);
        }

        EventChat eventChat = (EventChat) getItem(position);
        //System.out.println("Event Chat Adapter: "+eventChat.getChat());
        if(eventChat.getSenderId().equals(gatheringPreviewFragment.loggedInUser.getUserId())) { // From Chat

            chatViewHolder.llFromChat.setVisibility(View.VISIBLE);
            chatViewHolder.rlToChat.setVisibility(View.GONE);
            chatViewHolder.chatFromPic.setVisibility(View.GONE);
            chatViewHolder.rlChatFromProfileContainer.setVisibility(View.GONE);

            chatViewHolder.chatMessageFrom.setText(eventChat.getChat());
            String textToHighlight = "\\(Edited\\)";
            String replacedWith = "<font color= '#9B9B9B'>" + textToHighlight + "</font>";
            chatViewHolder.chatMessageFrom.setText(Html.fromHtml(eventChat.getChat().replaceAll(textToHighlight, replacedWith)));

            chatViewHolder.chatSentTimeFrom.setText(CenesUtils.hhmmaa.format(new Date(eventChat.getCreatedAt()))  );
            //System.out.println("Chat Status ::: " +eventChat.getChatStatus());
            if(eventChat.getChatStatus().equals("Read")) {
                chatViewHolder.chatReadStatus.setImageDrawable(gatheringPreviewFragment.getResources().getDrawable(R.drawable.read_tick));
            } else {
                chatViewHolder.chatReadStatus.setImageDrawable(gatheringPreviewFragment.getResources().getDrawable(R.drawable.unread_tick));
            }

        } else { // To Chat

            chatViewHolder.llFromChat.setVisibility(View.GONE);
            chatViewHolder.rlToChat.setVisibility(View.VISIBLE);
            chatViewHolder.chatFromPic.setVisibility(View.VISIBLE);
            chatViewHolder.rlChatFromProfileContainer.setVisibility(View.VISIBLE);

            String textToHighlight = "\\(Edited\\)";
            String replacedWith = "<font color= '#9B9B9B'>" + textToHighlight + "</font>";
            chatViewHolder.chatMessageTo.setText(Html.fromHtml(eventChat.getChat().replaceAll(textToHighlight, replacedWith)));

            chatViewHolder.chatSentTimeSender.setText(CenesUtils.hhmmaa.format(new Date(eventChat.getCreatedAt())) );
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_pic_no_image);
            requestOptions.circleCrop();

            Glide.with(gatheringPreviewFragment.getContext()).load(eventChat.getUser().getPicture()).apply(requestOptions).into(chatViewHolder.chatFromPic);

            if (eventChat.getSenderId().equals(gatheringPreviewFragment.event.getCreatedById())) {
                chatViewHolder.rlChatFromProfileContainer.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.host_gradient_circle));
            } else {
                chatViewHolder.rlChatFromProfileContainer.setBackground(gatheringPreviewFragment.getResources().getDrawable(R.drawable.xml_circle_profile_pic_white_border));
            }
        }
        return convertView;
    }

    @NonNull
    private View getSectionView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder sectionViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_event_chats_group_header, null);
            sectionViewHolder = new HeaderViewHolder();
            sectionViewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tv_list_title);
            convertView.setTag(R.layout.adapter_event_chats_group_header, sectionViewHolder);

        } else if (convertView.getTag(R.layout.adapter_event_chats_group_header) == null) {

            convertView = layoutInflater.inflate(R.layout.adapter_event_chats_group_header, null);
            sectionViewHolder = new HeaderViewHolder();
            sectionViewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tv_list_title);
            convertView.setTag(R.layout.adapter_event_chats_group_header, sectionViewHolder);

        } else {
            sectionViewHolder =  (HeaderViewHolder) convertView.getTag(R.layout.adapter_event_chats_group_header);
        }

        String headerTitle = (String) getItem(position);
        sectionViewHolder.tvHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        Object listItem = getItem(position);
        if (listItem instanceof EventChat) {
            return VIEW_TYPE_ITEM;
        } else if (listItem instanceof String) {
            return VIEW_TYPE_SECTION;
        }
        return VIEW_TYPE_NONE;
    }

    public void refreshItems(List<Object> eventChatList) {
        this.eventChatList = eventChatList;
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        private TextView tvHeader;
    }

    class ChatViewHolder {
        private TextView chatSentTimeFrom, chatSentTimeSender, chatMessageTo,chatMessageFrom;
        private ImageView chatReadStatus;
        private RoundedImageView chatFromPic;
        private RelativeLayout rlChatFromProfileContainer;
        private LinearLayout llFromChat, rlToChat;
    }

}
