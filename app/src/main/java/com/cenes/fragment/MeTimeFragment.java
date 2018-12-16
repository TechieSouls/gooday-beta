package com.cenes.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.activity.CenesActivity;
import com.cenes.activity.DiaryActivity;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.ReminderActivity;
import com.cenes.adapter.MeTimeAdapter;
import com.cenes.application.CenesApplication;
import com.cenes.bo.MeTimeItem;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.util.CenesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rohan on 10/10/17.
 */

public class MeTimeFragment extends CenesFragment {

    public final static String TAG = "MeTimeFragment";

    ImageView bedTime, addNewCategory;

    Map<String, MeTimeItem> meTimeItemMap;
    private List<MeTimeItem> meTimeItemList = new ArrayList<>();
    List<String> meTimeCategoryHeaders;
    Map<String, JSONObject> meTimeDataCategoryMap;
    Map<String, Boolean> daysSelectionMap;

    private MeTimeAdapter meTimeAdapter;
    private ListView metimeCatListView;
    private Map<Integer, String> daysIndexMap;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    public UserManager userManager;
    public ApiManager apiManager;
    public UrlManager urlManager;
    private User loggedInUser;
    RelativeLayout rlHeader;
    ImageView ivProfile;
    private User user;
    TextView tvSave;
    ProgressDialog processDialog;

    public static boolean isFirstLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_metime, container, false);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        loggedInUser = userManager.getUser();

        addNewCategory = (ImageView) v.findViewById(R.id.add_new_category);
        addNewCategory.setOnClickListener(onClickListener);
        metimeCatListView = (ListView) v.findViewById(R.id.metime_cat_list_view);
        rlHeader = (RelativeLayout) v.findViewById(R.id.rl_header);
        ivProfile = (ImageView) v.findViewById(R.id.ivProfile);
        tvSave = (TextView) v.findViewById(R.id.tvSave);

        meTimeItemMap = new HashMap<>();

        daysIndexMap = new HashMap<>();
        daysIndexMap.put(1, "Sunday");
        daysIndexMap.put(2, "Monday");
        daysIndexMap.put(3, "Tuesday");
        daysIndexMap.put(4, "Wednesday");
        daysIndexMap.put(5, "Thursday");
        daysIndexMap.put(6, "Friday");
        daysIndexMap.put(7, "Saturday");

        isFirstLogin = ((CenesActivity) getActivity()).sharedPrefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            rlHeader.setVisibility(View.GONE);
            showDefaultMeData();
            meTimeAdapter = new MeTimeAdapter(this, meTimeCategoryHeaders,meTimeDataCategoryMap,false);
            metimeCatListView.setAdapter(meTimeAdapter);
        } else {
            new MeTimePopulatingTask().execute();

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
        try {
            CenesUtils.logEntries(loggedInUser, "MeTime Next Btn Clicked", getCenesActivity().getApplicationContext());
            //if (meTimeItemList != null && meTimeItemList.size() > 0) {
            meTimeAdapter.saveMeTime();// MeTimePostingTask().execute(meTimeItemList);
            //}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void showDefaultMeData() {
        meTimeDataCategoryMap = new HashMap<>();
        meTimeCategoryHeaders = new ArrayList<>();
        meTimeCategoryHeaders.add("Bedtime");
        meTimeCategoryHeaders.add("Workout");
        meTimeCategoryHeaders.add("Family Time");
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_new_category:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getCenesActivity());
                    builder.setTitle("Add New MeTime Category");

                    // Set up the input
                    final EditText input = new EditText(getCenesActivity());
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newCategory = input.getText().toString();

                            meTimeCategoryHeaders.add(newCategory);
                            meTimeAdapter = new MeTimeAdapter(MeTimeFragment.this, meTimeCategoryHeaders,meTimeDataCategoryMap,true);
                            metimeCatListView.setAdapter(meTimeAdapter);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    break;
            }
        }
    };


    class MeTimePopulatingTask extends AsyncTask<String ,JSONObject,JSONObject> {
        ProgressDialog processDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(getCenesActivity());
            processDialog.setMessage("Processing..");
            processDialog.setIndeterminate(false);
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... lists) {

            loggedInUser.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?user_id="+loggedInUser.getUserId();
            JSONObject response = apiManager.getUserMeTimeData(loggedInUser,queryStr,getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            processDialog.dismiss();
            processDialog = null;
            try {
                if (response.getBoolean("success")){
                    meTimeCategoryHeaders = new ArrayList<>();
                    meTimeDataCategoryMap = new HashMap<>();
                    daysSelectionMap = new HashMap<>();

                    boolean dataFromDb = false;
                    JSONArray meTimeData = response.getJSONArray("data");
                    if (meTimeData != null && meTimeData.length() > 0) {

                        for (int i=0; i<meTimeData.length(); i++) {
                            JSONObject meTime = meTimeData.getJSONObject(i);

                            JSONObject meTimeJSONObj = new JSONObject();
                            meTimeJSONObj.put("start_time",meTime.getLong("startTime"));
                            meTimeJSONObj.put("end_time",meTime.getLong("endTime"));

                            JSONArray meTimeRecurring = meTime.getJSONArray("recurringPatterns");
                            for (int j=0; j < meTimeRecurring.length(); j++) {

                                Calendar meTimePatternCal = Calendar.getInstance();
                                meTimePatternCal.setTimeInMillis(meTimeRecurring.getJSONObject(j).getLong("dayOfWeekTimestamp"));
                                meTimeJSONObj.put(daysIndexMap.get(meTimePatternCal.get(Calendar.DAY_OF_WEEK)),true);
                                daysSelectionMap.put(daysIndexMap.get(meTimePatternCal.get(Calendar.DAY_OF_WEEK)),true);
                            }

                            meTimeDataCategoryMap.put(meTime.getString("title"),meTimeJSONObj);
                            meTimeCategoryHeaders.add(meTime.getString("title"));
                        }


                        List<String> defaultCats = new ArrayList<>();
                        defaultCats.add("Bedtime");
                        defaultCats.add("Workout");
                        defaultCats.add("Family Time");
                        for (String cat : defaultCats) {
                            if (!meTimeCategoryHeaders.contains(cat)) {
                                meTimeCategoryHeaders.add(cat);
                            }
                        }
                        dataFromDb = true;
                    } else {
                        showDefaultMeData();
                    }

                    meTimeAdapter = new MeTimeAdapter(MeTimeFragment.this, meTimeCategoryHeaders,meTimeDataCategoryMap,dataFromDb);
                    metimeCatListView.setAdapter(meTimeAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
