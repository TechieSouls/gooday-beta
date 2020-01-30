package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class CalendarSyncTokenManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createCalendarTableQuery = "CREATE TABLE calendar_sync_tokens " +
            "(refresh_token_id INTEGER, " +
            "account_type TEXT, " +
            "user_id INTEGER, " +
            "email_id TEXT, " +
            "refresh_token TEXT)";

    public CalendarSyncTokenManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    public void addNewRow(CalenadarSyncToken calenadarSyncToken) {
        try {

            CalenadarSyncToken calenadarSyncTokenDB = null;
            if (calenadarSyncToken.getRefreshTokenId() != null) {
                calenadarSyncTokenDB = fetchCalendarByRefreshTokenId(calenadarSyncToken.getRefreshTokenId() );
            }

            if (calenadarSyncTokenDB == null) {

                this.db = cenesDatabase.getReadableDatabase();
                if (CenesUtils.isEmpty(calenadarSyncToken.getAccountType())) {
                    calenadarSyncToken.setAccountType("");
                }
                if (CenesUtils.isEmpty(calenadarSyncToken.getEmailId())) {
                    calenadarSyncToken.setEmailId("");
                }

                if (CenesUtils.isEmpty(calenadarSyncToken.getRefreshToken())) {
                    calenadarSyncToken.setRefreshToken("");
                }
                String insertQuery = "insert into calendar_sync_tokens (`account_type`, `email_id`, `user_id`, `refresh_token`, `refresh_token_id`) values ('"+calenadarSyncToken.getAccountType()+"', '"+calenadarSyncToken.getEmailId()+"', " +
                        ""+calenadarSyncToken.getUserId()+", '"+calenadarSyncToken.getRefreshToken()+"', "+calenadarSyncToken.getRefreshTokenId()+")";
                System.out.println(insertQuery);
                db.execSQL(insertQuery);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CalenadarSyncToken fetchCalendarByRefreshTokenId(Integer refresh_token_id) {
        CalenadarSyncToken calenadarSyncToken = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from calendar_sync_tokens where refresh_token_id = "+refresh_token_id;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                calenadarSyncToken = putData(cursor);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calenadarSyncToken;
    }

    public CalenadarSyncToken fetchCalendarByAccountType(String accountType) {
        CalenadarSyncToken calenadarSyncToken = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from calendar_sync_tokens where account_type = '"+accountType+"'";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                calenadarSyncToken = putData(cursor);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calenadarSyncToken;
    }

    public List<CalenadarSyncToken> fetchCalendarAll() {
        List<CalenadarSyncToken> calenadarSyncTokens = new ArrayList<>();
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from calendar_sync_tokens";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                CalenadarSyncToken calenadarSyncToken = putData(cursor);
                calenadarSyncTokens.add(calenadarSyncToken);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calenadarSyncTokens;
    }

    public void deleteCalendarByRefreshTokenId(Integer refresh_token_id){
        this.db = cenesDatabase.getReadableDatabase();

        String deleteQuery = "delete from calendar_sync_tokens where refresh_token_id = "+refresh_token_id+"";
        db.execSQL(deleteQuery);

    }

    public void deleteCalendarAll(){
        this.db = cenesDatabase.getReadableDatabase();

        String deleteQuery = "delete from calendar_sync_tokens";
        db.execSQL(deleteQuery);

    }

    private CalenadarSyncToken putData(Cursor cursor){

        CalenadarSyncToken calenadarSyncToken = new CalenadarSyncToken();
        calenadarSyncToken.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        calenadarSyncToken.setRefreshTokenId(cursor.getInt(cursor.getColumnIndex("refresh_token_id")));
        calenadarSyncToken.setRefreshToken(cursor.getString(cursor.getColumnIndex("refresh_token")));
        calenadarSyncToken.setAccountType(cursor.getString(cursor.getColumnIndex("account_type")));
        calenadarSyncToken.setEmailId(cursor.getString(cursor.getColumnIndex("email_id")));
        return calenadarSyncToken;

    }

}
