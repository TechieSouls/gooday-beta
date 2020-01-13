package com.cenesbeta.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.fragment.CenesFragment;

public class ProfileMyCalendarsFragment extends CenesFragment {

    private static String TAG = "ProfileMyCalendarsFragment";

    private RelativeLayout rlMycalendarsHolidayBar, rlMycalendarsGoogleBar, rlMycalendarsOutlookBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_calendars, container, false);

        rlMycalendarsHolidayBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_holiday_bar);
        rlMycalendarsGoogleBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_google_bar);
        rlMycalendarsOutlookBar = (RelativeLayout) view.findViewById(R.id.rl_mycalendars_outlook_bar);

        rlMycalendarsHolidayBar.setOnClickListener(onClickListener);
        rlMycalendarsGoogleBar.setOnClickListener(onClickListener);
        rlMycalendarsOutlookBar.setOnClickListener(onClickListener);

        ((CenesBaseActivity)getActivity()).hideFooter();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_mycalendars_holiday_bar:

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
}
