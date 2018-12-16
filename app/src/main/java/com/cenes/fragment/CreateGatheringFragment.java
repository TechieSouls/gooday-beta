package com.cenes.fragment;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.activity.HomeScreenActivity;
import com.cenes.activity.SearchFriendActivity;
import com.cenes.activity.SearchLocationActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.materialcalendarview.CalendarDay;
import com.cenes.materialcalendarview.MaterialCalendarView;
import com.cenes.materialcalendarview.OnDateSelectedListener;
import com.cenes.materialcalendarview.decorators.BackgroundDecorator;
import com.cenes.materialcalendarview.decorators.EventDecorator;
import com.cenes.materialcalendarview.decorators.OneDayDecorator;
import com.cenes.service.GatheringService;
import com.cenes.util.CenesUtils;
import com.cenes.util.ImageUtils;
import com.cenes.util.RoundedImageView;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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

    private int SEACRH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002, GATHERING_SUMMARY_RESULT_CODE = 1003;

    private View fragmentView;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private Set<Map<String, String>> inviteFriendsImageList;
    private Calendar predictedDateStartCal, predictedDateEndCal;
    private boolean isPredictiveOn;
    private Boolean requestForGatheringSummary = false;
    private Calendar currentMonth;
    private JSONArray predictiveClanedarAPIData;

    private ImageView gathEventImage, ivEventShareIcon;
    private EditText gathEventTitleEditView, gatheringDescription;
    TextView gath_date_after_fix, gathInviteFrndsBtn, gathSelectDatetimeBtn, gathSearchLocationButton;
    private Switch predictiveCalSwitch;
    private TextView startTimePickerLabel, endTimePickerLabel;
    private LinearLayout startTimePickerLabelLayout, endTimePickerLabelLayout;

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
    private Boolean isEditMode = false;
    private User loggedInUser;

    private MaterialCalendarView materialCalendarView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_create_gathering, container, false);
        fragmentView = view;

        initializeComponents();
        addClickListnersToComponents();

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

        startTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateStartCal.getTime()));
        endTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateEndCal.getTime()));

        materialCalendarView.setCurrentDate(predictedDateStartCal);

        currentMonth = predictedDateStartCal;

        ivCreateGatheringCloseButton = (TextView) fragmentView.findViewById(R.id.crt_gath_cls_btn);

        //Registering click listeners
        ivCreateGatheringCloseButton.setOnClickListener(onClickListener);

        CalendarDay calDay = new CalendarDay();
        Set<CalendarDay> calDaySet = new HashSet<>();
        calDay = new CalendarDay(predictedDateStartCal.getTime());
        calDaySet.add(calDay);


        BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_white_color, calDaySet, false);
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
        ((GatheringScreenActivity) getActivity()).hideFooter();
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
                    getActivity().onBackPressed();
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
                        if (eventImageFile == null) {
                            JSONObject createEventObj = new JSONObject();
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
                            new CreateGatheringTask().execute(createEventObj);
                        } else {
                            new UploadImageTask().execute();
                        }
                    }
                    break;
                case R.id.gath_search_location_button:
                    startActivityForResult(new Intent(getActivity(), SearchLocationActivity.class), SEACRH_LOCATION_RESULT_CODE);
                    break;
                case R.id.gath_select_dtime_start_view:
                    int hour = predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY);
                    int minute = predictedDateStartCal.get(predictedDateStartCal.MINUTE);
                    TimePickerDialog startTimePickerDialog = new TimePickerDialog(getActivity(), startTimePickerLisener, hour, minute, false);
                    startTimePickerDialog.show();
                    break;
                case R.id.gath_select_dtime_end_view:
                    int endHour = predictedDateEndCal.get(predictedDateEndCal.HOUR_OF_DAY);
                    int endMinute = predictedDateEndCal.get(predictedDateEndCal.MINUTE);
                    TimePickerDialog endTimePickerDialog = new TimePickerDialog(getActivity(), endTimePickerLisener, endHour, endMinute, false);
                    endTimePickerDialog.show();
                    break;
                case R.id.gath_date_after_fix:
                    v.setVisibility(View.GONE);
                    gathSelectDatetimeBtn.setVisibility(View.GONE);
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
            startTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateStartCal.getTime()));
            Log.e("Start Date : ", predictedDateStartCal.getTime().toString());

            //Setting End Time to 1Hour Delay of Start Time by Default.
            //if (predictedDateEndCal.getTimeInMillis() <= predictedDateStartCal.getTimeInMillis()) {
                predictedDateEndCal.set(Calendar.HOUR_OF_DAY, predictedDateStartCal.get(predictedDateStartCal.HOUR_OF_DAY));
                predictedDateEndCal.set(Calendar.MINUTE, predictedDateStartCal.get(predictedDateStartCal.MINUTE));
                predictedDateEndCal.add(Calendar.MINUTE, 60);
                endTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateEndCal.getTime()));
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
            endTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateEndCal.getTime()));
            Log.e("End Date : ", predictedDateEndCal.getTime().toString());

            if (predictedDateEndCal.getTimeInMillis() <= predictedDateStartCal.getTimeInMillis()) {
                Toast.makeText(cenesApplication, "End Time should be greater than Start Time", Toast.LENGTH_SHORT).show();
                return;
            }
            endTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateEndCal.getTime()));
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
                ImageUtils.cropImageWithAspect(Uri.parse(data.getDataString()), this, 512, 512);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            try {
                String filePath = ImageUtils.getPath(getCenesActivity().getApplicationContext(), Crop.getOutput(data));
                eventImageFile = new File(filePath);

                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getCenesActivity().getContentResolver(), Crop.getOutput(data));

                gathEventImage.setImageBitmap(ImageUtils.getRotatedBitmap(imageBitmap, filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            String requiredValue = data.getStringExtra("title");
            gathSearchLocationButton.setText(requiredValue.toString());
            new FetchLatLngTask().execute(data.getStringExtra("placeId"));
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Location Cancelled");
        } else if (requestCode == SEARCH_FRIEND_RESULT_CODE && resultCode == Activity.RESULT_OK) {

            String requiredValue = data.getExtras().getString("photo");
            String name = data.getExtras().getString("name");
            Map<String, String> invFrnMap = new HashMap<>();
            invFrnMap.put("name", name);
            invFrnMap.put("photo", requiredValue);
            invFrnMap.put("userId", data.getExtras().getString("userId"));

            Boolean isFriendExist = false;
            if (inviteFriendsImageList.size() > 0) {
                for (Map<String, String> friendMap : inviteFriendsImageList) {
                    System.out.println(friendMap.get("userId"));
                    System.out.println(invFrnMap.get("userId"));
                    if (friendMap.get("userId") == invFrnMap.get("userId")) {
                        isFriendExist = true;
                        break;
                    }
                }
            }
            if (!isFriendExist) {
                inviteFriendsImageList.add(invFrnMap);
                if (isPredictiveOn) {
                    showPredictions();
                }
            }

            if (inviteFriendsImageList.size() > 0) {
                recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                recyclerView.setVisibility(View.VISIBLE);

                ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
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

        private ArrayList<Map<String, String>> jsonObjectArrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public RoundedImageView ivFriend;
            TextView tvName;
            RelativeLayout container;
            ImageButton ibDelete;

            public MyViewHolder(View view) {
                super(view);
                ivFriend = (RoundedImageView) view.findViewById(R.id.iv_friend_image);
                tvName = (TextView) view.findViewById(R.id.tv_friend_name);
                container = (RelativeLayout) view.findViewById(R.id.container);
                ibDelete = (ImageButton) view.findViewById(R.id.ib_delete);
            }
        }

        public FriendsAdapter(ArrayList<Map<String, String>> jsonObjectArrayList) {
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
                final Map<String, String> invFrn = jsonObjectArrayList.get(position);
                holder.tvName.setText(invFrn.get("name"));
                holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
                if (invFrn.get("photo") != null && invFrn.get("photo") != "null") {
                    Glide.with(getActivity()).load(invFrn.get("photo")).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.ivFriend);
                } else {
                    holder.ivFriend.setImageResource(R.drawable.default_profile_icon);
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
            locationPhotoUrl = "";
            try {
                String queryStr = "&placeid=" + URLEncoder.encode(keyword);
                JSONObject job = apiManager.locationLatLng(queryStr);
                JSONObject locationObj = job.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                latitude = locationObj.getString("lat");
                longitude = locationObj.getString("lng");

                if (!isUserUploadedImage) {
                    if (!job.getJSONObject("result").isNull("photos") && job.getJSONObject("result").getJSONArray("photos").length() > 0) {
                        locationPhotoUrl = GOOGLE_PLACE_PHOTOS_API + job.getJSONObject("result").getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (eventImageFile == null) {
                if (!locationPhotoUrl.equals("")) {
                    Glide.with(context).load(locationPhotoUrl).apply(RequestOptions.placeholderOf(R.drawable.party_image)).into(gathEventImage);
                } else {
                    if (!isUserUploadedImage) {
                        gathEventImage.setImageDrawable(context.getResources().getDrawable(R.drawable.party_image));
                    }
                }
            }
        }
    }

    String latitude = "";
    String longitude = "";
    String locationPhotoUrl = "";
    String oldImageUrl = "";
    public String GOOGLE_PLACE_PHOTOS_API = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyAg8FTMwwY2LwneObVbjcjj-9DYZkrTR58&maxwidth=400&photoreference=";
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
        startTimePickerLabelLayout = (LinearLayout) fragmentView.findViewById(R.id.gath_select_dtime_start_view);
        endTimePickerLabel = (TextView) fragmentView.findViewById(R.id.end_time_picker_label);
        endTimePickerLabelLayout = (LinearLayout) fragmentView.findViewById(R.id.gath_select_dtime_end_view);
        gatheringDescription = (EditText) fragmentView.findViewById(R.id.gath_desc);
        gath_date_after_fix = (TextView) fragmentView.findViewById(R.id.gath_date_after_fix);

        editGatheringSummaryBtn = (TextView) fragmentView.findViewById(R.id.edit_gathering_btn);
        ivEventShareIcon = (ImageView) fragmentView.findViewById(R.id.iv_event_share_icon);


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

                if (!isEditMode && eventId != null) {
                    return;
                }

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

                    predictedDateStartCal.set(Calendar.YEAR, date.getYear());
                    predictedDateStartCal.set(Calendar.MONTH, date.getMonth());
                    predictedDateStartCal.set(Calendar.DAY_OF_MONTH, date.getDay());
                    predictedDateStartCal.set(Calendar.SECOND, 0);
                    predictedDateStartCal.set(Calendar.MILLISECOND, 0);

                    predictedDateEndCal.set(Calendar.YEAR, date.getYear());
                    predictedDateEndCal.set(Calendar.MONTH, date.getMonth());
                    predictedDateEndCal.set(Calendar.DAY_OF_MONTH, date.getDay());
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
                showPredictions();
            } else {
                isPredictiveOn = false;
                Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(currentMonth);
                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getContext(), R.drawable.mcv_white_color, drawableDates, false);
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
                if (!requestForGatheringSummary) {
                    new PredictiveCalendarTask().execute(job);
                }
                //materialCalendarView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                CalendarDay currentMonth = (CalendarDay) intent.getExtras().get("CurrentMonth");
                CreateGatheringFragment.this.currentMonth.set(Calendar.MONTH, currentMonth.getMonth());
                CreateGatheringFragment.this.currentMonth.set(Calendar.YEAR, currentMonth.getYear());

//                enableDates = GatheringService.getEnableDates(currentMonth);
//                materialCalendarView.addDecorator(new DayEnableDecorator(enableDates));

                if (isPredictiveOn && !requestForGatheringSummary) {
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
                    Set<CalendarDay> drawableDates = CenesUtils.getDrawableMonthDateList(CreateGatheringFragment.this.currentMonth);
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(context, R.drawable.mcv_white_color, drawableDates, false);
                    materialCalendarView.addDecorator(calBgDecorator);
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
        startTimePickerLabelLayout.setOnClickListener(onClickListener);
        endTimePickerLabelLayout.setOnClickListener(onClickListener);
        gath_date_after_fix.setOnClickListener(onClickListener);

        editGatheringSummaryBtn.setOnClickListener(onClickListener);
        ivEventShareIcon.setOnClickListener(onClickListener);
        saveGatheringButton.setOnClickListener(onClickListener);
        gatheringDeletButton.setOnClickListener(onClickListener);
        //backGatheringSumamaryBtn.setOnClickListener(onClickListener);

    }

    public void shareEventLink() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.cenesWebDomain)+"/event/"+eventId);
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
            if (inviteFriendsImageList.size() > 0) {
                String friends = "";
                for (Map<String, String> friendMap : inviteFriendsImageList) {
                    if (String.valueOf(user.getUserId()) == friendMap.get("userId")) {
                        continue;
                    }
                    friends += friendMap.get("userId") + ",";
                }
                queryStr += "&friends=" + friends.substring(0, friends.length() - 1);
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
                if (calEntrySet.getKey().equals("RED")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_red_color, calEntrySet.getValue(), true);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("ORANGE")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_orange_color, calEntrySet.getValue(), true);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("YELLOW")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_yellow_color, calEntrySet.getValue(), true);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("LIME")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_lime_color, calEntrySet.getValue(), true);
                    materialCalendarView.addDecorator(calBgDecorator);
                } else if (calEntrySet.getKey().equals("GREEN")) {
                    BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_green_color, calEntrySet.getValue(), true);
                    materialCalendarView.addDecorator(calBgDecorator);
                }
            }
        }
    }

    class CreateGatheringTask extends AsyncTask<JSONObject, Object, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Creating...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjList) {
            JSONObject jsonObject = jsonObjList[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject job = apiManager.createGathering(user, jsonObject, getCenesActivity());
            Log.e("Resp", job.toString());
            return job;
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            Log.e("Gathering Obj ", obj.toString());
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            try {
                if (obj.getBoolean("success")) {
                    Toast.makeText(context, "Gathering Created", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().onBackPressed();
                    /*JSONObject data = obj.getJSONObject("data");
                    eventId = data.getLong("eventId");
                    getActivity().onBackPressed();
                    if (editGatheringSummaryBtn.getVisibility() == View.GONE) {


                        if (loggedInUser.getUserId() == data.getInt("createdById")) {
                            editGatheringSummaryBtn.setVisibility(View.VISIBLE);
                        }

                        saveGatheringButton.setVisibility(View.GONE);
                        gatheringDeletButton.setVisibility(View.GONE);
                    }
                    gatheringTitle.setText("Gathering");
                    isEditMode = false;
                    disableComponents();*/
                } else {
                    Toast.makeText(context, "Error in creating gathering", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UploadImageTask extends AsyncTask<JSONObject, Object, JSONObject> {

        @Override
        protected void onPreExecute() {

            mProgressDialog = CenesUtils.showProcessing(context, "Creating...");
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjList) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject imageUploadResp = apiManager.postMultipartEventImage(user, eventImageFile, getCenesActivity());
            return imageUploadResp;
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            CenesUtils.hideProcessing(context, mProgressDialog);
            try {
                if (obj.getBoolean("success")) {
                    User user = userManager.getUser();
                    JSONObject createEventObj = new JSONObject();
                    try {
                        createEventObj = populateCreateGatheringObj(user, obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    new CreateGatheringTask().execute(createEventObj);
                } else {
                    Toast.makeText(context, "Image Cannot be uploaded", Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    class MarkNotificationReadTask extends AsyncTask<Long, Long, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {

            Long eventId = longs[0];

            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId=" + user.getUserId()+"&notificationTypeId="+eventId;
            JSONObject response = apiManager.markNotificationAsReadByUserIdAndNotificatonId(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
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
                if (responseObj.getBoolean("success")) {
                    JSONObject summaryObj = responseObj.getJSONObject("data");
                    disableComponents();
                    //ivCreateGatheringCloseButton.setVisibility(View.GONE);
                    saveGatheringButton.setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.predictive_cal_date_time_cal_block).setVisibility(View.GONE);
                    fragmentView.findViewById(R.id.gath_select_datetime_mcv_section).setVisibility(View.VISIBLE);

                    gath_date_after_fix.setVisibility(View.VISIBLE);

                    if (loggedInUser.getUserId() == summaryObj.getInt("createdById")) {
                        editGatheringSummaryBtn.setVisibility(View.VISIBLE);
                        ivEventShareIcon.setVisibility(View.VISIBLE);
                    }
                    //backGatheringSumamaryBtn.setVisibility(View.VISIBLE);

                    gatheringTitle.setText("Gathering");

                    gathEventTitleEditView.setText(summaryObj.getString("title"));

                    if (!CenesUtils.isEmpty(summaryObj.getString("scheduleAs")) && summaryObj.getString("source").equals("Google")) {
                        editGatheringSummaryBtn.setVisibility(View.GONE);
                    }

                    if (summaryObj.getString("eventPicture") != null && summaryObj.getString("eventPicture") != "null" && summaryObj.getString("eventPicture") != "") {
                        oldImageUrl = summaryObj.getString("eventPicture");
                        Glide.with(context).load(summaryObj.getString("eventPicture")).apply(RequestOptions.placeholderOf(R.drawable.party_image)).into(gathEventImage);
                        if (summaryObj.getString("eventPicture").contains(GOOGLE_PLACE_PHOTOS_API)) {
                            isUserUploadedImage = false;
                        } else {
                            isUserUploadedImage = true;
                        }
                    } else {
                        oldImageUrl = "";
                        gathEventImage.setImageDrawable(context.getResources().getDrawable(R.drawable.party_image));
                    }

                    if (summaryObj.has("location") && !CenesUtils.isEmpty(summaryObj.getString("location"))) {
                        gathSearchLocationButton.setText(summaryObj.getString("location"));
                        gathSearchLocationButton.setHint("");
                    } else {
                        gathSearchLocationButton.setText("");
                        gathSearchLocationButton.setHint("Location");
                    }

                    if (summaryObj.has("description") && !CenesUtils.isEmpty(summaryObj.getString("description"))) {
                        gatheringDescription.setText(summaryObj.getString("description"));
                        gatheringDescription.setHint("");
                    } else {
                        gatheringDescription.setText("");
                        gatheringDescription.setHint("Event Details");
                        gatheringDescription.setHintTextColor(Color.BLACK);
                    }

                    if (summaryObj.has("eventMembers") && !summaryObj.isNull("eventMembers") && summaryObj.getJSONArray("eventMembers").length() > 0) {
                        inviteFriendsImageList = new HashSet<>();
                        for (int i = 0; i < summaryObj.getJSONArray("eventMembers").length(); i++) {
                            JSONObject frndObj = summaryObj.getJSONArray("eventMembers").getJSONObject(i);
                            System.out.println(frndObj.toString());
                            Map<String, String> invFrnMap = new HashMap<>();
                            invFrnMap.put("name", !CenesUtils.isEmpty(frndObj.getString("name")) ? frndObj.getString("name") : "");
                            invFrnMap.put("photo", frndObj.getString("picture"));
                            invFrnMap.put("userId", String.valueOf(frndObj.get("userId")));
                            invFrnMap.put("status", frndObj.getString("status"));
                            inviteFriendsImageList.add(invFrnMap);
                        }

                        if (inviteFriendsImageList.size() > 0) {
                            recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);
                            recyclerView.setVisibility(View.VISIBLE);

                            ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                            mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mFriendsAdapter);
                        }
                    }

                    String predictiveCalStatus = "OFF";
                    if (summaryObj.has("isPredictiveOn") && (summaryObj.getBoolean("isPredictiveOn") || summaryObj.getBoolean("isPredictiveOn") == true)) {
                        predictiveCalStatus = "ON";
                        predictiveCalSwitch.setChecked(true);

                        JSONArray predictiveData = null;
                        if (summaryObj.has("predictiveData")) {
                            predictiveData = new JSONArray(summaryObj.getString("predictiveData"));
                        } else {
                            predictiveData = new JSONArray(getActivity().getIntent().getStringExtra("predictiveData"));
                        }
                        predictiveClanedarAPIData = predictiveData;
                        Map<String, Set<CalendarDay>> colorCalendarDayMap = GatheringService.parsePredictiveData(predictiveData);

                        //Map<String,Set<CalendarDay>> colorCalendarDayMap = (Map<String, Set<CalendarDay>>) summaryObj.getJSONObject("predictive_data");
                        for (Map.Entry<String, Set<CalendarDay>> calEntrySet : colorCalendarDayMap.entrySet()) {

                            Set<CalendarDay> colorSet = null;
                            if (calEntrySet.getKey().equals("RED")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_red_color, calEntrySet.getValue(), true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("ORANGE")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_orange_color, calEntrySet.getValue(), true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("YELLOW")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_yellow_color, calEntrySet.getValue(), true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("LIME")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_lime_color, calEntrySet.getValue(), true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            } else if (calEntrySet.getKey().equals("GREEN")) {
                                BackgroundDecorator calBgDecorator = new BackgroundDecorator(getActivity(), R.drawable.mcv_green_color, calEntrySet.getValue(), true);
                                materialCalendarView.addDecorator(calBgDecorator);
                            }
                        }
                    }

                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(summaryObj.getLong("startTime"));
                    predictedDateStartCal.setTimeInMillis(summaryObj.getLong("startTime"));
                    materialCalendarView.setCurrentDate(predictedDateStartCal.getTime());

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTimeInMillis(summaryObj.getLong("endTime"));
                    predictedDateEndCal.setTimeInMillis(summaryObj.getLong("endTime"));

                    gath_date_after_fix.setText(CenesUtils.MMMdd.format(predictedDateStartCal.getTime()) + " at " + CenesUtils.hhmmaa.format(startTime.getTime()) + " to " + CenesUtils.hhmmaa.format(endTime.getTime()));

                    startTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateStartCal.getTime()));
                    endTimePickerLabel.setText(CenesUtils.hhmmaa.format(predictedDateEndCal.getTime()));

                    fragmentView.findViewById(R.id.suggested_date_time_label).setVisibility(View.VISIBLE);
                    eventTime.setVisibility(View.VISIBLE);
                    eventTime.setText(CenesUtils.MMMMddy.format(startTime.getTime()) + CenesUtils.getDateSuffix(startTime.get(Calendar.DAY_OF_MONTH)) + "," + startTime.get(Calendar.YEAR));

                    event_time_am_pm.setVisibility(View.VISIBLE);
                    event_time_am_pm.setText(CenesUtils.hhmmaa.format(startTime.getTime()) + " to " + CenesUtils.hhmmaa.format(endTime.getTime()));
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
            createEventObj.put("title", gathEventTitleEditView.getText().toString());
            if (!gathSearchLocationButton.getText().toString().equals("Location")) {
                createEventObj.put("location", gathSearchLocationButton.getText().toString());
            }
            if (gathSearchLocationButton.getText().toString().trim().length() > 0) {
                createEventObj.put("latitude", latitude);
                createEventObj.put("longitude", longitude);
            }

            if (!CenesUtils.isEmpty(gatheringDescription.getText().toString())) {
                createEventObj.put("description", gatheringDescription.getText().toString());
            }
            createEventObj.put("createdById", user.getUserId());
            createEventObj.put("source", "Cenes");
            createEventObj.put("timezone", predictedDateStartCal.getTimeZone().getID());
            createEventObj.put("scheduleAs", "Gathering");
            createEventObj.put("startTime", predictedDateStartCal.getTimeInMillis());
            createEventObj.put("endTime", predictedDateEndCal.getTimeInMillis());
            if (eventPictureObj != null) {
                createEventObj.put("eventPicture", eventPictureObj.getString("eventPicture"));
            } else if (oldImageUrl != "") {
                createEventObj.put("eventPicture", oldImageUrl);
            }
            createEventObj.put("isPredictiveOn", isPredictiveOn);
            if (isPredictiveOn) {
                createEventObj.put("predictiveData", predictiveClanedarAPIData.toString()); // getText() SHOULD NOT be static!!!
            }

            JSONArray friendsArray = new JSONArray();
            if (!isEditMode) {
                JSONObject userObj = new JSONObject();
                userObj.put("name", !CenesUtils.isEmpty(user.getName()) ? user.getName() : "");
                userObj.put("picture", user.getPicture());
                userObj.put("userId", user.getUserId());
                userObj.put("source", "Cenes");
                userObj.put("status", "Going");
                friendsArray.put(userObj);
            }

            if (inviteFriendsImageList.size() > 0) {
                for (Map<String, String> friend : inviteFriendsImageList) {

                    JSONObject frindObj = new JSONObject();
                    frindObj.put("name", friend.get("name"));
                    frindObj.put("picture", friend.get("photo"));
                    frindObj.put("userId", friend.get("userId"));
                    frindObj.put("source", "Cenes");
                    if (user.getUserId() == Integer.valueOf(friend.get("userId"))) {
                        frindObj.put("status", "Going");
                    } else {
                        if (!CenesUtils.isEmpty(friend.get("status"))) {
                            frindObj.put("status", friend.get("status"));
                        }
                    }
                    friendsArray.put(frindObj);
                }
            }
            createEventObj.put("eventMembers", friendsArray);
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
        startTimePickerLabelLayout.setClickable(false);
        //endDatePickerLabel.setClickable(false);
        //endTimePickerLabel.setClickable(false);
        endTimePickerLabelLayout.setClickable(false);
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
        startTimePickerLabelLayout.setClickable(true);
        //endDatePickerLabel.setClickable(true);
        //endTimePickerLabel.setClickable(true);
        endTimePickerLabelLayout.setClickable(true);
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
}
