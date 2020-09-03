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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createTableQuery = "CREATE TABLE events (event_id LONG, " +
            "title TEXT, " +
            "description TEXT, " +
            "start_time LONG, " +
            "end_time LONG, " +
            "thumbnail TEXT," +
            "photo TEXT," +
            "schedule_as TEXT," +
            "created_by_id INTEGER," +
            "location TEXT," +
            "latitude TEXT," +
            "longitude TEXT," +
            "source TEXT," +
            "expired INTEGER," +
            "synced INTEGER," +
            "recurring_event_id TEXT," +
            "display_at_screen TEXT," +
            "key TEXT)";

    public EventManagerImpl(CenesApplication cenesApplication){
        //this.cenesApplication = cenesApplication;
        //cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
    }

    public void addEvent(List<Event> events, String displayAtScreen) {

        for (Event event: events) {
            event.setDisplayAtScreen(displayAtScreen);

            boolean dbEvent = isEventExist(event);
            if (!dbEvent) {
                addEvent(event);
            }
        }
    }
    public void addEvent(Event event){

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getWriteableDatabase();
            }

            String description = "";
            if (!CenesUtils.isEmpty(event.getDescription())) {
                description = event.getDescription().replaceAll("'","''");
            }

            String location = "";
            if (!CenesUtils.isEmpty(event.getLocation())) {
                location = event.getLocation().replaceAll("'","''");
            }

            String recurringEventId = "";
            if (!CenesUtils.isEmpty(event.getRecurringEventId())) {
                recurringEventId = event.getRecurringEventId().replaceAll("'","''");
            }

            String thumbnail = "";
            if (!CenesUtils.isEmpty(event.getThumbnail())) {
                thumbnail = event.getThumbnail();
            }

            String eventPicture = "";
            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                eventPicture = event.getEventPicture();
            }

            String latitude = "";
            if (!CenesUtils.isEmpty(event.getLatitude())) {
                latitude = event.getLatitude();
            }

            String longitude = "";
            if (!CenesUtils.isEmpty(event.getLongitude())) {
                longitude = event.getLongitude();
            }

            String key = "";
            if (!CenesUtils.isEmpty(event.getKey())) {
                key = event.getKey();
            }

            String title = event.getTitle().replaceAll("'","''");

            int expired = event.getExpired() ? 1 : 0;

            int isSynced = event.isSynced() ? 1 : 0;

            //String insertQuery = "insert into events values ("+event.getEventId()+", '"+event.getTitle().replaceAll("'","''")+"', '"+description+"'," +
            //        " "+event.getStartTime()+", "+event.getEndTime()+", '"+event.getThumbnail()+"', '"+event.getEventPicture()+"', '"+event.getScheduleAs()+"', " +
            //        ""+event.getCreatedById()+", '"+location+"', '"+event.getLatitude()+"', '"+event.getLongitude()+"', " +
            //        "'"+event.getSource()+"', "+expired+", "+isSynced+",'"+recurringEventId+"', '"+event.getDisplayAtScreen()+"', '"+event.getKey()+"')";

            //System.out.println(insertQuery);
            //db.execSQL(insertQuery);

            String insertQuery2 = "insert into events (event_id, title, description, start_time, end_time, thumbnail, photo, " +
                    "schedule_as, created_by_id, location, latitude, longitude, source, expired, synced, recurring_event_id, " +
                    "display_at_screen, key) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            //cenesDatabase.getWriteableDatabase();
            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            SQLiteStatement stmt = CenesBaseActivity.sqlLiteDatabase.compileStatement(insertQuery2);
            stmt.bindLong(1, event.getEventId());
            stmt.bindString(2, title);
            stmt.bindString(3, description);
            stmt.bindLong(4, event.getStartTime());
            stmt.bindLong(5, event.getEndTime());
            stmt.bindString(6, thumbnail);
            stmt.bindString(7, eventPicture);
            stmt.bindString(8, event.getScheduleAs());
            stmt.bindLong(9, event.getCreatedById());
            stmt.bindString(10, location);
            stmt.bindString(11, latitude);
            stmt.bindString(12, longitude);
            stmt.bindString(13, event.getSource());
            stmt.bindLong(14, expired);
            stmt.bindLong(15, isSynced);
            stmt.bindString(16, recurringEventId);
            stmt.bindString(17, event.getDisplayAtScreen());
            stmt.bindString(18, key);
            long entryID = stmt.executeInsert();

            stmt.clearBindings();


            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.addEventMember(event.getEventMembers(), event.getDisplayAtScreen());

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public List<Event> fetchAllEventsByScreen(String displayAtScreen) {
        List<Event> events = new ArrayList<>();

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String query = "select * from events where display_at_screen = ? ";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[] {displayAtScreen });

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();
        }

        return events;
    }

    public List<Event> fetchHomeScreenFutureEvents(Integer goingMemberId) {

        List<Event> events = new ArrayList<>();

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= "+new Date().getTime()+" and " +
                        "em.user_id = "+goingMemberId+" and em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";
            System.out.println("Future Events Query : "+query);

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            Long startDate = new Date().getTime();
            String query1 = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= ? and " +
                    "em.user_id = ? and em.status = ?) as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";

            String[] args = new String[] {startDate+"" ,goingMemberId+"", EventMember.EventMemberAttendingStatus.Going.toString()};
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query1, args);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }

        return events;
    }

    public List<Event> fetchHomeScreenPastEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time < "+new Date().getTime()+" and " +
                    "em.user_id = "+goingMemberId+" and em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";
            System.out.println("Past Events Query : "+query);

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            Long startDate = new Date().getTime();
            String query1 = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time < ? and " +
                    "em.user_id = ? and em.status = ?) as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";
            String[] args = new String[] {startDate+"" ,goingMemberId+"", EventMember.EventMemberAttendingStatus.Going.toString()};
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query1, args);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }

        return events;
    }

    public List<Event> fetchAcceptedTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String query = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    " uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= "+new Date().getTime()+" and " +
                    "em.user_id = "+goingMemberId+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' " +
                    "and em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";
            System.out.println("Accepted Events Query : "+query);

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            Long startDate = new Date().getTime();
            String query1 = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    " uc.user_id as uc_user_id  from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= ? and " +
                    "em.user_id = ? and e.schedule_as = ? " +
                    "and em.status = ?) as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";

            String[] args = new String[] {startDate+"" ,goingMemberId+"", Event.EventScheduleAs.Gathering.toString(), EventMember.EventMemberAttendingStatus.Going.toString()};
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query1, args);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }

    public List<Event> fetchPendingTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            String query = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= "+new Date().getTime()+" and " +
                    "em.user_id = "+goingMemberId+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' " +
                    "and em.status is null or em.status = '') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";
            System.out.println("Pending Events Query : "+query);

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            Long startDate = new Date().getTime();
            String query1 = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= ? and " +
                    "em.user_id = ? and e.schedule_as = ? " +
                    "and em.status is null or em.status = ?) as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id";

            String[] args = new String[] {startDate+"" ,goingMemberId+"", Event.EventScheduleAs.Gathering.toString(), ""};
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query1, args);

            events = populateEventAndEventMembers(cursor);
            cursor.close();

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }

    public List<Event> fetchDeclinedTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            String query = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= "+new Date().getTime()+" and " +
                    "em.user_id = "+goingMemberId+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' " +
                    "and em.status = '"+EventMember.EventMemberAttendingStatus.NotGoing.toString()+"') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id " +
                    "and uc.user_id = "+goingMemberId+"";

            System.out.println("Declined Events Query : "+query);


            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            String query1 = "select *, event_temp.photo as event_picture, em.user_id as em_user_id, cu.user_id as cu_user_id, em.name as em_username, " +
                    "cu.name as username, cu.picture as user_photo, uc.name as phonebookName, " +
                    "uc.user_id as uc_user_id from (select * from events e " +
                    "JOIN event_members em on e.event_id = em.event_id where e.start_time >= "+new Date().getTime()+" and " +
                    "em.user_id = "+goingMemberId+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' " +
                    "and em.status = '"+EventMember.EventMemberAttendingStatus.NotGoing.toString()+"') as event_temp " +
                    "JOIN event_members em on event_temp.event_id = em.event_id  " +
                    "LEFT JOIN cenes_users cu on em.user_id = cu.user_id " +
                    "LEFT JOIN user_contacts uc on em.user_contact_id = uc.user_contact_id " +
                    "and uc.user_id = "+goingMemberId+"";

            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query1, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }


    public Event findEventByEventId(Long eventId) {
        Event event = null;

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String query = "select * from events where event_id = ?";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[]{eventId+""});
            if (cursor.moveToFirst()) {
                event = populateEventObject(cursor);
                cursor.close();
            }

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }

        return event;
    }

    public List<Event> findAllEventsByEventId(Long eventId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String query = "select * from events where event_id = ?";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[]{eventId+""});

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }

    public List<Event> findAllOfflineEvents() {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String query = "select * from events where synced = ?";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[]{"0"});

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }
    public List<Event> findAllEventsByRecurringEventId(String recurringEventId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String query = "select * from events where recurring_event_id = ?";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[]{recurringEventId});

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }

    public List<Event> findAllEventsByTitleAndSource(String title, String source) {
        List<Event> events = new ArrayList<>();
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                //CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            String query = "select * from events where title like ? and source = ? ";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, new String[]{title, source});

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return events;
    }

    public Event populateEventObject(Cursor cursor) {
        Event event = new Event();
        event.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
        event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        event.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
        event.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
        event.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
        event.setEventPicture(cursor.getString(cursor.getColumnIndex("photo")));
        event.setScheduleAs(cursor.getString(cursor.getColumnIndex("schedule_as")));
        event.setCreatedById(cursor.getInt(cursor.getColumnIndex("created_by_id")));
        event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        event.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
        event.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
        event.setSource(cursor.getString(cursor.getColumnIndex("source")));
        event.setExpired(cursor.getString(cursor.getColumnIndex("expired")).equals("1"));
        event.setSynced(cursor.getString(cursor.getColumnIndex("synced")).equals("1"));
        event.setRecurringEventId(cursor.getString(cursor.getColumnIndex("recurring_event_id")));
        event.setKey(cursor.getString(cursor.getColumnIndex("key")));
        event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));


        EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
        List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventIdAndDisplayAtScreen(event.getEventId(), event.getDisplayAtScreen());
        /////List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(Event.EventDisplayScreen.HOME.toString());

        event.setEventMembers(eventMembers);
        return event;
    }

    public List<Event> populateEventAndEventMembers(Cursor cursor) {

        Map<Integer, Event> eventIdMap = new HashMap<>();
        while (cursor.moveToNext()) {

        //for (String columnName: cursor.getColumnNames()) {
            //System.out.println(""+columnName+" : "+cursor.getString(cursor.getColumnIndex(columnName)));
        //  }
            Integer eventId = cursor.getInt(cursor.getColumnIndex("event_id"));

            Event event = null;
            if (eventIdMap.containsKey(eventId)) {
                event = eventIdMap.get(eventId);

                List<EventMember> eventMembers = event.getEventMembers();

                boolean memberExists = false;
                for (EventMember eveMem: eventMembers) {
                    Long eventMemberId = cursor.getLong(cursor.getColumnIndex("event_member_id"));
                    if (eventMemberId.equals(eveMem.getEventMemberId())) {
                        memberExists = true;
                        break;
                    }
                }
                if (!memberExists) {
                    EventMember eventMember = new EventMember();
                    eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                    eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                    eventMember.setName(cursor.getString(cursor.getColumnIndex("em_username")));
                    eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("em_user_id")));
                    eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                    eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                    eventMember.setStatus(cursor.getString(cursor.getColumnIndex("status")));

                    if (cursor.getInt(cursor.getColumnIndex("cu_user_id")) != 0) {
                        User user = new User();
                        user.setName(cursor.getString(cursor.getColumnIndex("username")));
                        user.setPicture(cursor.getString(cursor.getColumnIndex("user_photo")));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex("cu_user_id")));
                        eventMember.setUser(user);
                    }

                    if (cursor.getInt(cursor.getColumnIndex("user_contact_id")) != 0) {
                        try {
                            UserContact userContact = null;
                            if (cursor.getString(cursor.getColumnIndex("phonebookName")) != null) {
                                userContact =  new UserContact();
                                userContact.setName(cursor.getString(cursor.getColumnIndex("phonebookName")));
                                userContact.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                                try {
                                    userContact.setFriendId(cursor.getInt(cursor.getColumnIndex("friend_id")));
                                } catch(Exception e) {

                                }
                                try {
                                    userContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                                } catch(Exception e) {

                                }

                                int cenesMember = cursor.getInt(cursor.getColumnIndex("cenes_member"));
                                if (cenesMember == 1) {
                                    userContact.setCenesMember(UserContact.CenesMember.yes.toString());
                                } else {
                                    userContact.setCenesMember(UserContact.CenesMember.no.toString());
                                }
                            }
                            eventMember.setUserContact(userContact);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                    eventMembers.add(eventMember);
                    event.setEventMembers(eventMembers);
                    eventIdMap.put(eventId, event);
                }

            } else {
                event = new Event();
                event.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                event.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
                event.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
                event.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
                event.setEventPicture(cursor.getString(cursor.getColumnIndex("photo")));
                event.setScheduleAs(cursor.getString(cursor.getColumnIndex("schedule_as")));
                event.setCreatedById(cursor.getInt(cursor.getColumnIndex("created_by_id")));
                event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                event.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                event.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
                event.setSource(cursor.getString(cursor.getColumnIndex("source")));
                event.setExpired(cursor.getString(cursor.getColumnIndex("expired")).equals("1"));
                event.setSynced(cursor.getString(cursor.getColumnIndex("synced")).equals("1"));
                event.setRecurringEventId(cursor.getString(cursor.getColumnIndex("recurring_event_id")));
                event.setKey(cursor.getString(cursor.getColumnIndex("key")));
                event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

                List<EventMember> eventMembers = new ArrayList<>();
                EventMember eventMember = new EventMember();
                eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                eventMember.setName(cursor.getString(cursor.getColumnIndex("em_username")));
                eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("em_user_id")));
                eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                eventMember.setStatus(cursor.getString(cursor.getColumnIndex("status")));

                if (cursor.getInt(cursor.getColumnIndex("cu_user_id")) != 0) {
                    User user = null;
                    if (cursor.getString(cursor.getColumnIndex("username")) != null) {
                        user = new User();
                        user.setName(cursor.getString(cursor.getColumnIndex("username")));
                        user.setPicture(cursor.getString(cursor.getColumnIndex("user_photo")));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex("cu_user_id")));
                    }
                    eventMember.setUser(user);
                }

                if (cursor.getInt(cursor.getColumnIndex("user_contact_id")) != 0) {
                    try {
                        UserContact userContact = null;
                        if (cursor.getString(cursor.getColumnIndex("phonebookName")) != null) {
                            userContact =  new UserContact();
                            userContact.setName(cursor.getString(cursor.getColumnIndex("phonebookName")));
                            userContact.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                            try {
                                userContact.setFriendId(cursor.getInt(cursor.getColumnIndex("friend_id")));
                            } catch(Exception e) {

                            }
                            try {
                                userContact.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                            } catch(Exception e) {

                            }

                            int cenesMember = cursor.getInt(cursor.getColumnIndex("cenes_member"));
                            if (cenesMember == 1) {
                                userContact.setCenesMember(UserContact.CenesMember.yes.toString());
                            } else {
                                userContact.setCenesMember(UserContact.CenesMember.no.toString());
                            }
                        }
                        eventMember.setUserContact(userContact);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
                eventMembers.add(eventMember);
                event.setEventMembers(eventMembers);
                eventIdMap.put(eventId, event);
            }
        }

        List<Event> eventsToReturn = new ArrayList<>(eventIdMap.values());
        Collections.sort(eventsToReturn, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getStartTime() < o2.getStartTime() ? 1 : -1;
            }
        });
        return eventsToReturn;
    }


    public void updateEvent(Event event) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            String description = "";
            if (!CenesUtils.isEmpty(event.getDescription())) {
                description = event.getDescription().replaceAll("'","''");
            }

            String location = "";
            if (!CenesUtils.isEmpty(event.getLocation())) {
                location = event.getLocation().replaceAll("'","''");
            }

            int expired = event.getExpired() ? 1 : 0;

            String updateQuery = "update events set title = '"+event.getTitle().replaceAll("'","''")+"', " +
                    "description = '"+description+"', start_time = "+event.getStartTime()+", end_time = "+event.getEndTime()+", " +
                    "thumbnail = '"+event.getThumbnail()+"', photo = '"+event.getEventPicture()+"', " +
                    "location = '"+location+"', latitude = '"+event.getLatitude()+"', longitude = '"+event.getLongitude()+"', " +
                    "expired = "+expired+", key = '"+event.getKey()+"' where event_id = "+event.getEventId()+"";

            System.out.println(updateQuery);

            String title = event.getTitle().replaceAll("'","''");
            String thumbnail = "";
            if (!CenesUtils.isEmpty(event.getThumbnail())) {
                thumbnail = event.getThumbnail();
            }
            String eventPicture = "";
            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                eventPicture = event.getEventPicture();
            }

            String latitude = "";
            if (!CenesUtils.isEmpty(event.getLatitude())) {
                latitude = event.getLatitude();
            }

            String longitude = "";
            if (!CenesUtils.isEmpty(event.getLongitude())) {
                longitude = event.getLongitude();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            String updateQuery1 = "update events set title = ?, " +
                    "description = '"+description+"', start_time = ?, end_time = ?, " +
                    "thumbnail = ?, photo = ?, " +
                    "location = ?, latitude = ?, longitude = ?, " +
                    "expired = ?, key = ? where event_id = ?";
            CenesBaseActivity.sqlLiteDatabase.execSQL(updateQuery1, new Object[]{title, event.getStartTime(),
                    event.getEndTime(), thumbnail, eventPicture, location, latitude, longitude, expired,
                    event.getKey(), event.getEventId()});

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteFromEventMembersByEventId(Integer.parseInt(event.getEventId().toString()));
            eventMemberManagerImpl.addEventMember(event.getEventMembers(), event.getDisplayAtScreen());
        } catch (Exception e) {
            e.printStackTrace();
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }

    }
    public boolean isEventExist(Event event) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery("select * from events where event_id = "+event.getEventId()+" ", null);
            if (cursor.moveToNext()) {
                cursor.close();
                //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
                return true;
            }

            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();

        }
        return false;
    }

    public void deleteAllEventsByDisplayAtScreen(String dispayAtScreen) {

        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            String eventIds = "";
            String query = "select * from events where display_at_screen = '"+dispayAtScreen+"' ";
            Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

            while (cursor.moveToNext()) {
                eventIds = eventIds + cursor.getLong(cursor.getColumnIndex("event_id"))+ ",";

            }
            cursor.close();

            String deleteQuery = "delete from events where display_at_screen = '"+dispayAtScreen+"'";
            CenesBaseActivity.sqlLiteDatabase.execSQL(deleteQuery);
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            if (eventIds.length() == 0) {
                ////eventMemberManagerImpl.deleteAllFromEventMembers();
            } else {
                eventMemberManagerImpl.deleteFromEventMembersByEventIdsIn(eventIds.substring(0, eventIds.length() - 1));
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void deleteEventByEventId(Long eventId) {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }
            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();

            String deleteQuery = "delete from events where event_id = ?";
            String whereClause = "event_id = ?";
            CenesBaseActivity.sqlLiteDatabase.delete(deleteQuery, whereClause, new String[]{eventId+""});
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteFromEventMembersByEventId(eventId.intValue());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void deleteAllEvents() {
        try {
            if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
                CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
            }

            //CenesBaseActivity.sqlLiteDatabase.beginTransaction();
            //String deleteQuery = "delete from events";
            CenesBaseActivity.sqlLiteDatabase.delete("events", null, null);
            //CenesBaseActivity.sqlLiteDatabase.setTransactionSuccessful();

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteAllFromEventMembers();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //CenesBaseActivity.sqlLiteDatabase.endTransaction();
            //CenesBaseActivity.sqlLiteDatabase.close();
        }
    }

    public void updateByDisplayAtScreenByEventId(Long eventId, Integer userId, String displayAtScreen) {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String updateQuery = "update events set display_at_screen = '"+Event.EventDisplayScreen.DECLINED.toString()+"' where event_id = "+eventId+"";
            db.execSQL(updateQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.updateEventMemberStatus(eventId.intValue(), userId, displayAtScreen);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
