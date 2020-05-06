package com.cenesbeta.bo;

public class RecurringEventMember {

    private Integer recurringEventMemberId;
    private Integer recurringEventId;
    private Integer userId;
    private UserContact userContact;
    private  User user;

    public Integer getRecurringEventMemberId() {
        return recurringEventMemberId;
    }

    public void setRecurringEventMemberId(Integer recurringEventMemberId) {
        this.recurringEventMemberId = recurringEventMemberId;
    }

    public Integer getRecurringEventId() {
        return recurringEventId;
    }

    public void setRecurringEventId(Integer recurringEventId) {
        this.recurringEventId = recurringEventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserContact getUserContact() {
        return userContact;
    }

    public void setUserContact(UserContact userContact) {
        this.userContact = userContact;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
