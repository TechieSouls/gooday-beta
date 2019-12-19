package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class EventMemberManagerImpl  {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    CenesUserManagerImpl cenesUserManagerImpl;
    UserContactManagerImpl userContactManagerImpl;

    public static String createTableQuery = "CREATE TABLE event_members (event_member_id LONG, " +
            "event_id LONG, " +
            "name TEXT, " +
            "picture TEXT, " +
            "status TEXT, " +
            "user_id INTEGER, " +
            "phone TEXT," +
            "cenes_member TEXT," +
            "user_contact_id INTEGER)";

    public EventMemberManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.cenesUserManagerImpl = new CenesUserManagerImpl(cenesApplication);
        this.userContactManagerImpl = new UserContactManagerImpl(cenesApplication);
    }

    public void addEventMember(List<EventMember> eventMembers) {
        try {
            for (EventMember eventMember: eventMembers) {
                addEventMember(eventMember);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void addEventMember(EventMember eventMember){

        try {
            this.db = cenesDatabase.getReadableDatabase();
            if (CenesUtils.isEmpty(eventMember.getPicture())) {
                eventMember.setPicture("");
            }
            if (CenesUtils.isEmpty(eventMember.getPhone())) {
                eventMember.setPhone("");
            }
            if (CenesUtils.isEmpty(eventMember.getCenesMember())) {
                eventMember.setCenesMember("");
            }
            String insertQuery = "insert into event_members values("+eventMember.getEventMemberId()+", "+eventMember.getEventId()+", '"+eventMember.getName()+"'," +
                    " '"+eventMember.getPicture()+"', '"+eventMember.getStatus()+"', "+eventMember.getUserId()+", '"+eventMember.getPhone()+"', " +
                    "'"+eventMember.getCenesMember()+"', "+eventMember.getUserContactId()+")";
            db.execSQL(insertQuery);
            db.close();
            if (eventMember.getUser() != null) {
                cenesUserManagerImpl.addUser(eventMember.getUser());
            }

            if (eventMember.getUserContact() != null) {
                userContactManagerImpl.addUserContact(eventMember.getUserContact());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<EventMember> fetchEventMembersByEventId(Long eventId) {
        this.db = cenesDatabase.getReadableDatabase();
        List<EventMember> eventMembers = new ArrayList<>();

        try {
            String query = "select * from event_members where event_id = "+eventId;
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {

                EventMember eventMember = new EventMember();
                eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
                eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));

                if (eventMember.getUserId() != null) {
                    User user = this.cenesUserManagerImpl.fetchCenesUserByUserId(eventMember.getUserId());
                    eventMember.setUser(user);
                }

                if (eventMember.getUserContactId() != null && !eventMember.getUserContactId().equals(0)) {
                    UserContact userContact = this.userContactManagerImpl.fetchUserContactByUserContactId(eventMember.getUserContactId());
                    eventMember.setUserContact(userContact);
                }
                eventMembers.add(eventMember);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventMembers;
    }

    public List<EventMember> fetchEventMembersByEventAtScreen(String displayAtScreen) {
        this.db = cenesDatabase.getReadableDatabase();
        List<EventMember> eventMembers = new ArrayList<>();
        try {
            String query = "select * from event_members where event_id in (select event_id from events where display_at_screen = '"+displayAtScreen+"' )";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {

                EventMember eventMember = new EventMember();
                eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
                eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));

                if (eventMember.getUserId() != null) {
                    User user = this.cenesUserManagerImpl.fetchCenesUserByUserId(eventMember.getUserId());
                    eventMember.setUser(user);
                }

                if (eventMember.getUserContactId() != null && !eventMember.getUserContactId().equals(0)) {
                    UserContact userContact = this.userContactManagerImpl.fetchUserContactByUserContactId(eventMember.getUserContactId());
                    eventMember.setUserContact(userContact);
                }

                eventMembers.add(eventMember);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventMembers;
    }

    public void deleteFromEventMembersByEventIdsIn(String eventIds) {
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String deleteQuery = "delete from event_members where event_id in ("+eventIds+") ";
            System.out.println("Delete Query : "+deleteQuery);
            db.execSQL(deleteQuery);
            db.close();
            cenesUserManagerImpl.deleteAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteAllFromEventMembers() {
        try {
            this.db = cenesDatabase.getReadableDatabase();
            String deleteQuery = "delete from event_members";
            db.execSQL(deleteQuery);
            db.close();
            cenesUserManagerImpl.deleteAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
