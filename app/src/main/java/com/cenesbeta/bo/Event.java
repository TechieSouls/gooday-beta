package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mandeep on 23/8/17.
 */

public class Event {

    public enum EventDisplayScreen {PAST, HOME, ACCEPTED, PENDING, DECLINED};

    private Long eventId;
    private String logo;
    private String title;
    private Long startTime;
    private Long endTime;
    private String eventDate;
    private String location;
    private String eventPicture;
    private String thumbnail;
    private String latitude;
    private String longitude;
    private String status;
    private String source = "Cenes";
    private String scheduleAs = "Gathering";
    private String type;
    private String key;
    private Long startTimeMillis;
    private Long endTimeInMillis;
    private Boolean isFullDay;
    private String sender;
    private String description;
    private Integer createdById;
    private Long createdAt;
    private String eventImageURI;
    private boolean isOwner;
    private String placeId;
    private EventMember owner;
    private EventMember userEventMemberData;
    private List<EventMember> eventMembers;
    private String predictiveData;
    private Boolean isPredictiveOn = false;
    private String timezone;
    private String recurringEventId;
    private String fullDayStartTime;
    private String displayAtScreen;
    private boolean isEditMode;
    private boolean isSynced = true;

    @SerializedName("expired")
    private Boolean expired = false;

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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
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

    public Boolean getFullDay() {
        return isFullDay;
    }

    public void setFullDay(Boolean fullDay) {
        isFullDay = fullDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEndTimeInMillis() {
        return endTimeInMillis;
    }

    public void setEndTimeInMillis(Long endTimeInMillis) {
        this.endTimeInMillis = endTimeInMillis;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getEventImageURI() {
        return eventImageURI;
    }

    public void setEventImageURI(String eventImageURI) {
        this.eventImageURI = eventImageURI;
    }

    public EventMember getOwner() {
        return owner;
    }

    public void setOwner(EventMember owner) {
        this.owner = owner;
    }

    public void setIsOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public EventMember getUserEventMemberData() {
        return userEventMemberData;
    }

    public void setUserEventMemberData(EventMember userEventMemberData) {
        this.userEventMemberData = userEventMemberData;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getRecurringEventId() {
        return recurringEventId;
    }

    public void setRecurringEventId(String recurringEventId) {
        this.recurringEventId = recurringEventId;
    }

    public String getPredictiveData() {
        return predictiveData;
    }

    public void setPredictiveData(String predictiveData) {
        this.predictiveData = predictiveData;
    }

    public Boolean getPredictiveOn() {
        return isPredictiveOn;
    }

    public void setPredictiveOn(Boolean predictiveOn) {
        isPredictiveOn = predictiveOn;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getFullDayStartTime() {
        return fullDayStartTime;
    }

    public void setFullDayStartTime(String fullDayStartTime) {
        this.fullDayStartTime = fullDayStartTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public String getDisplayAtScreen() {
        return displayAtScreen;
    }

    public void setDisplayAtScreen(String displayAtScreen) {
        this.displayAtScreen = displayAtScreen;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", logo='" + logo + '\'' +
                ", title='" + title + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", location='" + location + '\'' +
                ", eventPicture='" + eventPicture + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                ", scheduleAs='" + scheduleAs + '\'' +
                ", type='" + type + '\'' +
                ", startTimeMillis=" + startTimeMillis +
                ", endTimeInMillis=" + endTimeInMillis +
                ", isFullDay=" + isFullDay +
                ", sender='" + sender + '\'' +
                ", description='" + description + '\'' +
                ", createdById=" + createdById +
                ", createdAt='" + createdAt + '\'' +
                ", eventImageURI='" + eventImageURI + '\'' +
                ", isOwner=" + isOwner +
                ", placeId='" + placeId + '\'' +
                ", owner=" + owner +
                ", userEventMemberData=" + userEventMemberData +
                ", eventMembers=" + eventMembers +
                ", predictiveData='" + predictiveData + '\'' +
                ", isPredictiveOn=" + isPredictiveOn +
                ", timezone='" + timezone + '\'' +
                ", fullDayStartTime='" + fullDayStartTime + '\'' +
                ", Private Key='" + key + '\'' +
                '}';
    }
}
