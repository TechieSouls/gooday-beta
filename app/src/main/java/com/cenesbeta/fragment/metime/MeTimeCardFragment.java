package com.cenesbeta.fragment.metime;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.MeTimeAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
//import com.cenesbeta.adapter.ImageAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.service.MeTimeService;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.cenesbeta.util.RoundedDrawable;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mandeep on 8/1/19.
 */

public class MeTimeCardFragment extends CenesFragment {

    /*
    *   Long userId;
	*   List<MeTimeEvent> events;
	*   String timezone;
    * */

    public final static String TAG = "MeTimeCardFragment";
    private final static int TIME_PICKER_INTERVAL = 5;

    private Button sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    private Button saveMeTime, deleteMeTime;
    private TextView startTimeText, endTimeText, tvTakePhoto, tvUploadPhoto, tvPhotoCancel;
    private LinearLayout metimeStartTime, metimeEndTime, llSliderDots;
    private EditText etMetimeTitle;
    private RelativeLayout rlUploadMetimeImg, swipeCard, rlPhotoActionSheet;
    private ImageView rivMeTimeImg;
    private View fragmentView;
    View viewOpaque;
    private MeTime metime;
    private CenesApplication cenesApplication;
    private InternetManager internetManager;

    private MeTimeService meTimeService;
    private MeTimeAsyncTask meTimeAsyncTask;
    private Long startTimeMillis;
    private Long endTimeMillis;
    private File metimePhotoFile;
    JSONObject selectedDaysHolder;

    private Uri cameraFileUri;
    private File file;
    private String isTakeOrUpload = "take_picture";

    ViewPager vpImagePager;
    ImageView[] dots;


    private static final int CAMERA_PERMISSION_CODE = 1001, UPLOAD_PERMISSION_CODE = 1002;
    private static final int OPEN_CAMERA_REQUEST_CODE = 1003, OPEN_GALLERY_REQUEST_CODE = 1004;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (fragmentView != null) {
            return fragmentView;
        }
        View view = inflater.inflate(R.layout.fragment_metime_card, container, false);

        fragmentView = view;

        ((CenesBaseActivity) getActivity()).hideFooter();
        cenesApplication = ((CenesBaseActivity)getActivity()).getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        internetManager = coreManager.getInternetManager();

        rlUploadMetimeImg = (RelativeLayout) view.findViewById(R.id.rl_upload_metime_img);
        rlPhotoActionSheet = (RelativeLayout) view.findViewById(R.id.rl_photo_action_sheet);
        swipeCard = (RelativeLayout) view.findViewById(R.id.swipe_card);
        rivMeTimeImg = (ImageView) view.findViewById(R.id.riv_meTime_img);
        etMetimeTitle = (EditText) view.findViewById(R.id.et_metime_title);
        viewOpaque = view.findViewById(R.id.view_opaque);

        sunday = (Button) view.findViewById(R.id.metime_sun_text);
        monday = (Button) view.findViewById(R.id.metime_mon_text);
        tuesday = (Button) view.findViewById(R.id.metime_tue_text);
        wednesday = (Button) view.findViewById(R.id.metime_wed_text);
        thursday = (Button) view.findViewById(R.id.metime_thu_text);
        friday = (Button) view.findViewById(R.id.metime_fri_text);
        saturday = (Button) view.findViewById(R.id.metime_sat_text);


        metimeStartTime = (LinearLayout) view.findViewById(R.id.metime_start_time);
        metimeEndTime = (LinearLayout) view.findViewById(R.id.metime_end_time);

        startTimeText = (TextView) view.findViewById(R.id.startTimeText);
        endTimeText = (TextView) view.findViewById(R.id.endTimeText);

        saveMeTime = (Button) view.findViewById(R.id.btn_save_metime);
        deleteMeTime = (Button) view.findViewById(R.id.btn_delete_meTime);

        tvTakePhoto = (TextView) view.findViewById(R.id.tv_take_photo);
        tvUploadPhoto = (TextView) view.findViewById(R.id.tv_choose_library);
        tvPhotoCancel = (TextView) view.findViewById(R.id.tv_photo_cancel);

        //llSliderDots = (LinearLayout) view.findViewById(R.id.ll_slider_dots);
        //vpImagePager = (ViewPager) view.findViewById(R.id.vp_image_pager);

