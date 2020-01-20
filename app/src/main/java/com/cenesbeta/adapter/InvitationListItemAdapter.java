package com.cenesbeta.adapter;

import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvitationListItemAdapter extends BaseExpandableListAdapter {

    private HomeFragmentV2 homeFragmentV2;
    private HomeScreenDto homeScreenDto;
    private LayoutInflater inflter;


    public InvitationListItemAdapter(HomeFragmentV2 homeFragmentV2, HomeScreenDto homeScreenDto) {

        this.homeFragmentV2 = homeFragmentV2;
        this.homeScreenDto = homeScreenDto;
        inflter = (LayoutInflater.from(homeFragmentV2.getContext()));

    }

    @Override
    public int getGroupCount() {
        return homeScreenDto.getHomeDataHeaders().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return homeScreenDto.getHomeDataHeaders().get(groupPosition);
    }

    @Override
    public Event getChild(int groupPosition, int childPosition) {
        return homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(groupPosition)).get(childPosition);
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

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_headers_v2, null);
            holder = new HeaderViewHolder();
            holder.tvHeader = (TextView) convertView.findViewById(R.id.tv_calendar_data_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        };

        String header = getGroup(groupPosition);
        holder.tvHeader.setText(header);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        EventViewHolder eventViewHolder;
        if(convertView == null) {
            eventViewHolder = new EventViewHolder();
            convertView = inflter.inflate(R.layout.adapter_invitation_list_item, null);

            eventViewHolder.tvEventTitle = (TextView) convertView.findViewById(R.id.tv_event_title);
            eventViewHolder.tvEventLocation = (TextView) convertView.findViewById(R.id.tv_event_location);
            eventViewHolder.tvEventDate = (TextView) convertView.findViewById(R.id.tv_event_date);
            eventViewHolder.tvHostName = (TextView) convertView.findViewById(R.id.tv_host_name);
            eventViewHolder.tvNonCenesCount = (TextView)convertView.findViewById(R.id.tv_non_cenes_count);
            eventViewHolder.llEventLocationSection = (LinearLayout) convertView.findViewById(R.id.ll_event_location_section);
            eventViewHolder.recyclerViewGuests = (RecyclerView) convertView.findViewById(R.id.recycler_view_guests);
            eventViewHolder.rvHostImage = (RoundedImageView) convertView.findViewById(R.id.rv_host_image);
            eventViewHolder.rlNonCenesCountView = (RelativeLayout) convertView.findViewById(R.id.rl_non_cenes_count_view);

            convertView.setTag(eventViewHolder);

        } else {
            eventViewHolder = (EventViewHolder)convertView.getTag();
        }

        Event event = getChild(groupPosition, childPosition);
        List<EventMember> eventMembers = event.getEventMembers();

        eventViewHolder.tvEventTitle.setText(event.getTitle());
        if (!CenesUtils.isEmpty(event.getLocation())) {
            eventViewHolder.llEventLocationSection.setVisibility(View.GONE);
            eventViewHolder.tvEventLocation.setText(event.getLocation());
        } else {
            eventViewHolder.llEventLocationSection.setVisibility(View.VISIBLE);
        }

        String eventDate = CenesUtils.hmmaa.format(new Date(event.getStartTime()))+"-"+CenesUtils.hmmaa.format(new Date(event.getEndTime()));
        eventViewHolder.tvEventDate.setText(eventDate.toUpperCase());

        //Lets find Event Host
        EventMember eventHost = null;
        for (EventMember eventMember: eventMembers) {
            if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                eventHost = eventMember;
                break;
            }
        }
        if (eventHost != null) {

            if (eventHost.getUser() != null && !CenesUtils.isEmpty(eventHost.getUser().getPicture())) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.profile_pic_no_image);
                requestOptions.circleCrop();
                Glide.with(homeFragmentV2.getContext()).load(eventHost.getUser().getPicture()).apply(requestOptions).into(eventViewHolder.rvHostImage);
            } else {
                eventViewHolder.rvHostImage.setImageResource(R.drawable.profile_pic_no_image);
            }
            eventViewHolder.tvHostName.setText(eventHost.getUser().getName());
        } else {
            eventViewHolder.rvHostImage.setImageResource(R.drawable.profile_pic_no_image);
        }


        //Lets show Event Members without images
        List<EventMember> eventMembersWithoutHostAsCenesMember = new ArrayList<>();
        for (EventMember eventMember: eventMembers) {
            if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                continue;
            }
            //Lets exclude who are not cenes member
            if (eventMember.getUserId() == null) {
                continue;
            }
            eventMembersWithoutHostAsCenesMember.add(eventMember);
        }

        int loopLimit = 0;
        if (eventMembersWithoutHostAsCenesMember.size() > 0) {
            eventViewHolder.recyclerViewGuests.setVisibility(View.VISIBLE);
            if (eventMembersWithoutHostAsCenesMember.size() > 3) {
                loopLimit = 2;
            } else {
                loopLimit = eventMembersWithoutHostAsCenesMember.size() - 1;
            }

            List<EventMember> eventMembersListForRecycler = new ArrayList<>();
            int lootIndex = 0;
            for (final EventMember eveMemWithoutHost: eventMembersWithoutHostAsCenesMember) {
                if (lootIndex > loopLimit) {
                    break;
                }
                eventMembersListForRecycler.add(eveMemWithoutHost);
            }
            InvitationTabItemRecyclerAdapter invitationTabItemRecyclerAdapter = new InvitationTabItemRecyclerAdapter(homeFragmentV2, eventMembersListForRecycler);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(homeFragmentV2.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            eventViewHolder.recyclerViewGuests.setLayoutManager(mLayoutManager);
            eventViewHolder.recyclerViewGuests.setItemAnimator(new DefaultItemAnimator());
            eventViewHolder.recyclerViewGuests.setAdapter(invitationTabItemRecyclerAdapter);

        } else {
            eventViewHolder.recyclerViewGuests.setVisibility(View.GONE);
        }

        //Lets show Event Members without images
        List<EventMember> eventMembersAsNonCenesMember = new ArrayList<>();
        for (EventMember eventMember: eventMembers) {
            //Lets find who are not cenes member
            if (eventMember.getUserId() == null) {
                eventMembersAsNonCenesMember.add(eventMember);
            }
        }
        if (eventMembersAsNonCenesMember.size() > 0) {
            eventViewHolder.rlNonCenesCountView.setVisibility(View.VISIBLE);
            eventViewHolder.tvNonCenesCount.setText("+"+eventMembersAsNonCenesMember.size());
        } else {
            eventViewHolder.rlNonCenesCountView.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class EventViewHolder {
        private TextView tvEventTitle, tvEventLocation, tvEventDate, tvHostName, tvNonCenesCount;
        private LinearLayout llEventLocationSection;
        private RelativeLayout rlNonCenesCountView;
        private RoundedImageView rvHostImage;
        private RecyclerView recyclerViewGuests;
    }
    class HeaderViewHolder {
        private TextView tvHeader;
    }
}