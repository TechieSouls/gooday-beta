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
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
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
        if (groupPosition <= homeScreenDto.getHomeDataListMap().size() - 1) {
            return homeScreenDto.getHomeDataListMap().get(homeScreenDto.getHomeDataHeaders().get(groupPosition)).size();
        }
        return 0;
    }

    @Override
    public String getGroup(int groupPosition) {
        if (groupPosition <= homeScreenDto.getHomeDataHeaders().size() - 1) {
            return homeScreenDto.getHomeDataHeaders().get(groupPosition);
        }
        return null;
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

        if (header != null) {
            if (header.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

                dateKey = "Today ";
            } else  if (header.equals(CenesUtils.EEEMMMMdd.format(cal.getTime()))) {

                dateKey = "Tomorrow ";
            }
            holder.tvHeader.setText(dateKey + header);
        }
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
            viewHolder.rlCenesEvents = (RelativeLayout) convertView.findViewById(R.id.rl_cenes_events);
            viewHolder.rlTpEvents = (RelativeLayout) convertView.findViewById(R.id.rl_tp_events);
            viewHolder.llHoliday = (LinearLayout) convertView.findViewById(R.id.ll_holiday);
            viewHolder.tvStartTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.tvTpStartTime = (TextView) convertView.findViewById(R.id.tv_tp_start_time);
            viewHolder.tvHolidayTitle = (TextView) convertView.findViewById(R.id.tv_holiday_title);
            viewHolder.tvMonthSeparator = (TextView) convertView.findViewById(R.id.tv_month_separator);
            viewHolder.tvThirdPartLabel = (CenesTextView) convertView.findViewById(R.id.tv_third_part_label);
            viewHolder.tvTpEventTitle = (CenesTextView) convertView.findViewById(R.id.tv_tp_event_title);
            viewHolder.tvTpSource = (CenesTextView) convertView.findViewById(R.id.tv_tp_source);
            viewHolder.dividerView = (View) convertView.findViewById(R.id.view_divider);
            viewHolder.rlMonthSeparator = (RelativeLayout) convertView.findViewById(R.id.rl_month_separator);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RowViewHolder) convertView.getTag();
        }
        viewHolder.rlCenesEvents.setVisibility(View.GONE);
        viewHolder.rlTpEvents.setVisibility(View.GONE);
        viewHolder.llHoliday.setVisibility(View.GONE);
        viewHolder.rlMonthSeparator.setVisibility(View.GONE);

        final Event event = getChild(groupPosition, childPosition);
        List<EventMember> eventMembers = event.getEventMembers();

        if ( isLastChild ) {
            viewHolder.dividerView.setVisibility(View.GONE);
        }else {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }

        System.out.println("Event Schdedule As : "+event.getTitle()+" ----- "+event.getScheduleAs());


        Calendar currentDateCal = Calendar.getInstance();
        Calendar eventDateCal = Calendar.getInstance();
        eventDateCal.setTimeInMillis(event.getStartTime());
        if (currentDateCal.get(Calendar.YEAR) == eventDateCal.get(Calendar.YEAR)) {
            homeFragmentV2.updateCalendarLabelDate(CenesUtils.MMMM.format(new Date(event.getStartTime())));
        } else {
            homeFragmentV2.updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(new Date(event.getStartTime())));
        }


        //Lets hanle the case of Event Type as Gatheirngs
        if (event.getScheduleAs().equals("Gathering")) {
            viewHolder.rlCenesEvents.setVisibility(View.VISIBLE);
            viewHolder.rlCenesEvents.setTag("January");
            viewHolder.tvEventTitle.setText(event.getTitle());
            if (CenesUtils.isEmpty(event.getLocation())) {
                viewHolder.llEventLocationSection.setVisibility(View.GONE);
            } else {
                viewHolder.llEventLocationSection.setVisibility(View.VISIBLE);
                viewHolder.tvEventLocation.setText(event.getLocation());
            }
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

            viewHolder.rlCenesEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.event = event;
                    gatheringPreviewFragment.sourceFragment = homeFragmentV2;
                    ((CenesBaseActivity) homeFragmentV2.getActivity()).replaceFragment(gatheringPreviewFragment, HomeFragmentV2.TAG);
                }
            });
        }

        else if (event.getScheduleAs().equals("Event")) {

            viewHolder.rlTpEvents.setVisibility(View.VISIBLE);
            viewHolder.rlTpEvents.setTag("January");

            viewHolder.tvTpEventTitle.setText(event.getTitle());
            viewHolder.tvTpStartTime.setText(CenesUtils.hmmaa.format(new Date(event.getStartTime())));
            if (event.getSource().equals("Google")) {
                viewHolder.tvThirdPartLabel.setText("G");
                viewHolder.tvTpSource.setText("Google");
            } else if (event.getSource().equals("Outlook")) {
                viewHolder.tvThirdPartLabel.setText("O");
                viewHolder.tvTpSource.setText("Outlook");
            } else if (event.getSource().equals("Apple")) {
                viewHolder.tvThirdPartLabel.setText("A");
                viewHolder.tvTpSource.setText("Apple");
            }

        } else if (event.getScheduleAs().equals("Holiday")) {
            viewHolder.llHoliday.setTag("January");

            viewHolder.llHoliday.setVisibility(View.VISIBLE);
            viewHolder.tvHolidayTitle.setText(event.getTitle());

        } else if (event.getScheduleAs().equals("MonthSeparator")) {
            viewHolder.rlMonthSeparator.setTag("January");

            viewHolder.rlMonthSeparator.setVisibility(View.VISIBLE);
            viewHolder.tvMonthSeparator.setText(event.getTitle());
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
        private TextView tvEventTitle, tvEventLocation, tvStartTime, tvTpStartTime, tvHolidayTitle, tvMonthSeparator, tvThirdPartLabel, tvTpSource;
        private TextView  tvTpEventTitle;
        private LinearLayout llEventLocationSection, llHoliday;
        private View dividerView;
        private RelativeLayout rlMonthSeparator, rlTpEvents, rlCenesEvents;
    }
}
