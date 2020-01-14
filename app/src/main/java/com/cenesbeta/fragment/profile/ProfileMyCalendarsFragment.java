package com.cenesbeta.fragment.profile;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.database.impl.CalendarSyncTokenManagerImpl;
import com.cenesbeta.fragment.CenesFragment;

public class ProfileMyCalendarsFragment extends CenesFragment {

    private static String TAG = "ProfileMyCalendarsFragment";

    private ImageView ivBackButtonImg;
    private TextView tvHolidayCalendar;
    private RelativeLayout rlMycalendarsHolidayBar, rlMycalendarsGoogleBar, rlMycalendarsOutlookBar;

    private CalenadarSyncToken holidayCalendarSyncToken;
    private CalendarSyncTokenManagerImpl calendarSyncTokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_calendars, container, false);

        ivBackButtonImg = (ImageView) view.findViewById(R.id.iv_back_button_img);
        tvHolidayCalendar = (TextView) view.findViewById(R.id.tv_holiday_calendar);
        rlMycalendarsHolidayBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_holiday_bar);
        rlMycalendarsGoogleBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_google_bar);
        rlMycalendarsOutlookBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_outlook_bar);

        ivBackButtonImg.setOnClickListener(onClickListener);
        rlMycalendarsHolidayBar.setOnClickListener(onClickListener);
        rlMycalendarsGoogleBar.setOnClickListener(onClickListener);
        rlMycalendarsOutlookBar.setOnClickListener(onClickListener);

        ((CenesBaseActivity)getActivity()).hideFooter();

        calendarSyncTokenManager = new CalendarSyncTokenManagerImpl(((CenesBaseActivity)getActivity()).getCenesApplication());
        loadAllCalendarsLocally();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_back_button_img:
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                    break;
                case R.id.rl_mycalendars_holiday_bar:
                    HolidayCalendarFragment holidayCalendarFragment = new HolidayCalendarFragment();
                    holidayCalendarFragment.calenadarSyncToken = holidayCalendarSyncToken;
                    ((CenesBaseActivity)getActivity()).replaceFragment(holidayCalendarFragment, ProfileMyCalendarsFragment.TAG);
                    break;
                case R.id.rl_mycalendars_google_bar:

                    ProfileMyCalendarsSocialFragment profileMyCalendarsSocialFragment = new ProfileMyCalendarsSocialFragment();
                    profileMyCalendarsSocialFragment.calendarSelected = ProfileMyCalendarsSocialFragment.CalendarType.Google;
                    ((CenesBaseActivity)getActivity()).replaceFragment(profileMyCalendarsSocialFragment, ProfileMyCalendarsFragment.TAG);

                    break;
                case R.id.rl_mycalendars_outlook_bar:
                    ProfileMyCalendarsSocialFragment profileMyCalendarsSocialFragmentOutlook = new ProfileMyCalendarsSocialFragment();
                    profileMyCalendarsSocialFragmentOutlook.calendarSelected = ProfileMyCalendarsSocialFragment.CalendarType.Outlook;
                    ((CenesBaseActivity)getActivity()).replaceFragment(profileMyCalendarsSocialFragmentOutlook, ProfileMyCalendarsFragment.TAG);

                    break;
            }
        }
    };

    public void loadAllCalendarsLocally() {
        holidayCalendarSyncToken = calendarSyncTokenManager.fetchCalendarByAccountType("Holiday");
        if (holidayCalendarSyncToken != null) {
            tvHolidayCalendar.setText("Holiday: "+holidayCalendarSyncToken.getEmailId());
        }

    }
}
