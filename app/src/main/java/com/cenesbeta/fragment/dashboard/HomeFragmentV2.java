package com.cenesbeta.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.HomeScreenDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.materialcalendarview.MaterialCalendarView;

public class HomeFragmentV2 extends CenesFragment {

    public static String TAG = "HomeFragmentV2";
    private TextView tvCalendarTab, tvInvitationTab, tvCalDate;
    private ImageView ivPlusBtn, ivCalDateBarArrow, ivRefreshCalsBtn;
    private LinearLayout llCalendarDateBar;
    private MaterialCalendarView mcvHomeCalendar;
    private ExpandableListView elvHomeListView;

    private HomeScreenDto homeScreenDto;
    private CoreManager coreManager;
    private InternetManager internetManager;
    private UserManager userManager;
    public  User loggedInUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_home_v2, container, false);

        tvCalendarTab = (TextView) view.findViewById(R.id.tv_calendar_tab);
        tvInvitationTab = (TextView) view.findViewById(R.id.tv_invitation_tab);
        tvCalDate = (TextView) view.findViewById(R.id.tv_cal_date);
        ivCalDateBarArrow = (ImageView) view.findViewById(R.id.iv_cal_date_bar_arrow);
        ivRefreshCalsBtn = (ImageView) view.findViewById(R.id.iv_refresh_cals_btn);
        ivPlusBtn = (ImageView) view.findViewById(R.id.iv_plus_btn);
        llCalendarDateBar = (LinearLayout) view.findViewById(R.id.ll_calendar_date_bar);
        mcvHomeCalendar = (MaterialCalendarView) view.findViewById(R.id.mcv_home_calendar);
        elvHomeListView = (ExpandableListView) view.findViewById(R.id.elv_home_list_view);

        //Click Listeners
        tvCalendarTab.setOnClickListener(onClickListener);
        tvInvitationTab.setOnClickListener(onClickListener);
        ivPlusBtn.setOnClickListener(onClickListener);
        ivRefreshCalsBtn.setOnClickListener(onClickListener);
        llCalendarDateBar.setOnClickListener(onClickListener);

        //Java Variables
        homeScreenDto = new HomeScreenDto();

        coreManager = ((CenesBaseActivity)getActivity()).getCenesApplication().getCoreManager();
        internetManager = coreManager.getInternetManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.tv_calendar_tab:
                    calendarTabPressed();
                    break;

                case R.id.tv_invitation_tab:
                    invitationTabPressed();
                    break;

                case R.id.iv_plus_btn:
                    plusButtonPressed();
                    break;

                case R.id.iv_refresh_cals_btn:
                    refreshCalBtnPressed();
                    break;

                case R.id.ll_calendar_date_bar:
                    calendarDateBarPressed();
                    break;
            }
        }
    };

    public void calendarTabPressed() {

        tvCalendarTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvInvitationTab.setBackground(null);
    }

    public void invitationTabPressed() {

        tvInvitationTab.setBackground(getResources().getDrawable(R.drawable.xml_border_bottom_black));
        tvCalendarTab.setBackground(null);
    }

    public void plusButtonPressed() {
        ((CenesBaseActivity)getActivity()).replaceFragment(new FriendListFragment(), HomeFragmentV2.TAG);
    }

    public void calendarDateBarPressed() {

        //If it was open..Lets close it
        if (homeScreenDto.isCalendarOpen() == true) {
            ivCalDateBarArrow.setImageResource(R.drawable.close_calendar);
            homeScreenDto.setCalendarOpen(false);
            mcvHomeCalendar.setVisibility(View.GONE);
        } else {
            ivCalDateBarArrow.setImageResource(R.drawable.open_calendar);
            homeScreenDto.setCalendarOpen(true);
            mcvHomeCalendar.setVisibility(View.VISIBLE);
        }
    }

    public void refreshCalBtnPressed() {

    }
}
