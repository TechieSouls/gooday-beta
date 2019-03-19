package com.cenes.fragment.gathering;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.AlertManager;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.DeviceManager;
import com.cenes.Manager.UrlManager;
import com.cenes.Manager.ValidationManager;
import com.cenes.R;
import com.cenes.activity.CenesBaseActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.SearchFriendActivity;
import com.cenes.activity.SearchLocationActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Event;
import com.cenes.bo.EventMember;
import com.cenes.bo.Location;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.fragment.CenesFragment;
import com.cenes.materialcalendarview.CalendarDay;
import com.cenes.materialcalendarview.MaterialCalendarView;
import com.cenes.materialcalendarview.OnDateSelectedListener;
import com.cenes.materialcalendarview.decorators.BackgroundDecorator;
import com.cenes.materialcalendarview.decorators.EventDecorator;
import com.cenes.materialcalendarview.decorators.OneDayDecorator;
import com.cenes.service.GatheringService;
import com.cenes.util.CenesConstants;
import com.cenes.util.CenesUtils;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mandeep on 2/11/17.
 */

public class CreateGatheringFragment extends CenesFragment implements View.OnFocusChangeListener {

    public static String TAG = "CreateGatheringFragment";
    private int SEACRH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002, GATHERING_SUMMARY_RESULT_CODE = 1003;

    private View fragmentView;

    private GatheringPreviewFragment gatheringPreviewFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private Set<EventMember> inviteFriendsImageList;
    private Calendar predictedDateStartCal, predictedDateEndCal;
    private boolean isPredictiveOn;
    private Boolean requestForGatheringSummary = false;
    private Calendar currentMonth;
    private JSONArray predictiveClanedarAPIData;

    private ImageView gathEventImage, ivEventShareIcon;
    private ImageView ivShowTimeMatchInfo;
    private EditText gathEventTitleEditView, gatheringDescription;
    TextView gath_date_after_fix, gathInviteFrndsBtn, gathSelectDatetimeBtn, gathSearchLocationButton;
    private Switch predictiveCalSwitch;
    private TextView startTimePickerLabel, endTimePickerLabel;
    private TextView previewGatheringBtn;
    private TextView ivCreateGatheringCloseButton, editGatheringSummaryBtn, gatheringTitle, eventTime, event_time_am_pm, saveGatheringButton, gatheringDeletButton;
    //private ImageView backGatheringSumamaryBtn;
    List<CalendarDay> enableDates;

    private File eventImageFile;
    private DeviceManager deviceManager;

    ProgressDialog mProgressDialog;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    private AlertManager alertManager;
    private ValidationManager validationManager;
    private Context context;
    private Long eventId;
    private String placeId;
    private Boolean isEditMode = false;
    private User loggedInUser;
    private Event event;

