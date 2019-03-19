package com.cenes.fragment.metime;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.AsyncTasks.MeTimeAsyncTask;
import com.cenes.R;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.MeTimeActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.MeTime;
import com.cenes.fragment.CenesFragment;
import com.cenes.service.MeTimeService;
import com.cenes.util.CenesConstants;
import com.cenes.util.CenesUtils;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedImageView;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private final static int UPLOAD_IMAGE_CODE = 100;

    private Button sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    private Button saveMeTime, deleteMeTime;
    private TextView startTimeText, endTimeText;
    private LinearLayout metimeStartTime, metimeEndTime;
    private EditText etMetimeTitle;
    private RelativeLayout rlUploadMetimeImg, swipeCard;
    private RoundedImageView rivMeTimeImg;
    View viewOpaque;

    JSONObject meTimeJSONObj;
    private JSONObject meTimeData = new JSONObject();
    private CenesApplication cenesApplication;
    private MeTimeService meTimeService;
    private MeTimeAsyncTask meTimeAsyncTask;
    private Long startTimeMillis;
    private Long endTimeMillis;
    private File metimePhotoFile;
    JSONObject selectedDaysHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_metime_card, container, false);

        ((CenesBaseActivity) getActivity()).hideFooter();

        rlUploadMetimeImg = (RelativeLayout) view.findViewById(R.id.rl_upload_metime_img);
        swipeCard = (RelativeLayout) view.findViewById(R.id.swipe_card);
        rivMeTimeImg = (RoundedImageView) view.findViewById(R.id.riv_meTime_img);
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

        rivMeTimeImg.setOnClickListener(onClickListener);
        rivMeTimeImg.setOnClickListener(onClickListener);

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

        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(width, height);
        viewOpaque.setLayoutParams(viewParams);*/

        cenesApplication = getCenesActivity().getCenesApplication();
        meTimeService = new MeTimeService();
        meTimeAsyncTask = new MeTimeAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());

        meTimeJSONObj = new JSONObject();
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
                meTimeJSONObj = new JSONObject(meTimeFragmentBundle.getString("meTimeCard"));

                if (!CenesUtils.isEmpty(meTimeJSONObj.getString("photo"))) {
                    rivMeTimeImg.setVisibility(View.VISIBLE);
                    rlUploadMetimeImg.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(CenesConstants.imageDomain+meTimeJSONObj.getString("photo")).apply(RequestOptions.placeholderOf(R.drawable.metime_default)).into(rivMeTimeImg);
                }

                etMetimeTitle.setText(meTimeJSONObj.getString("title"));

                if (meTimeJSONObj.has("startTime")) {
                    startTimeMillis = meTimeJSONObj.getLong("startTime");
                    endTimeMillis = meTimeJSONObj.getLong("endTime");

                    startTimeText.setText(CenesUtils.hmmaa.format(new Date(startTimeMillis)));
                    endTimeText.setText(CenesUtils.hmmaa.format(new Date(endTimeMillis)));

                    JSONArray recurringPatterns = meTimeJSONObj.getJSONArray("recurringPatterns");

                    for(int i=0; i < recurringPatterns.length(); i++) {

                        JSONObject recJson = recurringPatterns.getJSONObject(i);
                        //selectedDaysHolder.put(meTimeService.IndexDayMap().get(recJson.getInt("dayOfWeek")), true);
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(recJson.getLong("dayOfWeekTimestamp"));
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

                case R.id.rl_upload_metime_img:
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        browseIntent.setType("image/*");

                        startActivityForResult(browseIntent, UPLOAD_IMAGE_CODE);
                    }
                    break;
                case R.id.riv_meTime_img:
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        browseIntent.setType("image/*");

                        startActivityForResult(browseIntent, UPLOAD_IMAGE_CODE);
                    }
                    break;
                case R.id.metime_sun_text:
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
                            startTimeMillis = mcurrentTime.getTimeInMillis();
                        }
                    };

                    TimePickerDialog startTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), startTimePickerListener, mcurrentTimeForStartTimeHour, mcurrentTimeForStartTimeMinute, false);

                    startTimeDateTimePicker.setTitle("Select Time");
                    startTimeDateTimePicker.show();

                    break;
                case R.id.metime_end_time:
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
                            endTimeMillis = mcurrentTime.getTimeInMillis();
                        }
                    };

                    TimePickerDialog endTimeDateTimePicker;
                    endTimeDateTimePicker = new TimePickerDialog(getCenesActivity(), onTimeSetListenerEndTime, endTimeHour, endTimeMinute / TIME_PICKER_INTERVAL, false);
                    endTimeDateTimePicker.setTitle("Select Time");
                    endTimeDateTimePicker.show();
                    break;
                case R.id.btn_save_metime:

                    if (CenesUtils.isEmpty(etMetimeTitle.getText().toString())) {
                        Toast.makeText(getActivity(), "Please enter title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startTimeMillis == null) {
                        Toast.makeText(getActivity(), "Please select Start Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endTimeMillis == null) {
                        Toast.makeText(getActivity(), "Please select End Time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!selectedDaysHolder.keys().hasNext()) {
                        Toast.makeText(getActivity(), "Please select Days", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    JSONArray meTimeEvents = new JSONArray();
                    Iterator<String> itr = selectedDaysHolder.keys();
                     while(itr.hasNext()) {
                        JSONObject meTimeEvent = new JSONObject();
                        try {
                            String dayOfWeek = itr.next();

                            meTimeEvent.put("title", etMetimeTitle.getText().toString());
                            meTimeEvent.put("dayOfWeek", dayOfWeek);

                            Calendar startCal = Calendar.getInstance();
                            startCal.setTimeInMillis(startTimeMillis);
                            startCal.set(Calendar.DAY_OF_WEEK, new MeTimeService().dayIndexMap().get(dayOfWeek));

                            meTimeEvent.put("startTime", startCal.getTimeInMillis());

                            Calendar endCal = Calendar.getInstance();
                            endCal.setTimeInMillis(endTimeMillis);
                            endCal.set(Calendar.DAY_OF_WEEK, new MeTimeService().dayIndexMap().get(dayOfWeek));
                            meTimeEvent.put("endTime", endCal.getTimeInMillis());
                            meTimeEvents.put(meTimeEvent);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    try {
                        meTimeJSONObj.put("events", meTimeEvents);
                        if (metimePhotoFile != null) {
                            meTimeJSONObj.put("metimePhotoFilePath", metimePhotoFile.getPath());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent intent = new Intent();
                    try {
                        intent.putExtra(MeTimeFragment.SAVE_METIME_REQUEST_STRING, meTimeJSONObj.toString());
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
                    if (meTimeJSONObj.has("recurringEventId")) {
                        try {
                            JSONObject responseJSON = new JSONObject();
                            responseJSON.put("recurringEventId", meTimeJSONObj.getLong("recurringEventId"));
                            deleteIntent.putExtra(MeTimeFragment.DELETE_METIME_REQUEST_STRING, responseJSON.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ((CenesBaseActivity) getActivity()).fragmentManager.getFragments();
                    for (Fragment fragment : ((CenesBaseActivity) getActivity()).fragmentManager.getFragments()) {
                        if (fragment instanceof MeTimeFragment) {
                            if (meTimeJSONObj.has("recurringEventId")) {
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
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {

                Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                browseIntent.setType("image/*");
                startActivityForResult(browseIntent, UPLOAD_IMAGE_CODE);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            try {

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


                ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 200, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            try {
                String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
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

                rlUploadMetimeImg.setVisibility(View.GONE);
                rivMeTimeImg.setVisibility(View.VISIBLE);
                rivMeTimeImg.setImageBitmap(rotatedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}