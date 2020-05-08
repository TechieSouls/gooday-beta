package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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


    public static String createUserContactTableQuery = "CREATE TABLE "+TableName+" (recurring_event_member_id INTEGER, " +
            "recurring_event_id TEXT, " +
            "user_id INTEGER)";

    public RecurringEventMemberImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        userManagerImpl = new CenesUserManagerImpl(cenesApplication);
        userContactManagerImpl = new UserContactManagerImpl(cenesApplication);
    }

    public void addRecurringEventMember(RecurringEventMember recurringEventMember) {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String insertQuery = "insert into recurring_event_members values("+recurringEventMember.getRecurringEventMemberId()+", " +
                    ""+recurringEventMember.getRecurringEventId()+", "+recurringEventMember.getUserId()+" )";
            db.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void findByRecurringEventId(Integer recurringEventId) {

        List<RecurringEventMember> recurringEventMemberList = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String searchQuery = "select * from "+TableName+" where recurring_event_id = "+recurringEventId+" ";
            Cursor cursor = db.rawQuery(searchQuery, null);

            while (cursor.moveToNext()) {

                RecurringEventMember recurringEventMember = new RecurringEventMember();
                recurringEventMember.setRecurringEventMemberId(cursor.getColumnIndex("recurring_event_member_id"));
                recurringEventMember.setRecurringEventId(cursor.getColumnIndex("recurring_event_id"));
                recurringEventMember.setUserId(cursor.getColumnIndex("user_id"));

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
            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteByRecurringEventId(Integer recurringEventId) {

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from "+TableName+" where recurring_event_id = "+recurringEventId+" ";
            this.db.execSQL(deleteQuery);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
