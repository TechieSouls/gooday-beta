package com.cenesbeta.api;

/**
 * Created by mandeep on 5/1/19.
 */

public class NotificationAPI {
    //GET
    public static String get_notification_by_user = "/api/notification/byuser";//userId
    public static String get_mark_notification_as_read = "/api/notification/markReadByUserIdAndNotifyId"; //userId
    public static String get_pageable_notifications = "/api/notification/byuserpageable";
    public static String get_notification_counts = "/api/notification/counts";
    public static String get_notification_badgeCounts = "/api/notification/getBadgeCounts";
    public static String get_set_badge_counts_to_zero = "/api/notification/setBadgeCountsToZero";
}
