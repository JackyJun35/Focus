package com.example.jackyjun.focus.Model;


import java.util.Calendar;

/**
 * Created by jackyjun on 16/9/22.
 * this class stores the time that the user use everyday and it is related to the TIME_DATA table for the database
 */
public class TimeData {
    private String planName;
    private long second;
    private Calendar date;

    public TimeData(){}

    public TimeData(String planName) {
        this.planName = planName;
    }

    public TimeData(String planName, long second, Calendar date){
        this.planName = planName;
        this.second = second;
        this.date = date;
    }

    public String getPlanName() {
        return planName;
    }

    public long getSecond() {
        return second;
    }

    public long getMinute() {return second/60;}

    public Calendar getDate() {
        return date;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setMinute(long minute){this.second = minute*60;}
}
