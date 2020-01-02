package com.cenesbeta.dto;

public class PredictiveData {

    private String readableDate;
    private Long date;
    private Integer totalFriends;
    private Integer attendingFriends;
    private Integer predictivePercentage;
    private String attendingFriendsList;

    public Long getDate() {
        return date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public Integer getTotalFriends() {
        return totalFriends;
    }
    public void setTotalFriends(Integer totalFriends) {
        this.totalFriends = totalFriends;
    }
    public Integer getAttendingFriends() {
        return attendingFriends;
    }
    public void setAttendingFriends(Integer attendingFriends) {
        this.attendingFriends = attendingFriends;
    }
    public Integer getPredictivePercentage() {
        return predictivePercentage;
    }
    public void setPredictivePercentage(Integer predictivePercentage) {
        this.predictivePercentage = predictivePercentage;
    }
    public String getReadableDate() {
        return readableDate;
    }
    public void setReadableDate(String readableDate) {
        this.readableDate = readableDate;
    }
    public String getAttendingFriendsList() {
        return attendingFriendsList;
    }
    public void setAttendingFriendsList(String attendingFriendsList) {
        this.attendingFriendsList = attendingFriendsList;
    }
}
