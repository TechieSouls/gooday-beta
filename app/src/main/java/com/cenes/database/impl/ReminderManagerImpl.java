package com.cenes.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenes.application.CenesApplication;
import com.cenes.bo.Reminder;
import com.cenes.database.CenesDatabase;
import com.cenes.database.manager.ReminderManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mandeep on 25/11/17.
 */

public class ReminderManagerImpl implements ReminderManager {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public ReminderManagerImpl(CenesApplication cenesApplication) {
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    @Override
    public void addReminder(Reminder reminder) {
        db.execSQL("insert into reminders(title,reminder_time,location,created_by_id,status) values('" + reminder.getTitle() + "' , " + reminder.getReminderTime().getTimeInMillis()
                + " , '" + reminder.getLocation() + "' , " + reminder.getCreatedById() + ",'" + reminder.getStatus() + "')");
    }

    @Override
    public Reminder findReminderByReminderId(Long reminderId) {
        Cursor cursor = db.rawQuery("select * from reminders where reminder_id = " + reminderId + "", null);
        Reminder reminder = null;

        if (cursor.moveToFirst()) {
            reminder = new Reminder();
            reminder.setReminderId(cursor.getLong(cursor.getColumnIndex("reminder_id")));
            reminder.setTitle(cursor.getString(cursor.getColumnIndex("title")));

            Long reminderTimeInMillis = cursor.getLong(cursor.getColumnIndex("reminder_time"));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(reminderTimeInMillis);
            reminder.setReminderTime(cal);

            reminder.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            reminder.setCreatedById(cursor.getLong(cursor.getColumnIndex("created_by_id")));
            reminder.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            return reminder;
        }
        return reminder;
    }

    @Override
    public List<Reminder> getAllReminders() {
        Cursor cursor = db.rawQuery(
                "select * from reminders where status = 'Accept'", null);

        List<Reminder> reminders = new ArrayList<>();
        Reminder reminder = null;
        if (cursor.moveToFirst()) {
            reminder = new Reminder();
            reminder.setReminderId(cursor.getLong(cursor.getColumnIndex("reminder_id")));
            reminder.setTitle(cursor.getString(cursor.getColumnIndex("title")));

            Long reminderTimeInMillis = cursor.getLong(cursor.getColumnIndex("reminder_time"));
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(reminderTimeInMillis);
            reminder.setReminderTime(cal);

            reminder.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            reminder.setCreatedById(cursor.getLong(cursor.getColumnIndex("created_by_id")));
            reminder.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            reminders.add(reminder);
        }
        return reminders;
    }

    @Override
    public void updateReminder(Reminder reminder) {
        db.execSQL("update reminders set title = '" + reminder.getTitle() + "', reminder_time = " + reminder.getReminderTime().getTimeInMillis() + ", created_by_id = " + reminder.getCreatedById() + ", status = '" + reminder.getStatus() + "'");
    }

    @Override
    public void deleteReminderById(Long reminderId) {
        db.execSQL("delete from reminders where reminder_id = " + reminderId + " ");
    }
}