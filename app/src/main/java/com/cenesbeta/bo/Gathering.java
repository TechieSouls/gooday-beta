package com.cenesbeta.bo;

import com.cenesbeta.materialcalendarview.CalendarDay;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by mandeep on 13/9/17.
 */

public class Gathering implements Serializable{
    private Map<String,Set<CalendarDay>> predictiveData;

    public Map<String, Set<CalendarDay>> getPredictiveData() {
        return predictiveData;
    }

    public void setPredictiveData(Map<String, Set<CalendarDay>> predictiveData) {
        this.predictiveData = predictiveData;
    }
}
