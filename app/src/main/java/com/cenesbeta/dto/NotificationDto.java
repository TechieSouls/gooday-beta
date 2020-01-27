package com.cenesbeta.dto;

import com.cenesbeta.bo.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationDto {

    private int pageNumber;
    private int totalNotificationCounts;
    private List<Notification> newNotifications;
    private List<Notification> seenNotifications;
    private List<Notification> allNotifications;
    private boolean madeApiCall;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalNotificationCounts() {
        return totalNotificationCounts;
    }

    public void setTotalNotificationCounts(int totalNotificationCounts) {
        this.totalNotificationCounts = totalNotificationCounts;
    }

    public List<Notification> getNewNotifications() {
        return newNotifications;
    }

    public void setNewNotifications(List<Notification> newNotifications) {
        this.newNotifications = newNotifications;
    }

    public List<Notification> getSeenNotifications() {
        return seenNotifications;
    }

    public void setSeenNotifications(List<Notification> seenNotifications) {
        this.seenNotifications = seenNotifications;
    }

    public List<Notification> getAllNotifications() {
        return allNotifications;
    }

    public void setAllNotifications(List<Notification> allNotifications) {
        this.allNotifications = allNotifications;
    }

    public boolean isMadeApiCall() {
        return madeApiCall;
    }

    public void setMadeApiCall(boolean madeApiCall) {
        this.madeApiCall = madeApiCall;
    }
}
