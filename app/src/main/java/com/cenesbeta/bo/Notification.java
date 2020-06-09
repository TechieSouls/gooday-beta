package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mandeep on 9/10/17.
 */

public class Notification {

    private Long notificationId;
    private Long senderId;

    @SerializedName("sender")
    private String senderName;
    private String message;
    private String title;

    @SerializedName("createdAt")
    private Long notificationTime;

    @SerializedName("senderPicture")
    private String senderImage;
    private String type;
    private String notificationTypeStatus;
    private Long notificationTypeId;
    private String readStatus;
    private String action;
    private Event event;
    private User user;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

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

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", message='" + message + '\'' +
                ", title='" + title + '\'' +
                ", notificationTime=" + notificationTime +
                ", senderImage='" + senderImage + '\'' +
                ", type='" + type + '\'' +
                ", notificationTypeStatus='" + notificationTypeStatus + '\'' +
                ", notificationTypeId=" + notificationTypeId +
                ", readStatus='" + readStatus + '\'' +
                ", user=" + user +
                '}';
    }
}
