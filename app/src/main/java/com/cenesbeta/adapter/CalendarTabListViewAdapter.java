package com.cenesbeta.adapter;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.cenesbeta.fragment.CardSwipeDemoFragment;
import com.cenesbeta.fragment.ImageZoomerFragment;
import com.cenesbeta.fragment.dashboard.HomeFragmentV2;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.util.CenesTextView;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.cenesbeta.zoom.image.PhotoView;
import com.daimajia.swipe.SwipeLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

public class CalendarTabListViewAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    private HomeFragmentV2 homeFragmentV2;
    private LayoutInflater layoutInflater;
    private HomeScreenDto homeScreenDto;

    public CalendarTabListViewAdapter(HomeFragmentV2 homeFragmentV2, HomeScreenDto homeScreenDto) {

        this.homeFragmentV2 = homeFragmentV2;
        this.homeScreenDto = homeScreenDto;
        this.layoutInflater = LayoutInflater.from(homeFragmentV2.getContext());

    }

    @Override
    public int getCount() {
        return this.homeScreenDto.getHomelistViewWithHeaders().size();
    }

    @Override
    public Object getItem(int position) {
        return this.homeScreenDto.getHomelistViewWithHeaders().get(position);
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
        final RowViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_calendar_data_rows, parent, false);
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
            viewHolder.tvSwipeDelete = (TextView) convertView.findViewById(R.id.tv_swipe_delete);
            viewHolder.tvSwipeHide = (TextView) convertView.findViewById(R.id.tv_swipe_hide);
            viewHolder.tvThirdPartLabel = (CenesTextView) convertView.findViewById(R.id.tv_third_part_label);
            viewHolder.tvTpEventTitle = (CenesTextView) convertView.findViewById(R.id.tv_tp_event_title);
            viewHolder.tvTpSource = (CenesTextView) convertView.findViewById(R.id.tv_tp_source);
            viewHolder.dividerView = (View) convertView.findViewById(R.id.view_divider);
            viewHolder.rlMonthSeparator = (RelativeLayout) convertView.findViewById(R.id.rl_month_separator);
            viewHolder.slCenesEvent = (SwipeLayout) convertView.findViewById(R.id.sl_cenes_event);
            viewHolder.slSocilaEvent = (SwipeLayout) convertView.findViewById(R.id.sl_social_event);


            convertView.setTag(R.layout.adapter_calendar_data_rows);
        } else if (convertView.getTag(R.layout.adapter_calendar_data_rows) == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_calendar_data_rows, parent, false);
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
            viewHolder.tvSwipeDelete = (TextView) convertView.findViewById(R.id.tv_swipe_delete);
            viewHolder.tvSwipeHide = (TextView) convertView.findViewById(R.id.tv_swipe_hide);
            viewHolder.tvThirdPartLabel = (CenesTextView) convertView.findViewById(R.id.tv_third_part_label);
            viewHolder.tvTpEventTitle = (CenesTextView) convertView.findViewById(R.id.tv_tp_event_title);
            viewHolder.tvTpSource = (CenesTextView) convertView.findViewById(R.id.tv_tp_source);
            viewHolder.dividerView = (View) convertView.findViewById(R.id.view_divider);
            viewHolder.rlMonthSeparator = (RelativeLayout) convertView.findViewById(R.id.rl_month_separator);
            viewHolder.slCenesEvent = (SwipeLayout) convertView.findViewById(R.id.sl_cenes_event);
            viewHolder.slSocilaEvent = (SwipeLayout) convertView.findViewById(R.id.sl_social_event);


            convertView.setTag(R.layout.adapter_calendar_data_rows);
        } else {
            viewHolder = (RowViewHolder) convertView.getTag(R.layout.adapter_calendar_data_rows);
        }

        viewHolder.rlCenesEvents.setVisibility(View.GONE);
        viewHolder.rlTpEvents.setVisibility(View.GONE);
        viewHolder.llHoliday.setVisibility(View.GONE);
        viewHolder.rlMonthSeparator.setVisibility(View.GONE);
        viewHolder.slCenesEvent.setVisibility(View.GONE);
        viewHolder.slSocilaEvent.setVisibility(View.GONE);

        final Event event = (Event) getItem(position);
        List<EventMember> eventMembers = event.getEventMembers();

        /*if ( isLastChild ) {
            viewHolder.dividerView.setVisibility(View.GONE);
        }else {
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }*/

        System.out.println("Event Schdedule As : "+event.getTitle()+" ----- "+event.getScheduleAs());


        //if (event.getScheduleAs().equals("MonthSeparator")) {

            Calendar currentDateCal = Calendar.getInstance();
            Calendar eventDateCal = Calendar.getInstance();
            eventDateCal.setTimeInMillis(event.getStartTime());
            homeScreenDto.calendarPageDate = eventDateCal.getTime();

            System.out.println("Calendar Label : "+CenesUtils.MMMM.format(new Date(event.getStartTime())));

            if (currentDateCal.get(Calendar.YEAR) == eventDateCal.get(Calendar.YEAR)) {
                System.out.println("IF updateCalendarLabelDate");
                //homeFragmentV2.updateCalendarLabelDate(CenesUtils.MMMM.format(new Date(event.getStartTime())));
            } else {
                System.out.println("ELSE updateCalendarLabelDate");
                //homeFragmentV2.updateCalendarLabelDate(CenesUtils.MMMM_yyyy.format(new Date(event.getStartTime())));
            }

        //}


        //Lets hanle the case of Event Type as Gatheirngs
        if (event.getScheduleAs().equals("Gathering")) {

            viewHolder.slCenesEvent.setVisibility(View.VISIBLE);
            viewHolder.rlCenesEvents.setVisibility(View.VISIBLE);

            if (event.getExpired() == true) {
                viewHolder.slCenesEvent.setRightSwipeEnabled(false);
            } else {
                viewHolder.slCenesEvent.setRightSwipeEnabled(true);
            }
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

                final EventMember eventMemberHost = eventHost;
                viewHolder.ivEventHost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((CenesBaseActivity)homeFragmentV2.getActivity()).zoomImageFromThumb(viewHolder.ivEventHost, eventMemberHost.getUser().getPicture());
                    }
                });
            } else {
                viewHolder.ivEventHost.setImageResource(R.drawable.profile_pic_no_image);
            }

            viewHolder.rlCenesEvents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*CardSwipeDemoFragment gatheringPreviewFragment = new CardSwipeDemoFragment();
                    gatheringPreviewFragment.event = event;
                    gatheringPreviewFragment.sourceFragment = homeFragmentV2;
                    ((CenesBaseActivity) homeFragmentV2.getActivity()).replaceFragment(gatheringPreviewFragment, HomeFragmentV2.TAG);*/


                    GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                    gatheringPreviewFragment.event = event;
                    gatheringPreviewFragment.sourceFragment = homeFragmentV2;
                    ((CenesBaseActivity) homeFragmentV2.getActivity()).replaceFragment(gatheringPreviewFragment, HomeFragmentV2.TAG);
                }
            });


            if (event.getCreatedById().equals(homeFragmentV2.loggedInUser.getUserId())) {
                viewHolder.tvSwipeDelete.setText("Delete");
            } else {
                viewHolder.tvSwipeDelete.setText("Decline");
            }
            viewHolder.tvSwipeDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (event.getCreatedById().equals(homeFragmentV2.loggedInUser.getUserId())) {

                        homeFragmentV2.addOrRejectEvent(event, "Delete");
                    } else {
                        homeFragmentV2.addOrRejectEvent(event, "NotGoing");
                    }
                }
            });

        } else if (event.getScheduleAs().equals("Event")) {

            viewHolder.slSocilaEvent.setVisibility(View.VISIBLE);
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

            viewHolder.tvSwipeHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    homeFragmentV2.removeCalendarEvents(event);
                }
            });
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

    @NonNull
    private View getSectionView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder sectionViewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.adapter_home_data_headers_v2, parent, false);
            sectionViewHolder = new HeaderViewHolder();

            sectionViewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tv_calendar_data_header);

            convertView.setTag(R.layout.adapter_home_data_headers_v2);

        } else if (convertView.getTag(R.layout.adapter_home_data_headers_v2) == null) {

            convertView = layoutInflater.inflate(R.layout.adapter_home_data_headers_v2, parent, false);
            sectionViewHolder = new HeaderViewHolder();

            sectionViewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tv_calendar_data_header);

            convertView.setTag(R.layout.adapter_home_data_headers_v2);

        } else {
            sectionViewHolder = (HeaderViewHolder) convertView.getTag(R.layout.adapter_home_data_headers_v2);
        }


        String header = (String)getItem(position);
        String dateKey = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);

        if (header != null) {
            if (header.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

                dateKey = "Today ";
            } else if (header.equals(CenesUtils.EEEMMMMdd.format(cal.getTime()))) {

                dateKey = "Tomorrow ";
            }

            sectionViewHolder.tvHeader.setText(dateKey + header);

            System.out.println("Header Now : "+header);
        }
        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        Object listItem = getItem(position);
        if (listItem instanceof Event) {
            return VIEW_TYPE_ITEM;
        } else if (listItem instanceof String) {
            return VIEW_TYPE_SECTION;
        }

        return VIEW_TYPE_NONE;
    }

    public void refreshItems(HomeScreenDto homeScreenDto) {
        this.homeScreenDto = homeScreenDto;
        notifyDataSetChanged();
    }
    class HeaderViewHolder {
        private TextView tvHeader;
    }

    class RowViewHolder {

        private RoundedImageView ivEventHost;
        private TextView tvEventTitle, tvEventLocation, tvStartTime, tvTpStartTime, tvHolidayTitle, tvMonthSeparator, tvThirdPartLabel, tvTpSource;
        private TextView  tvTpEventTitle, tvSwipeDelete, tvSwipeHide;
        private LinearLayout llEventLocationSection, llHoliday;
        private View dividerView;
        private RelativeLayout rlMonthSeparator, rlTpEvents, rlCenesEvents;
        private SwipeLayout slCenesEvent, slSocilaEvent;
    }
}
