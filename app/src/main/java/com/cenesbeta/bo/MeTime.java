package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mandeep on 23/8/17.
 */

public class MeTime {

    private Long recurringEventId;
    String title;

    @SerializedName("recurringPatterns")
    List<MeTimeItem> items;
    private Long userId;
    String timezone;
    private String photo;
    private Long startTime;
    private Long endTime;
    private String days;

    public Long getRecurringEventId() {
        return recurringEventId;
    }

    public void setRecurringEventId(Long recurringEventId) {
        this.recurringEventId = recurringEventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MeTimeItem> getItems() {
        return items;
    }

    public void setItems(List<MeTimeItem> items) {
        this.items = items;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "MeTime{" +
                "title='" + title + '\'' +
                ", items=" + items +
                ", userId='" + userId + '\'' +
                ", timezone='" + timezone + '\'' +
                ", photo='" + photo + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", days='" + days + '\'' +
                '}';
    }
}
