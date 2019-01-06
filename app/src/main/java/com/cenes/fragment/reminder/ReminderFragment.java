package com.deploy.fragment.reminder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.UrlManager;
import com.deploy.R;
import com.deploy.activity.ReminderActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by rohan on 9/12/17.
 */

public class ReminderFragment extends CenesFragment {

    private int CREATE_REMINDER_REQUEST_CODE = 1001;

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UserManager userManager;
    private ApiManager apiManager;
    private UrlManager urlManager;
    User user;

    private RoundedImageView homeProfilePic;
    private ReminderAdapter reminderAdapter;

    ImageButton ibDeleteCompletedReminders;
    ImageView createReminderBtn;
    TextView tvNotificationCount;

    private ListView urgentListView, doneListView;

    private ReminderListTask reminderListTask;
    private NotificationCountTask notificationCountTask;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_reminder, container, false);
        init(view);

        user = userManager.getUser();
        if (user != null && user.getPicture() != null && user.getPicture() != "null") {
            Glide.with(getActivity()).load(user.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(homeProfilePic);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                reminderListTask = new ReminderListTask();
                reminderListTask.execute();
            }
        });
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

    @Override
    public void onResume() {
        super.onResume();
        ((ReminderActivity) getActivity()).showFooter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reminderListTask != null) {
            reminderListTask.cancel(true);
        }
        if (notificationCountTask != null) {
            notificationCountTask.cancel(true);
        }
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();

        createReminderBtn = (ImageView) view.findViewById(R.id.create_reminder_btn);
        urgentListView = (ListView) view.findViewById(R.id.reminder_urgent_list);
        doneListView = (ListView) view.findViewById(R.id.reminder_done_list);
        homeProfilePic = (RoundedImageView) view.findViewById(R.id.home_profile_pic);
        ibDeleteCompletedReminders = (ImageButton) view.findViewById(R.id.ibDeleteCompletedReminders);
        tvNotificationCount = (TextView) view.findViewById(R.id.tv_notification_count_pic);

        createReminderBtn.setOnClickListener(onClickListener);
        ibDeleteCompletedReminders.setOnClickListener(onClickListener);
        homeProfilePic.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_profile_pic:
                    ReminderActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.create_reminder_btn:
                    ((ReminderActivity) getActivity()).replaceFragment(new CreateReminderFragment(), "CreateReminderFragment");
                    break;
                case R.id.ibDeleteCompletedReminders:
                    isDeleteChecked = !isDeleteChecked;
                    refreshList("Completed");
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REMINDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            new ReminderListTask().execute();
        }
    }

    boolean isDeleteChecked;

    class ReminderAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context context;
        private JSONArray jaReminders;

        public ReminderAdapter(Context context, JSONArray jaReminders) {
            this.context = context;
            inflater = (LayoutInflater.from(context));
            this.jaReminders = jaReminders;
        }

        @Override
        public int getCount() {
            return jaReminders.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return jaReminders.get(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            final ViewHolder viewHolder;
            if (view == null) {
                view = this.inflater.inflate(R.layout.adapter_reminder_list_items, null);
                viewHolder = new ViewHolder();
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.tvTime = (TextView) view.findViewById(R.id.tv_time);
                viewHolder.tvLocation = (TextView) view.findViewById(R.id.tv_location);
                viewHolder.tvDelete = (TextView) view.findViewById(R.id.tv_delete);
                viewHolder.ibEdit = (ImageButton) view.findViewById(R.id.ib_edit);
                viewHolder.ibCompleted = (ImageButton) view.findViewById(R.id.ib_completed);
                viewHolder.llExpandedView = (LinearLayout) view.findViewById(R.id.ll_expanded_view);
                viewHolder.llInviteOptions = (LinearLayout) view.findViewById(R.id.llInviteOptions);
                viewHolder.tvAccept = (TextView) view.findViewById(R.id.tvAccept);
                viewHolder.tvDecline = (TextView) view.findViewById(R.id.tvDecline);
                viewHolder.tvInviteFrom = (TextView) view.findViewById(R.id.tv_invite_from);
                viewHolder.ivInviteFrom = (RoundedImageView) view.findViewById(R.id.iv_invite_from);

                viewHolder.tvFrom = (TextView) view.findViewById(R.id.tv_from);
                viewHolder.reminderId = null;
                viewHolder.reminderMemberId = null;
                viewHolder.allowEdit = false;
                viewHolder.showInviteFrom = false;
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            try {
                viewHolder.tvDelete.setVisibility(View.GONE);
                viewHolder.tvInviteFrom.setVisibility(View.GONE);
                viewHolder.ivInviteFrom.setVisibility(View.GONE);
                viewHolder.allowEdit = false;
                viewHolder.showInviteFrom = false;
                final JSONObject jsonReminder = (JSONObject) jaReminders.get(position);
                System.out.println("Reminders :" + jsonReminder.toString());
                viewHolder.tvTitle.setText(jsonReminder.getString("title"));

                if (!CenesUtils.isEmpty(jsonReminder.getString("reminderTime"))) {
                    Calendar reminderTime = Calendar.getInstance();
                    reminderTime.setTimeInMillis(jsonReminder.getLong("reminderTime"));
                    viewHolder.tvTime.setText(CenesUtils.MMMdd.format(reminderTime.getTime()) + CenesUtils.getDateSuffix(reminderTime.get(reminderTime.DAY_OF_MONTH)) + " at " + CenesUtils.hhmmaa.format(reminderTime.getTime()));
                    viewHolder.tvTime.setVisibility(View.VISIBLE);
                    if(reminderTime.getTimeInMillis() < System.currentTimeMillis()) {
                        viewHolder.tvTitle.setTextColor(Color.RED);
                    } else {
                        viewHolder.tvTitle.setTextColor(getResources().getColor(R.color.cenes_dark_gray));
                    }
                } else {
                    viewHolder.tvTime.setVisibility(View.GONE);
                }

                if (!CenesUtils.isEmpty(jsonReminder.getString("location"))) {
                    viewHolder.tvLocation.setVisibility(View.VISIBLE);
                    viewHolder.tvLocation.setText(jsonReminder.getString("location"));
                } else {
                    viewHolder.tvLocation.setVisibility(View.GONE);
                }

                if (!CenesUtils.isEmpty(jsonReminder.getString("from")) && !CenesUtils.isEmpty(jsonReminder.getJSONObject("from").getString("name"))) {
                    viewHolder.tvFrom.setVisibility(View.VISIBLE);
                    viewHolder.tvFrom.setText(jsonReminder.getJSONObject("from").getString("name"));
                } else {
                    viewHolder.tvFrom.setVisibility(View.GONE);
                }
                viewHolder.reminderId = jsonReminder.getLong("reminderId");

                if (!jsonReminder.getString("status").equalsIgnoreCase("Finish")) {

                    if (jsonReminder.has("reminderMembers") && !jsonReminder.isNull("reminderMembers") && jsonReminder.getJSONArray("reminderMembers").length() > 0) {
                        for (int i = 0; i < jsonReminder.getJSONArray("reminderMembers").length(); i++) {
                            JSONObject jo = jsonReminder.getJSONArray("reminderMembers").getJSONObject(i);
                            if (jo.getInt("memberId") == user.getUserId() && !jo.isNull("status")) {
                                viewHolder.llInviteOptions.setVisibility(View.GONE);
                                if (jsonReminder.getInt("createdById") != user.getUserId()) {
                                    viewHolder.allowEdit = false;
                                    //viewHolder.ibEdit.setVisibility(View.GONE);
                                    //viewHolder.ibCompleted.setOnClickListener(null);
                                } else {
                                    viewHolder.allowEdit = true;
                                    //viewHolder.ibEdit.setVisibility(View.VISIBLE);
                                    viewHolder.ibEdit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            /*Intent i = new Intent(getActivity(), CreateReminderActivity.class);
                                            i.putExtra("reminderObject", jsonReminder.toString());
                                            startActivityForResult(i, CREATE_REMINDER_REQUEST_CODE);*/
                                            CreateReminderFragment createReminderFragment = new CreateReminderFragment();
                                            Bundle reminderBundle = new Bundle();
                                            reminderBundle.putString("reminderObject", jsonReminder.toString());
                                            createReminderFragment.setArguments(reminderBundle);
                                            ((ReminderActivity) getActivity()).replaceFragment(createReminderFragment, "CreateReminderFragment");
                                        }
                                    });
                                }
                                viewHolder.ibCompleted.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                                        alert.setTitle("Completed");
                                        alert.setMessage("Are you sure you want to move this Reminder to Completed?");
                                        alert.setPositiveButton("Move", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new ReminderUpdateToFinishTask().execute(viewHolder.reminderId);
                                                new ReminderListTask().execute();
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
                                    }
                                });
                            } else if (jo.getInt("memberId") == user.getUserId() && CenesUtils.isEmpty(jo.getString("status"))) {
                                viewHolder.tvTitle.setTextColor(getResources().getColor(R.color.cenes_dark_gray));
                                viewHolder.reminderMemberId = jo.getLong("reminderMemberId");
                                //viewHolder.tvInviteFrom.setVisibility(View.VISIBLE);
                                //viewHolder.tvInviteFrom.setText("From " + viewHolder.tvFrom.getText().toString());
                                viewHolder.ivInviteFrom.setVisibility(View.VISIBLE);
                                String photo = jsonReminder.getJSONObject("from").getString("photo");
                                Glide.with(getActivity()).load(photo).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(viewHolder.ivInviteFrom);

                                viewHolder.llInviteOptions.setVisibility(View.VISIBLE);
                                //viewHolder.ibEdit.setVisibility(View.GONE);
                                viewHolder.ibCompleted.setOnClickListener(null);
                                viewHolder.allowEdit = false;
                                viewHolder.showInviteFrom = true;

                                viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            JSONObject job = new JSONObject();
                                            job.put("reminderMemberId", viewHolder.reminderMemberId);
                                            job.put("status", "Accept");
                                            new ReminderAcceptDeclineTask().execute(job);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                viewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            JSONObject job = new JSONObject();
                                            job.put("reminderMemberId", viewHolder.reminderMemberId);
                                            job.put("status", "Declined");
                                            new ReminderAcceptDeclineTask().execute(job);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    viewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (viewHolder.llExpandedView.getVisibility() == View.GONE) {
                                viewHolder.llExpandedView.setVisibility(View.VISIBLE);
                                if (viewHolder.allowEdit) {
                                    viewHolder.ibEdit.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.ibEdit.setVisibility(View.GONE);
                                }
                                viewHolder.tvInviteFrom.setVisibility(View.GONE);
                            } else {
                                viewHolder.ibEdit.setVisibility(View.GONE);
                                viewHolder.llExpandedView.setVisibility(View.GONE);
                                if (viewHolder.showInviteFrom) {
                                    viewHolder.tvInviteFrom.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.tvInviteFrom.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                } else {
                    viewHolder.tvTitle.setPaintFlags(viewHolder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.tvTitle.setTextColor(getResources().getColor(R.color.cenes_light_gray));
                    viewHolder.ibCompleted.setImageDrawable(getResources().getDrawable(R.drawable.reminder_checked));
                    viewHolder.ibEdit.setVisibility(View.GONE);

                    //TODO delete functionality

                    if (!CenesUtils.isEmpty(jsonReminder.getString("createdById")) && jsonReminder.getInt("createdById") == user.getUserId()) {
                        ibDeleteCompletedReminders.setVisibility(View.VISIBLE);
                        if (isDeleteChecked) {
                            viewHolder.tvDelete.setVisibility(View.VISIBLE);
                            ibDeleteCompletedReminders.setImageResource(R.drawable.ic_done);
                        } else {
                            viewHolder.tvDelete.setVisibility(View.GONE);
                            ibDeleteCompletedReminders.setImageResource(R.drawable.ic_delete);
                        }
                    } else {
                        viewHolder.tvDelete.setVisibility(View.GONE);
                    }

                    viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                            alert.setTitle("Delete");
                            alert.setMessage("Are you sure you want to delete this Reminder?");
                            alert.setCancelable(false);
                            alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DeleteReminderTask().execute(viewHolder.reminderId);
                                    jaReminderCompleted.remove(position);
                                    refreshList("Completed");
                                }
                            });
                            alert.setNegativeButton("Cancel", null);
                            alert.show();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        class ViewHolder {
            TextView tvTitle;
            TextView tvTime;
            TextView tvLocation;
            TextView tvDelete;
            Long reminderId;
            ImageButton ibEdit;
            ImageButton ibCompleted;
            LinearLayout llExpandedView;
            LinearLayout llInviteOptions;
            TextView tvAccept;
            TextView tvDecline;
            TextView tvInviteFrom;
            RoundedImageView ivInviteFrom;
            TextView tvFrom;
            Long reminderMemberId;
            boolean allowEdit;
            boolean showInviteFrom;
        }
    }

    public void refreshList(String type) {
        if (type.equalsIgnoreCase("OnGoing")) {
            if (jaReminderOnGoing != null && jaReminderOnGoing.length() > 0) {
                reminderAdapter = new ReminderAdapter((ReminderActivity) getActivity(), jaReminderOnGoing);
                urgentListView.setAdapter(reminderAdapter);
                urgentListView.setVisibility(View.VISIBLE);
            } else {
                urgentListView.setVisibility(View.GONE);
            }
        } else if (type.equalsIgnoreCase("Completed")) {
            ibDeleteCompletedReminders.setVisibility(View.GONE);
            if (jaReminderCompleted != null && jaReminderCompleted.length() > 0) {
                reminderAdapter = new ReminderAdapter((ReminderActivity) getActivity(), jaReminderCompleted);
                doneListView.setAdapter(reminderAdapter);
                doneListView.setVisibility(View.VISIBLE);
                //ibDeleteCompletedReminders.setVisibility(View.VISIBLE);

                try {
                    //Looping all reminders to check if completed reminders contains owners reminder
                    boolean ownerExists = false;
                    for (int j = 0; j < jaReminderCompleted.length(); j++) {
                        if (((JSONObject) jaReminderCompleted.get(j)).getInt("createdById") == user.getUserId()) {
                            ownerExists = true;
                        }
                    }
                    if (!ownerExists) {
                        isDeleteChecked = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                isDeleteChecked = false;
                doneListView.setVisibility(View.GONE);
                //ibDeleteCompletedReminders.setVisibility(View.GONE);
            }
        }
    }

    JSONArray jaReminderCompleted, jaReminderOnGoing;

    class ReminderListTask extends AsyncTask<String, Boolean, Boolean> {
//        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Loading...");
//            progressDialog.setIndeterminate(false);
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... responses) {

            Boolean dataExists = false;
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?user_id=" + user.getUserId();
            try {
                JSONObject response = apiManager.getReminders(user, queryStr, (ReminderActivity) getActivity());
                if (response.getBoolean("success") && response.getJSONArray("data").length() > 0) {
                    dataExists = true;
                    JSONArray respArray = response.getJSONArray("data");
                    jaReminderCompleted = new JSONArray();
                    jaReminderOnGoing = new JSONArray();
                    for (int i = 0; i < respArray.length(); i++) {
                        JSONObject reminderObj = respArray.getJSONObject(i);
                        if (reminderObj.has("status") && reminderObj.get("status") != null) {
                            if ("Start".equals(reminderObj.getString("status"))) {
                                jaReminderOnGoing.put(reminderObj);
                            } else {
                                jaReminderCompleted.put(reminderObj);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dataExists;
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            super.onPostExecute(dataExists);
//            progressDialog.hide();
//            progressDialog.dismiss();
//            progressDialog = null;
            if (getActivity() == null) {
                return;
            }
            if (!getActivity().isFinishing()) {
                if (!dataExists) {
                    Toast.makeText(getContext(), "Error in Getting Reminders", Toast.LENGTH_LONG);
                } else {
                    refreshList("OnGoing");
                    refreshList("Completed");
                }
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            reminderListTask.cancel(true);
        }
    }

    class DeleteReminderTask extends AsyncTask<Long, Boolean, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Deleting...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Long... longs) {
            Long reminderId = longs[0];
            Boolean dataExists = false;
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?reminderId=" + reminderId;
            try {
                JSONObject response = apiManager.deleteReminderById(user, queryStr, (ReminderActivity) getActivity());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return dataExists;
        }

        @Override
        protected void onPostExecute(Boolean dataExists) {
            super.onPostExecute(dataExists);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    class ReminderUpdateToFinishTask extends AsyncTask<Long, String, String> {
        @Override
        protected String doInBackground(Long... reminderIds) {
            Long reminderId = reminderIds[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            String queryStr = "?reminder_id=" + reminderId + "&user_id=" + user.getUserId();
            apiManager.updateReminderToFinish(user, queryStr, (ReminderActivity) getActivity());
            return null;
        }
    }

    class ReminderAcceptDeclineTask extends AsyncTask<JSONObject, String, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Updating..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(JSONObject... josns) {
            JSONObject requestObj = josns[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            try {
                String queryStr = "?reminderMemberId=" + requestObj.getInt("reminderMemberId") + "&status=" + requestObj.getString("status");
                apiManager.updateReminderInvitation(user, queryStr, (ReminderActivity) getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resp) {
            super.onPostExecute(resp);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;

            new ReminderListTask().execute();
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
}
