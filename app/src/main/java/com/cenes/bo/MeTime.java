package com.cenes.bo;

import java.util.List;

/**
 * Created by mandeep on 23/8/17.
 */

public class MeTime {

    Boolean isSelected;
    String category;
    List<MeTimeItem> items;
    String userId;
    String timezone;
    Boolean doNotDisturb;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MeTimeItem> getItems() {
        return items;
    }

    public void setItems(List<MeTimeItem> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(Boolean doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }
}
