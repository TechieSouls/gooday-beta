package com.cenesbeta.adapter;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
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
        return homeScreenDto.getInvitaitonDataHeaders().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return homeScreenDto.getInvitationDataListMap().get(homeScreenDto.getInvitaitonDataHeaders().get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return homeScreenDto.getInvitaitonDataHeaders().get(groupPosition);
    }

    @Override
    public Event getChild(int groupPosition, int childPosition) {
        return homeScreenDto.getInvitationDataListMap().get(homeScreenDto.getInvitaitonDataHeaders().get(groupPosition)).get(childPosition);
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
        String dateKey = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);

        if (header != null) {
            if (header.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

                dateKey = "Today ";
            } else  if (header.equals(CenesUtils.EEEMMMMdd.format(cal.getTime()))) {

                dateKey = "Tomorrow ";
            }
            holder.tvHeader.setText(dateKey + header);
        }
        //holder.tvHeader.setText(header);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final EventViewHolder eventViewHolder;
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
            eventViewHolder.rlInvitationBar = (RelativeLayout) convertView.findViewById(R.id.rl_invitation_bar);
            eventViewHolder.dividerView = (View) convertView.findViewById(R.id.view_divider);


            convertView.setTag(eventViewHolder);

        } else {
            eventViewHolder = (EventViewHolder)convertView.getTag();
        }

        final Event event = getChild(groupPosition, childPosition);
        List<EventMember> eventMembers = event.getEventMembers();

        eventViewHolder.tvEventTitle.setText(event.getTitle());
        if (CenesUtils.isEmpty(event.getLocation())) {
            eventViewHolder.llEventLocationSection.setVisibility(View.GONE);
        } else {
            eventViewHolder.llEventLocationSection.setVisibility(View.VISIBLE);
            eventViewHolder.tvEventLocation.setText(event.getLocation());
        }

        System.out.println("Event Title : "+event.getTitle()+" Start Time : "+event.getStartTime()+", End Time :"+event.getEndTime() );
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
            if (eventHost.getUser().getUserId().equals(homeFragmentV2.loggedInUser.getUserId())) {
                eventViewHolder.tvHostName.setText("Me");
            } else {
                eventViewHolder.tvHostName.setText(eventHost.getUser().getName());
            }
        } else {
            eventViewHolder.rvHostImage.setImageResource(R.drawable.profile_pic_no_image);
        }

        final EventMember eventMemberHost = eventHost;
        eventViewHolder.rvHostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CenesBaseActivity)homeFragmentV2.getActivity()).zoomImageFromThumb(eventViewHolder.rvHostImage, eventMemberHost.getUser().getPicture());
            }
        });

        //Lets show Event Members without images
        List<EventMember> eventMembersWithoutHostAsCenesMember = new ArrayList<>();
        for (EventMember eventMember: eventMembers) {

            //Removing host from list
            if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                continue;
            }

            //Lets exclude who are not cenes member and also those who have not accepted the invitation
            if (eventMember.getUserId() == null || (eventMember.getUserId() != null &&
                    (eventMember.getStatus() == null || !eventMember.getStatus().equals("Going")))) {
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
                lootIndex ++;
            }
            InvitationTabItemRecyclerAdapter invitationTabItemRecyclerAdapter = new InvitationTabItemRecyclerAdapter(homeFragmentV2, eventMembersListForRecycler);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(homeFragmentV2.getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            eventViewHolder.recyclerViewGuests.setLayoutManager(mLayoutManager);
            eventViewHolder.recyclerViewGuests.setItemAnimator(new DefaultItemAnimator());
            eventViewHolder.recyclerViewGuests.setAdapter(invitationTabItemRecyclerAdapter);

        } else {
            eventViewHolder.recyclerViewGuests.setVisibility(View.GONE);
        }

        //Lets find who are not cenes member
        System.out.println("Title : "+event.getTitle());
        int countOfNonCenesMembers = 0;
        for (EventMember eventMember: eventMembers) {
            System.out.println("Name : "+eventMember.getName());

            //Lets find who are not cenes member
            if (eventMember.getUserId() == null && eventMember.getStatus() == null) {
                countOfNonCenesMembers += 1;
                System.out.println("Name : "+eventMember.getName()+", Excluded");

            } else if (eventMember.getUserId() != null &&
                    (eventMember.getStatus() == null || !eventMember.getStatus().equals("Going"))) {
                countOfNonCenesMembers += 1;
                System.out.println("Name : "+eventMember.getName()+", Excluded");
            }
        }

        if (eventMembersWithoutHostAsCenesMember.size() > 3) {
            countOfNonCenesMembers += eventMembersWithoutHostAsCenesMember.size() - 3;
        }
        if (countOfNonCenesMembers > 0) {
            eventViewHolder.rlNonCenesCountView.setVisibility(View.VISIBLE);
            eventViewHolder.tvNonCenesCount.setText("+"+countOfNonCenesMembers);
        } else {
            eventViewHolder.rlNonCenesCountView.setVisibility(View.GONE);
        }

        eventViewHolder.rlInvitationBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                gatheringPreviewFragment.event = event;
                gatheringPreviewFragment.sourceFragment = homeFragmentV2;
                ((CenesBaseActivity) homeFragmentV2.getActivity()).replaceFragment(gatheringPreviewFragment, HomeFragmentV2.TAG);

            }
        });

        if ( isLastChild ) {
            eventViewHolder.dividerView.setVisibility(View.GONE);
        }else {
            eventViewHolder.dividerView.setVisibility(View.VISIBLE);
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
        private RelativeLayout rlNonCenesCountView, rlInvitationBar;
        private RoundedImageView rvHostImage;
        private RecyclerView recyclerViewGuests;
        private View dividerView;
    }
    class HeaderViewHolder {
        private TextView tvHeader;
    }
}
