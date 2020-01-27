package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagerImpl {

    CenesApplication cenesApplication;
    CenesDatabase cenesDatabase;
    SQLiteDatabase db;
    EventManagerImpl eventManagerImpl;
    CenesUserManagerImpl userManagerImpl;

    public static String CreateTableQuery = "CREATE TABLE IF NOT EXISTS notifications (" +
            "notification_id LONG, " +
            "sender_id LONG, " +
            "sender_name TEXT, " +
            "message TEXT," +
            "title TEXT, " +
            "created_at LONG, " +
            "sender_image TEXT, " +
            "type TEXT, " +
            "notification_type_status TEXT, " +
            "notification_type_id LONG, " +
            "read_status TEXT)";

    public NotificationManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        cenesDatabase = new CenesDatabase(cenesApplication);
        this.db = cenesDatabase.getReadableDatabase();
        this.eventManagerImpl = new EventManagerImpl(this.cenesApplication);
        this.userManagerImpl = new CenesUserManagerImpl(this.cenesApplication);
    }

    public void addNotification(List<Notification> notifications) {

        /*for (Notification notification: notifications) {
            saveNotification(notification);
            if (notification.getEvent() != null) {

                if (!this.eventManagerImpl.isEventExist(notification.getEvent())) {

                    this.eventManagerImpl.addEvent(notification.getEvent());
                }
            }
        }*/
    }
    public void saveNotification(Notification notification){

        this.db = cenesDatabase.getReadableDatabase();


        String title = "";
        if (!CenesUtils.isEmpty(notification.getTitle())) {
            title = notification.getTitle().replaceAll("'","''");
        }

        String message = "";
        if (!CenesUtils.isEmpty(notification.getMessage())) {
            message = notification.getMessage().replaceAll("'","''");
        }

        String senderImage = "";
        if (!CenesUtils.isEmpty(notification.getSenderImage())) {
            senderImage = notification.getMessage().replaceAll("'","''");
        }

        String senderName = "";
        if (!CenesUtils.isEmpty(notification.getSenderName())) {
            senderName = notification.getSenderName().replaceAll("'","''");
        }

        String insertQuery = "insert into notifications values("+notification.getNotificationId()+", "+notification.getSenderId()+", '"+senderName+"'," +
                " '"+message+"', '"+title+"', "+notification.getNotificationTime()+", '"+senderImage+"', " +
                "'"+notification.getType()+"', '"+notification.getNotificationTypeStatus()+"', "+notification.getNotificationTypeId()+", '"+notification.getReadStatus()+"')";

        System.out.println(insertQuery);
        db.execSQL(insertQuery);

        User user = this.userManagerImpl.fetchCenesUserByUserId(notification.getUser().getUserId());
        if (user == null) {
            this.userManagerImpl.addUser(notification.getUser());
        } else {
            this.userManagerImpl.updateCenesUser(notification.getUser());
        }
        db.close();
    }

    public List<Notification> fetchAllNotifications() {
        this.db = cenesDatabase.getReadableDatabase();

        List<Notification> notifications = new ArrayList<>();

        String query = "select * from notifications order by created_at desc";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            Notification notification = new Notification();
            notification.setNotificationId(cursor.getLong(cursor.getColumnIndex("notification_id")));
            notification.setSenderId(cursor.getLong(cursor.getColumnIndex("sender_id")));
            notification.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
            notification.setMessage(cursor.getString(cursor.getColumnIndex("message")));
            notification.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            notification.setNotificationTime(cursor.getLong(cursor.getColumnIndex("created_at")));
            notification.setSenderImage(cursor.getString(cursor.getColumnIndex("sender_image")));
            notification.setType(cursor.getString(cursor.getColumnIndex("type")));
            notification.setNotificationTypeStatus(cursor.getString(cursor.getColumnIndex("notification_type_status")));
            notification.setNotificationTypeId(cursor.getLong(cursor.getColumnIndex("notification_type_id")));
            notification.setReadStatus(cursor.getString(cursor.getColumnIndex("read_status")));


            notification.setEvent(eventManagerImpl.findEventByEventId(notification.getNotificationTypeId()));

            notification.setUser(this.userManagerImpl.fetchCenesUserByUserId(Integer.parseInt(notification.getSenderId().toString())));
            notifications.add(notification);
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public boolean isNotificationExist(Notification notification) {
        this.db = cenesDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from notifications where notification_id = "+notification.getNotificationId()+" ", null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void updateNotificationReadStatus(Notification notification) {
        this.db = cenesDatabase.getReadableDatabase();
        db.execSQL("update notifications set read_status = 'Read' where notification_id = "+notification.getNotificationId()+" ");
        db.close();
    }
    public void deleteAllNotifications() {
        this.db = cenesDatabase.getReadableDatabase();

        String deleteQuery = "delete from notifications";
        db.execSQL(deleteQuery);
        db.close();
    }
}
