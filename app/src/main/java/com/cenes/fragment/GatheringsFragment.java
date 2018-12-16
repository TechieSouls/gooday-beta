package com.cenes.fragment;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.GatheringScreenActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Event;
import com.cenes.bo.EventMember;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.UserManager;
import com.cenes.leolin.shortcurtbadger.ShortcutBadger;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 2/11/17.
 */

public class GatheringsFragment extends CenesFragment {

    public final static String TAG = "GatheringsFragment";

    private int CREATE_GATHERING_RESULT_CODE = 1001;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    InternetManager internetManager;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private View fragmentView;

    GatheringsAdapter listAdapter;

    ExpandableListView gatheringsEventsList;

    private TextView homeNoEvents, gatheringsText, tvNotificationCount;
    private RoundedImageView homePageProfilePic;
    private TextView confirmedBtn, maybeBtn, declinedBtn;
    private ImageView createGatheringBtn;

    private GatheringsTask gatheringsTask;
    private NotificationCountTask notificationCountTask;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_gatherings, container, false);
        fragmentView = view;
        init(view);
        User user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(this).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homePageProfilePic);
        }

        Bundle bundle_ = this.getArguments();
        if (bundle_ != null && "push".equals(bundle_.getString("dataFrom"))) {
            new FetchGatheringTask().execute(bundle_);
        } else if (bundle_ != null && "list".equals(bundle_.getString("dataFrom"))) {
            Bundle bundle = new Bundle();
            bundle.putString("dataFrom", "list");
            bundle.putLong("eventId", bundle_.getLong("eventId"));
            this.getArguments().clear();
            CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
            createGatheringFragment.setArguments(bundle);
            ((GatheringScreenActivity) getActivity()).replaceFragment(createGatheringFragment, "CreateGatheringFragment");
        } else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    gatheringsTask = new GatheringsTask();
                    gatheringsTask.execute("Going");
                }
            });
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                notificationCountTask = new NotificationCountTask();
                notificationCountTask.execute();
            }
        });

        return view;
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();

        gatheringsEventsList = (ExpandableListView) view.findViewById(R.id.home_events_list_view);

        homeNoEvents = (TextView) view.findViewById(R.id.home_no_events);
        homePageProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);

        gatheringsText = (TextView) view.findViewById(R.id.gatherings_text);
        gatheringsText.setText("Your Gatherings");

        confirmedBtn = (TextView) view.findViewById(R.id.confirmed_btn);
        maybeBtn = (TextView) view.findViewById(R.id.maybe_btn);
        declinedBtn = (TextView) view.findViewById(R.id.declined_btn);

        createGatheringBtn = (ImageView) view.findViewById(R.id.create_gath_btn);
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count_pic);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        //Listenerss
        confirmedBtn.setOnClickListener(onClickListener);
        maybeBtn.setOnClickListener(onClickListener);
        declinedBtn.setOnClickListener(onClickListener);
        createGatheringBtn.setOnClickListener(onClickListener);
        homePageProfilePic.setOnClickListener(onClickListener);

        //fab.setOnClickListener(onClickListener);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_GATHERING_RESULT_CODE && resultCode == RESULT_OK) {
            new GatheringsTask().execute("Going");
        } else if (requestCode == CREATE_GATHERING_RESULT_CODE && resultCode == RESULT_CANCELED) {
            //Do Nothing
        }
    }*/

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_profile_pic:
                    GatheringScreenActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.confirmed_btn:
                    selectTab(confirmedBtn);
                    gatheringsText.setText("Your Gatherings");
                    new GatheringsTask().execute("Going");
                    break;
                case R.id.maybe_btn:
                    selectTab(maybeBtn);
                    gatheringsText.setText("Your Invitations");
                    new GatheringsTask().execute("pending");
                    break;
                case R.id.declined_btn:
                    selectTab(declinedBtn);
                    gatheringsText.setText("Your Invitations");
                    new GatheringsTask().execute("NotGoing");
                    break;

                case R.id.create_gath_btn:
                    //startActivityForResult(new Intent(getActivity(), CreateGatheringActivity.class), CREATE_GATHERING_RESULT_CODE);
                    //break;
                    fragmentManager = getActivity().getSupportFragmentManager();
                    ((GatheringScreenActivity) getActivity()).replaceFragment(new CreateGatheringFragment(), "cgFragment");
