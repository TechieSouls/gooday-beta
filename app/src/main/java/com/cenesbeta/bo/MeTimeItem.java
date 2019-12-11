package com.cenesbeta.bo;

/**
 * Created by mandeep on 25/8/17.
 */

public class MeTimeItem {

    private Long recurringEventId;
    String day_Of_week;
    private Long dayOfWeekTimestamp;


    public Long getRecurringEventId() {
        return recurringEventId;
    }

    public void setRecurringEventId(Long recurringEventId) {
        this.recurringEventId = recurringEventId;
    }

    public String getDay_Of_week() {
        return day_Of_week;
    }

    public void setDay_Of_week(String day_Of_week) {
        this.day_Of_week = day_Of_week;
    }

    public Long getDayOfWeekTimestamp() {
        return dayOfWeekTimestamp;
    }

    public void setDayOfWeekTimestamp(Long dayOfWeekTimestamp) {
        this.dayOfWeekTimestamp = dayOfWeekTimestamp;
    }
}
