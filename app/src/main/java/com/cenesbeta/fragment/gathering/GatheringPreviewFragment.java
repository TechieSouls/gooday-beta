package com.cenesbeta.fragment.gathering;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.AsyncTasks.ProfileAsyncTask;
import com.cenesbeta.Manager.Impl.UrlManagerImpl;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.Manager.UrlManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.adapter.EventChatExpandableAdapter;
import com.cenesbeta.api.GatheringAPI;
import com.cenesbeta.api.UserAPI;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.backendManager.UserApiManager;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventChat;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.impl.UserManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.dto.AsyncTaskDto;
import com.cenesbeta.dto.GatheringPreviewDto;
import com.cenesbeta.extension.InvitationScrollView;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    private RelativeLayout rvEventDescriptionDialog, rlIncludeChat;
    private RelativeLayout rlDescriptionBubbleBackground, ivEventPictureOverlay;
    private ImageView ivDescriptionBubbleIcon;
    private RoundedImageView ivDescProfilePic,enterMsgPicture;
    private CardView tinderCardView;
    private RelativeLayout rlParentVew, rlSkipText, rlChatBubble, rlEnterChat;
    private RelativeLayout rlInvitationView, rlWelcomeInvitation;
    private RoundedImageView ivProfilePicView;
    private ImageView invitationAcceptSpinner, invitationRejectSpinner, enterChatImageView;
    private LinearLayout llBottomButtons, llEventDetails, llInvitationFooter, llInvitationImageLayout, llSenderPicture;
    private InvitationScrollView svCard;
    private ProgressBar progressBar;

    public Fragment sourceFragment;
    private CenesApplication cenesApplication;
    private InternetManager internetManager;
    private EventManagerImpl eventManagerImpl;
    public User loggedInUser;
    public Event event;
    public List<Event> pendingEvents;
    public List<EventChat> eventChats;
    private EventChatExpandableAdapter eventChatExpandableAdapter;
    private ExpandableListView elvEventChatList;
    private List<String> headers;
    private Map<String, List<EventChat>> eventChatMapList;


    private EventMember eventOwner, loggedInUserAsEventMember;
    private boolean enableLeftToRightSwipe, enableRightToLeftSwipe;
    private boolean isNewOrEditMode;
    private int pendingEventIndex;
    int windowWidth, windowHeight;
    int screenCenter;
    int xCord, yCord, newXcord, newYCord;
    float x, y;
    private int yPositionOfCard = 0;
    boolean leftPartClicked, bottomBarClicked;
    boolean isLoggedInUserExistsInMemberList = false;
    private List<EventMember> nonCenesMember;
    private boolean isNewEvent = false;
    private boolean isChatLoaded = false;

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

        rlSkipText = (RelativeLayout) view.findViewById(R.id.rl_skip_text);
        rvEventDescriptionDialog = (RelativeLayout) view.findViewById(R.id.rv_event_description_dialog);
        rlDescriptionBubbleBackground = (RelativeLayout) view.findViewById(R.id.rl_description_bubble_background);
        rlIncludeChat = (RelativeLayout) view.findViewById(R.id.rl_include_chat);
        elvEventChatList = (ExpandableListView) view.findViewById(R.id.elv_chat_listView);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            svCard.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    System.out.println("Is Visible or not : "+getVisiblePercent(rlSkipText));
                    if (getVisiblePercent(rlSkipText) > 10) {
                        tinderCardView.setOnTouchListener(null);
                        svCard.setOnTouchListener(null);
                        tinderCardView.setRotation(0);
                        tinderCardView.setX(0);

                    } else {
                        tinderCardView.setOnTouchListener(onTouchListener);
                        svCard.setOnTouchListener(onTouchListener);
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

            getChatThread(event.getEventId());
            populateInvitationCard(event);
        }
        ivProfilePicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CenesBaseActivity)getActivity()).zoomImageFromThumb(ivProfilePicView,eventOwner.getUser().getPicture() );

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    if (isChatLoaded == false) {
                        progressBar.setVisibility(View.VISIBLE);
                    } else {

                        if (!CenesUtils.isEmpty(event.getDescription())) {

                            if ((event.getExpired() != null && event.getExpired() == true) || event.getEndTime() < new Date().getTime()) {


                                if (rvEventDescriptionDialog.getVisibility() == View.GONE) {

                                    ivDescriptionBubbleIcon.setImageResource(R.drawable.message_on_icon);
                                    rlDescriptionBubbleBackground.setAlpha(1);
                                    rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
                                    tvEventDescriptionDialogText.setText(event.getDescription());
                                    rvEventDescriptionDialog.setVisibility(View.VISIBLE);

                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.circleCrop();
                                    requestOptions.placeholder(R.drawable.profile_pic_no_image);
                                    if (eventOwner != null && eventOwner.getUser() != null) {
                                        Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(requestOptions).into(ivDescProfilePic);
                                    }

                                } else {
                                    hideDescriptionMessage();
                                }

                            }else {
                                if (rlIncludeChat.getVisibility() == View.GONE) {

                                    rlIncludeChat.setVisibility(View.VISIBLE);
                                    elvEventChatList.invalidate();
                                    eventChatExpandableAdapter = new EventChatExpandableAdapter(GatheringPreviewFragment.this, headers, eventChatMapList);
                                    elvEventChatList.setAdapter(eventChatExpandableAdapter);
                                    RequestOptions options = new RequestOptions();
                                    options.placeholder(R.drawable.profile_pic_no_image);
                                    options.centerCrop();
                                    Glide.with(getContext()).load(loggedInUser.getPicture()).apply(options).into(enterMsgPicture);

                                    elvEventChatList.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            int scrollPosition = 0;
                                            for (Map.Entry<String, List<EventChat>> entrySet : eventChatMapList.entrySet()) {
                                                scrollPosition = scrollPosition + 1 + entrySet.getValue().size();
                                            }
                                            System.out.println("size event chat  list : : : " + eventChats.size());
                                            // elvEventChatList.smoothScrollBy(eventChatExpandableAdapter.getGroupCount()-1,100);
                                            //elvEventChatList.smoothScrollToPosition(rlChatBubble.getBottom());
                                            elvEventChatList.setSelection(eventChats.size() + headers.size());
                                        }
                                    });
                                } else {
                                    rlIncludeChat.setVisibility(View.GONE);
                                    //elvEventChatList.setAdapter(null);
                                }
                            }
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Description Not Available.")
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", null).show();
                        }
                        break;

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

                    event.setEditMode(true);
                    CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                    createGatheringFragment.event = event;
                    ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, null);
                    break;

                case R.id.iv_card_swipe_arrow:

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

                    Handler handler = new Handler(); handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            enterChatTv.requestFocus();
                            enterChatTv.setFocusableInTouchMode(true);
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                            imm.showSoftInput(enterChatTv, InputMethodManager.SHOW_FORCED);

                        }

                        }, 100);


                    break;
                case  R.id.enter_chat_button :
                    System.out.println("typing message...");
                    int scrollPosition = 0;
                    for (Map.Entry<String, List<EventChat>> entrySet: eventChatMapList.entrySet()) {
                        scrollPosition = scrollPosition + 1 + entrySet.getValue().size();
                    }
                    String chatMessage = enterChatTv.getText().toString();
                    enterChatTv.setText("");
                    if(chatMessage != null && chatMessage != "") {

                        postEventChat(chatMessage);

                    }
                    break;

                default:
                        System.out.println("Heyyy you did it.");
            }
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);

            xCord = (int)event.getRawX();
            yCord = (int) event.getRawY();

            tinderCardView.setX(0);
            tinderCardView.setY(0);

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    GatheringPreviewDto.userCanSwipe = true;

                    x = (int) event.getX();
                    y = (int) event.getY();

                    System.out.println("[ACTION_DOWN] : User Tap Finger of Card");
                    System.out.println("[ACTION_DOWN] : Rotation : "+tinderCardView.getRotation());

                    if (Math.abs(tinderCardView.getRotation()) != 20) {

                        System.out.println("[ACTION_DOWN] : Card was at initial stage");

                        xCord = 0;
                        tinderCardView.setX(0);
                        tinderCardView.setY(0);
                        tinderCardView.setRotation(0);
                        GatheringPreviewDto.ifSwipedRightToLeft = false;

                        if (x < screenCenter) {
                            leftPartClicked = true;
                        } else {
                            leftPartClicked = false;
                        }
                        if (y > (windowHeight - 200)) {
                            bottomBarClicked = true;
                        }

                        Log.d("Bottom : ", (y > (windowHeight - 100))+"");
                        Log.v("On touch", x + " " + y+ " Screen Center : "+screenCenter);

                        GatheringPreviewDto.cardSwipedToExtent = false;
                    } else {
                        GatheringPreviewDto.userClickedToCloseCard = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:

                    System.out.println("[ACTION_UP] : User Picker Up Finder");
                    if (GatheringPreviewDto.cardSwipedToExtent) {

                        System.out.println("[ACTION_UP] : Card Swiped to full Extent");
                        GatheringPreviewDto.cardSwipedToExtent = false;

                        GatheringPreviewDto.userClickedToCloseCard = true;

                        if (GatheringPreviewDto.ifSwipedRightToLeft) {

                            System.out.println("[ACTION_UP] : Card was swiped to left");


                                System.out.println("[ACTION_UP] : User Tap Finger of Card");
                            System.out.println("[ACTION_UP] : Rotatoin : "+tinderCardView.getRotation());

                                tinderCardView.setRotation(-20);
                                tinderCardView.setX(-300);

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
                                        //rejectGathering();
                                        rejectGatheringConfirmAlert();
                                    }
                                }


                        } else if (GatheringPreviewDto.ifSwipedLeftToRight) {

                            System.out.println("[ACTION_UP] : Card was swiped to Right");

                            tinderCardView.setRotation(20);
                            tinderCardView.setX(300);

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
                        System.out.println("[ACTION_UP] : Reset Card To Initial Position");
                        svCard.scrollTo(0, 0);
                        resetCardPosition();
                    }
                    break;



                case MotionEvent.ACTION_MOVE:

                    if (GatheringPreviewDto.userCanSwipe == false) {

                        return false;
                    }
                    xCord = (int) event.getRawX();
                    yCord = (int) event.getRawY();

                    newXcord = (int)(xCord - x);
                    System.out.println("enableLeftToRightSwipe && enableRightToLeftSwipe : "+enableLeftToRightSwipe+","+enableRightToLeftSwipe);

                    if (enableLeftToRightSwipe && enableRightToLeftSwipe) {

                        System.out.println("[ACTION_MOVE] -- INSIDE BOTH WAY SWIPE");

                        if (Math.abs(newXcord) < 300 ) {
                            tinderCardView.setX(newXcord);
                            if ((float)((xCord - x)/2 *  (Math.PI/32)) < 15) {
                                tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));
                            }
                        } else {
                            GatheringPreviewDto.cardSwipedToExtent = true;
                            if (newXcord < 0) {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);
                            } else {
                                tinderCardView.setX(300);
                                tinderCardView.setRotation(20);
                            }
                        }

                        if (newXcord < 0) {
                            GatheringPreviewDto.ifSwipedRightToLeft = true;
                        } else if (newXcord > 0) {
                            GatheringPreviewDto.ifSwipedLeftToRight = true;
                        } else {
                            resetCardPosition();
                        }

                    } else if (enableLeftToRightSwipe && !enableRightToLeftSwipe) {

                        System.out.println("[ACTION_MOVE] -- INSIDE LEFT TO RIGHT SWIPE");
                        GatheringPreviewDto.ifSwipedLeftToRight = true;
                        if (newXcord > 0) {
                            if (GatheringPreviewDto.cardSwipedToExtent == false) {

                                tinderCardView.setX(newXcord);
                                if (Math.abs(newXcord) < 300 ) {
                                    if ((float)((xCord - x)/2 *  (Math.PI/32)) < 15) {
                                        tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));
                                    }

                                    Log.d("Left To Right Swipe : ",(newXcord)+"  :   "+(yCord - y));
                                    tinderCardView.setX(newXcord);
                                    tinderCardView.setY(newYCord);
                                    GatheringPreviewDto.ifSwipedLeftToRight = true;

                                } else {
                                    tinderCardView.setX(300);
                                    GatheringPreviewDto.cardSwipedToExtent = true;

                                }
                            } else {
                                tinderCardView.setX(300);
                                tinderCardView.setRotation(20);
                                GatheringPreviewDto.cardSwipedToExtent = true;
                            }
                        }

                    } else  if (!enableLeftToRightSwipe && enableRightToLeftSwipe) {

                        System.out.println("[INSIDE RIGHT TO LEFT SWIPE] : Card Swiped Extent : " + GatheringPreviewDto.cardSwipedToExtent + "," +
                                "Card Closed Cliked : " + GatheringPreviewDto.userClickedToCloseCard);


                        GatheringPreviewDto.ifSwipedRightToLeft = true;
                        if (newXcord < 0) {

                            if (GatheringPreviewDto.cardSwipedToExtent == false) {

                                if (GatheringPreviewDto.userClickedToCloseCard) {
                                    resetCardPosition();
                                    GatheringPreviewDto.userClickedToCloseCard = false;

                                    System.out.println("X Cordinates after card closed : " + (tinderCardView.getX())+" And newXcord : "+newXcord);
                                } else {

                                    System.out.println("X Cordinates On Tap : " + (newXcord));

                                    tinderCardView.setX(newXcord);

                                    if (Math.abs(newXcord) < 300) {
                                        if ((float) ((xCord - x) / 2 * (Math.PI / 32)) < 15) {
                                            tinderCardView.setRotation((float) ((xCord - x) / 2 * (Math.PI / 32)));
                                        }

                                        Log.d("Right To Left Sipe : ", (newXcord) + "  :   " + (yCord - y));
                                        tinderCardView.setX(newXcord);
                                        tinderCardView.setY(newYCord);
                                        GatheringPreviewDto.ifSwipedRightToLeft = true;

                                    } else {
                                        tinderCardView.setX(-300);
                                        GatheringPreviewDto.cardSwipedToExtent = true;
                                    }
                                }

                            } else {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);
                                GatheringPreviewDto.cardSwipedToExtent = true;
                            }
                        }
                    }
                    break;

                default:

                    System.out.println("Default");
            }


            return false;
        }
    };

     View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                rlEnterChat.setVisibility(View.VISIBLE);
                rlEnterChat.setY(getActivity().getWindowManager().getDefaultDisplay().getHeight() - 500);
                //got focus
                //RelativeLayout.LayoutParams newPara = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PrecastrUtils.convertDpToPx(getContext(),350));
                //RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) rlEnterChat.getLayoutParams();
                //relativeParams.;  // left, top, right, bottom
                //rlBottomText.setLayoutParams(relativeParams);
                //relativeParams.height = CenesUtils.convertDpToPx(getContext(), 350);

            } else {
                //lost focus
                //RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) rlEnterChat.getLayoutParams();
                //relativeParams.height = PrecastrUtils.convertDpToPx(getContext(), 160);
                rlEnterChat.setVisibility(View.GONE);
                rlChatBubble.setVisibility(View.VISIBLE);
                llSenderPicture.setVisibility(View.VISIBLE);

                hideKeyboard();

            }
        }
    };


    public void resetCardPosition() {

        tinderCardView.setX(0);
        tinderCardView.setY(0);
        tinderCardView.setRotation(0);
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
        tinderCardView.setOnTouchListener(null);
        svCard.setOnTouchListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tinderCardView.setOnTouchListener(onTouchListener);
                svCard.setOnTouchListener(onTouchListener);
                GatheringPreviewDto.userCanSwipe = true;
            }
        }, 500);
    }

    public void hideDescriptionMessage() {
        rvEventDescriptionDialog.setVisibility(View.GONE);
        rlDescriptionBubbleBackground.setAlpha(0.3f);
        ivDescriptionBubbleIcon.setImageResource(R.drawable.message_off_icon);
        rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_black_faded));
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
                        ((CenesBaseActivity) getActivity()).homeScreenReloadBroadcaster();

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
                            //((CenesBaseActivity) getActivity()).homeFragmentV2.loadHomeScreenData();
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
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
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
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
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
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(input.getText().toString());
                    if (input.getText().toString() != "") {
                        postEventChat(input.getText().toString());
                    }
                    else{
                        showAlert("error","Message cannot be empty");
                    }
                }
            });
            builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    resetCardPosition();
                }
            });

            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void postEventChat(String message) {

        try {
            EventChat eventChat = new EventChat();
            eventChat.setChat(message);
            eventChat.setEventId(Integer.parseInt(event.getEventId().toString()));
            eventChat.setSenderId(loggedInUser.getUserId());
            eventChat.setCreatedAt(new Date().getTime());
            eventChats.add(eventChat);

            String key = CenesUtils.EEEEMMMdd.format(eventChat.getCreatedAt());

            if(!headers.contains(key)) {
                System.out.println("Header key  : "+ key);
                headers.add(key);
            }

            List<EventChat> eventChatTemp = null;
            if(eventChatMapList.containsKey(key)) {

                eventChatTemp = eventChatMapList.get(key);


            }else {

                eventChatTemp = new ArrayList<>();

            }
            eventChatTemp.add(eventChat);
            eventChatMapList.put(key,eventChatTemp);
            eventChatExpandableAdapter.notifyDataSetChanged();


            AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
            asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.post_event_chat_api);
            asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
            asyncTaskDto.setPostData(new JSONObject(new Gson().toJson(eventChat)));
            new ProfileAsyncTask.CommonPostRequestTask(new ProfileAsyncTask.CommonPostRequestTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {
                        boolean success = response.getBoolean("success");
                        if (success == true) {
                            elvEventChatList.setSelection(eventChats.size());

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
    public void getChatThread(Long eventId){

        try {
        AsyncTaskDto asyncTaskDto = new AsyncTaskDto();
        asyncTaskDto.setApiUrl(UrlManagerImpl.prodAPIUrl+ GatheringAPI.get_event_chat_api);
        asyncTaskDto.setAuthToken(loggedInUser.getAuthToken());
        asyncTaskDto.setQueryStr("eventId="+eventId);
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
                        EventChat eventChatEvent = new EventChat();
                        eventChatEvent.setChat(event.getDescription());
                        eventChatEvent.setCreatedAt(event.getCreatedAt());
                        eventChatEvent.setChatStatus("Sent");
                        eventChatEvent.setSenderId(event.getCreatedById());
                        eventChatEvent.setUser(eventOwner.getUser());
                        eventChats.add(eventChatEvent);
                        List<EventChat> eventChatsTmp = gson.fromJson(response.getJSONArray("data").toString(), listType);
                        for(EventChat  eventChat : eventChatsTmp) {
                            eventChats.add(eventChat);
                        }

                        for(EventChat  eventChat : eventChats) {
                            System.out.println(eventChat.getChat());
                            System.out.println(eventChat.getCreatedAt());
                            String key = CenesUtils.EEEEMMMdd.format(eventChat.getCreatedAt());

                            if(!headers.contains(key)) {
                                System.out.println("header key : +" + key);
                                headers.add(key);
                            }

                            List<EventChat> eventChatTemp = null;
                            if(eventChatMapList.containsKey(key)) {

                                eventChatTemp = eventChatMapList.get(key);


                            }else {

                                eventChatTemp = new ArrayList<>();

                            }
                            eventChatTemp.add(eventChat);
                            eventChatMapList.put(key,eventChatTemp);
                        }


                        System.out.println(" headers : "+headers.size());
                        System.out.println(" eventChatMapList : "+eventChatMapList.size());


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).execute(asyncTaskDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void setChatStuff(){

    }
}
