package com.example.jackyjun.focus.Model;

import java.util.Calendar;

/**
 * Created by jackyjun on 16/8/12.
 */
public class ToDo extends ToDoParent{

    private int type = TYPE_TODO;

    private int estimatedTime;
    private Calendar deadline;
    private boolean isDone = false;


    public ToDo(){super();}

    public ToDo(String name, String planName) {
        super(name,planName);
    }

    public ToDo(String name, int estimatedTime, Calendar deadline) {
        super(name);
        this.estimatedTime = estimatedTime;
        this.deadline = deadline;
    }


    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return isDone;
    }

    public Done toDone(){
        return new Done(this.name,this.estimatedTime,this.deadline);
    }


}
