package com.lotustechnologicalsolution.chiri.ModelClasses;

import java.io.Serializable;

public class DateAndTimeModel implements Serializable {

    private String date,time;

    public DateAndTimeModel() {
    }


    public String date() {
        return date;
    }

    public void setDate(String socialId) {
        this.date = date;
    }

    public String time() {
        return time;
    }

    public void setTime(String socialId) {
        this.time = time;
    }
}
