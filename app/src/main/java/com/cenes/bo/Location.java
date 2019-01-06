package com.deploy.bo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mandeep on 4/1/19.
 */

public class Location {

    @SerializedName("location")
    private String location;

    @SerializedName("photo")
    private String photo;

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
}
