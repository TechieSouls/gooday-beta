package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTime;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.bo.RecurringEventMember;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class MeTimeManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String TableName = "metime_recurring_events";
    public static String CreateTableQuery = "CREATE TABLE "+TableName+" (recurring_event_id LONG, " +
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
            String insertQuery = "insert into "+TableName+" values("+metime.getRecurringEventId()+", '"+title+"', "+metime.getUserId()+"," +
                    " "+metime.getStartTime()+", "+metime.getEndTime()+", '"+metime.getTimezone()+"', '"+photo+"',  '"+days+"')";

            System.out.println(insertQuery);
            db.execSQL(insertQuery);

            if (metime.getItems() != null) {
                for (MeTimeItem meTimeItem: metime.getItems()) {
                    MeTimePatternManagerImpl meTimePatternManagerImpl = new MeTimePatternManagerImpl(cenesApplication);
                    meTimePatternManagerImpl.addMeTimePattern(meTimeItem);
                }
            }
            if (metime.getRecurringEventMembers() != null && metime.getRecurringEventMembers().size() > 0) {
                RecurringEventMemberImpl recurringEventMemberImpl = new RecurringEventMemberImpl(cenesApplication);
                for (RecurringEventMember recurringEventMember: metime.getRecurringEventMembers()) {
                    recurringEventMemberImpl.addRecurringEventMember(recurringEventMember);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.db.isOpen()) {
                this.db.close();
            }
        }

    }

    public List<MeTime> fetchAllMeTimeRecurringEvents() {
        List<MeTime> metimeRecurringEvents = new ArrayList<>();

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from "+TableName+"";
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

                MeTimePatternManagerImpl meTimePatternManagerImpl = new MeTimePatternManagerImpl(cenesApplication);
                List<MeTimeItem> items = meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
                meTime.setItems(items);

                RecurringEventMemberImpl recurringEventMemberImpl = new RecurringEventMemberImpl(cenesApplication);
                List<RecurringEventMember> recurringEventMembers = recurringEventMemberImpl.findByRecurringEventId(Integer.parseInt(meTime.getRecurringEventId().toString()));
                meTime.setRecurringEventMembers(recurringEventMembers);

                metimeRecurringEvents.add(meTime);
            }
            cursor.close();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.db.isOpen()) {
                this.db.close();
            }
        }
        return metimeRecurringEvents;
    }

    public MeTime findMetimeEventByRecurringEventId(Long recurringEventId) {
        if (!this.db.isOpen()) {
            this.db = cenesDatabase.getReadableDatabase();
        }
        String query = "select * from "+TableName+" where recurring_event_id = "+recurringEventId+"";
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

            MeTimePatternManagerImpl meTimePatternManagerImpl = new MeTimePatternManagerImpl(cenesApplication);
            List<MeTimeItem> items = meTimePatternManagerImpl.fetchMeTimePatternByRecurringEventId(meTime.getRecurringEventId());
            meTime.setItems(items);

            if (this.db.isOpen()) {
                this.db.close();
            }
            return meTime;
        }
        if (this.db.isOpen()) {
            this.db.close();
        }
        return meTime;
    }

    public void deleteAllMeTimeRecurringEventsByRecurringEventId(Long recurringEventId) {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from "+TableName+" where recurring_event_id = "+recurringEventId+" ";
            db.execSQL(deleteQuery);

            MeTimePatternManagerImpl meTimePatternManagerImpl = new MeTimePatternManagerImpl(cenesApplication);
            meTimePatternManagerImpl.deleteMeTimeRecurringPatternsByRecurringEventId(recurringEventId);

            RecurringEventMemberImpl recurringEventMemberImpl = new RecurringEventMemberImpl(cenesApplication);
            recurringEventMemberImpl.deleteByRecurringEventId(Integer.valueOf(recurringEventId.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.db.isOpen()) {
                this.db.close();
            }
        }
    }


    public void deleteAllMeTimeRecurringEvents() {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from "+TableName+"";
            System.out.println(deleteQuery);
            db.execSQL(deleteQuery);

            MeTimePatternManagerImpl meTimePatternManagerImpl = new MeTimePatternManagerImpl(cenesApplication);
            meTimePatternManagerImpl.deleteMeTimeRecurringPatterns();

            RecurringEventMemberImpl recurringEventMemberImpl = new RecurringEventMemberImpl(cenesApplication);
            recurringEventMemberImpl.deleteAllRecurringEventMembers();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.db.isOpen()) {
                this.db.close();
            }
        }
    }

    public void updateMeTimePhoto(Long recurringEventId, String photoUrl) {

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            db.execSQL("update "+TableName+" set photo = '"+photoUrl+"' where recurring_event_id = "+recurringEventId+" ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.db.isOpen()) {
                this.db.close();
            }
        }
    }

}
