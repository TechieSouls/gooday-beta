package com.cenesbeta.dto;

public class CreateGatheringDto {

    private boolean startTime;
    private boolean endTime;
    private boolean date;
    private boolean picture;
    private boolean location;
    private boolean messsage;
    private boolean isPicture;

    public boolean isStartTime() {
        return startTime;
    }

    public void setStartTime(boolean startTime) {
        this.startTime = startTime;
    }

    public boolean isEndTime() {
        return endTime;
    }

    public void setEndTime(boolean endTime) {
        this.endTime = endTime;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isPicture() {
        return picture;
    }

    public void setPicture(boolean picture) {
        this.picture = picture;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isMesssage() {
        return messsage;
    }

    public void setMesssage(boolean messsage) {
        this.messsage = messsage;
    }
}
