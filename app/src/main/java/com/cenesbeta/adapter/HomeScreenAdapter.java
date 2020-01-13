package com.cenesbeta.adapter;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenesbeta.AsyncTasks.GatheringAsyncTask;
import com.cenesbeta.R;
import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.coremanager.CoreManager;
import com.cenesbeta.database.impl.EventManagerImpl;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.fragment.dashboard.HomeFragment;
import com.cenesbeta.fragment.gathering.GatheringExpiredFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragment;
import com.cenesbeta.fragment.gathering.GatheringPreviewFragmentBkup;
import com.cenesbeta.util.CenesUtils;
import com.cenesbeta.util.RoundedImageView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 23/8/17.
 */

public class HomeScreenAdapter extends BaseExpandableListAdapter {

    private int GATHERING_SUMMARY_RESULT_CODE = 1001, CREATE_GATHERING_RESULT_CODE = 1002;

    HomeFragment homeFragment;
    List<String> headers;
    Map<String, List<Event>> eventsMap;
    LayoutInflater inflter;
    User loggedInUser;

    public HomeScreenAdapter(HomeFragment homeFragment, List<String> headers, Map<String, List<Event>> eventsMap) {
        this.homeFragment = homeFragment;
        this.headers = headers;
        this.eventsMap = eventsMap;

        CoreManager coreManager = homeFragment.getCenesActivity().getCenesApplication().getCoreManager();
        loggedInUser = coreManager.getUserManager().getUser();

        inflter = (LayoutInflater.from(homeFragment.getContext()));
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

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_rows, null);
            viewHolder = new ViewHolder();
            viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
            viewHolder.eventLocation = (TextView) convertView.findViewById(R.id.event_location);
            viewHolder.reminderTime = (TextView) convertView.findViewById(R.id.tv_reminder_time);
            viewHolder.eventBar = convertView.findViewById(R.id.event_bar);
            viewHolder.llEventRowItem = (LinearLayout) convertView.findViewById(R.id.event_row_item);
            viewHolder.llReminderRowItem = (LinearLayout) convertView.findViewById(R.id.ll_reminder_row_item);
            viewHolder.divider = (View) convertView.findViewById(R.id.view_divider);
            viewHolder.startTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.ivOwnerImage = (RoundedImageView) convertView.findViewById(R.id.iv_owner);

            viewHolder.llCenesEvents = (LinearLayout) convertView.findViewById(R.id.ll_cenes_events);

            viewHolder.llTpEvents = (LinearLayout) convertView.findViewById(R.id.ll_tp_events);
            viewHolder.tvTpEventTitle = (TextView) convertView.findViewById(R.id.tv_holiday_title);
            viewHolder.tvTpSource = (TextView) convertView.findViewById(R.id.tv_tp_source);

            viewHolder.llHolidayEvents = (LinearLayout) convertView.findViewById(R.id.ll_holiday);
            viewHolder.tvHolidayTitle = (TextView) convertView.findViewById(R.id.tv_holiday_title);

            viewHolder.tvTpEventTitle = (TextView) convertView.findViewById(R.id.tv_tp_event_title);
            viewHolder.tvTpSource = (TextView) convertView.findViewById(R.id.tv_tp_source);
            viewHolder.tvTpStartTime = (TextView) convertView.findViewById(R.id.tv_tp_start_time);
            viewHolder.tvReminderTitle = (TextView) convertView.findViewById(R.id.tv_reminder_title);
            viewHolder.trash = (TextView) convertView.findViewById(R.id.trash);
            viewHolder.tvDecline = (TextView) convertView.findViewById(R.id.tv_decline);
            viewHolder.eventId = null;
            viewHolder.scheduleAs = null;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Event child = (Event) getChild(groupPosition, childPosition);

