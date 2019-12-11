package com.cenesbeta.bo;

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

    //@SerializedName("memberId")
    private Integer userId;
    private Integer userContactId;
    private String phone;
    private boolean owner;
    private User user;

    private UserContact userContact;

    private Integer friendId;
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

    public Integer getUserId() {
        if (friendId != null) {
            userId = friendId;
            return friendId;
        }
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserContactId() {
        return userContactId;
    }

    public void setUserContactId(Integer userContactId) {
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

    public Integer getFriendId() {
        return friendId;
    }

    public UserContact getUserContact() {
        return userContact;
    }

    public void setUserContact(UserContact userContact) {
        this.userContact = userContact;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
        if (friendId != null) {
            this.userId = friendId;
        }
    }

    public String getCenesMember() {
        return cenesMember;
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
                ", friendId=" + friendId +
                ", cenesMember='" + cenesMember + '\'' +
                '}';
    }
}
