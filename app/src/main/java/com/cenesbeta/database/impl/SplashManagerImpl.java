package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Splash;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.database.manager.SplashManager;
import com.cenesbeta.util.CenesUtils;

/**
 * Created by neha on 29/04/20.
 */

public class SplashManagerImpl implements SplashManager {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String CreateTableQuery = "CREATE TABLE IF NOT EXISTS splash_record (" +
            "splash_record_id LONG, " +
            "splash_image TEXT)";

    public SplashManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    @Override
    public void addSplash(Splash splash){

        this.db = cenesDatabase.getReadableDatabase();

        String insertQuery = "insert into splash_record values(1, '" + splash.getSplashImage() +"')";



        System.out.println(insertQuery);
        db.execSQL(insertQuery);
        db.close();

    }


    @Override
    public Splash getSplash() {
        Splash splash = null;
        this.db = cenesDatabase.getReadableDatabase();
        String query = "select * from splash_record";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            splash = new Splash();
            splash.setSplashId(cursor.getInt(cursor.getColumnIndex("splash_record_id")));
            splash.setSplashImage(cursor.getString(cursor.getColumnIndex("splash_image")));

        }
        cursor.close();
        db.close();
        return splash;
    }

    @Override
    public void deleteSplash() {

        this.db = cenesDatabase.getReadableDatabase();
        String deleteQuery = "delete from splash_record";
        db.execSQL(deleteQuery);
        db.close();
    }
}
