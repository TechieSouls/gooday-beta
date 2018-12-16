package com.cenes.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenes.application.CenesApplication;
import com.cenes.bo.Alarm;
import com.cenes.database.CenesDatabase;
import com.cenes.database.manager.AlarmManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 31/10/17.
 */

public class AlarmManagerImpl implements AlarmManager {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public AlarmManagerImpl(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    @Override
    public int addAlarm(Alarm alarm) {
        db.execSQL("insert into alarms(label,repeat,sound,alarm_time,is_on) values('" + alarm.getLabel() + "' , '" + alarm.getRepeat()
                + "' , '" + alarm.getSound() + "' , " + alarm.getTime() + "," + alarm.getIsOn() + ")");

        Cursor cur = db.rawQuery("SELECT last_insert_rowid()", null);
        cur.moveToFirst();
        int alarmId = cur.getInt(0);
        cur.close();
        return alarmId;
    }

    @Override
    public Alarm findAlarmByAlarmId(Long alarmId) {
        Cursor cursor = db.rawQuery("select * from alarms where alarm_id = " + alarmId + "", null);
        Alarm alarm = null;

        if (cursor.moveToFirst()) {
            alarm = new Alarm();
            alarm.setAlarmId(cursor.getInt(cursor.getColumnIndex("alarm_id")));
            alarm.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            alarm.setRepeat(cursor.getString(cursor.getColumnIndex("repeat")));
            alarm.setSound(cursor.getString(cursor.getColumnIndex("sound")));
            alarm.setTime(cursor.getLong(cursor.getColumnIndex("alarm_time")));
            alarm.setIsOn(cursor.getInt(cursor.getColumnIndex("is_on")));
            return alarm;
        }
        return alarm;
    }

    @Override
    public List<Alarm> getAlarms() {
        Cursor cursor = db.rawQuery(
                "select * from alarms", null);

        List<Alarm> alarms = new ArrayList<>();
        Alarm alarm = null;

        while (cursor.moveToNext()) {
            alarm = new Alarm();
            alarm.setAlarmId(cursor.getInt(cursor.getColumnIndex("alarm_id")));
            alarm.setLabel(cursor.getString(cursor.getColumnIndex("label")));
            alarm.setRepeat(cursor.getString(cursor.getColumnIndex("repeat")));
            alarm.setSound(cursor.getString(cursor.getColumnIndex("sound")));
            alarm.setTime(cursor.getLong(cursor.getColumnIndex("alarm_time")));
            alarm.setIsOn(cursor.getInt(cursor.getColumnIndex("is_on")));
            alarms.add(alarm);
        }
        return alarms;
    }

    @Override
    public void updateAlarm(Alarm alarm) {
        db.execSQL("update alarms set label = '" + alarm.getLabel() + "', alarm_time = " + alarm.getTime() +
                " ,sound = '" + alarm.getSound() + "', repeat = '" + alarm.getRepeat() + "', is_on = " + alarm.getIsOn() +
                " where alarm_id = " + alarm.getAlarmId());
    }

    @Override
    public void deleteAlarm(Alarm alarm) {
        db.execSQL("delete from alarms where alarm_id = " + alarm.getAlarmId());
    }

    @Override
    public void deleteAll() {
        db.execSQL("delete from alarms");
    }
}