       /* ImageAdapter adapterView = new ImageAdapter(this, post.getPostImages());
        vpImagePager.setAdapter(adapterView); */

        dots = new ImageView[2];

        //code for slider

        for(int i = 0; i < 2; i++){

            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.xml_circle_tranparent_white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            llSliderDots.addView(dots[i], params);
        }

        dots[1].setImageDrawable(ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.xml_circle_white));

        //code for slider


        slideToTop(swipeCard);

        sunday.setOnClickListener(onClickListener);
        monday.setOnClickListener(onClickListener);
        tuesday.setOnClickListener(onClickListener);
        wednesday.setOnClickListener(onClickListener);
        thursday.setOnClickListener(onClickListener);
        friday.setOnClickListener(onClickListener);
        saturday.setOnClickListener(onClickListener);
        saveMeTime.setOnClickListener(onClickListener);
        deleteMeTime.setOnClickListener(onClickListener);
        metimeStartTime.setOnClickListener(onClickListener);
        metimeEndTime.setOnClickListener(onClickListener);
        rlUploadMetimeImg.setOnClickListener(onClickListener);
        viewOpaque.setOnClickListener(onClickListener);
        tvTakePhoto.setOnClickListener(onClickListener);
        tvUploadPhoto.setOnClickListener(onClickListener);
        tvPhotoCancel.setOnClickListener(onClickListener);
        swipeCard.setOnClickListener(onClickListener);

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());

        metime = new MeTime();
        selectedDaysHolder = new JSONObject();
        metimePhotoFile = null;

        startTimeText.setText("00:00");
        startTimeMillis = null;

        endTimeText.setText("00:00");
        endTimeMillis = null;

        Bundle meTimeFragmentBundle = getArguments();
        //If user clicked on metime card saved
        if (meTimeFragmentBundle != null && meTimeFragmentBundle.getString("meTimeCard") != null) {
            try {
                metime = new Gson().fromJson(meTimeFragmentBundle.getString("meTimeCard"), MeTime.class);

                if (!CenesUtils.isEmpty(metime.getPhoto())) {
                   // rivMeTimeImg.setVisibility(View.VISIBLE);
                    //rlUploadMetimeImg.setVisibility(View.GONE);
                    rivMeTimeImg.getLayoutParams().height = CenesUtils.dpToPx(90);
                    rivMeTimeImg.getLayoutParams().width = CenesUtils.dpToPx(90);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.circleCrop();
                    Glide.with(getActivity()).load(CenesConstants.imageDomain+metime.getPhoto()).apply(requestOptions).into(rivMeTimeImg);
                }

                System.out.println(metime.getTitle());
                etMetimeTitle.setText(metime.getTitle());

                if (metime.getStartTime() != null && metime.getStartTime() != 0) {

                    startTimeText.setText(CenesUtils.hmm_aa.format(new Date(metime.getStartTime())).toUpperCase());
                    endTimeText.setText(CenesUtils.hmm_aa.format(new Date(metime.getEndTime())).toUpperCase());


                    for(MeTimeItem meTimeItem: metime.getItems()) {

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(meTimeItem.getDayOfWeekTimestamp());
                        //daysInStrList[j] = cal.get(Calendar.DAY_OF_WEEK);//recJson.getInt("dayOfWeek");
                        selectedDaysHolder.put(meTimeService.IndexDayMap().get(cal.get(Calendar.DAY_OF_WEEK)), true);
                    }

                    Iterator<String> daysItr = selectedDaysHolder.keys();
                    while (daysItr.hasNext()) {
                        String day = daysItr.next();
                        if ("Sunday".equals(day)) {
                            sunday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Monday".equals(day)) {
                            monday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            monday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Tuesday".equals(day)) {
                            tuesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Wednesday".equals(day)) {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Thursday".equals(day)) {
                            thursday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Friday".equals(day)) {
                            friday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            friday.setTextColor(getResources().getColor(R.color.white));
                        } else if ("Saturday".equals(day)) {
                            saturday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {


        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.swipe_card:
                    hideKeyboardAndClearFocus(etMetimeTitle);
                    break;

                case R.id.rl_upload_metime_img:
                    hideKeyboardAndClearFocus(etMetimeTitle);
                    rlPhotoActionSheet.setVisibility(View.VISIBLE);

                    break;

                case R.id.tv_take_photo:
                    isTakeOrUpload = "take_picture";
                    checkCameraPermissiosn();
                    rlPhotoActionSheet.setVisibility(View.GONE);


                    break;
                case R.id.tv_choose_library:
                    isTakeOrUpload = "upload_picture";
                    checkReadWritePermissiosn();
                    rlPhotoActionSheet.setVisibility(View.GONE);

                    break;

                case R.id.tv_photo_cancel:
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;

                case R.id.metime_sun_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Sunday") || !selectedDaysHolder.getBoolean("Sunday")) {
                            sunday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Sunday", true);
                        } else {
                            sunday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            sunday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Sunday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.metime_mon_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Monday") || !selectedDaysHolder.getBoolean("Monday")) {
                            monday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            monday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Monday", true);
                        } else {
                            monday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            monday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Monday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_tue_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Tuesday") || !selectedDaysHolder.getBoolean("Tuesday")) {
                            tuesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Tuesday", true);

                        } else {
                            selectedDaysHolder.put("Tuesday", false);
                            tuesday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            tuesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Tuesday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_wed_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Wednesday") || !selectedDaysHolder.getBoolean("Wednesday")) {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Wednesday", true);

                        } else {
                            wednesday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            wednesday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Wednesday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_thu_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Thursday") || !selectedDaysHolder.getBoolean("Thursday")) {
                            thursday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Thursday", true);

                        } else {
                            thursday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            thursday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Thursday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_fri_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Friday") || !selectedDaysHolder.getBoolean("Friday")) {
                            friday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            friday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Friday", true);

                        } else {
                            friday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            friday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Friday");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_sat_text:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    try {
                        if (!selectedDaysHolder.has("Saturday") || !selectedDaysHolder.getBoolean("Saturday")) {
                            saturday.setBackground(getResources().getDrawable(R.drawable.round_button_red));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.put("Saturday", true);
                        } else {
                            saturday.setBackground(getResources().getDrawable(R.drawable.xml_circle_trans_white_border));
                            saturday.setTextColor(getResources().getColor(R.color.white));
                            selectedDaysHolder.remove("Saturday");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.metime_start_time:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    Calendar mcurrentTimeForStartTime = Calendar.getInstance();
                    int mcurrentTimeForStartTimeHour = mcurrentTimeForStartTime.get(Calendar.HOUR_OF_DAY);
                    int mcurrentTimeForStartTimeMinute = mcurrentTimeForStartTime.get(Calendar.MINUTE);

                    TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);

                            System.out.println("HOur"+mcurrentTime.get(Calendar.HOUR)+"  -- "+selectedHour);

                            String ampm = "AM";
                            if (selectedHour >= 12) {
                                ampm = "PM";
                            }
                           /* String singleDigitZero = "";
                            if (mcurrentTime.get(Calendar.HOUR) < 10  && ampm.equals("AM")) {
                                singleDigitZero = "0";
                            }*/

                           int selectedHourTemp = 0;
                           if (selectedHour == 0 || selectedHour == 12) {
                               selectedHourTemp = 12;
                           } else {
                               selectedHourTemp = mcurrentTime.get(Calendar.HOUR);
                           }

                            String singleDigitMinuteZero = "";
                            if (selectedMinute < 10) {
                                singleDigitMinuteZero = "0";
                            }
                            startTimeText.setText( selectedHourTemp+ ":" + singleDigitMinuteZero + selectedMinute+ampm);
                            metime.setStartTime(mcurrentTime.getTimeInMillis());
                        }
                    };

                    TimePickerDialog startTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), startTimePickerListener, mcurrentTimeForStartTimeHour, mcurrentTimeForStartTimeMinute, false);

                    startTimeDateTimePicker.setTitle("Select Time");
                    startTimeDateTimePicker.show();

                    break;
                case R.id.metime_end_time:
                    hideKeyboardAndClearFocus(etMetimeTitle);

                    rlPhotoActionSheet.setVisibility(View.GONE);
                    Calendar endCalTime = Calendar.getInstance();
                    int endTimeHour = endCalTime.get(Calendar.HOUR_OF_DAY);
                    int endTimeMinute = endCalTime.get(Calendar.MINUTE);

                    TimePickerDialog.OnTimeSetListener onTimeSetListenerEndTime = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Calendar mcurrentTime = Calendar.getInstance();
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                            String ampm = "AM";
                            if (selectedHour >= 12) {
                                ampm = "PM";
                            }
                            /*String singleDigitZero = "";
                            if (mcurrentTime.get(Calendar.HOUR) < 10 && ampm.equals("AM")) {
                                singleDigitZero = "0";
                            }*/

                            int selectedHourTemp = 0;
                            if (selectedHour == 0 || selectedHour == 12) {
                                selectedHourTemp = 12;
                            } else {
                                selectedHourTemp = mcurrentTime.get(Calendar.HOUR);
                            }
                            String singleDigitMinuteZero = "";
                            if (selectedMinute < 10) {
                                singleDigitMinuteZero = "0";
                            }
                            endTimeText.setText(selectedHourTemp + ":" + singleDigitMinuteZero + selectedMinute+ampm);
                            metime.setEndTime(mcurrentTime.getTimeInMillis());
                        }
                    };

                    TimePickerDialog endTimeDateTimePicker;
                    endTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), onTimeSetListenerEndTime, endTimeHour, endTimeMinute / TIME_PICKER_INTERVAL, false);
                    endTimeDateTimePicker.setTitle("Select Time");
                    endTimeDateTimePicker.show();
                    break;
                case R.id.btn_save_metime:

                    if (!internetManager.isInternetConnection(getCenesActivity())) {
                        showAlert("No Internet", "Please connect to Internet.");
                        return;
                    }
                    if (CenesUtils.isEmpty(etMetimeTitle.getText().toString())) {
                        Toast.makeText(getActivity(), "Please enter title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (metime.getStartTime() == null || metime.getStartTime() == 0) {
                        Toast.makeText(getActivity(), "Please select Start Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (metime.getEndTime() == null || metime.getEndTime() == 0) {
                        Toast.makeText(getActivity(), "Please select End Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!selectedDaysHolder.keys().hasNext()) {
                        Toast.makeText(getActivity(), "Please select Days", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    metime.setTitle(etMetimeTitle.getText().toString());
                    if (metimePhotoFile != null) {
                        metime.setPhotoToUpload(metimePhotoFile.getAbsolutePath());
                    }
                    metime.setItems(null);
                    List<MeTimeItem> meTimeItemList = new ArrayList<>();
                    Iterator<String> itr = selectedDaysHolder.keys();
                     while(itr.hasNext()) {
                         MeTimeItem meTimeItem = new MeTimeItem();
                         try {
                             String dayOfWeek = itr.next();

                             meTimeItem.setDay_Of_week(dayOfWeek);
                             if (metime.getRecurringEventId() != null) {
                                 meTimeItem.setRecurringEventId(metime.getRecurringEventId());
                             }
                             meTimeItemList.add(meTimeItem);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                    metime.setItems(meTimeItemList);
                    Intent intent = new Intent();
                    try {
                        intent.putExtra(MeTimeFragment.SAVE_METIME_REQUEST_STRING, new Gson().toJson(metime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((CenesBaseActivity)getActivity()).fragmentManager.getFragments();
                    for (Fragment fragment : ((CenesBaseActivity)getActivity()).fragmentManager.getFragments()) {
                        if (fragment instanceof MeTimeFragment) {
                            fragment.onActivityResult(MeTimeFragment.SAVE_METIME_REQUEST_CODE, Activity.RESULT_OK, intent);
                        }
                    }
                    getFragmentManager().popBackStack();
                    break;

                case R.id.btn_delete_meTime:


                    Intent deleteIntent = new Intent();
                    if (metime.getRecurringEventId() != null) {
                        try {
                            JSONObject responseJSON = new JSONObject();
                            responseJSON.put("recurringEventId", metime.getRecurringEventId());
                            deleteIntent.putExtra(MeTimeFragment.DELETE_METIME_REQUEST_STRING, responseJSON.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ((CenesBaseActivity) getActivity()).fragmentManager.getFragments();
                    for (Fragment fragment : ((CenesBaseActivity) getActivity()).fragmentManager.getFragments()) {
                        if (fragment instanceof MeTimeFragment) {
                            if (metime.getRecurringEventId() != null) {
                                fragment.onActivityResult(MeTimeFragment.DELETE_METIME_REQUEST_CODE, Activity.RESULT_OK, deleteIntent);
                            } else {
                                fragment.onActivityResult(MeTimeFragment.CANCEL_METIME_REQUEST_CODE, Activity.RESULT_OK, deleteIntent);
                            }
                        }
                    }

                    getFragmentManager().popBackStack();
                    break;
                case R.id.view_opaque:
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                    break;

            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkReadWritePermissiosn();
            }
        } else if (requestCode == UPLOAD_PERMISSION_CODE) {
            try  {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firePictureIntent();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_CAMERA_REQUEST_CODE || requestCode == OPEN_GALLERY_REQUEST_CODE) {
                try {
                    if (isTakeOrUpload.equals("take_picture")) {

                        ImageUtils.cropImageWithAspect(cameraFileUri, this, 200, 200);

                    } else if (isTakeOrUpload.equals("upload_picture")) {

                        String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), data.getData());

                        Uri imageUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), imageUri);
                        ExifInterface ei = new ExifInterface(filePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }

                        //Uri resultUri = Uri.fromFile(new File(ImageUtils.getDefaultFile()));
                        ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 200, 200);
                        //UCrop.of(imageUri, resultUri)
                                //.withAspectRatio(3, 4)
                                //.withMaxResultSize(200, 200)
                                //.start(getContext(), MeTimeCardFragment.this, UCrop.REQUEST_CROP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == Crop.REQUEST_CROP) {
                try {
                    metimePhotoFile = null;
                    final String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                    metimePhotoFile = new File(filePath);

                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), Crop.getOutput(data));

                    ExifInterface ei = new ExifInterface(filePath);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);

                    Bitmap rotatedBitmap = null;
                    switch(orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotatedBitmap = rotateImage(imageBitmap, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotatedBitmap = rotateImage(imageBitmap, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotatedBitmap = rotateImage(imageBitmap, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            rotatedBitmap = imageBitmap;
                    }

                    rivMeTimeImg.getLayoutParams().height = CenesUtils.dpToPx(90);
                    rivMeTimeImg.getLayoutParams().width = CenesUtils.dpToPx(90);

                    persistImage(rotatedBitmap, filePath.substring(filePath.lastIndexOf("/"), filePath.length()));

                    final Bitmap bitImage = imageBitmap;
                    final RoundedDrawable drawable = new RoundedDrawable(ImageUtils.getRotatedBitmap(bitImage, filePath));

                    //Do something after 100ms
                    rivMeTimeImg.invalidate();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rivMeTimeImg.setImageDrawable(drawable);
                        }
                    }, 500);
                    rivMeTimeImg.requestLayout();

                    //rivMeTimeImg.setImageURI(getImageUri(getContext().getApplicationContext(), rotatedBitmap));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void persistImage(Bitmap bitmap, String name) {
        File filesDir = getActivity().getApplicationContext().getFilesDir();
        file = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
    }

    public static void slideToBottom(RelativeLayout view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,1000);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void slideToTop(RelativeLayout view){
        System.out.println(view.getHeight());
        TranslateAnimation animate = new TranslateAnimation(0,0,1000,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void firePictureIntent() {
        if (isTakeOrUpload == "take_picture") {
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cenes";
            File newdir = new File(dir);
            newdir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            file = new File(dir + File.separator + "IMG_" + timeStamp + ".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                cameraFileUri = null;
                cameraFileUri = Uri.fromFile(file);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            this.startActivityForResult(takePictureIntent, OPEN_CAMERA_REQUEST_CODE);

        } else if (isTakeOrUpload == "upload_picture") {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            this.startActivityForResult(browseIntent, OPEN_GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("cameraMediaOutputUri", cameraFileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState !=null && savedInstanceState.containsKey("cameraMediaOutputUri"))
            cameraFileUri = savedInstanceState.getParcelable("cameraMediaOutputUri");
    }

    public void checkCameraPermissiosn() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            checkReadWritePermissiosn();
        }
    }

    public void checkReadWritePermissiosn() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
        } else {
            firePictureIntent();
        }
    }

}
