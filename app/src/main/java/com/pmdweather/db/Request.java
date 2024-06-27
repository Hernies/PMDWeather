package com.pmdweather.db;

import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {
    private String cityName;
    private Date date;
    private boolean days;
    private boolean weeks;
    private int numWeeks;

    // Constructor
    public Request(String cityName, Date date, int numWeeks) {
        this.cityName = cityName;
        this.date = date;
        this.numWeeks = numWeeks;
    }

    // Default constructor
    public Request() {
    }

    // Getters and Setters
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDays(boolean days) {
        this.days = days;
    }


    public void setWeeks(boolean weeks) {
        this.weeks = weeks;
    }

    public int getNumWeeks() {
        return numWeeks;
    }

    public void setNumWeeks(int numWeeks) {
        this.numWeeks = numWeeks;
    }

    @Override
    public String toString() {
        return "Request{" +
                "cityName='" + cityName + '\'' +
                ", date=" + date +
                ", numWeeks=" + numWeeks +
                '}';
    }
}
