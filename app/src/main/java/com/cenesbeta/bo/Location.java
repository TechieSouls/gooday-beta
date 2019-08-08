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
}
