package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.List;

public class CenesUserManagerImpl  {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createTableQuery = "CREATE TABLE cenes_users (user_id INTEGER, " +
            "name TEXT, " +
            "picture TEXT, " +
            "phone TEXT)";

    public CenesUserManagerImpl(CenesApplication cenesApplication){
        //this.cenesApplication = cenesApplication;
        //cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
    }

    public void addUser(List<User> users) {

        for (User user: users) {
            addUser(user);
        }
    }
    public void addUser(User user){
        try {
            User userExists = fetchCenesUserByUserId(user.getUserId());
            if (userExists != null && !CenesUtils.isEmpty(userExists.getName())) {

                user.setUserId(userExists.getUserId());
                updateCenesUser(user);

            } else {
                if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                    CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
                }

                String insertQuery1 = "insert into cenes_users(user_id, name, picture, phone) values("+user.getUserId()+", '"+user.getName()+"', '"+user.getPicture()+"'," +
                        " '"+user.getPhone()+"')";

                String insertQuery = "insert into cenes_users(user_id, name, picture, phone) values(?, ?, ?, ?)";
                SQLiteStatement stmt = CenesBaseActivity.sqlLiteDatabase.compileStatement(insertQuery);
                stmt.bindLong(1, user.getUserId());
                stmt.bindString(2, !CenesUtils.isEmpty(user.getName()) ? user.getName() : "Guest");
                stmt.bindString(3, !CenesUtils.isEmpty(user.getPicture()) ? user.getPicture() : "");
                stmt.bindString(4, !CenesUtils.isEmpty(user.getPhone()) ? user.getPhone() : "");
                long entryID = stmt.executeInsert();
                stmt.clearBindings();

                //String insertQuery = "insert into cenes_users values("+user.getUserId()+", '"+user.getName()+"', '"+user.getPicture()+"'," +
                //        " '"+user.getPhone()+"')";
                //System.out.println("Insert Query : "+insertQuery);
                //CenesBaseActivity.sqlLiteDatabase.execSQL(insertQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public User fetchCenesUserByUserId(Integer userId) {

        User user = null;
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select * from cenes_users where user_id = "+userId;
            System.out.println("User Select Query : "+query);
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return user;
    }

    public void updateCenesUser(User user) {

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            if (CenesUtils.isEmpty(user.getName())) {
                return;
            }
            String updateQuery = "update cenes_users set name = '"+user.getName()+"', " +
                    " picture = '"+user.getPicture()+"', phone = '"+user.getPhone()+"' where user_id = "+user.getUserId()+" ";
            System.out.println("Update Query : "+updateQuery);
            CenesBaseActivity.sqlLiteDatabase.execSQL(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void deleteAllUsers() {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from cenes_users";
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           // CenesBaseActivity.sqlLiteDatabase.close();
        }
    }
}
