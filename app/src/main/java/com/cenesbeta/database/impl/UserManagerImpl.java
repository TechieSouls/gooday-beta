package com.cenesbeta.database.impl;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.database.manager.UserManager;
import com.cenesbeta.util.CenesUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by puneet on 11/8/17.
 */

public class UserManagerImpl implements UserManager {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String CreateTableQuery = "CREATE TABLE IF NOT EXISTS user_record (" +
            "user_id LONG, " +
            "email TEXT, " +
            "facebook_auth_token TEXT, " +
            "facebook_id TEXT," +
            "name TEXT, " +
            "password TEXT, " +
            "token TEXT, " +
            "auth_type TEXT, " +
            "api_url TEXT, " +
            "picture TEXT, " +
            "gender TEXT, " +
            "phone TEXT, " +
            "birth_day_str TEXT," +
            "country TEXT," +
            "google_id TEXT)";

    public UserManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    @Override
    public void addUser(User user){


        String insertQuery = "insert into user_record values(" + user.getUserId() +" , '" + user.getEmail() + "'";
        if (!CenesUtils.isEmpty(user.getFacebookAuthToken())) {
            insertQuery = insertQuery + ",'"+user.getFacebookAuthToken()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getFacebookAuthToken()+"";
        }
        if (!CenesUtils.isEmpty(user.getFacebookId())) {
            insertQuery = insertQuery + ",'"+user.getFacebookId()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getFacebookId()+"";
        }
        if (!CenesUtils.isEmpty(user.getName())) {
            insertQuery = insertQuery + ",'"+user.getName()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getName()+"";
        }
        if (!CenesUtils.isEmpty(user.getPassword())) {
            insertQuery = insertQuery + ",'"+user.getPassword()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getPassword()+"";
        }
        if (!CenesUtils.isEmpty(user.getAuthToken())) {
            insertQuery = insertQuery + ",'"+user.getAuthToken()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getAuthToken()+"";
        }
        if (user.getAuthType() != null && !CenesUtils.isEmpty(user.getAuthType().toString())) {
            insertQuery = insertQuery + ",'"+user.getAuthType()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getAuthType()+"";
        }
        if (!CenesUtils.isEmpty(user.getApiUrl())) {
            insertQuery = insertQuery + ",'"+user.getApiUrl()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getApiUrl()+"";
        }
        if (!CenesUtils.isEmpty(user.getPicture())) {
            insertQuery = insertQuery + ",'"+user.getPicture()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getPicture()+"";
        }
        if (!CenesUtils.isEmpty(user.getGender())) {
            insertQuery = insertQuery + ",'"+user.getGender()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getGender()+"";
        }
        if (!CenesUtils.isEmpty(user.getPhone())) {
            insertQuery = insertQuery + ",'"+user.getPhone()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getPhone()+"";
        }
        if (!CenesUtils.isEmpty(user.getBirthDateStr())) {
            insertQuery = insertQuery + ",'"+user.getBirthDateStr()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getBirthDateStr()+"";
        }
        if (!CenesUtils.isEmpty(user.getCountry())) {
            insertQuery = insertQuery + ",'"+user.getCountry()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getCountry()+"";
        }
        if (!CenesUtils.isEmpty(user.getGoogleId())) {
            insertQuery = insertQuery + ",'"+user.getGoogleId()+"'";
        } else {
            insertQuery = insertQuery + ","+user.getGoogleId()+"";
        }
        insertQuery = insertQuery + ")";
        System.out.println(insertQuery);
        db.execSQL(insertQuery);
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
        try {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                user.setApiUrl(cursor.getString(cursor.getColumnIndex("api_url")));
                user.setAuthToken(cursor.getString(cursor.getColumnIndex("token")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                user.setBirthDateStr(cursor.getString(cursor.getColumnIndex("birth_day_str")));

                if (User.AuthenticateType.email.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.email);
                } else if (User.AuthenticateType.facebook.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.facebook);
                } else if (User.AuthenticateType.google.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.google);
                }
                user.setFacebookId(cursor.getString(cursor.getColumnIndex("facebook_id")));
                user.setGoogleId(cursor.getString(cursor.getColumnIndex("google_id")));
                user.setCountry(cursor.getString(cursor.getColumnIndex("country")));

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User getUser(){
        Cursor cursor = db.rawQuery(
                "select * from user_record", null);
        User user = null;
        try {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                //user.setUserId(143);
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                user.setApiUrl(cursor.getString(cursor.getColumnIndex("api_url")));
                user.setAuthToken(cursor.getString(cursor.getColumnIndex("token")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                if (User.AuthenticateType.email.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.email);
                } else if (User.AuthenticateType.facebook.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.facebook);
                } else if (User.AuthenticateType.google.toString().equals(cursor.getString(cursor.getColumnIndex("auth_type")))) {
                    user.setAuthType(User.AuthenticateType.google);
                }            user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                user.setGoogleId(cursor.getString(cursor.getColumnIndex("google_id")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                user.setFacebookId(cursor.getString(cursor.getColumnIndex("facebook_id")));
                user.setBirthDateStr(cursor.getString(cursor.getColumnIndex("birth_day_str")));
                user.setCountry(cursor.getString(cursor.getColumnIndex("country")));

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void updateProfilePic(User user) {
        String query = "update user_record set picture = '"+user.getPicture()+"'";
        if (user.getPicture() == null) {
            query = "update user_record set picture = null";
        }
        db.execSQL(query);
    }

    @Override
    public void updateUser(User user) {
        db.execSQL("update user_record set name = '"+user.getName()+"', gender = '"+user.getGender()+"', email = '"+user.getEmail()+"', " +
                "token = '"+user.getAuthToken()+"', picture = '"+user.getPicture()+"', birth_day_str = '"+user.getBirthDateStr()+"'");
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