        if (childPosition == 0) {
            viewHolder.divider.setVisibility(View.GONE);
        } else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }

        EventMember owner = null;
        if (child.getEventMembers() != null) {

            for (EventMember ownerMember: child.getEventMembers()) {

                if (ownerMember.getUserId() != null && ownerMember.getUserId().equals(child.getCreatedById())) {
                    owner = ownerMember;
                    break;
                }
            }
            if (owner != null) {

                if (owner.getUser() != null && !CenesUtils.isEmpty(owner.getUser().getPicture())) {

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile_pic_no_image);
                    requestOptions.circleCrop();
                    Glide.with(homeFragment.getContext()).load(owner.getUser().getPicture()).apply(requestOptions).into(viewHolder.ivOwnerImage);

                    final String ownerImage = owner.getUser().getPicture();
                    viewHolder.ivOwnerImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((CenesBaseActivity)homeFragment.getActivity()).zoomImageFromThumb(viewHolder.ivOwnerImage, ownerImage);
                        }
                    });
                } else {
                    viewHolder.ivOwnerImage.setImageResource(R.drawable.profile_pic_no_image);
                }
            }

        }

            viewHolder.llEventRowItem.setVisibility(View.VISIBLE);
            viewHolder.llReminderRowItem.setVisibility(View.GONE);
            viewHolder.scheduleAs = child.getScheduleAs();
            viewHolder.eventId = child.getEventId();

            viewHolder.loggedInUserEventMemberData = child.getUserEventMemberData();
            if (child.getScheduleAs().equals("Gathering")) { //cenes events

                viewHolder.llCenesEvents.setVisibility(View.VISIBLE);
                viewHolder.llTpEvents.setVisibility(View.GONE);
                viewHolder.llHolidayEvents.setVisibility(View.GONE);

                viewHolder.eventTitle.setText(child.getTitle());
                if (child.getLocation() == null || child.getLocation().length() == 0) {
                    viewHolder.eventLocation.setVisibility(View.GONE);
                } else {
                    viewHolder.eventLocation.setVisibility(View.VISIBLE);
                    viewHolder.eventLocation.setText(child.getLocation());
                }
                viewHolder.startTime.setText(CenesUtils.hhmmaa.format(new Date(child.getStartTime())));

                if (child.getOwner() != null && child.getOwner().getUser() != null) {
                    Glide.with(homeFragment.getContext()).load(child.getOwner().getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.profile_pic_no_image)).into(viewHolder.ivOwnerImage);
                }

            } else if (child.getScheduleAs().equals("Holiday")) { //holidays

                viewHolder.llCenesEvents.setVisibility(View.GONE);
                viewHolder.llTpEvents.setVisibility(View.GONE);
                viewHolder.llHolidayEvents.setVisibility(View.VISIBLE);

                viewHolder.tvHolidayTitle.setText(child.getTitle());
                viewHolder.startTime.setText(CenesUtils.hhmmaa.format(new Date(child.getStartTime())));

            } else if (child.getScheduleAs().equals("Event")) { //third party events

                viewHolder.llCenesEvents.setVisibility(View.GONE);
                viewHolder.llTpEvents.setVisibility(View.VISIBLE);
                viewHolder.llHolidayEvents.setVisibility(View.GONE);

                viewHolder.tvTpEventTitle.setText(child.getTitle());
                viewHolder.tvTpSource.setText(child.getSource());
                if (child.getIsFullDay() != null && child.getIsFullDay()) {
                    viewHolder.tvTpStartTime.setText("00:00AM");
                } else {
                    viewHolder.tvTpStartTime.setText(CenesUtils.hhmmaa.format(new Date(child.getStartTime())));
                }
            }

            if (child.getCreatedById().equals(homeFragment.loggedInUser.getUserId())) {
                viewHolder.tvDecline.setVisibility(View.GONE);
                viewHolder.trash.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvDecline.setVisibility(View.VISIBLE);
                viewHolder.trash.setVisibility(View.GONE);
            }

            viewHolder.llEventRowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(child.toString());
                    if (viewHolder.scheduleAs.equals("Gathering")) {
                        /*Intent data = new Intent(context, GatheringScreenActivity.class);
                        data.putExtra("dataFrom", "list");
                        data.putExtra("eventId", viewHolder.eventId);
                        //context.startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                        context.startActivity(data);*/

                        //if (child.getExpired()) {
                          //  GatheringExpiredFragment gatheringExpiredFragment = new GatheringExpiredFragment();
                            //context.replaceFragment(gatheringExpiredFragment, GatheringExpiredFragment.TAG);
                        //} else {
                           /* Bundle bundle = new Bundle();
                            bundle.putString("dataFrom", "list");
                            bundle.putLong("eventId", viewHolder.eventId);
                            GatheringPreviewFragmentBkup gatheringPreviewFragmentBkup = new GatheringPreviewFragmentBkup();
                            gatheringPreviewFragmentBkup.setArguments(bundle);
                            context.replaceFragment(gatheringPreviewFragmentBkup, GatheringPreviewFragmentBkup.TAG);*/

                        GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                        gatheringPreviewFragment.event = child;
                        gatheringPreviewFragment.sourceFragment = homeFragment;
                        ((CenesBaseActivity) homeFragment.getCenesActivity()).replaceFragment(gatheringPreviewFragment, HomeFragment.TAG);
                        //}


                    }
                }
            });

            /*viewHolder.memberImagesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent(context, GatheringScreenActivity.class);
                    data.putExtra("dataFrom", "list");
                    data.putExtra("eventId", viewHolder.eventId);
                    //context.startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                    context.startActivity(data);

                }
            });*/

            viewHolder.trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DeleteGatheringTask().execute(viewHolder.eventId);
                }
            });

            viewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String declineQueryStr = "eventId=" +viewHolder.eventId+ "&userId="+loggedInUser.getUserId() + "&status=NotGoing";
                    new GatheringAsyncTask(homeFragment.getCenesActivity().getCenesApplication(), ((CenesBaseActivity) homeFragment.getActivity()));
                    new GatheringAsyncTask.UpdateStatusActionTask(new GatheringAsyncTask.UpdateStatusActionTask.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean response) {
                            //((HomeFragment)context.getVisibleFragment()).initialSync();
                            if (homeFragment.getCenesActivity() != null) {
                                Fragment currentFragment = homeFragment.getCenesActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                ((CenesBaseActivity)homeFragment.getCenesActivity()).fragmentManager
                                        .beginTransaction()
                                        .detach(currentFragment)
                                        .attach(currentFragment)
                                        .commit();
                            }
                        }
                    }).execute(declineQueryStr);
                }
            });

        //}

        return convertView;
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
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_headers, null);
            holder = new HeaderViewHolder();
            holder.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String headerTitle = (String) getGroup(groupPosition);

        try {
            String dateKey = CenesUtils.ddMMM.format(CenesUtils.yyyyMMdd.parse(headerTitle)).toUpperCase() + "<b>"+CenesUtils.EEEE.format(CenesUtils.yyyyMMdd.parse(headerTitle)).toUpperCase()+"</b>";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //String dateKey = calCategory.format(startDate) + CenesUtils.getDateSuffix(startDate.getDate());
            if (sdf.format(CenesUtils.yyyyMMdd.parse(headerTitle)).equals(sdf.format(new Date()))) {
                dateKey = "TODAY ";
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 1);
            if (sdf.format(CenesUtils.yyyyMMdd.parse(headerTitle)).equals(sdf.format(cal.getTime()))) {
                dateKey = "TOMORROW";
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.lblListHeader.setText(Html.fromHtml(dateKey, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.lblListHeader.setText(Html.fromHtml(dateKey));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        DisplayMetrics displayMetrics = homeFragment.getResources().getDisplayMetrics();
        return (int) (pp / displayMetrics.density);
    }

    public int convertDpToPp(int dp) {
        DisplayMetrics displayMetrics = homeFragment.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    static class ViewHolder {
        private Long eventId;
        private Long userId;
        private TextView eventTitle;
        private TextView eventLocation;
        private View eventBar;
        private LinearLayout llEventRowItem;
        private LinearLayout llReminderRowItem;
        private TextView reminderTime;
        private View divider;
        private TextView startTime;
        private RoundedImageView ivOwnerImage;

        private LinearLayout llCenesEvents;

        private LinearLayout llTpEvents;
        private TextView tvTpEventTitle;
        private TextView tvTpSource;
        private TextView tvTpStartTime;

        private LinearLayout llHolidayEvents;
        private TextView tvHolidayTitle;
        //private LinearLayout homeEventMemberImages;
        //private LinearLayout memberImagesContainer;
        //private TextView homeEventMemberImagesCount;
        private TextView tvReminderTitle;
        private String scheduleAs;
        //private HorizontalScrollView homeAdapterHorizontalImageScrollView;
        private TextView trash;
        private TextView tvDecline;
        private EventMember loggedInUserEventMemberData;
    }

    static class HeaderViewHolder {
        private ExpandableListView expandableListView;
        private TextView lblListHeader;
    }

    class DeleteGatheringTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog deleteGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deleteGathDialog = new ProgressDialog(homeFragment.getContext());
            deleteGathDialog.setMessage("Deleting..");
            deleteGathDialog.setIndeterminate(false);
            deleteGathDialog.setCanceledOnTouchOutside(false);
            deleteGathDialog.setCancelable(false);
            deleteGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
            UserManager userManager = homeFragment.getCenesActivity().getCenesApplication().getCoreManager().userManager;
            User user = userManager.getUser();

            Long eventId = longs[0];
            user.setApiUrl(homeFragment.getCenesActivity().getCenesApplication().getCoreManager().urlManager.getApiUrl("dev"));
            String queryStr = "?event_id=" + eventId;
            JSONObject response = homeFragment.getCenesActivity().getCenesApplication().getCoreManager().apiManager.deleteEventById(user, queryStr, homeFragment.getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            deleteGathDialog.dismiss();
            
            deleteGathDialog = null;
            try {
                if (response.getBoolean("success")) {
                    Toast.makeText(homeFragment.getContext(), "Gathering Deleted", Toast.LENGTH_SHORT).show();
                    //((HomeFragment)context.getVisibleFragment()).initialSync();
                    Fragment currentFragment = homeFragment.getCenesActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    ((CenesBaseActivity)homeFragment.getCenesActivity()).fragmentManager
                            .beginTransaction()
                            .detach(currentFragment)
                            .attach(currentFragment)
                            .commit();

                } else {
                    Toast.makeText(homeFragment.getContext(), "Gathering Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
