package com.cenesbeta.fragment.gathering;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
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

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatheringPreviewFragment  extends CenesFragment {

    public static String TAG = "GatheringPreviewFragment";
    private int SMS_COMPOSE_RESULT_CODE = 1001;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private CenesApplication cenesApplication;

    private RoundedImageView homeProfilePic, rivOwnerImage;
    private TextView ownerName, title, description, eDate, eTime, location;
    private ImageView eventPicture;
    private ImageView ivGatheringDate, ivGatheringTime, ivGatheringLocation, ivGatheringGuests;
    private ImageView tvBackPreview;
            private TextView tvSaveGatheirngBtn;
    private Button btnGathStylesheetEdit, btnGathStylesheetShare, btnGathStylesheetClipboard, btnGathStylesheetDelete, btnGathStylesheetDecline;
    private LinearLayout llOwnerSection, llAcceptDecline, llEventLocation, llEventMembers;
    private View viewEventLocationBorder;
    private RelativeLayout rvGathPreviewLayout;
    private View gathStylesheet;
    private RelativeLayout llStyleSheetItems;
    private Button getBtnEditGathStylesheetCancel;
    private ImageView editGatheringIcon;

    private TextView tvGathPreviewTitle;
    private Button btnAccept, btnDecline;

    private CoreManager coreManager;
    private UserManager userManager;
    private User loggedInUser;
    private Event event;
    private EventMember owner;
    private File eventPictureFile;
    private List<EventMember> nonCenesMembers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_gathering_preview, container, false);

        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();
        eventPictureFile = null;
        fragmentManager = getFragmentManager();

        ((CenesBaseActivity)getActivity()).hideFooter();

        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());

        //Headers Buttons
        tvBackPreview = (ImageView) view.findViewById(R.id.tv_back_preview);
        tvSaveGatheirngBtn = (TextView) view.findViewById(R.id.save_gathering_btn);
        btnGathStylesheetEdit = (Button) view.findViewById(R.id.edit_gathering_btn);

        llOwnerSection = (LinearLayout) view.findViewById(R.id.ll_owner_section);
        llAcceptDecline = (LinearLayout) view.findViewById(R.id.ll_accept_decline);
        llEventLocation = (LinearLayout) view.findViewById(R.id.ll_event_location);
        llEventMembers = (LinearLayout) view.findViewById(R.id.ll_event_members);

        ivGatheringDate = (ImageView) view.findViewById(R.id.iv_gathering_date);
        ivGatheringTime = (ImageView) view.findViewById(R.id.iv_gathering_time);
        ivGatheringLocation = (ImageView) view.findViewById(R.id.iv_gathering_location);
        ivGatheringGuests = (ImageView) view.findViewById(R.id.iv_gathering_guests);

        viewEventLocationBorder = (View) view.findViewById(R.id.view_event_location_border);
        rvGathPreviewLayout = (RelativeLayout) view.findViewById(R.id.rv_gath_preview_layout);

        editGatheringIcon = (ImageView) view.findViewById(R.id.edit_gathering_icon);
        btnGathStylesheetShare = (Button) view.findViewById(R.id.btn_gath_stylesheet_share);
        llStyleSheetItems = (RelativeLayout) view.findViewById(R.id.ll_style_sheet_items);
        gathStylesheet = (View) view.findViewById(R.id.rv_edit_gath_stylesheet);
        getBtnEditGathStylesheetCancel = (Button) view.findViewById(R.id.btn_edit_gath_stylesheet_cancel);
        btnGathStylesheetClipboard = (Button) view.findViewById(R.id.btn_gath_stylesheet_clipboard);
        btnGathStylesheetDelete = (Button) view.findViewById(R.id.btn_gath_stylesheet_delete);
        btnGathStylesheetDecline = (Button) view.findViewById(R.id.btn_gath_stylesheet_decline);

        rivOwnerImage = (RoundedImageView) view.findViewById(R.id.riv_owner_image);
        ownerName = (TextView) view.findViewById(R.id.tv_owner_name);
        eventPicture = (ImageView) view.findViewById(R.id.iv_gathering_img);
        title = (TextView) view.findViewById(R.id.title);
        description = (TextView) view.findViewById(R.id.description);
        eDate = (TextView)view.findViewById(R.id.eDate);
        eTime = (TextView) view.findViewById(R.id.eTime);
        location = (TextView) view.findViewById(R.id.location);
        tvGathPreviewTitle = (TextView) view.findViewById(R.id.tv_gath_preview_title);

        btnAccept = (Button) view.findViewById(R.id.btn_accept);
        btnDecline = (Button) view.findViewById(R.id.btn_decline);


        //Register Listeners
        tvBackPreview.setOnClickListener(onClickListener);
        tvSaveGatheirngBtn.setOnClickListener(onClickListener);
        btnGathStylesheetEdit.setOnClickListener(onClickListener);
        btnGathStylesheetDelete.setOnClickListener(onClickListener);
        btnGathStylesheetDecline.setOnClickListener(onClickListener);
        btnGathStylesheetClipboard.setOnClickListener(onClickListener);
        btnGathStylesheetShare.setOnClickListener(onClickListener);
        btnAccept.setOnClickListener(onClickListener);
        btnDecline.setOnClickListener(onClickListener);
        llEventMembers.setOnClickListener(onClickListener);
        llEventLocation.setOnClickListener(onClickListener);

        editGatheringIcon.setOnClickListener(onClickListener);
        getBtnEditGathStylesheetCancel.setOnClickListener(onClickListener);

        final Bundle eventBundle = getArguments();

        if (eventBundle != null) {
            //If user clicked on the gathering at Home or Gatherings List
            if (eventBundle.getString("dataFrom") != null && eventBundle.getString("dataFrom").equals("list")) {

                tvGathPreviewTitle.setText("Event");

                //We will hide the SEND Button and visible the EDIT Button
                tvSaveGatheirngBtn.setVisibility(View.GONE);

                System.out.println(eventBundle.getLong("eventId"));
                Long eventLong = eventBundle.getLong("eventId");
                new GatheringAsyncTask.EventInfoTask(new GatheringAsyncTask.EventInfoTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {
                        Gson gson = new Gson();
                        try {
                            event = gson.fromJson(response.getJSONObject("data").toString(), Event.class);
                            editGatheringIcon.setVisibility(View.VISIBLE);

                            if (loggedInUser.getUserId() != event.getCreatedById()) {

                                //btnGathStylesheetEdit.setVisibility(View.GONE);
                            } else {
                                //btnGathStylesheetEdit.setVisibility(View.VISIBLE);
                            }
                            System.out.println(response.toString());
                            updateUIWithEventInfo();
                            attendeePreviewModeConfig();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(eventLong);
            } else if (eventBundle.getString("dataFrom") != null && eventBundle.getString("dataFrom").equals("notification")) {

                tvGathPreviewTitle.setText("Event");

                editGatheringIcon.setVisibility(View.VISIBLE);

                invitationPreviewModeConfig();
                System.out.println(eventBundle.getLong("eventId"));
                Long eventLong = eventBundle.getLong("eventId");
                new GatheringAsyncTask.EventInfoTask(new GatheringAsyncTask.EventInfoTask.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject response) {
                        System.out.println("Event Resp : "+response.toString());

                            Gson gson = new Gson();
                            try {
                                if (response.getBoolean("success")) {

                                    event = gson.fromJson(response.getJSONObject("data").toString(), Event.class);

                                    if (event.getExpired() == true) {

                                        getFragmentManager().popBackStack();
                                        ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringExpiredFragment(), GatheringExpiredFragment.TAG);

                                    } else {

                                        boolean userFound = false;
                                        //Checkout for the status of LoggedIn User for The Event
                                        List<EventMember> eventMembers = event.getEventMembers();
                                        if (eventMembers.size() > 0) {

                                            for (EventMember eventMember: eventMembers) {

                                                if (eventMember.getUserId() != null && loggedInUser.getUserId() != event.getCreatedById() && loggedInUser.getUserId() == eventMember.getUserId()) {
                                                    userFound = true;
                                                    llAcceptDecline.setVisibility(View.VISIBLE);

                                                    String status = eventMember.getStatus();
                                                    if ("Going".equals(status)) {
                                                        btnAccept.setBackgroundColor(getResources().getColor(R.color.cenes_selectedText_color));
                                                        btnAccept.setEnabled(false);
                                                        btnDecline.setBackgroundColor(getResources().getColor(R.color.cenes_unselectedText_color));
                                                    } else if ("NotGoing".equals(status)) {
                                                        btnAccept.setBackgroundColor(getResources().getColor(R.color.cenes_unselectedText_color));
                                                        btnDecline.setBackgroundColor(getResources().getColor(R.color.transparent));
                                                        btnDecline.setEnabled(false);

                                                        ivGatheringDate.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gathering_unseelcted_date_icon));
                                                        ivGatheringTime.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gathering_unselected_time_icon));
                                                        ivGatheringLocation.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gathering_unselected_location_icon));
                                                        ivGatheringGuests.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.gathering__unselcetd_people_icon));
                                                    }
                                                } else if (eventMember.getUserId() != null && loggedInUser.getUserId() == event.getCreatedById() && loggedInUser.getUserId() == eventMember.getUserId()) {
                                                    userFound = true;
                                                }
                                            }
                                        }

                                        if (!userFound) {
                                            getFragmentManager().popBackStack();
                                            ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringExpiredFragment(), GatheringExpiredFragment.TAG);
                                        } else {
                                            System.out.println(response.toString());
                                            updateUIWithEventInfo();
                                        }
                                    }

                                } else {
                                    //GatheringExpiredFragment gatheringExpiredFragment = new GatheringExpiredFragment();
                                    //((CenesBaseActivity) getActivity()).clearFragmentsAndOpen(gatheringExpiredFragment);


                                    getFragmentManager().popBackStack();
                                    ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringExpiredFragment(), GatheringExpiredFragment.TAG);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                    }
                }).execute(eventLong);
            } else {


                tvGathPreviewTitle.setText("Preview Gathering");

                //We will hide the EDIT Button and visible the SEND Button
                btnGathStylesheetEdit.setVisibility(View.GONE);
                tvSaveGatheirngBtn.setVisibility(View.VISIBLE);
                llAcceptDecline.setVisibility(View.GONE);

                System.out.println(eventBundle.getString("eventJson"));
                try {
                    JSONObject eventJson = new JSONObject(eventBundle.getString("eventJson"));
                    Gson gson = new Gson();
                    event = gson.fromJson(eventBundle.getString("eventJson").toString(), Event.class);
                    updateUIWithEventInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.edit_gathering_btn:
                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId", event.getEventId());
                    CreateGatheringFragment cgFragment = new CreateGatheringFragment();
                    cgFragment.setArguments(bundle);
                    ((CenesBaseActivity) getActivity()).replaceFragment(cgFragment, CreateGatheringFragment.TAG);
                    break;
               /* case R.id.tv_rsvp_list_con:

                    if (rsvpAttendees.getVisibility() == View.VISIBLE) {
                        rsvpAttendees.setVisibility(View.GONE);
                    } else {
                        rsvpAttendees.setVisibility(View.VISIBLE);
                    }
                    break;*/

                case R.id.tv_back_preview:
                    //getActivity().onBackPressed();
                    getFragmentManager().popBackStack();

                    /*getFragmentManager()
                            .beginTransaction()
                            .remove(GatheringPreviewFragment.this).attach(CreateGatheringFragment)
                            .commit();*/
                    break;

                case R.id.save_gathering_btn:

                    Gson gson = new Gson();
                    String createObjStr = gson.toJson(event, Event.class);
                    try {
                        JSONObject createJSONObj = new JSONObject(createObjStr);
                        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());
                        new GatheringAsyncTask.CreateGatheringTask(new GatheringAsyncTask.CreateGatheringTask.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject response) {
                                try {
                                    if (response.getBoolean("success")) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Gathering Created", Toast.LENGTH_SHORT).show();
                                        final JSONObject eventData = response.getJSONObject("data");

                                        if (eventPictureFile != null) {

                                            Map<String, Object> postMapObject = new HashMap<>();
                                            postMapObject.put("eventId", eventData.getLong("eventId"));
                                            postMapObject.put("file", eventPictureFile);
                                            new GatheringAsyncTask.UploadImageTask(new GatheringAsyncTask.UploadImageTask.AsyncResponse() {
                                                @Override
                                                public void processFinish(JSONObject response) {
                                                    sendSmsToNonCenesMembers(nonCenesMembers, eventData);
                                                }
                                            }).execute(postMapObject);
                                        } else {
                                            sendSmsToNonCenesMembers(nonCenesMembers, eventData);
                                        }

                                    } else {
                                        Toast.makeText(getActivity().getApplicationContext(), "Error in creating gathering", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(createJSONObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.btn_accept:
                    String acceptQueryStr = "eventId=" +event.getEventId()+ "&userId="+loggedInUser.getUserId() + "&status=Going";
                    new GatheringAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());
                    new GatheringAsyncTask.UpdateStatusActionTask(new GatheringAsyncTask.UpdateStatusActionTask.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean response) {
                            //Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            //getActivity().fragmentManager
                                //    .beginTransaction()
                                 //   .detach(currentFragment)
                                 //   .attach(currentFragment)
                                 //   .commit();

                            getActivity().onBackPressed();
                        }
                    }).execute(acceptQueryStr);
                    break;

                case R.id.btn_decline:
                    String declineQueryStr = "eventId=" +event.getEventId()+ "&userId="+loggedInUser.getUserId() + "&status=NotGoing";
                    new GatheringAsyncTask(cenesApplication, (CenesBaseActivity) getActivity());
                    new GatheringAsyncTask.UpdateStatusActionTask(new GatheringAsyncTask.UpdateStatusActionTask.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean response) {
                            //Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            //getActivity().fragmentManager
                            //    .beginTransaction()
                            //   .detach(currentFragment)
                            //   .attach(currentFragment)
                            //   .commit();

                            getActivity().onBackPressed();
                        }
                    }).execute(declineQueryStr);
                    break;

                case R.id.ll_event_location:
                    if (event.getLatitude() != null && event.getLongitude() != null) {
                        String uri = "http://maps.google.com/maps?saddr=&daddr=" + event.getLatitude() + "," + event.getLongitude();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }

                    break;
                case R.id.ll_event_members:

                    Gson membersGson = new Gson();
                    String membersStr = membersGson.toJson(event.getEventMembers());

                    Bundle membersBundle = new Bundle();
                    membersBundle.putString("members", membersStr);
                    InviteesFragment inviteesFragment = new InviteesFragment();
                    inviteesFragment.setArguments(membersBundle);

                    ((CenesBaseActivity)getActivity()).replaceFragment(inviteesFragment, InviteesFragment.TAG);
                    break;

                case R.id.btn_edit_gath_stylesheet_cancel:
                    llEventMembers.setEnabled(true);
                    llEventLocation.setEnabled(true);
                    gathStylesheet.setVisibility(View.GONE);
                    llStyleSheetItems.setVisibility(View.GONE);
                    rvGathPreviewLayout.setEnabled(true);
                    rvGathPreviewLayout.setClickable(true);
                    break;

                case R.id.edit_gathering_icon:
                    llEventMembers.setEnabled(false);
                    llEventLocation.setEnabled(false);
                    gathStylesheet.setVisibility(View.VISIBLE);
                    llStyleSheetItems.setVisibility(View.VISIBLE);
                    rvGathPreviewLayout.setEnabled(false);
                    rvGathPreviewLayout.setClickable(false);
                    break;

                case R.id.btn_gath_stylesheet_share:

                    gathStylesheet.setVisibility(View.GONE);
                    llStyleSheetItems.setVisibility(View.GONE);


                    String hostName = "";
                    if (owner != null) {
                        hostName = owner.getName();
                    } else {
                        hostName = loggedInUser.getName();
                    }
                    String message  = CenesConstants.shareInvitationMessage.replaceAll("\\[Host\\]", hostName).replaceAll("\\[Title\\]",event.getTitle());
                    message += "\n"+CenesConstants.webDomainEventUrl+event.getKey();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    break;

                case R.id.btn_gath_stylesheet_clipboard:

                    String clipHostName = "";
                    if (owner != null) {
                        clipHostName = owner.getName();
                    } else {
                        clipHostName = loggedInUser.getName();
                    }
                    //String clipMessage  = CenesConstants.shareInvitationMessage.replaceAll("\\[Host\\]", clipHostName).replaceAll("\\[Title\\]",event.getTitle());
                    String clipMessage = CenesConstants.webDomainEventUrl+event.getKey();

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Link Copied", clipMessage);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Link Copied", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.btn_gath_stylesheet_decline:
                    btnDecline.performClick();
                    break;
                case R.id.btn_gath_stylesheet_delete:

                    new GatheringAsyncTask.DeleteGatheringTask(new GatheringAsyncTask.DeleteGatheringTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            try {
                                if (response != null && response.getBoolean("success")) {
                                    Toast.makeText(getActivity(), "Gathering Deleted", Toast.LENGTH_SHORT).show();

                                    ((CenesBaseActivity)getActivity()).clearBackStackInclusive(null);
                                    ((CenesBaseActivity)getActivity()).replaceFragment(new GatheringsFragment(), null);
                                    //((HomeFragment)context.getVisibleFragment()).initialSync();
                                    /*Fragment currentFragment = context.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                    context.fragmentManager
                                            .beginTransaction()
                                            .detach(currentFragment)
                                            .attach(currentFragment)
                                            .commit();*/

                                } else {
                                    Toast.makeText(getActivity(), "Gathering Not Deleted", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(event.getEventId());
                    break;

            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMS_COMPOSE_RESULT_CODE) {//We will not check
            // if user canceled or send the sms
            getActivity().onBackPressed();
        }
    }

    public void updateUIWithEventInfo() {

        ((CenesBaseActivity)getActivity()).parentEvent = event;

        try {
            title.setText(event.getTitle());

            System.out.println(event.toString());
            //If we have an image uri that means user has uploaded the image fro mobile
            //And we have to upload that image to server.
            //We will create a file object from that image uri.
            if (event.getEventImageURI() != null) {
                Uri eventImageUri = Uri.parse(event.getEventImageURI());
                eventPicture.setImageURI(eventImageUri);
                eventPicture.setImageURI(eventImageUri);

                eventPictureFile = new File(event.getEventImageURI());

            } else if (event.getEventPicture() != null) {
                Glide.with(GatheringPreviewFragment.this).load(event.getEventPicture()).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(eventPicture);
            } else {


                //We will hide the ImageView if the the parentEvent does not have any image.
                eventPicture.setVisibility(View.GONE);
                LinearLayout.LayoutParams  llOwnerSectionParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                llOwnerSectionParams.setMargins(CenesUtils.dpToPx(20), CenesUtils.dpToPx(75), CenesUtils.dpToPx(30), CenesUtils.dpToPx(0));
                llOwnerSection.setLayoutParams(llOwnerSectionParams);
            }

            if (event.getLocation() != null && event.getLocation().length() > 0) {
                location.setText(event.getLocation());
            } else {
                llEventLocation.setVisibility(View.GONE);
                viewEventLocationBorder.setVisibility(View.GONE);
                location.setVisibility(View.GONE);
            }

            if (event.getDescription() != null) {
                description.setText(event.getDescription());
            } else {
                description.setVisibility(View.GONE);
            }

            Long startTimeInMillis = Long.valueOf(event.getStartTime());
            Long endTimeInMillis = Long.valueOf(event.getEndTime());
            String dateKey = CenesUtils.ddMMM.format(new Date(startTimeInMillis)).toUpperCase() + "<b>"+CenesUtils.EEEE.format(new Date(startTimeInMillis)).toUpperCase()+"</b>";
            eDate.setText(Html.fromHtml(dateKey));

            eTime.setText(CenesUtils.hmmaa.format(new Date(startTimeInMillis))+"-"+CenesUtils.hmmaa.format(new Date(endTimeInMillis)));
            /*if (parentEvent.getEventPicture() != null) {
                Glide.with(GatheringPreviewFragment.this).load(parentEvent.getEventPicture()).apply(RequestOptions.placeholderOf(R.drawable.gath_upload_img)).into(eventPicture);
            }*/


            if (event.getEventMembers() == null || event.getEventMembers().size() == 0) {
                llEventMembers.setVisibility(View.GONE);
            }
            //Lets make the set of 4 Members each in the list
            nonCenesMembers = new ArrayList<>();
            int count = 0;
            Map<Integer, List<EventMember>> eventMEmbersSet = new HashMap<>();
            List<EventMember> eventMembers = event.getEventMembers();
            List<EventMember> membersExceptowner = new ArrayList<>();

            EventMember eventOwner = null;

            if (eventMembers != null && eventMembers.size() > 0) {
                for (EventMember eventMember: eventMembers) {
                    //Condition for owner
                    System.out.println(eventMember.getUserId() +"=="+ event.getCreatedById());
                    if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                        eventOwner = eventMember;
                        owner = eventMember;
                    }
                    if (eventMember.getUserId() != null && !event.getCreatedById().equals(eventMember.getUserId())) {
                        membersExceptowner.add(eventMember);
                    }

                    if (eventMember.getUserId() == null) {
                        nonCenesMembers.add(eventMember);
                    }
                }
            }

            try {
                //This means it is an parentEvent being created by App User
                if (eventOwner == null) {
                    Glide.with(GatheringPreviewFragment.this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(rivOwnerImage);
                    ownerName.setText(loggedInUser.getName());
                } else {
                    Glide.with(GatheringPreviewFragment.this).load(eventOwner.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(rivOwnerImage);
                    ownerName.setText(eventOwner.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Hiding Controls for Non host
            if (eventOwner != null && eventOwner.getUserId() != loggedInUser.getUserId()) {
                System.out.println(eventOwner.toString());
                //Hide
                //1. Delete Control
                //2. Edit Control
                btnGathStylesheetDelete.setVisibility(View.GONE);
                btnGathStylesheetEdit.setVisibility(View.GONE);
            } else {
                btnGathStylesheetDecline.setVisibility(View.GONE);
            }


            System.out.println("Events Member Site: "+eventMEmbersSet.size());
            //rsvpAttendees.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSmsToNonCenesMembers(List<EventMember> nonCenesMemberList, JSONObject eventData) {
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

            Gson gson = new Gson();
            event = gson.fromJson(eventData.toString(), Event.class);


            ((CenesBaseActivity) getActivity()).clearBackStackInclusive(null);
            ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringsFragment(), null);

            String clipHostName = "";
            if (owner != null) {
                clipHostName = owner.getName();
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


        } else {
            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            for (Fragment fragment: ((CenesBaseActivity) getActivity()).fragmentManager.getFragments()) {
                System.out.println("Fragment Tag : "+fragment.getTag());
               if (fragment instanceof HomeFragment) {
                   ((CenesBaseActivity) getActivity()).clearBackStackInclusive(null);
                   ((CenesBaseActivity) getActivity()).replaceFragment(new HomeFragment(), null);

                    //((CenesBaseActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                } else if (fragment instanceof GatheringsFragment) {

                   ((CenesBaseActivity) getActivity()).clearBackStackInclusive(null);
                   ((CenesBaseActivity) getActivity()).replaceFragment(new GatheringsFragment(), null);

                    //((CenesBaseActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GatheringsFragment()).addToBackStack(null).commit();
                }
            }
        }
    }


    public void ownerPreviewModeConfig() {
        llAcceptDecline.setVisibility(View.VISIBLE);
        //We will hide the SEND Button and visible the EDIT Button
        tvSaveGatheirngBtn.setVisibility(View.GONE);
    }

    public void attendeePreviewModeConfig() {
        llAcceptDecline.setVisibility(View.GONE);
        //We will hide the SEND Button and visible the EDIT Button
    }

    public void invitationPreviewModeConfig() {
        //We will hide the SEND Button and visible the EDIT Button
        tvSaveGatheirngBtn.setVisibility(View.GONE);
    }

}