//                    replaceFragment(new ProfileFragment(), "cgFragment");
                    break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gatheringsTask != null) {
            gatheringsTask.cancel(true);
        }
        if (notificationCountTask != null) {
            notificationCountTask.cancel(true);
        }
    }

    public void selectTab(TextView selection) {
        confirmedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        confirmedBtn.setTypeface(Typeface.DEFAULT);
        maybeBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        maybeBtn.setTypeface(Typeface.DEFAULT);
        declinedBtn.setBackground(getResources().getDrawable(R.drawable.border_bottom_gray));
        declinedBtn.setTypeface(Typeface.DEFAULT);

        selection.setBackground(getResources().getDrawable(R.drawable.border_bottom_orange));
        selection.setTypeface(Typeface.DEFAULT_BOLD);
    }

    class GatheringsTask extends AsyncTask<String, String, Boolean> {

        //ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(getActivity());
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (!isCancelled()) {
                String status = strings[0];
                User user = userManager.getUser();
                user.setApiUrl(urlManager.getApiUrl("dev"));


                String queryStr = "?user_id=" + user.getUserId() + "&status=" + status;
                JSONObject gatheringObj = apiManager.getUserGatherings(user, queryStr, getCenesActivity());
                Boolean dataExists = true;
                try {
                    if (gatheringObj.getJSONArray("data") == null || gatheringObj.getJSONArray("data").length() == 0) {
                        dataExists = false;
                        //homeNoEvents.setVisibility(View.VISIBLE);
                        //homeNoEvents.setText("No Event Exists For This Date");
                    } else {

                        JSONArray gatherings = gatheringObj.getJSONArray("data");
                        List<String> headers = new ArrayList<>();
                        Map<String, List<Event>> eventMap = new HashMap<>();
                        List<Event> events = new ArrayList<>();

                        for (int i = 0; i < gatherings.length(); i++) {

                            Event event = new Event();
                            JSONObject eventObj = (JSONObject) gatherings.getJSONObject(i);

                            SimpleDateFormat calCategory = new SimpleDateFormat("EEEE, MMM dd");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d\nEEE");

                            if (eventObj.has("eventId")) {
                                event.setEventId(eventObj.getLong("eventId"));
                            }
                            if (eventObj.has("title")) {
                                event.setTitle(eventObj.getString("title"));
                            }
                            if (eventObj.has("event_picture")) {
                                event.setEventPicture(eventObj.getString("event_picture"));
                            }
                            if (eventObj.has("location") && eventObj.getString("location") != "null") {
                                event.setLocation(eventObj.getString("location"));
                            }
                            if (eventObj.has("startTime")) {
                                Date startDate = new Date(eventObj.getLong("startTime"));
                                event.setStartTime(timeFormat.format(startDate));
                                event.setEventDate(dateFormat.format(startDate));
                            }
                            if (eventObj.has("startTime")) {
                                Date startDate = new Date(eventObj.getLong("startTime"));
                                String dateKey = calCategory.format(startDate) + CenesUtils.getDateSuffix(startDate.getDate());
                                if (CenesUtils.yyyyMMdd.format(startDate).equals(CenesUtils.yyyyMMdd.format(new Date()))) {
                                    dateKey = "TODAY " + dateKey;
                                }
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.DATE, 1);
                                if (CenesUtils.yyyyMMdd.format(startDate).equals(CenesUtils.yyyyMMdd.format(cal.getTime()))) {
                                    dateKey = "TOMORROW " + dateKey;
                                }
                                if (!headers.contains(dateKey)) {
                                    headers.add(dateKey);
                                }
                                if (eventMap.containsKey(dateKey)) {
                                    events = eventMap.get(dateKey);
                                } else {
                                    events = new ArrayList<>();
                                }
                                events.add(event);
                                eventMap.put(dateKey, events);
                            }
                            if (eventObj.has("sender")) {
                                event.setSender(eventObj.getString("sender"));
                            }
                            if (eventObj.has("event_member_id")) {
                                event.setEventMemberId(eventObj.getLong("event_member_id"));
                            }

                            if (eventObj.has("eventMembers")) {
                                JSONArray membersArray = eventObj.getJSONArray("eventMembers");
                                List<EventMember> members = new ArrayList<>();
                                for (int idx = 0; idx < membersArray.length(); idx++) {
                                    JSONObject memberObj = (JSONObject) membersArray.get(idx);
                                    EventMember eventMember = new EventMember();
                                    if (memberObj.has("picture")) {
                                        eventMember.setPicture(memberObj.getString("picture"));
                                    }
                                    if (memberObj.has("name")) {
                                        eventMember.setName(memberObj.getString("name"));
                                    }
                                    if (memberObj.has("owner")) {
                                        eventMember.setOwner(memberObj.getBoolean("owner"));
                                    }
                                    members.add(eventMember);
                                }
                                event.setEventMembers(members);
                            }
                        }

                        boolean isInvitation = false;
                        if (status.equalsIgnoreCase("pending")) {
                            isInvitation = true;
                        }
                  /*  if (status.equalsIgnoreCase("pending")) {
                        listAdapter = new GatheringsAdapter(GatheringsActivity.this, headers, eventMap, true);
                    } else {*/
                        listAdapter = new GatheringsAdapter(GatheringsFragment.this, headers, eventMap, isInvitation);
                        //}

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return dataExists;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            super.onPostExecute(dataExists);
//            mProgressDialog.hide();
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
            if (getActivity() == null) {
                return;
            }
            if (dataExists) {
                gatheringsEventsList.setVisibility(View.VISIBLE);
                gatheringsEventsList.setAdapter(listAdapter);
                homeNoEvents.setVisibility(View.GONE);
            } else {
                gatheringsEventsList.setVisibility(View.GONE);
                homeNoEvents.setVisibility(View.VISIBLE);
                homeNoEvents.setText("You have no gatherings");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            gatheringsTask.cancel(true);
        }
    }

    public class GatheringsAdapter extends BaseExpandableListAdapter {
        GatheringsFragment context;
        List<String> headers;
        Map<String, List<Event>> eventsMap;
        LayoutInflater inflter;
        boolean isInvitation;

        public GatheringsAdapter(GatheringsFragment applicationContext, List<String> headers, Map<String, List<Event>> eventsMap, boolean isInvitation) {
            this.context = applicationContext;
            this.headers = headers;
            this.eventsMap = eventsMap;
            this.isInvitation = isInvitation;
            inflter = (LayoutInflater.from(applicationContext.getCenesActivity()));
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.eventsMap.get(this.headers.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            //if (!isInvitation) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.activity_home_data_rows, null);
                viewHolder = new ViewHolder();
                viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
                viewHolder.eventLocation = (TextView) convertView.findViewById(R.id.event_location);
                viewHolder.eventImage = (RoundedImageView) convertView.findViewById(R.id.event_image);
                viewHolder.eventTime = (TextView) convertView.findViewById(R.id.event_time);
                viewHolder.eventRowItem = (LinearLayout) convertView.findViewById(R.id.event_row_item);
                viewHolder.homeEventMemberImages = (LinearLayout) convertView.findViewById(R.id.home_adapter_event_member_images);
                viewHolder.memberImagesContainer = (LinearLayout) convertView.findViewById(R.id.ll_member_images_container);
                viewHolder.homeEventMemberImagesCount = (TextView) convertView.findViewById(R.id.tv_event_member_images_count);
                viewHolder.homeAdapterHorizontalImageScrollView = (HorizontalScrollView) convertView.findViewById(R.id.home_adapter_horizontal_scroll_view);
                viewHolder.eventId = null;
                viewHolder.trash = (TextView) convertView.findViewById(R.id.trash);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Event child = (Event) getChild(groupPosition, childPosition);

            viewHolder.eventId = child.getEventId();
            viewHolder.eventTitle.setText(child.getTitle());

            if (child.getLocation() == null || child.getLocation().length() == 0) {
                viewHolder.eventLocation.setVisibility(View.GONE);
            } else {
                viewHolder.eventLocation.setVisibility(View.VISIBLE);
                viewHolder.eventLocation.setText(child.getLocation());
            }

               /* if (child.getEventPicture() != null && child.getEventPicture() != "" && child.getEventPicture() != "null") {
                    Glide.with(this.context).load(child.getEventPicture()).into(viewHolder.eventImage);
                } else {
                    viewHolder.eventImage.setImageResource(R.drawable.party_image);
                }*/

            //System.out.println(child.getTitle()+","+child.getEventMembers().size());
            if (child.getEventMembers() != null && child.getEventMembers().size() > 0) {
                viewHolder.homeEventMemberImages.removeAllViews();
                viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.VISIBLE);

                for (int i = 0; i < child.getEventMembers().size(); i++) {
                    EventMember em = child.getEventMembers().get(i);
                    if (i > 2) {
                        viewHolder.homeEventMemberImagesCount.setVisibility(View.VISIBLE);
                        viewHolder.homeEventMemberImagesCount.setText("+" + (child.getEventMembers().size() - 3));
                    } else {
                        viewHolder.homeEventMemberImagesCount.setVisibility(View.GONE);

                        RelativeLayout rlRoot = new RelativeLayout(context.getCenesActivity());
                        rlRoot.setLayoutParams(new RelativeLayout.LayoutParams(CenesUtils.dpToPx(60), CenesUtils.dpToPx(60)));

                        RelativeLayout.LayoutParams profileParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
                        profileParams.setMargins(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));

                        RoundedImageView roundedImageView = new RoundedImageView(context.getCenesActivity(), null);
                        roundedImageView.setLayoutParams(profileParams);
                        roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        RelativeLayout.LayoutParams starParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(30), CenesUtils.dpToPx(30));
                        starParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        starParams.addRule(RelativeLayout.ALIGN_PARENT_END);

                        ImageView ivStar = new ImageView(context.getCenesActivity());
                        ivStar.setLayoutParams(starParams);
                        ivStar.setPadding(CenesUtils.dpToPx(4), CenesUtils.dpToPx(4), CenesUtils.dpToPx(8), CenesUtils.dpToPx(8));
                        ivStar.setImageResource(R.drawable.star);

                        rlRoot.addView(roundedImageView);

                        try {
                            if(em.isOwner()) {
                                rlRoot.addView(ivStar);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        if (em.getPicture() != null && em.getPicture() != "null")
                            Glide.with(context).load(em.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(roundedImageView);
                        else
                            roundedImageView.setImageResource(R.drawable.default_profile_icon);

                        viewHolder.homeEventMemberImages.addView(rlRoot);
                    }
                }
            } else {
                viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.GONE);
            }

            viewHolder.eventTime.setText(child.getStartTime());


            viewHolder.eventRowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fragmentManager = getActivity().getSupportFragmentManager();

                    if (isInvitation) {
                        ((GatheringScreenActivity) getActivity()).hideFooter();
                        InvitationFragment ifFragment = new InvitationFragment();

                        Bundle bundle = new Bundle();
                        //bundle.putString("dataFrom", "GatheringsFragment");
                        bundle.putLong("eventId", viewHolder.eventId);
                        ifFragment.setArguments(bundle);
                        ((GatheringScreenActivity) getActivity()).replaceFragment(ifFragment, "ifFragment");


                    } else {
                        CreateGatheringFragment cgFragment = new CreateGatheringFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("dataFrom", "list");
                        bundle.putLong("eventId", viewHolder.eventId);
                        cgFragment.setArguments(bundle);
                        ((GatheringScreenActivity) getActivity()).replaceFragment(cgFragment, "cgFragment");
                    }
                }
            });

            viewHolder.memberImagesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentManager = getActivity().getSupportFragmentManager();
                    if (isInvitation) {
                        InvitationFragment ifFragment = new InvitationFragment();

                        Bundle bundle = new Bundle();
                        //bundle.putString("dataFrom", "GatheringsFragment");
                        bundle.putLong("eventId", viewHolder.eventId);
                        ifFragment.setArguments(bundle);
                        ((GatheringScreenActivity) getActivity()).replaceFragment(ifFragment, "ifFragment");
                    } else {
                        ((GatheringScreenActivity) getActivity()).hideFooter();
                        CreateGatheringFragment cgFragment = new CreateGatheringFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("dataFrom", "list");
                        bundle.putLong("eventId", viewHolder.eventId);
                        cgFragment.setArguments(bundle);
                        ((GatheringScreenActivity) getActivity()).replaceFragment(cgFragment, "cgFragment");
                    }
                }
            });

            viewHolder.trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DeleteGatheringTask().execute(viewHolder.eventId);
                }
            });
            return convertView;
           /*} else {
                final InvitationsViewHolder viewHolder;
                if (convertView == null) {
                    convertView = inflter.inflate(R.layout.invitations_list_item, null);
                    viewHolder = new InvitationsViewHolder();
                    viewHolder.eventDate = (TextView) convertView.findViewById(R.id.event_date);
                    viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
                    viewHolder.eventTime = (TextView) convertView.findViewById(R.id.event_time);
                    viewHolder.eventDescription = (TextView) convertView.findViewById(R.id.event_desc);
                    viewHolder.eventImage = (ImageView) convertView.findViewById(R.id.event_image);
                    viewHolder.eventRowItem = (LinearLayout) convertView.findViewById(R.id.event_row_item);
                    viewHolder.tvAccept = (TextView) convertView.findViewById(R.id.tvAccept);
                    viewHolder.tvDecline = (TextView) convertView.findViewById(R.id.tvDecline);
                    viewHolder.eventId = null;
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (InvitationsViewHolder) convertView.getTag();
                }

                final Event child = (Event) getChild(groupPosition, childPosition);

                viewHolder.eventId = child.getEventId();
                viewHolder.eventTitle.setText(child.getTitle());
                viewHolder.eventTime.setText(child.getStartTime());
                viewHolder.eventDate.setText(child.getEventDate().toUpperCase());
                viewHolder.eventDescription.setText(Html.fromHtml("<font color=#0000FF>" + child.getSender() + "</font> invited you"));

                *//*if (child.getEventPicture() != null && child.getEventPicture() != "" && child.getEventPicture() != "null") {
                    Glide.with(this.context).load(child.getEventPicture()).into(viewHolder.eventImage);
                } else {
                    viewHolder.eventImage.setImageResource(R.drawable.party_image);
                }*//*

                viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (internetManager.isInternetConnection(getCenesActivity())) {
                            String queryStr = "?event_member_id=" + child.getEventMemberId() + "&status=confirmed";
                            new ActionTask().execute(queryStr);
                        } else {
                            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                viewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (internetManager.isInternetConnection(getCenesActivity())) {
                            String queryStr = "?event_member_id=" + child.getEventMemberId() + "&status=declined";
                            new ActionTask().execute(queryStr);
                        } else {
                            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                return convertView;
            }*/
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.eventsMap.get(this.headers.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.headers.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.headers.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            ExpandableListView mExpandableListView = (ExpandableListView) parent;
            mExpandableListView.expandGroup(groupPosition);
            GatheringsAdapter holder;
            if (convertView == null) {
                convertView = inflter.inflate(R.layout.activity_home_data_headers, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            if (isInvitation) {
                lblListHeader.setVisibility(View.GONE);
            }
            String headerTitle = (String) getGroup(groupPosition);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        public int convertPpToDp(int pp) {
            DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
            return (int) (pp / displayMetrics.density);
        }

        public int convertDpToPp(int dp) {
            DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
            return (int) (dp * displayMetrics.density);
        }

        class ViewHolder {
            private Long eventId;
            private TextView eventTitle;
            private TextView eventLocation;
            private TextView eventTime;
            private RoundedImageView eventImage;
            private LinearLayout eventRowItem;
            private LinearLayout homeEventMemberImages;
            private LinearLayout memberImagesContainer;
            private TextView homeEventMemberImagesCount;
            private HorizontalScrollView homeAdapterHorizontalImageScrollView;
            private TextView trash;
        }

        class InvitationsViewHolder {
            private Long eventId;
            private TextView eventTitle;
            private TextView eventDate;
            private TextView eventDescription;
            private TextView eventTime;
            private ImageView eventImage;
            private LinearLayout eventRowItem;
            private TextView tvAccept;
            private TextView tvDecline;
        }

        class HeaderViewHolder {
            private ExpandableListView expandableListView;
            private TextView lblListHeader;
        }
    }

    class DeleteGatheringTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog deleteGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deleteGathDialog = new ProgressDialog(getActivity());
            deleteGathDialog.setMessage("Deleting..");
            deleteGathDialog.setIndeterminate(false);
            deleteGathDialog.setCanceledOnTouchOutside(false);
            deleteGathDialog.setCancelable(false);
            deleteGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
             User user = userManager.getUser();

            Long eventId = longs[0];
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
                    Toast.makeText(getActivity(), "Gathering Deleted", Toast.LENGTH_SHORT).show();
                    new GatheringsTask().execute("Going");
                } else {
                    Toast.makeText(getActivity(), "Gathering Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class FetchGatheringTask extends AsyncTask<Bundle, Map<String,Object>, Map<String,Object>> {
        ProgressDialog fetchingGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchingGathDialog = new ProgressDialog(getActivity());
            fetchingGathDialog.setMessage("Fetching..");
            fetchingGathDialog.setIndeterminate(false);
            fetchingGathDialog.setCanceledOnTouchOutside(false);
            fetchingGathDialog.setCancelable(false);
            fetchingGathDialog.show();
        }

        @Override
        protected Map<String,Object> doInBackground(Bundle... bundles) {

            Bundle bundle = bundles[0];

            User user = userManager.getUser();

            Long eventId = bundle.getLong("eventId");
            user.setApiUrl(urlManager.getApiUrl("dev"));

            Map<String,Object> gatheringMap = new HashMap<>();
            JSONObject response = apiManager.getEventById(user, eventId, getCenesActivity());
            gatheringMap.put("response",response);
            gatheringMap.put("bundle",bundle);
            return gatheringMap;
        }

        @Override
        protected void onPostExecute(Map<String,Object> responseMap) {
            super.onPostExecute(responseMap);
            fetchingGathDialog.dismiss();

            fetchingGathDialog = null;
            try {

                JSONObject apiResponse = (JSONObject) responseMap.get("response");

                if (apiResponse.getBoolean("success")) {
                    //Toast.makeText(getActivity(), "Gathering ", Toast.LENGTH_SHORT).show();
                    if (apiResponse.getString("data") == "null" || apiResponse.get("data") == null) {
                        Toast.makeText(getActivity(), "Gathering Not Available", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle_ = (Bundle)responseMap.get("bundle");
                        Bundle bundle = new Bundle();
                        bundle.putString("dataFrom", "push");
                        bundle.putLong("eventId", bundle_.getLong("eventId"));
                        bundle.putString("message", bundle_.getString("message"));
                        bundle.putString("title", bundle_.getString("title"));
                        (GatheringsFragment.this).getArguments().clear();
                        InvitationFragment invitationFragment = new InvitationFragment();
                        invitationFragment.setArguments(bundle);
                        ((GatheringScreenActivity) getActivity()).replaceFragment(invitationFragment, "InvitationFragment");
                    }
                } else {
                    Toast.makeText(getActivity(), "Gathering Not Available", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class NotificationCountTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
            //           progressDialog.setCancelable(false);
            //          progressDialog.setMessage("Loading...");
            //progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... strings) {
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?userId="+user.getUserId();
            return apiManager.getNotificationCounts(user,queryStr,getCenesActivity());
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);

            try {
                tvNotificationCount.setText(String.valueOf(s.getInt("data")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //progressDialog.hide();
            //progressDialog.dismiss();
            //progressDialog = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            notificationCountTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GatheringScreenActivity) getActivity()).showFooter();
    }
}
