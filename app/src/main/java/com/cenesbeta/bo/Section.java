package com.cenesbeta.bo;

/**
 * Created by rohan on 14/11/2017.
 */
public class Section {

    private final String name;

    public boolean isExpanded;

    public Section(String name) {
        this.name = name;
        isExpanded = true;
    }

    public String getName() {
        return name;
    }
}
