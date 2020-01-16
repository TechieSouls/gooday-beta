package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class EventManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;

    public static String createTableQuery = "CREATE TABLE events (event_id LONG, " +
            "title TEXT, " +
            "description TEXT, " +
            "start_time LONG, " +
            "end_time LONG, " +
            "photo TEXT," +
            "schedule_as TEXT," +
            "created_by_id INTEGER," +
            "location TEXT," +
            "latitude TEXT," +
            "longitude TEXT," +
            "source TEXT," +
            "display_at_screen TEXT," +
            "key TEXT)";

    public EventManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
    }

    public void addEvent(List<Event> events, String displayAtScreen) {

        for (Event event: events) {
            event.setDisplayAtScreen(displayAtScreen);
            addEvent(event);
        }
    }
    public void addEvent(Event event){
        this.db = cenesDatabase.getReadableDatabase();

        try {
            String description = "";
            if (!CenesUtils.isEmpty(event.getDescription())) {
                description = event.getDescription().replaceAll("'","''");
            }

            String location = "";
            if (!CenesUtils.isEmpty(event.getLocation())) {
                location = event.getLocation().replaceAll("'","''");
            }
            String insertQuery = "insert into events values("+event.getEventId()+", '"+event.getTitle().replaceAll("'","''")+"', '"+description+"'," +
                    " "+event.getStartTime()+", "+event.getEndTime()+", '"+event.getEventPicture()+"', '"+event.getScheduleAs()+"', " +
                    ""+event.getCreatedById()+", '"+location+"', '"+event.getLatitude()+"', '"+event.getLongitude()+"', " +
                    "'"+event.getSource()+"', '"+event.getDisplayAtScreen()+"', '"+event.getKey()+"')";

            System.out.println(insertQuery);
            db.execSQL(insertQuery);
            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.addEventMember(event.getEventMembers());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<Event> fetchAllEventsByScreen(String displayAtScreen) {
        this.db = cenesDatabase.getReadableDatabase();

        List<Event> events = new ArrayList<>();

        try {
            String query = "select * from events where display_at_screen = '"+displayAtScreen+"' ";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = new Event();
                event.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                event.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
                event.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
                event.setEventPicture(cursor.getString(cursor.getColumnIndex("photo")));
                event.setScheduleAs(cursor.getString(cursor.getColumnIndex("schedule_as")));
                event.setCreatedById(cursor.getInt(cursor.getColumnIndex("created_by_id")));
                event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                event.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                event.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
                event.setSource(cursor.getString(cursor.getColumnIndex("source")));
                event.setKey(cursor.getString(cursor.getColumnIndex("key")));
                event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

                EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
                List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventId(event.getEventId());
                //List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(displayAtScreen);
                event.setEventMembers(eventMembers);
                events.add(event);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return events;
    }

    public Event findEventByEventId(Long eventId) {
        this.db = cenesDatabase.getReadableDatabase();
        Event event = null;

        try {
            String query = "select * from events where event_id = "+eventId+"";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                event = new Event();
                event.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                event.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                event.setStartTime(cursor.getLong(cursor.getColumnIndex("start_time")));
                event.setEndTime(cursor.getLong(cursor.getColumnIndex("end_time")));
                event.setEventPicture(cursor.getString(cursor.getColumnIndex("photo")));
                event.setScheduleAs(cursor.getString(cursor.getColumnIndex("schedule_as")));
                event.setCreatedById(cursor.getInt(cursor.getColumnIndex("created_by_id")));
                event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                event.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                event.setLongitude(cursor.getString(cursor.getColumnIndex("longitude")));
                event.setSource(cursor.getString(cursor.getColumnIndex("source")));
                event.setKey(cursor.getString(cursor.getColumnIndex("key")));
                event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

                EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
                List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventId(event.getEventId());
                //List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(Event.EventDisplayScreen.HOME.toString());

                event.setEventMembers(eventMembers);

                cursor.close();
                db.close();
                return event;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return event;
    }

    public boolean isEventExist(Event event){
        try {
            this.db = cenesDatabase.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from events where event_id = "+event.getEventId()+" ", null);
            if (cursor.moveToNext()) {
                cursor.close();
                db.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void deleteAllEventsByDisplayAtScreen(String dispayAtScreen) {

        try {
            this.db = cenesDatabase.getReadableDatabase();

            String eventIds = "";
            String query = "select * from events where display_at_screen = '"+dispayAtScreen+"' ";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                eventIds = eventIds + cursor.getLong(cursor.getColumnIndex("event_id"))+ ",";

            }
            cursor.close();

            String deleteQuery = "delete from events where display_at_screen = '"+dispayAtScreen+"'";
            db.execSQL(deleteQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            if (eventIds.length() == 0) {
                eventMemberManagerImpl.deleteAllFromEventMembers();
            } else {
                eventMemberManagerImpl.deleteFromEventMembersByEventIdsIn(eventIds.substring(0, eventIds.length() - 1));
            }
            db.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void deleteAllEvents() {

        try {
            this.db = cenesDatabase.getReadableDatabase();

            String deleteQuery = "delete from events";
            db.execSQL(deleteQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteAllFromEventMembers();

            db.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
