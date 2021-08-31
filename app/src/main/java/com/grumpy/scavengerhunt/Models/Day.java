package com.grumpy.scavengerhunt.Models;

public class Day {

    private String currentDay;

    public Day(String currentDay) {
        this.currentDay = currentDay;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(String currentDay) {
        this.currentDay = currentDay;
    }
}
