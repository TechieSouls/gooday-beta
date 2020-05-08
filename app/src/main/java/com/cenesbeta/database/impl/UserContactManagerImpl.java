package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

public class UserContactManagerImpl {

    public static String TableName = "user_contacts";
    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createUserContactTableQuery = "CREATE TABLE "+TableName+" (user_contact_id INTEGER, " +
            "name TEXT, " +
            "user_id INTEGER, " +
            "friend_id INTEGER, " +
            "cenes_member TEXT, " +
            "phone TEXT)";

    public UserContactManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    public void addUserContact(UserContact userContact) {
        try {

            UserContact userContactDB = null;
            if (userContact.getUserContactId() != null) {
                userContactDB = fetchUserContactByUserContactId(userContact.getUserContactId());
            }

            if (userContactDB == null) {

                this.db = cenesDatabase.getReadableDatabase();
                if (CenesUtils.isEmpty(userContact.getName())) {
                    userContact.setName("");
                }
                if (CenesUtils.isEmpty(userContact.getPhone())) {
                    userContact.setPhone("");
                }
                if (CenesUtils.isEmpty(userContact.getCenesMember())) {
                    userContact.setCenesMember("no");
                }

                String insertQuery = "insert into user_contacts values("+userContact.getUserContactId()+", '"+userContact.getName()+"', " +
                        ""+userContact.getUserId()+", "+userContact.getFriendId()+", '"+userContact.getCenesMember()+"', " +
                        "'"+userContact.getPhone()+"')";
                db.execSQL(insertQuery);
                db.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public UserContact fetchUserContactByUserContactId(Integer userContactId) {
        UserContact userContact = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from user_contacts where user_contact_id = "+userContactId;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                userContact = new UserContact();
                userContact.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                userContact.setFriendId(cursor.getInt(cursor.getColumnIndex("friend_id")));
                userContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                userContact.setName(cursor.getString(cursor.getColumnIndex("name")));
                userContact.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                userContact.setCenesMember(cursor.getString(cursor.getColumnIndex("cenes_member")));

            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return userContact;
    }

    public UserContact fetchUserContactByUserId(Integer useriId) {
        UserContact userContact = null;
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String query = "select * from user_contacts where user_id = "+useriId;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                userContact = new UserContact();
                userContact.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                userContact.setFriendId(cursor.getInt(cursor.getColumnIndex("friend_id")));
                userContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                userContact.setName(cursor.getString(cursor.getColumnIndex("name")));
                userContact.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                userContact.setCenesMember(cursor.getString(cursor.getColumnIndex("cenes_member")));

            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return userContact;
    }

}
