package com.cenesbeta.database.impl;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.database.manager.UserManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by puneet on 11/8/17.
 */

public class UserManagerImpl implements UserManager {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public UserManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    @Override
    public void addUser(User user){
        db.execSQL("insert into user_record values(" + user.getUserId()
                + " , '" + user.getEmail() + "' , '" + user.getFacebookAuthToken()
                + "' , '" + user.getFacebookID() + "' , '" + user.getName()+"' , '" + user.getPassword()
                + "' , '" + user.getAuthToken()
                + "' , '"+user.getApiUrl()+"' , '"+user.getPicture()+"' , '"+user.getGender()+"', '"+user.getPhone()+"', "+user.getBirthDate()+")");
    }

    @Override
    public boolean isUserExist(){
        Cursor cursor = db.rawQuery("select * from user_record", null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    @Override
    public User findUserByUserId(Long userId){
        Cursor cursor = db.rawQuery("select * from user_record where user_id = "+userId+"", null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setApiUrl(cursor.getString(cursor.getColumnIndex("api_url")));
            user.setAuthToken(cursor.getString(cursor.getColumnIndex("tocken")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setBirthDate(cursor.getLong(cursor.getColumnIndex("birth_date")));
            return user;
        }
        return user;
    }

    @Override
    public User getUser(){
        Cursor cursor = db.rawQuery(
                "select * from user_record", null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
            user.setAuthToken(cursor.getString(cursor.getColumnIndex("tocken")));

            //user.setUserId(18);
            //user.setAuthToken("1552477453360eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2ZXJnaWwudGFuMTU1MjM5MTA1MzM2MCJ9.IDL53gmPa3vHfNKFDUms6y9qOxbHvRUrtDUR709xF_0dONXucZG-_xt2S5IvjaJuRIaGrypmxts9-RDMvJ9lMA");
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setApiUrl(cursor.getString(cursor.getColumnIndex("api_url")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            user.setBirthDate(cursor.getLong(cursor.getColumnIndex("birth_date")));
            return user;
        }
        return user;
    }


    @Override
    public void updateProfilePic(User user) {
        db.execSQL("update user_record set picture = '"+user.getPicture()+"'");
    }

    @Override
    public void updateUser(User user) {
        db.execSQL("update user_record set name = '"+user.getName()+"', gender = '"+user.getGender()+"', email = '"+user.getEmail()+"', " +
                "tocken = '"+user.getAuthToken()+"', picture = '"+user.getPicture()+"', birth_date = "+user.getBirthDate()+"");
    }

    @Override
    public void deleteUserById(int userId) {
        db.execSQL("delete from user_record where user_id = "+userId);
    }

    @Override
    public void deleteAll() {
        db.execSQL("delete from user_record");
    }
}
