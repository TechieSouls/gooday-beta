package com.cenes.api;

/**
 * Created by mandeep on 5/1/19.
 */

public class NotificationAPI {
    //GET
    public static String get_notificationCounts = "/api/notification/unreadbyuser";//userId
    public static String get_notification_by_user = "/api/notification/byuser";//userId
    public static String get_mark_notification_as_read = "/api/notification/markReadByUserIdAndNotifyId"; //userId
}
