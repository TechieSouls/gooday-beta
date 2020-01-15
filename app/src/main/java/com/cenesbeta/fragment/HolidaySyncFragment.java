package com.cenesbeta.fragment;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.ApiManager;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.GuestActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.HolidayCalendar;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.countrypicker.CountryPicker;
import com.cenesbeta.countrypicker.CountryPickerListener;
import com.cenesbeta.countrypicker.CountryUtils;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by rohan on 10/10/17.
 */

public class HolidaySyncFragment extends CenesFragment {

    public final static String TAG = "HolidaySyncFragment";

    CenesApplication cenesApplication;
    CoreManager coreManager;
    DeviceManager deviceManager;
    UserManager userManager;
    ApiManager apiManager;
    UrlManager urlManager;
    private HolidayCalendar holidayCalendar;
    private User loggedInUser;
    private ProfileAsyncTask profileAsyncTask;

    RelativeLayout rlHeader;
    ImageView ivHolidayForward, instabugReport;
    private User user;
    private RoundedImageView homeProfilePic, rivCountryFlag;
    private ImageView homeIcon;
    private RelativeLayout rvSignupView, rlSideMenuView;
    private Button btnChangeCountry, btnSidemenuChangeCountry;

    TextView tvSignupSelectedCountry, tvSidemenuSelectedCountry;
    Button btnSidemenuSaveCountry;
    String holidayCalendarId, tempHolidayCalendarId;
    public static boolean isFirstLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_holiday_sync, container, false);

        if (getActivity() instanceof CenesBaseActivity) {
            isFirstLogin = ((CenesBaseActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);
        } else if (getActivity() instanceof GuestActivity) {
            isFirstLogin = ((GuestActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);
        }

        holidayCalendar = null;
        holidayCalendarId = null;
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        deviceManager = coreManager.getDeviceManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        loggedInUser = userManager.getUser();
        profileAsyncTask = new ProfileAsyncTask(cenesApplication, getActivity());

        rvSignupView = (RelativeLayout) v.findViewById(R.id.rv_signup_view);
        tvSignupSelectedCountry = (TextView) v.findViewById(R.id.tv_signup_selected_country);
        ivHolidayForward= (ImageView) v.findViewById(R.id.iv_holiday_forward);
        btnChangeCountry = (Button) v.findViewById(R.id.btn_change_country);
        instabugReport = (ImageView) v.findViewById(R.id.instabug_report);

        btnSidemenuSaveCountry  = (Button) v.findViewById(R.id.btn_sidemenu_save_country);
        rlSideMenuView = (RelativeLayout) v.findViewById(R.id.rl_side_menu_view);
        tvSidemenuSelectedCountry = (TextView) v.findViewById(R.id.tv_sidemenu_selected_country);
        homeProfilePic = (RoundedImageView) v.findViewById(R.id.home_profile_pic);
        homeIcon = (ImageView) v.findViewById(R.id.home_icon);
        rlHeader = (RelativeLayout) v.findViewById(R.id.rl_header);
        rivCountryFlag = (RoundedImageView) v.findViewById(R.id.riv_country_flag);
        btnSidemenuChangeCountry = (Button) v.findViewById(R.id.btn_sidemenu_change_country);

        if (getActivity() instanceof GuestActivity) {
            //((GuestActivity)getActivity()).hideFooter();
        } else if (getActivity() instanceof CenesBaseActivity) {
            ((CenesBaseActivity) getActivity()).hideFooter();
        }

         if (isFirstLogin) {
            rvSignupView.setVisibility(View.VISIBLE);
            rlSideMenuView.setVisibility(View.GONE);
             selectHolidayCalendarBasedOnCountry();

         } else {
            rvSignupView.setVisibility(View.GONE);
            rlSideMenuView.setVisibility(View.VISIBLE);

            new ProfileAsyncTask.HolidayFetchSyncTask(new ProfileAsyncTask.HolidayFetchSyncTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        if (response != null && response.getBoolean("success")) {

                            Gson gson = new Gson();

                            Type listType = new TypeToken<List<HolidayCalendar>>() {}.getType();
                            List<HolidayCalendar> calendars = new Gson().fromJson(response.getJSONArray("data").toString(), listType);

                            if (calendars != null && calendars.size() > 0) {
                                holidayCalendar = calendars.get(0);
                                holidayCalendarId = CountryUtils.getCountryCalendarIdMap().get(holidayCalendar.getCountryName());
                                tempHolidayCalendarId = CountryUtils.getCountryCalendarIdMap().get(holidayCalendar.getCountryName());
                                tvSidemenuSelectedCountry.setText(holidayCalendar.getCountryName());
                                rivCountryFlag.setImageResource(getActivity().getResources()
                                        .getIdentifier("flag_" + holidayCalendar.getCountryCode().toLowerCase(), "drawable",
                                                getActivity().getPackageName()));

                            } else {
                                rivCountryFlag.setImageResource(R.drawable.holiday_globe);
                                tvSidemenuSelectedCountry.setText("No Calendar Selected");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();

            user = userManager.getUser();

            if (user != null && !CenesUtils.isEmpty(user.getPicture())) {
                // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
                Glide.with(HolidaySyncFragment.this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homeProfilePic);
            }
            /*user = userManager.getUser();
            if (user != null && user.getPicture() != null && user.getPicture() != "null") {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.circleCrop();
                requestOptions.placeholder(R.drawable.default_profile_icon);
                Glide.with(getActivity()).load(user.getPicture()).apply(requestOptions).into(ivProfile);
            }*/
        }

        //Click Listeners
        instabugReport.setOnClickListener(onClickListener);
        btnChangeCountry.setOnClickListener(onClickListener);
        ivHolidayForward.setOnClickListener(onClickListener);
        homeIcon.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
        btnSidemenuSaveCountry.setOnClickListener(onClickListener);
        btnSidemenuChangeCountry.setOnClickListener(onClickListener);

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.instabug_report:
                    break;
                case R.id.btn_change_country:
                    final CountryPicker picker = CountryPicker.newInstance("Select Holiday Calendar");
                    //deviceManager.showKeyBoard(picker.getSearchEditText(),HolidaySyncActivity.this);
                    picker.show(getCenesActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String calendarId) {

                            try {
                                tvSignupSelectedCountry.setText(name);
                                holidayCalendarId = calendarId;

                                if (holidayCalendar == null) {
                                    holidayCalendar = new HolidayCalendar();
                                }
                                holidayCalendar.setCountryCalendarId(holidayCalendarId);
                                holidayCalendar.setCountryName(name);
                                holidayCalendar.setCountryCode(code.toLowerCase());
                                holidayCalendar.setUserId(loggedInUser.getUserId());
                                System.out.println(holidayCalendar.toString());
                                picker.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    break;

                case R.id.iv_holiday_forward:
                        nextClickListener();
                    break;

                case R.id.home_icon:
                    ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                    ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);
                    break;

                case R.id.home_profile_pic:
                    if (getActivity() instanceof CenesBaseActivity) {
                        ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;

                case R.id.btn_sidemenu_change_country:
                    final CountryPicker sidemenuCountryPicker = CountryPicker.newInstance("Select Holiday Calendar");
                    //deviceManager.showKeyBoard(picker.getSearchEditText(),HolidaySyncActivity.this);
                    sidemenuCountryPicker.show(getCenesActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
                    sidemenuCountryPicker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String calendarId) {
                            tvSidemenuSelectedCountry.setText(name);
                            holidayCalendarId = calendarId;
                            if (tempHolidayCalendarId != null && tempHolidayCalendarId.equals(holidayCalendarId)) {
                                btnSidemenuSaveCountry.setVisibility(View.GONE);
                            } else {
                                btnSidemenuSaveCountry.setVisibility(View.VISIBLE);
                            }

                            if (holidayCalendar == null) {
                                holidayCalendar = new HolidayCalendar();
                            }
                            holidayCalendar.setCountryCalendarId(holidayCalendarId);
                            holidayCalendar.setCountryName(name);
                            holidayCalendar.setCountryCode(code.toLowerCase());
                            holidayCalendar.setUserId(loggedInUser.getUserId());

                            rivCountryFlag.setImageResource(getActivity().getResources()
                                    .getIdentifier("flag_" + code, "drawable",
                                            getActivity().getPackageName()));
                            sidemenuCountryPicker.dismiss();
                        }
                    });
                    break;
                case R.id.btn_sidemenu_save_country:

                    new ProfileAsyncTask.HolidaySyncTask(new ProfileAsyncTask.HolidaySyncTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            btnSidemenuSaveCountry.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Holidays Synced", Toast.LENGTH_SHORT).show();
                        }
                    }).execute(holidayCalendar);
                    break;
            }
        }
    };

    public void selectHolidayCalendarBasedOnCountry() {

        Map<String, String> countryNameCodeMap = CountryUtils.getCountryCodeMap();

        //String userCountryCode = CenesUtils.getDeviceCountryCode();
        String userCountryCode = CenesUtils.getDeviceCountryCode(getContext());

        String countryName = null;

        for (Map.Entry<String, String> countryNameCodeEntryMap: countryNameCodeMap.entrySet()) {

            if (countryNameCodeEntryMap.getValue().equals(userCountryCode.toLowerCase())) {
                countryName = countryNameCodeEntryMap.getKey();
                break;
            }
        }

        if (countryName != null) {
            holidayCalendarId = CountryUtils.getCountryCalendarIdMap().get(countryName);
            tempHolidayCalendarId = CountryUtils.getCountryCalendarIdMap().get(countryName);
            if (isFirstLogin) {

                if (holidayCalendarId == null) {
                    tvSignupSelectedCountry.setText(countryName);
                } else {
                    tvSignupSelectedCountry.setText(countryName);

                    holidayCalendar = new HolidayCalendar();
                    holidayCalendar.setCountryCalendarId(holidayCalendarId);
                    holidayCalendar.setCountryName(countryName);
                    holidayCalendar.setCountryCode(userCountryCode.toLowerCase());
                    holidayCalendar.setUserId(loggedInUser.getUserId());
                }


            } else {
                tvSidemenuSelectedCountry.setText(countryName);
                rivCountryFlag.setImageResource(getActivity().getResources()
                        .getIdentifier("flag_" + userCountryCode.toLowerCase(), "drawable",
                                getActivity().getPackageName()));


                //Glide.with(HolidaySyncFragment.this).load(getActivity().getResources()
                 //       .getIdentifier("flag_" + userCountryCode.toLowerCase(), "drawable",
                  //              getActivity().getPackageName())).apply(RequestOptions.placeholderOf(R.drawable.holiday_globe)).into(rivCountryFlag);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void nextClickListener() {
        if (holidayCalendarId != null) {
            new ProfileAsyncTask.HolidaySyncTask(new ProfileAsyncTask.HolidaySyncTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    System.out.println(response);
                    if (getActivity() instanceof GuestActivity) {
                        ((GuestActivity)getActivity()).replaceFragment(new CalenderSyncFragment(), null);
                    }
                }
            }).execute(holidayCalendar);
        }
    }
}
