package com.cenesbeta.dto;

public class ProfileItemDto {

    public enum ProfileItemDtoEnum{Profile, Calendars, Settings, Help, About}

    private String title;
    private String description;
    private ProfileItemDtoEnum TAG;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProfileItemDtoEnum getTAG() {
        return TAG;
    }

    public void setTAG(ProfileItemDtoEnum TAG) {
        this.TAG = TAG;
    }
}
