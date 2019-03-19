package com.cenes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cenes.application.CenesApplication;

/**
 * Created by puneet on 11/8/17.
 */

public class CenesDatabase {

    public DataBaseOpenHelper dataBaseOpenHelper;

     public CenesDatabase(CenesApplication cenesApplication) {
        // TODO Auto-generated constructor stub
        dataBaseOpenHelper = new DataBaseOpenHelper(cenesApplication);
    }

    public SQLiteDatabase getReadableDatabase() {
        return dataBaseOpenHelper.getReadableDatabase();
    }

    public SQLiteDatabase getWriteableDatabase() {
        return dataBaseOpenHelper.getWritableDatabase();
    }

    public class DataBaseOpenHelper extends SQLiteOpenHelper {

        public static final String DATA_BASE_NAME = "cenes.db";
        public static final int VERSION = 1;

        public DataBaseOpenHelper(Context context) {
            super(context, DATA_BASE_NAME, null, VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("CREATE TABLE IF NOT EXISTS user_record (user_id LONG, email TEXT, facebook_auth_token TEXT, facebook_id TEXT,name TEXT, password TEXT, tocken TEXT, api_url TEXT, picture TEXT, gender TEXT, phone TEXT, birth_date LONG)");
            db.execSQL("CREATE TABLE alarms (alarm_id INTEGER PRIMARY KEY AUTOINCREMENT, label TEXT, repeat TEXT, sound TEXT,alarm_time LONG, is_on INTEGER)");
            //db.execSQL("CREATE TABLE reminders (reminder_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT,reminder_time LONG, location TEXT,created_by_id LONG, status TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }

    }
}
