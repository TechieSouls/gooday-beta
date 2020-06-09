package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

public class LocationPhoto {

    private Integer width;
    private Integer height;

    @SerializedName("photo_reference")
    private String photoReferences;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getPhotoReferences() {
        return photoReferences;
    }

    public void setPhotoReferences(String photoReferences) {
        this.photoReferences = photoReferences;
    }
}
