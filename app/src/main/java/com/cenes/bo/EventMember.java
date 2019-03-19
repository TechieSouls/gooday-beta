package com.cenes.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mandeep on 30/8/17.
 */

public class EventMember {

    //@SerializedName("memberId")
    private Long eventMemberId;
    private String name;
    private String picture;
    private String status;
    private Long eventId;
    private Long userId;
    private int userContactId;
    private String phone;
    private boolean owner;
    private User user;
    private String cenesMember;

    public Long getEventMemberId() {
        return eventMemberId;
    }

    public void setEventMemberId(Long eventMemberId) {
        this.eventMemberId = eventMemberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserContactId() {
        return userContactId;
    }

    public void setUserContactId(int userContactId) {
        this.userContactId = userContactId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String isCenesMember() {
        return cenesMember;
    }

    public void setCenesMember(String cenesMember) {
        this.cenesMember = cenesMember;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "EventMember{" +
                "eventMemberId=" + eventMemberId +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", status='" + status + '\'' +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", userContactId=" + userContactId +
                ", phone='" + phone + '\'' +
                ", owner=" + owner +
                ", user=" + user +
                ", cenesMember='" + cenesMember + '\'' +
                '}';
    }
}
