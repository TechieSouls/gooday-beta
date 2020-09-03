package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.User;
import com.cenesbeta.bo.UserContact;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class EventMemberManagerImpl  {

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
            "display_screen_at TEXT," +
            "user_contact_id INTEGER)";

    public EventMemberManagerImpl(CenesApplication cenesApplication){
        //this.cenesApplication = cenesApplication;
        //cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
        this.cenesUserManagerImpl = new CenesUserManagerImpl(cenesApplication);
        this.userContactManagerImpl = new UserContactManagerImpl(cenesApplication);
    }

    public void addEventMember(List<EventMember> eventMembers, String displayAtScreen) {
        try {
            for (EventMember eventMember: eventMembers) {
                eventMember.setDisplayScreenAt(displayAtScreen);
                addEventMember(eventMember);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in : addEventMember (List)");

        }
    }
    public void addEventMember(EventMember eventMember){

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
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
                    "'"+eventMember.getCenesMember()+"', '"+eventMember.getDisplayScreenAt()+"', "+eventMember.getUserContactId()+")";
            System.out.println("Add Member Query : "+insertQuery);

            String insertQuery1 = "insert into event_members(event_member_id, event_id, name, picture, status, " +
                    "user_id, phone, cenes_member, display_screen_at, user_contact_id) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement stmt = CenesBaseActivity.sqlLiteDatabase.compileStatement(insertQuery1);
            stmt.bindLong(1, eventMember.getEventMemberId());
            stmt.bindLong(2, eventMember.getEventId());
            stmt.bindString(3, !CenesUtils.isEmpty(eventMember.getName()) ? eventMember.getName() : "Guest");
            stmt.bindString(4, eventMember.getPicture());
            stmt.bindString(5, !CenesUtils.isEmpty(eventMember.getStatus()) ? eventMember.getStatus() : "");
            stmt.bindLong(6, eventMember.getUserId() != null ? eventMember.getUserId() : 0);
            stmt.bindString(7, !CenesUtils.isEmpty(eventMember.getPhone()) ? eventMember.getPhone(): "");
            stmt.bindString(8, !CenesUtils.isEmpty(eventMember.getCenesMember())? eventMember.getCenesMember(): "");
            stmt.bindString(9, !CenesUtils.isEmpty(eventMember.getDisplayScreenAt())? eventMember.getDisplayScreenAt(): "");
            stmt.bindLong(10, eventMember.getUserContactId() != null ? eventMember.getUserContactId() : 0);
            long entryID = stmt.executeInsert();
            stmt.clearBindings();

            //Lets Add User Now
            if (eventMember.getUser() != null) {
                cenesUserManagerImpl.addUser(eventMember.getUser());
            }
            if (eventMember.getUserContact() != null) {
                userContactManagerImpl.addUserContact(eventMember.getUserContact());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : addEventMember");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }

    }

    public List<EventMember> fetchEventMembersByEventId(Long eventId) {
        List<EventMember> eventMembers = new ArrayList<>();

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select * from event_members where event_id = "+eventId;
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            List<Integer> userIdTracking = new ArrayList<>();
            List<Long> eventMemberIdList = new ArrayList<>();

            while (cursor.moveToNext()) {

                EventMember eventMember = populateEventMember(cursor);

                if (eventMemberIdList.contains(eventMember.getEventMemberId())) {
                    continue;
                } else {
                    eventMemberIdList.add(eventMember.getEventMemberId());
                }

                if (eventMember.getUserId() != null && userIdTracking.contains(eventMember.getUserId()) ) {
                    continue;
                } else {
                    if (eventMember.getUserId() != null) {
                        userIdTracking.add(eventMember.getUserId());
                    }
                }

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : fetchEventMembersByEventId");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return eventMembers;
    }

    public List<EventMember> fetchEventMembersByEventIdAndDisplayAtScreen(Long eventId, String displayAtScreen) {
        List<EventMember> eventMembers = new ArrayList<>();

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select * from event_members where event_id = "+eventId+" and display_screen_at = '"+displayAtScreen+"' ";
            System.out.printf("EventMember Query : "+query);
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);
            System.out.println("Results counts : "+cursor.getCount());

            List<Integer> userIdTracking = new ArrayList<>();
            List<Long> eventMemberIdList = new ArrayList<>();


            while (cursor.moveToNext()) {

                EventMember eventMember = populateEventMember(cursor);

                if (eventMemberIdList.contains(eventMember.getEventMemberId())) {
                    continue;
                } else {
                    eventMemberIdList.add(eventMember.getEventMemberId());
                }

                if (eventMember.getUserId() != null && userIdTracking.contains(eventMember.getUserId()) ) {
                    continue;
                } else {
                    if (eventMember.getUserId() != null) {
                        userIdTracking.add(eventMember.getUserId());
                    }
                }

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : fetchEventMembersByEventIdAndDisplayAtScreen");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
        return eventMembers;
    }

    public void deleteFromEventMembersByEventIdsIn(String eventIds) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from event_members where event_id in ("+eventIds+") ";
            System.out.println("Delete Query : "+deleteQuery);
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);
            cenesUserManagerImpl.deleteAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : deleteFromEventMembersByEventIdsIn");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void deleteFromEventMembersByEventId(Integer eventId) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from event_members where event_id = "+eventId+" ";
            System.out.println("Delete Query : "+deleteQuery);
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : deleteFromEventMembersByEventId");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }


    public void deleteAllFromEventMembers() {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from event_members";
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);
            //cenesUserManagerImpl.deleteAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : deleteAllFromEventMembers");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void updateEventMemberStatus(Integer eventId, Integer userId, String eventMemberStatus) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String updateQuery = "update event_members set status = '"+eventMemberStatus+"' where user_id = "+userId+" and event_id = "+eventId+" ";
            CenesBaseActivity.sqlLiteDatabase.execSQL(updateQuery);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in : updateEventMemberStatus");

        } finally {
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public EventMember populateEventMember(Cursor cursor) {

        EventMember eventMember = new EventMember();
        eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
        eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
        eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
        if (cursor.getInt(cursor.getColumnIndex("user_id")) == 0) {
            eventMember.setUserId(null);
        } else {
            eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
        }
        if (cursor.getInt(cursor.getColumnIndex("user_contact_id")) == 0) {
            eventMember.setUserContactId(null);
        } else {
            eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
        }

        eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
        eventMember.setStatus(cursor.getString(cursor.getColumnIndex("status")));
        eventMember.setDisplayScreenAt(cursor.getString(cursor.getColumnIndex("display_screen_at")));
        return eventMember;

    }
}
