package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.RecurringEventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.database.CenesDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecurringEventMemberImpl {

    public static String TableName = "recurring_event_members";

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    private CenesUserManagerImpl userManagerImpl;
    private UserContactManagerImpl userContactManagerImpl;


    public static String CreateTableQuery = "CREATE TABLE "+TableName+" (recurring_event_member_id INTEGER, " +
            "recurring_event_id TEXT, " +
            "user_id INTEGER)";

    public RecurringEventMemberImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
        userManagerImpl = new CenesUserManagerImpl(cenesApplication);
        userContactManagerImpl = new UserContactManagerImpl(cenesApplication);
    }

    public void addRecurringEventMember(RecurringEventMember recurringEventMember) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String insertQuery = "insert into recurring_event_members values("+recurringEventMember.getRecurringEventMemberId()+", " +
                    ""+recurringEventMember.getRecurringEventId()+", "+recurringEventMember.getUserId()+" )";

            System.out.println(insertQuery);
            CenesBaseActivity.sqlLiteDatabase.execSQL(insertQuery);

            if (recurringEventMember.getUser() != null) {
                userManagerImpl.addUser(recurringEventMember.getUser());
            }
            if (recurringEventMember.getUserContact() != null) {
                userContactManagerImpl.addUserContact(recurringEventMember.getUserContact());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public List<RecurringEventMember> findByRecurringEventId(Integer recurringEventId) {

        List<RecurringEventMember> recurringEventMemberList = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String searchQuery = "select * from "+TableName+" where recurring_event_id = "+recurringEventId+" ";
            System.out.println(searchQuery);
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(searchQuery, null);

            while (cursor.moveToNext()) {

                RecurringEventMember recurringEventMember = new RecurringEventMember();
                recurringEventMember.setRecurringEventMemberId(cursor.getInt(cursor.getColumnIndex("recurring_event_member_id")));
                recurringEventMember.setRecurringEventId(cursor.getInt(cursor.getColumnIndex("recurring_event_id")));
                recurringEventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));

                User cenesUser = userManagerImpl.fetchCenesUserByUserId(recurringEventMember.getUserId());
                if (cenesUser != null) {
                    recurringEventMember.setUser(cenesUser);
                }

                UserContact userContact = userContactManagerImpl.fetchUserContactByUserId(recurringEventMember.getUserId());
                if (userContact != null) {
                    recurringEventMember.setUserContact(userContact);
                }
                recurringEventMemberList.add(recurringEventMember);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
        return recurringEventMemberList;
    }

    public void deleteByRecurringEventId(Integer recurringEventId) {

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from "+TableName+" where recurring_event_id = "+recurringEventId+" ";
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }

    public void deleteAllRecurringEventMembers() {

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from "+TableName+" ";
            System.out.println(deleteQuery);
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }

}
