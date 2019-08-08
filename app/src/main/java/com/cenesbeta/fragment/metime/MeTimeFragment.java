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
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.NavigationFragment;
import com.cenesbeta.service.MeTimeService;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_metime, container, false);

        ((CenesBaseActivity) getActivity()).showFooter();
        ((CenesBaseActivity) getActivity()).activateFooterIcon(MeTimeFragment.TAG);

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());

        //footerHomeIcon = ((MeTimeActivity) getActivity()).footerHomeIcon;
        //footerGatheringIcon = ((MeTimeActivity) getActivity()).footerGatheringIcon;
        ivAddMetime = v.findViewById(R.id.iv_add_metime);
        llMetimeTilesContainer = v.findViewById(R.id.ll_metime_tiles_container);
        homeProfilePic = v.findViewById(R.id.home_profile_pic);

        //footerHomeIcon.setOnClickListener(onClickListener);
        //footerGatheringIcon.setOnClickListener(onClickListener);
        ivAddMetime.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
        meTimeItemMap = new HashMap<>();

        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        User user = userManager.getUser();
        // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
        Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homeProfilePic);

        return v;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //case R.id.footer_home_icon:
                    //getActivity().startActivity(new Intent((MeTimeActivity)getActivity(), HomeScreenActivity.class));
                    //getActivity().finish();
                  //  break;

                //case R.id.footer_gathering_icon:
                    //getActivity().startActivity(new Intent((MeTimeActivity)getActivity(), GatheringScreenActivity.class));
                    //getActivity().finish();
                  //  break;

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

        JSONArray defaultMetimesArr = null;
        Boolean defaultExists = false;
        SharedPreferences prefs = getActivity().getSharedPreferences("DEFAULT_METIME", Context.MODE_PRIVATE);
        if (prefs != null ) {
            defaultExists = true;
            String meTimeJSONString = prefs.getString("defaultMeTimeJSON", null);
            if (meTimeJSONString != null) {
                try {
                    defaultMetimesArr = new JSONArray(meTimeJSONString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Fetching MeTimeData
        if (defaultMetimesArr == null) {
            defaultMetimesArr = new JSONArray();
        }
        final JSONArray finalDefaultMetimesArr = defaultMetimesArr;
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

                        for (int m = 0; m < meTimeData.length(); m++) {
                            JSONObject meTimeJSON = meTimeData.getJSONObject(m);
                            finalDefaultMetimesArr.put(meTimeJSON);
                        }
                        if (finalDefaultMetimesArr != null && finalDefaultMetimesArr.length() > 0) {

                            for (int i = 0; i < finalDefaultMetimesArr.length(); i++) {
                                final JSONObject meTimeJSON = finalDefaultMetimesArr.getJSONObject(i);

                                Gson gson = new Gson();
                                MeTime meTime = gson.fromJson(meTimeJSON.toString(), MeTime.class);

                                System.out.println(meTime.toString());
                                if (meTimeJSON.has("recurringPatterns")) {
                                    JSONArray recurringPatterns = meTimeJSON.getJSONArray("recurringPatterns");
                                    if (recurringPatterns.length() > 0) {
                                        String daysStr = "";

                                        Integer daysInStrList[] = new Integer[recurringPatterns.length()];

                                        for(int j=0; j < recurringPatterns.length(); j++) {
                                            JSONObject recJson = recurringPatterns.getJSONObject(j);

                                            Calendar cal = Calendar.getInstance();
                                            cal.setTimeInMillis(recJson.getLong("dayOfWeekTimestamp"));
                                            daysInStrList[j] = cal.get(Calendar.DAY_OF_WEEK);//recJson.getInt("dayOfWeek");
                                        }
                                        Arrays.sort(daysInStrList);
                                        /*String daysStrTemp = "";
                                        for(int j=0; j < recurringPatterns.length(); j++) {
                                            JSONObject recJson = recurringPatterns.getJSONObject(j);
                                            daysStrTemp += meTimeService.IndexDayMap().get(recJson.getInt("dayOfWeek"))+",";
                                        }*/
                                        boolean allDaysMeTime = false;
                                        /*for (String daysInStrTemp: daysInStrList) {
                                            if (daysStrTemp.indexOf(daysInStrTemp) == -1) {
                                                allDaysMeTime = false;
                                                break;
                                            }
                                        }*/
                                        if (allDaysMeTime) {
                                            daysStr = "MON-FRI";
                                            meTime.setDays(daysStr);
                                        } else {
                                            for(int j=0; j < daysInStrList.length; j++) {
                                                //JSONObject recJson = recurringPatterns.getJSONObject(j);
                                                daysStr += meTimeService.IndexDayMap().get(daysInStrList[j]).substring(0,3).toUpperCase() +",";
                                            }
                                            meTime.setDays(daysStr.substring(0, daysStr.length() - 1));
                                        }
                                    }
                                }
                                System.out.println(meTime.getDays());

                                //MeTimeDetails
                                LinearLayout detailsLayout = meTimeService.createMetimeCards((CenesBaseActivity)getActivity(), meTime);
                                detailsLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("meTimeCard", meTimeJSON.toString());
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

                        } else {
                            //showDefaultMeData();
                        }
                    } else {
                        //showDefaultMeData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute();
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

                        JSONObject meTimeJSONObj = new JSONObject(metimeStr);

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
                        JSONObject meTimeJSONObj = new JSONObject(metimeStr);

                        boolean isUpdateCallTemp = false;
                        if (meTimeJSONObj.has("recurringEventId")) {
                            isUpdateCallTemp = true;
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
                                        if (metimePhotoFile != null) {
                                            JSONObject recurringEventJson = response.getJSONObject("recurringEvent");
                                            Map<String, Object> photoPostData = new HashMap<>();
                                            photoPostData.put("file",metimePhotoFile);
                                            photoPostData.put("recurringEventId", recurringEventJson.getLong("recurringEventId"));

                                            new MeTimeAsyncTask.UploadPhotoTask(new MeTimeAsyncTask.UploadPhotoTask.AsyncResponse() {
                                                @Override
                                                public void processFinish(JSONObject response) {
                                                    System.out.println(response.toString());
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
