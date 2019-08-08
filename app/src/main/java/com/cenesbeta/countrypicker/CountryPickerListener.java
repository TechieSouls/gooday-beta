package com.cenesbeta.countrypicker;

/**
 * Created by mandeep on 16/9/17.
 */

public interface CountryPickerListener {
    public void onSelectCountry(String name, String code, String calendarId);
}
