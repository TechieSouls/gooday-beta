package com.cenesbeta.fragment.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.HolidayCountryItemAdapter;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.countrypicker.CountryUtils;
import com.cenesbeta.database.impl.CalendarSyncTokenManagerImpl;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.CustomLoadingDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

public class HolidayCalendarFragment extends CenesFragment {

    private ImageView ivBackButtonImg;
    private LinearLayout llHideHolidayCalendarBar;
    private ListView lvHolidayCounties;

    private HolidayCountryItemAdapter holidayCountryItemAdapter;
    private CoreManager coreManager;
    private User loggedInUser;
    public CalenadarSyncToken calenadarSyncToken;
    private CalendarSyncTokenManagerImpl calendarSyncTokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_holiday_countries, container, false);

        ivBackButtonImg = (ImageView) view.findViewById(R.id.iv_back_button_img);
        llHideHolidayCalendarBar = (LinearLayout) view.findViewById(R.id.ll_hide_holiday_calendar_bar);
        lvHolidayCounties = (ListView) view.findViewById(R.id.lv_holiday_countries);

        ivBackButtonImg.setOnClickListener(onClickListener);
        llHideHolidayCalendarBar.setOnClickListener(onClickListener);

        coreManager = ((CenesBaseActivity)getActivity()).getCenesApplication().getCoreManager();
        calendarSyncTokenManager = new CalendarSyncTokenManagerImpl(((CenesBaseActivity)getActivity()).getCenesApplication());
        loggedInUser = coreManager.getUserManager().getUser();
        loadHolidayCountries();
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back_button_img:
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                    break;

                case R.id.ll_hide_holiday_calendar_bar:
                    unsycHolidayCalendar();
                    break;
            }
        }
    };

    public void loadHolidayCountries() {
        if (calenadarSyncToken != null) {
            llHideHolidayCalendarBar.setVisibility(View.VISIBLE);
        }
        List<String> countries = CountryUtils.getCalendarCountries();
        holidayCountryItemAdapter = new HolidayCountryItemAdapter(this, countries);
        lvHolidayCounties.setAdapter(holidayCountryItemAdapter);
    }

    public void holidayBarPresssed(String country) {

        try {
            JSONObject postData = new JSONObject();
            postData.put("userId", loggedInUser.getUserId());
            postData.put("calendarId", CountryUtils.getCountryCalendarIdMap().get(country).toString());
            postData.put("name", country);

            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setPostData(postData);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ UserAPI.post_sync_holiday_calendar);
            postAsyncCall(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsycHolidayCalendar() {
        try {

            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setQueryStr("calendarSyncTokenId="+calenadarSyncToken.getRefreshTokenId());
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+UserAPI.delete_sync_token);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

            deleteAsyncTaskCall(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postAsyncCall(AsyncTaskDto asyncTaskDto) {

        final CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog(((CenesBaseActivity)getActivity()));
        customLoadingDialog.showDialog();
        new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {
                customLoadingDialog.hideDialog();

                try {
                    boolean success = response.getBoolean("success");
                    if (success == true) {
                        if (calenadarSyncToken != null) {
                            calendarSyncTokenManager.deleteCalendarByRefreshTokenId(calenadarSyncToken.getRefreshTokenId());
                        }

                        calenadarSyncToken = new Gson().fromJson(response.getJSONObject("data").toString(), CalenadarSyncToken.class);
                        calendarSyncTokenManager.addNewRow(calenadarSyncToken);

                        loadHolidayCountries();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //((CenesBaseActivity)getActivity()).homeScreenReloadBroadcaster();
                ((CenesBaseActivity)getActivity()).homeFragmentV2.loadCalendarTabData();
                ((CenesBaseActivity)getActivity()).onBackPressed();

            }
        }).execute(asyncTaskDto);
    }

    public void deleteAsyncTaskCall(AsyncTaskDto asyncTaskDto) {
        try {

            final CustomLoadingDialog customLoadingDialog = new CustomLoadingDialog((CenesBaseActivity) getActivity());
            customLoadingDialog.showDialog();
            new ProfileAsyncTask.CommonDeleteRequestTask(new ProfileAsyncTask.CommonDeleteRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    customLoadingDialog.hideDialog();

                    if (calenadarSyncToken != null) {
                        calendarSyncTokenManager.deleteCalendarByRefreshTokenId(calenadarSyncToken.getRefreshTokenId());
                    }
                    loadHolidayCountries();
                    //((CenesBaseActivity)getActivity()).homeScreenReloadBroadcaster();
                    ((CenesBaseActivity)getActivity()).homeFragmentV2.loadCalendarTabData();
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
