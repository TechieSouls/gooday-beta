package com.cenesbeta.database.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cenesbeta.activity.CenesBaseActivity;
import com.cenesbeta.application.CenesApplication;
import com.cenesbeta.bo.Notification;
import com.cenesbeta.bo.User;
import com.cenesbeta.database.CenesDatabase;
import com.cenesbeta.util.CenesUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagerImpl {

    CenesApplication cenesApplication;
    //CenesDatabase cenesDatabase;
    //SQLiteDatabase db;
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
            "action TEXT, " +
            "notification_type_status TEXT, " +
            "notification_type_id LONG, " +
            "read_status TEXT)";

    public NotificationManagerImpl(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        //cenesDatabase = new CenesDatabase(cenesApplication);
        //this.db = cenesDatabase.getReadableDatabase();
        this.eventManagerImpl = new EventManagerImpl(this.cenesApplication);
        this.userManagerImpl = new CenesUserManagerImpl(this.cenesApplication);
    }

    public void addNotification(List<Notification> notifications) {

        for (Notification notification: notifications) {
            saveNotification(notification);
            if (notification.getEvent() != null) {
                if (!this.eventManagerImpl.isEventExist(notification.getEvent())) {
                    this.eventManagerImpl.addEvent(notification.getEvent());
                }
            }
        }
    }

    public void saveNotification(Notification notification){

        if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
            CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
        }


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

        String action = "";
        if (!CenesUtils.isEmpty(notification.getAction())) {
            action = notification.getAction();
        }


        String insertQuery = "insert into notifications values("+notification.getNotificationId()+", "+notification.getSenderId()+", '"+senderName+"'," +
                " '"+message+"', '"+title+"', "+notification.getNotificationTime()+", '"+senderImage+"', " +
                "'"+notification.getType()+"', '"+action+"', '"+notification.getNotificationTypeStatus()+"', "+notification.getNotificationTypeId()+", '"+notification.getReadStatus()+"')";

        System.out.println(insertQuery);
        //CenesBaseActivity.sqlLiteDatabase.execSQL(insertQuery);
        String insertQuery1 = "insert into notifications(notification_id, sender_id, sender_name, message, " +
                "title, created_at, sender_image, type, action, notification_type_status, " +
                "notification_type_id, read_status) values(?,?,?,?,?,?,?,?,?,?,?,?)";

        SQLiteStatement stmt = CenesBaseActivity.sqlLiteDatabase.compileStatement(insertQuery1);
        stmt.bindLong(1, notification.getNotificationId());
        stmt.bindLong(2, notification.getSenderId());
        stmt.bindString(3, senderName);
        stmt.bindString(4, message);
        stmt.bindString(5, title);
        stmt.bindLong(6, notification.getNotificationTime());
        stmt.bindString(7, senderImage);
        stmt.bindString(8, notification.getType());
        stmt.bindString(9, action);
        stmt.bindString(10, notification.getNotificationTypeStatus());
        stmt.bindLong(11, notification.getNotificationTypeId());
        stmt.bindString(12, notification.getReadStatus());
        long entryID = stmt.executeInsert();
        stmt.clearBindings();

        User user = this.userManagerImpl.fetchCenesUserByUserId(notification.getUser().getUserId());
        if (user == null) {
            this.userManagerImpl.addUser(notification.getUser());
        } else {
            this.userManagerImpl.updateCenesUser(notification.getUser());
        }
        //CenesBaseActivity.sqlLiteDatabase.close();
    }

    public List<Notification> fetchAllNotifications() {
        if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
            CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
        }
        List<Notification> notifications = new ArrayList<>();

        String query = "select * from notifications order by created_at desc";
        Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery(query, null);

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
            notification.setAction(cursor.getString(cursor.getColumnIndex("action")));
            notification.setNotificationTypeStatus(cursor.getString(cursor.getColumnIndex("notification_type_status")));
            notification.setNotificationTypeId(cursor.getLong(cursor.getColumnIndex("notification_type_id")));
            notification.setReadStatus(cursor.getString(cursor.getColumnIndex("read_status")));

            notification.setEvent(eventManagerImpl.findEventByEventId(notification.getNotificationTypeId()));

            notification.setUser(this.userManagerImpl.fetchCenesUserByUserId(Integer.parseInt(notification.getSenderId().toString())));
            notifications.add(notification);
        }
        cursor.close();
        //CenesBaseActivity.sqlLiteDatabase.close();
        return notifications;
    }

    public boolean isNotificationExist(Notification notification) {
        if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
            CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
        }
        Cursor cursor = CenesBaseActivity.sqlLiteDatabase.rawQuery("select * from notifications where notification_id = "+notification.getNotificationId()+" ", null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public void updateNotificationReadStatus(Notification notification) {
        if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
            CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
        }
        CenesBaseActivity.sqlLiteDatabase.execSQL("update notifications set read_status = 'Read' where notification_id = "+notification.getNotificationId()+" ");
        //CenesBaseActivity.sqlLiteDatabase.close();
    }
    public void deleteAllNotifications() {
        if (!CenesBaseActivity.sqlLiteDatabase.isOpen()) {
            CenesBaseActivity.sqlLiteDatabase = CenesBaseActivity.cenesDatabase.getReadableDatabase();
        }
        String deleteQuery = "delete from notifications";
        CenesBaseActivity.sqlLiteDatabase.delete("notifications", null, null);
        //CenesBaseActivity.sqlLiteDatabase.close();
    }
}
