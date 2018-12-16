package com.cenes.bo;

import java.util.List;

/**
 * Created by mandeep on 23/8/17.
 */

public class Event {
    private Long eventId;
    private Long eventMemberId;
    private String logo;
    private String title;
    private String startTime;
    private String eventDate;
    private String location;
    private String eventPicture;
    private String status;
    private String source;
    private String scheduleAs;
    private String type;
    private Long startTimeMillis;
    private Boolean isFullDay;
    private String sender;
    private List<EventMember> eventMembers;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<EventMember> getEventMembers() {
        return eventMembers;
    }

    public void setEventMembers(List<EventMember> eventMembers) {
        this.eventMembers = eventMembers;
    }

    public String getEventPicture() {
        return eventPicture;
    }

    public void setEventPicture(String eventPicture) {
        this.eventPicture = eventPicture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEventMemberId() {
        return eventMemberId;
    }

    public void setEventMemberId(Long eventMemberId) {
        this.eventMemberId = eventMemberId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getScheduleAs() {
        return scheduleAs;
    }

    public void setScheduleAs(String scheduleAs) {
        this.scheduleAs = scheduleAs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public Boolean getIsFullDay() {
        return isFullDay;
    }

    public void setIsFullDay(Boolean isFullDay) {
        this.isFullDay = isFullDay;
    }
}
