package com.cenes.bo;

/**
 * Created by mandeep on 9/10/17.
 */

public class Notification {

    private Long senderId;
    private String senderName;
    private String message;
    private String title;
    private Long notificationTime;
    private String senderImage;
    private String type;
    private String notificationTypeStatus;
    private Long notificationTypeId;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getNotificationTypeId() {
        return notificationTypeId;
    }

    public void setNotificationTypeId(Long notificationTypeId) {
        this.notificationTypeId = notificationTypeId;
    }

    public String getNotificationTypeStatus() {
        return notificationTypeStatus;
    }

    public void setNotificationTypeStatus(String notificationTypeStatus) {
        this.notificationTypeStatus = notificationTypeStatus;
    }
}
