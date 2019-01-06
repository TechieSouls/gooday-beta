package com.deploy.fragment.reminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.UrlManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.R;
import com.deploy.activity.ReminderActivity;
import com.deploy.activity.SearchFriendActivity;
import com.deploy.activity.SearchLocationActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.Reminder;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by rohan on 9/12/17.
 */

public class CreateReminderFragment extends CenesFragment {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private UrlManager urlManager;
    private ApiManager apiManager;
    private UserManager userManager;
    private ValidationManager validationManager;
    private AlertManager alertManager;

    private EditText reminderTitle, reminderLocationSearchKeyword, reminderFriendSearchKeyword;
    private TextView reminderDateTime, saveReminderBtn, reminderLocation, addReminderPeople;
    private ListView reminderLocationResults, reminderFriendListView;
    private TextView createReminderBackBtn, tvAddReminderHeaderTitle, editReminderBtn, btnDelete;
    private ReminderLocationAdapter reminderLocationAdapter;
    private LinearLayout reminderSearchLocationBlock;
    private Calendar reminderDateTimeCalendar;
    private JSONArray selectedFriendsArray;

    //JAVA Variables
    private User loggedInUser;
    private Reminder reminder;
    private JSONArray searchedFriends, allfriends;
    private int SEACRH_LOCATION_RESULT_CODE = 1001, SEARCH_FRIEND_RESULT_CODE = 1002;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_create_reminder, container, false);
        init();

        loggedInUser = userManager.getUser();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString("reminderObject") != null) {
            tvAddReminderHeaderTitle.setText("Edit Reminder");
            /*    try {
                    JSONObject reminderObj = new JSONObject(getIntent().getStringExtra("reminderObject"));
                    if (loggedInUser.getUserId() == reminderObj.getInt("createdById")) {
                        editReminderBtn.setVisibility(View.VISIBLE);
                    } else {
                        editReminderBtn.setVisibility(View.GONE);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }

            btnDelete.setVisibility(View.GONE);
            saveReminderBtn.setVisibility(View.GONE);
            disableComponents();*/
            btnDelete.setVisibility(View.VISIBLE);
            populateReminderData(bundle.getString("reminderObject"));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ReminderActivity) getActivity()).hideFooter();
    }

    public void init() {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        userManager = coreManager.getUserManager();
        validationManager = coreManager.getValidatioManager();
        alertManager = coreManager.getAlertManager();
        reminder = new Reminder();
        selectedFriendsArray = new JSONArray();
        reminderDateTimeCalendar = null;

        reminderTitle = (EditText) view.findViewById(R.id.reminder_title);
        reminderDateTime = (TextView) view.findViewById(R.id.reminder_datetime);
        saveReminderBtn = (TextView) view.findViewById(R.id.save_reminder_btn);
        reminderLocationResults = (ListView) view.findViewById(R.id.reminder_locations_list_view);
        reminderLocationSearchKeyword = (EditText) view.findViewById(R.id.reminder_location_search_keyword);
        reminderLocation = (TextView) view.findViewById(R.id.reminder_location);
        reminderSearchLocationBlock = (LinearLayout) view.findViewById(R.id.reminder_search_location_block);
        createReminderBackBtn = (TextView) view.findViewById(R.id.create_reminder_back_btn);
        addReminderPeople = (TextView) view.findViewById(R.id.add_reminder_people);
        ivAddMoreFriends = (ImageView) view.findViewById(R.id.iv_add_more_friends);
        btnDelete = (TextView) view.findViewById(R.id.btn_delete);
        editReminderBtn = (TextView) view.findViewById(R.id.edit_reminder_btn);
        tvAddReminderHeaderTitle = (TextView) view.findViewById(R.id.tv_add_reminder_header_title);

        //ClickListeners
        reminderDateTime.setOnClickListener(onClickListener);
        saveReminderBtn.setOnClickListener(onClickListener);
        reminderLocation.setOnClickListener(onClickListener);
        createReminderBackBtn.setOnClickListener(onClickListener);
        addReminderPeople.setOnClickListener(onClickListener);
        ivAddMoreFriends.setOnClickListener(onClickListener);
        btnDelete.setOnClickListener(onClickListener);
        editReminderBtn.setOnClickListener(onClickListener);

        //Text Change Listeners
        inviteFriendsImageList = new HashSet<>();

        reminderTitle.setOnTouchListener(clearTextTouchListener);
    }

    public void populateReminderData(String reminderObjStr) {
        try {
            JSONObject reminderObj = new JSONObject(reminderObjStr);
            reminder.setReminderId(reminderObj.getLong("reminderId"));
            reminder.setTitle((String) reminderObj.get("title"));
            reminderTitle.setText((String) reminderObj.get("title"));

            if (!CenesUtils.isEmpty(reminderObj.getString("reminderTime"))) {
                if (reminderDateTimeCalendar == null) {
                    reminderDateTimeCalendar = Calendar.getInstance();
                }
                reminderDateTimeCalendar.setTimeInMillis((Long) reminderObj.get("reminderTime"));
                reminder.setReminderTime(reminderDateTimeCalendar);

                reminderDateTime.setText(CenesUtils.MMMMddy.format(reminderDateTimeCalendar.getTime()) + CenesUtils.getDateSuffix(reminderDateTimeCalendar.get(Calendar.DAY_OF_MONTH)) + " at " + CenesUtils.hhmmaa.format(reminderDateTimeCalendar.getTime()));
                if (reminderObj.has("location") && reminderObj.get("location") != null && reminderObj.getString("location") != "null") {
                    reminderLocation.setText(reminderObj.getString("location"));
                    reminder.setLocation(reminderObj.getString("location"));
                }
            }


            selectedFriendsArray = new JSONArray();
            inviteFriendsImageList = new HashSet<>();
            if (reminderObj.has("reminderMembers") && reminderObj.getJSONArray("reminderMembers").length() > 0) {
                JSONArray friendsArray = reminderObj.getJSONArray("reminderMembers");
                for (int i = 0; i < friendsArray.length(); i++) {
                    JSONObject frindObj = new JSONObject();
                    frindObj.put("name", friendsArray.getJSONObject(i).get("name"));
                    frindObj.put("picture", friendsArray.getJSONObject(i).get("picture"));
                    frindObj.put("userId", friendsArray.getJSONObject(i).get("memberId"));
                    if (friendsArray.getJSONObject(i).getString("status") != "null") {
                        frindObj.put("status", friendsArray.getJSONObject(i).get("status"));
                    }
                    if (loggedInUser.getUserId() == friendsArray.getJSONObject(i).getInt("memberId")) {
                        continue;
                    }
                    selectedFriendsArray.put(frindObj);

                    Map<String, String> invFrnMap = new HashMap<>();
                    invFrnMap.put("name", friendsArray.getJSONObject(i).getString("name"));
                    invFrnMap.put("photo", friendsArray.getJSONObject(i).getString("picture"));
                    invFrnMap.put("userId", String.valueOf(friendsArray.getJSONObject(i).getLong("memberId")));
                    if (friendsArray.getJSONObject(i).getString("status") != "null") {
                        invFrnMap.put("status", friendsArray.getJSONObject(i).getString("status"));
                    }
                    inviteFriendsImageList.add(invFrnMap);
                }
            }
            showSelectedFriend(inviteFriendsImageList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View.OnTouchListener clearTextTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (reminderTitle.getRight() - reminderTitle.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) - 50) {
                    reminderTitle.setText("");
                    return true;
                }
            }
            return false;
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.create_reminder_back_btn:
                    getActivity().onBackPressed();
                    break;

                case R.id.reminder_datetime:
                    final TimePickerDialog.OnTimeSetListener reminderTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                            System.out.println("hour: " + hour);

                            Calendar temp = Calendar.getInstance();
                            temp.set(Calendar.HOUR_OF_DAY, hour);
                            temp.set(Calendar.MINUTE, minute);
                            temp.set(Calendar.YEAR, reminderDateTimeCalendar.get(Calendar.YEAR));
                            temp.set(Calendar.MONTH, reminderDateTimeCalendar.get(Calendar.MONTH));
                            temp.set(Calendar.DAY_OF_MONTH, reminderDateTimeCalendar.get(Calendar.DAY_OF_MONTH));

                            if (temp.before(GregorianCalendar.getInstance())) {
                                Toast.makeText(getActivity(), "Please select a time in future for Reminders.", Toast.LENGTH_SHORT).show();
                            } else {
                                reminderDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                reminderDateTimeCalendar.set(Calendar.MINUTE, minute);
                                System.out.println(reminderDateTimeCalendar.getTime());
                                reminderDateTime.setText(CenesUtils.EEEEMMMddhhmma.format(reminderDateTimeCalendar.getTimeInMillis()));
                            }
                        }
                    };

                    final DatePickerDialog.OnDateSetListener reminderDatePickerListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            reminderDateTimeCalendar.set(Calendar.YEAR, year);
                            reminderDateTimeCalendar.set(Calendar.MONTH, month);
                            reminderDateTimeCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                            System.out.println("DatePicker : " + reminderDateTimeCalendar.getTime());
                            int hour = reminderDateTimeCalendar.get(Calendar.HOUR);
                            int minute = reminderDateTimeCalendar.get(Calendar.MINUTE);

                            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), reminderTimePickerListener, hour, minute, false);
                            timePickerDialog.show();
                        }
                    };

                    if (reminderDateTimeCalendar == null) {
                        reminderDateTimeCalendar = Calendar.getInstance();
                    }
                    int year = reminderDateTimeCalendar.get(Calendar.YEAR);
                    int month = reminderDateTimeCalendar.get(Calendar.MONTH);
                    int day = reminderDateTimeCalendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog reminderDatePicker = new DatePickerDialog(getActivity(), reminderDatePickerListener, year, month, day);
                    reminderDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
                    reminderDatePicker.show();
                    break;

                case R.id.add_reminder_people:
                case R.id.iv_add_more_friends:
                    startActivityForResult(new Intent(getActivity(), SearchFriendActivity.class), SEARCH_FRIEND_RESULT_CODE);
                    break;

                case R.id.reminder_location:
                    startActivityForResult(new Intent(getActivity(), SearchLocationActivity.class), SEACRH_LOCATION_RESULT_CODE);
                    break;
                case R.id.save_reminder_btn:
                    if (isValid()) {
                        try {
                            User user = userManager.getUser();
                            JSONObject jsonObject = new JSONObject();
                            if (reminder.getReminderId() != null) {
                                jsonObject.put("reminderId", reminder.getReminderId());
                            }
                            jsonObject.put("title", reminderTitle.getText().toString());
                            jsonObject.put("category", reminder.getCategory());
                            if (reminderDateTimeCalendar != null) {
                                jsonObject.put("reminderTime", reminderDateTimeCalendar.getTimeInMillis());
                            }
                            if (reminder.getLocation() != null) {
                                jsonObject.put("location", reminder.getLocation());
                            }
                            jsonObject.put("createdById", user.getUserId());


                            if (inviteFriendsImageList.size() > 0) {
                                JSONArray membersArray = new JSONArray();
                                for (Map<String, String> friend : inviteFriendsImageList) {
                                    if (friend.get("userId") == String.valueOf(user.getUserId())) {
                                        continue;
                                    }
                                    JSONObject frindObj = new JSONObject();
                                    frindObj.put("name", friend.get("name"));
                                    frindObj.put("picture", friend.get("photo"));
                                    frindObj.put("memberId", friend.get("userId"));
                                    frindObj.put("status", friend.get("status"));
                                    membersArray.put(frindObj);
                                }
                                jsonObject.put("reminderMembers", membersArray);
                            }

                            new CreateReminderTask().execute(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_delete:
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete Reminder");
                    alert.setMessage("Do you really want to delete?");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String queryStr = "?reminderId=" + reminder.getReminderId();
                                new DeleteReminderTask().execute(queryStr);
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
                case R.id.edit_reminder_btn:
//                    editReminderBtn.setVisibility(View.GONE);
//                    btnDelete.setVisibility(View.VISIBLE);
//                    saveReminderBtn.setVisibility(View.VISIBLE);
//                    enableComponents();
                    break;
            }
        }
    };

    TextWatcher textChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            new SearchLocationTask().execute(editable.toString());
        }
    };


    public Boolean isValid() {
        Boolean isValid = true;
        if (validationManager.isFieldBlank(reminderTitle.getText().toString())) {
            //alertManager.getAlert(getApplicationContext(), "Please Enter Reminder Title", "Info", null, false, "OK");
            Toast.makeText(getActivity(), "Please Enter Reminder Title.", Toast.LENGTH_LONG).show();
            isValid = false;
        }/* else if (reminder.getCategory() == null || reminder.getCategory().length() == 0) {
            Toast.makeText(CreateReminderActivity.this, "Please Select at least one category", Toast.LENGTH_LONG).show();
            isValid = false;
        }*/ else if (reminderDateTimeCalendar != null && reminderDateTimeCalendar.before(GregorianCalendar.getInstance())) {
            Toast.makeText(getActivity(), "Please select a time in future for Reminders.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_OK) {

            String requiredValue = data.getStringExtra("title");
            if (requiredValue != null && requiredValue != "null") {
                reminderLocation.setText(requiredValue.toString());
                reminder.setLocation(requiredValue.toString());
            }
        } else if (requestCode == SEACRH_LOCATION_RESULT_CODE && resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Location canceled");
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
            }

            if (inviteFriendsImageList.size() > 0) {
                recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                llFriendsContainer = (HorizontalScrollView) view.findViewById(R.id.ll_friends_container);
                ivAddMoreFriends = (ImageView) view.findViewById(R.id.iv_add_more_friends);
                llFriendsContainer.setVisibility(View.VISIBLE);

                ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(inviteFriendsImageList);
                mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mFriendsAdapter);
            }
        }
    }

    private RecyclerView recyclerView;
    private HorizontalScrollView llFriendsContainer;
    private ImageView ivAddMoreFriends;
    private FriendsAdapter mFriendsAdapter;
    private Set<Map<String, String>> inviteFriendsImageList;

    public void showSelectedFriend(Set<Map<String, String>> selectedFriendsArray) {
        try {
            if (selectedFriendsArray.size() == 0) {
                return;
            }
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            llFriendsContainer = (HorizontalScrollView) view.findViewById(R.id.ll_friends_container);
            ivAddMoreFriends = (ImageView) view.findViewById(R.id.iv_add_more_friends);
            llFriendsContainer.setVisibility(View.VISIBLE);

            ArrayList<Map<String, String>> jsonObjectArrayList = new ArrayList<>(selectedFriendsArray);
            mFriendsAdapter = new FriendsAdapter(jsonObjectArrayList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mFriendsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
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
                        mFriendsAdapter.notifyItemRemoved(position);
                        mFriendsAdapter.notifyItemRangeChanged(position, jsonObjectArrayList.size());
                        if (jsonObjectArrayList.size() == 0) {
                            llFriendsContainer.setVisibility(View.GONE);
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

    class ReminderLocationAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private JSONArray locationArray;

        ReminderLocationAdapter(Context context, JSONArray locationArray) {
            this.context = context;
            this.locationArray = locationArray;
            this.inflater = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return locationArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return locationArray.get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        TextView locTitle, locAddress;
        LinearLayout linerLayout;

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = inflater.inflate(R.layout.adapter_search_location, null);
            }
            linerLayout = (LinearLayout) view.findViewById(R.id.ll_loc_title_add);
            try {
                JSONObject locObj = this.locationArray.getJSONObject(position);

                locTitle = (TextView) view.findViewById(R.id.loc_title);
                locAddress = (TextView) view.findViewById(R.id.loc_add);

                locTitle.setText(locObj.getString("name"));
                locAddress.setText(locObj.getString("formatted_address"));

                linerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            JSONObject locObj = locationArray.getJSONObject(position);
                            reminderLocation.setText(locObj.getString("name"));
                            reminderSearchLocationBlock.setVisibility(View.GONE);
                            reminderLocation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.reminder_loc), null, getResources().getDrawable(R.drawable.down_arrow), null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    class CreateReminderTask extends AsyncTask<JSONObject, JSONObject, JSONObject> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Creating...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjs) {
            JSONObject jsonObj = jsonObjs[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject response = apiManager.saveReminder(user, jsonObj, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
            try {
                if (response.getBoolean("success")) {
                    JSONObject reminderResponse = response.getJSONObject("data");
                    reminder.setReminderId(reminderResponse.getLong("reminderId"));
//                    editReminderBtn.setVisibility(View.VISIBLE);
//                    saveReminderBtn.setVisibility(View.GONE);
//                    btnDelete.setVisibility(View.GONE);
//                    disableComponents();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), response.getString("errorDetail"), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DeleteReminderTask extends AsyncTask<String, String, JSONObject> {
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
        protected JSONObject doInBackground(String... strings) {
            String queryStr = strings[0];
            User user = userManager.getUser();
            user.setApiUrl(urlManager.getApiUrl("dev"));
            JSONObject response = apiManager.deleteReminderById(user, queryStr, getCenesActivity());
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            progressDialog.hide();
            progressDialog.dismiss();
            progressDialog = null;
            try {
                if (response.getBoolean("success")) {
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), response.getString("errorDetail"), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SearchLocationTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String keyword = strings[0];
            try {
                String queryStr = "&query=" + URLEncoder.encode(keyword);
                JSONObject job = apiManager.locationSearch(queryStr);
                JSONArray locations = job.getJSONArray("results");
                reminderLocationAdapter = new ReminderLocationAdapter(getActivity(), locations);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                getCenesActivity().showRequestTimeoutDialog();
            } else {
                reminderLocationResults.setAdapter(reminderLocationAdapter);
            }
        }
    }

    public void disableComponents() {
        reminderTitle.setEnabled(false);
        reminderDateTime.setClickable(false);
        reminderLocation.setClickable(false);
        addReminderPeople.setClickable(false);
    }

    public void enableComponents() {
        reminderTitle.setEnabled(true);
        reminderDateTime.setClickable(true);
        reminderLocation.setClickable(true);
        addReminderPeople.setClickable(true);
    }
}
