package com.cenesbeta.bo;

public class HolidayCalendar {

    private Long holidayCalendarId;

    private String countryName;

    private String countryCode;

    private String countryCalendarId;

    private Integer userId;

    public Long getHolidayCalendarId() {
        return holidayCalendarId;
    }

    public void setHolidayCalendarId(Long holidayCalendarId) {
        this.holidayCalendarId = holidayCalendarId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCalendarId() {
        return countryCalendarId;
    }

    public void setCountryCalendarId(String countryCalendarId) {
        this.countryCalendarId = countryCalendarId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HolidayCalendar{" +
                "holidayCalendarId=" + holidayCalendarId +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryCalendarId='" + countryCalendarId + '\'' +
                ", userId=" + userId +
                '}';
    }
}
