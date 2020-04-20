package com.cenesbeta.fragment.gathering;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.AsyncTasks.LocationAsyncTask;
import com.cenesbeta.Manager.DeviceManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.activity.SearchLocationActivity;
import com.cenesbeta.adapter.FriendHorizontalScrollAdapter;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.Location;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.CreateGatheringDto;
import com.cenesbeta.dto.PredictiveData;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.friend.FriendListFragment;
import com.cenesbeta.materialcalendarview.CalendarDay;
import com.cenesbeta.materialcalendarview.MaterialCalendarView;
import com.cenesbeta.materialcalendarview.OnDateSelectedListener;
import com.cenesbeta.materialcalendarview.decorators.BackgroundDecorator;
import com.cenesbeta.service.GatheringService;
import com.cenesbeta.util.CenesEditText;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mandeep on 2/11/17.
 */

public class CreateGatheringFragment extends CenesFragment {

    public static String TAG = "CreateGatheringFragment";
    private int SEACRH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002,
            GATHERING_SUMMARY_RESULT_CODE = 1003, MESSAGE_FRAGMENT_CODE = 1004,
            CLICK_IMAGE_REQUEST_CODE = 1005, UPLOAD_IMAGE_REQUEST_CODE = 1006,
            UPLOAD_PERMISSION_CODE = 1007;

    private int CAMERA_PERMISSION_CODE = 2001;
    private View fragmentView;

    private Calendar currentMonth;

    private CenesEditText gathEventTitleEditView;
    private Switch predictiveCalSwitch;

    private TextView startTimePickerLabel, endTimePickerLabel;
    private ProgressBar progressBar;

    public ImageView ivAbandonEvent;
    private ImageView ivPredictiveInfo, gathInviteFrndsBtn, ivDateBarArrow;
    private TextView tvLocationLabel, tvGatheringMessage, tvCoverImageStatus, tvEventDate, tvChooseLibrary, tvTakePhoto, tvPhotoCancel;
    private RelativeLayout rlStartBar, rlEndBar, rlDateBar;
    private RelativeLayout rlHeader, gathSearchLocationButton, rlGatheringMessageBar, rlCoverImageBar, rlSelectedFriendsRecyclerView;
    private RelativeLayout rlPreviewInvitationButton, rlPhotoActionSheet;
    private ScrollView svGatheringScrollview;
    private LinearLayout llGatheringDateBars, llGatheringInfoBars;
    private LinearLayout llPredictiveCalCell, llPredictiveCalInfo;
    public static RecyclerView recyclerView;
    private FriendHorizontalScrollAdapter friendHorizontalScrollAdapter;
    private Uri cameraFileUri;
    private File eventImageFile;
    private String isTakeOrUpload = "Take";
    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private DeviceManager deviceManager;
    private Long eventId;
    public User loggedInUser;
    public Event event, eventTemp;
    public List<EventMember> membersSelected;
    public List<PredictiveData> predictiveDataList;
    public PredictiveData predictiveDataForDate;
    private CreateGatheringDto createGatheringDto;
    private MaterialCalendarView materialCalendarView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (fragmentView != null) {
            return fragmentView;
        }

        System.out.println("Return after location");
        View view = inflater.inflate(R.layout.fragment_gathering_create, container, false);

        fragmentView = view;

        initializeComponents();
        addClickListnersToComponents();

        ((CenesBaseActivity)getActivity()).hideFooter();

        //LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter("month_changed_intent"));

        loggedInUser = userManager.getUser();

