package com.cenes.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
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
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.CenesActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.countrypicker.CountryPicker;
import com.cenes.countrypicker.CountryPickerListener;
import com.cenes.database.manager.UserManager;

import java.net.URLEncoder;

/**
 * Created by rohan on 10/10/17.
 */

public class HolidaySyncFragment extends CenesFragment {

    public final static String TAG = "HolidaySyncFragment";

    private GestureDetector gestureDetector;
    CenesApplication cenesApplication;
    CoreManager coreManager;
    DeviceManager deviceManager;
    UserManager userManager;
    ApiManager apiManager;
    UrlManager urlManager;

    RelativeLayout rlHeader;
    ImageView ivProfile;
    private User user;
    TextView tvSave;

    TextView holiday_search_textbox;
    String holidayCalendarId;
    public static boolean isFirstLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_holiday_sync, container, false);

        holidayCalendarId = null;
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        deviceManager = coreManager.getDeviceManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();

        holiday_search_textbox = (TextView) v.findViewById(R.id.holiday_search_textbox);

        rlHeader = (RelativeLayout) v.findViewById(R.id.rl_header);
        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        tvSave = (TextView) v.findViewById(R.id.tvSave);

        holiday_search_textbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Holiday Calendar");
                //deviceManager.showKeyBoard(picker.getSearchEditText(),HolidaySyncActivity.this);
                picker.show(getCenesActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String calendarId) {
                        holiday_search_textbox.setText(name);
                        holidayCalendarId = calendarId;
                        picker.dismiss();
                    }
                });
            }
        });

        isFirstLogin = ((CenesActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            rlHeader.setVisibility(View.GONE);
        } else {
            rlHeader.setVisibility(View.VISIBLE);
            user = userManager.getUser();
            if (user != null && user.getPicture() != null && user.getPicture() != "null") {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.circleCrop();
                requestOptions.placeholder(R.drawable.default_profile_icon);
                Glide.with(getActivity()).load(user.getPicture()).apply(requestOptions).into(ivProfile);
            }

            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextClickListener();
                }
            });

            ivProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getActivity() instanceof HomeScreenActivity) {
                ((HomeScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof ReminderActivity) {
                ((ReminderActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof GatheringScreenActivity) {
                ((GatheringScreenActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof DiaryActivity) {
                ((DiaryActivity) getActivity()).hideFooter();
            } else if (getActivity() instanceof AlarmActivity) {
                ((AlarmActivity) getActivity()).hideFooter();
            }
        } catch (Exception e) {

        }
    }

    public void nextClickListener() {
        if (holidayCalendarId != null) {
            new HolidaySyncTask().execute(holidayCalendarId);
        }
    }

    class HolidaySyncTask extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getCenesActivity());
            progressDialog.setMessage("Syncing....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String calendarId = strings[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?calendar_id=" + URLEncoder.encode(calendarId) + "&user_id=" + user.getUserId();
            apiManager.syncHolidayCalendar(user, queryStr, getCenesActivity());
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.hide();
            }
            Toast.makeText(getCenesActivity(), "Holiday Calendar Synced!!", Toast.LENGTH_LONG);
        }
    }
}
