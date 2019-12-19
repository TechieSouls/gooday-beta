package com.cenesbeta.fragment.metime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.MeTimeAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.MeTimeManagerImpl;
import com.cenesbeta.database.impl.MeTimePatternManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.service.MeTimeService;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mandeep on 10/10/17.
 */

public class MeTimeFragment extends CenesFragment {

    /*
    *   Long userId;
	*   List<MeTimeEvent> events;
	*   String timezone;
    * */

    public final static String TAG = "MeTimeFragment";
    public static int SAVE_METIME_REQUEST_CODE = 1001, DELETE_METIME_REQUEST_CODE = 1002, CANCEL_METIME_REQUEST_CODE = 1003;
    public static String SAVE_METIME_REQUEST_STRING = "save", DELETE_METIME_REQUEST_STRING = "delete", CANCEL_METIME_REQUEST_STRING = "cancel";

    //ImageView footerHomeIcon, footerGatheringIcon;
    ImageView ivAddMetime;
    LinearLayout llMetimeTilesContainer;
    RoundedImageView homeProfilePic;


    private FragmentManager fragmentManager;
    Map<String, MeTimeItem> meTimeItemMap;
    List<String> meTimeCategoryHeaders;
    Map<String, JSONObject> meTimeDataCategoryMap;
    Map<String, Boolean> daysSelectionMap;
    CenesApplication cenesApplication;
    private MeTimeService meTimeService;
    private MeTimeAsyncTask meTimeAsyncTask;
    private List<MeTime> meTimes;
    private MeTimeManagerImpl meTimeManagerImpl;
    private InternetManager internetManager;
    private User loggedInUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_metime, container, false);

        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity) getActivity()).activateFooterIcon(MeTimeFragment.TAG);

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());

        ivAddMetime = v.findViewById(R.id.iv_add_metime);
        llMetimeTilesContainer = v.findViewById(R.id.ll_metime_tiles_container);
        homeProfilePic = v.findViewById(R.id.home_profile_pic);

        ivAddMetime.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
        meTimeItemMap = new HashMap<>();

        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        internetManager = coreManager.getInternetManager();
        meTimeManagerImpl = new MeTimeManagerImpl(cenesApplication);

        Glide.with(this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(homeProfilePic);

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
        try {
            JSONObject props = new JSONObject();
            props.put("Action","MetimeScreenOpened");
            props.put("UserEmail",loggedInUser.getEmail());
            props.put("UserName",loggedInUser.getName());
            mixpanel.track("MeTime", props);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.iv_add_metime:
                    ((CenesBaseActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, new MeTimeCardFragment())
                            .addToBackStack(MeTimeCardFragment.TAG)
                            .commit();

                    break;
                case R.id.home_profile_pic:
                if (getActivity() instanceof CenesBaseActivity) {
                    ((CenesBaseActivity)getActivity()).fragmentManager.beginTransaction().add(R.id.settings_container, new NavigationFragment(), null).commit();
                    ((CenesBaseActivity)getActivity()).mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        loadMeTimes();
    }

    public void loadMeTimes() {

        meTimes = new ArrayList<>();
        meTimes = meTimeManagerImpl.fetchAllMeTimeRecurringEvents();
        processMeTimeList(meTimes, false);

        if (internetManager.isInternetConnection(getCenesActivity())) {

            new MeTimeAsyncTask.GetMeTimeDataTask(new MeTimeAsyncTask.GetMeTimeDataTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {
                    try {

                        if (getActivity() == null) {
                            return;
                        }

                        if (response.getBoolean("success")) {
                            meTimeCategoryHeaders = new ArrayList<>();
                            meTimeDataCategoryMap = new HashMap<>();
                            daysSelectionMap = new HashMap<>();
                            llMetimeTilesContainer.removeAllViews();
                            JSONArray meTimeData = response.getJSONArray("data");

                            if (meTimeData != null && meTimeData.length() > 0) {

                                meTimeManagerImpl.deleteAllMeTimeRecurringEvents();

                                Type listType = new TypeToken<List<MeTime>>() {}.getType();
                                meTimes = new Gson().fromJson(response.getJSONArray("data").toString(), listType);
                                processMeTimeList(meTimes, true);


                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute();
        }

    }

    public void processMeTimeList(List<MeTime> meTimes, boolean refresh) {
        for (final MeTime metime: meTimes) {
            if (metime.getItems().size() > 0) {
                String daysStr = "";

                Integer daysInStrList[] = new Integer[metime.getItems().size()];

                int index = 0;
                for(MeTimeItem meTimeItem: metime.getItems()) {

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(meTimeItem.getDayOfWeekTimestamp());
                    daysInStrList[index] = cal.get(Calendar.DAY_OF_WEEK);
                    index++;
                }
                Arrays.sort(daysInStrList);
                boolean allDaysMeTime = false;
                if (allDaysMeTime) {
                    daysStr = "MON-FRI";
                    metime.setDays(daysStr);
                } else {
                    for(int j=0; j < daysInStrList.length; j++) {
                        //JSONObject recJson = recurringPatterns.getJSONObject(j);
                        daysStr += meTimeService.IndexDayMap().get(daysInStrList[j]).substring(0,3).toUpperCase() +",";
                    }
                    metime.setDays(daysStr.substring(0, daysStr.length() - 1));
                }
            }

            System.out.println(metime.getDays());
            if (refresh) {
                meTimeManagerImpl.addMeTime(metime);
            }

            //MeTimeDetails
            LinearLayout detailsLayout = meTimeService.createMetimeCards((CenesBaseActivity)getActivity(), metime);
            detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String jsonStr = new Gson().toJson(metime);
                    Bundle bundle = new Bundle();
                    bundle.putString("meTimeCard", jsonStr);
                    MeTimeCardFragment ll = new MeTimeCardFragment();
                    ll.setArguments(bundle);
                    ((CenesBaseActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, ll)
                            .addToBackStack(MeTimeCardFragment.TAG)
                            .commit();
                }
            });
            llMetimeTilesContainer.addView(detailsLayout);
        }
    }

    public void showDefaultMeData() {
        List<MeTime> defaultCards = meTimeService.getDefaultMeTimeValues((CenesBaseActivity) getActivity());
        for (MeTime meTime: defaultCards) {
            //MeTimeDetails
            final JSONObject meTimeJSON = new JSONObject();
            try {
                meTimeJSON.put("title", meTime.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LinearLayout detailsLayout = meTimeService.createMetimeCards((CenesBaseActivity)getActivity(), meTime);
            detailsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("meTimeCard", meTimeJSON.toString());
                    MeTimeCardFragment ll = new MeTimeCardFragment();
                    ll.setArguments(bundle);
                    ll.setTargetFragment(MeTimeFragment.this, DELETE_METIME_REQUEST_CODE);

                    ((CenesBaseActivity)getActivity()).getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, ll)
                            .addToBackStack(MeTimeCardFragment.TAG)
                            .commit();

                }
            });
            llMetimeTilesContainer.addView(detailsLayout);
        }
    }

    Runnable hideLoadingBlock = new Runnable() {
        public void run() {

            try {
                if (((CenesBaseActivity)getActivity()).rlLoadingBlock != null) {
                    ((CenesBaseActivity)getActivity()).rlLoadingBlock.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            ((CenesBaseActivity) getActivity()).showFooter();
            ((CenesBaseActivity) getActivity()).activateFooterIcon(MeTimeFragment.TAG);

            //Delete CallBack
            if(requestCode == DELETE_METIME_REQUEST_CODE) {
                if(data != null) {
                    String metimeStr = data.getStringExtra(MeTimeFragment.DELETE_METIME_REQUEST_STRING);
                    try {
                        ((CenesBaseActivity) getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((CenesBaseActivity)getActivity()).rlLoadingBlock.setVisibility(View.VISIBLE);
                                ((CenesBaseActivity)getActivity()).tvLoadingMsg.setText("Deleting MeTime.");
                            }
                        });

                        final JSONObject meTimeJSONObj = new JSONObject(metimeStr);

                        if (meTimeJSONObj.has("recurringEventId")) {
                            new MeTimeAsyncTask.DeleteMeTimeDataTask(new MeTimeAsyncTask.DeleteMeTimeDataTask.AsyncResponse() {
                                @Override
                                public void processFinish(JSONObject response) {

                                    ((CenesBaseActivity) getActivity()).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((CenesBaseActivity)getActivity()).tvLoadingMsg.setText("Deleted.");

                                        }
                                    });
                                    try {
                                        meTimeManagerImpl.deleteAllMeTimeRecurringEventsByRecurringEventId(meTimeJSONObj.getLong("recurringEventId"));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    loadMeTimes();
                                    Handler handler = new android.os.Handler();
                                    handler.postDelayed(hideLoadingBlock, 1000);

                                }
                            }).execute(meTimeJSONObj.getLong("recurringEventId"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if(requestCode == SAVE_METIME_REQUEST_CODE) {
                if(data != null) {
                    String metimeStr = data.getStringExtra(SAVE_METIME_REQUEST_STRING);
                    try {

                        final MeTime meTimeBeforeSaving = new Gson().fromJson(metimeStr, MeTime.class);
                        JSONObject meTimeJSONObj = new JSONObject();

                        JSONArray meTimeEvents  = new JSONArray();
                        for(MeTimeItem meTimeItem:  meTimeBeforeSaving.getItems()) {
                            JSONObject meTimeEvent = new JSONObject();
                            try {

                                meTimeEvent.put("title", meTimeBeforeSaving.getTitle());
                                meTimeEvent.put("dayOfWeek", meTimeItem.getDay_Of_week());

                                Calendar startCal = Calendar.getInstance();
                                startCal.setTimeInMillis(meTimeBeforeSaving.getStartTime());
                                startCal.set(Calendar.DAY_OF_WEEK, new MeTimeService().dayIndexMap().get(meTimeItem.getDay_Of_week()));

                                meTimeEvent.put("startTime", startCal.getTimeInMillis());

                                Calendar endCal = Calendar.getInstance();
                                endCal.setTimeInMillis(meTimeBeforeSaving.getEndTime());
                                endCal.set(Calendar.DAY_OF_WEEK, new MeTimeService().dayIndexMap().get(meTimeItem.getDay_Of_week()));
                                meTimeEvent.put("endTime", endCal.getTimeInMillis());
                                meTimeEvents.put(meTimeEvent);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        meTimeJSONObj.put("events", meTimeEvents);
                        boolean isUpdateCallTemp = false;
                        if (meTimeBeforeSaving.getRecurringEventId() != null) {
                            isUpdateCallTemp = true;
                            meTimeJSONObj.put("recurringEventId", meTimeBeforeSaving.getRecurringEventId());
                        }
                        final boolean isUpdateCall = isUpdateCallTemp;
                        ((CenesBaseActivity) getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((CenesBaseActivity)getActivity()).rlLoadingBlock.setVisibility(View.VISIBLE);

                                String loadingText = "Adding MeTIME";
                                if (isUpdateCall) {
                                    loadingText = "Updating MeTIME";
                                }
                                ((CenesBaseActivity)getActivity()).tvLoadingMsg.setText(loadingText);
                            }
                        });

                        File metimePhotoFileTemp = null;
                        if (meTimeJSONObj.has("metimePhotoFilePath")) {
                            metimePhotoFileTemp = new File(meTimeJSONObj.getString("metimePhotoFilePath"));
                        }
                        final File metimePhotoFile = metimePhotoFileTemp;
                        new MeTimeAsyncTask.MeTimePosting(new MeTimeAsyncTask.MeTimePosting.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                try {
                                    if(response != null) {
                                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                                        try {
                                            JSONObject props = new JSONObject();
                                            props.put("Action","MeTime Create Success");
                                            props.put("Title",meTimeBeforeSaving.getTitle());
                                            props.put("UserEmail",loggedInUser.getEmail());
                                            props.put("UserName",loggedInUser.getName());
                                            mixpanel.track("MeTime", props);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        if (metimePhotoFile != null) {
                                            JSONObject recurringEventJson = response.getJSONObject("recurringEvent");
                                            Map<String, Object> photoPostData = new HashMap<>();
                                            photoPostData.put("file",metimePhotoFile);
                                            photoPostData.put("recurringEventId", recurringEventJson.getLong("recurringEventId"));

                                            meTimeManagerImpl.deleteAllMeTimeRecurringEventsByRecurringEventId(recurringEventJson.getLong("recurringEventId"));
                                            meTimeManagerImpl.addMeTime(new Gson().fromJson(recurringEventJson.toString(), MeTime.class));

                                            new MeTimeAsyncTask.UploadPhotoTask(new MeTimeAsyncTask.UploadPhotoTask.AsyncResponse() {
                                                @Override
                                                public void processFinish(JSONObject response) {
                                                    System.out.println(response.toString());

                                                    try {
                                                        String photoStr = response.getString("photo");
                                                        Long recurringEventId = response.getLong("recurringEventId");
                                                        meTimeManagerImpl.updateMeTimePhoto(recurringEventId, photoStr);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    ((CenesBaseActivity) getActivity()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String loadingText = "Added.";
                                                            if (isUpdateCall) {
                                                                loadingText = "Updated.";
                                                            }
                                                            ((CenesBaseActivity)getActivity()).tvLoadingMsg.setText(loadingText);
                                                        }
                                                    });
                                                    loadMeTimes();
                                                    Handler handler = new android.os.Handler();
                                                    handler.postDelayed(hideLoadingBlock, 1000);
                                                }
                                            }).execute(photoPostData);
                                        } else {
                                            ((CenesBaseActivity) getActivity()).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String loadingText = "Added.";
                                                    if (isUpdateCall) {
                                                        loadingText = "Updated.";
                                                    }
                                                    ((CenesBaseActivity)getActivity()).tvLoadingMsg.setText(loadingText);
                                                }
                                            });

                                            loadMeTimes();
                                            Handler handler = new android.os.Handler();
                                            handler.postDelayed(hideLoadingBlock, 1000);
                                        }
                                    } else {
                                        ((CenesBaseActivity) getActivity()).showRequestTimeoutDialog();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(meTimeJSONObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if(requestCode == CANCEL_METIME_REQUEST_CODE) {
                System.out.println("Cancelled");
            }
        }

    }
}
