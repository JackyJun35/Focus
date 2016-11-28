package com.example.jackyjun.focus.Model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jackyjun on 16/8/12.
 */
public class ToDoParent implements Comparable<ToDoParent>{

    protected String planName;
    protected String name;
    private int type;
    private int id;
    //the exact time in millis the last change of the ToDoParent object;
    private long lastChangedTime;



    public static final int TYPE_TODO = 1;
    public static final int TYPE_DONE = 0;

    // these strings will be displayed on the parent view of the recyclerview
    private static final String STRING_TODO = "Todo";
    private static final String STRING_DONE = "Done";

    public ToDoParent(){}

    public ToDoParent(String name){
        this.name = name;
    }


    public ToDoParent(String name,String planName){
        this.name = name;
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLastChangedTime() {
        return lastChangedTime;
    }

    public void setLastChangedTime(long lastChangedTime) {
        this.lastChangedTime = lastChangedTime;
    }

    @Override
    public int compareTo(ToDoParent another) {
        return (int) (this.getLastChangedTime()-another.getLastChangedTime());
    }

    @Override
    public String toString() {
        return "ToDo [id=" + id + ", name="+ name +", plan_name ="+planName+ "]";
    }





}
