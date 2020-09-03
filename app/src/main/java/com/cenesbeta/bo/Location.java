package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mandeep on 4/1/19.
 */

public class Location {

    @SerializedName("location")
    private String location;

    @SerializedName("photo")
    private String photo;

    private String latitude;

    private String longitude;

    private String placeId;

    private String address;

    private String kilometers;

    private Float kilometersInDouble;

    private String country;

    private String state;

    private String county;

    private String city;

    private Integer lastForteenDays;

    private Boolean isOpenNow;

    private String phoneNumber;

    private String newCases;

    private String markerSnippet;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public Float getKilometersInDouble() {
        return kilometersInDouble;
    }

    public void setKilometersInDouble(Float kilometersInDouble) {
        this.kilometersInDouble = kilometersInDouble;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Boolean isOpenNow() {
        return isOpenNow;
    }

    public void setOpenNow(Boolean openNow) {
        isOpenNow = openNow;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    public String getMarkerSnippet() {
        return markerSnippet;
    }

    public void setMarkerSnippet(String markerSnippet) {
        this.markerSnippet = markerSnippet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLastForteenDays() {
        return lastForteenDays;
    }

    public void setLastForteenDays(Integer lastForteenDays) {
        this.lastForteenDays = lastForteenDays;
    }
}
