package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.CalenadarSyncToken;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

public class CalendarSyncTokenManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createCalendarTableQuery = "CREATE TABLE calendar_sync_tokens (refresh_token_id INTEGER, " +
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
                calenadarSyncTokenDB = fetchCalendarByUserId(calenadarSyncToken.getRefreshTokenId() );
            }

            if (calenadarSyncTokenDB == null) {

                this.db = cenesDatabase.getReadableDatabase();
                if (CenesUtils.isEmpty(calenadarSyncToken.getAccountType())) {
                    calenadarSyncToken.setAccountType("");
                }
                if (CenesUtils.isEmpty(calenadarSyncToken.getEmailId())) {
                    calenadarSyncToken.setEmailId("");
                }


                String insertQuery = "insert into calendar_sync_tokens values("+calenadarSyncToken.getAccountType()+", '"+calenadarSyncToken.getEmailId()+"', " +
                        ""+calenadarSyncToken.getUserId()+", "+calenadarSyncToken.getRefreshToken()+", '"+calenadarSyncToken.getRefreshTokenId()+"')";
                db.execSQL(insertQuery);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CalenadarSyncToken fetchCalendarByUserId(Integer userId) {
        CalenadarSyncToken calenadarSyncToken = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from calendar_sync_tokens where user_id = "+userId;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                calenadarSyncToken = new CalenadarSyncToken();

                calenadarSyncToken.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                calenadarSyncToken.setRefreshTokenId(cursor.getInt(cursor.getColumnIndex("refresh_token_id")));

                calenadarSyncToken.setRefreshToken(cursor.getString(cursor.getColumnIndex("refresh_token")));
                calenadarSyncToken.setAccountType(cursor.getString(cursor.getColumnIndex("account_type")));
                calenadarSyncToken.setEmailId(cursor.getString(cursor.getColumnIndex("email_id")));

            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calenadarSyncToken;
    }

    public CalenadarSyncToken fetchCalendarAll(Integer userId) {
        CalenadarSyncToken calenadarSyncToken = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from calendar_sync_tokens";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                calenadarSyncToken = new CalenadarSyncToken();

                calenadarSyncToken.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                calenadarSyncToken.setRefreshTokenId(cursor.getInt(cursor.getColumnIndex("refresh_token_id")));
                calenadarSyncToken.setRefreshToken(cursor.getString(cursor.getColumnIndex("refresh_token")));
                calenadarSyncToken.setAccountType(cursor.getString(cursor.getColumnIndex("account_type")));
                calenadarSyncToken.setEmailId(cursor.getString(cursor.getColumnIndex("email_id")));

            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calenadarSyncToken;
    }

    public void deleteCalendarByRefreshTokenId(Integer refresh_token_id){

        String deleteQuery = "delete from calendar_sync_tokens where refresh_token_id = '"+refresh_token_id+"'";
        db.execSQL(deleteQuery);

    }

    public void deleteCalendarAll(){

        String deleteQuery = "delete from calendar_sync_tokens";
        db.execSQL(deleteQuery);

    }

}