        Calendar predictedDateStartCal = Calendar.getInstance();

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


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if ("list".equals(bundle.getString("dataFrom"))) {

                eventId = bundle.getLong("eventId");

                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                event = new Event();
            }
        } else {

            if (event == null) {

                event = new Event();

                Calendar predictedDateEndCal = Calendar.getInstance();
                predictedDateEndCal.set(Calendar.MINUTE, 60);

                event.setStartTime(predictedDateStartCal.getTimeInMillis());
                event.setEndTime(predictedDateEndCal.getTimeInMillis());

            } else {

                populateCreateGatheringObj();
            }

            populateFriendCollectionView();
        }

        materialCalendarView.sourceFragment = this;
        materialCalendarView.setCurrentDate(predictedDateStartCal);
        currentMonth = predictedDateStartCal;

        CalendarDay calDay = new CalendarDay();
        Set<CalendarDay> calDaySet = new HashSet<>();
        calDay = new CalendarDay(predictedDateStartCal.getTime());
        calDaySet.add(calDay);

        BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_orange_color, calDaySet, true, false);
        materialCalendarView.addDecorator(calBgDecorator);

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

        return view;
    }


    public void initializeComponents() {
        eventImageFile = null;
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        deviceManager = coreManager.getDeviceManager();

        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);

        gathEventTitleEditView = (CenesEditText) fragmentView.findViewById(R.id.gath_event_title_et);

        ivDateBarArrow = (ImageView) fragmentView.findViewById(R.id.iv_date_bar_arrow);
        gathInviteFrndsBtn = (ImageView) fragmentView.findViewById(R.id.gath_invite_frnds_btn);
        ivAbandonEvent = (ImageView) fragmentView.findViewById(R.id.iv_abandon_event);
        ivPredictiveInfo = (ImageView) fragmentView.findViewById(R.id.iv_predictive_info);
        tvGatheringMessage = (TextView) fragmentView.findViewById(R.id.tv_gathering_message);

        tvEventDate = (TextView) fragmentView.findViewById(R.id.tv_event_date);
        tvLocationLabel = (TextView) fragmentView.findViewById(R.id.tv_location_label);
        tvCoverImageStatus = (TextView) fragmentView.findViewById(R.id.tv_cover_image_status);
        tvChooseLibrary = (TextView) fragmentView.findViewById(R.id.tv_choose_library);
        tvTakePhoto = (TextView) fragmentView.findViewById(R.id.tv_take_photo);
        tvPhotoCancel = (TextView) fragmentView.findViewById(R.id.tv_photo_cancel);

        gathSearchLocationButton = (RelativeLayout) fragmentView.findViewById(R.id.gath_search_location_button);
        rlCoverImageBar = (RelativeLayout) fragmentView.findViewById(R.id.rl_cover_image_bar);
        rlGatheringMessageBar = (RelativeLayout) fragmentView.findViewById(R.id.rl_gath_msg_bar);
        rlStartBar = (RelativeLayout) fragmentView.findViewById(R.id.rl_start_bar);
        rlEndBar = (RelativeLayout) fragmentView.findViewById(R.id.rl_end_bar);
        rlDateBar =(RelativeLayout) fragmentView.findViewById(R.id.rl_date_bar);
        rlSelectedFriendsRecyclerView = (RelativeLayout) fragmentView.findViewById(R.id.rl_selected_friends_recycler_view);
        rlHeader = (RelativeLayout) fragmentView.findViewById(R.id.rl_header);
        rlPreviewInvitationButton = (RelativeLayout) fragmentView.findViewById(R.id.rl_preview_invitation_button);
        rlPhotoActionSheet = (RelativeLayout) fragmentView.findViewById(R.id.rl_photo_action_sheet);

        llGatheringDateBars = (LinearLayout) fragmentView.findViewById(R.id.ll_gathering_date_bars);
        llGatheringInfoBars = (LinearLayout) fragmentView.findViewById(R.id.ll_gathering_info_bars);
        llPredictiveCalCell = (LinearLayout) fragmentView.findViewById(R.id.ll_predictive_cal_cell);
        llPredictiveCalInfo = (LinearLayout) fragmentView.findViewById(R.id.ll_predictive_cal_info);

        predictiveCalSwitch = (Switch) fragmentView.findViewById(R.id.predictive_cal_switch);
        startTimePickerLabel = (TextView) fragmentView.findViewById(R.id.start_time_picker_label);
        endTimePickerLabel = (TextView) fragmentView.findViewById(R.id.end_time_picker_label);

        materialCalendarView = (MaterialCalendarView) fragmentView.findViewById(R.id.material_calendar_view);
        svGatheringScrollview = (ScrollView) fragmentView.findViewById(R.id.sv_gathering_scrollview);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);


        createGatheringDto = new CreateGatheringDto();
        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());
    }

    public void addClickListnersToComponents() {

        gathInviteFrndsBtn.setOnClickListener(onClickListener);
        ivAbandonEvent.setOnClickListener(onClickListener);
        ivPredictiveInfo.setOnClickListener(onClickListener);
        materialCalendarView.setOnDateChangedListener(onDateSelectedListener);
        gathEventTitleEditView.setOnFocusChangeListener(focusListener);
        gathEventTitleEditView.setOnEditorActionListener(oneditorListener);
        gathEventTitleEditView.setOnClickListener(onClickListener);

        gathSearchLocationButton.setOnClickListener(onClickListener);
        rlGatheringMessageBar.setOnClickListener(onClickListener);
        rlCoverImageBar.setOnClickListener(onClickListener);
        rlStartBar.setOnClickListener(onClickListener);
        rlEndBar.setOnClickListener(onClickListener);
        rlDateBar.setOnClickListener(onClickListener);
        rlPreviewInvitationButton.setOnClickListener(onClickListener);
        rlPhotoActionSheet.setOnClickListener(onClickListener);
        tvChooseLibrary.setOnClickListener(onClickListener);
        tvTakePhoto.setOnClickListener(onClickListener);
        tvPhotoCancel.setOnClickListener(onClickListener);
        predictiveCalSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        recyclerView.setOnTouchListener(layoutTouchListener);
        svGatheringScrollview.setOnTouchListener(layoutTouchListener);
    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){

                rlPreviewInvitationButton.setVisibility(View.GONE);

            } else {

                deviceManager.hideKeyBoard(gathEventTitleEditView, (CenesBaseActivity)getActivity());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (llPredictiveCalCell.getVisibility() == View.GONE) {
                                rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }, 200);

            }
        }
    };

    TextView.OnEditorActionListener oneditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // do your stuff here
                //rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            hideKeyboardAndClearFocus(gathEventTitleEditView);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }, 200);

            }
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ((CenesBaseActivity) getActivity()).hideFooter();
        System.out.println("On Resume Called..");
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.gath_event_title_et:

                   // rlPreviewInvitationButton.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rl_start_bar:

                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    Calendar predictedDateStartCal = Calendar.getInstance();
                    predictedDateStartCal.setTimeInMillis(event.getStartTime());
                    int hour = predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY);
                    int minute = predictedDateStartCal.get(predictedDateStartCal.MINUTE);
                    TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), startTimePickerLisener, hour, minute, false);
                    startTimePickerDialog.show();
                    break;

                case R.id.rl_end_bar:

                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    Calendar predictedDateEndCal = Calendar.getInstance();
                    predictedDateEndCal.setTimeInMillis(event.getEndTime());
                    int endHour = predictedDateEndCal.get(predictedDateEndCal.HOUR_OF_DAY);
                    int endMinute = predictedDateEndCal.get(predictedDateEndCal.MINUTE);
                    TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), endTimePickerLisener, endHour, endMinute, false);
                    endTimePickerDialog.show();
                    break;

                case R.id.rl_gath_msg_bar:

                    hideKeyboardAndClearFocus(gathEventTitleEditView);
                    GatheirngMessageFragment gatheirngMessageFragment = new GatheirngMessageFragment();
                    gatheirngMessageFragment.setTargetFragment(CreateGatheringFragment.this, MESSAGE_FRAGMENT_CODE);
                    gatheirngMessageFragment.message = event.getDescription();
                    ((CenesBaseActivity) getActivity()).replaceFragment( gatheirngMessageFragment, CreateGatheringFragment.TAG);
                    break;

                case R.id.rl_cover_image_bar:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    rlPreviewInvitationButton.setVisibility(View.GONE);
                    rlPhotoActionSheet.setVisibility(View.VISIBLE);

                    break;
                case R.id.tv_choose_library:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    isTakeOrUpload = "Upload";
                    chooseFromGalleryPressed();
                    rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                    break;

                case R.id.tv_take_photo:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    isTakeOrUpload = "Take";
                    checkCameraPermissiosn();
                    rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_photo_cancel:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                    rlPhotoActionSheet.setVisibility(View.GONE);
                    break;
                case R.id.iv_abandon_event:

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Abandon Event?")
                            .setMessage("If you decide to leave this page, all progress will be lost.")
                            .setCancelable(false)
                            .setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {



                                }
                            }).setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            membersSelected = new ArrayList<EventMember>();
                            List<EventMember> mebersToRemove = new ArrayList<>();

                            int loopSize = event.getEventMembers().size();
                            for (int i=0; i < loopSize; i++) {
                                EventMember eveMem = event.getEventMembers().get(i);
                                if (eveMem.getEventMemberId() != null) {
                                    mebersToRemove.add(event.getEventMembers().get(i));
                                }
                            }
                            event.setEventMembers(mebersToRemove);

                            if (event.getEventId() != null) {
                                event.setTitle(eventTemp.getTitle());
                                event.setStartTime(eventTemp.getStartTime());
                                event.setEndTime(eventTemp.getEndTime());
                                event.setDescription(eventTemp.getDescription());
                                event.setLocation(eventTemp.getLocation());
                                event.setEventPicture(eventTemp.getEventPicture());
                            }

                            ((CenesBaseActivity)getActivity()).replaceFragment(((CenesBaseActivity)getActivity()).homeFragmentV2, null);
                                    /*if (event.getEventId() != null && event.getEventId() != 0) {

                                        ((CenesBaseActivity)getActivity()).clearAllFragmentsInBackstack();
                                        ((CenesBaseActivity)getActivity()).replaceFragment(new HomeFragment(), null);

                                    } else {

                                        ((CenesBaseActivity)getActivity()).clearAllFragmentsInBackstack();
                                        ((CenesBaseActivity)getActivity()).replaceFragment(new GatheringsFragment(), null);

                                    }*/
                        }
                    }).show();
                    break;

                case R.id.rl_date_bar:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    if (llPredictiveCalCell.getVisibility() == View.GONE) {

                        llPredictiveCalCell.setVisibility(View.VISIBLE);
                        llGatheringInfoBars.setVisibility(View.GONE);
                        rlPreviewInvitationButton.setVisibility(View.GONE);
                        ivDateBarArrow.setImageResource(R.drawable.date_panel_down_arrow);

                    } else {

                        llPredictiveCalCell.setVisibility(View.GONE);
                        llGatheringInfoBars.setVisibility(View.VISIBLE);
                        if (!gathEventTitleEditView.hasFocus()) {
                            rlPreviewInvitationButton.setVisibility(View.VISIBLE);
                        }
                        ivDateBarArrow.setImageResource(R.drawable.date_panel_right_arrow);

                    }
                    break;

                case R.id.rl_preview_invitation_button:

                    if (validateCreateGatheringForm()) {

                        if (event.getEventMembers() == null) {
                            event.setEventMembers(new ArrayList<EventMember>());
                        }


                        List<EventMember> eventMemberList = new ArrayList<>();
                        for (EventMember eventMember: membersSelected) {

                            EventMember eventMemberToAdd = eventMember;

                            //If its first time when the event is abut to create
                            if (eventMember.getFriendId() != null && !eventMember.getFriendId().equals(loggedInUser.getUserId())) {

                                eventMemberToAdd.setUserId(eventMember.getFriendId());

                            } else if (eventMember.getEventMemberId() == null && eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId()) && eventMember.getUser() == null) {
                                //If its the editng time If its the editng time
                                eventMemberToAdd.setUserId(null);
                            }
                            eventMemberList.add(eventMemberToAdd);
                        }
                        event.setEventMembers(eventMemberList);
                        event.setCreatedById(loggedInUser.getUserId());
                        Log.v("Event Memebrs Size : ",event.getEventMembers().size()+"");
                        event.setTitle(gathEventTitleEditView.getText().toString());

                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                        try {
                            JSONObject props = new JSONObject();
                            props.put("Action","Create Gathering Begins");
                            props.put("Title",event.getTitle());
                            props.put("UserEmail",loggedInUser.getEmail());
                            props.put("UserName",loggedInUser.getName());
                            mixpanel.track("Gathering", props);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                        gatheringPreviewFragment.event = event;
                        ((CenesBaseActivity) getActivity()).replaceFragment( gatheringPreviewFragment, CreateGatheringFragment.TAG);

                    }

                    break;

                case R.id.iv_predictive_info:

                    if (llPredictiveCalInfo.getVisibility() == View.GONE) {

                        ((RelativeLayout)fragmentView.findViewById(R.id.rl_header)).setVisibility(View.GONE);
                        ((RelativeLayout)fragmentView.findViewById(R.id.rl_selected_friends_recycler_view)).setVisibility(View.GONE);
                        ((LinearLayout)fragmentView.findViewById(R.id.ll_gathering_date_bars)).setVisibility(View.GONE);
                        llPredictiveCalInfo.setVisibility(View.VISIBLE);
                        ivPredictiveInfo.setImageResource(R.drawable.time_match_icon_on);

                    } else {

                        llPredictiveCalInfo.setVisibility(View.GONE);

                        ((RelativeLayout)fragmentView.findViewById(R.id.rl_header)).setVisibility(View.VISIBLE);
                        ((RelativeLayout)fragmentView.findViewById(R.id.rl_selected_friends_recycler_view)).setVisibility(View.VISIBLE);
                        ((LinearLayout)fragmentView.findViewById(R.id.ll_gathering_date_bars)).setVisibility(View.VISIBLE);
                        ivPredictiveInfo.setImageResource(R.drawable.time_match_icon_off);

                    }
                    break;

                case R.id.gath_invite_frnds_btn:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);

                    FriendListFragment friendListFragment = new FriendListFragment();
                    friendListFragment.setTargetFragment(CreateGatheringFragment.this, SEARCH_FRIEND_RESULT_CODE);
                    friendListFragment.selectedEventMembers = membersSelected;
                    friendListFragment.isEditMode = true;
                    ((CenesBaseActivity) getActivity()).replaceFragment(friendListFragment, CreateGatheringFragment.TAG);

                    break;

                case R.id.gath_search_location_button:
                    hideKeyboardAndClearFocus(gathEventTitleEditView);
                    startActivityForResult(new Intent(getActivity(), SearchLocationActivity.class), SEACRH_LOCATION_RESULT_CODE);
                    break;
            }
        }
    };

            TimePickerDialog.OnTimeSetListener startTimePickerLisener = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            hideKeyboardAndClearFocus(gathEventTitleEditView);
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


            Calendar predictedDateStartCal = Calendar.getInstance();
            predictedDateStartCal.setTimeInMillis(event.getStartTime());
            predictedDateStartCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            predictedDateStartCal.set(Calendar.MINUTE, minute);

            startTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateStartCal.getTime()).toUpperCase());
            Log.e("Start Date : ", predictedDateStartCal.getTime().toString());
            event.setStartTime(predictedDateStartCal.getTimeInMillis());

            //Setting End Time to 1Hour Delay of Start Time by Default.
            if (!createGatheringDto.isEndTime()) {
                Calendar predictedDateEndCal = Calendar.getInstance();
                predictedDateEndCal.setTimeInMillis(predictedDateStartCal.getTimeInMillis());
                predictedDateEndCal.add(Calendar.MINUTE, 60);
                endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()).toUpperCase());
                Log.e("End Date : ", predictedDateEndCal.getTime().toString());
                event.setEndTime(predictedDateEndCal.getTimeInMillis());

            } else if (createGatheringDto.isEndTime()) {

                Calendar predictedDateEndCal = Calendar.getInstance();
                predictedDateEndCal.setTimeInMillis(event.getEndTime());
                predictedDateEndCal.add(Calendar.DAY_OF_MONTH, 1);
                endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()).toUpperCase());
                Log.e("End Date : ", predictedDateEndCal.getTime().toString());
                event.setEndTime(predictedDateEndCal.getTimeInMillis());
            }
            createGatheringDto.setStartTime(true);
            createGatheringDto.setEndTime(true);


            System.out.println("startTimePickerLisener Listener");
            showPredictions();
        }
    };

    TimePickerDialog.OnTimeSetListener endTimePickerLisener = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            hideKeyboardAndClearFocus(gathEventTitleEditView);

            //Setting the time to interval of 5 minutes
            //minute = minute + (5 - (((minute % 5) != 0) ? (minute % 5) : 5));
            if (minute % 5 != 0) {
                minute = minute + (5 - (minute % 5));
            }
            if (minute == 60) {
                hourOfDay = hourOfDay + 1;
                minute = 0;
            }

            Calendar predictedDateEndCal = Calendar.getInstance();
            predictedDateEndCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            predictedDateEndCal.set(Calendar.MINUTE, minute);
            endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()).toUpperCase());
            Log.e("End Date : ", predictedDateEndCal.getTime().toString());

            if (predictedDateEndCal.getTimeInMillis() <= event.getStartTime()) {
                //Toast.makeText(cenesApplication, "End Time should be greater than Start Time", Toast.LENGTH_SHORT).show();
                //return;
                predictedDateEndCal.add(Calendar.DAY_OF_MONTH, 1);
                Log.e("End Date After Adding: ",predictedDateEndCal.getTime().toString());
            }
            endTimePickerLabel.setText(CenesUtils.hmmaa.format(predictedDateEndCal.getTime()).toUpperCase());
            Log.e("End Date : ", predictedDateEndCal.getTime().toString());
            event.setEndTime(predictedDateEndCal.getTimeInMillis());

            //We will set the end time to be selected if
            if (!createGatheringDto.isStartTime()) {
                createGatheringDto.setEndTime(true);
            }

            System.out.println("endTimePickerLisener Listener");
            showPredictions();
        }
    };

    OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            try {
                hideKeyboardAndClearFocus(gathEventTitleEditView);

                ivDateBarArrow.setImageResource(R.drawable.date_panel_right_arrow);

                System.out.println("Calendar Date Clicked.");
                //ivDateBarArrow.setImageResource(R.drawable.date_panel_right_arrow);
                if (event.getPredictiveOn()) {
                    if (predictiveDataList != null) {
                        for (PredictiveData predictiveData: predictiveDataList) {

                            Calendar predictiveDateCal = Calendar.getInstance();
                            predictiveDateCal.setTimeInMillis(predictiveData.getDate());
                            if (predictiveDateCal.get(Calendar.DAY_OF_MONTH) == date.getDay() &&
                                    predictiveDateCal.get(Calendar.MONTH) == date.getMonth() &&
                                    predictiveDateCal.get(Calendar.YEAR) == date.getYear()) {


                                predictiveDataForDate = predictiveData;
                                break;
                            }
                        }

                        //Refershing Membres Collection view to show dots
                        friendHorizontalScrollAdapter = new FriendHorizontalScrollAdapter(CreateGatheringFragment.this, membersSelected);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(friendHorizontalScrollAdapter);
                    }
                } else {
                    predictiveDataForDate = null;
                }



                Long selectedTime = date.getCalendar().getTimeInMillis();

                Calendar selectedTimeCal = Calendar.getInstance();
                selectedTimeCal.setTimeInMillis(selectedTime);

                Calendar currentDate = Calendar.getInstance();
                currentDate.set(Calendar.HOUR, 0);
                currentDate.set(Calendar.MINUTE, 0);
                currentDate.set(Calendar.SECOND, 0);
                currentDate.set(Calendar.MILLISECOND, 0);

                if (((selectedTimeCal.get(Calendar.DAY_OF_MONTH) < currentDate.get(Calendar.DAY_OF_MONTH) || selectedTimeCal.get(Calendar.DAY_OF_MONTH) >= currentDate.get(Calendar.DAY_OF_MONTH)) &&
                        (selectedTimeCal.get(Calendar.MONTH) >= currentDate.get(Calendar.MONTH) || selectedTimeCal.get(Calendar.MONTH) < currentDate.get(Calendar.MONTH)) &&
                        selectedTimeCal.get(Calendar.YEAR) > currentDate.get(Calendar.YEAR)) ||

                        (selectedTimeCal.get(Calendar.DAY_OF_MONTH) >= currentDate.get(Calendar.DAY_OF_MONTH) &&
                                (selectedTimeCal.get(Calendar.MONTH) >= currentDate.get(Calendar.MONTH)) &&
                                selectedTimeCal.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) ||

                        (selectedTimeCal.get(Calendar.DAY_OF_MONTH) < currentDate.get(Calendar.DAY_OF_MONTH) &&
                                (selectedTimeCal.get(Calendar.MONTH) > currentDate.get(Calendar.MONTH)) &&
                                selectedTimeCal.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) ) {

                    try {

                        Calendar eventDateCalendar = Calendar.getInstance();
                        eventDateCalendar.setTimeInMillis(selectedTime);

                        //This code is needed to check if the selected date in MVC
                        //is of same Year or next Year
                        Calendar currentDateCal = Calendar.getInstance();

                        //Code to show selected date
                        if (currentDateCal.get(Calendar.YEAR) == eventDateCalendar.get(Calendar.YEAR)) {
                            tvEventDate.setText(CenesUtils.EEEMMMMdd.format(eventDateCalendar.getTime()));
                        } else {
                            tvEventDate.setText(CenesUtils.EEEMMMMddcmyyyy.format(eventDateCalendar.getTime()));
                        }

                        Calendar startTimeCal = Calendar.getInstance();
                        startTimeCal.setTimeInMillis(event.getStartTime());
                        startTimeCal.set(Calendar.DAY_OF_MONTH, eventDateCalendar.get(Calendar.DAY_OF_MONTH));
                        startTimeCal.set(Calendar.YEAR, eventDateCalendar.get(Calendar.YEAR));
                        startTimeCal.set(Calendar.MONTH, eventDateCalendar.get(Calendar.MONTH));

                        Calendar endTimeCal = Calendar.getInstance();
                        endTimeCal.setTimeInMillis(event.getEndTime());
                        endTimeCal.set(Calendar.DAY_OF_MONTH, eventDateCalendar.get(Calendar.DAY_OF_MONTH));
                        endTimeCal.set(Calendar.YEAR, eventDateCalendar.get(Calendar.YEAR));
                        endTimeCal.set(Calendar.MONTH, eventDateCalendar.get(Calendar.MONTH));

                        if (startTimeCal.getTimeInMillis() > endTimeCal.getTimeInMillis()) {
                            endTimeCal.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        event.setStartTime(startTimeCal.getTimeInMillis());
                        event.setEndTime(endTimeCal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    llPredictiveCalCell.setVisibility(View.GONE);
                    llPredictiveCalInfo.setVisibility(View.GONE);

                    llGatheringDateBars.setVisibility(View.VISIBLE);
                    llGatheringInfoBars.setVisibility(View.VISIBLE);
                    rlSelectedFriendsRecyclerView.setVisibility(View.VISIBLE);
                    rlHeader.setVisibility(View.VISIBLE);
                    rlPreviewInvitationButton.setVisibility(View.VISIBLE);

                    createGatheringDto.setDate(true);
                } else {

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void firePictureIntent(){
        if(isTakeOrUpload == "Upload"){
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, UPLOAD_IMAGE_REQUEST_CODE);

        }else if(isTakeOrUpload == "Take"){
            checkReadWritePermissiosn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == UPLOAD_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                firePictureIntent();
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                firePictureIntent();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("On Activity Result of Base Activity Called : Inside Fragment");
        if  (resultCode == Activity.RESULT_OK ) {
            if (requestCode == UPLOAD_IMAGE_REQUEST_CODE) {
                try {

                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), data.getData());

                    Uri imageUri = data.getData();

                /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), imageUri);
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

                ImageUtils.cropImageWithAspect(getImageUri(getContext().getApplicationContext(), rotatedBitmap), this, 1280, 512);*/
                    Uri resultUri = Uri.fromFile(new File(ImageUtils.getDefaultFile()));
                    UCrop.Options options = new UCrop.Options();
                    options.setAspectRatioOptions(1,
                            new AspectRatio("1:2", 1, 2),
                            new AspectRatio("3:4", 3, 4),
                            new AspectRatio("Original", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                            new AspectRatio("16:9", 16, 9),
                            new AspectRatio("1:1", 1, 1));
                    UCrop.of(imageUri, resultUri).withOptions(options)
                            //.withAspectRatio(3, 4)
                            .withMaxResultSize(1600, 1000)
                            .start(getContext(), CreateGatheringFragment.this, UCrop.REQUEST_CROP);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CLICK_IMAGE_REQUEST_CODE) {

                   try {
                        Uri resultUri = Uri.fromFile(new File(ImageUtils.getDefaultFile()));
                       UCrop.Options options = new UCrop.Options();
                       options.setAspectRatioOptions(1,
                               new AspectRatio("1:2", 1, 2),
                               new AspectRatio("3:4", 3, 4),
                               new AspectRatio("Original", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                               new AspectRatio("16:9", 16, 9),
                               new AspectRatio("1:1", 1, 1));

                       UCrop.of(cameraFileUri, resultUri).withOptions(options)
                                        //.withAspectRatio(3, 4)
                                        .withMaxResultSize(1600, 1000)
                                        .start(getContext(), CreateGatheringFragment.this, UCrop.REQUEST_CROP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else if (requestCode == UCrop.REQUEST_CROP) {

               try {
                    System.out.println("Back from UCrop.....");
                    Uri resultUri = UCrop.getOutput(data);
                    String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), resultUri);
                    eventImageFile = new File(filePath);
                    System.out.println("File Path String : " + filePath);
                    System.out.println("File Path : " + eventImageFile.getPath());
                    event.setEventImageURI(eventImageFile.getPath());

                   MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                   try {
                       JSONObject props = new JSONObject();
                       props.put("Action","After Cropping Success");
                       props.put("Logs", "FilePath: "+filePath);
                       props.put("UserEmail",loggedInUser.getEmail());
                       props.put("UserName",loggedInUser.getName());
                       mixpanel.track("Gathering", props);

                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                    //Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), resultUri);

                    /*ExifInterface ei = new ExifInterface(filePath);
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

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvCoverImageStatus.setText("Uplaoded");
                        }
                    }, 500);*/
                } catch (Exception e) {
                    e.printStackTrace();

                   MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                   try {
                       JSONObject props = new JSONObject();
                       props.put("Action","After Cropping Exception");
                       props.put("Logs", e.getMessage());
                       props.put("UserEmail",loggedInUser.getEmail());
                       props.put("UserName",loggedInUser.getName());
                       mixpanel.track("Gathering", props);

                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }
                }

                progressBar.setVisibility(View.VISIBLE);


                //tvCoverImageStatus.setText("Uplaoded");
                uploadEventPicture(eventImageFile);
                createGatheringDto.setPicture(true);
            } else if (requestCode == SEACRH_LOCATION_RESULT_CODE) {

                hideKeyboardAndClearFocus(gathEventTitleEditView);
                createGatheringDto.setLocation(true);

                if (data.hasExtra("selection")) {
                    if (data.getStringExtra("selection").equals("list")) {
                        //placeId = data.getStringExtra("placeId");

                        Gson gson = new Gson();
                        Location location = gson.fromJson(data.getStringExtra("location"), Location.class);
                        event.setPlaceId(location.getPlaceId());

                        event.setLocation(location.getLocation());

                        if (location.getLocation().length() > 20) {
                            tvLocationLabel.setText(location.getLocation().substring(0, 20)+"...");
                        } else {
                            tvLocationLabel.setText(location.getLocation());

                        }

                        new LocationAsyncTask.FetchLatLngTask(new LocationAsyncTask.FetchLatLngTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject locations) {

                                try {

                                    JSONObject locationObj = locations.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                                    event.setLatitude(locationObj.getString("lat"));
                                    event.setLongitude(locationObj.getString("lng"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).execute(location.getPlaceId());
                    } else if (data.getStringExtra("selection").equals("horizontalScroll")) {
                        Location locationObj = new Gson().fromJson(data.getStringExtra("locationObj"), Location.class);
                        event.setPlaceId(locationObj.getPlaceId());
                        String title = locationObj.getLocation();
                        System.out.println("Location Title : "+title);
                        event.setLocation(title);

                        if (title.length() > 20) {
                            tvLocationLabel.setText(title.substring(0, 20)+"...");
                        } else {
                            tvLocationLabel.setText(title);

                        }


                        new LocationAsyncTask.FetchLatLngTask(new LocationAsyncTask.FetchLatLngTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject locations) {

                                try {

                                    JSONObject locationObj = locations.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");

                                    //parentEvent.setLocation(job.getJSONObject("result").getString("formatted_address"));
                                    event.setLatitude(locationObj.getString("lat"));
                                    event.setLongitude(locationObj.getString("lng"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).execute(locationObj.getPlaceId());
                    } else if (data.getStringExtra("selection").equals("done")) {
                        String title = data.getStringExtra("title");

                        if (title.length() > 20) {
                            tvLocationLabel.setText(title.substring(0, 20)+"...");
                        } else {
                            tvLocationLabel.setText(title);

                        }
                        event.setLocation(title);

                    }
                }
            }  else if (requestCode == SEARCH_FRIEND_RESULT_CODE) {

                hideKeyboardAndClearFocus(gathEventTitleEditView);
                System.out.println("Inside Activity result : ");
                String selectedFriendsJsonArrayStr = data.getExtras().getString("selectedFriendJsonArray");
                try {
                    System.out.println(selectedFriendsJsonArrayStr);
                    predictiveDataList = null;
                    predictiveDataForDate = null;

                    Gson gson = new GsonBuilder().create();
                    Type listType = new TypeToken<List<EventMember>>(){}.getType();
                    membersSelected = gson.fromJson( selectedFriendsJsonArrayStr, listType);
                    populateFriendCollectionView();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (event.getPredictiveOn()) {
                    showPredictions();
                }

            } else if (requestCode == GATHERING_SUMMARY_RESULT_CODE && resultCode == Activity.RESULT_OK) {

                getActivity().setResult(Activity.RESULT_OK);
                event.setPredictiveOn(false);
                getActivity().finish();

            }  else if (requestCode == MESSAGE_FRAGMENT_CODE && resultCode == Activity.RESULT_OK) {

                hideKeyboardAndClearFocus(gathEventTitleEditView);
                final String message = data.getStringExtra("message");
                if (message != null && message.length() != 0) {

                    event.setDescription(message.toString());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            tvGatheringMessage.setText("Saved");
                        }
                    }, 500);

                    createGatheringDto.setMesssage(true);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {

            if (requestCode == SEACRH_LOCATION_RESULT_CODE) {
                Log.e("Cancelled", "Location Cancelled");
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Log.e("Error", "Upload Error");
            } else {
                System.out.println("Error on Cropping...");
            }
        }

        //materialCalendarView.setPagingEnabled(true);
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

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                System.out.println("On Checked Change Listener");
                if (createGatheringDto.isStartTime() && createGatheringDto.isEndTime()) {

                    event.setPredictiveOn(true);
                    showPredictions();

                } else {
                    showAlert("Alert","Please select Start and End Time.");
                    predictiveCalSwitch.setChecked(false);
                }

            } else {

                event.setPredictiveOn(false);
                Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(currentMonth);
                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getContext(), R.drawable.mcv_white_color, drawableDates, false, false);
                materialCalendarView.addDecorator(calBgDecorator);
                //Refershing Membres Collection view to show dots

                predictiveDataList = null;
                predictiveDataForDate = null;
                friendHorizontalScrollAdapter = new FriendHorizontalScrollAdapter(CreateGatheringFragment.this, membersSelected);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(friendHorizontalScrollAdapter);
            }

        }
    };

    public void showPredictions() {
        if (event.getPredictiveOn()) {
            try {

                Calendar predictiveStartCal = Calendar.getInstance();
                predictiveStartCal.setTimeInMillis(event.getStartTime());
                predictiveStartCal.set(Calendar.DAY_OF_MONTH, 15);
                predictiveStartCal.set(Calendar.MILLISECOND, 0);
                predictiveStartCal.set(Calendar.SECOND, 0);
                predictiveStartCal.set(Calendar.MONTH, currentMonth.get(Calendar.MONTH));

                Calendar predictiveEndCal = Calendar.getInstance();
                predictiveEndCal.setTimeInMillis(event.getEndTime());
                predictiveEndCal.set(Calendar.DAY_OF_MONTH, 15);
                predictiveEndCal.set(Calendar.MILLISECOND, 0);
                predictiveEndCal.set(Calendar.SECOND, 0);
                predictiveEndCal.set(Calendar.MONTH, currentMonth.get(Calendar.MONTH));

                Calendar predictiveStartCalTemp = Calendar.getInstance();
                predictiveStartCalTemp.setTimeInMillis(event.getStartTime());

                Calendar predictiveEndCalTemp = Calendar.getInstance();
                predictiveEndCalTemp.setTimeInMillis(event.getEndTime());

                if (predictiveStartCalTemp.get(Calendar.DAY_OF_MONTH) < predictiveEndCalTemp.get(Calendar.DAY_OF_MONTH)) {
                    predictiveEndCal.add(Calendar.DAY_OF_MONTH, 1);
                }

                JSONObject job = new JSONObject();
                job.put("startTimeMilliseconds", predictiveStartCal.getTimeInMillis());
                job.put("endTimeMilliseconds", predictiveEndCal.getTimeInMillis());
                callPredictiveCalendarTask(predictiveStartCal.getTimeInMillis(), predictiveEndCal.getTimeInMillis());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateCreateGatheringForm() {

        if (gathEventTitleEditView.getText().toString().length() == 0) {
            showAlert("Alert", "Event Title cannot be empty.");
            return false;

        } else if (!createGatheringDto.isStartTime()) {
            showAlert("Alert", "Please select event start time.");
            return false;
        } else if (!createGatheringDto.isEndTime()) {
            showAlert("Alert", "Please select event end time.");
            return false;
        } else if (!createGatheringDto.isDate()) {
            showAlert("Alert", "Please select event date.");
            return false;
        }

        //If its a group event
        //Then validate all fields.
        if (membersSelected.size() > 1) {
            if (!createGatheringDto.isLocation()) {
                showAlert("Alert", "Please choose event location.");
                return false;
            } else if (!createGatheringDto.isMesssage()) {
                showAlert("Alert", "Event description cannot be empty.");
                return false;
            } else if (!createGatheringDto.isPicture()) {
                showAlert("Alert", "Please upload event image.");
                return false;
            }
        }

        return true;
    }

    public void onCalendarPageChangeListener(CalendarDay currentMonth) {
        System.out.println("Page Change Listener. Month : "+currentMonth.getMonth()+", Year : "+currentMonth.getYear());
        try {
            CreateGatheringFragment.this.currentMonth.set(Calendar.MONTH, currentMonth.getMonth());
            CreateGatheringFragment.this.currentMonth.set(Calendar.YEAR, currentMonth.getYear());

            if (event.getPredictiveOn()) {
                Calendar predictiveStartCal = Calendar.getInstance();
                predictiveStartCal.setTimeInMillis(event.getStartTime());
                predictiveStartCal.set(Calendar.DAY_OF_MONTH, 15);
                predictiveStartCal.set(Calendar.YEAR, currentMonth.getYear());
                predictiveStartCal.set(Calendar.MONTH, currentMonth.getMonth());
                predictiveStartCal.set(Calendar.SECOND, 0);
                predictiveStartCal.set(Calendar.MILLISECOND, 0);


                Calendar nextMonth = Calendar.getInstance();
                nextMonth.set(Calendar.DAY_OF_MONTH, 1);
                nextMonth.set(Calendar.YEAR, currentMonth.getYear());
                nextMonth.set(Calendar.MONTH, currentMonth.getMonth());
                nextMonth.add(Calendar.MONTH, 1);


                Calendar predictiveEndCal = Calendar.getInstance();
                predictiveEndCal.setTimeInMillis(event.getEndTime());
                predictiveEndCal.set(Calendar.DAY_OF_MONTH, 15);
                predictiveEndCal.set(Calendar.MONTH, currentMonth.getMonth());
                predictiveEndCal.set(Calendar.YEAR, currentMonth.getYear());
                predictiveEndCal.set(Calendar.SECOND, 0);
                predictiveEndCal.set(Calendar.MILLISECOND, 0);

                Calendar predictiveStartCalTemp = Calendar.getInstance();
                predictiveStartCalTemp.setTimeInMillis(event.getStartTime());

                Calendar predictiveEndCalTemp = Calendar.getInstance();
                predictiveEndCalTemp.setTimeInMillis(event.getEndTime());

                if (predictiveStartCalTemp.get(Calendar.DAY_OF_MONTH) < predictiveEndCalTemp.get(Calendar.DAY_OF_MONTH)) {
                    predictiveEndCal.add(Calendar.DAY_OF_MONTH, 1);
                }

                JSONObject job = new JSONObject();
                try {
                    job.put("startTimeMilliseconds", predictiveStartCal.getTimeInMillis());
                    job.put("endTimeMilliseconds", predictiveEndCal.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (llPredictiveCalCell.getVisibility() == View.VISIBLE) {
                    callPredictiveCalendarTask(predictiveStartCal.getTimeInMillis(), predictiveEndCal.getTimeInMillis());
                }
            } else {
                if (((CenesBaseActivity)getActivity()) != null && ((CenesBaseActivity)getActivity()).parentEvent == null) {
                    Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(CreateGatheringFragment.this.currentMonth);
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getContext(), R.drawable.mcv_white_color, drawableDates, false, false);
                    materialCalendarView.addDecorator(calBgDecorator);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseFromGalleryPressed() {

        rlPhotoActionSheet.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
        } else {
            Intent browseIntent = new Intent(Intent.ACTION_GET_CONTENT);
            browseIntent.setType("image/*");
            startActivityForResult(browseIntent, UPLOAD_IMAGE_REQUEST_CODE);
        }
    }

    public void takePhotoPressed() {
        rlPhotoActionSheet.setVisibility(View.GONE);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            (CreateGatheringFragment.this).requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_CODE);
        } else {

            openCamera();
        }
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        //startActivityForResult(takePictureIntent, CLICK_IMAGE_REQUEST_CODE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Cenes";
            File newdir = new File(dir);
            newdir.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File file = new File(dir + File.separator + "IMG_" + timeStamp + ".jpg");

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cameraFileUri = null;
            cameraFileUri = Uri.fromFile(file);
            //takePictureIntent.putExtra( android.provider.MediaStore.EXTRA_SIZE_LIMIT, "720000");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
            //takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 180);
            this.startActivityForResult(takePictureIntent, CLICK_IMAGE_REQUEST_CODE);
        }
    }
    public void uploadEventPicture(File file) {

        new GatheringAsyncTask.UploadOnlyImageTask(new GatheringAsyncTask.UploadOnlyImageTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                try {
                    boolean success = false;
                    if(response != null) {
                        success = response.getBoolean("success");
                    }
                    if (success == true) {
                        tvCoverImageStatus.setText("Uploaded");

                        JSONObject data = response.getJSONObject("data");
                        event.setEventPicture(data.getString("large"));
                        event.setThumbnail(data.getString("thumbnail"));
                    } else {
                        //String message = response.getString("message");
                        Toast.makeText(getContext(), "Error Uploading Image", Toast.LENGTH_LONG).show();
                    }

                    gathEventTitleEditView.clearFocus();
                    gathEventTitleEditView.setFocusableInTouchMode(false);
                    gathEventTitleEditView.setFocusable(false);
                    gathEventTitleEditView.setFocusableInTouchMode(true);
                    gathEventTitleEditView.setFocusable(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).execute(file);
    }

    public void callPredictiveCalendarTask(Long startTime, Long endTime) {

        String queryStr = "userId=" + loggedInUser.getUserId() + "&startTime=" + startTime + "&endTime=" + endTime + "";
        try {
            if (membersSelected.size() > 0) {
                String friends = "";
                for (EventMember friendMap : membersSelected) {
                    if (friendMap.getUserId() == null || (friendMap.getUserId() != null && loggedInUser.getUserId().equals(friendMap.getUserId()))) {
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

        System.out.println(queryStr);
        new GatheringAsyncTask.PredictiveCalendarTask(new GatheringAsyncTask.PredictiveCalendarTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {

                    JSONArray preditiveArr = response.getJSONArray("data");
                    Gson gson = new GsonBuilder().create();
                    Type listType = new TypeToken<List<PredictiveData>>(){}.getType();
                    predictiveDataList = gson.fromJson(preditiveArr.toString(), listType);

                    Map<String, Set<CalendarDay>> calMap = GatheringService.parsePredictiveData(preditiveArr);
                    for (Map.Entry<String, Set<CalendarDay>> calEntrySet : calMap.entrySet()) {

                        if (getActivity() == null) {
                            return;
                        }
                        Set<CalendarDay> colorSet = null;
                        if (calEntrySet.getKey().equals("WHITE")) {
                            BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_lightgrey_color, calEntrySet.getValue(), true, true);
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
                            materialCalendarView.addDecorator(calBgDecorator);                }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).execute(queryStr);
    }

    public void populateFriendCollectionView() {

        if (membersSelected != null && membersSelected.size() > 0) {

            System.out.println("MembersSelected Size : "+membersSelected.size());
            List<EventMember> orderedMembersSelected = new ArrayList<>();
            for (EventMember eventMember: membersSelected) {
                if (eventMember.getUser() != null && eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    orderedMembersSelected.add(eventMember);
                    break;
                }
            }
            for (EventMember eventMember: membersSelected) {
                System.out.println("Event Member User Id : "+eventMember.getFriendId());

                if (eventMember.getUser() != null &&  eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    continue;
                }
                orderedMembersSelected.add(eventMember);
            }
            membersSelected = orderedMembersSelected;

            if (event == null) {
                event = new Event();
            }
            if (event.getEventMembers() == null) {
                event.setEventMembers(new ArrayList<EventMember>());
            }
            event.setEventMembers(membersSelected);
            System.out.println("Event Member Size : "+membersSelected.size());
            recyclerView.setVisibility(View.VISIBLE);

            friendHorizontalScrollAdapter = new FriendHorizontalScrollAdapter(CreateGatheringFragment.this, membersSelected);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(friendHorizontalScrollAdapter);
        }

    }


    public void populateCreateGatheringObj() {
        try {

            gathEventTitleEditView.setText(event.getTitle());

            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                tvCoverImageStatus.setText("Uploaded");
                createGatheringDto.setPicture(true);
            }
            if (!CenesUtils.isEmpty(event.getLocation())) {

                if (event.getLocation().length() > 20) {
                    tvLocationLabel.setText(event.getLocation().substring(0, 20)+"...");
                } else {
                    tvLocationLabel.setText(event.getLocation());
                }
                createGatheringDto.setLocation(true);
            }
            if (!CenesUtils.isEmpty(event.getDescription())) {
                tvGatheringMessage.setText("Saved");
                createGatheringDto.setMesssage(true);
            }

            Calendar eventDateCal = Calendar.getInstance();
            eventDateCal.setTimeInMillis(event.getStartTime());
            if (eventDateCal.get(Calendar.YEAR) == eventDateCal.get(Calendar.YEAR)) {
                tvEventDate.setText(CenesUtils.EEEMMMMdd.format(eventDateCal.getTime()));
            } else {
                tvEventDate.setText(CenesUtils.EEEMMMMddcmyyyy.format(eventDateCal.getTime()));
            }
            createGatheringDto.setDate(true);

            startTimePickerLabel.setText(CenesUtils.hmmaa.format(new Date(event.getStartTime())).toUpperCase());
            createGatheringDto.setStartTime(true);

            endTimePickerLabel.setText(CenesUtils.hmmaa.format(new Date(event.getEndTime())).toUpperCase());
            createGatheringDto.setEndTime(true);

            if (event.getPredictiveOn()) {
                predictiveCalSwitch.setChecked(true);
            }

            membersSelected = event.getEventMembers();

            predictiveDataList = null;
            predictiveDataForDate = null;
            populateFriendCollectionView();

            if (event.getPredictiveOn()) {
                if (llPredictiveCalCell.getVisibility() != View.VISIBLE) {

                    if (currentMonth == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(event.getStartTime());
                        currentMonth = calendar;
                    }

                    event.setPredictiveOn(false);
                    predictiveCalSwitch.performClick();
                }
            }

            if (event.getEventId() != null) {
                eventTemp = new Event();
                eventTemp.setTitle(event.getTitle());
                eventTemp.setStartTime(event.getStartTime());
                eventTemp.setEndTime(event.getEndTime());
                eventTemp.setLocation(event.getLocation());
                eventTemp.setDescription(event.getDescription());
                eventTemp.setEventPicture(event.getEventPicture());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void checkCameraPermissiosn() {
        rlPhotoActionSheet.setVisibility(View.GONE);
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
            openCamera();

        }
    }
}
