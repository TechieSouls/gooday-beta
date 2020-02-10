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
            "expired INTEGER," +
            "synced INTEGER," +
            "recurring_event_id TEXT," +
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

            String recurringEventId = "";
            if (!CenesUtils.isEmpty(event.getRecurringEventId())) {
                recurringEventId = event.getRecurringEventId().replaceAll("'","''");
            }

            int expired = event.getExpired() ? 1 : 0;

            int isSynced = event.isSynced() ? 1 : 0;

            String insertQuery = "insert into events values("+event.getEventId()+", '"+event.getTitle().replaceAll("'","''")+"', '"+description+"'," +
                    " "+event.getStartTime()+", "+event.getEndTime()+", '"+event.getEventPicture()+"', '"+event.getScheduleAs()+"', " +
                    ""+event.getCreatedById()+", '"+location+"', '"+event.getLatitude()+"', '"+event.getLongitude()+"', " +
                    "'"+event.getSource()+"', "+expired+", "+isSynced+",'"+recurringEventId+"', '"+event.getDisplayAtScreen()+"', '"+event.getKey()+"')";

            System.out.println(insertQuery);
            db.execSQL(insertQuery);
            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.addEventMember(event.getEventMembers(), event.getDisplayAtScreen());
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
                Event event = populateEventObject(cursor);
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
                event = populateEventObject(cursor);

                cursor.close();
                db.close();
                return event;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return event;
    }

    public List<Event> findAllEventsByEventId(Long eventId) {
        this.db = cenesDatabase.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        try {
            String query = "select * from events where event_id = "+eventId+"";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
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

    public List<Event> findAllOfflineEvents() {
        this.db = cenesDatabase.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        try {

            String query = "select * from events where synced = 0";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
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
    public List<Event> findAllEventsByRecurringEventId(String recurringEventId) {
        this.db = cenesDatabase.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        try {
            String query = "select * from events where recurring_event_id = '"+recurringEventId+"'";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
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

    public List<Event> findAllEventsByTitleAndSource(String title, String source) {
        this.db = cenesDatabase.getReadableDatabase();
        List<Event> events = new ArrayList<>();
        try {
            String query = "select * from events where title like '"+title+"' and source = '"+source+"' ";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
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

    public Event populateEventObject(Cursor cursor) {
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
        event.setExpired(cursor.getString(cursor.getColumnIndex("expired")).equals("1"));
        event.setSynced(cursor.getString(cursor.getColumnIndex("synced")).equals("1"));
        event.setRecurringEventId(cursor.getString(cursor.getColumnIndex("recurring_event_id")));
        event.setKey(cursor.getString(cursor.getColumnIndex("key")));
        event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

        EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
        List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventIdAndDisplayAtScreen(event.getEventId(), event.getDisplayAtScreen());
        //List<EventMember> eventMembers = eventMemberManagerImpl.fetchEventMembersByEventAtScreen(Event.EventDisplayScreen.HOME.toString());

        event.setEventMembers(eventMembers);
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
                //eventMemberManagerImpl.deleteAllFromEventMembers();
            } else {
                eventMemberManagerImpl.deleteFromEventMembersByEventIdsIn(eventIds.substring(0, eventIds.length() - 1));
            }
            db.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteEventByEventId(Long eventId) {
        try {
            this.db = cenesDatabase.getReadableDatabase();

            String deleteQuery = "delete from events where event_id = "+eventId+"";
            db.execSQL(deleteQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteFromEventMembersByEventId(eventId.intValue());
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

    public void updateByDisplayAtScreenByEventId(Long eventId, Integer userId, String displayAtScreen) {
        try {
            this.db = cenesDatabase.getReadableDatabase();

            String updateQuery = "update events set display_at_screen = '"+Event.EventDisplayScreen.DECLINED.toString()+"' where event_id = "+eventId+"";
            db.execSQL(updateQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.updateEventMemberStatus(eventId.intValue(), userId, displayAtScreen);
            db.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