    private MaterialCalendarView materialCalendarView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gathering_create, container, false);
        fragmentView = view;

        initializeComponents();
        addClickListnersToComponents();

        ((CenesBaseActivity)getActivity()).hideFooter();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter("month_changed_intent"));

        loggedInUser = userManager.getUser();

        predictedDateStartCal = Calendar.getInstance();

        int minute = predictedDateStartCal.get(predictedDateStartCal.MINUTE);
        int hourOfDay = predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY);

        //Setting time to a multiple of 5 when user opens the create gathering for first time.
        minute = minute + (5 - (minute % 5));

        if (minute == 60) {
            hourOfDay = hourOfDay + 1;
            minute = 0;
        }

        predictedDateStartCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        predictedDateStartCal.set(Calendar.MINUTE, minute);
        predictedDateStartCal.add(Calendar.MINUTE, 0);
        predictedDateStartCal.set(Calendar.SECOND, 0);
        predictedDateStartCal.set(Calendar.MILLISECOND, 0);

        //Setting End time to be default 1 hour delay to Start Time
        predictedDateEndCal = Calendar.getInstance();
        predictedDateEndCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        predictedDateEndCal.set(Calendar.MINUTE, minute);
        predictedDateEndCal.add(Calendar.MINUTE, 60);
        predictedDateEndCal.set(Calendar.SECOND, 0);
        predictedDateEndCal.set(Calendar.MILLISECOND, 0);

        startTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateStartCal.getTime()));
        endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()));

        materialCalendarView.setCurrentDate(predictedDateStartCal);

        currentMonth = predictedDateStartCal;

        ivCreateGatheringCloseButton = (TextView) fragmentView.findViewById(R.id.crt_gath_cls_btn);

        //Registering click listeners
        ivCreateGatheringCloseButton.setOnClickListener(onClickListener);

        CalendarDay calDay = new CalendarDay();
        Set<CalendarDay> calDaySet = new HashSet<>();
        calDay = new CalendarDay(predictedDateStartCal.getTime());
        calDaySet.add(calDay);

        BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_orange_color, calDaySet, true, false);
        materialCalendarView.addDecorator(calBgDecorator);

        Bundle bundle = this.getArguments();
        if (bundle != null && "list".equals(bundle.getString("dataFrom"))) {
            eventId = bundle.getLong("eventId");
            requestForGatheringSummary = true;
            try {
                new EventInfoTask().execute(eventId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            event = new Event();
        }
        //Code to disable previous dates
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.set(Calendar.DAY_OF_MONTH, endDateCalendar.getMaximum(Calendar.DAY_OF_MONTH));
        endDateCalendar.add(Calendar.MONTH, 12);

        materialCalendarView.state().edit()
                .setMinimumDate(startDateCalendar.getTime())
                .setMaximumDate(endDateCalendar.getTime())
                .commit();
        //new HolidayCalendarTask().execute();

        gathEventTitleEditView.setOnTouchListener(clearTextTouchListener);
        gathEventTitleEditView.setOnFocusChangeListener(this);
        gatheringDescription.setOnTouchListener(clearTextTouchListener);
        gatheringDescription.setOnFocusChangeListener(this);
        return view;
    }

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public View.OnTouchListener clearTextTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            EditText et = (EditText) v;
            if (event.getAction() == MotionEvent.ACTION_UP && et.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                if (event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) - 50) {
                    et.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((EditText) v).setCompoundDrawablesRelativeWithIntrinsicBounds(((EditText) v).getCompoundDrawables()[DRAWABLE_LEFT], null, getResources().getDrawable(R.drawable.close), null);
        } else {
            ((EditText) v).setCompoundDrawablesRelativeWithIntrinsicBounds(((EditText) v).getCompoundDrawables()[DRAWABLE_LEFT], null, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity) getActivity()).hideFooter();
        System.out.println("On Resume Called..");

        try {
            if (((CenesBaseActivity)getActivity()).parentEvent != null) {
                event = ((CenesBaseActivity)getActivity()).parentEvent;

                startTimePickerLabel.setText(CenesUtils.hmmaa.format(new Date(Long.valueOf(((CenesBaseActivity)getActivity()).parentEvent.getStartTime()))));
                endTimePickerLabel.setText(CenesUtils.hmmaa.format(new Date(Long.valueOf(((CenesBaseActivity)getActivity()).parentEvent.getEndTime()))));
                predictedDateStartCal.setTimeInMillis(Long.valueOf(((CenesBaseActivity)getActivity()).parentEvent.getStartTime()));
                predictedDateEndCal.setTimeInMillis(Long.valueOf(((CenesBaseActivity)getActivity()).parentEvent.getEndTime()));

                fragmentView.findViewById(R.id.gath_select_datetime_btn).setVisibility(View.GONE);

                RelativeLayout rl1 = (RelativeLayout) fragmentView.findViewById(R.id.gath_select_datetime_mcv_section);
                rl1.setVisibility(View.VISIBLE);

                fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block).setVisibility(View.GONE);
                RelativeLayout rl3 = (RelativeLayout) fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block);
                rl3.setVisibility(View.GONE);

                materialCalendarView.removeDecorator(mOneDayDecorator);
                CalendarDay calDay = new CalendarDay();
                calDay = new CalendarDay(predictedDateStartCal.getTime());
                mOneDayDecorator = new OneDayDecorator(calDay, getActivity(), R.drawable.calendar_selector_red);
                materialCalendarView.addDecorator(mOneDayDecorator);

                String am_pm = "AM";
                if (predictedDateStartCal.get(Calendar.HOUR_OF_DAY) > 11) {
                    am_pm = "PM";
                }

                String end_am_pm = "AM";
                if (predictedDateEndCal.get(Calendar.HOUR_OF_DAY) > 11) {
                    end_am_pm = "PM";
                }

                gath_date_after_fix.setText(CenesUtils.MMMdd.format(Long.valueOf(event.getStartTime())) + " at " + CenesUtils.hhmm.format(Long.valueOf(event.getStartTime())) + " " + am_pm + " to " + CenesUtils.hhmm.format(Long.valueOf(event.getEndTime())) + " " + end_am_pm);
                gath_date_after_fix.setVisibility(View.VISIBLE);

                populateCreateGatheringObj(loggedInUser, new JSONObject(new Gson().toJson(event)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.crt_gath_cls_btn:
                    //startActivity(new Intent(CreateGatheringActivity.this,GatheringsActivity.class));
                    //setResult(RESULT_CANCELED);
                    isPredictiveOn = false;
                    //finish();
                    //getActivity().onBackPressed();

                    ((CenesBaseActivity)getActivity()).fragmentManager.popBackStack();

                    break;
                case R.id.gath_event_image:
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        browseIntent.setType("image/*");

                        startActivityForResult(browseIntent, 100);
                    }
                    break;
                case R.id.gath_select_datetime_btn:
                    v.setVisibility(View.GONE);
                    RelativeLayout rl1 = (RelativeLayout) fragmentView.findViewById(R.id.gath_select_datetime_mcv_section);
                    if (rl1.getVisibility() == View.GONE) {
                        rl1.setVisibility(View.VISIBLE);

                        Calendar today = Calendar.getInstance();
                        today.setTime(new Date());
                        materialCalendarView.setSelectedDate(today);
                        materialCalendarView.addDecorator(new OneDayDecorator(new CalendarDay(today), getActivity(), R.drawable.calendar_selector_orange));
                    } else {
                        rl1.setVisibility(View.GONE);
                    }
                    break;
                case R.id.gath_invite_frnds_btn:
                    startActivityForResult(new Intent(getActivity(), SearchFriendActivity.class), SEARCH_FRIEND_RESULT_CODE);
                    //fragmentManager = getActivity().getSupportFragmentManager();
                    //replaceFragment(new FriendsFragment(),"friendsFragment");
                    break;
                case R.id.save_gathering_btn:
                    requestForGatheringSummary = true;
                    Boolean isValid = validateGatheringForm();
                    if (isValid) {
                        User user = userManager.getUser();
                        JSONObject createEventObj = new JSONObject();

                        if (eventImageFile == null) {
                            try {
                                Log.e("GatheringDate Start", predictedDateStartCal.getTime().toString());
                                Log.e("GatheringDate End", predictedDateEndCal.getTime().toString());
                                //eventImageFile = new File(new URI("drawable/party_image");
                                if (locationPhotoUrl.equals("")) {
                                    createEventObj = populateCreateGatheringObj(user, null);
                                } else {
                                    createEventObj = populateCreateGatheringObj(user, new JSONObject().put("eventPicture", locationPhotoUrl));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("GatheringObj", createEventObj.toString());
                        } else {
                            //new UploadImageTask().execute();
                        }
                    }
                    break;
                case R.id.gath_search_location_button:
                    startActivityForResult(new Intent(getActivity(), SearchLocationActivity.class), SEACRH_LOCATION_RESULT_CODE);
                    break;
                case R.id.start_time_picker_label:
                    int hour = predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY);
                    int minute = predictedDateStartCal.get(predictedDateStartCal.MINUTE);
                    TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), startTimePickerLisener, hour, minute, false);
                    startTimePickerDialog.show();
                    break;
                case R.id.end_time_picker_label:
                    int endHour = predictedDateEndCal.get(predictedDateEndCal.HOUR_OF_DAY);
                    int endMinute = predictedDateEndCal.get(predictedDateEndCal.MINUTE);
                    TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), endTimePickerLisener, endHour, endMinute, false);
                    endTimePickerDialog.show();
                    break;
                case R.id.gath_date_after_fix:
                    v.setVisibility(View.GONE);
                    try {
                        gathSelectDatetimeBtn.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RelativeLayout rl3 = (RelativeLayout) fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block);
                    if (rl3.getVisibility() == View.GONE) {
                        rl3.setVisibility(View.VISIBLE);
                    } else {
                        rl3.setVisibility(View.GONE);
                    }
                    break;
                case R.id.edit_gathering_btn:
                    isEditMode = true;
                    requestForGatheringSummary = false;
                    gatheringTitle.setText("Edit Gathering");
                    editGatheringSummaryBtn.setVisibility(View.GONE);
                    ivEventShareIcon.setVisibility(View.GONE);
                    saveGatheringButton.setVisibility(View.VISIBLE);
                    fragmentView.findViewById(R.id.gath_delete_btn).setVisibility(View.VISIBLE);
                    gatheringTitle.setText("Edit Gathering");
                    enableComponents();
                    break;
                case R.id.iv_event_share_icon:
                    shareEventLink();
                    break;
                case R.id.gath_delete_btn:
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete Gathering");
                    alert.setMessage("Do you really want to delete?");
                    alert.setPositiveButton("Move", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                new DeleteGatheringTask().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    break;

                case R.id.preview_gathering_btn:
                    JSONObject createEventObj = new JSONObject();
                    Boolean isValidGath = validateGatheringForm();
                    if (isValidGath) {
                        User user = userManager.getUser();
                        /*if (parentEvent.getEventImageURI() == null) {
                         *//*try {
                                if (parentEvent.getEventPicture() == null || parentEvent.getEventPicture().equals("")) {
                                    populateCreateGatheringObj(user, null);
                                } else {
                                    populateCreateGatheringObj(user, new JSONObject().put("eventPicture", locationPhotoUrl));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*//*
                            Log.e("GatheringObj", createEventObj.toString());
                        } else {
                            createEventObj = populateCreateGatheringObj(user, null);
                            try {
                                createEventObj.put("eventImageURI",eventImageFile.getPath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
*/
                        populateCreateGatheringObj(user, null);
                        event.setStartTime(predictedDateStartCal.getTimeInMillis()+"");
                        event.setEndTime(predictedDateEndCal.getTimeInMillis()+"");

                        Bundle bundle = new Bundle();
                        bundle.putString("eventJson", new Gson().toJson(event));
                        GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                        gatheringPreviewFragment.setArguments(bundle);
                        ((CenesBaseActivity) getActivity()).replaceFragment(gatheringPreviewFragment, GatheringPreviewFragment.TAG);

                        // user wants to go from B to C
                        /*getFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_container, gatheringPreviewFragment).hide(CreateGatheringFragment.this)
                                .commit();*/
                    }

                    break;

                case R.id.iv_show_time_match_info:

                    if (fragmentView.findViewById(R.id.rv_time_match_info_tip).getVisibility() == View.VISIBLE) {
                        fragmentView.findViewById(R.id.rv_time_match_info_tip).setVisibility(View.GONE);
                    } else {
                        fragmentView.findViewById(R.id.rv_time_match_info_tip).setVisibility(View.VISIBLE);
                    }
                    break;

                /*case R.id.save_edited_gathering_btn:
                    isEditMode = false;
                    requestForGatheringSummary = true;
                    isValid = validateGatheringForm();
                    if (isValid) {
                        User user = userManager.getUser();
                        if (eventImageFile == null) {
                            JSONObject createEventObj = new JSONObject();
                            try {
                                Log.e("GatheringDate Start", predictedDateStartCal.getTime().toString());
                                Log.e("GatheringDate End", predictedDateEndCal.getTime().toString());
                                //eventImageFile = new File(new URI("drawable/party_image");
                                createEventObj = populateCreateGatheringObj(user, null);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.e("GatheringObj", createEventObj.toString());
                            new CreateGatheringTask().execute(createEventObj);
                        } else {
                            new UploadImageTask().execute();
                        }

                        editGatheringSummaryBtn.setVisibility(View.VISIBLE);
                        saveGatheringButton.setVisibility(View.GONE);
                        fragmentView.findViewById(R.id.gath_delete_btn).setVisibility(View.GONE);
                    }
                    break;*/

            }
        }
    };

    TimePickerDialog.OnTimeSetListener startTimePickerLisener = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            //Setting time to an interval of 5 when creating the gatheirng
            //minute = minute + (5 - (minute % 5));
            //minute = minute + (5 - (((minute % 5) != 0) ? (minute % 5) : 5));
            if (minute % 5 != 0) {
                minute = minute + (5 - (minute % 5));
            }
            if (minute == 60) {
                hourOfDay = hourOfDay + 1;
                minute = 0;
            }

            predictedDateStartCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            predictedDateStartCal.set(Calendar.MINUTE, minute);
            startTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateStartCal.getTime()));
            Log.e("Start Date : ", predictedDateStartCal.getTime().toString());

            //Setting End Time to 1Hour Delay of Start Time by Default.
            //if (predictedDateEndCal.getTimeInMillis() <= predictedDateStartCal.getTimeInMillis()) {
                predictedDateEndCal.set(Calendar.DAY_OF_MONTH, predictedDateStartCal.get(Calendar.DAY_OF_MONTH));
                predictedDateEndCal.set(Calendar.HOUR_OF_DAY, predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY));
                predictedDateEndCal.set(Calendar.MINUTE, predictedDateStartCal.get(predictedDateStartCal.MINUTE));
                predictedDateEndCal.add(Calendar.MINUTE, 60);
                endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()));
                Log.e("End Date : ", predictedDateEndCal.getTime().toString());
            //}

            showPredictions();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimePickerLisener = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            //Setting the time to interval of 5 minutes
            //minute = minute + (5 - (((minute % 5) != 0) ? (minute % 5) : 5));
            if (minute % 5 != 0) {
                minute = minute + (5 - (minute % 5));
            }
            if (minute == 60) {
                hourOfDay = hourOfDay + 1;
                minute = 0;
            }

            predictedDateEndCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            predictedDateEndCal.set(Calendar.MINUTE, minute);
            endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()));
            Log.e("End Date : ", predictedDateEndCal.getTime().toString());

            if (predictedDateEndCal.getTimeInMillis() <= predictedDateStartCal.getTimeInMillis()) {
                //Toast.makeText(cenesApplication, "End Time should be greater than Start Time", Toast.LENGTH_SHORT).show();
                //return;
                predictedDateEndCal.add(Calendar.DAY_OF_MONTH, 1);
                Log.e("End Date After Adding: ",predictedDateEndCal.getTime().toString());
            }
            endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()));
            Log.e("End Date : ", predictedDateEndCal.getTime().toString());

            showPredictions();
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
                startActivityForResult(browseIntent, 100);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
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

                ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 1280, 512);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            try {
                String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                eventImageFile = new File(filePath);
                System.out.println("File Path String : " + filePath);
                System.out.println("File Path : " + eventImageFile.getPath());

                event.setEventImageURI(eventImageFile.getPath());
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



                gathEventImage.setImageBitmap(ImageUtils.getRotatedBitmap(imageBitmap, filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_OK) {


            if (data.hasExtra("selection")) {
                if (data.getStringExtra("selection").equals("list")) {
                    //placeId = data.getStringExtra("placeId");
                    event.setPlaceId(data.getStringExtra("placeId"));

                    String requiredValue = data.getStringExtra("title");
                    event.setLocation(requiredValue);
                    gathSearchLocationButton.setText(requiredValue.toString());

                    new FetchLatLngTask().execute(data.getStringExtra("placeId"));
                } else if (data.getStringExtra("selection").equals("horizontalScroll")) {
                    Location locationObj = new Gson().fromJson(data.getStringExtra("locationObj"), Location.class);
                    event.setPlaceId(locationObj.getPlaceId());
                    String title = locationObj.getLocation();
                    System.out.println("Location Title : "+title);
                    event.setLocation(title);
                    gathSearchLocationButton.setText(title);

                    /*parentEvent.setLocation(locationObj.getLocation());
                    parentEvent.setLongitude(locationObj.getLongitude());
                    parentEvent.setPlaceId(locationObj.getPlaceId());
                    parentEvent.setPlaceId(locationObj.getPlaceId());

                    Glide.with(context).load(photo).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(gathEventImage);*/
                    new FetchLatLngTask().execute(locationObj.getPlaceId());
                } else if (data.getStringExtra("selection").equals("done")) {
                    String title = data.getStringExtra("title");
                    gathSearchLocationButton.setText(title);
                    event.setLocation(title);

                }
            }
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Location Cancelled");
        } else if (requestCode == SEARCH_FRIEND_RESULT_CODE && resultCode == Activity.RESULT_OK) {

            String selectedFriendsJsonArrayStr = data.getExtras().getString("selectedFriendJsonArray");
            try {
                JSONArray selectedFriendsJsonArray = new JSONArray(selectedFriendsJsonArrayStr);
                for (int i=0; i< selectedFriendsJsonArray.length(); i++) {
                    JSONObject selectedFriend = selectedFriendsJsonArray.getJSONObject(i);

                    boolean isUserExists = false;
                    for (EventMember alreadyMember: inviteFriendsImageList) {

                        if (alreadyMember.getUserContactId() == selectedFriend.getInt("userContactId")) {
                            isUserExists = true;
                            break;
                        }
                    }
                    if (isUserExists) {
                     continue;
                    }

                    EventMember invFrnMap = new EventMember();
                    invFrnMap.setUserContactId(selectedFriend.getInt("userContactId"));
                    invFrnMap.setName(selectedFriend.getString("name"));
                    invFrnMap.setPhone(selectedFriend.getString("phone"));

                    String userPhoto = null;
                    if (selectedFriend.getString("user") != "null")  {

                        Gson gson = new Gson();
                        invFrnMap.setUser(gson.fromJson(selectedFriend.getString("user"), User.class));

                        JSONObject userObj = selectedFriend.getJSONObject("user");
                        if (userObj.has("photo") && !CenesUtils.isEmpty(userObj.getString("photo"))) {
                            userPhoto = userObj.getString("photo");
                        }
                    }

                    invFrnMap.setPicture(userPhoto);
                    invFrnMap.setCenesMember(selectedFriend.getString("cenesMember"));
                    if (selectedFriend.getString("cenesMember").equals("yes")) {
                        invFrnMap.setUserId(selectedFriend.getLong("friendId"));
                    }
                    inviteFriendsImageList.add(invFrnMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (isPredictiveOn) {
                showPredictions();
            }

            if (inviteFriendsImageList.size() > 0) {
                recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                recyclerView.setVisibility(View.VISIBLE);

                ArrayList<EventMember> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mFriendsAdapter);
            }
        } else if (requestCode == GATHERING_SUMMARY_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK);
            isPredictiveOn = false;
            getActivity().finish();
        }
    }

    private RecyclerView recyclerView;
    private FriendsAdapter mFriendsAdapter;

    public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {

        private ArrayList<EventMember> jsonObjectArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RoundedImageView ivFriend;
            public RelativeLayout rvNonCenesMemImg;
            TextView tvName;
            RelativeLayout container;
            ImageButton ibDelete;

            public MyViewHolder(View view) {
                super(view);
                ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
                tvName = (TextView) view.findViewById(R.id.tv_friend_name);
                container = (RelativeLayout) view.findViewById(R.id.container);
                ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
                rvNonCenesMemImg = (RelativeLayout) view.findViewById(R.id.rv_non_cenes_mem_img);
            }
        }

        public FriendsAdapter(ArrayList<EventMember> jsonObjectArrayList) {
            this.jsonObjectArrayList = jsonObjectArrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gathering_friend_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final FriendsAdapter.MyViewHolder holder, final int position) {
            try {
                final EventMember invFrn = jsonObjectArrayList.get(position);
                holder.tvName.setText(invFrn.getName());
                holder.ivFriend.setImageResource(R.drawable.cenes_user_no_image);


                if ((invFrn.isCenesMember() != null && invFrn.isCenesMember().equals("no")) || invFrn.getUser() == null) {

                    holder.rvNonCenesMemImg.setVisibility(View.VISIBLE);
                    holder.ivFriend.setVisibility(View.GONE);

                    String imageName = "";
                    String[] titleArr = invFrn.getName().split(" ");
                    int i=0;
                    for (String str: titleArr) {
                        if (i > 1) {
                            break;
                        }
                        imageName += str.substring(0,1).toUpperCase();
                        i++;
                    }

                    TextView circleText = new TextView(getActivity());
                    LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(CenesUtils.dpToPx(40), CenesUtils.dpToPx(40));
                    circleText.setLayoutParams(imageViewParams);
                    circleText.setText(imageName);
                    circleText.setGravity(Gravity.CENTER);
                    circleText.setTextColor(getActivity().getResources().getColor(R.color.white));
                    circleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    circleText.setBackground(getActivity().getResources().getDrawable(R.drawable.xml_circle_noncenes_grey));

                    holder.rvNonCenesMemImg.removeAllViews();
                    holder.rvNonCenesMemImg.addView(circleText);

                } else {

                    holder.rvNonCenesMemImg.setVisibility(View.GONE);
                    holder.ivFriend.setVisibility(View.VISIBLE);

                    //if (invFrn.getPicture() != null && invFrn.getPicture() != "null") {
                    try {
                        Glide.with(getActivity()).load(invFrn.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.cenes_user_no_image)).into(holder.ivFriend);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //} else {
                    //    holder.ivFriend.setImageResource(R.drawable.cenes_user_no_image);
                   // }
                }

                holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (holder.ibDelete.getVisibility() == View.VISIBLE) {
                            holder.ibDelete.setVisibility(View.GONE);
                        } else {
                            holder.ibDelete.setVisibility(View.VISIBLE);
                        }
                        return false;
                    }
                });
                holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inviteFriendsImageList.remove(invFrn);
                        jsonObjectArrayList.remove(position);
                        recyclerView.removeViewAt(position);
                        mFriendsAdapter.notifyItemRemoved(position);
                        mFriendsAdapter.notifyItemRangeChanged(position, jsonObjectArrayList.size());
                        if (isPredictiveOn) {
                            showPredictions();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonObjectArrayList.size();
        }
    }

    public Boolean validateGatheringForm() {

        Boolean isValid = true;
        if (validationManager.isFieldBlank(gathEventTitleEditView.getText().toString())) {
            alertManager.getAlert(context, "Please Enter Gathering Title", "Info", null, false, "OK");
            isValid = false;
        } else if (predictedDateStartCal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(cenesApplication, "Start Time should be greater than Current Time", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if (predictedDateEndCal.getTimeInMillis() <= predictedDateStartCal.getTimeInMillis()) {
            Toast.makeText(cenesApplication, "End Time should be greater than Start Time", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    class FetchLatLngTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String keyword = strings[0];
            //locationPhotoUrl = "";
            try {
                String queryStr = "&placeid=" + URLEncoder.encode(keyword);
                JSONObject job = apiManager.locationLatLng(queryStr);
                System.out.println("Selected Location Object : "+job.toString());

                JSONObject locationObj = job.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                //latitude = locationObj.getString("lat");
                //longitude = locationObj.getString("lng");

                //If user did not upload image then set this as Event Picture Url
                if (!isUserUploadedImage) {
                    if (!job.getJSONObject("result").isNull("photos") && job.getJSONObject("result").getJSONArray("photos").length() > 0) {
                        String largelocationPhotoUrl = GOOGLE_PLACE_LARGE_PHOTOS_API + job.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                        String thumbNailLocationPhotoUrl = GOOGLE_PLACE_THUMBNAIL_PHOTOS_API + job.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");

                        event.setEventPicture(largelocationPhotoUrl);
                        event.setThumbnail(thumbNailLocationPhotoUrl);
                    }
                }
                //parentEvent.setLocation(job.getJSONObject("result").getString("formatted_address"));
                event.setLatitude(locationObj.getString("lat"));
                event.setLongitude(locationObj.getString("lng"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (eventImageFile == null) {
                if (event.getEventPicture() != null) {
                    Glide.with(context).load(event.getEventPicture()).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(gathEventImage);
                } else {
                    if (!isUserUploadedImage) {
                        gathEventImage.setImageDrawable(context.getResources().getDrawable(R.drawable.gath_upload_img));
                    }
                }
            }
        }
    }

    String latitude = "";
    String longitude = "";
    String locationPhotoUrl = "";
    String oldImageUrl = "";
    public String GOOGLE_PLACE_LARGE_PHOTOS_API = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58&maxwidth=1200&photoreference=";
    public String GOOGLE_PLACE_THUMBNAIL_PHOTOS_API = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58&maxwidth=200&photoreference=";

    public boolean isUserUploadedImage;

    public void initializeComponents() {
        eventImageFile = null;
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        deviceManager = coreManager.getDeviceManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        validationManager = coreManager.getValidatioManager();
        alertManager = coreManager.getAlertManager();

        gathEventImage = (ImageView) fragmentView.findViewById(R.id.gath_event_image);

        gathEventTitleEditView = (EditText) fragmentView.findViewById(R.id.gath_event_title_et);
        gathSelectDatetimeBtn = (TextView) fragmentView.findViewById(R.id.gath_select_datetime_btn);
        //gathOnselectDatetimeBanner = (LinearLayout) findViewById(R.id.gath_onselect_datetime_banner);
        gathInviteFrndsBtn = (TextView) fragmentView.findViewById(R.id.gath_invite_frnds_btn);
        gathSearchLocationButton = (TextView) fragmentView.findViewById(R.id.gath_search_location_button);
        predictiveCalSwitch = (Switch) fragmentView.findViewById(R.id.predictive_cal_switch);
        saveGatheringButton = (TextView) fragmentView.findViewById(R.id.save_gathering_btn);
        startTimePickerLabel = (TextView) fragmentView.findViewById(R.id.start_time_picker_label);
        endTimePickerLabel = (TextView) fragmentView.findViewById(R.id.end_time_picker_label);
        gatheringDescription = (EditText) fragmentView.findViewById(R.id.gath_desc);
        gath_date_after_fix = (TextView) fragmentView.findViewById(R.id.gath_date_after_fix);

        editGatheringSummaryBtn = (TextView) fragmentView.findViewById(R.id.edit_gathering_btn);
        previewGatheringBtn = (TextView) fragmentView.findViewById(R.id.preview_gathering_btn);
        ivEventShareIcon = (ImageView) fragmentView.findViewById(R.id.iv_event_share_icon);
        ivShowTimeMatchInfo = (ImageView) fragmentView.findViewById(R.id.iv_show_time_match_info);

        gatheringTitle = (TextView) fragmentView.findViewById(R.id.cenes_title);
        //backGatheringSumamaryBtn = (ImageView) fragmentView.findViewById(R.id.back_gath_summary_btn);
        gatheringDeletButton = (TextView) fragmentView.findViewById(R.id.gath_delete_btn);
        eventTime = (TextView) fragmentView.findViewById(R.id.event_time);
        event_time_am_pm = (TextView) fragmentView.findViewById(R.id.event_time_am_pm);

        isEditMode = false;

        materialCalendarView = (MaterialCalendarView) fragmentView.findViewById(R.id.material_calendar_view);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                /*if (!isEditMode && eventId != null) {
                    return;
                }*/

                try {
                    Calendar startDateCalendar = Calendar.getInstance();
                    startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    startDateCalendar.set(Calendar.MINUTE, 0);
                    startDateCalendar.set(Calendar.SECOND, 0);
                    startDateCalendar.set(Calendar.MILLISECOND, 0);

                    Calendar endDateCalendar = Calendar.getInstance();
                    endDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    endDateCalendar.set(Calendar.MINUTE, 0);
                    endDateCalendar.set(Calendar.SECOND, 0);
                    endDateCalendar.set(Calendar.MILLISECOND, 0);
                    endDateCalendar.add(Calendar.MONTH, 12);

                    Long startTime = startDateCalendar.getTimeInMillis();
                    Long endTime = endDateCalendar.getTimeInMillis();
                    Long selectedTime = date.getCalendar().getTimeInMillis();

                    if (selectedTime >= startTime && selectedTime <= endTime) {
                        if (!isPredictiveOn) {
                            materialCalendarView.removeDecorator(mOneDayDecorator);
                            mOneDayDecorator = new OneDayDecorator(date, getActivity(), R.drawable.calendar_selector_red);
                            materialCalendarView.addDecorator(mOneDayDecorator);
                        }

                        boolean isMidnightEvent = false;
                        if (predictedDateEndCal.get(Calendar.DAY_OF_MONTH) > predictedDateStartCal.get(Calendar.DAY_OF_MONTH)) {
                            isMidnightEvent = true;
                        }

                        predictedDateStartCal.set(Calendar.YEAR, date.getYear());
                        predictedDateStartCal.set(Calendar.MONTH, date.getMonth());
                        predictedDateStartCal.set(Calendar.DAY_OF_MONTH, date.getDay());
                        predictedDateStartCal.set(Calendar.SECOND, 0);
                        predictedDateStartCal.set(Calendar.MILLISECOND, 0);

                        predictedDateEndCal.set(Calendar.YEAR, date.getYear());
                        predictedDateEndCal.set(Calendar.MONTH, date.getMonth());

                        if (isMidnightEvent) {
                            predictedDateEndCal.set(Calendar.DAY_OF_MONTH, date.getDay() + 1);
                        } else {
                            predictedDateEndCal.set(Calendar.DAY_OF_MONTH, date.getDay());
                        }
                        predictedDateEndCal.set(Calendar.SECOND, 0);
                        predictedDateEndCal.set(Calendar.MILLISECOND, 0);

                        fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block).setVisibility(View.GONE);

                        String am_pm = "AM";
                        if (predictedDateStartCal.get(Calendar.HOUR_OF_DAY) > 11) {
                            am_pm = "PM";
                        }

                        String end_am_pm = "AM";
                        if (predictedDateEndCal.get(Calendar.HOUR_OF_DAY) > 11) {
                            end_am_pm = "PM";
                        }
                        gath_date_after_fix.setText(CenesUtils.MMMdd.format(predictedDateStartCal.getTime()) + " at " + CenesUtils.hhmm.format(predictedDateStartCal.getTime()) + " " + am_pm + " to " + CenesUtils.hhmm.format(predictedDateEndCal.getTime()) + " " + end_am_pm);
                        gath_date_after_fix.setVisibility(View.VISIBLE);

                        String predictiveStatus = "ON";
                        if (!isPredictiveOn) {
                            predictiveStatus = "OFF";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        context = this.getContext();
        inviteFriendsImageList = new HashSet<>();
    }

    OneDayDecorator mOneDayDecorator;

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                isPredictiveOn = true;
                event.setPredictiveOn(true);
                showPredictions();
            } else {
                isPredictiveOn = false;
                event.setPredictiveOn(false);
                Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(currentMonth);
                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getContext(), R.drawable.mcv_white_color, drawableDates, false, false);
                materialCalendarView.addDecorator(calBgDecorator);
            }

        }
    };

    public void showPredictions() {
        if (isPredictiveOn) {
            try {
                Long gathStartTimeMilliseconds = predictedDateStartCal.getTimeInMillis();
                Long gathEndTimeMilliseconds = predictedDateEndCal.getTimeInMillis();

                JSONObject job = new JSONObject();
                job.put("startTimeMilliseconds", gathStartTimeMilliseconds);
                job.put("endTimeMilliseconds", gathEndTimeMilliseconds);
                new PredictiveCalendarTask().execute(job);

                //materialCalendarView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Message Receiver..................");

            if (intent != null) {
                CalendarDay currentMonth = (CalendarDay) intent.getExtras().get("CurrentMonth");
                CreateGatheringFragment.this.currentMonth.set(Calendar.MONTH, currentMonth.getMonth());
                CreateGatheringFragment.this.currentMonth.set(Calendar.YEAR, currentMonth.getYear());

//                enableDates = GatheringService.getEnableDates(currentMonth);
//                materialCalendarView.addDecorator(new DayEnableDecorator(enableDates));

                if (isPredictiveOn) {
                    predictedDateStartCal.set(Calendar.MONTH, currentMonth.getMonth());
                    predictedDateEndCal.set(Calendar.MONTH, currentMonth.getMonth());

                    predictedDateStartCal.set(Calendar.YEAR, currentMonth.getYear());
                    predictedDateEndCal.set(Calendar.YEAR, currentMonth.getYear());
                    JSONObject job = new JSONObject();
                    try {
                        job.put("startTimeMilliseconds", predictedDateStartCal.getTimeInMillis());
                        job.put("endTimeMilliseconds", predictedDateEndCal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new PredictiveCalendarTask().execute(job);
                } else {
                    if (((CenesBaseActivity)getActivity()) != null && ((CenesBaseActivity)getActivity()).parentEvent == null) {
                        Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(CreateGatheringFragment.this.currentMonth);
                        BackgroundDecorator calBgDecorator = new BackgroundDecorator(context, R.drawable.mcv_white_color, drawableDates, false, false);
                        materialCalendarView.addDecorator(calBgDecorator);
                    }
                }
            }
        }
    };

    public void addClickListnersToComponents() {
        gathEventImage.setOnClickListener(onClickListener);
        gathSelectDatetimeBtn.setOnClickListener(onClickListener);
        //gathOnselectDatetimeBanner.setOnClickListener(onClickListener);
        gathInviteFrndsBtn.setOnClickListener(onClickListener);
        saveGatheringButton.setOnClickListener(onClickListener);
        gathSearchLocationButton.setOnClickListener(onClickListener);
        predictiveCalSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        //startDatePickerLabel.setOnClickListener(onClickListener);
        //endDatePickerLabel.setOnClickListener(onClickListener);
        //startTimePickerLabel.setOnClickListener(onClickListener);
        //endTimePickerLabel.setOnClickListener(onClickListener);
        startTimePickerLabel.setOnClickListener(onClickListener);
        endTimePickerLabel.setOnClickListener(onClickListener);
        gath_date_after_fix.setOnClickListener(onClickListener);
        ivShowTimeMatchInfo.setOnClickListener(onClickListener);

        ivEventShareIcon.setOnClickListener(onClickListener);
        saveGatheringButton.setOnClickListener(onClickListener);
        gatheringDeletButton.setOnClickListener(onClickListener);
        previewGatheringBtn.setOnClickListener(onClickListener);
        //backGatheringSumamaryBtn.setOnClickListener(onClickListener);

        gatheringTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

    }

    public void shareEventLink() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, CenesConstants.webDomainEventUrl+event.getKey());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    class PredictiveCalendarTask extends AsyncTask<JSONObject, Object, JSONArray> {
        ProgressDialog predictiveProcessing;

        @Override
        protected void onPreExecute() {
            if (getActivity() == null) {
                return;
            }
            predictiveProcessing = new ProgressDialog(getActivity());
            predictiveProcessing.setMessage("Loading...");
            predictiveProcessing.setIndeterminate(false);
            predictiveProcessing.setCancelable(false);
            predictiveProcessing.show();
        }

        @Override
        protected JSONArray doInBackground(JSONObject... jsonObjs) {

            JSONObject job = jsonObjs[0];

            Long startTime = 0l;
            Long endTime = 0l;
            try {
                startTime = job.getLong("startTimeMilliseconds");
                endTime = job.getLong("endTimeMilliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }

            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId() + "&start_time=" + startTime + "&end_time=" + endTime + "";

            try {
                if (inviteFriendsImageList.size() > 0) {
                    String friends = "";
                    for (EventMember friendMap : inviteFriendsImageList) {
                        if (friendMap.getUserId() == null || (friendMap.getUserId() != null && user.getUserId() == friendMap.getUserId())) {
                            continue;
                        }
                        friends += friendMap.getUserId() + ",";
                    }
                    if (friends.length() > 0) {
                        queryStr += "&friends=" + friends.substring(0, friends.length() - 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONArray predictiveArray = apiManager.predictiveCalendar(user, queryStr, getCenesActivity());
            return predictiveArray;
        }

        @Override
        protected void onPostExecute(JSONArray predictiveArray) {
            if (predictiveProcessing != null) {
                predictiveProcessing.dismiss();
                predictiveProcessing = null;
            }
            //materialCalendarView = (MaterialCalendarView) findViewById(R.id.material_calendar_view);

            predictiveClanedarAPIData = predictiveArray;

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            //materialCalendarView.setSelectedDate(today);
            //materialCalendarView.addDecorator(new OneDayDecorator(new CalendarDay(today), getActivity(), R.drawable.calendar_selector_orange));

            Map<String, Set<CalendarDay>> calMap = GatheringService.parsePredictiveData(predictiveClanedarAPIData);
            for (Map.Entry<String, Set<CalendarDay>> calEntrySet : calMap.entrySet()) {

                if (getActivity() == null) {
                    return;
                }
                Set<CalendarDay> colorSet = null;
                if (calEntrySet.getKey().equals("WHITE")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.time_match_predictive_cross, calEntrySet.getValue(), false, true);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("YELLOW")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_yellow_color, calEntrySet.getValue(), true, false);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("RED")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_red_color, calEntrySet.getValue(), true, false);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("PINK")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_ping_color, calEntrySet.getValue(), true, false);
                    materialCalendarView.addDecorator(calBgDecorator);                } else if (calEntrySet.getKey().equals("GREEN")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_green_color, calEntrySet.getValue(), true, false);
                    materialCalendarView.addDecorator(calBgDecorator);                }
            }
        }
    }

    class HolidayCalendarTask extends AsyncTask<String, Set, Map> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Map doInBackground(String... strings) {
            Map<String, Set<CalendarDay>> calendarHighlights = new HashMap<>();
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?user_id=" + user.getUserId();
            JSONObject response = apiManager.getUserHolidays(user, queryStr, getCenesActivity());
            try {
                if (response.getBoolean("success") && response.getJSONArray("data").length() > 0) {
                    JSONArray holidaysArray = response.getJSONArray("data");


                    for (int i = 0; i < holidaysArray.length(); i++) {


                        JSONObject holiday = holidaysArray.getJSONObject(i);

                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(holiday.getLong("startTime"));
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                        CalendarDay calendarDay = new CalendarDay(year, month, dayOfMonth);

                        if (calendarHighlights.containsKey(holiday.getString("scheduleAs"))) {
                            Set<CalendarDay> holidaysBackgroundDates = calendarHighlights.get(holiday.getString("scheduleAs"));
                            holidaysBackgroundDates.add(calendarDay);
                            calendarHighlights.put(holiday.getString("scheduleAs"), holidaysBackgroundDates);
                        } else {
                            Set<CalendarDay> holidaysBackgroundDates = new HashSet<>();
                            holidaysBackgroundDates.add(calendarDay);
                            calendarHighlights.put(holiday.getString("scheduleAs"), holidaysBackgroundDates);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return calendarHighlights;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);

            try {
                Map<String, Set<CalendarDay>> calendarHighlights = map;
                if (calendarHighlights.containsKey("MeTime")) {
                    materialCalendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_light_gray), calendarHighlights.get("MeTime")));
                }
                if (calendarHighlights.containsKey("Holiday")) {
                    materialCalendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.cenes_blue), calendarHighlights.get("Holiday")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DeleteGatheringTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog deleteGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deleteGathDialog = new ProgressDialog(context);
            deleteGathDialog.setMessage("Deleting..");
            deleteGathDialog.setIndeterminate(false);
            deleteGathDialog.setCanceledOnTouchOutside(false);
            deleteGathDialog.setCancelable(false);
            deleteGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?event_id=" + eventId;
            JSONObject response = apiManager.deleteEventById(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            deleteGathDialog.dismiss();
            deleteGathDialog = null;
            try {
                if (response.getBoolean("success")) {
                    Toast.makeText(context, "Gathering Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), HomeScreenActivity.class));
                    isPredictiveOn = false;
                    getActivity().finish();
                } else {
                    Toast.makeText(context, "Gathering Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class EventInfoTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog eventInfoDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            eventInfoDialog = new ProgressDialog(context);
            eventInfoDialog.setMessage("Loading");
            eventInfoDialog.setIndeterminate(false);
            eventInfoDialog.setCanceledOnTouchOutside(false);
            eventInfoDialog.setCancelable(false);
            eventInfoDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... eventIds) {
            Long evventId = eventIds[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject response = apiManager.getEventById(user, evventId, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject responseObj) {
            super.onPostExecute(responseObj);
            eventInfoDialog.dismiss();
            eventInfoDialog = null;
            try {
                //System.out.println(responseObj.toString());
                if (responseObj != null && responseObj.getBoolean("success")) {
                    JSONObject summaryObj = responseObj.getJSONObject("data");
                        enableComponents();

                    event = new Gson().fromJson(summaryObj.toString(), Event.class);
                    //disableComponents();
                    //ivCreateGatheringCloseButton.setVisibility(View.GONE);
                    saveGatheringButton.setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.gath_select_datetime_mcv_section).setVisibility(View.VISIBLE);

                    gath_date_after_fix.setVisibility(View.VISIBLE);

                    if (loggedInUser.getUserId() == event.getCreatedById()) {
                        //editGatheringSummaryBtn.setVisibility(View.VISIBLE);
                        //ivEventShareIcon.setVisibility(View.VISIBLE);
                    }
                    //backGatheringSumamaryBtn.setVisibility(View.VISIBLE);

                    gatheringTitle.setText("Gathering");

                    gathEventTitleEditView.setText(event.getTitle());

                    if (!CenesUtils.isEmpty(event.getScheduleAs()) && "Google".equals(event.getSource())) {
                        editGatheringSummaryBtn.setVisibility(View.GONE);
                    }

                    if (!CenesUtils.isEmpty(event.getEventPicture())) {
                        oldImageUrl = event.getEventPicture();
                        Glide.with(context).load(event.getEventPicture()).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(gathEventImage);
                        if (event.getEventPicture().contains(GOOGLE_PLACE_LARGE_PHOTOS_API)) {
                            isUserUploadedImage = false;
                        } else {
                            isUserUploadedImage = true;
                        }
                    } else {
                        oldImageUrl = "";
                        //gathEventImage.setImageDrawable(context.getResources().getDrawable(R.drawable.party_image));
                    }

                    if (!CenesUtils.isEmpty(event.getLocation())) {
                        gathSearchLocationButton.setText(event.getLocation());
                        gathSearchLocationButton.setHint("");
                    } else {
                        gathSearchLocationButton.setText("");
                        gathSearchLocationButton.setHint("Location");
                    }

                    if (!CenesUtils.isEmpty(event.getDescription())) {
                        gatheringDescription.setText(event.getDescription());
                        gatheringDescription.setHint("");
                    } else {
                        gatheringDescription.setText("");
                        gatheringDescription.setHint("Event Details");
                        gatheringDescription.setHintTextColor(Color.BLACK);
                    }

                    if (event.getEventMembers() != null && event.getEventMembers().size() > 0) {
                        inviteFriendsImageList = new HashSet<>();

                        List<EventMember> eventMembers = event.getEventMembers();

                        for (EventMember frndObj: eventMembers) {
                            inviteFriendsImageList.add(frndObj);
                        }

                        if (inviteFriendsImageList.size() > 0) {
                            recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                            recyclerView.setVisibility(View.VISIBLE);

                            ArrayList<EventMember> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                            mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mFriendsAdapter);
                        }
                    }

                    String predictiveCalStatus = "OFF";
                    if (event.getPredictiveOn()) {
                        predictiveCalStatus = "ON";
                        predictiveCalSwitch.setChecked(true);

                        JSONArray predictiveData = null;
                        if (event.getPredictiveData() != null) {
                            predictiveData = new JSONArray(event.getPredictiveData());
                        } else {
                            predictiveData = new JSONArray(getActivity().getIntent().getStringExtra("predictiveData"));
                        }
                        predictiveClanedarAPIData = predictiveData;
                        Map<String, Set<CalendarDay>> colorCalendarDayMap = GatheringService.parsePredictiveData(predictiveData);

                        //Map<String,Set<CalendarDay>> colorCalendarDayMap = (Map<String, Set<CalendarDay>>) summaryObj.getJSONObject("predictive_data");
                        for (Map.Entry<String, Set<CalendarDay>> calEntrySet : colorCalendarDayMap.entrySet()) {

                            Set<CalendarDay> colorSet = null;
                            if (calEntrySet.getKey().equals("WHITE")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.time_match_predictive_cross, calEntrySet.getValue(), false, true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("YELLOW")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_yellow_color, calEntrySet.getValue(), true, false);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("RED")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_red_color, calEntrySet.getValue(), true, false);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("PINK")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_ping_color, calEntrySet.getValue(), true, false);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("GREEN")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_green_color, calEntrySet.getValue(), true, false);
                                materialCalendarView.addDecorator(calBgDecorator);
                            }
                        }
                    }

                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(Long.valueOf(event.getStartTime()));
                    predictedDateStartCal.setTimeInMillis(Long.valueOf(event.getStartTime()));
                    materialCalendarView.setCurrentDate(predictedDateStartCal.getTime());

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTimeInMillis(Long.valueOf(event.getEndTime()));
                    predictedDateEndCal.setTimeInMillis(Long.valueOf(event.getEndTime()));

                    gath_date_after_fix.setText(CenesUtils.MMMdd.format(predictedDateStartCal.getTime()) + " at " + CenesUtils.hmmaa.format(startTime.getTime()) + " to " + CenesUtils.hmmaa.format(endTime.getTime()));

                    startTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateStartCal.getTime()));
                    endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()));

                    fragmentView.findViewById(R.id.suggested_date_time_label).setVisibility(View.VISIBLE);
                    eventTime.setVisibility(View.VISIBLE);
                    eventTime.setText(CenesUtils.MMMMddy.format(startTime.getTime()) + CenesUtils.getDateSuffix(startTime.get(Calendar.DAY_OF_MONTH)) + "," + startTime.get(Calendar.YEAR));

                    event_time_am_pm.setVisibility(View.VISIBLE);
                    event_time_am_pm.setText(CenesUtils.hmmaa.format(startTime.getTime()) + " to " + CenesUtils.hmmaa.format(endTime.getTime()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject populateCreateGatheringObj(User user, JSONObject eventPictureObj) {
        JSONObject createEventObj = new JSONObject();
        try {
            if (eventId != null) {
                createEventObj.put("eventId", eventId);
            }
            event.setTitle(gathEventTitleEditView.getText().toString());

            /*if (!gathSearchLocationButton.getText().toString().equals("Location")) {
                createEventObj.put("location", gathSearchLocationButton.getText().toString());
            }
            if (gathSearchLocationButton.getText().toString().trim().length() > 0) {
                createEventObj.put("latitude", latitude);
                createEventObj.put("longitude", longitude);
            }*/

            if (!CenesUtils.isEmpty(gatheringDescription.getText().toString())) {
                event.setDescription(gatheringDescription.getText().toString());
            }
            event.setCreatedById(Long.valueOf(user.getUserId()));
            event.setSource("Cenes");
            event.setTimezone(predictedDateStartCal.getTimeZone().getID());
            event.setScheduleAs("Gathering");

            //createEventObj.put("isPredictiveOn", isPredictiveOn);
            event.setPredictiveOn(isPredictiveOn);
            if (isPredictiveOn) {
                event.setPredictiveData(predictiveClanedarAPIData.toString());
                //createEventObj.put("predictiveData", predictiveClanedarAPIData.toString()); // getText() SHOULD NOT be static!!!
            }

            if (event.getEventImageURI() != null) {
                Uri eventImageUri = Uri.parse(event.getEventImageURI());
                gathEventImage.setImageURI(eventImageUri);

                eventImageFile = new File(event.getEventImageURI());

            } else if (event.getEventPicture() != null) {
                Glide.with(CreateGatheringFragment.this).load(event.getEventPicture()).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(gathEventImage);
            }

            gathSearchLocationButton.setText(event.getLocation());

            /*if (placeId != null) {
                createEventObj.put("placeId", placeId);
            }*/
            /*if (!isEditMode) {
                JSONObject userObj = new JSONObject();
                userObj.put("name", !CenesUtils.isEmpty(user.getName()) ? user.getName() : "");
                userObj.put("picture", user.getPicture());
                userObj.put("userId", user.getUserId());
                userObj.put("source", "Cenes");
                userObj.put("status", "Going");
                friendsArray.put(userObj);
            }*/

            /*if (inviteFriendsImageList.size() > 0) {
                for (Map<String, String> friend : inviteFriendsImageList) {


                    JSONObject frindObj = new JSONObject();
                    frindObj.put("name", friend.get("name"));
                    frindObj.put("picture", friend.get("photo"));
                    frindObj.put("phone", friend.get("phone"));
                    if (!friend.containsKey("userId")) {
                        frindObj.put("userContactId", friend.get("userContactId"));
                    } else {
                        frindObj.put("userId", friend.get("userId"));
                    }
                    frindObj.put("source", "Cenes");
                    if (user.getUserId() == Integer.valueOf(friend.get("userId"))) {
                        //frindObj.put("status", "Going");
                        continue;
                    }
                    //if (!CenesUtils.isEmpty(friend.get("status"))) {
                        frindObj.put("status", friend.get("status"));
                    //}

                    friendsArray.put(frindObj);
                }
            }*/

            Type listType = new TypeToken<List<EventMember>>() {}.getType();
            Gson gson = new Gson();
            String membersJson = gson.toJson(inviteFriendsImageList, listType);
            if (inviteFriendsImageList != null && inviteFriendsImageList.size() > 0) {
                List<EventMember> members = new ArrayList<>();
                for (EventMember eventMember: inviteFriendsImageList) {
                    members.add(eventMember);
                }
                event.setEventMembers(members);
            } else if ( event.getEventMembers() != null &&  event.getEventMembers().size() > 0) {

                inviteFriendsImageList = new HashSet<>();

                List<EventMember> eventMembers = event.getEventMembers();

                for (EventMember frndObj: eventMembers) {
                    inviteFriendsImageList.add(frndObj);
                }

                if (inviteFriendsImageList.size() > 0) {
                    recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                    recyclerView.setVisibility(View.VISIBLE);

                    ArrayList<EventMember> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                    mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mFriendsAdapter);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return createEventObj;
    }

    public void disableComponents() {
        gathEventTitleEditView.setEnabled(false);
        gathEventImage.setClickable(false);
        gathSearchLocationButton.setClickable(false);
        gatheringDescription.setEnabled(false);
        gathInviteFrndsBtn.setClickable(false);
        predictiveCalSwitch.setEnabled(false);
        materialCalendarView.setClickable(false);
        materialCalendarView.setPagingEnabled(false);
        //Disabling Calendar on Gathering View
        materialCalendarView.setEnabled(false);
        materialCalendarView.setPressed(false);
        //startDatePickerLabel.setClickable(false);
        //startTimePickerLabel.setClickable(false);
        startTimePickerLabel.setClickable(false);
        //endDatePickerLabel.setClickable(false);
        //endTimePickerLabel.setClickable(false);
        endTimePickerLabel.setClickable(false);
    }

    public void enableComponents() {
        gathEventTitleEditView.setEnabled(true);
        gathEventImage.setClickable(true);
        gathSearchLocationButton.setClickable(true);
        gatheringDescription.setEnabled(true);
        gathInviteFrndsBtn.setClickable(true);
        predictiveCalSwitch.setEnabled(true);
        materialCalendarView.setClickable(true);
        materialCalendarView.setPagingEnabled(true);
        //Disabling Calendar on Gathering View
        materialCalendarView.setEnabled(true);
        materialCalendarView.setPressed(true);
        //startDatePickerLabel.setClickable(true);
        //startTimePickerLabel.setClickable(true);
        startTimePickerLabel.setClickable(true);
        //endDatePickerLabel.setClickable(true);
        //endTimePickerLabel.setClickable(true);
        endTimePickerLabel.setClickable(true);
    }

    public void replaceFragment(Fragment fragment, String tag) {

        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            if (tag != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
                fragmentTransaction.addToBackStack(tag);
            } else {
                fragmentTransaction.replace(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "Exception replacing fragment: " + e.getMessage());
        }
    }

    public void clearFragmentsAndOpen(Fragment fragment) {
        getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(fragment, null);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
