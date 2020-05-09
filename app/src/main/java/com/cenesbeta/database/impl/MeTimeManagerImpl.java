package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class MeTimeManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    MeTimePatternManagerImpl meTimePatternManagerImpl;

    public static String CreateTableQuery = "CREATE TABLE metime_recurring_events (recurring_event_id LONG, " +
            "title TEXT, " +
            "user_id LONG, " +
            "start_time LONG, " +
            "end_time LONG, " +
            "timezone TEXT, " +
            "photo TEXT, " +
            "days TEXT)";

    public MeTimeManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.meTimePatternManagerImpl = new MeTimePatternManagerImpl(this.cenesApplication);
    }

    public void addMeTime(MeTime metime){

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String title = "";
            if (!CenesUtils.isEmpty(metime.getTitle())) {
                title = metime.getTitle().replaceAll("'","''");
            }

            String timezone = "";
            if (!CenesUtils.isEmpty(metime.getTimezone())) {
                timezone = metime.getTimezone().replaceAll("'","''");
            }

            String photo = "";
            if (!CenesUtils.isEmpty(metime.getPhoto())) {
                photo = metime.getPhoto().replaceAll("'","''");
            }

            String days = "";
            if (!CenesUtils.isEmpty(metime.getDays())) {
                days = metime.getDays().replaceAll("'","''");
            }
            String insertQuery = "insert into metime_recurring_events values("+metime.getRecurringEventId()+", '"+title+"', "+metime.getUserId()+"," +
                    " "+metime.getStartTime()+", "+metime.getEndTime()+", '"+metime.getTimezone()+"', '"+photo+"',  '"+days+"')";

            System.out.println(insertQuery);
            db.execSQL(insertQuery);

            if (metime.getItems() != null) {
                for (MeTimeItem meTimeItem: metime.getItems()) {
                    this.meTimePatternManagerImpl.addMeTimePattern(meTimeItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    public List<MeTime> fetchAllMeTimeRecurringEvents() {
        List<MeTime> metimeRecurringEvents = new ArrayList<>();

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from metime_recurring_events";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                MeTime meTime = new MeTime();
                meTime.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
                meTime.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                meTime.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
                meTime.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
                meTime.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
                meTime.setTimezone(cursor.getString(cursor.getColumnIndex("timezone")));
                meTime.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));

                List<MeTimeItem> items = this.meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
                meTime.setItems(items);

                metimeRecurringEvents.add(meTime);
            }
            cursor.close();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return metimeRecurringEvents;
    }

    public MeTime findMetimeEventByRecurringEventId(Long recurringEventId) {
        if (!this.db.isOpen()) {
            this.db = cenesDatabase.getReadableDatabase();
        }
        String query = "select * from metime_recurring_events where recurring_event_id = "+recurringEventId+"";
        Cursor cursor = db.rawQuery(query, null);
        MeTime meTime = null;
        if (cursor.moveToFirst()) {
            meTime = new MeTime();
            meTime.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
            meTime.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            meTime.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
            meTime.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
            meTime.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
            meTime.setTimezone(cursor.getString(cursor.getColumnIndex("timezone")));
            meTime.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));

            List<MeTimeItem> items = this.meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
            meTime.setItems(items);

            db.close();
            return meTime;
        }
        db.close();
        return meTime;
    }

    public void deleteAllMeTimeRecurringEventsByRecurringEventId(Long recurringEventId) {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from metime_recurring_events where recurring_event_id = "+recurringEventId+" ";
            db.execSQL(deleteQuery);

            this.meTimePatternManagerImpl.deleteMeTimeRecurringPatternsByRecurringEventId(recurringEventId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }


    public void deleteAllMeTimeRecurringEvents() {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from metime_recurring_events";
            db.execSQL(deleteQuery);

            this.meTimePatternManagerImpl.deleteMeTimeRecurringPatterns();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void updateMeTimePhoto(Long recurringEventId, String photoUrl) {

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            db.execSQL("update metime_recurring_events set photo = '"+photoUrl+"' where recurring_event_id = "+recurringEventId+" ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

}
