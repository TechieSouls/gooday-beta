package com.cenes.fragment;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cenes.Manager.ApiManager;
import com.cenes.Manager.InternetManager;
import com.cenes.Manager.UrlManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Alarm;
import com.cenes.bo.User;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.AlarmManager;
import com.cenes.database.manager.UserManager;
import com.cenes.service.AlarmReceiver;
import com.cenes.util.CenesTextView;
import com.cenes.util.CenesUtils;
import com.cenes.util.RoundedImageView;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mandeep on 24/10/17.
 */

public class AlarmsFragment extends CenesFragment {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private ImageView addAlarmBtn, ivDeleteAlarms;
    private RoundedImageView userProfilePic;
    private TextView tvNoAlarms;
    private ListView lvAlarms;
    private AlarmsAdapter alarmsAdapter;

    CenesApplication cenesApplication;
    CoreManager coreManager;
    UserManager userManager;
    AlarmManager alarmManager;
    ApiManager apiManager;
    UrlManager urlManager;
    InternetManager internetManager;
    User loggedInUser;
    Typeface tfLatoRegular, tfLatoLight;
    List<Alarm> alarms;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_alarms, container, false);
        init(view);

        tfLatoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato_regular.ttf");
        tfLatoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato_light.ttf");

        if (loggedInUser != null && loggedInUser.getPicture() != null && loggedInUser.getPicture() != "null") {
            // DownloadImageTask(homePageProfilePic).execute(user.getPicture());
            Glide.with(this).load(loggedInUser.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(userProfilePic);
        }

        refreshUI(alarmManager.getAlarms(), false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AlarmActivity) getActivity()).showFooter();
    }

    public void refreshUI(List<Alarm> alarms, boolean notifyDataSetChanged) {
        if (alarms != null && alarms.size() > 0) {
            lvAlarms.setVisibility(View.VISIBLE);
            alarmsAdapter = new AlarmsAdapter(AlarmsFragment.this, alarms);
            /*if(notifyDataSetChanged) {
                alarmsAdapter.notifyDataSetChanged();
            } else {
                lvAlarms.setAdapter(alarmsAdapter);
            }*/
            lvAlarms.setAdapter(alarmsAdapter);
            ivDeleteAlarms.setVisibility(View.VISIBLE);
            //userProfilePic.setVisibility(View.GONE);
        } else {
            isDeleteEnabled = false;
            lvAlarms.setVisibility(View.GONE);
            tvNoAlarms.setVisibility(View.VISIBLE);
            ivDeleteAlarms.setVisibility(View.GONE);
            //userProfilePic.setVisibility(View.VISIBLE);
        }
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        userManager = coreManager.getUserManager();
        alarmManager = coreManager.getAlarmManager();
        apiManager = coreManager.getApiManager();
        urlManager = coreManager.getUrlManager();
        internetManager = coreManager.getInternetManager();
        loggedInUser = userManager.getUser();

        addAlarmBtn = (ImageView) view.findViewById(R.id.add_alarm_btn);
        ivDeleteAlarms = (ImageView) view.findViewById(R.id.delete_alarm_btn);
        userProfilePic = (RoundedImageView) view.findViewById(R.id.user_profile_pic);

        tvNoAlarms = (TextView) view.findViewById(R.id.tv_no_alarms);
        lvAlarms = (ListView) view.findViewById(R.id.lv_alarms);

        addAlarmBtn.setOnClickListener(onClickListener);
        ivDeleteAlarms.setOnClickListener(onClickListener);
        userProfilePic.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.user_profile_pic:
                    AlarmActivity.mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.add_alarm_btn:
                    fragmentManager = getActivity().getSupportFragmentManager();
                    ((AlarmActivity) getActivity()).replaceFragment(new AddAlarmFragment(), "addAlarmFragment");
                    break;
                case R.id.delete_alarm_btn:
                    isDeleteEnabled = !isDeleteEnabled;
                    refreshUI(alarmManager.getAlarms(), true);
                    break;
            }
        }
    };

    boolean isDeleteEnabled;

    class AlarmsAdapter extends BaseAdapter {

        AlarmsFragment context;
        LayoutInflater inflater;
        List<Alarm> alarms;

        public AlarmsAdapter(AlarmsFragment context, List<Alarm> alarms) {
            this.context = context;
            this.inflater = LayoutInflater.from(context.getActivity());
            this.alarms = alarms;
        }

        @Override
        public int getCount() {
            return alarms.size();
        }

        @Override
        public Object getItem(int position) {
            return alarms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final AlarmDetailViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.adapter_alarms_listview_items, null);
                holder = new AlarmDetailViewHolder();
                holder.alarmDetailLinerLayout = (LinearLayout) view.findViewById(R.id.ll_alarm_info);
                holder.alarmTime = (CenesTextView) view.findViewById(R.id.tv_time);
                holder.alarmOccurance = (CenesTextView) view.findViewById(R.id.tv_repeat);
                holder.alarmLabel = (CenesTextView) view.findViewById(R.id.tv_label);
                holder.onOffSwitch = (Switch) view.findViewById(R.id.switch_onoff);
                holder.ibDeleteAlarm = (ImageButton) view.findViewById(R.id.ibDeleteAlarm);
                holder.alarmId = 0;
                view.setTag(holder);
            } else {
                holder = (AlarmDetailViewHolder) view.getTag();
            }

            final Alarm alarm = alarms.get(position);
            holder.alarmId = alarm.getAlarmId();
            holder.alarmTime.setText(CenesUtils.HHmm.format(alarm.getTime()));
            //holder.alarmOccurance.setText(alarm.getRepeat());
            holder.alarmOccurance.setText(getWeekdaysLabel(alarm.getRepeat()));
            holder.alarmLabel.setText(alarm.getLabel());

            while (getWeekdaysLabel(alarm.getRepeat()).equals("Never") && alarm.getIsOn() == 1 && alarm.getTime() < System.currentTimeMillis()) {
                alarm.setIsOn(0);
                alarm.setTime(alarm.getTime() + (24 * 60 * 60 * 1000));
                alarmManager.updateAlarm(alarm);
            }

            holder.onOffSwitch.setChecked(alarm.getIsOn() == 1 ? true : false);

            if (isDeleteEnabled) {
                holder.ibDeleteAlarm.setVisibility(View.VISIBLE);
                //ivDeleteAlarms.setText("Done");
                ivDeleteAlarms.setImageResource(R.drawable.ic_done_white);
            } else {
                holder.ibDeleteAlarm.setVisibility(View.GONE);
                //ivDeleteAlarms.setText("Delete");
                ivDeleteAlarms.setImageResource(R.drawable.ic_delete_white);
            }

            toggleAlarm(holder, alarm, holder.onOffSwitch.isChecked(), false);

            holder.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        alarm.setIsOn(1);
                    } else {
                        alarm.setIsOn(0);
                    }
                    toggleAlarm(holder, alarm, isChecked, true);
                }
            });

            holder.alarmDetailLinerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentManager = getActivity().getSupportFragmentManager();
                    AddAlarmFragment fragment = new AddAlarmFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("alarmId", holder.alarmId);
                    fragment.setArguments(bundle);
                    ((AlarmActivity) getActivity()).replaceFragment(fragment, "addAlarmFragment");
                }
            });

            holder.ibDeleteAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete");
                    alert.setMessage("Are you sure you want to delete this Alarm?");
                    alert.setCancelable(false);
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addRemoveServiceAlarms(alarm, false);
                            alarmManager.deleteAlarm(alarm);
                            refreshUI(alarmManager.getAlarms(), true);
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                }
            });

            return view;
        }

        class AlarmDetailViewHolder {
            LinearLayout alarmDetailLinerLayout;
            CenesTextView alarmTime;
            CenesTextView alarmOccurance;
            CenesTextView alarmLabel;
            Switch onOffSwitch;
            ImageButton ibDeleteAlarm;
            int alarmId;
        }

        public void toggleAlarm(AlarmsAdapter.AlarmDetailViewHolder holder, Alarm alarm, boolean isChecked, boolean userChangedSwitchState) {
            if (isChecked) {
                holder.alarmTime.setTypeface(tfLatoRegular);
                holder.alarmOccurance.setTypeface(tfLatoRegular);
                holder.alarmLabel.setTypeface(tfLatoRegular);
            } else {
                holder.alarmTime.setTypeface(tfLatoLight);
                holder.alarmOccurance.setTypeface(tfLatoLight);
                holder.alarmLabel.setTypeface(tfLatoLight);
            }
            if (userChangedSwitchState) {
                addRemoveServiceAlarms(alarm, isChecked);
                alarmManager.updateAlarm(alarm);
                refreshUI(alarmManager.getAlarms(), true);
            }
        }

        public void addRemoveServiceAlarms(Alarm alarm, boolean isChecked) {
            int j = 0;
            while (j++ < 7) {
                cancelAlarm(Integer.valueOf("" + alarm.getAlarmId() + 77 + j));
            }

            if (isChecked) {
                LinkedHashMap<String, Boolean> hMapSelectedWeekdays = getSelectedWeekdaysMap(alarm.getRepeat());

                if (hMapSelectedWeekdays != null) {
                    Iterator iterator = hMapSelectedWeekdays.entrySet().iterator();
                    while (iterator.hasNext()) {
                        LinkedHashMap.Entry<String, Boolean> entry = (LinkedHashMap.Entry<String, Boolean>) iterator.next();

                        for (int i = 0; i < weekdayObjects.length; i++) {
                            if (entry.getKey().equals(weekdayObjects[i])) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(alarm.getTime());
                                calendar.set(Calendar.DAY_OF_WEEK, calendarWeekdayObjects[i]);
                                setAlarm((Calendar) calendar.clone(), alarm, true);
                            }
                        }
                    }
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(alarm.getTime());
                    setAlarm((Calendar) calendar.clone(), alarm, false);
                }
            }
        }

        private String[] weekdayObjects = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        private int[] calendarWeekdayObjects = new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};

        private void setAlarm(Calendar targetCal, Alarm alarm, boolean isRepeating) {
            if (targetCal.getTimeInMillis() < System.currentTimeMillis()) {
                if (isRepeating) {
                    targetCal.add(Calendar.DAY_OF_YEAR, 7);
                } else {
                    targetCal.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
            System.out.println("\n\n***\nAlarm is set@ " + targetCal.getTime() + "\n***\n");
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            intent.putExtra("alarmName", alarm.getLabel());
            intent.putExtra("alarmSound", alarm.getSound());
            System.out.println("alarm.getLabel(): " + alarm.getLabel() + "alarm.getSound(): " + alarm.getSound() + "DAY_OF_WEEK: " + targetCal.get(Calendar.DAY_OF_WEEK));
            String requestId = "" + alarm.getAlarmId() + 77 + targetCal.get(Calendar.DAY_OF_WEEK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), Integer.valueOf(requestId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            if (isRepeating) {
                alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), android.app.AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            } else {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
            }
        }

        private void cancelAlarm(int RSQ) {
            System.out.println("\n\n***\nAlarm is cancelled for alarmId " + RSQ + "\n***\n");
            Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), RSQ, intent, 0);
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        String weekdaysLabelString = "";

        public String getWeekdaysLabel(String weekdaysSelections) {
            if (weekdaysSelections.equalsIgnoreCase("") || weekdaysSelections.equalsIgnoreCase("Never")) {
                weekdaysLabelString = "Never";
            } else {
                weekdaysLabelString = "";
                String[] selectionsArray = weekdaysSelections.split(",");

                for (int i = 0; i < selectionsArray.length; i++) {
                    weekdaysLabelString = weekdaysLabelString.concat(selectionsArray[i].substring(0, 3) + " ");
                }
                if (weekdaysLabelString.contains(" ")) {
                    weekdaysLabelString = weekdaysLabelString.substring(0, weekdaysLabelString.lastIndexOf(" "));
                }
                if(selectionsArray.length == 7) {
                    weekdaysLabelString = "Every day";
                }
                if(selectionsArray.length == 1) {
                    weekdaysLabelString = "Every " + selectionsArray[0];
                }
            }
            return weekdaysLabelString;
        }

        public LinkedHashMap<String, Boolean> getSelectedWeekdaysMap(String weekdaysSelections) {
            LinkedHashMap<String, Boolean> hMapSelectedWeekdays = null;
            if (!weekdaysSelections.isEmpty() || !weekdaysSelections.equals("")) {
                String[] selectionsArray = weekdaysSelections.split(",");
                hMapSelectedWeekdays = new LinkedHashMap<>();

                for (int i = 0; i < selectionsArray.length; i++) {
                    hMapSelectedWeekdays.put(selectionsArray[i], true);
                }
            }
            return hMapSelectedWeekdays;
        }
    }
}
