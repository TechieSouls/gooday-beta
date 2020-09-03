package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

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
        //this.cenesApplication = cenesApplication;
        //cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
    }

    public void addUserContact(UserContact userContact) {
        try {

            UserContact userContactDB = null;
            if (userContact.getUserContactId() != null) {
                userContactDB = fetchUserContactByUserContactId(userContact.getUserContactId());
            }

            if (userContactDB == null) {

                if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                    CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
                }
                if (CenesUtils.isEmpty(userContact.getName())) {
                    userContact.setName("");
                }
                if (CenesUtils.isEmpty(userContact.getPhone())) {
                    userContact.setPhone("");
                }
                if (CenesUtils.isEmpty(userContact.getCenesMember())) {
                    userContact.setCenesMember("no");
                }

                String insertQuery = "insert into user_contacts(user_contact_id, name, user_id, friend_id, " +
                        "cenes_member, phone) values(?, ? ,? ,? ,? ,?)";
                //CenesBaseActivity.sqlLiteDatabase.execSQL(insertQuery);

                String insertQuery1 = "insert into user_contacts values("+userContact.getUserContactId()+", '"+userContact.getName().replaceAll("'", "''")+"', " +
                        ""+userContact.getUserId()+", "+userContact.getFriendId()+", '"+userContact.getCenesMember()+"', " +
                        "'"+userContact.getPhone()+"')";

                SQLiteStatement stmt = CenesBaseActivity.sqlLiteDatabase.compileStatement(insertQuery);
                stmt.bindLong(1, userContact.getUserContactId());
                stmt.bindString(2, !CenesUtils.isEmpty(userContact.getName()) ? userContact.getName().replaceAll("'", "''") : "");
                stmt.bindLong(3, userContact.getUserId() != null ? userContact.getUserId() : 0);
                stmt.bindLong(4, userContact.getFriendId() != null ? userContact.getFriendId() : 0);
                stmt.bindString(5, userContact.getCenesMember());
                stmt.bindString(6, userContact.getPhone());
                long entryID = stmt.executeInsert();
                stmt.clearBindings();

                if (userContact.getUser() != null) {
                    CenesUserManagerImpl userManagerImpl = new CenesUserManagerImpl(cenesApplication);
                    userManagerImpl.addUser(userContact.getUser());
                }
                //CenesBaseActivity.sqlLiteDatabase.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : addUserContact");
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public List<UserContact> fetchAllUserContacts() {
        List<UserContact> userContacts = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select uc.*, cu.user_id as cenes_user_id, cu.name as cenes_username, cu.picture as cenes_user_photo, " +
                    "cu.phone as cenes_user_phone from user_contacts uc LEFT JOIN " +
                    "cenes_users cu on uc.friend_id = cu.user_id order by cu.name asc";

            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            while (cursor.moveToNext()) {
                UserContact userContact = processUserContactData(cursor);

                if (cursor.getInt(cursor.getColumnIndex("cenes_user_id")) != 0) {
                    User user = new User();
                    user.setUserId(cursor.getInt(cursor.getColumnIndex("cenes_user_id")));
                    user.setName(cursor.getString(cursor.getColumnIndex("cenes_username")));
                    user.setPicture(cursor.getString(cursor.getColumnIndex("cenes_user_photo")));
                    user.setPhone(cursor.getString(cursor.getColumnIndex("cenes_user_phone")));
                    userContact.setUser(user);
                }

                userContacts.add(userContact);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : fetchAllUserContacts");
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return userContacts;
    }

    public UserContact fetchUserContactByUserContactId(Integer userContactId) {
        UserContact userContact = null;
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select * from user_contacts where user_contact_id = "+userContactId;
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                userContact =  processUserContactData(cursor);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : fetchUserContactByUserContactId");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return userContact;
    }

    public UserContact fetchUserContactByUserId(Integer useriId) {
        UserContact userContact = null;
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select * from user_contacts where user_id = "+useriId;
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                userContact = processUserContactData(cursor);

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : fetchUserContactByUserId");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return userContact;
    }

    public void deleteAllUserContacts() {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "delete from user_contacts";
            CenesBaseActivity.sqlLiteDatabase.delete("user_contacts", null, null);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : deleteAllUserContacts");
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public UserContact processUserContactData(Cursor cursor) {

        UserContact userContact = new UserContact();
        userContact.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
        if (cursor.getInt(cursor.getColumnIndex("friend_id")) == 0) {
            userContact.setFriendId(null);
        } else {
            userContact.setFriendId(cursor.getInt(cursor.getColumnIndex("friend_id")));
        }
        if (cursor.getInt(cursor.getColumnIndex("user_id")) == 0) {
            userContact.setUserId(null);
        } else {
            userContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        }
        userContact.setName(cursor.getString(cursor.getColumnIndex("name")));
        userContact.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        userContact.setCenesMember(cursor.getString(cursor.getColumnIndex("cenes_member")));
        return userContact;
    }
}
