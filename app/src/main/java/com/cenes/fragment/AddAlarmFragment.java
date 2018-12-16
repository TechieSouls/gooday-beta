package com.cenes.fragment;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cenes.Manager.AlertManager;
import com.cenes.R;
import com.cenes.activity.AlarmActivity;
import com.cenes.application.CenesApplication;
import com.cenes.bo.Alarm;
import com.cenes.coremanager.CoreManager;
import com.cenes.database.manager.AlarmManager;
import com.cenes.service.AlarmReceiver;
import com.cenes.util.CenesTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 24/10/17.
 */

public class AddAlarmFragment extends CenesFragment {

    private CenesApplication cenesApplication;
    private CoreManager coreManager;
    private AlarmManager alarmManager;
    private AlertManager alertManager;

    public enum AlarmOptionsType {Repeat, Label, Sound}

    private TextView tvAlarmTimePicker, saveBtn;
    private TextView addAlarmCancelBtn, alarmOptionsBackButton;
    private TextView tvLabel, tvRepeat, tvSound, tvDelete;
    private TextView addAlarmScreenTitle, addAlarmScreenpOptionsTitle;
    private LinearLayout repeatAlarmLL, labelAlarmLL, llSoundOption;
    private EditText etLabel;

    private AlarmOptionsAdapter alarmOptionsAdapter;
    private ListView repeatAlarmList;

    private View fragmentView;

    private ImageView footerHomeIcon, footerReminderIcon, footerGatheringIcon, footerDiaryIcon;

    private String alarmOptionType;
    private LinkedHashMap<String, Boolean> repeatAlarmMap;
    private String alarmSound = "";

