package com.cenes.bo;

/**
 * Created by mandeep on 25/8/17.
 */

public class MeTimeItem {
    String day_Of_week;
    String title;
    String Description;
    String startTime;
    String endTime;

    public String getDay_Of_week() {
        return day_Of_week;
    }

    public void setDay_Of_week(String day_Of_week) {
        this.day_Of_week = day_Of_week;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
