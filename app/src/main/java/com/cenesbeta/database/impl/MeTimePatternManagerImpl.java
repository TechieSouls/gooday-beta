package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.MeTimeItem;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class MeTimePatternManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String CreateTableQuery = "CREATE TABLE metime_recurring_patterns (recurring_event_id LONG, " +
        "day_of_week TEXT, " +
        "day_of_week_timestamp LONG)";

        public MeTimePatternManagerImpl(CenesApplication cenesApplication){
                this.cenesApplication = cenesApplication;
                cenesDatabase = new CenesDatabase(cenesApplication);
                this.db = cenesDatabase.getReadableDatabase();
        }

        public void addMeTimePattern(MeTimeItem meTimeItem){
            try {
                if (!this.db.isOpen()) {
                    this.db = cenesDatabase.getReadableDatabase();
                }
                String dayOfWeek = "";
                if (!CenesUtils.isEmpty(meTimeItem.getDay_Of_week())) {
                    dayOfWeek = meTimeItem.getDay_Of_week().replaceAll("'","''");
                }

                String insertQuery = "insert into metime_recurring_patterns values("+meTimeItem.getRecurringEventId()+", '"+dayOfWeek+"', "+meTimeItem.getDayOfWeekTimestamp()+")";

                System.out.println(insertQuery);
                db.execSQL(insertQuery);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (this.db.isOpen()) {
                    this.db.close();
                }
            }
        }

        public List<MeTimeItem> fetchMeTimePatternByRecurringEventId(Long recurringEventId) {
            List<MeTimeItem> metimeRecurringPatterns = new ArrayList<>();
            try {
                if (!this.db.isOpen()) {
                    this.db = cenesDatabase.getReadableDatabase();
                }

                String query = "select * from metime_recurring_patterns where recurring_event_id = "+recurringEventId+" ";
                Cursor cursor = db.rawQuery(query, null);

                while (cursor.moveToNext()) {
                    MeTimeItem meTimeItem = new MeTimeItem();
                    meTimeItem.setRecurringEventId(cursor.getLong(cursor.getColumnIndex("recurring_event_id")));
                    meTimeItem.setDay_Of_week(cursor.getString(cursor.getColumnIndex("day_of_week")));
                    meTimeItem.setDayOfWeekTimestamp(cursor.getLong(cursor.getColumnIndex("day_of_week_timestamp")));
                    metimeRecurringPatterns.add(meTimeItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (this.db != null && this.db.isOpen()) {
                    this.db.close();
                }
            }
            return metimeRecurringPatterns;
        }

        public void deleteMeTimeRecurringPatternsByRecurringEventId(Long recurringEventId) {
            try {
                if (!this.db.isOpen()) {
                    this.db = cenesDatabase.getReadableDatabase();
                }
                String deleteQuery = "delete from metime_recurring_patterns where recurring_event_id = "+recurringEventId+" ";
                db.execSQL(deleteQuery);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (this.db.isOpen()) {
                    this.db.close();
                }
            }

        }

        public void deleteMeTimeRecurringPatterns() {

            try {
                if (!this.db.isOpen()) {
                    this.db = cenesDatabase.getReadableDatabase();
                }
                String deleteQuery = "delete from metime_recurring_patterns";
                System.out.println(deleteQuery);
                db.execSQL(deleteQuery);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (this.db.isOpen()) {
                    this.db.close();
                }
            }
        }
}
