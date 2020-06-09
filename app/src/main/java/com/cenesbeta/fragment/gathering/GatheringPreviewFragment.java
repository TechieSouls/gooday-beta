package com.cenesbeta.fragment.gathering;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.EventChatBaseAdapter;
import com.cenesbeta.adapter.LocationPhotoRecyclerViewAdapter;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventChat;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.LocationPhoto;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.impl.EventMemberManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.GatheringPreviewDto;
import com.cenesbeta.dto.SelectedEventChatDto;
import com.cenesbeta.extension.InvitationScrollView;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.service.ResizeAnimation;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatheringPreviewFragment extends CenesFragment {

    private static String TAG = "GatheringPreviewFragment";
    private int SMS_COMPOSE_RESULT_CODE = 1001;

    private View fragmentView;
    private ImageView ivEventPicture, ivAcceptSendIcon, ivEditRejectIcon, ivDeleteIcon, ivCardSwipeArrow;
    private TextView tvEventTitle, tvEventDate, tvEventDescriptionDialogText, sendChatTV, enterChatTv;
    private RelativeLayout rlGuestListBubble, rlLocationBubble, rlDescriptionBubble, rlShareBubble;
    private RelativeLayout rvEventDescriptionDialog, rlIncludeChat, rlChatt;
    private RelativeLayout rlDescriptionBubbleBackground, ivEventPictureOverlay;
    private ImageView ivDescriptionBubbleIcon;
    private RoundedImageView ivDescProfilePic,enterMsgPicture;
    private CardView tinderCardView;
    private RelativeLayout rlParentVew, rlSkipText, rlChatBubble, rlEnterChat, rlChatlistAndBubble;
    private RelativeLayout rlInvitationView, rlWelcomeInvitation;
    private RoundedImageView ivProfilePicView;
    private ImageView invitationAcceptSpinner, invitationRejectSpinner, enterChatImageView;
    private LinearLayout llBottomButtons, llEventDetails, llInvitationFooter, llInvitationImageLayout, llSenderPicture;
    private InvitationScrollView svCard;
    private ProgressBar progressBar;

    //Layout ChatView Components
    private RelativeLayout rlLocationAlertBackdrop, rlIncludeLocationAlertView;
    private TextView tvLocation;
    private Button btnGetDirections;
    private RecyclerView rvLocationPlaces;

    public Fragment sourceFragment;
    private CenesApplication cenesApplication;
    private InternetManager internetManager;
    private EventManagerImpl eventManagerImpl;
    public User loggedInUser;
    public Event event;
    public List<Event> pendingEvents;
    public List<EventChat> eventChats;
    private EventChatBaseAdapter eventChatBaseAdapter;
    private ListView lvChatListView;
    private List<String> headers;
    private Map<String, List<EventChat>> eventChatMapList;
    private List<Object> eventChatList;


    private EventMember eventOwner, loggedInUserAsEventMember;
    private boolean enableLeftToRightSwipe, enableRightToLeftSwipe;
    private boolean isNewOrEditMode;
    private boolean isPushRequest = false;
    private int pendingEventIndex;
    private boolean isKeyboardVisible = false;
    int windowWidth, windowHeight;
    int screenCenter;
    boolean isLoggedInUserExistsInMemberList = false;
    private List<EventMember> nonCenesMember;
    private boolean isNewEvent = false;
    private boolean isChatLoaded = false;
    private boolean isDescriptionButtonOn = false;
    private int mActivePointerId;
    private int parentWidth, initialX;
    private float ROTATION_DEGREES = 20f;
    private float MAX_CARD_EXTENT = 200f;
    private float CARD_SWIPE_DELAY = 20f;

    private boolean click = true;
    private float initialXPress;
    private float initialYPress;
    public SelectedEventChatDto selectedEventChatDto;
    public List<LocationPhoto> locationPhotos;
    private LocationPhotoRecyclerViewAdapter locationPhotoRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (fragmentView != null) {
            return fragmentView;
        }
        View view = inflater.inflate(R.layout.fragment_gathering_preview, container, false);
        fragmentView = view;
        ivProfilePicView = (RoundedImageView) view.findViewById(R.id.iv_profile_pic);
        enterMsgPicture = (RoundedImageView) view.findViewById(R.id.enter_msg_picture);
        ivEventPicture = (ImageView) view.findViewById(R.id.iv_event_picture);
        tvEventTitle = (TextView) view.findViewById(R.id.tv_event_title);
        tvEventDate = (TextView) view.findViewById(R.id.tv_event_date);
        tvEventDescriptionDialogText = (TextView) view.findViewById(R.id.tv_event_description_dialog_text);

        rlGuestListBubble = (RelativeLayout) view.findViewById(R.id.rl_guest_list_bubble);
        rlLocationBubble = (RelativeLayout) view.findViewById(R.id.rl_location_bubble);
        rlDescriptionBubble = (RelativeLayout) view.findViewById(R.id.rl_description_bubble);
        rlShareBubble = (RelativeLayout) view.findViewById(R.id.rl_share_bubble);
        rlChatBubble = (RelativeLayout) view.findViewById(R.id.rl_chat_bubble);
        llSenderPicture = (LinearLayout) view.findViewById(R.id.ll_sender_picture);
        rlEnterChat = (RelativeLayout) view.findViewById(R.id.rl_enter_chat);
        rlChatt = (RelativeLayout) view.findViewById(R.id.rl_chat);
        rlChatlistAndBubble = (RelativeLayout) view.findViewById(R.id.rl_chatlist_and_bubble);

        rlSkipText = (RelativeLayout) view.findViewById(R.id.rl_skip_text);
        rvEventDescriptionDialog = (RelativeLayout) view.findViewById(R.id.rv_event_description_dialog);
        rlDescriptionBubbleBackground = (RelativeLayout) view.findViewById(R.id.rl_description_bubble_background);
        rlIncludeChat = (RelativeLayout) view.findViewById(R.id.rl_include_chat);
        lvChatListView = (ListView) view.findViewById(R.id.lv_chat_listView);
        ivDescriptionBubbleIcon = (ImageView) view.findViewById(R.id.iv_description_bubble_icon);
        ivDescProfilePic = (RoundedImageView) view.findViewById(R.id.iv_desc_profile_pic);

        ivAcceptSendIcon = (ImageView) view.findViewById(R.id.iv_accept_icon);
        ivEditRejectIcon = (ImageView) view.findViewById(R.id.iv_edit_reject_icon);
        ivDeleteIcon = (ImageView) view.findViewById(R.id.iv_delete_icon);
        invitationAcceptSpinner = (ImageView) view.findViewById(R.id.iv_invitation_accept_spinner);
        invitationRejectSpinner = (ImageView) view.findViewById(R.id.iv_invitation_decline_spinner);
        ivCardSwipeArrow = (ImageView) view.findViewById(R.id.iv_card_swipe_arrow);

        tinderCardView = (CardView) view.findViewById(R.id.tinderCardView);
        svCard = (InvitationScrollView) view.findViewById(R.id.sv_card);
        rlParentVew = (RelativeLayout) view.findViewById(R.id.rl_parent_vew);
        rlWelcomeInvitation = (RelativeLayout) view.findViewById(R.id.rl_welcome_invitation);
        rlInvitationView = (RelativeLayout) view.findViewById(R.id.rl_invitation_view);
        ivEventPictureOverlay = (RelativeLayout) view.findViewById(R.id.iv_event_picture_overlay);

        //Location Alert View Components
        rlIncludeLocationAlertView = (RelativeLayout) view.findViewById(R.id.rl_include_location_alert_view);
        rlLocationAlertBackdrop = (RelativeLayout) view.findViewById(R.id.rl_location_alert_backdrop);
        tvLocation = (TextView) view.findViewById(R.id.tv_location);
        btnGetDirections = (Button) view.findViewById(R.id.btn_get_directions);
        rvLocationPlaces = (RecyclerView) view.findViewById(R.id.rv_location_photos);

        llBottomButtons = (LinearLayout) view.findViewById(R.id.ll_bottom_buttons);
        llEventDetails = (LinearLayout) view.findViewById(R.id.ll_event_details);
        llInvitationFooter = (LinearLayout) view.findViewById(R.id.ll_invitation_footer);
        llInvitationImageLayout = (LinearLayout) view.findViewById(R.id.ll_invitation_image_layout);

        sendChatTV = (TextView) view.findViewById(R.id.send_chat_tv);
        enterChatTv = (TextView) view.findViewById(R.id.enter_chat_tv);
        enterChatImageView = (ImageView) view.findViewById(R.id.enter_chat_button);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        ivCardSwipeArrow.setOnClickListener(onClickListener);
        rlGuestListBubble.setOnClickListener(onClickListener);
        rlLocationBubble.setOnClickListener(onClickListener);
        rlDescriptionBubble.setOnClickListener(onClickListener);
        rlShareBubble.setOnClickListener(onClickListener);
        ivDeleteIcon.setOnClickListener(onClickListener);
        ivEditRejectIcon.setOnClickListener(onClickListener);
        sendChatTV.setOnClickListener(onClickListener);
        tinderCardView.setOnTouchListener(onTouchListener);
        svCard.setOnTouchListener(onTouchListener);
        enterChatTv.setOnFocusChangeListener(onFocusChangeListener);
        enterChatImageView.setOnClickListener(onClickListener);
        rlLocationAlertBackdrop.setOnClickListener(onClickListener);
        btnGetDirections.setOnClickListener(onClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            svCard.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    System.out.println("scrollX - scrollY , oldScrollX - oldScrollY, "+ scrollX+" - "+scrollY+", "+oldScrollX+" - "+oldScrollY);
                    if (scrollY == 0) {
                        svCard.setOnTouchListener(onTouchListener);
                    } else {
                        tinderCardView.setX(0);
                        tinderCardView.setRotation(0);
                    }

                }
            });
        }

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();
        loggedInUser = userManager.getUser();
        eventManagerImpl = new EventManagerImpl(cenesApplication);

        new GatheringPreviewDto();
        locationPhotos = new ArrayList<>();

        windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        screenCenter = windowWidth / 2;

        invitationAcceptSpinner.setVisibility(View.GONE);
        invitationRejectSpinner.setVisibility(View.GONE);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        RelativeLayout.LayoutParams bubbleBarParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(290), CenesUtils.dpToPx(50));
        bubbleBarParams.setMargins(0    ,metrics.heightPixels - CenesUtils.dpToPx(100), 0, 0);
        bubbleBarParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llBottomButtons.setLayoutParams(bubbleBarParams);

        RelativeLayout.LayoutParams rvEventDescriptionDialogParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(400));
        rvEventDescriptionDialogParams.setMargins(CenesUtils.dpToPx(30)    ,metrics.heightPixels - CenesUtils.dpToPx(550), CenesUtils.dpToPx(30), 0);
        rvEventDescriptionDialogParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rvEventDescriptionDialog.setLayoutParams(rvEventDescriptionDialogParams);


        ivEventPicture.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(getActivity().getWindowManager().getDefaultDisplay().getHeight())));
        ivEventPictureOverlay.getLayoutParams().height = metrics.heightPixels;
        ivEventPicture.getLayoutParams().height = metrics.heightPixels;
        if (pendingEvents != null && pendingEvents.size() > 0) {
            event =  pendingEvents.get(0);
            pendingEventIndex++;
        }

        populateInvitationCard(event);
        if (event.getScheduleAs() != null && event.getScheduleAs().equals("Notification")) {

            populateInvitationCard(event);

        } else if (event != null && event.getEventId() != null && event.getEventId() != 0) {
            rlInvitationView.setVisibility(View.VISIBLE);
            rlWelcomeInvitation.setVisibility(View.GONE);

            populateInvitationCard(event);
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setQueryStr("eventId="+event.getEventId()+"&userId="+loggedInUser.getUserId()+"");
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+GatheringAPI.get_gathering_detail);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {

                            updateEventFromServer(response);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } else if (event.getKey() != null) {
            rlInvitationView.setVisibility(View.VISIBLE);
            rlWelcomeInvitation.setVisibility(View.GONE);

            populateInvitationCard(event);
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setQueryStr("key="+event.getKey()+"&userId="+loggedInUser.getUserId()+"");
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+GatheringAPI.get_gathering_detail);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());

            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {

                            updateEventFromServer(response);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        }

        if (event != null) {

            if (event.getEventId() == null || event.getEventId().equals(0l)) {
                rlShareBubble.setVisibility(View.GONE);
            } else {

                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                try {
                    JSONObject props = new JSONObject();
                    props.put("Action","View Invitation Card");
                    props.put("Title",event.getTitle());
                    props.put("UserEmail",loggedInUser.getEmail());
                    props.put("UserName",loggedInUser.getName());
                    mixpanel.track("Invitation", props);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ivProfilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CenesBaseActivity)getActivity()).zoomImageFromThumb(ivProfilePicView,eventOwner.getUser().getPicture() );

            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        parentWidth = width;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rlChatlistAndBubble.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, getActivity().getWindowManager().getDefaultDisplay().getHeight() - (CenesUtils.dpToPx(450) + CenesUtils.dpToPx(110)));
        return view;
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mChatMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("Broadcaster Processing");
            isPushRequest = true;
            getChatThread();

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mChatMessageReceiver, new IntentFilter("eventchatrefresh"));
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(mChatMessageReceiver);
        } catch (Exception e){

        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_guest_list_bubble:

                    GatheringGuestListFragment gatheringGuestListFragment = new GatheringGuestListFragment();
                    gatheringGuestListFragment.event = event;
                    ((CenesBaseActivity)getActivity()).replaceFragment(gatheringGuestListFragment, GatheringPreviewFragment.TAG);
                    break;
                case R.id.rl_location_bubble:

                    hideDescriptionMessage();

                    if (!CenesUtils.isEmpty(event.getLatitude())) {

                        if (locationPhotos.size() > 0) {

                            rlIncludeLocationAlertView.setVisibility(View.VISIBLE);

                            tvLocation.setText(event.getLocation());

                            locationPhotoRecyclerViewAdapter = new LocationPhotoRecyclerViewAdapter(GatheringPreviewFragment.this, locationPhotos);
                            locationPhotoRecyclerViewAdapter.notifyDataSetChanged();
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            rvLocationPlaces.setLayoutManager(mLayoutManager);
                            rvLocationPlaces.setAdapter(locationPhotoRecyclerViewAdapter);
                            rvLocationPlaces.invalidate();

                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("")
                                    .setMessage(event.getLocation())
                                    .setPositiveButton("Get Directions", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Create a Uri from an intent string. Use the result to create an Intent.
                                            //  Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+event.getLatitude()+","+event.getLongitude()+"");
                                            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+event.getLatitude()+","+event.getLongitude()+"(label)");
                                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            // Make the Intent explicit by setting the Google Maps package
                                            mapIntent.setPackage("com.google.android.apps.maps");

                                            // Attempt to start an activity that can handle the Intent
                                            startActivity(mapIntent);

                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }

                    } else  if (!CenesUtils.isEmpty(event.getLocation())) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(event.getLocation())
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    } else {

                        new AlertDialog.Builder(getActivity())
                                .setTitle("No Location Selected")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    }
                    break;

                case R.id.rl_description_bubble:
                    System.out.println("rl_description_bubble");
                    if(isDescriptionButtonOn == false) {

                        isDescriptionButtonOn = true;
                        scrollFeatureOff();
                        ivDescriptionBubbleIcon.setImageResource(R.drawable.message_on_icon);
                        rlDescriptionBubbleBackground.setAlpha(1);
                        rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_white));

                    }else {
                        isDescriptionButtonOn = false;
                        scrollFeatureOn();
                        rlDescriptionBubbleBackground.setAlpha(0.3f);
                        ivDescriptionBubbleIcon.setImageResource(R.drawable.message_off_icon);
                        rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_black_faded));

                    }

                    if (isChatLoaded == false && event.getEventId() != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {

                        loadDescriptionData();

                    }

                    break;
                case R.id.rl_share_bubble:

                    String name = "Your Friend";
                    if (eventOwner.getName() != null) {
                        name = eventOwner.getName();
                    } else if (eventOwner.getUser() != null && eventOwner.getUser().getName() != null) {
                        name = eventOwner.getUser().getName();
                    }
                    String shrareUrl = name+" invites you to "+event.getTitle()+". RSVP through the Cenes app. Link below: \n";
                    shrareUrl = shrareUrl + CenesConstants.webDomainEventUrl+""+event.getKey();


                    if (event.getEventId() != null) {

                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                        try {
                            JSONObject props = new JSONObject();
                            props.put("Action","Share Invitation");
                            props.put("Title",event.getTitle());
                            props.put("UserEmail",loggedInUser.getEmail());
                            props.put("UserName",loggedInUser.getName());
                            mixpanel.track("Invitation", props);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shrareUrl);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Event cannot be shared this time.")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    }

                    break;

                case R.id.iv_delete_icon:

                    deleteGathering(event);
                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                    break;

                case R.id.iv_edit_reject_icon:

                    try {
                        event.setEditMode(true);
                        //fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
                        CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                        createGatheringFragment.event = event;
                        ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case R.id.iv_card_swipe_arrow:

                    //fragmentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);

                    if ((GatheringPreviewFragment.this).event.getScheduleAs() != null && (GatheringPreviewFragment.this).event.getScheduleAs().equals("Notification")) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity() != null) {
                                    ((CenesBaseActivity) getActivity()).onBackPressed();
                                }
                            }
                        }, 500);
                    } else {

                        if (!isLoggedInUserExistsInMemberList) {

                            if (GatheringPreviewFragment.this.event != null && GatheringPreviewFragment.this.event.getEventId() != null && loggedInUser.getUserId() != null) {

                                String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=pending";
                                updateAttendingStatus(queryStr);

                            }

                        }

                        if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                            (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                            pendingEventIndex++;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    GatheringPreviewDto.cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);

                        } else {
                            ((CenesBaseActivity) getActivity()).onBackPressed();
                        }
                    }

                    break;

                case  R.id.send_chat_tv :
                    rlChatBubble.setVisibility(View.GONE);
                    llSenderPicture.setVisibility(View.GONE);
                    rlEnterChat.setVisibility(View.VISIBLE);

                    System.out.println("rl enter chat width : "+rlEnterChat.getLayoutParams().width);
                    ResizeAnimation resizeAnimation = new ResizeAnimation(rlEnterChat, windowWidth);
                    resizeAnimation.setDuration(500);
                    rlEnterChat.startAnimation(resizeAnimation);
                    enterChatTv.setHint("Type a message");



                    /*rlEnterChat.animate()
                            .rotationX(10.0f)
                           // .translationY(rlEnterChat.getHeight())
                            .alpha(1f)
                            .setDuration(3000)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    rlEnterChat.setVisibility(View.VISIBLE);
                                }
                            }); */

                    Handler handler = new Handler(); handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        enterChatTv.requestFocus();
                        //enterChatTv.setFocusableInTouchMode(true);
                        //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                        //imm.showSoftInput(enterChatTv, InputMethodManager.SHOW_FORCED);

                    }

                }, 1500);


                    break;
                case  R.id.enter_chat_button :

                    if (!internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
                        showAlert("Error", "Network Connection Error");
                        return;
                    }
                    System.out.println("typing message...");
                    String chatMessage = enterChatTv.getText().toString();
                    enterChatTv.setText("");
                    if (chatMessage != null && chatMessage.length() > 0) {
                        postEventChat(chatMessage);

                    }
                    break;

                case R.id.btn_get_directions:
                    rlIncludeLocationAlertView.setVisibility(View.GONE);

                    //  Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+event.getLatitude()+","+event.getLongitude()+"");
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+event.getLatitude()+","+event.getLongitude()+"(label)");
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");
                    // Attempt to start an activity that can handle the Intent
                    startActivity(mapIntent);

                    break;

                case R.id.rl_location_alert_backdrop:

                    rlIncludeLocationAlertView.setVisibility(View.GONE);

                    break;
                default:
                    System.out.println("Heyyy you did it.");
            }
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    click = true;
                    //gesture has begun
                    float x;
                    float y;
                    //cancel any current animations
                    //v.clearAnimation();

                    mActivePointerId = event.getPointerId(0);

                    x = event.getX();
                    y = event.getY();

                    if(event.findPointerIndex(mActivePointerId) == 0) {
                        //callback.cardActionDown();
                    }

                    initialXPress = x;
                    initialYPress = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    //gesture is in progress

                    Log.e("OnMove : ", "On MOve Called.");
                    final int pointerIndex = event.findPointerIndex(mActivePointerId);
                    Log.i("pointer index: " , Integer.toString(pointerIndex));
                    if(pointerIndex < 0 || pointerIndex > 0 ){
                        break;
                    }

                    final float xMove = event.getX(pointerIndex);
                    final float yMove = event.getY(pointerIndex);
                    Log.i("xMove, : yMove" , xMove+"  -  "+yMove);

                    //calculate distance moved
                    final float dx = xMove - initialXPress;
                    final float dy = yMove - initialYPress;
                    Log.i("initialPress: " , initialXPress+"  -  "+initialYPress);

                    //throw away the move in this case as it seems to be wrong
                    //TODO: figure out why this is the case
                    if((int)initialXPress == 0 && (int) initialYPress == 0){
                        //makes sure the pointer is valid
                        break;
                    }
                    //calc rotation here
                    Log.i("dx - dy : " , dx+", "+dy);

                    float posX = (tinderCardView.getX() + dx) * 0.4f;
                    float posY = tinderCardView.getY() + dy;
                    float distobjectX = posX - initialX;
                    float rotation = ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                    Log.i("Rotation: " , rotation+"");

                    //in this circumstance consider the motion a click
                    if (Math.abs(dx + dy) > 5) click = false;
                    Log.e("X Position : ",posX+" ---------- "+posY);


                    if (Math.abs(posX) < 10 && posY < -40) {
                        //tinderCardView.setY(posY);
                        svCard.setOnTouchListener(null);
                        break;
                    } else {
                        if (Math.abs(posX) > 30) {
                            svCard.scrollTo(0, 0);
                            svCard.setOnTouchListener(onTouchListener);
                        }
                    }

                    if (enableLeftToRightSwipe && enableRightToLeftSwipe) {
                        System.out.println("[ACTION_MOVE] -- INSIDE BOTH WAY SWIPE");

                        if (Math.abs(posX - CARD_SWIPE_DELAY) < MAX_CARD_EXTENT && !GatheringPreviewDto.cardSwipedToExtent) {
                            if (posX + CARD_SWIPE_DELAY < 0) {
                                tinderCardView.setX(posX + CARD_SWIPE_DELAY);
                            } else {
                                tinderCardView.setX(posX - CARD_SWIPE_DELAY);
                            }
                            tinderCardView.setRotation(rotation);
                        } else {

                            GatheringPreviewDto.cardSwipedToExtent = true;
                            if (posX < 0) {
                                tinderCardView.setX(-MAX_CARD_EXTENT);
                                tinderCardView.setRotation(-ROTATION_DEGREES);
                            } else {
                                tinderCardView.setX(MAX_CARD_EXTENT);
                                tinderCardView.setRotation(ROTATION_DEGREES);
                            }
                        }

                        if (posX + CARD_SWIPE_DELAY < 0) {
                            GatheringPreviewDto.ifSwipedRightToLeft = true;
                        } else if (posX - CARD_SWIPE_DELAY > 0) {
                            GatheringPreviewDto.ifSwipedLeftToRight = true;
                        }
                    } else  if (!enableLeftToRightSwipe && enableRightToLeftSwipe) {
                        System.out.println("[INSIDE RIGHT TO LEFT SWIPE] : Card Swiped Extent : " + GatheringPreviewDto.cardSwipedToExtent + "," +
                                "Card Closed Cliked : " + GatheringPreviewDto.userClickedToCloseCard);
                        GatheringPreviewDto.ifSwipedRightToLeft = true;

                        if (GatheringPreviewDto.cardSwipedToExtent == false) {
                            if (posX + CARD_SWIPE_DELAY < 0) {

                                Log.e("Alpha  ", posX+"+"+CenesUtils.dpToPx(20)+"="+(Math.abs(posX) - CenesUtils.dpToPx(20))+"/"+parentWidth);
                                //ivEditRejectIcon.setAlpha((float) ((Math.abs(posX) + CenesUtils.dpToPx(20))/(parentWidth)));
                                if (posX > -MAX_CARD_EXTENT) {
                                    Log.d("Right To Left Sipe : ", (posX + CARD_SWIPE_DELAY) + "  :   " + (posY));
                                    tinderCardView.setX(posX + CARD_SWIPE_DELAY);
                                    tinderCardView.setRotation(rotation);
                                    GatheringPreviewDto.ifSwipedRightToLeft = true;

                                } else {

                                    tinderCardView.setX(-MAX_CARD_EXTENT);
                                    tinderCardView.setRotation(-ROTATION_DEGREES);
                                    GatheringPreviewDto.cardSwipedToExtent = true;
                                }
                            }
                        }

                    }  else if (enableLeftToRightSwipe && !enableRightToLeftSwipe) {

                        System.out.println("[ACTION_MOVE] -- INSIDE LEFT TO RIGHT SWIPE");
                        GatheringPreviewDto.ifSwipedLeftToRight = true;
                        if (GatheringPreviewDto.cardSwipedToExtent == false) {
                            if (posX - CARD_SWIPE_DELAY > 0) {
                                if (posX < MAX_CARD_EXTENT ) {

                                    Log.d("Left To Right Swipe : ",(posX - CARD_SWIPE_DELAY)+"  :   "+(posY));
                                    tinderCardView.setX(posX - CARD_SWIPE_DELAY);
                                    tinderCardView.setRotation(rotation);
                                    GatheringPreviewDto.ifSwipedLeftToRight = true;
                                } else {
                                    tinderCardView.setX(MAX_CARD_EXTENT);
                                    tinderCardView.setRotation(ROTATION_DEGREES);
                                    GatheringPreviewDto.cardSwipedToExtent = true;
                                }
                            }
                        }

                    }
                    /*if (rightView != null && leftView != null){
                        //set alpha of left and right image
                        float alpha = (((posX - paddingLeft) / (parentWidth * OPACITY_END)));
                        //float alpha = (((posX - paddingLeft) / parentWidth) * ALPHA_MAGNITUDE );
                        //Log.i("alpha: ", Float.toString(alpha));
                        rightView.setAlpha(alpha);
                        leftView.setAlpha(-alpha);
                    }*/

                    break;

                case MotionEvent.ACTION_UP:
                    //gesture has finished
                    //check to see if card has moved beyond the left or right bounds or reset
                    //card position
                    Log.e("ACTION_UP", GatheringPreviewDto.cardSwipedToExtent+"");
                    Log.e("ifSwipedRightToLeft : ", GatheringPreviewDto.ifSwipedRightToLeft+"");
                    Log.e("ifSwipedLeftToRight : ", GatheringPreviewDto.ifSwipedRightToLeft+"");

                    svCard.setOnClickListener(null);
                    if (GatheringPreviewDto.cardSwipedToExtent) {

                        svCard.scrollTo(0, 0);
                        svCard.setOnTouchListener(onTouchListener);
                        GatheringPreviewDto.cardSwipedToExtent = false;
                        if (GatheringPreviewDto.ifSwipedRightToLeft) {
                            GatheringPreviewDto.ifSwipedRightToLeft = false;
                            GatheringPreviewDto.ifSwipedLeftToRight = false;

                            System.out.println("[ACTION_UP] : Card was swiped to left");
                            System.out.println("[ACTION_UP] : User Tap Finger of Card");
                            System.out.println("[ACTION_UP] : Rotatoin : "+tinderCardView.getRotation());

                            tinderCardView.setRotation(-ROTATION_DEGREES);
                            tinderCardView.setX(-MAX_CARD_EXTENT);

                            if (!isNewOrEditMode) {
                                if (pendingEvents != null && pendingEvents.size() > 0) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            resetCardPosition();
                                            populateInvitationCard((GatheringPreviewFragment.this).event);
                                        }
                                    }, 500);
                                } else {
                                    rejectGatheringConfirmAlert();
                                }
                            }
                        } else if (GatheringPreviewDto.ifSwipedLeftToRight) {
                            GatheringPreviewDto.ifSwipedLeftToRight = false;
                            GatheringPreviewDto.ifSwipedRightToLeft = false;

                            System.out.println("[ACTION_UP] : Card was swiped to Right");

                            tinderCardView.setRotation(ROTATION_DEGREES);
                            tinderCardView.setX(MAX_CARD_EXTENT);

                            if (!isNewOrEditMode) {

                                if (pendingEvents != null && pendingEvents.size() > 0) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            resetCardPosition();
                                            populateInvitationCard((GatheringPreviewFragment.this).event);
                                        }
                                    }, 500);
                                } else {
                                    acceptGathering();
                                }
                            } else {
                                createUpdateGathering();
                            }
                        }


                    } else {
                        resetCardPosition();
                        if (click) v.performClick();
                        if(click) return false;
                    }

                    //checkCardForEvent();

                    if(event.findPointerIndex(mActivePointerId) == 0) {
                        //callback.cardActionUp();
                    }
                    //check if this is a click event and then perform a click
                    //this is a workaround, android doesn't play well with multiple listeners




                    break;

                default:
                    return false;
            }
            return true;
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            System.out.println("Has Focus : "+hasFocus);

            if (hasFocus) {
                isKeyboardVisible=true;

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)rlChatlistAndBubble.getLayoutParams();

                int heightDiff = fragmentView.getRootView().getHeight() - fragmentView.getHeight();
                // IF height diff is more then 150, consider keyboard as visible.
                System.out.println("Ha ha ha ha hi Maaaa : "+heightDiff);

                // TODO Auto-generated method stub
                final Rect r = new Rect();
                fragmentView.getWindowVisibleDisplayFrame(r);

                int screenHeight = fragmentView.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);

                //rlEnterChat.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(enterChatTv, WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            } else {

                System.out.println("On Back Button Pressed : ");

                //lost focus
                //RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) rlEnterChat.getLayoutParams();
                //relativeParams.height = PrecastrUtils.convertDpToPx(getContext(), 160);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) rlEnterChat.getLayoutParams();
                relativeParams.width = 0;
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlEnterChat.setLayoutParams(relativeParams);
                rlEnterChat.setVisibility(View.GONE);
                rlChatBubble.setVisibility(View.VISIBLE);
                llSenderPicture.setVisibility(View.VISIBLE);

                hideKeyboard();

            }
        }
    };


    public void hideChatBoxKeyboard() {
        rlEnterChat.setVisibility(View.GONE);
        rlChatBubble.setVisibility(View.VISIBLE);
        llSenderPicture.setVisibility(View.VISIBLE);
        hideKeyboard();
    }
    public void resetCardPosition() {

        tinderCardView.animate().setDuration(200)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .x(0)
                .y(0)
                .rotation(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                svCard.fullScroll(View.FOCUS_UP);
            }
        }, 100);
        GatheringPreviewDto.userClickedToCloseCard = false;
        GatheringPreviewDto.cardSwipedToExtent = false;
        GatheringPreviewDto.ifSwipedLeftToRight = false;
        GatheringPreviewDto.ifSwipedRightToLeft = false;
        GatheringPreviewDto.ifSwipedUp = false;
        GatheringPreviewDto.userCanSwipe = false;
        //tinderCardView.setOnTouchListener(null);
        //svCard.setOnTouchListener(null);
        //svCard.setScrollingEnabled(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tinderCardView.setOnTouchListener(onTouchListener);
                svCard.setOnTouchListener(onTouchListener);
                GatheringPreviewDto.userCanSwipe = true;
            }
        }, 500);
    }

    public void updateEventFromServer(JSONObject response) {

        try {
            Event eventTemp = new Gson().fromJson(response.getString("data"), Event.class);
            eventManagerImpl.updateEvent(eventTemp);

            //Lets refresh the invitation tab and calendar tab here
            //TODO

            System.out.println("Yahi koi : "+eventTemp.getEventMembers().toString());

            if (eventTemp.getCreatedAt() != null) {
                event.setCreatedAt(eventTemp.getCreatedAt());
            } else {
                event.setCreatedAt(new Date().getTime());
            }

            if (!event.isEditMode()) {

                event.setTitle(eventTemp.getTitle());
                event.setStartTime(eventTemp.getStartTime());
                event.setEndTime(eventTemp.getEndTime());
                event.setLocation(eventTemp.getLocation());

                event.setEventPicture(eventTemp.getEventPicture());
                event.setDescription(eventTemp.getDescription());

                System.out.println("Sever Event Member List : "+eventTemp.getEventMembers().size());

                if (eventTemp.getEventMembers() != null && eventTemp.getEventMembers().size() > 0) {

                    List<EventMember> eventMembersExistingInEvent  = event.getEventMembers();
                    List<EventMember> eventMemberList = new ArrayList<>();

                    for (EventMember eventMemFromServer: eventTemp.getEventMembers()) {

                        for (EventMember eveMemFromEdit: eventMembersExistingInEvent) {
                            if (eventMemFromServer.getUserId() != null && eventMemFromServer.getUserId() != 0 && eventMemFromServer.getUserId().equals(eveMemFromEdit.getUserId())) {
                                EventMemberManagerImpl eventMemberManager = new EventMemberManagerImpl(cenesApplication);
                                eventMemberManager.addEventMember(eventMemFromServer);
                                eventMemberList.add(eventMemFromServer);
                                break;
                            }
                        }
                    }

                    //Lets add newly added members
                    for (EventMember oldEventMember: eventMembersExistingInEvent) {
                        if (oldEventMember.getEventMemberId() == null) {
                            eventMemberList.add(oldEventMember);
                        }
                    }

                    System.out.println("New Event Member List : "+eventMemberList.size());
                    event.setEventMembers(eventMemberList);
                }
            }
            event.setThumbnail(eventTemp.getThumbnail());
            populateInvitationCard(event);

            if (event.getDescription() != null) {
                if (selectedEventChatDto != null && selectedEventChatDto.isShowChatWindow()) {
                    selectedEventChatDto.setShowChatWindow(false);
                    rlDescriptionBubble.performClick();
                }
                getChatThread();
            } else {
                isChatLoaded = true;
            }

            if (eventTemp.getPlaceId() != null) {
                fetchLocationPhotos(eventTemp.getPlaceId());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void hideDescriptionMessage() {
        rvEventDescriptionDialog.setVisibility(View.GONE);
        //scrollFeatureOn();
        System.out.println("scroll enabled");
    }


    public void createGathering() {

        //If its the create event requiest, then we will remove event owner from the
        //event members list. Lets server handle the owner as event member
        if (event.getEventId() == null) {
            for (EventMember eventMember: event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    event.getEventMembers().remove(eventMember);
                    break;
                }
            }
        } else if (event.getEventId() != null) {

            //Lets delete this event from home screen.
            ((CenesBaseActivity)getActivity()).homeFragmentV2.addOrRejectEvent(event, "Refresh");
        }

        System.out.println("Going to create Gathering.");
        Gson gson = new Gson();
        try {
            tinderCardView.setOnTouchListener(null);
            svCard.setOnTouchListener(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                svCard.setOnScrollChangeListener(null);
            }
            JSONObject postata = new JSONObject(gson.toJson(event));

            new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
            new GatheringAsyncTask.CreateGatheringTask(new GatheringAsyncTask.CreateGatheringTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        //((CenesBaseActivity) getActivity()).homeScreenReloadBroadcaster();

                        if (response.getBoolean("success") == true) {
                            JSONObject data = response.getJSONObject("data");
                            Event eve = new Gson().fromJson(data.toString(), Event.class);

                            if (isNewEvent) {
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getContext(), CenesUtils.MIXPANEL_TOKEN);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("Action","Create Gathering Success");
                                    props.put("Title",event.getTitle());
                                    props.put("UserEmail",loggedInUser.getEmail());
                                    props.put("UserName",loggedInUser.getName());
                                    mixpanel.track("Gathering", props);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            if (nonCenesMember.size() > 0) {
                                ((CenesBaseActivity) getActivity()).homeFragmentV2.loadHomeScreenData();
                                sendSmsToNonCenesMembers(nonCenesMember, eve);
                                ((CenesBaseActivity) getActivity()).replaceFragment(((CenesBaseActivity) getActivity()).homeFragmentV2, null);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(postata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAttendingStatus(String queryStr) {

        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        new GatheringAsyncTask.UpdateStatusActionTask(new GatheringAsyncTask.UpdateStatusActionTask.AsyncResponse() {
            @Override
            public void processFinish(Boolean response) {

            }
        }).execute(queryStr);
    }

    public void deleteGathering(Event event) {

        /*new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        new GatheringAsyncTask.DeleteGatheringTask(new GatheringAsyncTask.DeleteGatheringTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {
            }
        }).execute(event.getEventId());*/

        ((CenesBaseActivity)getActivity()).homeFragmentV2.addOrRejectEvent(event, "Delete");

    }

    public void createUpdateGathering() {

        try {
            invitationAcceptSpinner.setVisibility(View.VISIBLE);
            rotate(360, invitationAcceptSpinner);

            if (internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {

                createGathering();
                if (nonCenesMember.size() == 0) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            System.out.println("Firing Broadcaster");
                            //((CenesBaseActivity) getActivity()).clearAllFragmentsInBackstack();

                            try {
                                getActivity().unregisterReceiver(((CenesBaseActivity) getActivity()).homeFragmentV2.mMessageReceiver);
                            } catch (Exception e) {

                            }
                            ((CenesBaseActivity) getActivity()).homeFragmentV2.reloadHomeData();
                            ((CenesBaseActivity) getActivity()).replaceFragment(((CenesBaseActivity) getActivity()).homeFragmentV2, null);
                        }
                    }, 2000);
                }
            } else {

                event.setSynced(false);
                if (event.getEventId() == null) {
                    event.setEventId(new Date().getTime());
                }
                ((CenesBaseActivity) getActivity()).homeFragmentV2.addEventLocally(event, "Going");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("Firing Broadcaster");
                        //((CenesBaseActivity) getActivity()).clearAllFragmentsInBackstack();
                        ((CenesBaseActivity) getActivity()).homeFragmentV2.firstTimeLoadData();
                        ((CenesBaseActivity) getActivity()).replaceFragment(((CenesBaseActivity) getActivity()).homeFragmentV2, null);
                    }
                }, 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptGathering() {
        try {
            invitationAcceptSpinner.setVisibility(View.VISIBLE);
            rotate(360, invitationAcceptSpinner);

            if ((GatheringPreviewFragment.this).event.getScheduleAs() != null && (GatheringPreviewFragment.this).event.getScheduleAs().equals("Notification")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null){
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }
                    }
                }, 500);
            } else if (GatheringPreviewFragment.this.event != null && GatheringPreviewFragment.this.event.getEventId() != null && loggedInUser.getUserId() != null) {

                /*String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going";
                updateAttendingStatus(queryStr);*/

                if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                    (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                    pendingEventIndex++;

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((CenesBaseActivity) getActivity()).homeFragmentV2.addOrRejectEvent(event, "Going");
                            //((CenesBaseActivity) getActivity()).homeFragmentV2.loadHomeScreenData();
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }
                    }, 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rejectGathering() {

        try {
            invitationRejectSpinner.setVisibility(View.VISIBLE);
            rotate(360, invitationRejectSpinner);

            if ((GatheringPreviewFragment.this).event.getScheduleAs() != null && (GatheringPreviewFragment.this).event.getScheduleAs().equals("Notification")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null){
                            //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }
                    }
                }, 500);
            } else {
                /*String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing";
                updateAttendingStatus(queryStr);*/

                if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                    (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                    pendingEventIndex++;

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            ((CenesBaseActivity) getActivity()).homeFragmentV2.addOrRejectEvent(event, "NotGoing");
                            //((CenesBaseActivity) getActivity()).homeFragmentV2.loadHomeScreenData();
                            //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }
                    }, 500);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void rejectGatheringConfirmAlert() {

        try {
            new AlertDialog.Builder(getContext()).setCancelable(false)
                    .setTitle("Message")
                    .setMessage("Are you sure to decline this event?")
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            rejectGathering();
                            rejectGatheringSendMessageAlert();

                        }})
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetCardPosition();
                        }
                    }).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rejectGatheringSendMessageAlert() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Decline Event").setMessage("Do you want to leave a note to "+eventOwner.getName());

            // Set up the input
            final EditText input = new EditText(getContext());
            input.setHint(" Write a message");
            FrameLayout container = new FrameLayout(getActivity());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(CenesUtils.dpToPx(20), 0, CenesUtils.dpToPx(20), 0);
            input.setLayoutParams(params);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            container.addView(input);

            builder.setView(container);

            // Set up the buttons
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(input.getText().toString());
                    if (input.getText().toString() != "") {
                        postEventChat(input.getText().toString());

                        rotate(360, invitationRejectSpinner);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                            }
                        }, 1000);
                    } else{
                        showAlert("error","Message cannot be empty");
                    }
                }
            });
            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    resetCardPosition();
                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                }
            });

            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postEventChat(String message) {

        try {
            final EventChat eventChat = new EventChat();
            eventChat.setChat(message);
            eventChat.setEventId(Integer.parseInt(event.getEventId().toString()));
            eventChat.setSenderId(loggedInUser.getUserId());
            eventChat.setCreatedAt(new Date().getTime());
            eventChats.add(eventChat);

            String key = CenesUtils.EEEMMMMdd.format(eventChat.getCreatedAt());

            Calendar previousDateCal = Calendar.getInstance();
            previousDateCal.add(Calendar.DAY_OF_MONTH, -1);

            Calendar currentYear = Calendar.getInstance();
            Calendar chatCalendar = Calendar.getInstance();
            chatCalendar.setTimeInMillis(eventChat.getCreatedAt());

            String yearAppend = "";

            if(currentYear.get(Calendar.YEAR) != chatCalendar.get(Calendar.YEAR)) {
                yearAppend = ", "+chatCalendar.get(Calendar.YEAR);

            }else {
                yearAppend = "";
            }

            if (key != null) {
                if (key.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

                    key = "Today ";

                } else if (key.equals(CenesUtils.EEEMMMMdd.format(previousDateCal.getTime()))) {

                    key = "Yesterday ";
                }
            }

            if(!headers.contains(key)) {
                //System.out.println("Header key  : "+ key);
                headers.add(key + yearAppend);
            }

            List<EventChat> eventChatTemp = null;
            if(eventChatMapList.containsKey(key)) {
                eventChatTemp = eventChatMapList.get(key);
            } else {
                eventChatTemp = new ArrayList<>();
            }

            if (!internetManager.isInternetConnection((CenesBaseActivity)getActivity())) {
                showAlert("Error","Network Connection Error");
                return;
            }
            eventChatTemp.add(eventChat);
            eventChatMapList.put(key,eventChatTemp);

            eventChatList = new ArrayList<>();
            for (String header: headers) {
                eventChatList.add(header);
                List<EventChat> eventChats = eventChatMapList.get(header);
                for (EventChat eventChatTmp: eventChats) {
                    eventChatList.add(eventChatTmp);
                }
            }

            if (eventChatBaseAdapter != null) {
                eventChatBaseAdapter.refreshItems(eventChatList);
            }


            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.post_event_chat_api);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setPostData(new JSONObject(new Gson().toJson(eventChat)));
            System.out.println("gggggggtttttt "+ eventChats.size() +" : "+ headers.size());
            lvChatListView.setSelection(eventChatList.size() -1);

            new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success == true) {
                            //  elvEventChatList.setSelection(eventChats.size() + headers.size());

                            System.out.println("Message sent for chat ....");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postReadChatStatus() {

        try {
            JSONObject postData = new JSONObject();
            postData.put("userId", loggedInUser.getUserId());
            postData.put("eventId", event.getEventId());
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.post_read_event_chat_status);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setPostData(postData);
            new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success == true) {

                            System.out.println("Message read for chat ....");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateInvitationCard(final Event event) {
        tvEventTitle.setText(event.getTitle());

        final String eventDate = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime())) + ", " + CenesUtils.hmmaa.format(new Date(event.getStartTime())).toUpperCase() + "-" + CenesUtils.hmmaa.format(new Date(event.getEndTime())).toUpperCase();
        tvEventDate.setText(eventDate);

        if (event.getScheduleAs() != null && event.getScheduleAs().equals("Notification")) {

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            ///ivEventPicture.setImageDrawable(getResources().getDrawable(R.drawable.welcome_invitation_card));
            RelativeLayout.LayoutParams invitationFooterParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(50));
            invitationFooterParams.setMargins(0    ,metrics.heightPixels - CenesUtils.dpToPx(100), 0, 0);
            invitationFooterParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            llInvitationFooter.setLayoutParams(invitationFooterParams);

            RelativeLayout.LayoutParams invitationImageLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            invitationImageLayoutParams.setMargins(0    ,CenesUtils.dpToPx(170), 0, CenesUtils.dpToPx(150));
            invitationImageLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            llInvitationImageLayout.getLayoutParams().height = metrics.heightPixels - (CenesUtils.dpToPx(170) + CenesUtils.dpToPx(150));
            //llInvitationImageLayout.setLayoutParams(invitationImageLayoutParams);


            rlInvitationView.setVisibility(View.GONE);
            rlWelcomeInvitation.setVisibility(View.VISIBLE);

        } else {
            rlInvitationView.setVisibility(View.VISIBLE);
            rlWelcomeInvitation.setVisibility(View.GONE);

            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                if (getActivity() != null) {

                    Glide.with(getContext())
                            .load(event.getThumbnail())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                                    ivEventPicture.setImageDrawable(resource);

                                    if (getActivity() != null) {
                                        Glide.with(getContext())
                                                .load(event.getEventPicture()).into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                                ivEventPicture.setImageDrawable(resource);
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        }


        if (event.getEventId() != null) {
            if (event.isEditMode()) {
                ivAcceptSendIcon.setImageResource(R.drawable.invitation_send_button);
            } else {
                ivAcceptSendIcon.setImageResource(R.drawable.invitation_accept_button);
            }
        } else {
            isNewEvent = true;
            ivAcceptSendIcon.setImageResource(R.drawable.invitation_send_button);
        }


        if (event.getEventMembers() != null && event.getEventMembers().size() > 0) {

            for (EventMember eventMember: event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    isLoggedInUserExistsInMemberList = true;
                    break;
                }
            }

            if (!isLoggedInUserExistsInMemberList) {
                EventMember eventMember = new EventMember();
                eventMember.setName(loggedInUser.getName());
                eventMember.setUserId(loggedInUser.getUserId());
                eventMember.setUser(loggedInUser);
                event.getEventMembers().add(eventMember);
            }

            for (EventMember eventMember : event.getEventMembers()) {

                if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    eventOwner = eventMember;
                    break;
                }
            }

            for (EventMember eventMember : event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    loggedInUserAsEventMember = eventMember;
                    break;
                }
            }
        }

        //We will only filter thoses messages whose event_member_id not generated yet.
        nonCenesMember = new ArrayList<>();
        for (EventMember eventMember: event.getEventMembers()) {
            if ((eventMember.getUserId() == null || eventMember.getUserId() == 0) && (eventMember.getEventMemberId() == null || eventMember.getEventMemberId() == 0)) {
                nonCenesMember.add(eventMember);
            }
        }

        if (eventOwner != null) {

            if (eventOwner.getUser() != null && !CenesUtils.isEmpty(eventOwner.getUser().getPicture())) {

                if (((CenesBaseActivity)getActivity()) != null) {

                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.drawable.profile_pic_no_image);
                    options.centerCrop();
                    Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(options).into(ivProfilePicView);

                }

            } else {

                ivProfilePicView.setImageResource(R.drawable.profile_pic_no_image);
            }
        }

        if (event.getEventId() == null) {

            enableLeftToRightSwipe = true;
            enableRightToLeftSwipe = true;
            isNewOrEditMode = true;
            ivDeleteIcon.setVisibility(View.GONE);

        } else {

            //If Logged In User is the owner of the event
            if (eventOwner != null && eventOwner.getUserId() != null && eventOwner.getUserId().equals(loggedInUser.getUserId())) {

                isNewOrEditMode = true;
                if (event.isEditMode()) {
                    enableLeftToRightSwipe = true;
                } else {
                    //User cannot accept or send invitation
                    enableLeftToRightSwipe = false;
                }

                //User Can edit or delete the invitation
                enableRightToLeftSwipe = true;
                ivEditRejectIcon.setImageResource(R.drawable.invitation_edit_button);
                ivDeleteIcon.setVisibility(View.VISIBLE);

            } else {

                isNewOrEditMode = false;
                ivEditRejectIcon.setImageResource(R.drawable.invitation_decline_button);
                ivDeleteIcon.setVisibility(View.GONE);

                //If this is the event from somebody. Then user can accept decline that event
                if (loggedInUserAsEventMember != null) {
                    if (CenesUtils.isEmpty(loggedInUserAsEventMember.getStatus())) {
                        enableLeftToRightSwipe = true;
                        enableRightToLeftSwipe = true;
                    } else {
                        if ("Going".equals(loggedInUserAsEventMember.getStatus())) {

                            enableLeftToRightSwipe = false;
                            enableRightToLeftSwipe = true;

                        } else if ("NotGoing".equals(loggedInUserAsEventMember.getStatus())) {


                            enableLeftToRightSwipe = true;
                            enableRightToLeftSwipe = false;

                        }
                    }
                }

            }
        }

        tinderCardView.setX(0);
        tinderCardView.setY(0);
        ivAcceptSendIcon.setVisibility(View.VISIBLE);
        ivEditRejectIcon.setVisibility(View.VISIBLE);


        //Stopping Card from swipe cases
        //Case 1  - Offline
        if (getCenesActivity() != null && event.getEventId() != null && event.getEventId() != 0 && !internetManager.isInternetConnection(getCenesActivity())) {
            enableLeftToRightSwipe = false;
            enableRightToLeftSwipe = false;
        }
        //Case 2 - Expired Event
        if ((event.getExpired() != null && event.getExpired() == true) || event.getEndTime() < new Date().getTime()) {
            enableLeftToRightSwipe = false;
            enableRightToLeftSwipe = false;
        }

        if (event.getScheduleAs() != null && event.getScheduleAs().equals("Notification")) {
            enableLeftToRightSwipe = true;
            enableRightToLeftSwipe = true;
            ivEditRejectIcon.setImageResource(R.drawable.invitation_decline_button);
            ivDeleteIcon.setVisibility(View.GONE);
            isNewOrEditMode = false;
        }
    }

    public void sendSmsToNonCenesMembers(List<EventMember> nonCenesMemberList, Event event) {
        if (nonCenesMemberList.size() > 0) {
            String phoneNumbers = "";
            String separator = "; ";
            if(android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")){
                separator = ", ";
            }

            for (int i=0; i < nonCenesMemberList.size(); i++) {
                try {
                    EventMember jsonObject = nonCenesMemberList.get(i);
                    phoneNumbers += jsonObject.getPhone()+separator;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String clipHostName = "";
            if (eventOwner != null) {
                clipHostName = eventOwner.getName();
            } else {
                clipHostName = loggedInUser.getName();
            }
            String message  = CenesConstants.shareInvitationMessage.replaceAll("\\[Host\\]", clipHostName).replaceAll("\\[Title\\]",event.getTitle());
            message += "\n"+CenesConstants.webDomainEventUrl+event.getKey();

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // Greater than Nugget
                {
                    try {
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ phoneNumbers.substring(0, phoneNumbers.length()-1)));
                        sendIntent.putExtra("sms_body", message);
                        startActivityForResult(sendIntent, SMS_COMPOSE_RESULT_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) // At least KitKat And Upto MarshMallow
                {
                    String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity()); // Need to change the build to API 19

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");

                    if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
                    // any app that support this intent.
                    {
                        sendIntent.setPackage(defaultSmsPackageName);
                    }
                    //sendIntent.setData(Uri.parse("sms:"+phoneNumbers.substring(0, phoneNumbers.length()-1)));
                    sendIntent.putExtra("address",phoneNumbers.substring(0, phoneNumbers.length()-1));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivityForResult(sendIntent, SMS_COMPOSE_RESULT_CODE);

                }
                else // For early versions, do what worked for you before.
                {
                    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address",phoneNumbers.substring(0, phoneNumbers.length()-1));
                    smsIntent.putExtra("sms_body",message);
                    startActivityForResult(smsIntent, SMS_COMPOSE_RESULT_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getVisiblePercent(View v) {
        if (v.isShown()) {
            Rect r = new Rect();
            boolean isVisible = v.getGlobalVisibleRect(r);
            if (isVisible) {
                double sVisible = r.width() * r.height();
                double sTotal = v.getWidth() * v.getHeight();
                return (int) (100 * sVisible / sTotal);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    public void getChatThread(){

        System.out.println("getChatThread");
        try {
            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_event_chat_api);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setQueryStr("eventId="+event.getEventId());
            new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {

                        progressBar.setVisibility(View.GONE);
                        isChatLoaded = true;
                        Gson gson = new GsonBuilder().create();
                        eventChatMapList = new HashMap<>();
                        headers = new ArrayList<>();
                        eventChats = new ArrayList<>();
                        Type listType = new TypeToken<List<EventChat>>() {
                        }.getType();
                       /* EventChat eventChatEvent = new EventChat();
                        eventChatEvent.setChat(event.getDescription());
                        eventChatEvent.setCreatedAt(event.getCreatedAt());
                        eventChatEvent.setChatStatus("Sent");
                        eventChatEvent.setSenderId(event.getCreatedById());
                        eventChatEvent.setUser(eventOwner.getUser());
                        eventChats.add(eventChatEvent); */
                        boolean success = response.getBoolean("success");
                        if (success) {

                            List<EventChat> eventChatsTmp = gson.fromJson(response.getJSONArray("data").toString(), listType);
                            for(EventChat  eventChat : eventChatsTmp) {
                                if(!eventChat.getChat().contains("(Edited)") && eventChat.getChatType().equals("Description")) {
                                    if(!eventChat.getChat().equals(event.getDescription())) {
                                        eventChat.setChat(event.getDescription()+" (Edited)");
                                    } else if("Yes".equals(eventChat.getChatEdited())) {
                                        eventChat.setChat(event.getDescription()+" (Edited)");
                                    }
                                }
                                eventChats.add(eventChat);
                            }
                            for(EventChat  eventChat : eventChats) {
                                 // System.out.println(eventChat.getChat());
                                // System.out.println(eventChat.getCreatedAt());
                                String key = CenesUtils.EEEMMMMdd.format(eventChat.getCreatedAt());

                                Calendar previousDateCal = Calendar.getInstance();
                                previousDateCal.add(Calendar.DAY_OF_MONTH, -1);
                                Calendar currentYear = Calendar.getInstance();
                                Calendar chatCalendar = Calendar.getInstance();
                                chatCalendar.setTimeInMillis(eventChat.getCreatedAt());


                                String yearAppend = "";

                                if(currentYear.get(Calendar.YEAR) != chatCalendar.get(Calendar.YEAR)) {
                                    yearAppend = ", "+chatCalendar.get(Calendar.YEAR);

                                }else {
                                    yearAppend = "";
                                }

                                if (key != null) {
                                    if (key.equals(CenesUtils.EEEMMMMdd.format(new Date()))) {

                                        key = "Today ";

                                    } else if (key.equals(CenesUtils.EEEMMMMdd.format(previousDateCal.getTime()))) {

                                        key = "Yesterday ";
                                    }
                                }
                                if(!headers.contains(key)) {
                                    //  System.out.println("header key : +" + key);
                                    headers.add(key + yearAppend);
                                }

                                List<EventChat> eventChatTemp = null;
                                if (eventChatMapList.containsKey(key)) {

                                    eventChatTemp = eventChatMapList.get(key);

                                } else {

                                    eventChatTemp = new ArrayList<>();

                                }
                                eventChatTemp.add(eventChat);
                                eventChatMapList.put(key,eventChatTemp);
                            }

                            eventChatList = new ArrayList<>();
                            for (String header: headers) {
                                eventChatList.add(header);
                                List<EventChat> eventChats = eventChatMapList.get(header);
                                for (EventChat eventChatTmp: eventChats) {
                                    eventChatList.add(eventChatTmp);
                                }
                            }
                        }

                        if(isDescriptionButtonOn == true ) {
                            // System.out.println("fgdfgdgdfgf");
                            loadDescriptionData();
                        }
                        // System.out.println(" headers : "+headers.size());
                        // System.out.println(" eventChatMapList : "+eventChatMapList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void loadDescriptionData(){
        // svCard.setOnTouchListener(null);
        // tinderCardView.setOnTouchListener(null);
        System.out.println("Description : "+event.getDescription());
        if (!CenesUtils.isEmpty(event.getDescription())) {

            System.out.println("Description : event id : "+event.getEventId());
            if (event.getEventId() == null || (event.getExpired() != null && event.getExpired() == true) || event.getEndTime() < new Date().getTime() ||  event.getEventMembers().size() == 1 ) {


                if (rvEventDescriptionDialog.getVisibility() == View.GONE) {

                    System.out.println("Stepxx_1 ");
                    tvEventDescriptionDialogText.setText(event.getDescription());
                    rvEventDescriptionDialog.setVisibility(View.VISIBLE);
                    rlChatBubble.setVisibility(View.GONE);
                    llSenderPicture.setVisibility(View.GONE);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.circleCrop();
                    requestOptions.placeholder(R.drawable.profile_pic_no_image);
                    if (eventOwner != null && eventOwner.getUser() != null) {
                        Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(requestOptions).into(ivDescProfilePic);
                    }

                } else {
                    System.out.println("Stepxx_2 ");
                    hideDescriptionMessage();
                }

            } else {
                if (rlIncludeChat.getVisibility() == View.GONE || isPushRequest == true) {

                    System.out.println("Stepxx_3 ");
                    postReadChatStatus();

                    rlEnterChat.setVisibility(View.GONE);
                    rlIncludeChat.setVisibility(View.VISIBLE);
                    if (event.isEditMode()) {
                        rlChatBubble.setVisibility(View.GONE);
                    } else {
                        rlChatBubble.setVisibility(View.VISIBLE);
                    }
                    llSenderPicture.setVisibility(View.VISIBLE);

                    //new Handler().postDelayed(new Runnable() {
                    //@Override
                    //public void run() {

                    if (event.getCreatedById().equals(loggedInUser.getUserId())) {
                        llSenderPicture.setBackground(getResources().getDrawable(R.drawable.host_gradient_circle));
                    } else {
                        llSenderPicture.setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
                    }

                    //elvEventChatList.invalidate();
                    if (eventChatBaseAdapter == null) {
                        eventChatBaseAdapter = new EventChatBaseAdapter(GatheringPreviewFragment.this, eventChatList);
                        lvChatListView.setAdapter(eventChatBaseAdapter);
                    } else {
                        eventChatBaseAdapter.refreshItems(eventChatList);
                        //eventChatExpandableAdapter.notifyDataSetChanged();
                    }
                    //

                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.drawable.profile_pic_no_image);
                    options.centerCrop();
                    Glide.with(getContext()).load(loggedInUser.getPicture()).apply(options).into(enterMsgPicture);

                    lvChatListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                          /*  int scrollPosition = 0;
                            for (Map.Entry<String, List<EventChat>> entrySet : eventChatMapList.entrySet()) {
                                scrollPosition = scrollPosition + 1 + entrySet.getValue().size();
                            } */
                            System.out.println("size event chat  list : : : " + eventChats.size() + headers.size());
                            // elvEventChatList.smoothScrollBy(eventChatExpandableAdapter.getGroupCount()-1,100);
                            //elvEventChatList.smoothScrollToPosition(rlChatBubble.getBottom());
                            //elvEventChatList.setSelection(scrollPosition);
                            //elvEventChatList.setSelection(eventChats.size() + headers.size());
                            //for (int i=0; i < 10; i++) {
                            // elvEventChatList.setSelection(eventChats.size() + headers.size());

                            int selectionIndex = eventChatList.size() - 1;
                            if (selectedEventChatDto != null && selectedEventChatDto.getMessage() != null) {
                                selectionIndex = 0;
                                for (Object chatObj: eventChatList) {
                                    if (chatObj instanceof  EventChat) {
                                        System.out.println("Checking if chat equals");
                                        if (((EventChat)chatObj).getChat().equals(selectedEventChatDto.getMessage())) {
                                            System.out.println("Chat Equals");
                                            break;
                                        }

                                    }
                                    selectionIndex++;
                                }
                                selectedEventChatDto = null;
                            }
                            System.out.println("Selection Index : "+selectionIndex);
                            lvChatListView.setSelection(selectionIndex);
                            //try {
                            //  Thread.sleep(1000);
                            //} catch (Exception e) {

                            //}
                            //}
                            //elvEventChatList.setSelectedGroup(headers.size() - 1);
                            //elvEventChatList.setSelectedChild(headers.size() - 1, eventChatMapList.get(headers.get(headers.size() - 1)).size() - 1, false);
                            //elvEventChatList.set( eventChatMapList.get(headers.get(headers.size() - 1)).size() - 1);


                        }
                    }, 100);

                    //fragmentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
                    //}
                    //}, 1000);

                } else {
                    System.out.println("Stepxx_4 ");
                    llSenderPicture.setVisibility(View.GONE);
                    rlIncludeChat.setVisibility(View.GONE);
                    rlChatBubble.setVisibility(View.GONE);
                    //elvEventChatList.setAdapter(null);
                }
            }
        } else {
            svCard.setScrollingEnabled(true);
            new AlertDialog.Builder(getActivity())
                    .setTitle("Description Not Available.")
                    .setMessage("")
                    .setCancelable(false)
                    .setPositiveButton("Ok", null).show();
        }
        isPushRequest = false;
    }

    public void scrollFeatureOn() {

        tinderCardView.setOnTouchListener(onTouchListener);
        svCard.setOnTouchListener(onTouchListener);
        svCard.setScrollingEnabled(true);

    }
    public void scrollFeatureOff() {

        tinderCardView.setOnTouchListener(null);
        svCard.setOnTouchListener(null);
        svCard.setScrollingEnabled(false);
    }

    public void fetchLocationPhotos(String placeId) {

        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(GatheringAPI.get_place_details_api);
        asyncTaskDto.setQueryStr("place_id="+placeId+"&fields=photos&key="+CenesConstants.GOOGLE_MAP_API_KEY);
        new ProfileAsyncTask.CommonGetRequestTask(new ProfileAsyncTask.CommonGetRequestTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

                try {
                    JSONObject result = response.getJSONObject("result");
                    JSONArray photosArray = result.getJSONArray("photos");

                    Gson gson = new GsonBuilder().create();
                    Type listType = new TypeToken<List<LocationPhoto>>() {}.getType();
                    locationPhotos = gson.fromJson(photosArray.toString(), listType);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).execute(asyncTaskDto);
    }
}