package com.cenesbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.R;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.util.CenesEditText;
import com.cenesbeta.util.CenesTextView;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarTabExpandableListAdapter extends BaseExpandableListAdapter {

    private HomeFragmentV2 homeFragmentV2;
    private HomeScreenDto homeScreenDto;
    private LayoutInflater inflter;

    public CalendarTabExpandableListAdapter(HomeFragmentV2 homeFragmentV2, HomeScreenDto homeScreenDto) {
        this.homeFragmentV2 = homeFragmentV2;
        this.homeScreenDto = homeScreenDto;
        this.inflter = (LayoutInflater.from(homeFragmentV2.getContext()));

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
        String dateKey = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        if (header.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

            dateKey = "Today ";
        } else  if (header.equals(CenesUtils.EEEMMMMdd.format(cal.getTime()))) {

            dateKey = "Tomorrow ";
        }
        holder.tvHeader.setText(dateKey + header);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final RowViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_calendar_data_rows, null);
            viewHolder = new RowViewHolder();

            viewHolder.ivEventHost = (RoundedImageView) convertView.findViewById(R.id.iv_event_host);
            viewHolder.tvEventTitle = (CenesTextView) convertView.findViewById(R.id.tv_event_title);
            viewHolder.tvEventLocation = (CenesTextView) convertView.findViewById(R.id.tv_event_location);
            viewHolder.llEventLocationSection = (LinearLayout) convertView.findViewById(R.id.ll_event_location_section);
            viewHolder.llCenesEvents = (LinearLayout) convertView.findViewById(R.id.ll_cenes_events);
            viewHolder.llTpEvents = (LinearLayout) convertView.findViewById(R.id.ll_tp_events);
            viewHolder.llHoliday = (LinearLayout) convertView.findViewById(R.id.ll_holiday);
            viewHolder.tvStartTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.tvTpStartTime = (TextView) convertView.findViewById(R.id.tv_tp_start_time);
            viewHolder.tvHolidayTitle = (TextView) convertView.findViewById(R.id.tv_holiday_title);
            viewHolder.dividerView = (View) convertView.findViewById(R.id.view_divider);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RowViewHolder) convertView.getTag();
        }
        viewHolder.llCenesEvents.setVisibility(View.GONE);
        viewHolder.llTpEvents.setVisibility(View.GONE);
        viewHolder.llHoliday.setVisibility(View.GONE);

        Event event = getChild(groupPosition, childPosition);
        List<EventMember> eventMembers = event.getEventMembers();

        if ( isLastChild ) {
            viewHolder.dividerView.setVisibility(View.GONE);
        }else {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }

        //Lets hanle the case of Event Type as Gatheirngs
        if (event.getScheduleAs().equals("Gathering")) {

            viewHolder.tvEventTitle.setText(event.getTitle());
            if (CenesUtils.isEmpty(event.getLocation())) {
                viewHolder.llEventLocationSection.setVisibility(View.GONE);
            } else {
                viewHolder.llEventLocationSection.setVisibility(View.VISIBLE);
                viewHolder.tvEventLocation.setText(event.getLocation());
            }
            viewHolder.llCenesEvents.setVisibility(View.VISIBLE);
            viewHolder.tvStartTime.setText(CenesUtils.hmmaa.format(new Date(event.getStartTime())));
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
                    Glide.with(homeFragmentV2.getContext()).load(eventHost.getUser().getPicture()).apply(requestOptions).into(viewHolder.ivEventHost);
                } else {
                    viewHolder.ivEventHost.setImageResource(R.drawable.profile_pic_no_image);
                }
            } else {
                viewHolder.ivEventHost.setImageResource(R.drawable.profile_pic_no_image);
            }
        }

        else if (event.getScheduleAs().equals("Event")) {

            viewHolder.llTpEvents.setVisibility(View.VISIBLE);
            viewHolder.tvTpStartTime.setText(CenesUtils.hmmaa.format(new Date(event.getStartTime())));

        }

        else if (event.getScheduleAs().equals("Holiday")) {
            viewHolder.llHoliday.setVisibility(View.VISIBLE);
            viewHolder.tvHolidayTitle.setText(event.getTitle());
        }




        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class HeaderViewHolder {
        private TextView tvHeader;
    }

    class RowViewHolder {

        private RoundedImageView ivEventHost;
        private TextView tvEventTitle, tvEventLocation, tvStartTime, tvTpStartTime, tvHolidayTitle;
        private LinearLayout llEventLocationSection, llCenesEvents, llTpEvents, llHoliday;
        private View dividerView;
    }
}
