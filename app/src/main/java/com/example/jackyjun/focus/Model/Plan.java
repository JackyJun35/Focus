package com.example.jackyjun.focus.Model;

import com.example.jackyjun.focus.util.TimeAndSecondsConverter;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by jackyjun on 16/7/20.
 */
public class Plan implements Serializable{
    private String name;
    private int id;
    private String description;
    private Calendar deadline;
    private Calendar startDate;
    private int duration; //hour
    private long totalSecondSpent;
    private static final long serialVersionUID = -7060210544600464481L;
    private List<ToDo> toDoList;
    private HashMap<Date,Integer> minuteSpentEveryDay;

    public Plan(){}

    public Plan(String name){
        this.name = name;
    }

    public Plan(String name, int duration, Calendar deadline) {
        this.name = name;
        this.duration = duration;
        this.deadline = deadline;
        this.description = "";
    }

    public Plan(String name, String description, Calendar deadline, int duration) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.duration = duration;
    }

    //getter and setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        if (description==null){
            return "";
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getTotalSecondSpent() {
        return totalSecondSpent;
    }

    public void setTotalSecondSpent(long totalSecondSpent) {
        this.totalSecondSpent = totalSecondSpent;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public List<ToDo> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    public String getProgressInString(){
        if(duration!=0){
            double progress = (double) totalSecondSpent/(duration*3600);
            int ans =((int)(progress*100));
            return ans +"%";
        }
        return "0%";
    }
    //return progress 1 to 100
    public int getProgressInInt(){
        if(duration!=0){
            double progress = (double)totalSecondSpent/(duration*3600);
            int ans =(int)(progress*100);
            return ans;
        }
        return 0;
    }
    //return from 0 to 100
    public float getProgress(){
        if(duration!=0){
            float progress = (float)totalSecondSpent/(duration*36);
            return progress;
        }
        return 0;
    }

    public void setMinuteSpentEveryDay(Date date,int minute){
        minuteSpentEveryDay.put(date,minute);
    }

    public int getMinuteSpendByDate(Date date){
       return minuteSpentEveryDay.get(date);
    }

    //This method convert the seconds into format HH:MM:SS and return a string
    public String getCurrentTimeInString(){
       return TimeAndSecondsConverter.fromMilisecondToString(totalSecondSpent*1000);
    }
    //toString
    @Override
    public String toString() {
        return "Plan [id=" + id + ", name="+ name + ", deadline=" + deadline + ", duration=" + duration
                +", description="+description+",totalsecond="+totalSecondSpent+",start date="+startDate+"]";
    }


}
