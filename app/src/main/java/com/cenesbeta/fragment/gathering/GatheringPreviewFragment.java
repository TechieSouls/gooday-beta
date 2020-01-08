package com.cenesbeta.fragment.gathering;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.Manager.InternetManager;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.CenesFragment;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.util.CenesConstants;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatheringPreviewFragment extends CenesFragment {

    private static String TAG = "GatheringPreviewFragment";
    private int SMS_COMPOSE_RESULT_CODE = 1001;

    private View fragmentView;
    private ImageView ivEventPicture, ivAcceptSendIcon, ivEditRejectIcon, ivDeleteIcon, ivCardSwipeArrow;
    private TextView tvEventTitle, tvEventDate, tvEventDescriptionDialogText;
    private RelativeLayout rlGuestListBubble, rlLocationBubble, rlDescriptionBubble, rlShareBubble;
    private RelativeLayout rvEventDescriptionDialog;
    private RelativeLayout rlDescriptionBubbleBackground;
    private ImageView ivDescriptionBubbleIcon;
    private RoundedImageView ivDescProfilePic;
    private CardView tinderCardView;
    private RelativeLayout rlParentVew, rlSkipText;
    private RelativeLayout rlInvitationView, rlWelcomeInvitation;
    private RoundedImageView ivProfilePicView;
    private ImageView invitationAcceptSpinner, invitationRejectSpinner;
    private LinearLayout llBottomButtons, llEventDetails;

    public Fragment sourceFragment;
    private CenesApplication cenesApplication;
    private InternetManager internetManager;
    private VelocityTracker mVelocityTracker = null;
    private User loggedInUser;
    public Event event;
    public List<Event> pendingEvents;
    private EventMember eventOwner, loggedInUserAsEventMember;
    private boolean enableLeftToRightSwipe, enableRightToLeftSwipe;
    private boolean isNewOrEditMode;
    private int pendingEventIndex;
    int windowWidth, windowHeight;
    int screenCenter;
    int xCord, yCord, newXcord, newYCord;
    float x, y;
    boolean leftPartClicked, bottomBarClicked;
    boolean ifSwipedLeftToRight, ifSwipedRightToLeft, ifSwipedUp;
    boolean cardSwipedToExtent;
    boolean isLoggedInUserExistsInMemberList = false;
    private List<EventMember> nonCenesMember;
    private boolean isNewEvent = false;

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
        ivEventPicture = (ImageView) view.findViewById(R.id.iv_event_picture);
        tvEventTitle = (TextView) view.findViewById(R.id.tv_event_title);
        tvEventDate = (TextView) view.findViewById(R.id.tv_event_date);
        tvEventDescriptionDialogText = (TextView) view.findViewById(R.id.tv_event_description_dialog_text);

        rlGuestListBubble = (RelativeLayout) view.findViewById(R.id.rl_guest_list_bubble);
        rlLocationBubble = (RelativeLayout) view.findViewById(R.id.rl_location_bubble);
        rlDescriptionBubble = (RelativeLayout) view.findViewById(R.id.rl_description_bubble);
        rlShareBubble = (RelativeLayout) view.findViewById(R.id.rl_share_bubble);

        rlSkipText = (RelativeLayout) view.findViewById(R.id.rl_skip_text);
        rvEventDescriptionDialog = (RelativeLayout) view.findViewById(R.id.rv_event_description_dialog);
        rlDescriptionBubbleBackground = (RelativeLayout) view.findViewById(R.id.rl_description_bubble_background);
        ivDescriptionBubbleIcon = (ImageView) view.findViewById(R.id.iv_description_bubble_icon);
        ivDescProfilePic = (RoundedImageView) view.findViewById(R.id.iv_desc_profile_pic);

        ivAcceptSendIcon = (ImageView) view.findViewById(R.id.iv_accept_icon);
        ivEditRejectIcon = (ImageView) view.findViewById(R.id.iv_edit_reject_icon);
        ivDeleteIcon = (ImageView) view.findViewById(R.id.iv_delete_icon);
        invitationAcceptSpinner = (ImageView) view.findViewById(R.id.iv_invitation_accept_spinner);
        invitationRejectSpinner = (ImageView) view.findViewById(R.id.iv_invitation_decline_spinner);
        ivCardSwipeArrow = (ImageView) view.findViewById(R.id.iv_card_swipe_arrow);

        tinderCardView = (CardView) view.findViewById(R.id.tinderCardView);
        rlParentVew = (RelativeLayout) view.findViewById(R.id.rl_parent_vew);
        rlWelcomeInvitation = (RelativeLayout) view.findViewById(R.id.rl_welcome_invitation);
        rlInvitationView = (RelativeLayout) view.findViewById(R.id.rl_invitation_view);

        llBottomButtons = (LinearLayout) view.findViewById(R.id.ll_bottom_buttons);
        llEventDetails = (LinearLayout) view.findViewById(R.id.ll_event_details);

        ivCardSwipeArrow.setOnClickListener(onClickListener);
        rlGuestListBubble.setOnClickListener(onClickListener);
        rlLocationBubble.setOnClickListener(onClickListener);
        rlDescriptionBubble.setOnClickListener(onClickListener);
        rlShareBubble.setOnClickListener(onClickListener);
        ivDeleteIcon.setOnClickListener(onClickListener);
        ivEditRejectIcon.setOnClickListener(onClickListener);
        tinderCardView.setOnTouchListener(onTouchListener);

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        internetManager = coreManager.getInternetManager();
        loggedInUser = userManager.getUser();

        windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        screenCenter = windowWidth / 2;

        invitationAcceptSpinner.setVisibility(View.GONE);
        invitationRejectSpinner.setVisibility(View.GONE);

        ivEventPicture.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(getActivity().getWindowManager().getDefaultDisplay().getHeight())));

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
            populateInvitationCard(event);
        }
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
                                        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+event.getLongitude()+","+event.getLongitude()+"");

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

                    if (!CenesUtils.isEmpty(event.getDescription())) {

                        if (rvEventDescriptionDialog.getVisibility() == View.GONE) {

                            ivDescriptionBubbleIcon.setImageResource(R.drawable.message_on_icon);
                            rlDescriptionBubbleBackground.setAlpha(1);
                            rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
                            tvEventDescriptionDialogText.setText(event.getDescription());
                            rvEventDescriptionDialog.setVisibility(View.VISIBLE);

                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.circleCrop();
                            requestOptions.placeholder(R.drawable.profile_pic_no_image);
                            if (eventOwner.getUser() != null) {
                                Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(requestOptions).into(ivDescProfilePic);
                            }

                        } else {
                            hideDescriptionMessage();
                        }

                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Description Not Available.")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();
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

                    deleteGathering();
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
                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
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
                                    cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);

                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (getActivity() != null) {
                                        ((CenesBaseActivity) getActivity()).onBackPressed();
                                        tinderCardView.setY(0);

                                    }
                                }
                            }, 500);

                        }
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

                    x = (int) event.getX();
                    y = (int) event.getY();
                    xCord = 0;
                    tinderCardView.setX(0);
                    tinderCardView.setY(0);
                    tinderCardView.setRotation(0);
                    ifSwipedRightToLeft = false;

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

                    cardSwipedToExtent = false;

                    break;

                case MotionEvent.ACTION_UP:

                    if (ifSwipedRightToLeft) {


                        if (cardSwipedToExtent) {
                            tinderCardView.setRotation(-20);
                            tinderCardView.setX(-300);
                        } else {
                            tinderCardView.setRotation(0);
                        }
                        if (isNewOrEditMode) {

                            if (Math.abs(xCord - x) > 300) {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);

                            } else {
                                //tinderCardView.setX(newXcord);
                                tinderCardView.setX(0);
                                tinderCardView.setY(0);
                                tinderCardView.setRotation(0);
                            }
                            //tinderCardView.setY(newYCord);

                        } else {

                            if (Math.abs(newXcord) > 300) {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);

                            }
                            //tinderCardView.setY(newYCord);
                        }
                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        cardSwipedToExtent = false;
                        //ivAcceptSendIcon.setVisibility(View.GONE);
                        //ivEditRejectIcon.setVisibility(View.GONE);
                        if (pendingEvents != null && pendingEvents.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);
                        }


                    } else if (ifSwipedLeftToRight) {

                        if (cardSwipedToExtent) {
                            tinderCardView.setRotation(20);
                            tinderCardView.setX(300);
                        } else {
                            tinderCardView.setRotation(0);
                        }

                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        //ivAcceptSendIcon.setVisibility(View.GONE);
                        //ivEditRejectIcon.setVisibility(View.GONE);

                        if (pendingEvents != null && pendingEvents.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);
                        }


                    } else if (ifSwipedUp) {

                        //ifSwipedLeftToRight = false;
                        //ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        tinderCardView.setRotation(0);
                        tinderCardView.setY(-500);

                        if (cardSwipedToExtent) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tinderCardView.setX(0);
                                    //tinderCardView.setY(-windowHeight + 400);
                                }
                            }, 500);

                            /*if (pendingEvents != null && pendingEvents.size() > 0) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tinderCardView.setY(0);
                                        tinderCardView.setRotation(0);

                                        populateInvitationCard((GatheringPreviewFragment.this).event);
                                    }
                                }, 500);
                            }*/

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);
                            tinderCardView.setRotation(0);

                        }
                    } else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                tinderCardView.setX(0);
                                tinderCardView.setY(0);
                                tinderCardView.setRotation(0);
                            }
                        }, 500);


                    }
                    bottomBarClicked = false;
                    leftPartClicked = false;
                     //}

                    break;



                case MotionEvent.ACTION_MOVE:

                    xCord = (int) event.getRawX();
                    yCord = (int) event.getRawY();

                    newXcord = (int)(xCord - x);
                    //newYCord = (int)(yCord - y);

                    Log.d("xCord : yCord : ",(xCord - x)+""+(yCord - y));
                    if (enableLeftToRightSwipe && enableRightToLeftSwipe && !ifSwipedUp) {

                        if (Math.abs(newXcord) < 300) {

                            tinderCardView.setX(newXcord);
                            tinderCardView.setY(newYCord);

                        } else {

                            if (newXcord < 0) {

                                tinderCardView.setX(-300);
                                cardSwipedToExtent = true;

                            } else if (newXcord > 0) {

                                tinderCardView.setX(300);
                                cardSwipedToExtent = true;

                            }

                        }

                    } else if (enableLeftToRightSwipe && !enableRightToLeftSwipe && !ifSwipedUp) {

                        if (enableLeftToRightSwipe && newXcord > 100) {

                            if (Math.abs(xCord - x) < 300) {

                                tinderCardView.setX(newXcord);
                                tinderCardView.setY(newYCord);

                            } else {
                                    //tinderCardView.setX(300);
                                    //cardSwipedToExtent = true;
                                    //ifSwipedRightToLeft = true;
                                    if (Math.abs(newYCord) < 200) {

                                        tinderCardView.setX(300);
                                        cardSwipedToExtent = true;

                                    }
                                    ifSwipedLeftToRight = false;
                            }


                        } else if (newYCord < 0) {

                            //if (Math.abs(yCord - y) < 300) {

                                tinderCardView.setX(0);
                                tinderCardView.setY(yCord - y);

                            //}

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);
                            tinderCardView.setRotation(0);

                        }

                    } else if (!enableLeftToRightSwipe && enableRightToLeftSwipe && !ifSwipedUp) {

                        Log.d("RightToLeftSwipe : ", newXcord+" newYCord : "+newYCord);
                        if (enableRightToLeftSwipe && newXcord < -100) {


                                if (Math.abs(newXcord) < 300 ) {

                                    Log.d("Right To Left Sipe : ",(newXcord)+"  :   "+(yCord - y));
                                    tinderCardView.setX(newXcord);
                                    tinderCardView.setY(newYCord);
                                    ifSwipedRightToLeft = true;

                                } else {

                                    if (Math.abs(newYCord) < 200) {

                                        tinderCardView.setX(-300);
                                        cardSwipedToExtent = true;

                                    }
                                    ifSwipedRightToLeft = false;

                                }

                        }  else if (newYCord < 0) {

                                tinderCardView.setX(0);
                                tinderCardView.setY(yCord - y);

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);
                            tinderCardView.setRotation(0);

                        }
                    }

                    Log.v("X And Xcord : ", (xCord - x)+" Screen Center : "+screenCenter);
                    if ((xCord - x) > 100 && enableLeftToRightSwipe && !ifSwipedUp) {
                        //If User swipe from left to right

                        if ((float)((xCord - x)/2 *  (Math.PI/32)) < 15) {
                            tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));
                        }
                        ifSwipedLeftToRight = true;
                        if (Math.abs(xCord - x) > 200) {

                            if (isNewOrEditMode) {

                                if (!cardSwipedToExtent) {
                                    cardSwipedToExtent = true;
                                    invitationAcceptSpinner.setVisibility(View.VISIBLE);
                                    rotate(360, invitationAcceptSpinner);

                                    createGathering();
                                    if (nonCenesMember.size() == 0) {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                ((CenesBaseActivity) getActivity()).clearAllFragmentsInBackstack();
                                                ((CenesBaseActivity) getActivity()).replaceFragment(new HomeFragment(), null);
                                            }
                                        }, 1000);
                                    }
                                }
                            } else {

                                Log.v("Swipe Cords : ",(xCord - x)+" ===  "+(screenCenter - 150));
                                    if (!cardSwipedToExtent) {
                                        cardSwipedToExtent = true;
                                        invitationAcceptSpinner.setVisibility(View.VISIBLE);
                                        rotate(360, invitationAcceptSpinner);

                                        if (GatheringPreviewFragment.this.event != null && GatheringPreviewFragment.this.event.getEventId() != null && loggedInUser.getUserId() != null) {

                                            String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going";
                                            updateAttendingStatus(queryStr);

                                            if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                                                (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                                                pendingEventIndex++;

                                            } else {

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (sourceFragment != null) {
                                                            if (sourceFragment instanceof HomeFragment) {
                                                                ((HomeFragment) sourceFragment).initialSync();
                                                            } else if (sourceFragment instanceof GatheringsFragment) {
                                                                ((GatheringsFragment) sourceFragment).refreshSelectedTabData();
                                                            }
                                                        }
                                                        ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                                    }
                                                }, 500);
                                            }
                                        }
                                    }
                            }

                        } else {
                            ifSwipedLeftToRight = true;
                        }

                    } else if ((xCord - x) < -100 && enableRightToLeftSwipe && !ifSwipedUp) {
                        //If User swipe from right to left
                        ifSwipedRightToLeft = false;

                        if ((float)((xCord - x)/2 *  (Math.PI/32)) > -15) {
                            tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));

                        }

                        if (Math.abs(xCord - x) > 200) {

                            ifSwipedRightToLeft = true;

                            if (isNewOrEditMode) {

                                //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                            } else {

                                Log.v("Swipe Cords : ",Math.abs(xCord - x)+" ===  "+(screenCenter - 150));
                                if (!cardSwipedToExtent) {
                                    cardSwipedToExtent = true;

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
                                        String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing";
                                        updateAttendingStatus(queryStr);

                                        if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                                            (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                                            pendingEventIndex++;

                                        } else {

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (sourceFragment != null) {
                                                        if (sourceFragment instanceof HomeFragment) {
                                                            ((HomeFragment) sourceFragment).initialSync();
                                                        } else if (sourceFragment instanceof GatheringsFragment) {
                                                            ((GatheringsFragment) sourceFragment).refreshSelectedTabData();
                                                        }
                                                    }
                                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                                }
                                            }, 500);

                                        }
                                    }


                                }

                            }
                        } else {
                            ifSwipedRightToLeft = true;
                        }
                    }


                    //This is when user move the card up
                    Log.d("Y Cords : ","------------------ "+Math.abs(yCord  - y)+" , X Cord : "+Math.abs((xCord - x))+"");
                    if ((yCord - y) < -50) {
                        ifSwipedUp = true;
                        tinderCardView.setY(yCord - y);

                        if ((yCord - y) > -100) {

                            Log.e("Y Crossed  : ", "1000000000 "+rlSkipText.getScaleX()+"");
                            ifSwipedRightToLeft = false;
                            ifSwipedRightToLeft = false;
                            x = 0;
                            xCord = 0;
                            tinderCardView.setX(0);
                            tinderCardView.setRotation(0);
                        }

                    }
                    if ((yCord - y) < -300) {


                        Log.e("Skip Text : ", "SWipppppeedddddd uuppppppppppppppp "+rlSkipText.getScaleX()+"");
                        ifSwipedUp = true;
                        if (!cardSwipedToExtent) {
                            cardSwipedToExtent = true;
                            tinderCardView.setY(-300);
                           /*

                            if ((GatheringPreviewFragment.this).event.getScheduleAs() != null && (GatheringPreviewFragment.this).event.getScheduleAs().equals("Notification")) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
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

                                } else {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getActivity() != null) {
                                                ((CenesBaseActivity) getActivity()).onBackPressed();

                                            }
                                        }
                                    }, 500);

                                }
                            }

                        */}


                    } else {
                    }

                    break;

                default:

                    System.out.println("Default");
            }


            return true;
        }
    };

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
        }
        System.out.println("Going to create Gathering.");
        Gson gson = new Gson();
        try {
            JSONObject postata = new JSONObject(gson.toJson(event));

            new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
            new GatheringAsyncTask.CreateGatheringTask(new GatheringAsyncTask.CreateGatheringTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                    try {

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
                                sendSmsToNonCenesMembers(nonCenesMember, eve);
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

    public void deleteGathering() {

        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        new GatheringAsyncTask.DeleteGatheringTask(new GatheringAsyncTask.DeleteGatheringTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

            }
        }).execute(event.getEventId());

    }

    private void rotate(float degree, ImageView imageView) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }

    public void populateInvitationCard(final Event event) {
        tvEventTitle.setText(event.getTitle());

        final String eventDate = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime())) + "," + CenesUtils.hmmaa.format(new Date(event.getStartTime())) + "-" + CenesUtils.hmmaa.format(new Date(event.getEndTime()));
        tvEventDate.setText(eventDate);

        if (event.getScheduleAs() != null && event.getScheduleAs().equals("Notification")) {

            ///ivEventPicture.setImageDrawable(getResources().getDrawable(R.drawable.welcome_invitation_card));
            rlInvitationView.setVisibility(View.GONE);
            rlWelcomeInvitation.setVisibility(View.VISIBLE);

        } else {
            rlInvitationView.setVisibility(View.VISIBLE);
            rlWelcomeInvitation.setVisibility(View.GONE);

            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                if (((CenesBaseActivity)getActivity()) != null) {

                    Glide.with(getContext())
                            .load(event.getThumbnail())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                                    ivEventPicture.setImageDrawable(resource);

                                    Glide.with(getContext())
                                            .load(event.getEventPicture()).into(new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                            ivEventPicture.setImageDrawable(resource);
                                        }
                                    });

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

                    Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(RequestOptions.centerCropTransform()).into(ivProfilePicView);

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

            ((CenesBaseActivity) getActivity()).clearBackStackInclusive(null);
            ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringsFragment(), null);

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
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } // Author: silentnuke

}
