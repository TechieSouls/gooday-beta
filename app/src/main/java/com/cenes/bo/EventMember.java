package com.cenes.bo;

/**
 * Created by mandeep on 30/8/17.
 */

public class EventMember {

    private String eventMemberId;
    private String name;
    private String picture;
    private String status;
    private boolean owner;

    public String getEventMemberId() {
        return eventMemberId;
    }

    public void setEventMemberId(String eventMemberId) {
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
}
