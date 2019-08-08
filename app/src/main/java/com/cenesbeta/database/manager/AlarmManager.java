package com.cenesbeta.database.manager;

import com.cenesbeta.bo.Alarm;

import java.util.List;

/**
 * Created by mandeep on 31/10/17.
 */

public interface AlarmManager {

    public int addAlarm(Alarm alarm);
    public Alarm findAlarmByAlarmId(Long alarmId);
    public List<Alarm> getAlarms();
    public void updateAlarm(Alarm alarm);
    public void deleteAlarm(Alarm alarm);
    public void deleteAll();
}