    private Alarm alarm;
    public static boolean saveDataOnBackPressed;

    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH : mm");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.activity_alarm_add, container, false);
        fragmentView = v;
        init(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AlarmActivity) getActivity()).hideFooter();
    }

    public void clearHashMaps() {
        hMap = null;
        hMapSelected = null;
        hMapSelectedWeekdays = null;
        hMapWeekdays = null;
    }

    public String getWeekdaysLabel(String weekdaysSelections) {
        if (weekdaysSelections.equalsIgnoreCase("") || weekdaysSelections.equalsIgnoreCase("Never")) {
            weekdaysLabelString = "Never";
        } else {
            weekdaysLabelString = "";
            hMapSelectedWeekdays = new LinkedHashMap<>();
            String[] selectionsArray = weekdaysSelections.split(",");
            for (int i = 0; i < selectionsArray.length; i++) {
                hMapSelectedWeekdays.put(selectionsArray[i], true);
                weekdaysLabelString = weekdaysLabelString.concat(selectionsArray[i].substring(0, 3) + " ");
            }
            if (weekdaysLabelString.contains(" ")) {
                weekdaysLabelString = weekdaysLabelString.substring(0, weekdaysLabelString.lastIndexOf(" "));
            }
            if (selectionsArray.length == 7) {
                weekdaysLabelString = "Every day";
            }
            if (selectionsArray.length == 1) {
                weekdaysLabelString = "Every " + selectionsArray[0];
            }
        }
        return weekdaysLabelString;
    }

    public void init(View view) {
        cenesApplication = getCenesActivity().getCenesApplication();
        coreManager = cenesApplication.getCoreManager();
        alarmManager = coreManager.getAlarmManager();
        alertManager = coreManager.getAlertManager();

        alarm = new Alarm();
        repeatAlarmMap = new LinkedHashMap<>();

        tvAlarmTimePicker = (TextView) view.findViewById(R.id.alarm_time_picker_btn);
        addAlarmCancelBtn = (TextView) view.findViewById(R.id.add_alarm_screen_cancel_btn);
        alarmOptionsBackButton = (TextView) view.findViewById(R.id.alarm_options_back_btn);
        saveBtn = (TextView) view.findViewById(R.id.add_alarm_screen_save_btn);
        addAlarmScreenTitle = (TextView) view.findViewById(R.id.add_alarm_screen_title);
        addAlarmScreenpOptionsTitle = (TextView) view.findViewById(R.id.alarm_options_screen_title);

        tvLabel = (TextView) view.findViewById(R.id.tv_label);
        tvRepeat = (TextView) view.findViewById(R.id.tv_repeat);
        tvSound = (TextView) view.findViewById(R.id.tv_sound);
        tvDelete = (TextView) view.findViewById(R.id.tv_delete);

        etLabel = (EditText) view.findViewById(R.id.et_label);

        tvLabel.setText(etLabel.getText().toString());
        tvRepeat.setText("Never");

        repeatAlarmLL = (LinearLayout) view.findViewById(R.id.repeat_alarm_ll);
        labelAlarmLL = (LinearLayout) view.findViewById(R.id.label_alarm_ll);
        llSoundOption = (LinearLayout) view.findViewById(R.id.ll_sound_option);

        repeatAlarmList = (ListView) view.findViewById(R.id.repeat_alarm_list);

        footerHomeIcon = (ImageView) view.findViewById(R.id.footer_home_icon);
        footerReminderIcon = (ImageView) view.findViewById(R.id.footer_reminder_icon);
        footerGatheringIcon = (ImageView) view.findViewById(R.id.footer_gathering_icon);
        //footerDiaryIcon = (ImageView) findViewById(R.id.footer_dairy_icon);

        tvAlarmTimePicker.setOnClickListener(onClickListener);
        addAlarmCancelBtn.setOnClickListener(onClickListener);
        alarmOptionsBackButton.setOnClickListener(onClickListener);
        repeatAlarmLL.setOnClickListener(onClickListener);
        labelAlarmLL.setOnClickListener(onClickListener);
        llSoundOption.setOnClickListener(onClickListener);
        saveBtn.setOnClickListener(onClickListener);
        tvDelete.setOnClickListener(onClickListener);
        etLabel.setOnTouchListener(clearTextTouchListener);

        Calendar cal = Calendar.getInstance();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Long alarmId = bundle.getLong("alarmId");
            alarm = alarmManager.findAlarmByAlarmId(alarmId);
            cal.setTimeInMillis(alarm.getTime());
            alarm.setCalendar(cal);
            tvAlarmTimePicker.setText(timeFormatter.format(cal.getTime()));
            etLabel.setText(alarm.getLabel());
            tvLabel.setText(alarm.getLabel());
            tvRepeat.setText(getWeekdaysLabel(alarm.getRepeat()));
            alarmSound = alarm.getSound();
            //tvSound.setText(alarmSound);
            weekdaysString = alarm.getRepeat();
            addAlarmScreenTitle.setText("Edit Alarm");
            addAlarmScreenpOptionsTitle.setText("Edit Alarm");
            tvDelete.setVisibility(View.VISIBLE);
        } else {
            tvAlarmTimePicker.setText(timeFormatter.format(cal.getTime()));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            alarm.setTime(cal.getTimeInMillis());
            alarm.setCalendar(cal);
            addAlarmScreenTitle.setText("New Alarm");
            addAlarmScreenpOptionsTitle.setText("New Alarm");
            tvDelete.setVisibility(View.INVISIBLE);
        }
    }

    public View.OnTouchListener clearTextTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etLabel.getRight() - etLabel.getCompoundDrawables()[2].getBounds().width()) - 50) {
                    etLabel.setText("");
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
                case R.id.alarm_time_picker_btn:
                    openTimePickerDialog();
                    break;
                case R.id.add_alarm_screen_cancel_btn:
                    getActivity().onBackPressed();
                    break;
                case R.id.repeat_alarm_ll:
                    alarmOptionType = AlarmOptionsType.Repeat.toString();
                    showRepeatAndSoundsAlarmOptionsScreen();
                    /*alarmOptionsAdapter = new AlarmOptionsAdapter(getActivity().getApplicationContext(), getDaysList(), getDaysMap());
                    repeatAlarmList.setAdapter(alarmOptionsAdapter);*/
                    showSelectionList("Weekdays");
                    break;
                case R.id.alarm_options_back_btn:
                    hideIme(view);
                    saveDataOnAlarmOptionsBackBtn();
                    break;

                case R.id.label_alarm_ll:
                    alarmOptionType = AlarmOptionsType.Label.toString();
                    showAlarmLabelScreen();
                    break;

                case R.id.ll_sound_option:
//                    alarmOptionType = AlarmOptionsType.Sound.toString();
//                    showRepeatAndSoundsAlarmOptionsScreen();
//                    alarmOptionsAdapter = new AlarmOptionsAdapter(getActivity().getApplicationContext(), getSoundList(), null);
//                    repeatAlarmList.setAdapter(alarmOptionsAdapter);
                    break;

                case R.id.tv_delete:
                    android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete");
                    alert.setMessage("Are you sure you want to delete this Alarm?");
                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            int j = 0;
                            while (j++ < 7) {
                                cancelAlarm(Integer.valueOf("" + alarm.getAlarmId() + 77 + j));
                            }
                            alarmManager.deleteAlarm(alarm);
                            getActivity().onBackPressed();
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

                case R.id.add_alarm_screen_save_btn:

                    if (weekdaysString.isEmpty()) {
                        //alertManager.getAlert(getCenesActivity(), "Please Select Repeat Day / Days For Alarm.", null, null, false, "OK");
                        //return;
                    } else if (etLabel.getText().toString().trim().isEmpty()) {
                        alertManager.getAlert(getCenesActivity(), "Please Add A Label For Alarm.", null, null, false, "OK");
                        return;
                    }

                    alarm.setLabel(etLabel.getText().toString().trim());

                    while (getWeekdaysLabel(weekdaysString).equals("Never")/* && alarm.getIsOn() == 1 */ && alarm.getTime() < System.currentTimeMillis()) {
                        //alarm.setIsOn(0);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(alarm.getTime());
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        alarm.setTime(calendar.getTimeInMillis());
                        alarm.setCalendar(calendar);
                        //alarmManager.updateAlarm(alarm);
                    }

                    if (alarm.getAlarmId() == 0) {
                        alarm.setIsOn(1);
                        alarm.setSound(tvSound.getText().toString());
                        //alarm.setRepeat(tvRepeat.getText().toString());
                        alarm.setRepeat(weekdaysString);
                        alarm.setAlarmId(alarmManager.addAlarm(alarm));
                    } else {
                        alarm.setSound(alarmSound);
                        /*String repeatStr = "";
                        for (String keys : repeatAlarmMap.keySet()) {
                            repeatStr += keys + " ";
                        }
                        alarm.setRepeat(repeatStr.substring(0, repeatStr.length() - 1));*/
                        alarm.setRepeat(weekdaysString);
                        alarmManager.updateAlarm(alarm);
                    }

                    //System.out.println("cal time: " + alarm.getCalendar().getTime());

                    if (alarm.getIsOn() == 1) {
                        int j = 0;
                        while (j++ < 7) {
                            cancelAlarm(Integer.valueOf("" + alarm.getAlarmId() + 77 + j));
                        }

                        Iterator iterator = hMapSelectedWeekdays.entrySet().iterator();
                        while (iterator.hasNext()) {
                            LinkedHashMap.Entry<String, Boolean> entry = (LinkedHashMap.Entry<String, Boolean>) iterator.next();

                            for (int i = 0; i < weekdayObjects.length; i++) {
                                if (entry.getKey() == weekdayObjects[i] || entry.getKey().equals(weekdayObjects[i])) {
                                    Calendar calendar = alarm.getCalendar();
                                    calendar.set(Calendar.DAY_OF_WEEK, calendarWeekdayObjects[i]);
                                    setAlarm((Calendar) calendar.clone(), alarm, true);
                                }
                            }
                        }

                        if (hMapSelectedWeekdays.size() < 1) {
                            setAlarm((Calendar) alarm.getCalendar().clone(), alarm, false);
                        }
                    }

                    getActivity().onBackPressed();
                    clearHashMaps();
            }
        }
    };

    public void saveDataOnAlarmOptionsBackBtn() {
        /*if (repeatAlarmMap.size() > 0) {
                        String repeatString = "";
                        for (String keys : repeatAlarmMap.keySet()) {
                            repeatString += keys + " ";
                        }
                        tvRepeat.setText(repeatString);
                    }
                    tvSound.setText(alarmSound);
                    tvLabel.setText(etLabel.getText().toString());
                    hideAlamOptionsScreen();*/

        try {
            hMapSelected = new LinkedHashMap<>();

            if (hMapSelectedWeekdays == null) {
                hMapSelectedWeekdays = new LinkedHashMap<>();
            }
            Iterator iterator = hMap.entrySet().iterator();
            weekdaysString = "";
            weekdaysLabelString = "";
            while (iterator.hasNext()) {
                LinkedHashMap.Entry<String, Boolean> entry = (LinkedHashMap.Entry<String, Boolean>) iterator.next();
                if (entry.getValue()) {
                    hMapSelected.put(entry.getKey(), entry.getValue());
                    weekdaysString = weekdaysString.concat(entry.getKey() + ",");
                    weekdaysLabelString = weekdaysLabelString.concat(entry.getKey().substring(0, 3) + " ");
                }
            }

            if (weekdaysString.contains(",")) {
                weekdaysString = weekdaysString.substring(0, weekdaysString.lastIndexOf(","));
                weekdaysLabelString = weekdaysLabelString.substring(0, weekdaysLabelString.lastIndexOf(" "));
            }

            hMapWeekdays = hMap;
            hMapSelectedWeekdays = hMapSelected;
            if (weekdaysLabelString.isEmpty() || weekdaysLabelString.equals("")) {
                weekdaysLabelString = "Never";
            }
            if (hMapSelectedWeekdays.size() == 7) {
                weekdaysLabelString = "Every day";
            }
            if (hMapSelectedWeekdays.size() == 1) {
                weekdaysLabelString = "Every " + weekdaysString;
            }
            System.out.println(weekdaysString + " --- " + weekdaysLabelString);

        } catch (Exception e) {
            e.printStackTrace();
        }

        tvRepeat.setText(weekdaysLabelString);
        //tvSound.setText(alarmSound);
        tvLabel.setText(etLabel.getText().toString());
        hideAlamOptionsScreen();
    }


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
        Intent intent = new Intent(getActivity().getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getBaseContext(), RSQ, intent, 0);
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        System.out.println("cancelAlarm with requestId: " + RSQ);
    }

    TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                    String timeString = "";
                    if (selectedHour < 10) {
                        timeString += "0";
                    }
                    timeString += selectedHour + " : ";
                    if (selectedMinute < 10) {
                        timeString += "0";
                    }
                    timeString += selectedMinute;

                    tvAlarmTimePicker.setText(timeString);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    alarm.setTime(calendar.getTimeInMillis());
                    alarm.setCalendar(calendar);
                }
            };

    public void openTimePickerDialog() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog alarmTimePickerDialog = new TimePickerDialog(this.getActivity(), timePickerListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        alarmTimePickerDialog.show();
    }

    public List<String> getDaysList() {
        List<String> days = new ArrayList<>();

        days.add("Every Sunday");
        days.add("Every Monday");
        days.add("Every Tuesday");
        days.add("Every Wednesday");
        days.add("Every Thursday");
        days.add("Every Friday");
        days.add("Every Saturday");

        return days;
    }

    private static String[] weekdayObjects = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static int[] calendarWeekdayObjects = new int[]{Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};

    public Map<String, String> getDaysMap() {
        Map<String, String> dayMap = new HashMap<>();

        dayMap.put("Every Sunday", "S");
        dayMap.put("Every Monday", "M");
        dayMap.put("Every Tuesday", "T");
        dayMap.put("Every Wednesday", "W");
        dayMap.put("Every Thursday", "T");
        dayMap.put("Every Friday", "F");
        dayMap.put("Every Saturday", "S");

        return dayMap;
    }

    public List<String> getSoundList() {
        List<String> days = new ArrayList<>();

        days.add("Radar");
        days.add("Apex");
        days.add("Bells");
        days.add("Chimes");
        days.add("Opening");
        days.add("Signal");
        days.add("Twinkle");

        return days;
    }

    public void showRepeatAndSoundsAlarmOptionsScreen() {
        saveDataOnBackPressed = true;

        fragmentView.findViewById(R.id.add_alarm_content).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.repeat_alarm_list_ll).setVisibility(View.VISIBLE);

        fragmentView.findViewById(R.id.rl_alarm_header).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.rl_alarm_options_header).setVisibility(View.VISIBLE);
    }

    public void showAlarmLabelScreen() {
        saveDataOnBackPressed = true;

        fragmentView.findViewById(R.id.add_alarm_content).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.rl_et_label).setVisibility(View.VISIBLE);

        fragmentView.findViewById(R.id.rl_alarm_header).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.rl_alarm_options_header).setVisibility(View.VISIBLE);
    }

    public void hideAlamOptionsScreen() {
        saveDataOnBackPressed = false;

        fragmentView.findViewById(R.id.add_alarm_content).setVisibility(View.VISIBLE);
        fragmentView.findViewById(R.id.repeat_alarm_list_ll).setVisibility(View.GONE);

        fragmentView.findViewById(R.id.rl_alarm_header).setVisibility(View.VISIBLE);
        fragmentView.findViewById(R.id.rl_alarm_options_header).setVisibility(View.GONE);
        fragmentView.findViewById(R.id.rl_et_label).setVisibility(View.GONE);
    }

    public void showCheckedSound() {
    }

    public void showSelectionList(final String type) {
        saveDataOnBackPressed = true;

        if (hMapWeekdays == null) {
            hMapWeekdays = new LinkedHashMap<>();
            for (int i = 0; i < weekdayObjects.length; ++i) {
                hMapWeekdays.put(weekdayObjects[i], false);
            }
        }

        if (hMapSelectedWeekdays != null) {
            hMapWeekdays.putAll(hMapSelectedWeekdays);
        }

        hMap = new LinkedHashMap<>();

        if (type.equals("Weekdays")) {
            hMap = hMapWeekdays;
        }

        mWeekdayAdapter = new WeekdayAdapter(hMap, type);
        repeatAlarmList.setAdapter(mWeekdayAdapter);
    }

    String weekdaysString = "";
    String weekdaysLabelString = "";

    LinkedHashMap<String, Boolean> hMap, hMapWeekdays;
    LinkedHashMap<String, Boolean> hMapSelected, hMapSelectedWeekdays = new LinkedHashMap<>();
    WeekdayAdapter mWeekdayAdapter;

    public class WeekdayAdapter extends BaseAdapter {
        private final ArrayList mData;
        private final String type;

        public WeekdayAdapter(LinkedHashMap<String, Boolean> hMap, String type) {
            mData = new ArrayList();
            mData.addAll(hMap.entrySet());
            this.type = type;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public HashMap.Entry<String, Boolean> getItem(int position) {
            return (LinkedHashMap.Entry) mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_options_items, parent, false);
                holder = new ViewHolder();
                holder.tvWeekday = (CenesTextView) convertView.findViewById(R.id.alarm_options_item_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LinkedHashMap.Entry<String, Boolean> item = getItem(position);

            holder.tvWeekday.setText("Every " + item.getKey());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (item.getValue()) {
                        holder.tvWeekday.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.check_mark), null);
                    } else {
                        holder.tvWeekday.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                    }
                }
            });

            holder.tvWeekday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getValue()) {
                        hMap.put(item.getKey(), false);
                        holder.tvWeekday.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                    } else {
                        hMap.put(item.getKey(), true);
                        holder.tvWeekday.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.check_mark), null);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWeekdayAdapter = new WeekdayAdapter(hMap, type);
                            mWeekdayAdapter.notifyDataSetChanged();
                        }
                    });
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        private CenesTextView tvWeekday;
    }

    class AlarmOptionsAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        List<String> options;
        Map<String, String> optionsMap;

        public AlarmOptionsAdapter(Context context, List<String> options, Map<String, String> optionsMap) {
            this.context = context;
            this.options = options;
            this.optionsMap = optionsMap;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {

            final OptionsViewHolder optionsViewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.adapter_alarm_options_items, null);
                optionsViewHolder = new OptionsViewHolder();
                optionsViewHolder.alarmOptionsItemtv = (TextView) view.findViewById(R.id.alarm_options_item_tv);

                view.setTag(optionsViewHolder);
            } else {
                optionsViewHolder = (OptionsViewHolder) view.getTag();
            }

            optionsViewHolder.alarmOptionsItemtv.setText(options.get(position));

            if (alarmSound.equalsIgnoreCase(options.get(position))) {
                checkedPosition = position;
            }

            if (checkedPosition == position) {
                optionsViewHolder.alarmOptionsItemtv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.check_mark), null);
                alarmSound = optionsViewHolder.alarmOptionsItemtv.getText().toString();
            } else {
                optionsViewHolder.alarmOptionsItemtv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                //alarmSound = optionsViewHolder.alarmOptionsItemtv.getText().toString();
            }

            optionsViewHolder.alarmOptionsItemtv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (position == checkedPosition) {
                                checkedPosition = -1;
                            } else {
                                checkedPosition = position;
                                alarmSound = optionsViewHolder.alarmOptionsItemtv.getText().toString();
                            }
                            alarmOptionsAdapter.notifyDataSetChanged();
                        }
                    });

                }
            });

            return view;
        }

        int checkedPosition = -1;

        class OptionsViewHolder {
            TextView alarmOptionsItemtv;
        }
    }

    public void hideIme(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
