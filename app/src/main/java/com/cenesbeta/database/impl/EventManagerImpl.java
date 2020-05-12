package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Event;
import com.cenesbeta.bo.EventMember;
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

            boolean dbEvent = isEventExist(event);
            if (!dbEvent) {
                addEvent(event);
            }
        }
    }
    public void addEvent(Event event){

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
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

            int expired = event.getExpired() ? 1 : 0;

            int isSynced = event.isSynced() ? 1 : 0;

            String insertQuery = "insert into events values ("+event.getEventId()+", '"+event.getTitle().replaceAll("'","''")+"', '"+description+"'," +
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
        List<Event> events = new ArrayList<>();

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where display_at_screen = '"+displayAtScreen+"' ";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return events;
    }

    public List<Event> fetchHomeScreenFutureEvents(Integer goingMemberId) {

        List<Event> events = new ArrayList<>();

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events e JOIN event_members em on e.event_id = em.event_id where " +
                    "e.start_time >= "+new Date().getTime()+" and em.user_id = "+goingMemberId+" and " +
                    "em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"' ";
            System.out.println("Future Events Query : "+query);
            Cursor cursor = db.rawQuery(query, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return events;
    }

    public List<Event> fetchHomeScreenPastEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events e JOIN event_members em on e.event_id = em.event_id where " +
                    "e.start_time < "+new Date().getTime()+" and em.user_id = "+goingMemberId+" and " +
                    "em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"' ";
            System.out.println("Past Events Query : "+query);
            Cursor cursor = db.rawQuery(query, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return events;
    }

    public List<Event> fetchAcceptedTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events e JOIN event_members em on e.event_id = em.event_id where " +
                    "e.start_time >= "+new Date().getTime()+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' and " +
                    "em.user_id = "+goingMemberId+" and " +
                    "em.status = '"+ EventMember.EventMemberAttendingStatus.Going.toString()+"' ";
            System.out.println("Accepted Events Query : "+query);
            Cursor cursor = db.rawQuery(query, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }

    public List<Event> fetchPendingTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events e JOIN event_members em on e.event_id = em.event_id where " +
                    "e.start_time >= "+new Date().getTime()+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' and " +
                    "em.user_id = "+goingMemberId+" and " +
                    "em.status is null or e.status = '' ";
            System.out.println("Pending Events Query : "+query);
            Cursor cursor = db.rawQuery(query, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }

    public List<Event> fetchDeclinedTabEvents(Integer goingMemberId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events e JOIN event_members em on e.event_id = em.event_id where " +
                    "e.start_time >= "+new Date().getTime()+" and e.schedule_as = '"+ Event.EventScheduleAs.Gathering.toString() +"' and " +
                    "em.user_id = "+goingMemberId+" and " +
                    "em.status = '"+EventMember.EventMemberAttendingStatus.NoGoing.toString()+"' ";
            System.out.println("Declined Events Query : "+query);
            Cursor cursor = db.rawQuery(query, null);

            events = populateEventAndEventMembers(cursor);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }


    public Event findEventByEventId(Long eventId) {
        Event event = null;

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where event_id = "+eventId+"";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                event = populateEventObject(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return event;
    }

    public List<Event> findAllEventsByEventId(Long eventId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where event_id = "+eventId+"";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }

    public List<Event> findAllOfflineEvents() {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where synced = 0";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }
    public List<Event> findAllEventsByRecurringEventId(String recurringEventId) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where recurring_event_id = '"+recurringEventId+"'";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return events;
    }

    public List<Event> findAllEventsByTitleAndSource(String title, String source) {
        List<Event> events = new ArrayList<>();
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String query = "select * from events where title like '"+title+"' and source = '"+source+"' ";
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                Event event = populateEventObject(cursor);
                events.add(event);
            }
            cursor.close();
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

    public List<Event> populateEventAndEventMembers(Cursor cursor) {

        Map<Integer, Event> eventIdMap = new HashMap<>();
        while (cursor.moveToNext()) {

            Integer eventId = cursor.getInt(cursor.getColumnIndex("event_id"));

            Event event = null;
            if (eventIdMap.containsKey(eventId)) {
                event = eventIdMap.get(eventId);

                List<EventMember> eventMembers = event.getEventMembers();
                EventMember eventMember = new EventMember();
                eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
                eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                eventMember.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                eventMembers.add(eventMember);
                event.setEventMembers(eventMembers);
            } else {
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
                event.setExpired(cursor.getString(cursor.getColumnIndex("expired")).equals("1"));
                event.setSynced(cursor.getString(cursor.getColumnIndex("synced")).equals("1"));
                event.setRecurringEventId(cursor.getString(cursor.getColumnIndex("recurring_event_id")));
                event.setKey(cursor.getString(cursor.getColumnIndex("key")));
                event.setDisplayAtScreen(cursor.getString(cursor.getColumnIndex("display_at_screen")));

                List<EventMember> eventMembers = new ArrayList<>();
                EventMember eventMember = new EventMember();
                eventMember.setEventId(cursor.getLong(cursor.getColumnIndex("event_id")));
                eventMember.setEventMemberId(cursor.getLong(cursor.getColumnIndex("event_member_id")));
                eventMember.setName(cursor.getString(cursor.getColumnIndex("name")));
                eventMember.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                eventMember.setUserContactId(cursor.getInt(cursor.getColumnIndex("user_contact_id")));
                eventMember.setPicture(cursor.getString(cursor.getColumnIndex("picture")));
                eventMember.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                eventMembers.add(eventMember);
                event.setEventMembers(eventMembers);
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

    public boolean isEventExist(Event event){
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            Cursor cursor = db.rawQuery("select * from events where event_id = "+event.getEventId()+" ", null);
            if (cursor.moveToNext()) {
                cursor.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.db.close();
        }

        return false;
    }

    public void deleteAllEventsByDisplayAtScreen(String dispayAtScreen) {

        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
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
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteEventByEventId(Long eventId) {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from events where event_id = "+eventId+"";
            db.execSQL(deleteQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteFromEventMembersByEventId(eventId.intValue());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteAllEvents() {
        try {
            if (!this.db.isOpen()) {
                this.db = cenesDatabase.getReadableDatabase();
            }
            String deleteQuery = "delete from events";
            db.execSQL(deleteQuery);

            EventMemberManagerImpl eventMemberManagerImpl = new EventMemberManagerImpl(cenesApplication);
            eventMemberManagerImpl.deleteAllFromEventMembers();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
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
