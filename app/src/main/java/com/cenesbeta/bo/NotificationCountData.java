package com.cenesbeta.bo;

public class NotificationCountData {

    private Long notificationCountDataId;

    private int badgeCount;

    private Long userId;

    public Long getNotificationCountDataId() {
        return notificationCountDataId;
    }

    public void setNotificationCountDataId(Long notificationCountDataId) {
        this.notificationCountDataId = notificationCountDataId;
    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}